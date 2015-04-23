package com.dhcs.reps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
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
    private double prevAcc;
    private int currSet;
    private boolean isCountDownOn;
    SharedPreferences sharedPref;
    
    private Map<String, Map<Integer, List<Integer>>> record;
    //Map<exercise type, Map<weight, List<number of reps>>>
    private String[] arraySpinner;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    
        cnt = 0;
        isOn = false;
        record = new HashMap<String, Map<Integer, List<Integer>>>();
        prevAcc = 0;
        currSet = 1;
        isCountDownOn = false;
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        
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
        record = new HashMap<String, Map<Integer, List<Integer>>>();
        prevAcc = 0;
        currSet = 1;
        isCountDownOn = false;
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
    }
	
	public void newWorkOut(View v) {
		setContentView(R.layout.activity_new);
	}
	
	public void add_pre(View v) {
		  isOn = false;
		  Map<Integer, List<Integer>> exerciseData = record.get(currType);
		  if (exerciseData == null) {
			  exerciseData = new HashMap<Integer, List<Integer>>();
			  List<Integer> repsData = new ArrayList<Integer>();
			  repsData.add(cnt);
			  exerciseData.put(Integer.parseInt(currWeight), repsData);
			  record.put(currType, exerciseData);
		  }
		  else {
			  List<Integer> repsData = exerciseData.get(Integer.parseInt(currWeight));
			  if (repsData == null) {
				  repsData = new ArrayList<Integer>();
				  repsData.add(cnt);
				  exerciseData.put(Integer.parseInt(currWeight), repsData);
				  record.put(currType, exerciseData);
			  }
			  else {
				  repsData.add(cnt);
				  exerciseData.put(Integer.parseInt(currWeight), repsData);
				  record.put(currType, exerciseData);
			  }
		  }
		  
		  cnt = 0;
	      currSet++;
	      
	      add(v);
	}
	
	public void add(View v) {
		isCountDownOn = false;
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
	
	public void ready(View v) {
		isCountDownOn=false;
		setContentView(R.layout.activity_ready);
		TextView info = (TextView)findViewById(R.id.info);
		info.setText(currType + ": " + currWeight+" lb");
		TextView recordtxt = (TextView)findViewById(R.id.textView1);
		recordtxt.setText("Current Set: " + currSet);
	}
	
	public void startMeasure(View v) {
		setContentView(R.layout.activity_measure);
		
		isOn = true;
		TextView info = (TextView)findViewById(R.id.info);
		info.setText(currType + ": " + currWeight+" lb");
		TextView recordtxt = (TextView)findViewById(R.id.textView1);
		recordtxt.setText("Current Set: " + currSet);
		TextView count = (TextView)findViewById(R.id.cnt);
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
	    		TextView count = (TextView)findViewById(R.id.cnt);
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
		  Map<Integer, List<Integer>> exerciseData = record.get(currType);
		  if (exerciseData == null) {
			  exerciseData = new HashMap<Integer, List<Integer>>();
			  List<Integer> repsData = new ArrayList<Integer>();
			  repsData.add(cnt);
			  exerciseData.put(Integer.parseInt(currWeight), repsData);
			  record.put(currType, exerciseData);
		  }
		  else {
			  List<Integer> repsData = exerciseData.get(Integer.parseInt(currWeight));
			  if (repsData == null) {
				  repsData = new ArrayList<Integer>();
				  repsData.add(cnt);
				  exerciseData.put(Integer.parseInt(currWeight), repsData);
				  record.put(currType, exerciseData);
			  }
			  else {
				  repsData.add(cnt);
				  exerciseData.put(Integer.parseInt(currWeight), repsData);
				  record.put(currType, exerciseData);
			  }
		  }
		  
		  cnt = 0;
	      currSet++;
		  TextView info = (TextView)findViewById(R.id.info);
		  info.setText(currType + ": " + currWeight+" lb");
		  TextView recordtxt = (TextView)findViewById(R.id.textView1);
		  recordtxt.setText("Current Set: " + currSet);
	      final TextView timer = (TextView)findViewById(R.id.timer);
	      int restTimeMiliSec = Integer.parseInt(currRestTime) * 1000;
	      isCountDownOn = true;
	      
	      new CountDownTimer(restTimeMiliSec, 1000) {

	    	     public void onTick(long millisUntilFinished) {
	    	         timer.setText(millisUntilFinished / 1000 + "");
	    	     }

	    	     public void onFinish() {
	    	    	 if (isCountDownOn) ready(v);
	    	     }
	    	  }.start();
	  }
	  
	  public void finish_pre(View v) {
		  isOn = false;
		  Map<Integer, List<Integer>> exerciseData = record.get(currType);
		  if (exerciseData == null) {
			  exerciseData = new HashMap<Integer, List<Integer>>();
			  List<Integer> repsData = new ArrayList<Integer>();
			  repsData.add(cnt);
			  exerciseData.put(Integer.parseInt(currWeight), repsData);
			  record.put(currType, exerciseData);
		  }
		  else {
			  List<Integer> repsData = exerciseData.get(Integer.parseInt(currWeight));
			  if (repsData == null) {
				  repsData = new ArrayList<Integer>();
				  repsData.add(cnt);
				  exerciseData.put(Integer.parseInt(currWeight), repsData);
				  record.put(currType, exerciseData);
			  }
			  else {
				  repsData.add(cnt);
				  exerciseData.put(Integer.parseInt(currWeight), repsData);
				  record.put(currType, exerciseData);
			  }
		  }
		  
		  cnt = 0;
		  finish(v);
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
		  
		  Set<String> typeList = record.keySet();
		  for (String type : typeList) {
			  Map<Integer, List<Integer>> weightData = record.get(type);
			  Set<Integer> weightList = weightData.keySet();
			  for (Integer weight : weightList) {
				  List<Integer> repsList = weightData.get(weight);
				  String repsString = "";
				  for (Integer rep : repsList) {
					  repsString += rep + " ";
				  }
				  editor.putString(dateString + " " + type + " " + weight, repsString);
				  editor.commit();
			  }
		  }		  
		  

		  setContentView(R.layout.activity_saved);
	  }
	  
	  @SuppressWarnings("unchecked")
	  public void load(View v) {
		  setContentView(R.layout.activity_load);

	  }
	  
}
