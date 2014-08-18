package farin.code.air.option;


import android.content.Context;
import android.net.wifi.WifiManager;

public class WiFi 
{
	public static boolean isWifiEnabled(Context context)
	{
		WifiManager wifi = (WifiManager) 
				context.getSystemService(Context.WIFI_SERVICE);
		return wifi.isWifiEnabled();

	}

	public static void ensureWifiStatus(boolean on,Context context){
		WifiManager wifi = (WifiManager) 
				context.getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(on);
	}
}
