/*
 * Copyright eng.khaled.lela@gmail.com KhaledLela on 9/30/17.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.szugyi.circlemenu.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.szugyi.circlemenu.R;

import java.util.ArrayList;
import java.util.List;

public class TextCircleWheel extends CircleLayout implements CircleLayout.OnRotationFinishedListener,CircleLayout.OnItemSelectedListener, CircleLayout.OnChildrenAngleChanged {
    private final int DIRECTION_CW = 1;  // clockwise
    private final int DIRECTION_CCW = -1; // anticlockwise
    private List<WheelTextItem> wheelTextItems;
    private ObjectAnimator animator;
    private int itemCount;
    private int currentItem;
    private int selectedChild;
    protected OnRotationChangedListener onRotationChangedListener = null;
    protected OnLoadMoreListener onLoadMoreListener = null;
    protected OnItemSelectedListener onItemSelectedListener = null;
    private Bitmap imageScaled;
    private Matrix matrix;

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setOnRotationChangedListener(OnRotationChangedListener onRotationChangedListener) {
        this.onRotationChangedListener = onRotationChangedListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public TextCircleWheel(Context context) {
        super(context);
    }

    public TextCircleWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextCircleWheel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(TypedArray a) {
        super.init(a);
        wheelTextItems = new ArrayList<>();
        matrix = new Matrix();
        setOnRotationFinishedListener(this);
        setOnItemSelectedListener(this);
        setOnChildrenAngleChanged(this);

        // Set background
        final int bgRes = a.getResourceId(R.styleable.CircleLayout_wheel_bg,0);
        if (bgRes !=0)
            initBackground(bgRes);
    }

    private void initBackground(int bgRes) {
        final Bitmap imageOriginal = BitmapFactory.decodeResource(getResources(), bgRes);
        final int size = Math.min(imageOriginal.getWidth(),imageOriginal.getHeight());
        // resize
        Matrix resize = new Matrix();
        resize.postScale(size/imageOriginal.getWidth(),size/imageOriginal.getHeight());
        imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);
        setBackgroundAngle(0);
    }

    private void setBackgroundAngle(float angle) {
        matrix.postRotate(angle);
        BitmapDrawable bd = new BitmapDrawable(getResources(), Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(), imageScaled.getHeight(), matrix, true));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(bd);
        } else {
            setBackgroundDrawable(bd);
        }
    }


    @Override
    protected void rotateButtons(float degrees) {
        super.rotateButtons(degrees);
        if (imageScaled !=null)
            setBackgroundAngle(getAngle());
        if (onRotationChangedListener !=null)
            onRotationChangedListener.onRotate(degrees);
    }

    @Override
    protected void animateTo(float endDegree, long duration) {
        if (imageScaled !=null)
            rotateBackground(getAngle(),endDegree, duration);
        if (onRotationChangedListener !=null)
            onRotationChangedListener.onAnimationStarted(endDegree, duration);
        super.animateTo(endDegree, duration);
    }

    private void resetRotation() {
        setAngle(getFirstChildPosition().getAngle());
        resetPage();
    }

    private void resetPage() {
        currentItem = 0;
    }

    @Override
    public void onItemSelected(View child) {
        final int childIndex = indexOfChild(child);
        // When Item not changed or list less than equals nr of views.
        if (selectedChild == childIndex || itemCount <=getChildCount())
            return;

        final int direction = (selectedChild == getChildCount()-1 && childIndex == 0)
                || ((childIndex != getChildCount()-1 || selectedChild !=0) && childIndex > selectedChild)
                ? DIRECTION_CW : DIRECTION_CCW;

        currentItem += direction == DIRECTION_CW ? +1 : -1;
        if (currentItem <0)
            currentItem = itemCount -1;
        else if (currentItem >= itemCount)
            currentItem = 0;
//        Log.d(TAG,"CurrentItem: " + currentItem);
//        Log.d(TAG,"Selected: " + childIndex);
        displayBufferItem(childIndex,direction);
        selectedChild = childIndex;

        if (onItemSelectedListener !=null)
            onItemSelectedListener.onItemSelected(child);
    }

    @Override
    protected void stopAnimation() {
        super.stopAnimation();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
        if (onRotationChangedListener !=null)
            onRotationChangedListener.onStopAnimation();
    }

    /**
     * Rotate the Background.
     *
     * @param angle
     * @param endDegree The degrees, the background should get rotated.
     */
    private void rotateBackground(float angle, float endDegree, long duration) {
        if (animator != null && animator.isRunning() || Math.abs(angle - endDegree) < 1) {
            return;
        }
        animator = ObjectAnimator.ofFloat(this, "backgroundAngle", angle,endDegree);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public void setItems(List<? extends WheelTextItem> items) {
        clear();
        addItems(items);
        displayData();
    }

    public void clear() {
        resetRotation();
        wheelTextItems.clear();
        clearWheel();
    }

    private void displayData() {
        if (!wheelTextItems.isEmpty())
            initWheelData();
    }

    private void clearWheel() {
        for (int i=0; i <getChildCount();i++){
            final View child = getChildAt(i);
            if (child instanceof TextView){
                clearView(child);
            }
        }
    }

    private void clearView(View child) {
        ((TextView) child).setText("");
        child.setTag(R.id.wheel_item_id,null);
    }

    private void initWheelData() {
        setPageCount();
        if (itemCount > getChildCount())
            displayFirstPageMirror();
        else
            displayFirstPage();
    }

    /**
     * Display buffer item to update list while scrolling.
     * @param direction boolean for scrolling direction.
     */
    private void displayBufferItem(int selectedIndex,int direction){
        final int halfItems = getChildCount()/2;
        int itemIndex;
        final int viewIndex;
        final int BUFFER_COUNT = 4;

        if (direction == DIRECTION_CW) {
            itemIndex = currentItem + halfItems;
            viewIndex = selectedIndex + halfItems - 1;
            if (itemIndex + BUFFER_COUNT >= itemCount)
                if (onLoadMoreListener !=null)
                    onLoadMoreListener.loadMore(OnLoadMoreListener.DIRECTION_DESC);
        }else {
            itemIndex = currentItem - halfItems;
            if (itemIndex - BUFFER_COUNT < 0)
                // Load more from Z-A
                if (onLoadMoreListener !=null)
                    onLoadMoreListener.loadMore(OnLoadMoreListener.DIRECTION_ASC);

            if (itemIndex <0)
                itemIndex += itemCount;
            viewIndex = selectedIndex + halfItems + 1;
        }

        final View child = getChildAt(viewIndex); // Get View
        final WheelTextItem item = getItem(itemIndex); // Get item
        if (child != null && item != null)
            setChildrenValue(child, item);
    }

    /**
     * Display all item on first page item count less than or equals #getChildCount().
     */
    private void displayFirstPage() {
        // Display next items
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index); // Get View
            final WheelTextItem item = getItem(index); // Get item
            if (child == null || item == null)
                continue;
            setChildrenValue(child, item);
        }
    }

    /**
     * Display first page as mirror when items count more than #getChildCount to enable next previous scrolling.
     */
    private void displayFirstPageMirror() {
        final int halfItemsCount = getChildCount()/2;
        // Display next items
        for (int index=0; index< halfItemsCount; index++){
            final View child = getChildAt(index); // Get View
            final WheelTextItem item = getItem(index); // Get item
            if (child == null || item == null)
                continue;
            setChildrenValue(child, item);
        }

        int itemIndexStartReversed = itemCount - halfItemsCount;
        // Display previous items
        for (int index=getChildCount()/2; index< getChildCount(); index++){
            final View child = getChildAt(index); // Get View
            final WheelTextItem item = getItem(itemIndexStartReversed); // Get item
            if (child == null || item == null)
                continue;
            setChildrenValue(child, item);
            itemIndexStartReversed++; // update Item index
        }
    }

    @Override
    public View getChildAt(int index) {
        return super.getChildAt(index%getChildCount());
    }

    private void setChildrenValue(View child, WheelTextItem item) {
        if (child instanceof TextView) {
            ((TextView) child).setText(item.getName());
            child.setTag(R.id.wheel_item_id,item);
        }
    }

    public void addItems(List<? extends WheelTextItem> items) {
        if (items == null || items.isEmpty())
            return;
        wheelTextItems.addAll(items);
        setPageCount();
    }

    private void setPageCount() {
        itemCount = wheelTextItems.size();
    }

    public int getItemCount(){
        return wheelTextItems.size();
    }

    private WheelTextItem getItem(int index) {
        if (wheelTextItems ==null || wheelTextItems.isEmpty() ||  index < 0 || index >= wheelTextItems.size())
            return null;
//        index = index% itemCount; // repeat displaying items on wheel
        return wheelTextItems.get(index);
    }

    public WheelTextItem getSelectedWheelItem(){
        return getItemByView(getSelectedItem());
    }

    private WheelTextItem getItemByView(View child) {
        return child !=null
                && child.getTag(R.id.wheel_item_id) instanceof WheelTextItem
                ? (WheelTextItem) child.getTag(R.id.wheel_item_id)
                : null;
    }

    public void select(WheelTextItem item) {
        if (item == null)
            return;
        final View viewToCenter = getViewByItem(item);
        if (viewToCenter !=null)
            rotateViewToCenter(viewToCenter);
        else
            setChildrenValue(getSelectedItem(),item);
    }

    private View getViewByItem(WheelTextItem itemName) {
        for (int i = 0; i < getChildCount(); i++){
            final View child = getChildAt(i);
            final WheelTextItem item = getItemByView(child);
            if (item !=null && item.equals(itemName))
                return child;
        }
        return null;
    }

    @Override
    public void onRotationFinished(View view) {
        if (onRotationChangedListener !=null)
            onRotationChangedListener.onRotationFinished(view);
    }

    @Override
    public void onChildAngleChanged(View child, float localAngle) {
        child.setRotation(localAngle + getFirstChildPosition().getAngle());  // Fix view rotation of each child
    }

    public interface OnRotationChangedListener extends OnRotationFinishedListener {
        void onAnimationStarted(float endDegree, long duration);
        void onRotate(float degree);
        void onStopAnimation();
    }

    public interface OnLoadMoreListener {
        int DIRECTION_ASC = 0; // clockwise
        int DIRECTION_DESC = 1;
        void loadMore(int direction);
    }
}