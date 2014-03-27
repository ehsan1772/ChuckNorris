package views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * A textview with a custom animation that can load a string letter by letter
 * uses the UI thread handler to update the textview
 * @author ehsan.barekati
 *
 */
public class AnimatedTextView extends TextView {
	public static final String TAG = AnimatedTextView.class.getSimpleName();
	private Handler mainHandler;
	private TextAnimationListener listener;
	char[] textArray;

	public AnimatedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public AnimatedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public AnimatedTextView(Context context) {
		super(context);
		initialize(context);
	}

	private void initialize(Context context) {
		//getting the UI handler
		mainHandler = new Handler(context.getMainLooper());
	}

	public void setAnimatedText(final String text, final long interval) {
		textArray = new char[text.length()];

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "Starting Text Animation");
				animationStarted();
				for (int i = 0; i < textArray.length; i++) {
					text.getChars(0, i, textArray, 0);
					Runnable forMainThread = setTextRunnable(new String(
							textArray));
					mainHandler.post(forMainThread);
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.d(TAG, "Text Animation Finished");
				animationEnded();
			}

		};

		Thread thread = new Thread(runnable);
		thread.start();
	}

	private void animationEnded() {
		if (listener != null) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					listener.textAnimationEnded();
				}
			};
			mainHandler.post(runnable);
		}
	}

	private void animationStarted() {
		if (listener != null) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					listener.textAnimationStrated();
				}
			};
			mainHandler.post(runnable);
		}
	}

	private Runnable setTextRunnable(final String chars) {
		Runnable result = new Runnable() {
			@Override
			public void run() {
				setText(chars);
			}
		};
		return result;
	}

	public TextAnimationListener getListener() {
		return listener;
	}

	public void setListener(TextAnimationListener listener) {
		this.listener = listener;
	}

	public interface TextAnimationListener {
		public void textAnimationStrated();
		public void textAnimationEnded();
	}

}
