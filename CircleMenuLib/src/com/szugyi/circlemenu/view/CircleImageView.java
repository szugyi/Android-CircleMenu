package com.szugyi.circlemenu.view;

/*
 * Copyright 2013 Csaba Szugyiczki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.szugyi.circlemenu.R;

/**
 * 
 * @author Szugyi
 * Custom ImageView for the CircleLayout class.
 * Makes it possible for the image to have a name.
 */
public class CircleImageView extends ImageView {
	// The name of the view
	private String name;
	
	/**
	 * Return the name of the view.
	 * @return Returns the name of the view.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Set the name of the view.
	 * @param name The name to be set for the view.
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * @param context
	 */
	public CircleImageView(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (attrs != null) {
			TypedArray array = getContext().obtainStyledAttributes(attrs,
					R.styleable.CircleImageView);
			
			this.name = array.getString(R.styleable.CircleImageView_name);
		}
	}

}
