package notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class NotificationService extends Service {
	private final static long day = 24 * 60 * 60 * 1000;
	private final static long test = 2 * 1000;
	public final static String TAG = NotificationService.class.getSimpleName();
	
	@SuppressLint("NewApi")
	public static void setTimer(Context context) {
		Intent intent = new Intent(context, NotificationService.class);
		intent.setClass(context, NotificationService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		if (cal.getTimeInMillis() < System.currentTimeMillis()) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "SERVICE STARTED");
		showNotification();
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	private void showNotification() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
