package farin.code.air;


import java.util.Locale;
import de.ankri.views.Switch;
import farin.code.air.db.DbAdapter;
import farin.code.air.shapeengin.PersianReshap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity  {


	//local variable
	TabHost tabHost;	
	Context context;
	Switch onoff;				//custom component for switchONOFF
	SetSwitchONOFF setter;		//Controller for Switch
	TextView help;
	Dialog dialog=null;
	Typeface face ;
	ImageView helpimg;
	DbAdapter database;

	//globale varible
	public static int Lang=0;
	public static int Reshape=0;
	public boolean expire;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		SetAlarm();
		initial();

	}

	private void loadLang() {
		
		if(database.Select(DbAdapter.LANG)==0)
		{
			setLanguage(context, "fa");
			Lang=0;
		}
		else{
			setLanguage(context, "en");
			Lang=1;
		}

	}
	
//	public boolean checkexpire()
//	{
//		if(database.Select(DbAdapter.Run)>=5)
//		{
//			database.Update(DbAdapter.ON, 0);
//			return true;
//		}
//		return false;	
//	}

	private void initial() {
		face = Typeface.createFromAsset(context.getAssets(),"font/irsans.ttf");
		initialdatabase();
		loadLang();
		/*
		if(checkexpire())
		{
			loaderexpireDialog();
		}
		else
		{
			database.Update(DbAdapter.Run, database.Select(DbAdapter.Run)+1);
		}*/
		loadReshape();
		//loadTabHost();
		loadSeekBar();
		loadButton();
		//quiceaccess.setEnabled(true);
		loadTextViews();
		loadimages();
		loadreShapeDialog();
		//
	}



	private void loadReshape() {
		if(database.Select(DbAdapter.RESHAPE)==0)
		{
			Reshape=0;
		}
		else{
			Reshape=1;
		}
		
	}

	private void initialdatabase() 
	{
		database=new DbAdapter(this);
	}

	private void loadimages() {
		
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int ScreenW=metrics.widthPixels;
		helpimg=(ImageView)findViewById(R.id.helpimg);
		helpimg.setImageResource(R.drawable.helpimg);
		RelativeLayout.LayoutParams lp=
				(RelativeLayout.LayoutParams)helpimg.getLayoutParams();
		//lp.width=(int)(ScreenW*3f/5);
		//lp.height=lp.width;
		//lp.topMargin=(int)(lp.height/7);
		//helpimg.setImageResource(R.drawable.helpimg);
		//
		RelativeLayout.LayoutParams hlp=
				(RelativeLayout.LayoutParams)help.getLayoutParams();
		hlp.rightMargin=hlp.leftMargin=(int)((ScreenW-lp.width)/12);
		//help.setTextSize(ScreenW/45);
		
	}

	

	private void loadreShapeDialog() {
		if(Lang==1)
			return;
		if(database.Select(DbAdapter.FIRST)==1)
		{
			final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_reshape);
			//Typeface face = Typeface.createFromAsset(contex.getAssets(),"font/"+farin.code.rahnamaee.attrib.attribute.font_title);

			Button enablereshape = (Button) dialog.findViewById(R.id.withreshape);
			enablereshape.setText(PersianReshap.reshape(getResources().getString(R.string.trytxt)));
			enablereshape.setTypeface(face);
			//enablereshape.setTextSize(farin.code.rahnamaee.attrib.attribute.title1_font_size);
			// if button is clicked, close the custom dialog
			enablereshape.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					database.Update(DbAdapter.RESHAPE,1);
					Reshape=1;
					database.Update(DbAdapter.FIRST,0);
				}
			});
			Button disablereshape = (Button) dialog.findViewById(R.id.withoutreshape);
			disablereshape.setText(getResources().getString(R.string.trytxt));
			disablereshape.setTypeface(face);
			//disablereshape.setTextSize(farin.code.rahnamaee.attrib.attribute.title1_font_size);
			// if button is clicked, close the custom dialog
			disablereshape.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					database.Update(DbAdapter.RESHAPE,0);
					Reshape=0;
					database.Update(DbAdapter.FIRST,0);
				}
			});
			//
			TextView trylbl =(TextView)dialog.findViewById(R.id.trylbl);
			trylbl.setTypeface(face);
			trylbl.setText(PersianReshap.reshape(getResources().getString(R.string.trylbl)));
			//
			try{
				dialog.getWindow().getAttributes().windowAnimations = R.style.reshapDialogAnimation;
				dialog.show();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/*
	private void loaderexpireDialog() {
		expire=true;
			dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_expire);
			//Typeface face = Typeface.createFromAsset(contex.getAssets(),"font/"+farin.code.rahnamaee.attrib.attribute.font_title);
			ImageButton download = (ImageButton) dialog.findViewById(R.id.aboutus);
			//enablereshape.setText(PersianReshap.reshape(getResources().getString(R.string.trytxt)));
			//enablereshape.setTypeface(face);
			//enablereshape.setTextSize(farin.code.rahnamaee.attrib.attribute.title1_font_size);
			// if button is clicked, close the custom dialog
			download.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					finish();
				}
			});
			
			TextView expirelbl =(TextView)dialog.findViewById(R.id.expirelbl);
			expirelbl.setTypeface(face);
			expirelbl.setText(PersianReshap.reshape(getResources().getString(R.string.expirelbl)));
			//
			try{
				dialog.getWindow().getAttributes().windowAnimations = R.style.reshapDialogAnimation;
				dialog.show();
				dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
						if( keyCode == KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_HOME ) {
							finish();
					        return true;
					    }
						return false;
					}
				});
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

	}
	*/

	private void loadTextViews() 
	{
		help=settextbox(face,R.string.help, R.id.helptxt);
	}
	
	private void loadButton()
	{
		ImageButton setting=(ImageButton)findViewById(R.id.setting);
		setting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, Setting.class));
			}
		});
		//
		ImageButton helpbtn=(ImageButton)findViewById(R.id.helpbtn);
		helpbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, Help.class));
			}
		});
		ImageButton aboutbtn=(ImageButton)findViewById(R.id.aboutus);
		aboutbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, About.class));
			}
		});
	}
	private void loadSeekBar() {
		onoff=(Switch)findViewById(R.id.onoff);
		setter=new SetSwitchONOFF();
		//
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int ScreenW=metrics.widthPixels;
        
		//
        onoff.mSwitchWidth=(int)(ScreenW*4/7f);
        onoff.mThumbWidth=(int)(onoff.mSwitchWidth/2.2);
		//
		if(database.Select(DbAdapter.ON)==1)
		{
			onoff.setChecked(true);
			Intent intent = new Intent(MainActivity.this, SensorService.class);
			startService(intent);
		
			//
		}
		else
		{
			onoff.setChecked(false);
		}
		onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked)
		    	{
		    		Intent intent = new Intent(MainActivity.this, SensorService.class);
					startService(intent);
					database.Update(DbAdapter.ON, 1);
					
		    	}
		    	else
		    	{
		    		Intent intent = new Intent(MainActivity.this, SensorService.class);
					stopService(intent);
					database.Update(DbAdapter.ON, 0);
		    	}
			}
		});
	}
	
	public void SetAlarm()
	{
		AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(context, OnAlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(context, 0,i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime()+60000,
				BootCompletedIntentReceiver.PERIOD,
				pi);
	}

	private TextView settextbox(Typeface typeface,int stringid,int id)
	{
		TextView t=(TextView)findViewById(id);
		t.setTypeface(typeface);
		t.setText(PersianReshap.reshape(getResources().getString(stringid)));
		if(Lang==0)
			t.setGravity(Gravity.RIGHT);
		else
			t.setGravity(Gravity.LEFT);
		return t;

	}
	
	

	public static void setLanguage(Context context, String languageToLoad) {
		Locale locale2 = new Locale(languageToLoad); 
		Locale.setDefault(locale2);
		Configuration config2 = new Configuration();
		config2.locale = locale2;
		context.getResources().updateConfiguration(config2, context.getResources().getDisplayMetrics());
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if( keyCode == KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_HOME ) {
			finish();
	        return true;
	    }
	    else{
	        return super.onKeyDown(keyCode, event);
	    }
	}
	
	
	//
	private class SetSwitchONOFF extends AsyncTask<Boolean, Void, Void> {
	    @Override
	    protected Void doInBackground(Boolean... params) {
	    	onoff.setEnabled(false);
	    	if(params[0])
	    	{
	    		Intent intent = new Intent(MainActivity.this, SensorService.class);
				startService(intent);
				database.Update(DbAdapter.ON, 1);
	    	}
	    	else
	    	{
	    		Intent intent = new Intent(MainActivity.this, SensorService.class);
				stopService(intent);
				database.Update(DbAdapter.ON, 0);
	    	}
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	    	onoff.setEnabled(true);
	    }
	  }

	
}
