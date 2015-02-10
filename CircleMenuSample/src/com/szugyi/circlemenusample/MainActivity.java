package com.szugyi.circlemenusample;

import com.szugyi.circlemenusample.R;

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

		Intent intent = new Intent(this, SampleActivity.class);
		switch (position) {
			case 0:
				intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample);
				break;
			case 1:
				intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample_fast);
				break;
			case 2:
				intent.putExtra(SampleActivity.ARG_LAYOUT,
						R.layout.sample_no_rotation);
				break;
			case 3:
				intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample_west);
				break;
			case 4:
				intent.putExtra(SampleActivity.ARG_LAYOUT,
						R.layout.sample_with_background);
				break;
			case 5:
				intent.putExtra(SampleActivity.ARG_LAYOUT,
						R.layout.sample_7_items);
				break;
			case 6:
				intent.putExtra(SampleActivity.ARG_LAYOUT,
						R.layout.sample_8_items);
				break;
		}

		startActivity(intent);
	}
}
