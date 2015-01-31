package com.szugyi.circlemenu;

import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent intent = null;
		switch (position) {
			case 0:
				intent = new Intent(this, SampleActivity.class);
				break;
			case 1:
				intent = new Intent(this, SupportSampleActivity.class);
				break;
		}

		startActivity(intent);
	}
}
