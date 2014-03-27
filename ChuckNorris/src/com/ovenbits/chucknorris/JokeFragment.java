package com.ovenbits.chucknorris;

import views.AnimatedTextView;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.foundation.restful.RestFulDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import data.Joke;
import data.JokeNumber;

public class JokeFragment extends Fragment implements AnimatedTextView.TextAnimationListener{
	public static String TAG =  JokeFragment.class.getSimpleName();
	private AnimatedTextView jokeTextView;
	private ProgressBar jokeProgressBar;
	private int slideInDuration;
	private int animationInDuration;
	private AnimationListener listener;
	
	public final static int STATUS_LOADING = 1;
	public final static int STATUS_RESTING = 0;
	
	private final static int ANIMATION_NOT_STARTED = 0;
	private final static int ANIMATION_STARTED = 1;
	private final static int ANIMATION_ENDED = 2;
	
	private int status = STATUS_RESTING;
	private int parentWidth;
	
	private int jokeCount;
	private Joke joke;
	
	private String jokeCountUrl;
	private String jokeUrl;
	private String jokeBaseUrl;
	
	private BroadcastReceiver notificationReciever;
	private int slideAnimationStatus = 0;
	private Bundle savedInstance;
	
	private int refKey;
	
	public static JokeFragment getInstance() {
		JokeFragment f = new JokeFragment();
		
		Bundle args = new Bundle();
		//add parameters here
		f.setArguments(args);
	//	f.setRetainInstance(true);
		return f;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		slideInDuration = getResources().getInteger(R.integer.joke_slie_duration);
		animationInDuration = getResources().getInteger(R.integer.joke_in_duration);
		jokeCountUrl = getResources().getString(R.string.joke_count_url);
		jokeBaseUrl = getResources().getString(R.string.joke_base_url);
		
		initiateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.ovenbits.chucknorris.RESULT");
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(notificationReciever, filter);
//		if (savedInstanceState == null || savedInstanceState.getInt("KEY") == 0) {
//			fetchData(jokeCountUrl);
//		} else {
			savedInstance = savedInstanceState;
//		}
	}
	
	public void refresh() {
		Log.d(TAG, "refresh");
		jokeProgressBar.setVisibility(View.VISIBLE);
		jokeTextView.setText("");
		joke = null;
		fetchData(jokeCountUrl);
	}
	
	public int getStatus() {
		return status;
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.joke_fragment, container, false);
		jokeTextView = (AnimatedTextView) view.findViewById(R.id.jokeTextView);
		jokeTextView.setListener(this);
		jokeProgressBar = (ProgressBar) view.findViewById(R.id.jokeProgressBar);
		parentWidth = container.getWidth();
		if (savedInstance != null) {
			slideAnimationStatus = ANIMATION_ENDED;
			refKey = savedInstanceState.getInt("KEY");
			joke = getJoke(refKey);
			jokeTextView.setText(joke.getValue().getJoke());
			jokeTextView.setVisibility(View.VISIBLE);
			jokeProgressBar.setVisibility(View.INVISIBLE);
		}
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("KEY", refKey);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume(); 
		
		initiateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.ovenbits.chucknorris.RESULT");
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(notificationReciever, filter);
		
		if (jokeTextView.getText().length() != 0) {
			return;
		}
		
		if (savedInstance== null || savedInstance.getInt("KEY") == 0) {
			fetchData(jokeCountUrl);
		} 
		
		if (slideAnimationStatus == ANIMATION_NOT_STARTED) {
			slideAnimationStatus = ANIMATION_STARTED;
			float delta = getResources().getDimension(R.dimen.joke_fragment_width);
			Animator animator = ObjectAnimator.ofFloat(getView(), View.X, delta, 0);
			animator.setDuration(slideInDuration);
			animator.addListener(animatorListener);
			animator.start();
		}
	}
	
	private AnimatorListener animatorListener = new AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {}
		
		@Override
		public void onAnimationRepeat(Animator animation) {}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			slideAnimationStatus = ANIMATION_ENDED;
			showTheJoke();
		}

		@Override
		public void onAnimationCancel(Animator animation) {}
	};
	
	@Override
	public void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}
	
	
	private void showTheJoke() {
		if (slideAnimationStatus == ANIMATION_ENDED && joke != null) {
			jokeProgressBar.setVisibility(View.INVISIBLE);
			jokeTextView.setAnimatedText(joke.getValue().getJoke(), 50);
		}	
		
	}


	private void initiateReceiver() {
		notificationReciever = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.hasExtra(RestFulDataManager.REF_KEY)) {
					int refKeyIntent = intent.getExtras().getInt(RestFulDataManager.REF_KEY);
					Log.d(TAG, "Data is available for request id " + refKeyIntent);
					if (refKeyIntent == jokeCountUrl.hashCode()) {
						jokeCount = getCount(refKeyIntent);
						jokeUrl= jokeBaseUrl + getRandomInt(jokeCount);
						fetchData(jokeUrl);
					}
					if (jokeUrl != null && !jokeUrl.isEmpty() && refKeyIntent == jokeUrl.hashCode()) {
						if (joke != getJoke(refKeyIntent)) {
							refKey = refKeyIntent;
							joke = getJoke(refKey);
							showTheJoke();
						}
					}
					
				}
				
			}
		};
	}
	
	private int getRandomInt(int max) {
		return (int)(Math.random() * (max + 1));
	}
	
	private int getCount(int refKey){
		String result = RestFulDataManager.getInstance().getData(refKey, String.class);
		Log.d(TAG, "Result is: " + result);
		JokeNumber number = null;
		Gson gson = new GsonBuilder().create();
		try {
			number = gson.fromJson(result, JokeNumber.class);
			int count = number.getValue();
			return count;
		} catch(JsonSyntaxException e) {
			Log.e(TAG, "Wrong syntax : " + result);
		}
		return 0;
	}
	
	private Joke getJoke(int refKey){
		String result = RestFulDataManager.getInstance().getData(refKey, String.class);
		Log.d(TAG, "Result is: " + result);
		
		Gson gson = new GsonBuilder().create();
		try {
			Joke joke = gson.fromJson(result, Joke.class);
			return joke;
		} catch(JsonSyntaxException e) {
			Log.e(TAG, "Wrong syntax : " + result);
			refresh();
		}
					
		return null;
	}
	
	
	private void fetchData(String url) {
		if (getActivity() != null) {
			Log.d(TAG, "fetchData");
			status = STATUS_LOADING;
			Intent intent = new Intent(getActivity(), RestFulDataManager.class);
			intent.putExtra(RestFulDataManager.REQUESTED_URL, url);
			getActivity().startService(intent);
		}
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(notificationReciever);
		super.onPause();
	}
	
	public AnimationListener getListener() {
		return listener;
	}


	public void setListener(AnimationListener listener) {
		this.listener = listener;
	}

	public interface AnimationListener {
		public void textAnimationStrated();
		public void textAnimationEnded();
	}

	@Override
	public void textAnimationStrated() {
		if (listener != null) {
			listener.textAnimationStrated();
		}
		
	}


	@Override
	public void textAnimationEnded() {
		status = STATUS_RESTING;
		if (listener != null) {
			listener.textAnimationEnded();
		}
		
	}

}
