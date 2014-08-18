package farin.code.air.status;


import farin.code.air.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class Flash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);  
		setContentView(R.layout.activity_flash);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.screenBrightness = 1f;
		
		getWindow().setAttributes(params);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if( keyCode == KeyEvent.KEYCODE_BACK ) {
	        //Ask the user if they want to quit
	    	finish();
	        return true;
	    }
	    else {
	        return super.onKeyDown(keyCode, event);
	    }
	}

}
