package com.seoultechappsoftlab.wireloc.activity;

import java.lang.reflect.Field;
import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.entities.Student;
import com.seoultechappsoftlab.wireloc.helpers.SQLiteImportHelper;
import com.seoultechappsoftlab.wireloc.services.BeaconService;
import com.seoultechappsoftlab.wireloc.services.FingerprintService;
import com.seoultechappsoftlab.wireloc.services.StageService;
import com.seoultechappsoftlab.wireloc.services.StudentService;
import com.seoultechappsoftlab.wireloc.utilities.CustomCharactersUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DatabaseImportActivity extends Activity {

	private TextView tvStudentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_main);
		this.showDbImportConfirmation();
		this.initLayoutElement();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	
	
	// Region public method

	private void initLayoutElement() {
		this.tvStudentList = (TextView) findViewById(R.id.tv_studentList);
		this.tvStudentList.setText(CustomCharactersUtils.EMPTY_STRING);
	}

	public void readTestedDatabase() {
		StudentService studentService = new StudentService(this);
		// boolean insertResult = studentService.insert(student);
		List<Student> students = studentService.getAll();
		StringBuilder builder = new StringBuilder();
		for (Student student : students) {
			builder.append("ID: " + student.getId() + "\n");
			builder.append("Name: " + student.getName() + "\n\n");
		}
		this.tvStudentList.setText(builder.toString());
	}

	public void readBeaconData() {
		BeaconService service = new BeaconService(this);
		List<Beacon> beacons = service.getAll();

		StringBuilder builder = new StringBuilder();
		for (Beacon beacon : beacons) {
			builder.append("ID: " + beacon.getId() + "\n");
			builder.append("Mac Address: " + beacon.getMacAddress() + "\n");
			builder.append("Label: " + beacon.getLabel() + "\n");
			builder.append("Position(" + beacon.getPointX() + ", "
					+ beacon.getPointY() + ")\n");
			builder.append("Stage: " + beacon.getStageId() + "\n");
			builder.append("Initial RSSI: " + beacon.getRssi() + "\n");
			builder.append("Is Active "
					+ ((beacon.isActive() == 1) ? "Active" : "Non Active")
					+ "\n");
		}
		this.tvStudentList.setText(builder.toString());
	}

	public void readFingerPrintData() {
		FingerprintService service = new FingerprintService(this);
		List<Fingerprint> fingerprints = service.getAll(4);
		StringBuilder sb = new StringBuilder();
		for (Fingerprint fingerprint : fingerprints) {
			sb.append("Point (" + fingerprint.getPointX() + ", "
					+ fingerprint.getPointY() + ") \n");
			sb.append("Mac Address: " + fingerprint.getMacAddress() + "\n");
			sb.append("RSSI: " + fingerprint.getRssi() + "\n\n");
		}
		this.tvStudentList.setText(sb.toString());
	}

	public void readStage() {
		StageService service = new StageService(this);
		List<Stage> stages = service.getAll();
		StringBuilder sb = new StringBuilder();
		for (Stage stage : stages) {
			sb.append("Id: " + stage.getId() + "\n");
			sb.append("Name: " + stage.getStageName() + "\n");
			sb.append("Is Active: " + stage.getIsActive() + "\n");
			sb.append("Resource Folder: " + stage.getResourceFolderName()
					+ "\n\n");
		}
		this.tvStudentList.setText(sb.toString());
	}

	public static int getResId(String resName) {
		try {
		    @SuppressWarnings("rawtypes")
			Class res = R.drawable.class;
		    Field field = res.getField(resName);
		    int drawableId = field.getInt(null);
		    return drawableId;
		}
		catch (Exception e) {
		    e.printStackTrace();
		    return -1;
		}
	}
	
	// end Region public method
	
	//Region Private Variable
	
	private void showDbImportConfirmation(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(this.getString(R.string.activity_home_dialog_title));
		alertDialogBuilder.setMessage(this.getString(R.string.activity_home_dialog_message));
		alertDialogBuilder.setPositiveButton(this.getString(R.string.activity_home_dialog_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				runImport();
				readBeaconData();
			}
		});
		
		alertDialogBuilder.setNegativeButton(this.getString(R.string.activity_home_dialog_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private boolean importDatabase() {
		boolean result = false;
		SQLiteImportHelper sqliteHelper = new SQLiteImportHelper(this);
		sqliteHelper.removeDatabase(this);
		result = sqliteHelper.importDatabaseFromAssets(this);
		return result;
	}
	
	private void runImport(){
		this.tvStudentList.setText(R.string.bluetooth_not_supported);
		boolean importResult = this.importDatabase();
		if (importResult) {
			this.readBeaconData();
		}
		
		Toast.makeText(getBaseContext(),
				importResult ? "Import Success" : "Import Failed",
				Toast.LENGTH_LONG).show();
	}
	
	//End Region Private Variable
}
