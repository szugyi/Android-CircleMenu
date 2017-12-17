package com.szugyi.circlemenusample;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.szugyi.circlemenu.view.TextCircleWheel;
import com.szugyi.circlemenu.view.WheelTextItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KhaledLela on 9/30/17.
 */

public class WheelActivity extends AppCompatActivity{
    private TextCircleWheel mainCategoryWheel;
    private TextCircleWheel subCategoryWheel;
    private final int ITEM_LIMIT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        final View wheelView = findViewById(R.id.wheel_layout);
        mainCategoryWheel = (TextCircleWheel) findViewById(R.id.main_category_wheel);
        subCategoryWheel = (TextCircleWheel) findViewById(R.id.sub_category_wheel);

        wheelView.post(new Runnable() {
            @Override
            public void run() {
                final int offset = getResources().getDimensionPixelSize(R.dimen.spin_pointer_width)/2;
                int width = mainCategoryWheel.getWidth() - offset;

                int height = mainCategoryWheel.getHeight();
                int mSize = Math.min(width,height);

                ((FrameLayout.LayoutParams)mainCategoryWheel.getLayoutParams()).width = mSize;
                ((FrameLayout.LayoutParams)mainCategoryWheel.getLayoutParams()).height = mSize;

                ((FrameLayout.LayoutParams)subCategoryWheel.getLayoutParams()).width = mSize;
                ((FrameLayout.LayoutParams)subCategoryWheel.getLayoutParams()).height = mSize;
            }
        });
        initWheel();
        loadMainCategory();
        loadSubCategoryByMainCategory(mainCategoryWheel.getSelectedWheelItem());
    }

    private void initWheel() {
        final ImageView sprLeft = (ImageView) findViewById(R.id.spr_left);
        final ImageView sprRight = (ImageView) findViewById(R.id.spr_right);
        subCategoryWheel.setOnRotationChangedListener(new TextCircleWheel.OnRotationChangedListener() {
            @Override
            public void onRotationFinished(View view) {
                sprRight.setRotation(30);
            }

            private ObjectAnimator animator;
            @Override
            public void onStopAnimation() {
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                    animator = null;
                }
            }

            @Override
            public void onRotate(float angle) {
                final float rotation = sprRight.getRotation() - angle;
                if (rotation > 83 || rotation <-23)
                    return;
                sprRight.setRotation(rotation);
            }

            @Override
            public void onAnimationStarted(float endDegree, long duration) {
                if (animator != null && animator.isRunning() || Math.abs(sprRight.getRotation() -30) < 1) {
                    return;
                }
                animator = ObjectAnimator.ofFloat(sprRight, "rotation", sprRight.getRotation(),30);
                animator.setDuration(duration);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            }
        });

        // Load more from server : sort [DEC | ASubCategory]
        subCategoryWheel.setOnLoadMoreListener(new TextCircleWheel.OnLoadMoreListener() {
            @Override
            public void loadMore(int direction) {
                loadSubCategoryByMainCategory(mainCategoryWheel.getSelectedWheelItem(),direction);
                Log.d("WheelMenu","Load more Page: " + mainCategoryWheel.getItemCount()/ITEM_LIMIT);
            }
        });

        mainCategoryWheel.setOnRotationChangedListener(new TextCircleWheel.OnRotationChangedListener() {
            @Override
            public void onRotationFinished(View view) {
                sprLeft.setRotation(-30);
                loadSubCategoryByMainCategory(mainCategoryWheel.getSelectedWheelItem());
            }

            private ObjectAnimator animator;

            @Override
            public void onStopAnimation() {
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                    animator = null;
                }
            }

            @Override
            public void onRotate(float angle) {
                final float rotation = sprLeft.getRotation() - angle;
                if (rotation > 23 || rotation < -83)
                    return;
                sprLeft.setRotation(rotation);
            }

            @Override
            public void onAnimationStarted(float endDegree, long duration) {
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

    private void loadSubCategoryByMainCategory(WheelTextItem mainCategory) {
        loadSubCategoryByMainCategory(mainCategory, 0, ITEM_LIMIT);
    }

    private void loadSubCategoryByMainCategory(WheelTextItem mainCategory, int direction) {
        final int itemCount = subCategoryWheel.getItemCount();
        final int start = direction == TextCircleWheel.OnLoadMoreListener.DIRECTION_DESC ? itemCount : itemCount + ITEM_LIMIT;
        final int end = direction == TextCircleWheel.OnLoadMoreListener.DIRECTION_DESC ? itemCount + ITEM_LIMIT : itemCount;
        loadSubCategoryByMainCategory(mainCategory, start, end);
    }

    private void loadSubCategoryByMainCategory(WheelTextItem mainCategory, int start, int end) {
        List<MenuItem> items = new ArrayList<>(ITEM_LIMIT);
        for (int i=start ; i<end;i++){
            final MenuItem item = new MenuItem();
            item.setId(i);
            item.setName(mainCategory.getName() +"-->" +  i);
            items.add(item);
        }
        if (start == 0 )
            subCategoryWheel.setItems(items);
        else
            subCategoryWheel.addItems(items);
    }

    private void loadMainCategory() {
        List<MenuItem> items = new ArrayList<>(ITEM_LIMIT);
        for (int i=0 ; i<ITEM_LIMIT;i++){
            final MenuItem item = new MenuItem();
            item.setId(i);
            item.setName("WheelTextItem " + i);
            items.add(item);
        }
        mainCategoryWheel.setItems(items);
    }
}
