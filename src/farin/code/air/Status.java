package farin.code.air;

import farin.code.air.R;
import farin.code.air.calender.MultiCalendar;
import farin.code.air.calender.YearMonthDate;
import farin.code.air.db.DbAdapter;
import farin.code.air.shapeengin.PersianReshap;
import farin.code.air.status.Listeners;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Status extends Activity {

	public static final int Persian =0;
	public static final int Arabic  =1;
	public static final int English =2;

	boolean access,gps,wifi,bt,flash;
	int Lang=Persian;
	Player player;
	static Activity activity;
	Typeface clocktf,notifytf;
	static DbAdapter loader;
	
	public static Handler mhHandler=new Handler();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON); 
		setContentView(R.layout.activity_status);
		//
		loader=new DbAdapter(this);
	    access=loader.Select(DbAdapter.ACCESS)==1?true:false;
		//access=getIntent().getStringExtra(MainActivity.ACCESS).equals("1")?true:false;
		gps=loader.Select(DbAdapter.GPS)==1?true:false;
		wifi=loader.Select(DbAdapter.WIFI)==1?true:false;
		bt=loader.Select(DbAdapter.BT)==1?true:false;
		flash=loader.Select(DbAdapter.FLASH)==1?true:false;
		
		//
		loader.Update(DbAdapter.STATUS, 1);
		//
		initial();
		//loader.Update(DbAdapter.Run, loader.Select(DbAdapter.Run)+1);
		mhHandler.postDelayed(runable, 8000);

	}
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!PlaybackService.hasInstance())
			startService(new Intent(this, PlaybackService.class));
	}



	private void initial() {
		activity=this;  
		loadTypeFaces();
		loadLang();
		loadclock();
		loadImageButtons();
		misscallcount();
		getNumberOfUnreadMessages();
		this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		loadDate();
		loadPlayer();
		loadfirstanimation();
	}

	private void loadLang() {
		if(MainActivity.Lang==0)
			Lang=Status.Persian;
		else
			Lang=Status.English;
		
	}
	
	

	@Override
	protected void onDestroy() {
		unregisterReceiver(batteryInfoReceiver);
		super.onDestroy();
	}

	private void loadfirstanimation() {
		RelativeLayout lightrl=(RelativeLayout)findViewById(R.id.lightrl);
		AnimationSet anim=(AnimationSet) AnimationUtils.loadAnimation(this, R.anim.r2c2);
		lightrl.startAnimation(anim);
	}

	private void loadPlayer() 
	{
		ImageButton play=(ImageButton)findViewById(R.id.playimgbtn);
		ImageButton back=(ImageButton)findViewById(R.id.backimgbtn);
		ImageButton next=(ImageButton)findViewById(R.id.nextimgbtn);
		SeekBar timeline=(SeekBar)findViewById(R.id.timeline);
			player=new Player(activity, play, next, back, timeline);
		
	}
	
	private void loadDate() {
		TextView datename;
		TextView datenum;
		TextView monthname;
		TextView yearnum;
		//
		YearMonthDate date;
		if(Lang==Persian||Lang==Arabic)
		{
			//daterllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			date=MultiCalendar.gregorianToJalali(MultiCalendar.gregorian());
			yearnum=(TextView)findViewById(R.id.recom);
			monthname=(TextView)findViewById(R.id.textView2);
			datenum=(TextView)findViewById(R.id.textView3);
			datename=(TextView)findViewById(R.id.textView4);
		}
		else
		{
			date=MultiCalendar.gregorian();
			//daterllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			yearnum=(TextView)findViewById(R.id.textView4);
			monthname=(TextView)findViewById(R.id.textView3);
			datenum=(TextView)findViewById(R.id.textView2);
			datename=(TextView)findViewById(R.id.recom);
		}
		//
		yearnum.setTypeface(notifytf);
		monthname.setTypeface(notifytf);
		datenum.setTypeface(notifytf);
		datename.setTypeface(notifytf);
		//
		
		datename.setText(PersianReshap.reshape(date.getDayName(Lang)));
		datenum.setText(PersianReshap.reshape(String.valueOf(date.getDate())));
		monthname.setText(PersianReshap.reshape(String.valueOf(date.getMonthName(Lang))));
		yearnum.setText(PersianReshap.reshape(String.valueOf(date.getYear())));
	}

	private void loadTypeFaces() 
	{
		clocktf=Typeface.createFromAsset(this.getAssets(),"font/Eurosti.ttf");
		notifytf=Typeface.createFromAsset(this.getAssets(),"font/irsans.ttf");
	}

	private void loadImageButtons() 
	{
		if(!access)
		{
			RelativeLayout rl=(RelativeLayout)findViewById(R.id.quiceaccessrl);
			rl.setVisibility(View.INVISIBLE);
			return;
		}
		//
		Listeners onclicks=new Listeners(this);
		onclicks.inialgps(setImageButton(onclicks.gps,R.id.gpsimgbtn,gps));
		onclicks.inialflash(setImageButton(onclicks.flash,R.id.flashimgbtn,flash));
		onclicks.inialbt(setImageButton(onclicks.bt,R.id.btimgbtn,bt));
		onclicks.inialwifi(setImageButton(onclicks.wifi,R.id.wifibtnimg,wifi));
	}



	private ImageButton setImageButton(OnClickListener onclick, int id,boolean view) {
		ImageButton wifiimgbtn=(ImageButton)findViewById(id);
		wifiimgbtn.setOnClickListener(onclick);
		if(view)
			wifiimgbtn.setVisibility(View.VISIBLE);
		else
			wifiimgbtn.setVisibility(View.INVISIBLE);
		return wifiimgbtn;

	}

	private void loadclock() {
		Time now=new Time();
		now.setToNow();
		String hourse,minuate;
		hourse=String.valueOf(now.hour);
		if(hourse.length()==1)
			hourse="0"+hourse;

		minuate=String.valueOf(now.minute);
		if(minuate.length()==1)
			minuate="0"+minuate;
		//

		loadclocktxt(clocktf,R.id.clocktxt1,hourse.substring(0, 1));
		loadclocktxt(clocktf,R.id.clocktxt2,hourse.substring(1, 2));
		loadclocktxt(clocktf,R.id.clocktxt3,":");
		loadclocktxt(clocktf,R.id.clocktxt4,minuate.substring(0, 1));
		loadclocktxt(clocktf,R.id.clocktxt5,minuate.substring(1, 2));
		//
		//
		loadclocktxtshadow(clocktf,R.id.clocktxt1shadow,hourse.substring(0, 1));
		loadclocktxtshadow(clocktf,R.id.clocktxt2shadow,hourse.substring(1, 2));
		loadclocktxtshadow(clocktf,R.id.clocktxt3shadow,":");
		loadclocktxtshadow(clocktf,R.id.clocktxt4shadow,minuate.substring(0, 1));
		loadclocktxtshadow(clocktf,R.id.clocktxt5shadow,minuate.substring(1, 2));
		//
	}

	private TextView loadclocktxt(Typeface typeface,int id,String content) {
		TextView secondTextView =(TextView)findViewById(id);
		//Shader textShader=new LinearGradient(0, 0, 0, 60,
		//		new int[]{Color.argb(255, 198, 198, 198),Color.argb(255, 143, 146, 148)},
		//		new float[]{0, 1}, TileMode.CLAMP);
		//secondTextView.getPaint().setShader(textShader);
		secondTextView.setTextColor(Color.parseColor("#ffffff"));
		secondTextView.setTypeface(typeface);
		secondTextView.setText(content);
		return secondTextView;
	}

	private TextView loadclocktxtshadow(Typeface typeface,int id,String content) {
		TextView secondTextView =(TextView)findViewById(id);
		Shader textShader=new LinearGradient(0, 0, 0, 100,
				new int[]{Color.argb(0, 0, 0, 0),Color.argb(60, 250, 250, 250)},
				new float[]{0, 1}, TileMode.MIRROR);
		secondTextView.getPaint().setShader(textShader);
		secondTextView.setTypeface(typeface);
		secondTextView.setText(content);
		//
		AnimationSet animSet = new AnimationSet(true);
		animSet.setInterpolator(new DecelerateInterpolator());
		animSet.setFillAfter(true);
		animSet.setFillEnabled(true);

		final RotateAnimation animRotate = new RotateAnimation(0.0f, 180.0f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);

		animRotate.setDuration(0);
		animRotate.setFillAfter(true);
		animSet.addAnimation(animRotate);
		secondTextView.startAnimation(animSet);
		//
		return secondTextView;
	}

	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			//int  health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
			//int  icon_small= intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL,0);
			int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
			//int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
			//boolean  present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT); 
			//int  scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
			//int  status= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
			//String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
			//int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
			//int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0)
			
			TextView batterylbl=(TextView)findViewById(R.id.batterylbl);
			batterylbl.setText(PersianReshap.reshape(String.valueOf(level)));
		}
	};

	/*
	 * this method return number of new miss call
	 */
	public void misscallcount()
	{
		String[] projection = { CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE };
		String where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.NEW + "=1" ;         
		Cursor c = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection,where, null, null);
		c.moveToFirst();    
		Log.d("CALL", ""+c.getCount()); //do some other operation
		TextView misscalllbl=(TextView)findViewById(R.id.misscalllbl);
		String temp;
		if(c.getCount()==0)
		{
			temp="  ";
		}
		else if(c.getCount()<10)
		{
			temp=" "+String.valueOf(c.getCount());
		}
		else
		{
			temp=String.valueOf(c.getCount());
		}
		misscalllbl.setText(PersianReshap.reshape(temp));
	}


	/*
	 * this method return number of un readed message
	 */
	public void  getNumberOfUnreadMessages() 
	{
		
		final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
		Cursor c = getContentResolver().query(SMS_INBOX, null, "read = 0", null, null);
		int unreadMessagesCount = c.getCount();
		TextView smslbl=(TextView)findViewById(R.id.smslbl);
		String temp;
		if(unreadMessagesCount==0)
		{
			temp="  ";
		}
		else if(unreadMessagesCount<10)
		{
			temp=" "+String.valueOf(unreadMessagesCount);
		}
		else
		{
			temp=String.valueOf(unreadMessagesCount);
		}
		smslbl.setText(PersianReshap.reshape(temp));
	}


	/*
	 * this Runable object is for clossing activity after a few  seconds
	 */
	public static Runnable runable=new Runnable() 
	{
		@Override
		public void run() {
			loader.Update(DbAdapter.STATUS, 0);
			activity.finish();
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if( keyCode == KeyEvent.KEYCODE_BACK ) {
	    	loader.Update(DbAdapter.STATUS, 0);
			finish();
	        return true;
	    }
	    else {
	        return super.onKeyDown(keyCode, event);
	    }
	}
	
	public static void postpone()
	{
		mhHandler.removeCallbacks(runable);
		mhHandler.postDelayed(runable, 8000);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		postpone();
		return super.onTouchEvent(event);
	}
	
	
}
