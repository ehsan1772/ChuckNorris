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
	
	
	private int jokeCount;
	private Joke joke;
	
	private String jokeCountUrl;
	private String jokeUrl;
	
	private String jokeBaseUrl;
	
	private BroadcastReceiver notificationReciever;
	private int slideAnimationStatus = 0;
	
	public static JokeFragment getInstance() {
		JokeFragment f = new JokeFragment();
		
		Bundle args = new Bundle();
		//add parameters here
		f.setArguments(args);
		f.setRetainInstance(true);
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

	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			fetchData(jokeCountUrl);
		}
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
		return view;
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume(); 
		float delta = getResources().getDimension(R.dimen.joke_fragment_width);
		Animator animator = ObjectAnimator.ofFloat(getView(), View.X, delta, 0);
		animator.setDuration(slideInDuration);
		animator.addListener(animatorListener);
		slideAnimationStatus = ANIMATION_STARTED;
		animator.start();
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
					int refKey = intent.getExtras().getInt(RestFulDataManager.REF_KEY);
					Log.d(TAG, "Data is available for request id " + refKey);
					if (refKey == jokeCountUrl.hashCode()) {
						jokeCount = getCount(refKey);
						jokeUrl= jokeBaseUrl + getRandomInt(jokeCount);
						fetchData(jokeUrl);
					}
					if (!jokeUrl.isEmpty() && refKey == jokeUrl.hashCode()) {
						if (joke != getJoke(refKey)) {
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
		Log.d(TAG, "fetchData");
		status = STATUS_LOADING;
		Intent intent = new Intent(getActivity(), RestFulDataManager.class);
		intent.putExtra(RestFulDataManager.REQUESTED_URL, url);
		getActivity().startService(intent);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(notificationReciever);
		super.onDestroy();
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
