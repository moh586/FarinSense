package farin.code.air;

import farin.code.air.db.DbAdapter;
import farin.code.air.shapeengin.PersianReshap;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setting extends Activity {

	TextView calllbl,accesslbl,textlbl,vibratelbl,langlbl,methodlbl,gyrlbl,scrlbl;
	Spinner mySpinner;
	RadioButton method1,method2,answer,reject,silenc,enable,disable;
	RadioGroup enablerg,methodrg,callrg;
	CheckBox gpschbox,wifichbox,flashchbox,btchbox,textchbox,soundchbox,vibratechbox,gyrchbox,scrchbox;
	Typeface face ;
	RelativeLayout quiceaccess;
	DbAdapter database;
	Context context;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		context=this;
		initial();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}
	
	private void initialdatabase() 
	{
		database=new DbAdapter(this);
	}
	
	private void initial() {
		face = Typeface.createFromAsset(context.getAssets(),"font/irsans.ttf");
		initialdatabase();
		loadSpinner();
		quiceaccess=(RelativeLayout)findViewById(R.id.quiceaccessrl);
		//quiceaccess.setEnabled(true);
		loadTextViews();
		loadRadioButtons();
		loadCheckBox();
		loadRadioGroups();
		
		//
	}
	
	//***********************
	private void loadRadioGroups() 
	{
		RadioGroup.OnCheckedChangeListener enablelistener=new RadioGroup.OnCheckedChangeListener() {


			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				enablequiceaccess(arg1==enable.getId());
				
				if(arg1==enable.getId())
					database.Update(DbAdapter.ACCESS,1);
				else
					database.Update(DbAdapter.ACCESS,0);
			}
		};
		enablerg=setradiogroup(enablelistener, R.id.radioenable);

		//
		RadioGroup.OnCheckedChangeListener calllistener=new RadioGroup.OnCheckedChangeListener() {


			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg1==reject.getId())
					database.Update(DbAdapter.CALL,1);
				else if(arg1==answer.getId())
					database.Update(DbAdapter.CALL,0);
				else
					database.Update(DbAdapter.CALL,2);
			}
		};
		callrg=setradiogroup(calllistener, R.id.radiocall);

		//
		RadioGroup.OnCheckedChangeListener methodistener=new RadioGroup.OnCheckedChangeListener() {


			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg1==method1.getId())
					database.Update(DbAdapter.METHOD,0);
				else
					database.Update(DbAdapter.METHOD,1);
			}
		};
		methodrg=setradiogroup(methodistener, R.id.radiomethod);

		//
	}
	//***********************************
	//**************
		private CheckBox setchackbox(Typeface typeface,int stringid,int id)
		{

			CheckBox t=(CheckBox)findViewById(id);
			t.setTypeface(typeface);
			t.setText(PersianReshap.reshape(getResources().getString(stringid)));
			return t;

		}

		private RadioGroup setradiogroup(RadioGroup.OnCheckedChangeListener i,int id)
		{
			RadioGroup rg=(RadioGroup)findViewById(id);
			rg.setOnCheckedChangeListener(i);
			return rg;
		}

		private void enablequiceaccess(boolean enable)
		{
			gpschbox.setEnabled(enable);
			wifichbox.setEnabled(enable);
			flashchbox.setEnabled(enable);
			btchbox.setEnabled(enable);
			quiceaccess.setEnabled(enable);

		}
		
		//***************************

	private void loadCheckBox()
	{
		OnCheckedChangeListener chboxlis=new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg0.getId()==gpschbox.getId())
					database.Update(DbAdapter.GPS, arg1?1:0);
				else if(arg0.getId()==wifichbox.getId())
					database.Update(DbAdapter.WIFI, arg1?1:0);
				else if(arg0.getId()==flashchbox.getId())
					database.Update(DbAdapter.FLASH, arg1?1:0);
				else if(arg0.getId()==btchbox.getId())
					database.Update(DbAdapter.BT, arg1?1:0);
				else if(arg0.getId()==textchbox.getId()){
					database.Update(DbAdapter.RESHAPE, arg1?1:0);
					MainActivity.Reshape=arg1?1:0;
				}
				else if(arg0.getId()==vibratechbox.getId())
					database.Update(DbAdapter.VIBRATE, arg1?1:0);
				else if(arg0.getId()==soundchbox.getId())
					database.Update(DbAdapter.SOUND, arg1?1:0);
				else if(arg0.getId()==gyrchbox.getId())
				{
					if(arg1)
						VerfiyDialog(arg1);
					else
					{
						database.Update(DbAdapter.Gyroscop, arg1?1:0);
    					startService(new Intent(Setting.this, SensorService.class));
					}
					
				}
				else if(arg0.getId()==scrchbox.getId())
					database.Update(DbAdapter.SCROFF, arg1?1:0);
			}
			//
			private void VerfiyDialog(boolean arg1) {
				final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);

                dialog.setContentView(R.layout.dialog_verify);
                //RelativeLayout dia=(RelativeLayout)findViewById(R.id.dialog);
                //RelativeLayout.LayoutParams dialoglp=(RelativeLayout.LayoutParams)dia.getLayoutParams();
                //dialoglp.addRule(RelativeLayout.LEFT_OF,R.id.imageView);
                //settextbox(face, R.id.textView, R.string.verifylbl);
                //Typeface face = Typeface.createFromAsset(context.getAssets(), "font/irsans.ttf");
                TextView text = (TextView) dialog.findViewById(R.id.textView);
                text.setText(PersianReshap.reshape(getResources().getString(R.string.verifylbl)));
                text.setTypeface(face);
                //
                TextView recom = (TextView) dialog.findViewById(R.id.recom);
                recom.setText(PersianReshap.reshape(getResources().getString(R.string.recomlbl)));
                recom.setTypeface(face);
                //RelativeLayout.LayoutParams textlp=(RelativeLayout.LayoutParams)text.getLayoutParams();
                //textlp.topMargin=
                //
                Button submit=(Button)dialog.findViewById(R.id.submit);
                Button cancel=(Button)dialog.findViewById(R.id.cancel);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	database.Update(DbAdapter.Gyroscop, 1);
    					startService(new Intent(Setting.this, SensorService.class));
    					dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        gyrchbox.setChecked(false);
                    }
                });
                try {
                    dialog.getWindow().getAttributes().windowAnimations = R.style.reshapDialogAnimation;
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
				

		};
		

		gpschbox=setchackbox(face, R.string.gps, R.id.gpschbox);
		gpschbox.setChecked(database.Select(DbAdapter.GPS)==1?true:false);
		gpschbox.setOnCheckedChangeListener(chboxlis);
		//
		wifichbox=setchackbox(face, R.string.wifi, R.id.wifichbox);
		wifichbox.setChecked(database.Select(DbAdapter.WIFI)==1?true:false);
		wifichbox.setOnCheckedChangeListener(chboxlis);
		//
		flashchbox=setchackbox(face, R.string.flash, R.id.flashchbox);
		flashchbox.setChecked(database.Select(DbAdapter.FLASH)==1?true:false);
		flashchbox.setOnCheckedChangeListener(chboxlis);
		//
		btchbox=setchackbox(face, R.string.bt, R.id.btchbox);
		btchbox.setChecked(database.Select(DbAdapter.BT)==1?true:false);
		btchbox.setOnCheckedChangeListener(chboxlis);
		//
		textchbox=setchackbox(face, R.string.textchbox, R.id.textchbox);
		textchbox.setChecked(database.Select(DbAdapter.RESHAPE)==1?true:false);
		textchbox.setOnCheckedChangeListener(chboxlis);
		//
		vibratechbox=setchackbox(face, R.string.vibratechbox, R.id.vibratechbox);
		vibratechbox.setChecked(database.Select(DbAdapter.VIBRATE)==1?true:false);
		vibratechbox.setOnCheckedChangeListener(chboxlis);
		//
		soundchbox=setchackbox(face, R.string.soundchbox, R.id.soundchbox);
		soundchbox.setChecked(database.Select(DbAdapter.SOUND)==1?true:false);
		soundchbox.setOnCheckedChangeListener(chboxlis);
		//
		gyrchbox=setchackbox(face, R.string.gyrchbox, R.id.gyrchbox);
		gyrchbox.setChecked(database.Select(DbAdapter.Gyroscop)==1?true:false);
		gyrchbox.setOnCheckedChangeListener(chboxlis);
		//
		scrchbox=setchackbox(face, R.string.scrchbox, R.id.scrchbox);
		scrchbox.setChecked(database.Select(DbAdapter.SCROFF)==1?true:false);
		scrchbox.setOnCheckedChangeListener(chboxlis);
		//
		enablequiceaccess(enable.isChecked());
	}

	
	//************************
	
	private void loadRadioButtons() 
	{
		method1=setradiobutton(face, R.string.method1, R.id.method1rb,database.Select(DbAdapter.METHOD)==0?true:false);
		method2=setradiobutton(face, R.string.method2, R.id.method2rb,database.Select(DbAdapter.METHOD)==1?true:false);
		answer =setradiobutton(face, R.string.callanswer, R.id.answerrb,database.Select(DbAdapter.CALL)==0?true:false);
		reject =setradiobutton(face, R.string.callreject, R.id.rejectrb,database.Select(DbAdapter.CALL)==1?true:false);
		silenc =setradiobutton(face, R.string.callsilence,R.id.silencrb,database.Select(DbAdapter.CALL)==2?true:false);
		enable =setradiobutton(face, R.string.quiceaccessenable, R.id.enable,database.Select(DbAdapter.ACCESS)==1?true:false);
		disable=setradiobutton(face, R.string.quiceaccessdisable,R.id.disablerb,database.Select(DbAdapter.ACCESS)==0?true:false);
	}
	
	//**************
		private RadioButton setradiobutton(Typeface typeface,int stringid,int id,boolean checked)
		{
			RadioButton t=(RadioButton)findViewById(id);
			t.setTypeface(typeface);
			t.setText(PersianReshap.reshape(getResources().getString(stringid)));
			t.setChecked(checked);
			return t;

		}
	
	private void loadTextViews() 
	{
		//offlbl=settextbox(face,   R.string.off, R.id.off);
		//onlbl =settextbox( face,    R.string.on,  R.id.on);
		calllbl=settextbox(face,  R.string.calltitle, R.id.calltitle);
		accesslbl=settextbox(face,R.string.quiceaccesstitle, R.id.quiceaccesstitle);
		textlbl=settextbox(face,R.string.texttitle, R.id.texttitle);
		vibratelbl=settextbox(face,R.string.vibratetitle, R.id.vibratetitle);
		langlbl=settextbox(face,R.string.langtitle, R.id.langtitle);
		methodlbl=settextbox(face,R.string.detecttitle, R.id.detecttitle);
		gyrlbl=settextbox(face,R.string.gyrtitle, R.id.gyrtitle);
		scrlbl=settextbox(face,R.string.scrtitle, R.id.scrtitle);
	}
	
	private TextView settextbox(Typeface typeface,int stringid,int id)
	{
		TextView t=(TextView)findViewById(id);
		t.setTypeface(typeface);
		t.setText(PersianReshap.reshape(getResources().getString(stringid)));
		if(MainActivity.Lang==0)
			t.setGravity(Gravity.RIGHT);
		else
			t.setGravity(Gravity.LEFT);
		return t;

	}
	
	private void loadSpinner() {

		mySpinner = (Spinner) findViewById(R.id.langspin);
		final String[] data = getResources().getStringArray(
				R.array.language);
		final ContactsSpinnerAdapater adapter = new ContactsSpinnerAdapater(data, this);
		mySpinner.setAdapter(adapter); 
		//
		mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2==0)
				{
					MainActivity.setLanguage(context, "fa");
					database.Update(DbAdapter.LANG,0);
					MainActivity.Lang=0;
				}
				else if(arg2==1)
				{
					MainActivity.setLanguage(context, "en");
					database.Update(DbAdapter.LANG,1);
					MainActivity.Lang=1;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		if(MainActivity.Lang==0)
			mySpinner.setSelection(0);
		else
			mySpinner.setSelection(1);

	}
	
	
	
	public class ContactsSpinnerAdapater extends BaseAdapter implements SpinnerAdapter
	{
		//private Context context;
		String[] data = null;
		private final Activity activity;
		int rowid;

		public ContactsSpinnerAdapater(String[] data2,Activity activity) {
			this.activity=activity;
			this.data=data2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			return getCustomView(position, convertView, parent,false);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent)
		{   
			return getCustomView(position, convertView, parent,true);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent,boolean col) 
		{
			View row = convertView;

			//inflate your customlayout for the textview
			LayoutInflater inflater =activity. getLayoutInflater();
			row = inflater.inflate(R.layout.spinner_item,null);

			//put the data in it
			String item = data[position];
			if(item != null)
			{   
				TextView text = (TextView) row.findViewById(R.id.spintxt);
				text.setText(item);
				if(col)
					text.setTextColor(Color.parseColor("#333333"));	
				else
					text.setTextColor(Color.parseColor("#f2f2f2"));	//
				ImageView img=(ImageView)row.findViewById(R.id.spinimg);
				if(position==0)
					img.setImageResource(R.drawable.persian);
				else
					img.setImageResource(R.drawable.english);

				Typeface type=Typeface.createFromAsset(context.getAssets(),"font/irsans.ttf");
				text.setTypeface(type);

			}

			return row;


		}

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int arg0) {
			return data[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

	}


}
