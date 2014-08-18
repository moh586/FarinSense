package farin.code.air;

import farin.code.air.shapeengin.PersianReshap;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

public class About extends Activity {

	Typeface face;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
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
		settextbox(face,  PersianReshap.reshape(getResources().getString(R.string.corporation)), R.id.corpo);
		settextbox(face,  getResources().getString(R.string.site), R.id.site);
		settextbox(face,  getResources().getString(R.string.email), R.id.email);
		settextbox(face,  "09155070053", R.id.tell);
	}
	
	private TextView settextbox(Typeface typeface,String stringid,int id)
	{
		TextView t=(TextView)findViewById(id);
		t.setTypeface(typeface);
		t.setText(stringid);
		return t;

	}


}
