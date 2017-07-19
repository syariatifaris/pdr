package com.seoultechappsoftlab.wireloc.activity.canvases;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.Projection;
import org.mapsforge.core.model.GeoPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.RoomObject;
import com.seoultechappsoftlab.wireloc.helpers.CollisionDetectionHelper;
import com.seoultechappsoftlab.wireloc.helpers.GeoPositionHelper;
import com.seoultechappsoftlab.wireloc.helpers.ParticleFilterStatisticsHelper;
import com.seoultechappsoftlab.wireloc.infrastructures.OverlayCanvasBase;
import com.seoultechappsoftlab.wireloc.utilities.MapsUtils;

public class ParticleFilterOverlayCanvas extends OverlayCanvasBase {

	// Region private variables

	public int CANVAS_WIDTH = 1080;
	public int CANVAS_HEIGHT = 1577;

	private Bitmap personBitmap;
	private List<Particle> particleFilterList;
	private List<RoomObject> roomObjects;
	private CollisionDetectionHelper collisionManager;
	private List<Particle> trajectoryParticle;
	private double[] totalWeightAndNormalDistribution;

	private ParticleFilterStatisticsHelper statisticsHelper;
	private OnParticleDraw onParticleDraw;

	private boolean isParticleHidden;
	private boolean isTrajectoryHidden;
	private boolean isAfterReset;

	private Particle averagePositionParticle;
	private Context context;

	// End Region private variables

	// Region Constructors

	/**
	 * Constructor
	 */
	public ParticleFilterOverlayCanvas(OnParticleDraw onParticleDraw, Context context) {
		super();
		this.onParticleDraw = onParticleDraw;
		this.isParticleHidden = false;
		this.isTrajectoryHidden = false;
		this.context = context;
	}

	/**
	 * Constructor
	 * 
	 * @param bitmap
	 *            Bitmap
	 * @param geoPoint
	 *            GeoPoint
	 */
	public ParticleFilterOverlayCanvas(Bitmap bitmap, GeoPoint geoPoint) {
		super(bitmap, geoPoint);
	}

	/**
	 * Constructor
	 * 
	 * @param bitmap
	 *            Bitmap
	 * @param position
	 *            Position
	 * @param geoPoint
	 *            GeoPoint
	 */
	public ParticleFilterOverlayCanvas(Bitmap bitmap, PointF position, GeoPoint geoPoint) {
		super(bitmap, position, geoPoint);
	}

	// End Region Constructors

	/**
	 * Draw Overlay
	 * 
	 * @param canvasCanvas
	 * @param pointPoint
	 * @param projectionProjection
	 * @param zoomLevelbyte
	 */
	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point point, Projection projection, byte zoomLevel) {
		super.drawOverlayBitmap(canvas, point, projection, zoomLevel);

		if (true && totalWeightAndNormalDistribution != null) {
			Paint textPaint = new Paint();
			textPaint.setTextSize(30);
			textPaint.setColor(Color.BLUE);
			canvas.drawText(this.context.getString(R.string.total_weight_show) + totalWeightAndNormalDistribution[0], 10, CANVAS_HEIGHT - 40, textPaint);
			canvas.drawText(this.context.getString(R.string.maximum_normal_distribution_show) + totalWeightAndNormalDistribution[3], 10, CANVAS_HEIGHT - 80, textPaint);
		}

		if (this.particleFilterList != null && !this.particleFilterList.isEmpty()) {
			for (Particle particle : this.particleFilterList) {

				// particle = this.getNewPositionIfOutsideStage(particle);

				particle.setDisplayPoint(particle.getDisplayPoint() == null ? new Point() : particle.getDisplayPoint());
				projection.toPixels(particle.getGeoPoint(), particle.getDisplayPoint());

				if (!this.isParticleHidden) {
					Paint paint = new Paint();
					int color = particle.getRgbColor() != 0 ? Color.rgb(255, 255 - particle.getRgbColor(), 255 - particle.getRgbColor()) : Color.WHITE;
					paint.setColor(color);
					canvas.drawCircle(particle.getDisplayPoint().x, particle.getDisplayPoint().y, (float) particle.getParticle_view_weight(), paint);
				}
			}

			if (this.personBitmap != null) {
				Particle averageParticle = this.statisticsHelper.getAverageLocationParticle();
				GeoPoint averageParticleLatLon = GeoPositionHelper.convertXYMilesToLatLon(GeoPositionHelper.magnifiedPosition(GeoPositionHelper.convertMeterToMiles(new PointF((float) averageParticle.getParticle_x(), (float) averageParticle.getParticle_y())), 100), MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
				averageParticle.setGeoPoint(averageParticleLatLon);
				averageParticle.setDisplayPoint(averageParticle.getDisplayPoint() == null ? new Point() : averageParticle.getDisplayPoint());
				projection.toPixels(averageParticle.getGeoPoint(), averageParticle.getDisplayPoint());
				if (trajectoryParticle == null) {
					trajectoryParticle = new ArrayList<Particle>();
				}
				trajectoryParticle.add(averageParticle);

				CollisionDetectionHelper collisionHelper = new CollisionDetectionHelper(this.getPhoneScreen(), averageParticle.getDisplayPoint());
				if (!collisionHelper.isInsideRectangle()) {
					this.onParticleDraw.setMapCenter(averageParticle.getGeoPoint());
				}

				// set average position global variable
				this.averagePositionParticle = averageParticle;
				this.onParticleDraw.redrawArrow();
			}

			if (isTrajectoryHidden && trajectoryParticle != null && trajectoryParticle.size() > 1) {
				for (int i = 0; i < trajectoryParticle.size() - 1; i++) {
					projection.toPixels(trajectoryParticle.get(i).getGeoPoint(), trajectoryParticle.get(i).getDisplayPoint());
					projection.toPixels(trajectoryParticle.get(i + 1).getGeoPoint(), trajectoryParticle.get(i + 1).getDisplayPoint());
					Paint linePaint = new Paint();
					linePaint.setColor(Color.BLUE);
					linePaint.setAntiAlias(true);
					linePaint.setStrokeWidth(10);
					canvas.drawLine(trajectoryParticle.get(i).getDisplayPoint().x, trajectoryParticle.get(i).getDisplayPoint().y, trajectoryParticle.get(i + 1).getDisplayPoint().x, trajectoryParticle.get(i + 1).getDisplayPoint().y, linePaint);
				}
			}

			// mock
			Point centerPoint = new Point();
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			projection.toPixels(MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_CENTER, centerPoint);
			canvas.drawCircle(centerPoint.x, centerPoint.y, 10, paint);
		}
	}

	// Region Getters and Setters

	public List<Particle> getParticleFilterList() {
		return particleFilterList;
	}

	public void setParticleFilterList(List<Particle> particleFilterList) {
		this.particleFilterList = particleFilterList;
		this.statisticsHelper = new ParticleFilterStatisticsHelper(this.particleFilterList);
		// this.onParticleDraw.setMapCenter(this.getAverageparticlePosition());
		this.invalidate();
	}

	public Bitmap getPersonBitmap() {
		return personBitmap;
	}

	public void setPersonBitmap(Bitmap personBitmap) {
		this.personBitmap = personBitmap;
		this.invalidate();
	}

	public boolean isParticleHidden() {
		return this.isParticleHidden;
	}

	public void setParticleHidden(boolean isParticleHidden) {
		this.isParticleHidden = isParticleHidden;
		this.invalidate();
	}

	public boolean isTrajectoryHidden() {
		return this.isTrajectoryHidden;
	}

	public void setTrajectoryHidden(boolean isTrajectoryHidden) {
		this.isTrajectoryHidden = isTrajectoryHidden;
		this.invalidate();
	}

	public Particle getAveragePositionParticle() {
		return this.averagePositionParticle;
	}

	public void setAveragePositionParticle(Particle averagePositionParticle) {
		this.averagePositionParticle = averagePositionParticle;
	}

	public boolean isAfterReset() {
		return isAfterReset;
	}

	public void setAfterReset(boolean isAfterReset) {
		this.isAfterReset = isAfterReset;
	}

	/**
	 * Get Room Object
	 * 
	 * @return
	 */
	public List<RoomObject> getroomObjects() {
		return this.roomObjects;
	}

	/**
	 * Set Room Object
	 * 
	 * @return
	 */
	public void setRoomObjects(List<RoomObject> roomObjects) {
		this.roomObjects = roomObjects;
	}

	public double[] getTotalWeightAndNormalDistribution() {
		return totalWeightAndNormalDistribution;
	}

	public void setTotalWeightAndNormalDistribution(double[] totalWeightAndNormalDistribution) {
		this.totalWeightAndNormalDistribution = totalWeightAndNormalDistribution;
	}

	/**
	 * Reset Trajectory
	 */
	public void resetTrajectoryParticle() {
		if (this.trajectoryParticle != null) {
			this.trajectoryParticle.clear();
			this.invalidate();
		}
	}

	// End Region Getters and Setters
	@SuppressWarnings("unused")
	private GeoPoint getAverageparticlePosition() {
		Particle averageParticle = this.statisticsHelper.getAverageLocationParticle();
		GeoPoint averageParticleLatLon = GeoPositionHelper.convertXYMilesToLatLon(
				GeoPositionHelper.magnifiedPosition(GeoPositionHelper.convertMeterToMiles(new PointF((float) averageParticle.getParticle_x(), (float) averageParticle.getParticle_y())), 100), MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
		return averageParticleLatLon;
	}

	/**
	 * Get New Position if Particle has been elapsed / outside stage
	 * 
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unused")
	private Particle getNewPositionIfOutsideStage(Particle vo) {
		Point currPoint = new Point((int) vo.getParticle_x(), (int) vo.getParticle_y());

		this.collisionManager = new CollisionDetectionHelper(this.getStage(), currPoint);

		if (!this.collisionManager.isInsideRectangle()) {
			Point newPoint = this.collisionManager.getNearestPointOutPerimiter(this.getStage(), currPoint);
			vo.setParticle_x(newPoint.x);
			vo.setParticle_y(newPoint.y);
			PointF fPoint = new PointF((float) newPoint.x, (float) newPoint.y);
			fPoint = GeoPositionHelper.convertMeterToMiles(fPoint);
			GeoPoint newGeoPoint = GeoPositionHelper.convertXYMilesToLatLon(GeoPositionHelper.magnifiedPosition(GeoPositionHelper.convertMeterToMiles(new PointF((float) vo.getParticle_x(), (float) vo.getParticle_y())), 100),
					MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
			vo.setGeoPoint(newGeoPoint);
		}

		return vo;
	}

	/**
	 * Get New Position if Particle has been elapsed / inside stage
	 * 
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unused")
	private Particle getNewPositionIfInsideStage(Particle vo) {
		Point currPoint = new Point((int) vo.getParticle_x(), (int) vo.getParticle_y());

		this.collisionManager = new CollisionDetectionHelper(this.getStage(), currPoint);

		if (!this.collisionManager.isInsideRectangle()) {
			Point newPoint = this.collisionManager.getNearestPointInPerimiter(this.getStage(), currPoint);
			vo.setParticle_x(newPoint.x);
			vo.setParticle_y(newPoint.y);
		}

		return vo;
	}

	/**
	 * Get Stage
	 * 
	 * @return
	 */
	private Rect getStage() {
		Point minPoint = GeoPositionHelper.convertLatLonPositionToXYInMeter(MapsUtils.MIN_GEOPOINT, MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
		minPoint.x = (int) (minPoint.x * 0.01);
		minPoint.y = (int) (minPoint.y * 0.01);
		Point maxPoint = GeoPositionHelper.convertLatLonPositionToXYInMeter(MapsUtils.MAX_GEOPOINT, MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
		maxPoint.x = (int) (maxPoint.x * 0.01);
		maxPoint.y = (int) (maxPoint.y * 0.01);
		Rect rect = new Rect(minPoint.x, minPoint.y, maxPoint.x, maxPoint.y);
		return rect;
	}

	/**
	 * Get Phone Screen
	 * 
	 * @return
	 */
	private Rect getPhoneScreen() {
		return new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
	}

	/**
	 * Interface On ParticleDraw
	 * 
	 * @author SeoulTech Application Software Lab
	 *
	 */
	public interface OnParticleDraw {
		void setMapCenter(GeoPoint averagePoint);

		void redrawArrow();
	}
}
