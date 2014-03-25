package com.ovenbits.chucknorris;

import com.foundation.restful.RestFulDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import data.Joke;
import data.JokeNumber;
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
import android.widget.TextView;

public class JokeFragment extends Fragment {
	public static String TAG =  JokeFragment.class.getSimpleName();
	private JokeActivity jokeActivity;
	private TextView jokeTextView;
	private ProgressBar jokeProgressBar;
	private int slideInDuration;
	private int animationInDuration;
	
	private int jokeCount;
	private Joke joke;
	
	private String jokeCountUrl;
	private String jokeUrl;
	
	private String jokeBaseUrl;
	
	private BroadcastReceiver notificationReciever;
	private boolean slideAnimationIsFinished = false;
	
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
		slideInDuration = getResources().getInteger(R.integer.joke_slie_duration);
		animationInDuration = getResources().getInteger(R.integer.joke_in_duration);
		jokeCountUrl = getResources().getString(R.string.joke_count_url);
		jokeBaseUrl = getResources().getString(R.string.joke_base_url);
		
		initiateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.ovenbits.chucknorris.RESULT");
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(notificationReciever, filter);
		
		if (savedInstanceState == null) {
			fetchData(jokeCountUrl);
		}
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.joke_fragment, container, false);
		jokeTextView = (TextView) view.findViewById(R.id.jokeTextView);
		jokeProgressBar = (ProgressBar) view.findViewById(R.id.jokeProgressBar);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume(); 
		float delta = getResources().getDimension(R.dimen.joke_fragment_width);
		Animator animator = ObjectAnimator.ofFloat(getView(), View.X, delta, 0);
		animator.setDuration(1000);
		animator.addListener(animatorListener);
		slideAnimationIsFinished = false;
		animator.start();
	}
	
	private AnimatorListener animatorListener = new AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {}
		
		@Override
		public void onAnimationRepeat(Animator animation) {}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			slideAnimationIsFinished = true;
			showTheJoke();
		}

		@Override
		public void onAnimationCancel(Animator animation) {}
	};
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onAttach(Activity activity) {
		try {
			jokeActivity = (JokeActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement JokeActivity");
		}
		super.onAttach(activity);
	}
	
	public interface JokeActivity {
		
	}
	
	
	private void showTheJoke() {
		if (slideAnimationIsFinished && joke != null) {
			jokeProgressBar.setVisibility(View.INVISIBLE);
			jokeTextView.setText(joke.getValue().getJoke());
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
						joke = getJoke(refKey);
						showTheJoke();
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
		}
					
		return null;
	}
	
	
	private void fetchData(String url) {
		Intent intent = new Intent(getActivity(), RestFulDataManager.class);
		intent.putExtra(RestFulDataManager.REQUESTED_URL, url);
		getActivity().startService(intent);
	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(notificationReciever);
		super.onDestroy();
	}

}
