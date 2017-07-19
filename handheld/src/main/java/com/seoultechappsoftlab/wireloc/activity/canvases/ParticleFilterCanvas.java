package com.seoultechappsoftlab.wireloc.activity.canvases;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.RoomObject;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.helpers.CollisionDetectionHelper;
import com.seoultechappsoftlab.wireloc.helpers.ParticleFilterStatisticsHelper;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;
import com.seoultechappsoftlab.wireloc.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Particle Filter's Canvas
 * @author Lab-01
 *
 */
public class ParticleFilterCanvas extends View {

	boolean checkPreloadBeacon = false;
	private boolean pauseState = false;
	
	public int CANVAS_WIDTH = 1080;
	public int CANVAS_HEIGHT = 1577;
	
	private static final int STAGE_WIDTH = 1070;
	private static final int STAGE_HEIGHT = 1523;

	private List<Particle> particleFilterList;
	
	private List<Particle> particleGridVO;

	private List<Beacon> preloadBeaconList;

	private Bitmap arrowBitmap;
	private Bitmap personBitmap;

	private ParticleFilterStatisticsHelper particleFilterStatistics;
	private Paint canvasPaint;

	private Stage stageDetail;
	private RoomObject randomObstacle;
	private List<RoomObject> roomObjects;

	private boolean detectCollision;
	private boolean isRadarMode;

	private CollisionDetectionHelper collisionManager;

	/**
	 * Constructor
	 * @param context
	 */
	public ParticleFilterCanvas(Context context) {
		super(context);
	}

	/**
	 * Constructor
	 * @param context
	 * @param attrs
	 */
	public ParticleFilterCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * On Draw Method
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.initializeStage(canvas);

		if (this.roomObjects != null && !this.isRadarMode) {
			this.drawroomObjects(canvas);
		}

		if (preloadBeaconList != null) {
			this.canvasPaint = this.initPaint();
			this.canvasPaint.setColor(Color.BLUE);
			for (Beacon vo : preloadBeaconList) {
				this.drawBeacon(canvas, vo);
			}
		}

		if (particleFilterList != null) {
			for (Particle vo : particleFilterList) {
				// check if outside stage
				if (!this.isRadarMode) {
					vo = this.getNewPositionIfOutsideStage(vo);
				}

				this.canvasPaint = this.initPaint();

				int color = vo.getRgbColor() != 0 ? Color.rgb(255, 255 - vo.getRgbColor(), 255 - vo.getRgbColor()) : Color.WHITE;

				this.canvasPaint.setColor(color);
				canvas.drawCircle((int) vo.getParticle_x(), (int) vo.getParticle_y(), (float) vo.getParticle_view_weight(), this.canvasPaint);
			}
		}

		if (this.particleFilterList != null) {
			// add particle average point
			this.canvasPaint = this.initPaint();
			this.canvasPaint.setColor(Color.BLUE);
			this.particleFilterStatistics = this.initParticleStatistics(particleFilterList);
			Particle averagePoint = particleFilterStatistics.getAverageLocationParticle();

			if (!this.isRadarMode) {
				averagePoint = this.getNewPositionIfCollied(averagePoint);
			}

			if (arrowBitmap != null) {
				canvas.drawBitmap(this.arrowBitmap, (int) averagePoint.getParticle_x() - this.arrowBitmap.getWidth(),
						(int) averagePoint.getParticle_y() - this.arrowBitmap.getHeight(), this.initPaint());
			}
			
			//canvas.drawBitmap(this.personBitmap, (int) averagePoint.getParticle_x() - this.personBitmap.getWidth(),
			//		(int) averagePoint.getParticle_y() - this.personBitmap.getHeight(), this.initPaint());
		}

		//if (arrowBitmap != null) {
		//	canvas.drawBitmap(arrowBitmap, 1000, 750, this.initPaint());
		//}
		
		if (this.stageDetail != null) {
			this.drawStageDetail(canvas);
		}

		if (pauseState) {
			this.drawPause(canvas);
		}
	}

	/**
	 * Set Particle and Redraw the Canvas
	 * @param particleFilterList
	 */
	public void setParticleList(ArrayList<Particle> particleFilterList) {
		this.particleFilterList = particleFilterList;
		invalidate();
	}

	/**
	 * Set Beacon and Redraw the Canvas
	 * @param preloadBeaconList
	 */
	public void setPreloadBeacons(List<Beacon> preloadBeaconList) {
		this.preloadBeaconList = preloadBeaconList;
		this.checkPreloadBeacon = true;
		invalidate();
	}

	/**
	 * Set Arrow Bitmap nd Redraw the Canvas
	 * @param arrowBitmap
	 */
	public void setArrowBitmap(Bitmap arrowBitmap) {
		this.arrowBitmap = arrowBitmap;
		invalidate();
	}

	/**
	 * Collection Detection
	 * Return true if Collision is Detected otherwise False
	 * @return
	 */
	public boolean isDetectCollision() {
		return this.detectCollision;
	}

	/**
	 * Set Detection Collision
	 * @param detectCollision
	 */
	public void setDetectCollision(boolean detectCollision) {
		this.detectCollision = detectCollision;
	}

	/**
	 * Get Poit List
	 * Return Particle Filter
	 * @return
	 */
	public List<Particle> getPointList() {
		return particleFilterList;
	}

	/**
	 * set Particle and Redraw the Canvas 
	 * @param pList
	 */
	public void setParticleFilterList(List<Particle> pList) {
		this.particleFilterList = pList;
		this.invalidate();
	}

	/**
	 * Get Stage Detail
	 * @return
	 */
	public Stage getStageDetail() {
		return stageDetail;
	}

	/**
	 * Set Stage Detail
	 * @param stageDetail
	 */
	public void setStageDetail(Stage stageDetail) {
		this.stageDetail = stageDetail;
		this.invalidate();
	}
	
	/**
	 * Get Random Obstacle
	 * @return
	 */
	public RoomObject getRandomObstacle() {
		return this.randomObstacle;
	}

	/**
	 * Set Random Obstacle
	 * @param randomObstacle
	 */
	public void setRandomObstacle(RoomObject randomObstacle) {
		this.randomObstacle = randomObstacle;
		this.invalidate();
	}

	/**
	 * Get Room Object
	 * @return
	 */
	public List<RoomObject> getroomObjects() {
		return this.roomObjects;
	}

	/**
	 * Set Room Object
	 * @param roomObjects
	 */
	public void setRoomObjects(List<RoomObject> roomObjects) {
		this.roomObjects = roomObjects;
		this.invalidate();
	}
	
	/**
	 * Return Pause State
	 * @return
	 */
	public boolean isPauseState() {
		return pauseState;
	}

	/**
	 * Set Pause Stage and Redraw
	 * @param pauseState
	 */
	public void setPauseState(boolean pauseState) {
		this.pauseState = pauseState;
		this.invalidate();
	}

	/**
	 * Get Canvas Width
	 * @return
	 */
	public int getCanvasWidth()
	{
		return this.getWidth();
	}

	/**
	 * Get Canvas Height
	 * @return
	 */
	public int getCanvasHeight() {
		return this.getHeight();
	}

	/**
	 * Get Particle Grid
	 * @return
	 */
	public List<Particle> getParticleGridVO() {
		return particleGridVO;
	}

	/**
	 * Set Particle Grid
	 * @param particleGridVO
	 */
	public void setParticleGridVO(List<Particle> particleGridVO) {
		this.particleGridVO = particleGridVO;
	}

	/**
	 * Get Person Bitmap
	 * @return
	 */
	public Bitmap getPersonBitmap() {
		return personBitmap;
	}

	/**
	 * Set Person Bitmap
	 * @param personBitmap
	 */
	public void setPersonBitmap(Bitmap personBitmap) {
		this.personBitmap = personBitmap;
		this.invalidate();
	}

	/**
	 * Is Radar Mode
	 * Return Radar Mode Status
	 * @return
	 */
	public boolean isRadarMode() {
		return isRadarMode;
	}

	/**
	 * Set Radar Mode and Redraw Canvas
	 * @param isRadarMode
	 */
	public void setRadarMode(boolean isRadarMode) {
		this.isRadarMode = isRadarMode;
		this.invalidate();
	}

	// region private variable

	/**
	 * Initialize new Paint
	 * 
	 * @return
	 */
	private Paint initPaint() {
		return new Paint();
	}

	/**
	 * Initialize particle filter statistics
	 * 
	 * @param collections
	 * @return
	 */
	private ParticleFilterStatisticsHelper initParticleStatistics(List<Particle> collections) {
		return new ParticleFilterStatisticsHelper(collections);
	}

	/**
	 * Get Stage
	 * 
	 * @return
	 */
	private Rect getStage() {
		return new Rect(10, 10, STAGE_WIDTH, STAGE_HEIGHT);
	}

	/**
	 * Initialize Stage
	 * 
	 * @param canvas
	 */
	private void initializeStage(Canvas canvas) {
		this.canvasPaint = this.initPaint();
		this.canvasPaint.setColor(Color.BLACK);
		// all boundary less 10 from the rssi
		canvas.drawRect(this.getStage(), this.canvasPaint);
	}

	/**
	 * Draw roomObjects
	 * 
	 * @param canvas
	 */
	private void drawroomObjects(Canvas canvas) {
		for (RoomObject roomObject : this.getroomObjects()) {
			this.drawObstacle(canvas, roomObject);
		}
	}

	/**
	 * Draw each obstacle
	 * 
	 * @param canvas
	 * @param obstacle
	 */
	private void drawObstacle(Canvas canvas, RoomObject obstacle) {
		Paint paint = this.initPaint();
		paint.setColor(Color.WHITE);
		Rect rectangle = obstacle.getObjectRectangle();
		canvas.drawRect(rectangle, paint);
		canvas.drawBitmap(obstacle.getObject(), obstacle.getObjectPosition().x, obstacle.getObjectPosition().y, this.initPaint());
	}

	/**
	 * Get New Position if Particle has been elapsed / outside stage
	 * 
	 * @param vo
	 * @return
	 */
	private Particle getNewPositionIfOutsideStage(Particle vo) {
		Point currPoint = new Point((int) vo.getParticle_x(), (int) vo.getParticle_y());
		this.collisionManager = new CollisionDetectionHelper(this.getStage(), currPoint);

		if (!this.collisionManager.isInsideRectangle()) {
			Point newPoint = this.collisionManager.getNearestPointOutPerimiter(this.getStage(), currPoint);
			vo.setParticle_x(newPoint.x);
			vo.setParticle_y(newPoint.y);
		}

		return vo;
	}

	/**
	 * Get New Position If a Particle is Collied on the Obstacle
	 * 
	 * @param currentParticleFilterVO
	 * @return
	 */
	private Particle getNewPositionIfCollied(Particle currentParticleFilterVO) {
		Point currentParticlePoint = new Point((int) currentParticleFilterVO.getParticle_x(), (int) currentParticleFilterVO.getParticle_y());

		for (RoomObject obstacle : this.roomObjects) {
			Rect currentChecked = obstacle.getObjectRectangle();
			this.collisionManager = new CollisionDetectionHelper(currentChecked, new Point((int) currentParticleFilterVO.getParticle_x(),
					(int) currentParticleFilterVO.getParticle_y()));
			if (this.collisionManager.isInsideRectangle()) {
				Point newPoint = this.collisionManager.getNearestPointInPerimiter(currentChecked, currentParticlePoint);
				currentParticleFilterVO.setParticle_x(newPoint.x);
				currentParticleFilterVO.setParticle_y(newPoint.y);
			}

			// this part will check if the position is oppressed between stage
			// and corner roomObjects
			if (currentChecked.left - currentParticleFilterVO.getParticle_x() <= 20
					&& currentParticleFilterVO.getParticle_x() - this.getStage().left <= 20) {
				currentParticleFilterVO.setParticle_x(currentChecked.right);
			}

			if (currentChecked.top - currentParticleFilterVO.getParticle_y() <= 20 && currentParticleFilterVO.getParticle_y() <= 20) {
				currentParticleFilterVO.setParticle_y(currentChecked.bottom);
			}

		}
		return currentParticleFilterVO;
	}

	/**
	 * Get Beacon Bitmap
	 * 
	 * @return
	 */
	private Bitmap getBeaconBitmap() {
		Bitmap beacon = BitmapFactory.decodeResource(getResources(), R.drawable.beacon);
		Bitmap beaconBitmap = Bitmap.createBitmap(beacon, 0, 0, beacon.getWidth(), beacon.getHeight());
		return Bitmap.createScaledBitmap(beaconBitmap, 100, 100, true);
	}

	/**
	 * Draw Beacon
	 * 
	 * @param canvas
	 * @param vo
	 */
	private void drawBeacon(Canvas canvas, Beacon vo) {
		Bitmap beaconBitmap = this.getBeaconBitmap();
		int xPosition =  vo.getPointX() * PointUtils.SCALE_X;
		int yPosition = vo.getPointY() * PointUtils.SCALE_Y;

		if (vo.getMacAddress().equals("20:CD:39:9F:0A:B2")) {
			xPosition -= beaconBitmap.getWidth() / 2 + 10;
		}

		if (vo.getMacAddress().equals("20:CD:39:9F:12:03")) {
			xPosition -= beaconBitmap.getWidth() / 2 + 10;
			yPosition -= beaconBitmap.getHeight() / 2 + 10;
		}

		if (vo.getMacAddress().equals("20:CD:39:9F:10:0A")) {
			yPosition -= beaconBitmap.getHeight() / 2 + 10;
		}
		canvas.drawBitmap(beaconBitmap, xPosition, yPosition, this.canvasPaint);
	}

	/**
	 * Draw Pause
	 * @param canvas
	 */
	private void drawPause(Canvas canvas) {
		Paint paint = this.initPaint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(2);
		paint.setTextSize(50);
		canvas.drawText("PAUSE", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2, paint);
	}
	
	/**
	 * Draw Stage Detail
	 * @param canvas
	 */
	private void drawStageDetail(Canvas canvas) {
		Paint paint = this.initPaint();
		paint.setColor(Color.CYAN);
		paint.setStrokeWidth(2);
		int textSize = 30;
		int pointX = 10;
		int pointY = 30;
		paint.setTextSize(textSize);
		canvas.drawText("Stage : "+this.stageDetail.getStageName(), pointX, pointY, paint);
		canvas.drawText("Description : "+this.stageDetail.getStageDescription(), pointX, pointY+textSize, paint);
	}

	// end region private variable

	/**
	 * Particle Comparator
	 * @author Lab-01
	 *
	 */
	protected class CompareParticleFilter implements Comparator<Particle> {
		@Override
		public int compare(Particle a, Particle b) {
			if (a.getParticle_x() < b.getParticle_y()) {
				return -1;
			} else if (a.getParticle_x() < b.getParticle_y()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
