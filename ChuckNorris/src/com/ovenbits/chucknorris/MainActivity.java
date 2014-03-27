package com.ovenbits.chucknorris;

import notification.NotificationService;
import shake.ShakeListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * main and only activity of the app hosting two fragments:
 * TalkingChuck
 * JokeFragment
 * 
 * @author ehsan.barekati
 *
 */
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
		Log.d(TAG, "onCreated");
		setContentView(R.layout.activity_main);
		connectivity = findViewById(R.id.no_connectivity);

		shaker = new ShakeListener(this);
		shaker.setOnShakeListener(this);
		startNotificationTimer(getResources().getInteger(R.integer.notification_hour), getResources().getInteger(R.integer.notification_minute));

		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (savedInstanceState == null) {
			loadTheApp();
		} else {
			talkingChuck = (TalkingChuck) getFragmentManager().findFragmentByTag(TalkingChuck.TAG);
			jokeFragment = (JokeFragment) getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		}

		jokeFragment.setListener(this);
	}
	
	/**
	 * loads the right content based on the connectivity state
	 */
	private void loadTheApp() {
		Log.d(TAG, "showNoConnectivityScreen()");
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
		Log.d(TAG, "showNoConnectivityScreen()");
		connectivity.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
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
		Log.d(TAG, "addTalkFragment()");
		talkingChuck = (TalkingChuck) getFragmentManager().findFragmentByTag(TalkingChuck.TAG);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (talkingChuck == null) {
			talkingChuck = TalkingChuck.getInstance();
		}

		transaction.replace(R.id.talk_fragment_container, talkingChuck, TalkingChuck.TAG);
		transaction.commit();
	}

	private void addJokeFragment() {
		Log.d(TAG, "addJokeFragment()");
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
		Log.d(TAG, "onPause()");
		shaker.pause();
		super.onPause();
	}

	@Override
	public void onShake() {
		Log.d(TAG, "onShake()");
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

	private void startNotificationTimer(int hour, int minute) {
		Log.d(TAG, "startNotificationTimer()");
		NotificationService.setTimer(this, hour, minute);
	}

	@Override
	public void textAnimationStrated() {
		Log.d(TAG, "textAnimationStrated()");
		talkingChuck.talk();

	}

	@Override
	public void textAnimationEnded() {
		Log.d(TAG, "textAnimationEnded()");
		talkingChuck.shutUp();
	}

}
