package com.szugyi.circlemenusample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.szugyi.circlemenu.view.CircleLayout;

import java.util.Arrays;

/**
 * Created by szugyi on 07/02/16.
 */
public class SampleDynamicActivity extends SampleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dynamic_attributes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_speed:
                showSpeedDialog();
                return true;
            case R.id.menu_isRotating:
                showIsRotatingDialog();
                return true;
            case R.id.menu_firstChildPosition:
                showFirstChildPositionDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSpeedDialog() {
        LayoutInflater li = LayoutInflater.from(SampleDynamicActivity.this);
        View dialogView = li.inflate(R.layout.dialog_speed, null);

        int speed = circleLayout.getSpeed() - 1;

        final TextView valueText = (TextView) dialogView.findViewById(R.id.value_textView);
        valueText.setText(getString(R.string.dialog_speed_value, speed));

        final SeekBar speedSeek = (SeekBar) dialogView.findViewById(R.id.speed_seekBar);
        speedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int speed = progress + 1;
                valueText.setText(getString(R.string.dialog_speed_value, speed));
                circleLayout.setSpeed(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        speedSeek.setProgress(speed);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_speed)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    private void showIsRotatingDialog() {
        CheckBox isRotatingCheck = new CheckBox(this);
        isRotatingCheck.setText(R.string.is_rotating);
        isRotatingCheck.setChecked(circleLayout.isRotating());
        isRotatingCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                circleLayout.setRotating(isChecked);
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_is_rotating)
                .setView(isRotatingCheck)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    private void showFirstChildPositionDialog() {
        int selected = Arrays.asList(CircleLayout.FirstChildPosition.values()).indexOf(circleLayout.getFirstChildPosition());
        ArrayAdapter<CircleLayout.FirstChildPosition> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                CircleLayout.FirstChildPosition.values());
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        Spinner firstPosSpinner = new Spinner(this);
        firstPosSpinner.setAdapter(adapter);
        firstPosSpinner.setSelection(selected);
        firstPosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                circleLayout.setFirstChildPosition(CircleLayout.FirstChildPosition.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_first_child_position)
                .setView(firstPosSpinner)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }
}
