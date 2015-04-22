package com.dhcs.reps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{
	private String currType;
	private String currWeight;
	private String currRestTime;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
    private int cnt;
    private boolean isOn;
    SharedPreferences sharedPref;
    private double prevAcc;
    private int currSet;
    private boolean isCountDownOn;
    
    private List<String> recordList;
    //each element in recordList represents each set in a form of "weight count"
    private String[] arraySpinner;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    
        cnt = 0;
        isOn = false;
        recordList = new ArrayList<String>();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        prevAcc = 0;
        currSet = 1;
        isCountDownOn = false;
        
        new CountDownTimer(2000, 1000) {

   	     public void onTick(long millisUntilFinished) {
   	     }

   	     public void onFinish() {
   	    	setContentView(R.layout.activity_main);
   	     }
   	  }.start();
	}
    
    public void init(View v) {
		setContentView(R.layout.activity_main);
        cnt = 0;
        isOn = false;
        recordList = new ArrayList<String>();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        prevAcc = 0;
        currSet = 1;
        isCountDownOn = false;
    }
	
	public void newWorkOut(View v) {
		setContentView(R.layout.activity_new);
	}
	
	public void add(View v) {
		setContentView(R.layout.activity_add);
		this.arraySpinner = new String[] {
	            "Benchpress", "Deadlift", "Squat"
	        };
	        Spinner s = (Spinner) findViewById(R.id.spinner1);
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	                android.R.layout.simple_spinner_item, arraySpinner);
	        s.setAdapter(adapter);
	}
	
	public void addWorkOut(View v) {
		EditText weightField = (EditText)findViewById(R.id.editText2);
		EditText restTimeField = (EditText)findViewById(R.id.editText3);
		Spinner spinner = (Spinner)findViewById(R.id.spinner1);
		
		currWeight = weightField.getText().toString();
		currRestTime = restTimeField.getText().toString();
		currType = spinner.getSelectedItem().toString();

		if (currWeight.length() == 0 || currRestTime.length() == 0) {
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
		name.setText(currType);
		weight.setText(currWeight+"lb");
		TextView record = (TextView)findViewById(R.id.record);
		record.setText("Current Set: " + currSet);
		TextView count = (TextView)findViewById(R.id.count);
		count.setText(""+cnt);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	    double x =  event.values[0];
	    double y =  event.values[1];
	    double z =  event.values[2];
	    
	    double acc = Math.sqrt(x*x + y*y + z*z);
	    
	    if (isOn) {
	    	if (acc - prevAcc > 2) {
	    		cnt++;
	    		TextView count = (TextView)findViewById(R.id.count);
	    		count.setText(""+cnt);
	    		isOn = false;
	    		new CountDownTimer(1000, 1000) {

		    	     public void onTick(long millisUntilFinished) {
		    	     }

		    	     public void onFinish() {
		    	    	 isOn=true;
		    	     }
		    	  }.start();
	    		
	    	}
	    }
	    prevAcc = acc;
		
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
	      recordList.add(currWeight + " " + cnt);
	      cnt = 0;
	      currSet++;
	      final TextView timer = (TextView)findViewById(R.id.timer);
	      int restTimeMiliSec = Integer.parseInt(currRestTime) * 1000;
	      isCountDownOn = true;
	      
	      new CountDownTimer(restTimeMiliSec, 1000) {

	    	     public void onTick(long millisUntilFinished) {
	    	         timer.setText(millisUntilFinished / 1000 + "sec");
	    	     }

	    	     public void onFinish() {
	    	    	 if (isCountDownOn) reMeasure(v);
	    	     }
	    	  }.start();
	  }
	  
	  public void reMeasure(View v) {
		  setContentView(R.layout.activity_remeasure);
	  }
	  
	  public void editWeight(View v) {
		  EditText weightField = (EditText)findViewById(R.id.editText2);
		  currWeight = weightField.getText().toString();
		  if (currWeight.length() > 0) startMeasure(v);
	  }
	
	  public void finish(View v) {
		  isCountDownOn = false;
		  setContentView(R.layout.activity_finished);
	  }
	  
	  public void save(View v) {
		  SharedPreferences.Editor editor = sharedPref.edit();
		  DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		  Calendar cal = Calendar.getInstance();
		  String dateString = dateFormat.format(cal.getTime());
		  String saveString = "";
		  for (String rec : recordList) {
			  saveString += rec + "\n";
		  }
		  editor.putString(dateString + "\n" + currType, saveString);
		  editor.commit();
		  setContentView(R.layout.activity_saved);
		  TextView name = (TextView)findViewById(R.id.name);
		  TextView date = (TextView)findViewById(R.id.date);
		  name.setText(currType);
		  date.setText(dateString);
	  }
	  
	  @SuppressWarnings("unchecked")
	  public void load(View v) {
		  setContentView(R.layout.activity_load);
		  Map<String, ?> data = (Map<String, String>) sharedPref.getAll();
		  Set<String> keySet = data.keySet();
		  List<String> keyList = new ArrayList<String>();
		  keyList.addAll(keySet);
		  Collections.sort(keyList);
		  Collections.reverse(keyList);
		  
		  if (keyList.size() > 0) {
			  String key0 = keyList.get(0);
			  String[] parts0 = key0.split("\n");
			  TextView name1 = (TextView)findViewById(R.id.name1);
			  TextView date1 = (TextView)findViewById(R.id.date1);
			  name1.setText(parts0[1]);
			  date1.setText(parts0[0]);
		  }

		  if (keyList.size() > 1) {
			  String key1 = keyList.get(1);
			  String[] parts1 = key1.split("\n");
			  TextView name2 = (TextView)findViewById(R.id.name2);
			  TextView date2 = (TextView)findViewById(R.id.date2);
			  name2.setText(parts1[1]);
			  date2.setText(parts1[0]);
		  }
		  
		  if (keyList.size() > 2) {
			  String key2 = keyList.get(2);
			  String[] parts2 = key2.split("\n");
			  TextView name3 = (TextView)findViewById(R.id.name3);
			  TextView date3 = (TextView)findViewById(R.id.date3);
			  name3.setText(parts2[1]);
			  date3.setText(parts2[0]);
		  }

	  }
	  
}
