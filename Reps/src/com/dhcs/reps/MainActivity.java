package com.dhcs.reps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{
	private String currName;
	private String currWeight;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
    private int prevX,prevY,prevZ;
    private int cnt;
    private boolean isOn;
    
    private List<String> recordList;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    
        prevX = 0;
        prevY = 0;
        prevZ = 0;
        cnt = 0;
        isOn = false;
        recordList = new ArrayList<String>();
	}
	
	public void newWorkOut(View v) {
		setContentView(R.layout.activity_add);
	}
	
	public void addWorkOut(View v) {
		EditText nameField = (EditText)findViewById(R.id.editText1);
		EditText weightField = (EditText)findViewById(R.id.editText2);
		
		currName = nameField.getText().toString();
		currWeight = weightField.getText().toString();

		if (currName.length() == 0 || currWeight.length() == 0) {
			TextView error = (TextView)findViewById(R.id.error);
			error.setText("Invalid Input");
		}
		else setContentView(R.layout.activity_place);
	}
	
	public void startMeasure(View v) {
		setContentView(R.layout.activity_measure);
		
		isOn = true;
		TextView name = (TextView)findViewById(R.id.currname);
		TextView weight = (TextView)findViewById(R.id.currweight);
		name.setText(currName);
		weight.setText(currWeight+"lb");
		TextView record = (TextView)findViewById(R.id.record);
		String recordString = "";
		for (int i = 0; i < recordList.size(); i++) {
			if (i != 0) recordString +=" - ";
			recordString += recordList.get(i)+"reps";
		}
		record.setText(recordString);
		TextView count = (TextView)findViewById(R.id.count);
		count.setText(""+cnt);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	    int x =  (int)event.values[0];
	    int y =  (int)event.values[1];
	    int z =  (int)event.values[2];
	    
	    if (isOn) {
	    	if ((prevX > 2 && x < -2) ||
	    		(prevY > 2 && y < -2) ||
	    		(prevZ > 2 && z < -2)) {
	    		cnt++;
	    		TextView count = (TextView)findViewById(R.id.count);
	    		count.setText(""+cnt);
	    	}
	    }
		
	    prevX = x;
	    prevY = y;
	    prevZ = z;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	  @Override
	  protected void onResume()
	  {
	    super.onResume();
	    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  @Override
	  protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
	
	  public void rest(final View v) {
		  setContentView(R.layout.activity_rest);
		  isOn = false;
	      prevX = 0;
	      prevY = 0;
	      prevZ = 0;
	      recordList.add(""+cnt);
	      cnt = 0;
	      final TextView timer = (TextView)findViewById(R.id.timer);
	      
	      new CountDownTimer(30000, 1000) {

	    	     public void onTick(long millisUntilFinished) {
	    	         timer.setText(millisUntilFinished / 1000 + "sec");
	    	     }

	    	     public void onFinish() {
	    	    	 startMeasure(v);
	    	     }
	    	  }.start();
	  }
	
	  public void finish(View v) {
		  setContentView(R.layout.activity_finished);
			TextView record = (TextView)findViewById(R.id.record);
			String recordString = "";
			for (int i = 0; i < recordList.size(); i++) {
				if (i != 0) recordString +=" - ";
				recordString += recordList.get(i)+"reps";
			}
			record.setText(recordString);
	  }
}
