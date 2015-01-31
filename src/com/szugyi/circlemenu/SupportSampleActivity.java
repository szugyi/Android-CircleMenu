package com.szugyi.circlemenu;

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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.szugyi.circlemenu.support.view.CircleLayout;
import com.szugyi.circlemenu.support.view.CircleLayout.OnCenterClickListener;
import com.szugyi.circlemenu.support.view.CircleLayout.OnItemClickListener;
import com.szugyi.circlemenu.support.view.CircleLayout.OnItemSelectedListener;
import com.szugyi.circlemenu.support.view.CircleLayout.OnRotationFinishedListener;
import com.szugyi.circlemenu.view.CircleImageView;

public class SupportSampleActivity extends Activity implements OnItemSelectedListener,
		OnItemClickListener, OnRotationFinishedListener, OnCenterClickListener {
	TextView selectedTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support_sample);

		CircleLayout circleMenu = (CircleLayout) findViewById(R.id.main_circle_layout);
		circleMenu.setOnItemSelectedListener(this);
		circleMenu.setOnItemClickListener(this);
		circleMenu.setOnRotationFinishedListener(this);
		circleMenu.setOnCenterClickListener(this);

		selectedTextView = (TextView) findViewById(R.id.main_selected_textView);
		selectedTextView.setText(((CircleImageView) circleMenu
				.getSelectedItem()).getName());
	}

	@Override
	public void onItemSelected(View view, String name) {
		selectedTextView.setText(name);

		switch (view.getId()) {
			case R.id.main_facebook_image:
				// Handle facebook selection
				break;
			case R.id.main_google_image:
				// Handle google selection
				break;
			case R.id.main_linkedin_image:
				// Handle linkedin selection
				break;
			case R.id.main_myspace_image:
				// Handle myspace selection
				break;
			case R.id.main_twitter_image:
				// Handle twitter selection
				break;
			case R.id.main_wordpress_image:
				// Handle wordpress selection
				break;
		}
	}

	@Override
	public void onItemClick(View view, String name) {
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.start_app) + " " + name,
				Toast.LENGTH_SHORT).show();

		switch (view.getId()) {
			case R.id.main_facebook_image:
				// Handle facebook click
				break;
			case R.id.main_google_image:
				// Handle google click
				break;
			case R.id.main_linkedin_image:
				// Handle linkedin click
				break;
			case R.id.main_myspace_image:
				// Handle myspace click
				break;
			case R.id.main_twitter_image:
				// Handle twitter click
				break;
			case R.id.main_wordpress_image:
				// Handle wordpress click
				break;
		}
	}

	@Override
	public void onCenterClick() {
		Toast.makeText(getApplicationContext(), R.string.center_click,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRotationFinished(View view, String name) {
		Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2,
				view.getHeight() / 2);
		animation.setDuration(250);
		view.startAnimation(animation);
	}

}
