package com.bizwave.bamstory.view;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {
	private final String FONT = "malgun.ttf";
	
	public CustomTextView(Context context,AttributeSet attrs,int defStyle){
		super(context,attrs,defStyle);
		setFont(context);
	}
	
	public CustomTextView(Context context){
		super(context);
		setFont(context);
	}
	
	public CustomTextView(Context context,AttributeSet attrs){
		super(context,attrs);
		setFont(context);
	}
	
	private void setFont(Context context){
		Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/" + FONT);
		this.setTypeface(face);
	}


	
	
	
}
