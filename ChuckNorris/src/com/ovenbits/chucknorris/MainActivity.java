package com.ovenbits.chucknorris;

import notification.NotificationService;
import shake.ShakeListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements ShakeListener.OnShakeListener, JokeFragment.AnimationListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private ShakeListener shaker;
	private JokeFragment jokeFragment;
	private TalkingChuck talkingChuck;
	private ConnectivityManager cManager;
	private View connectivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connectivity = findViewById(R.id.no_connectivity);

		shaker = new ShakeListener(this);
		shaker.setOnShakeListener(this);
		startNotificationTimer();

		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);



		if (savedInstanceState == null) {
			loadTheApp();
		} else {
			talkingChuck = (TalkingChuck) getFragmentManager().findFragmentByTag(TalkingChuck.TAG);
			jokeFragment = (JokeFragment) getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		}

		jokeFragment.setListener(this);
	}
	
	private void loadTheApp() {
		NetworkInfo activeNetwork = cManager.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		
		if (isConnected) {
			connectivity.setVisibility(View.INVISIBLE);
			addTalkFragment();
			addJokeFragment();
		} else {
			showNoConnectivityScreen();
		}
		
	}

	private void showNoConnectivityScreen() {
		connectivity.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		
		NetworkInfo activeNetwork = cManager.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		
		if (isConnected) {
			connectivity.setVisibility(View.INVISIBLE);
		} else {
			connectivity.setVisibility(View.VISIBLE);
		}

		shaker.resume();
		super.onResume();
	}

	private void addTalkFragment() {
		talkingChuck = (TalkingChuck) getFragmentManager().findFragmentByTag(TalkingChuck.TAG);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (talkingChuck == null) {
			talkingChuck = TalkingChuck.getInstance();
		}

		transaction.replace(R.id.talk_fragment_container, talkingChuck, TalkingChuck.TAG);
		transaction.commit();
	}

	private void addJokeFragment() {
		jokeFragment = (JokeFragment) getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (jokeFragment == null) {
			jokeFragment = JokeFragment.getInstance();
		}

		transaction.replace(R.id.joke_fragment_container, jokeFragment, JokeFragment.TAG);
		transaction.commit();
	}

	@Override
	protected void onPause() {
		shaker.pause();
		super.onPause();
	}

	@Override
	public void onShake() {
		NetworkInfo activeNetwork = cManager.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		
		if (isConnected) {
			connectivity.setVisibility(View.INVISIBLE);
			jokeFragment = (JokeFragment) getFragmentManager().findFragmentByTag(JokeFragment.TAG);
			if (jokeFragment != null && jokeFragment.getStatus() != JokeFragment.STATUS_LOADING) {
				jokeFragment.refresh();
			}
		} else {
			showNoConnectivityScreen();
		}
	}

	private void startNotificationTimer() {
		NotificationService.setTimer(this);
	}

	@Override
	public void textAnimationStrated() {
		talkingChuck.talk();

	}

	@Override
	public void textAnimationEnded() {
		talkingChuck.shutUp();

	}

}
