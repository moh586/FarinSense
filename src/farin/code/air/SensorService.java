package farin.code.air;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import farin.code.air.db.DbAdapter;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;


public class SensorService extends Service {

	public static SensorManager mSensorManager=null;
	public static Sensor mproximity=null;
	public static Sensor maccelator=null;
	public static Sensor mgyroscope=null;
	public static SensorEventListener sensoreventlistner=null;
	private static int TIMEDISTANCE=1400;
	private  boolean ISTouch=false;
	public static final String NOTIFICATION = "farin.code.air.status";
	public static final String INSTRUCTION = "instruction";
	public static int LastState;
	public static final int  NEXT=1;
	public static final int  BACK=2;

	BroadcastReceiver CallBlocker;
	TelephonyManager telephonyManager;
	ITelephony telephonyService;
	public static boolean calling=false;
	public static boolean flating=true;
	private final IBinder mBinder = new MyBinder();
	DbAdapter loader;
	long start,end;

	@Override
	public void onCreate() 
	{
		super.onCreate();
		initialloader();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		inialCallReciver();	//Make Caller Reciver Ready 
		inialSensor();
		return Service.START_NOT_STICKY;
	}

	private void initialloader() {
		loader=new DbAdapter(this);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class MyBinder extends Binder {
		SensorService getService() 
		{
			return SensorService.this;
		}
	}
	//*************************
	/*
	 * this Method is for Running Status Activity
	 */
	private void publishResults(int instruction) 
	{

		if(loader.Select(DbAdapter.STATUS)==1)
		{
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					loader.Update(DbAdapter.STATUS, 0);
				}
			}, 8000);
			return;
		}
		Intent intent = new Intent(this,farin.code.air.Status.class);
		//Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
		intent.putExtra(INSTRUCTION, instruction);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//sendBroadcast(intent);
		play();
		vibrate();
		startActivity(intent);
	}

	private void inialSensor()
	{
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mSensorManager.unregisterListener(sensoreventlistner);
		mproximity =  mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		//

		if(loader.Select(DbAdapter.METHOD)==0)
		{
			maccelator=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(sensoreventlistner,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		else
		{
			mSensorManager.unregisterListener(sensoreventlistner,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		}
		//
		if(loader.Select(DbAdapter.Gyroscop)==0)
		{
			flating=true;
			mSensorManager.unregisterListener(sensoreventlistner, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		}
		else
		{
			mgyroscope=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			mSensorManager.registerListener(sensoreventlistner,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					500);
			flating=false;
			if(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null)
				flating=true;
			else
				flating=false;
		}
		//mgyroscope=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		//

		sensoreventlistner=new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) 
			{
				if(loader.Select(DbAdapter.ON)==0)
					return;
				//Log.d("madalll", String.valueOf(ISOFF()));

				if(event.sensor.getType()==Sensor.TYPE_ORIENTATION)
				{
					if(Math.abs(event.values[2])<13&&Math.abs(event.values[1])<13&&loader.Select(DbAdapter.Gyroscop)==1)
						flating=true;
					else
						flating=false;
				}
				if(loader.Select(DbAdapter.METHOD)==1)
				{
					if(event.sensor.getType()==Sensor.TYPE_PROXIMITY &&flating)
					{
						if(event.values[0]>0)
						{
							end=System.currentTimeMillis();
							if(end-start>500)
								ISTouch=false;
							else
								detectAction();

						}else
						{
							start=System.currentTimeMillis();
						}

					}
				}
				else 
				{
					if(ISTouch)
					{
						if(event.sensor.getType()==Sensor.TYPE_PROXIMITY && event.values[0]==0)
							detectAction();
					}
					else if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER&& (Math.abs(event.values[0])+Math.abs(event.values[1])+Math.abs(event.values[2]))>30)
					{
						detectAction();
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mSensorManager.registerListener(sensoreventlistner,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	private boolean ISOFF()
	{
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		boolean isScreenOn = powerManager.isScreenOn();
		boolean disablescreenon=loader.Select(DbAdapter.SCROFF)==1?true:false;
		if(!disablescreenon)
			return !disablescreenon;
		return !isScreenOn;
	}


	private void detectAction()
	{
		//Log.d("debugsens",String.valueOf(loader.Select(DbAdapter.ON)));
		if(ISTouch)
		{
			if(calling)
			{
				ISTouch=false;
				try {
					if(loader.Select(DbAdapter.CALL)==1)
						telephonyService.endCall();
					else if(loader.Select(DbAdapter.CALL)==0)
					{
						try {
							answerPhoneAidl(getBaseContext());
						} catch (Exception e) {
							answerPhoneHeadsethook(getBaseContext());
							//enableSpeakerPhone(getBaseContext());
						}
					}
					else if(loader.Select(DbAdapter.CALL)==2)
					{
						AudioManager audioManager= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
						audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT); 
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			else
			{
				if(ISOFF())
					publishResults(NEXT);
			}

		}
		else
		{
			if(ISOFF()||calling)
			{
				ISTouch=true;
				//firsttime=System.currentTimeMillis();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ISTouch=false;
					}
				}, TIMEDISTANCE);
				vibrate();
				play();
			}
		}

	}



	@Override
	public void onDestroy() {
		unregisterReceiver(CallBlocker);
		mSensorManager.unregisterListener(sensoreventlistner);
		super.onDestroy();
	}

	private void inialCallReciver()
	{
		CallBlocker =new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) {
				telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
				//Java Reflections
				Class<?> c = null;
				try {
					c = Class.forName(telephonyManager.getClass().getName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				Method m = null;
				try {
					m = c.getDeclaredMethod("getITelephony");
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				m.setAccessible(true);
				try {
					telephonyService = (ITelephony)m.invoke(telephonyManager);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				telephonyManager.listen(callBlockListener, PhoneStateListener.LISTEN_CALL_STATE);
			}//onReceive()
			PhoneStateListener callBlockListener = new PhoneStateListener()
			{
				public void onCallStateChanged(int state, String incomingNumber)
				{
					if(state==TelephonyManager.CALL_STATE_RINGING)
					{
						LastState=LastState();
						calling=true;
					}
					else if(state==TelephonyManager.CALL_STATE_OFFHOOK)
					{
						LastState=LastState();
						calling=false;
						mSensorManager.unregisterListener(sensoreventlistner);
						loader.Update(DbAdapter.ON,0);
					}
					else 
					{
						calling=false;
						//register sensor listner
						loader.Update(DbAdapter.ON,1);
						inialSensor();

						//change profile to normal mood
						AudioManager audioManager= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
						audioManager.setRingerMode(LastState); 

					}
				}
			};
		};//BroadcastReceiver
		IntentFilter filter= new IntentFilter("android.intent.action.PHONE_STATE");
		registerReceiver(CallBlocker, filter);
	}
	public int LastState()
	{
		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		return am.getRingerMode();
	}

	public void play()
	{
		if(loader.Select(DbAdapter.SOUND)==1)
		{
			MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.notification);
			AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			try
			{
				if(!(am.getRingerMode()==AudioManager.RINGER_MODE_SILENT || 
						am.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE))
					mPlayer.start();
				mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						arg0.release();

					}
				});
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void vibrate()
	{
		if(loader.Select(DbAdapter.VIBRATE)==1)
		{
			Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
			vibe.vibrate(50);
		}
	}

	private void answerPhoneHeadsethook(Context context) {
		//
		Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
		headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
		headSetUnPluggedintent.putExtra("name", "Headset");
		sendOrderedBroadcast(headSetUnPluggedintent, null);
		// Simulate a press of the headset button to pick up the call
		Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);            
		buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");

		// froyo and beyond trigger on buttonUp instead of buttonDown
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);              
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

		headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
		headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		headSetUnPluggedintent.putExtra("state", 0); // 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
		headSetUnPluggedintent.putExtra("name", "Headset");
		sendOrderedBroadcast(headSetUnPluggedintent, null);

	}

	@SuppressWarnings("unchecked")
	private void answerPhoneAidl(Context context) throws Exception {
		// Silence the ringer and answer the call!
		telephonyService.silenceRinger();
		telephonyService.answerRingingCall();
	}

}
