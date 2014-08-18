package farin.code.air.status;

import farin.code.air.R;
import farin.code.air.Status;
import farin.code.air.option.Bluetooth;
import farin.code.air.option.Flashlight;
import farin.code.air.option.GPS;
import farin.code.air.option.WiFi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Listeners 
{
	Activity context;
	Context ApplicationContext;

	public Listeners(Activity context) {
		super();
		this.context = context;
		ApplicationContext=this.context;
	}



	public OnClickListener gps =new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			ImageButton gpsimgbtn=(ImageButton)context.findViewById(R.id.gpsimgbtn);

			if (GPS.isGPSEnabled(context))
			{      	
				GPS.ensureGPSStatus(false,context);
				gpsimgbtn.setBackgroundResource(R.drawable.gpsoff);
			}else
			{
				GPS.ensureGPSStatus(true,context);
				gpsimgbtn.setBackgroundResource(R.drawable.gps);
			}
			Status.postpone();

		}
	};

	public OnClickListener wifi =new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			ImageButton wifiimgbtn=(ImageButton)context.findViewById(R.id.wifibtnimg);
			if (WiFi.isWifiEnabled(context)){
				WiFi.ensureWifiStatus(false, context);
				wifiimgbtn.setBackgroundResource(R.drawable.wifioff);
			}else{
				WiFi.ensureWifiStatus(true, context);
				wifiimgbtn.setBackgroundResource(R.drawable.wifi);
			}
			Status.postpone();
		}
	};

	public OnClickListener flash =new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			ImageButton flashimgbtn=(ImageButton)context.findViewById(R.id.flashimgbtn);
			if(Flashlight.FlashlightisEnable(context))
			{
				Flashlight.Toggle();
				flashimgbtn.setBackgroundResource(Flashlight.isFlashOn?R.drawable.flash:R.drawable.flashoff);
				Status.postpone();
			}
			else
				context.startActivity(new Intent(context,farin.code.air.status.Flash.class));
			
			
		}
	};

	public OnClickListener bt =new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			ImageButton btimgbtn=(ImageButton)context.findViewById(R.id.btimgbtn);
			if(Bluetooth.isBluetoothEnabled())
			{
				Bluetooth.ensureBluetoothStatus(false);
				btimgbtn.setBackgroundResource(R.drawable.btoff);
			}
			else
			{
				Bluetooth.ensureBluetoothStatus(true);
				btimgbtn.setBackgroundResource(R.drawable.bt);
			}
			Status.postpone();
		}
	};

	public void inialbt(ImageButton ib)
	{
		if(Bluetooth.isBluetoothEnabled())
		{
			ib.setBackgroundResource(R.drawable.bt);
		}
		else
		{
			ib.setBackgroundResource(R.drawable.btoff);
		}
	}
	
	
	public void inialgps(ImageButton ib)
	{
		if (GPS.isGPSEnabled(context)){
			ib.setBackgroundResource(R.drawable.gps);
		}else{
			ib.setBackgroundResource(R.drawable.gpsoff);
		}
	}
	
	public void inialwifi(ImageButton ib)
	{
		if (WiFi.isWifiEnabled(context)){
			ib.setBackgroundResource(R.drawable.wifi);
		}else{
			ib.setBackgroundResource(R.drawable.wifioff);
		}
	}
	
	public void inialflash(ImageButton ib)
	{
		if (Flashlight.isFlashOn){
			ib.setBackgroundResource(R.drawable.flash);
		}else{
			ib.setBackgroundResource(R.drawable.flashoff);
		}
	}
}
