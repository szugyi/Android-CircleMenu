package com.szugyi.circlemenusample;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.szugyi.circlemenu.view.WheelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KhaledLela on 9/30/17.
 */

public class WheelActivity extends AppCompatActivity{
    private WheelLayout mcWheel;
    private WheelLayout scWheel;
    private final int ITEM_LIMIT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        final View wheelView = findViewById(R.id.wheel_layout);
        mcWheel = (WheelLayout) findViewById(R.id.mc_wheel);
        scWheel = (WheelLayout) findViewById(R.id.sc_wheel);

        wheelView.post(new Runnable() {
            @Override
            public void run() {
                final int offset = getResources().getDimensionPixelSize(R.dimen.spin_pointer_width)/2;
                int width = mcWheel.getWidth() - offset;

                int height = mcWheel.getHeight();
                int mSize = Math.min(width,height);

                ((FrameLayout.LayoutParams)mcWheel.getLayoutParams()).width = mSize;
                ((FrameLayout.LayoutParams)mcWheel.getLayoutParams()).height = mSize;

                ((FrameLayout.LayoutParams)scWheel.getLayoutParams()).width = mSize;
                ((FrameLayout.LayoutParams)scWheel.getLayoutParams()).height = mSize;
            }
        });
        initWheel();
        loadMC();
        loadSCByMC(mcWheel.getSelectedWheelItem());
    }

    private void initWheel() {
        mcWheel.setBgImageView((ImageView) findViewById(R.id.mc_bg));
        scWheel.setBgImageView((ImageView) findViewById(R.id.sc_bg));
        final ImageView sprLeft = (ImageView) findViewById(R.id.spr_left);
        final ImageView sprRight = (ImageView) findViewById(R.id.spr_right);
        scWheel.setWheelCallBack(new WheelLayout.WheelCallBack() {
            private ObjectAnimator animator;
            @Override
            public void onStopAnimation() {
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                    animator = null;
                }
            }

            @Override
            public void loadMore(boolean isNext) {
                // Load more from server : sort [DEC | ASC]
                loadSCByMC(mcWheel.getSelectedWheelItem(),isNext);
                Log.d("WheelMenu","Load more Page: " + mcWheel.getItemCount()/ITEM_LIMIT);
            }

            @Override
            public void onRotate(float angle) {
                final float rotation = sprRight.getRotation() - angle;
                if (rotation > 83 || rotation <-23)
                    return;
                sprRight.setRotation(rotation);
            }

            @Override
            public void onRotationFinished(View view) {
                sprRight.setRotation(30);
            }

            @Override
            public void onSettleRotation(float endDegree, long duration) {
                if (animator != null && animator.isRunning() || Math.abs(sprRight.getRotation() -30) < 1) {
                    return;
                }
                animator = ObjectAnimator.ofFloat(sprRight, "rotation", sprRight.getRotation(),30);
                animator.setDuration(duration);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            }
        });

        mcWheel.setWheelCallBack(new WheelLayout.WheelCallBack() {
            private ObjectAnimator animator;

            @Override
            public void onStopAnimation() {
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                    animator = null;
                }
            }

            @Override
            public void loadMore(boolean isNext) {

            }

            @Override
            public void onRotate(float angle) {
                final float rotation = sprLeft.getRotation() - angle;
                if (rotation > 23 || rotation < -83)
                    return;
                sprLeft.setRotation(rotation);
            }

            @Override
            public void onRotationFinished(View view) {
                sprLeft.setRotation(-30);
                loadSCByMC(mcWheel.getSelectedWheelItem());
            }

            @Override
            public void onSettleRotation(float endDegree, long duration) {
                if (animator != null && animator.isRunning() || Math.abs(sprLeft.getRotation() +30) < 1) {
                    return;
                }
                animator = ObjectAnimator.ofFloat(sprLeft, "rotation", sprLeft.getRotation(),-30);
                animator.setDuration(duration);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            }
        });
    }

    private void loadSCByMC(WheelLayout.WheelItem mc) {
        loadSCByMC(mc, 0, ITEM_LIMIT);
    }

    private void loadSCByMC(WheelLayout.WheelItem mc,boolean isNext) {
        final int itemCount = scWheel.getItemCount();
        final int start = isNext ? itemCount : itemCount + ITEM_LIMIT;
        final int end = isNext ? itemCount + ITEM_LIMIT : itemCount;
        loadSCByMC(mc, start, end);
    }

    private void loadSCByMC(WheelLayout.WheelItem mc, int start, int end) {
        List<MenuItem> items = new ArrayList<>(ITEM_LIMIT);
        for (int i=start ; i<end;i++){
            final MenuItem item = new MenuItem();
            item.setId(i);
            item.setName(mc.getName() +"-->" +  i);
            items.add(item);
        }
        if (start == 0 )
            scWheel.setItems(items,true);
        else
            scWheel.addItems(items);
    }

    private void loadMC() {
        List<MenuItem> items = new ArrayList<>(ITEM_LIMIT);
        for (int i=0 ; i<ITEM_LIMIT;i++){
            final MenuItem item = new MenuItem();
            item.setId(i);
            item.setName("WheelItem " + i);
            items.add(item);
        }
        mcWheel.setItems(items,true);
    }
}
