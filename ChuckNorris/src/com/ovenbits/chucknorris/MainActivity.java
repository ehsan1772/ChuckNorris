package com.ovenbits.chucknorris;

import notification.NotificationService;
import shake.ShakeListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;


public class MainActivity extends Activity implements ShakeListener.OnShakeListener, JokeFragment.AnimationListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private ShakeListener shaker;
	private JokeFragment jokeFragment;
	private TalkingChuck talkingChuck;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		shaker = new ShakeListener(this);
		shaker.setOnShakeListener(this);
		startNotificationTimer();
		
		if (savedInstanceState == null) {
			addTalkFragment();
			addJokeFragment();
		} else {
			talkingChuck = (TalkingChuck) getFragmentManager().findFragmentByTag(TalkingChuck.TAG);
			jokeFragment = (JokeFragment) getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		}
		
		jokeFragment.setListener(this);
	}
	
	@Override
	protected void onResume() {

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
		jokeFragment = (JokeFragment) getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		if (jokeFragment != null && jokeFragment.getStatus() != JokeFragment.STATUS_LOADING) {
			jokeFragment.refresh();
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
