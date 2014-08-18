package farin.code.air.option;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.Settings;

public class GPS {
	public static boolean isGPSEnabled(Context context){
		String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		return provider.contains("gps");
	}

	private static  boolean canToggleGPSDirectly(Context context) {
		PackageManager pacman = context.getPackageManager();
		PackageInfo pacInfo = null;

		try {
			pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);
		} catch (NameNotFoundException e) {
			return false; //package not found
		}

		if(pacInfo != null){
			for(ActivityInfo actInfo : pacInfo.receivers){
				//test if recevier is exported. if so, we can toggle GPS.
				if(actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported){
					return true;
				}
			}
		}

		return false; //default
	}

	//this only works in older versions of android (it woks on 2.2)
	private static  void toggleGPS(Context context){
		final Intent poke = new Intent();
		poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		poke.setData(Uri.parse("3")); 
		context.sendBroadcast(poke);
	}

	private static  void showGPSSettingsScreen(Context context){
		//show the settings screen
		Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(myIntent);

		// this doens't work if your app isn't signed as firmware....Settings.Secure.setLocationProviderEnabled(ApplicationContext.getInstance().getContentResolver(), LocationManager.GPS_PROVIDER, true);
	}

	public static void ensureGPSStatus(boolean on,Context context){
		boolean isGPSEnabled = isGPSEnabled(context);
		if((isGPSEnabled && !on) || (!isGPSEnabled && on)){
			if(canToggleGPSDirectly(context)){
				toggleGPS(context);
			}else{
				showGPSSettingsScreen(context);
			}
		}
	}
}
