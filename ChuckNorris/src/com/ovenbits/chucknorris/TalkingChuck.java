package com.ovenbits.chucknorris;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TalkingChuck extends Fragment {
	public static String TAG =  TalkingChuck.class.getSimpleName();
	ImageView jokeView;
	AnimationDrawable jokeAnimation;
	
	public static TalkingChuck getInstance() {
		TalkingChuck f = new TalkingChuck();
		
		Bundle args = new Bundle();
		//add parameters here
		f.setArguments(args);
		f.setRetainInstance(true);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.chuck_talking, container, false);
		jokeView = (ImageView) view.findViewById(R.id.talking_image);
		jokeAnimation = (AnimationDrawable) jokeView.getBackground();
		return view;
	}
	
	public void talk() {
		jokeAnimation.start();
	}
	
	public void shutUp() {
		jokeAnimation.stop();
	}
}
