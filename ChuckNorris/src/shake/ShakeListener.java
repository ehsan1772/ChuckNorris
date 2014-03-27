package shake;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * A class to detect shake events, the algorithms were collected from the
 * Internet!
 * 
 * @author ehsan.barekati
 * 
 */
public class ShakeListener implements SensorEventListener {
	private static final String TAG = ShakeListener.class.getSimpleName();
	private static final int FORCE_THRESHOLD = 350;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;

	private final SensorManager mSensorManager;
	private final Sensor mAccelerometer;

	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime;
	private OnShakeListener mShakeListener;
	private WeakReference<Context> mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;

	public interface OnShakeListener {
		public void onShake();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		Log.d(TAG, "setOnShakeListener(OnShakeListener listener)");
		mShakeListener = listener;
	}

	public ShakeListener(Context context) {
		Log.d(TAG, "ShakeListener(Context context)");
		mContext = new WeakReference<Context>(context);
		mSensorManager = (SensorManager) mContext.get().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	public void resume() {
		Log.d(TAG, "resume()");
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long now = System.currentTimeMillis();

		if ((now - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}

		if ((now - mLastTime) > TIME_THRESHOLD) {
			float[] values = event.values;
			long diff = now - mLastTime;
			float speed = Math.abs(values[0] + values[1] + values[2] - mLastX - mLastY - mLastZ) / diff * 10000;
			if (speed > FORCE_THRESHOLD) {
				if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
					mLastShake = now;
					mShakeCount = 0;
					if (mShakeListener != null) {
						mShakeListener.onShake();
					}
				}
				mLastForce = now;
			}
			mLastTime = now;
			mLastX = values[0];
			mLastY = values[1];
			mLastZ = values[2];
		}

	}

	public void pause() {
		Log.d(TAG, "pause()");
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
