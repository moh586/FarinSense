package farin.code.air;

import farin.code.air.shapeengin.PersianReshap;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

public class Help extends Activity {

	Typeface face;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		initail();
	}

	private void initail() {
		face = Typeface.createFromAsset(this.getAssets(),"font/irsans.ttf");
		loadTextViews();
	}

	
	private void loadTextViews() 
	{
		//offlbl=settextbox(face,   R.string.off, R.id.off);
		//onlbl =settextbox( face,    R.string.on,  R.id.on);
		settextbox(face,  R.string.help_onoff, R.id.textView1);
		settextbox(face,  R.string.help_setting, R.id.textView2);
		settextbox(face,  R.string.help_lang, R.id.textView3);
		settextbox(face,  R.string.help_detect, R.id.textView4);
		settextbox(face,  R.string.help_call, R.id.textView5);
		settextbox(face,  R.string.help_quick, R.id.textView6);
		settextbox(face,  R.string.help_vibrate, R.id.textView7);
		settextbox(face,  R.string.help_flat, R.id.textView8);
		settextbox(face,  R.string.help_disable, R.id.textView9);
		settextbox(face,  R.string.help_widget, R.id.textView10);
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

}
