package notification;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ovenbits.chucknorris.MainActivity;
import com.ovenbits.chucknorris.R;

/**
 * This class takes care of the notification through AlarmManager and a service that gets called by the alarm manager
 * @author ehsan.barekati
 *
 */
public class NotificationService extends Service {
	public final static String TAG = NotificationService.class.getSimpleName();

	/**
	 * Set's up a timer through AlarmManager to publish a notification
	 * @param context
	 * @param hour
	 * @param minute
	 */
	@SuppressLint("NewApi")
	public static void setTimer(Context context, int hour, int minute) {
		Intent intent = new Intent(context, NotificationService.class);
		intent.setClass(context, NotificationService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
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

	/**
	 * adds a notification
	 */
	private void showNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.chuck_launcher);
		builder.setContentTitle(getResources().getString(R.string.notification_title));
		builder.setContentText(getResources().getString(R.string.notification_text));
		builder.setAutoCancel(true);

		Intent result = new Intent(this, MainActivity.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, result, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(resultPendingIntent);

		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(01, builder.build());

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
