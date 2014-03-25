package com.ovenbits.chucknorris;

import shake.ShakeListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;


public class MainActivity extends Activity implements ShakeListener.OnShakeListener{
	private static final String TAG = MainActivity.class.getSimpleName();
	private ShakeListener shaker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		shaker = new ShakeListener(this);
		shaker.setOnShakeListener(this);
	}
	
	@Override
	protected void onResume() {
		Fragment existingFragment = getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (existingFragment == null) {
			transaction.replace(R.id.joke_fragment_container, JokeFragment.getInstance(), JokeFragment.TAG);
		} else {
			transaction.replace(R.id.joke_fragment_container, existingFragment, JokeFragment.TAG);
		}
		transaction.commit();
		shaker.resume();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		shaker.pause();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onShake() {
		JokeFragment jokeFragment = (JokeFragment) getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		if (jokeFragment != null && jokeFragment.getStatus() != JokeFragment.STATUS_LOADING) {
			jokeFragment.refresh();
		}
	}



}
