package farin.code.air.option;

import android.bluetooth.BluetoothAdapter;

public class Bluetooth {
	
	public static boolean isBluetoothEnabled()
	{
		BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
		if(btadapter==null)
			return false;
		return btadapter.isEnabled();
	}
	
	public static void ensureBluetoothStatus(boolean on){
		BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
		if(on)
			btadapter.enable();
		else
			btadapter.disable();
	}

}
