package farin.code.air.option;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

public class Flashlight {


	private static Camera camera;
	public static  boolean isFlashOn;
	static Parameters params;
	public static boolean FlashlightisEnable(Context context)
	{
		return context.getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}

	// getting camera parameters
	private static void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
			}
		}
	}

	/*
	 * Turning On flash
	 */
	private static void turnOnFlash() {
		getCamera();
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;
		}
	}

	/*
	 * Turning Off flash
	 */
	private static void turnOffFlash(){
		getCamera();
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;
		}
	}
	public static void Toggle()
	{
		if(isFlashOn)
			turnOffFlash();
		else
			turnOnFlash();
	}


}
