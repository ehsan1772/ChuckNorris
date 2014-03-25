package com.ovenbits.chucknorris;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ProgressBar;
import android.widget.TextView;

public class JokeFragment extends Fragment {
	public static String TAG =  JokeFragment.class.getSimpleName();
	private JokeActivity jokeActivity;
	private TextView jokeTextView;
	private ProgressBar jokeProgressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.joke_fragment, container, true);
		jokeTextView = (TextView) view.findViewById(R.id.jokeTextView);
		jokeProgressBar = (ProgressBar) view.findViewById(R.id.jokeProgressBar);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume(); 
		getView().getLeft();
		getView().getTranslationX();
		getView().getX();
		
	}
	
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

}
