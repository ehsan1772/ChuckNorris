package views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class AnimatedTextView extends TextView {
	private Handler mainHandler;
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
		mainHandler = new Handler(context.getMainLooper());
		
	}
	
	public void setAnimatedText(final String text, final long interval) {
		textArray = new char[text.length()];
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				for(int i = 0 ; i < textArray.length ; i++) {
					text.getChars(0, i, textArray, 0);
					Runnable forMainThread = setTextRunnable(new String(textArray));
					mainHandler.post(forMainThread);
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		
		Thread thread = new Thread(runnable);
		thread.start();
		
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
	


}
