package com.szugyi.circlemenusample;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ListView) findViewById(android.R.id.list)).setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, SampleActivity.class);
        switch (position) {
            case 0:
                intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample);
                break;
            case 1:
                intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample_no_rotation);
                break;
            case 2:
                intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample_attributes);
                break;
            case 3:
                intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample_any_items);
                break;
            case 4:
                intent.putExtra(SampleActivity.ARG_LAYOUT, R.layout.sample_dynamic_children);
                break;
        }

        startActivity(intent);
    }
}
