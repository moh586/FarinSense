package farin.code.air;

import farin.code.air.db.DbAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
	  if(new DbAdapter(context).Select(DbAdapter.ON)==1)
	  {
		  context.startService(new Intent(context, SensorService.class));
	  }
  }
}
