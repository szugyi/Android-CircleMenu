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
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.szugyi.circlemenu.R;

import java.util.ArrayList;
import java.util.List;

public class WheelLayout extends CircleLayout implements CircleLayout.OnRotationFinishedListener,CircleLayout.OnItemSelectedListener {
    // Background image
    private ImageView bgImageView;
    protected List<WheelItem> wheelItems;
    private ObjectAnimator animator;
    private int itemCount;
    private int currentItem;
    private int selectedChild;
    protected OnRotationChangedListener onRotationChangedListener = null;
    protected OnLoadMoreListener onLoadMoreListener = null;
    protected OnItemSelectedListener onItemSelectedListener = null;

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

    public WheelLayout(Context context) {
        super(context);
    }

    public WheelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WheelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(TypedArray a) {
        super.init(a);
        wheelItems = new ArrayList<>();
        setOnRotationFinishedListener(this);
        setOnItemSelectedListener(this);
    }

    public void setBgImageView(ImageView bgImageView) {
        this.bgImageView = bgImageView;
    }

    public ImageView getBgImageView() {
        return bgImageView;
    }


    @Override
    protected void rotateButtons(float degrees) {
        super.rotateButtons(degrees);
        if (bgImageView !=null)
            bgImageView.setRotation(bgImageView.getRotation()+degrees);
        if (onRotationChangedListener !=null)
            onRotationChangedListener.onRotate(degrees);
        onFixWheelItemRotation(); // Fixing wheel items rotate angle.
    }

    protected void onFixWheelItemRotation() {
        for (int i = 0; i < getItemCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            final float localAngle = (float) child.getTag();
            child.setRotation(localAngle + getFirstChildPosition().getAngle());  // Fix view rotation of each child
        }
    }

    @Override
    protected void animateTo(float endDegree, long duration) {
        if (bgImageView !=null)
            rotateBackground(getAngle(),endDegree, duration);
        if (onRotationChangedListener !=null)
            onRotationChangedListener.onAmimationStarted(endDegree, duration);
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

        final boolean next = (selectedChild == getChildCount()-1 && childIndex == 0)
                || ((childIndex != getChildCount()-1 || selectedChild !=0) && childIndex > selectedChild);

        currentItem += next ? +1 : -1;
        if (currentItem <0)
            currentItem = itemCount -1;
        else if (currentItem >= itemCount)
            currentItem = 0;
//        Log.d(TAG,"CurrentItem: " + currentItem);
//        Log.d(TAG,"Selected: " + childIndex);
        displayBufferItem(childIndex,next);
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
        animator = ObjectAnimator.ofFloat(bgImageView, "rotation", angle,endDegree);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public void setItems(List<? extends WheelItem> items, boolean display) {
        clear();
        addItems(items);
        if (!display)
            return;
        displayData();
    }

    protected void clear() {
        resetRotation();
        wheelItems.clear();
        clearWheel();
    }

    private void displayData() {
        if (!wheelItems.isEmpty())
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
     * @param next boolean for scrolling direction.
     */
    private void displayBufferItem(int selectedIndex,boolean next){
        final int halfItems = getChildCount()/2;
        int itemIndex;
        final int viewIndex;
        final int BUFFER_COUNT = 4;

        if (next) {
            itemIndex = currentItem + halfItems;
            viewIndex = selectedIndex + halfItems - 1;
            if (itemIndex + BUFFER_COUNT >= itemCount)
                loadMore(true);  // Load more
        }else {
            itemIndex = currentItem - halfItems;
            if (itemIndex - BUFFER_COUNT < 0)
                loadMore(false); // Load more from Z-A

            if (itemIndex <0)
                itemIndex += itemCount;
            viewIndex = selectedIndex + halfItems + 1;
        }

        final View child = getChildAt(viewIndex); // Get View
        final WheelItem item = getItem(itemIndex); // Get item
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
            final WheelItem item = getItem(index); // Get item
            if (child == null || item == null)
                continue;
            setChildrenValue(child, item);
        }
    }

    /**
     * Display first page as mirror when items count more than #getChildCount to enable next previous scrolling.
     */
    private void displayFirstPageMirror() {
        final int itemCount = getChildCount()/2;
        // Display next items
        for (int index=0; index< itemCount; index++){
            final View child = getChildAt(index); // Get View
            final WheelItem item = getItem(index); // Get item
            if (child == null || item == null)
                continue;
            setChildrenValue(child, item);
        }

        int itemIndexStartReversed = this.itemCount - itemCount;
        // Display previous items
        for (int index=getChildCount()/2; index< getChildCount(); index++){
            final View child = getChildAt(index); // Get View
            final WheelItem item = getItem(itemIndexStartReversed); // Get item
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

    private void setChildrenValue(View child, WheelItem item) {
        if (child instanceof TextView) {
            ((TextView) child).setText(item.getName());
            child.setTag(R.id.wheel_item_id,item);
        }
    }

    public void addItems(List<? extends WheelItem> items) {
        if (items == null || items.isEmpty())
            return;
        if (wheelItems == null)
            wheelItems = new ArrayList<>();
        wheelItems.addAll(items);
        setPageCount();
    }

    private void setPageCount() {
        itemCount = wheelItems.size();
    }

    public int getItemCount(){
        return wheelItems.size();
    }

    private WheelItem getItem(int index) {
        if (wheelItems ==null || wheelItems.isEmpty() ||  index < 0)
            return null;
        index = index% itemCount; // repeat displaying items on wheel
        return wheelItems.get(index);
    }

    private void loadMore(boolean isNext){
        if (onLoadMoreListener !=null)
            onLoadMoreListener.loadMore(isNext);
    }

    public WheelItem getSelectedWheelItem(){
        return getItemByView(getSelectedItem());
    }

    private WheelItem getItemByView(View child) {
        return child !=null
                && child.getTag(R.id.wheel_item_id) instanceof WheelItem
                ? (WheelItem) child.getTag(R.id.wheel_item_id)
                : null;
    }


    public void select(WheelItem itemName) {
        if (itemName == null)
            return;
        select(itemName.getName());
    }

    public void select(String itemName) {
        if (itemName == null)
            return;
        final View viewToCenter = getViewByItem(itemName);
        if (viewToCenter !=null)
            rotateViewToCenter(viewToCenter);
    }

    private View getViewByItem(String itemName) {
        for (int i = 0; i < getChildCount(); i++){
            final View child = getChildAt(i);
            final WheelItem item = getItemByView(child);
            if (item !=null && item.getName().equals(itemName))
                return child;
        }
        return null;
    }

    @Override
    public void onRotationFinished(View view) {
        if (onRotationChangedListener !=null)
            onRotationChangedListener.onRotationFinished(view);
    }

    public interface OnRotationChangedListener extends OnRotationFinishedListener{
        void onAmimationStarted(float endDegree, long duration);
        void onRotate(float degree);
        void onStopAnimation();
    }

    public interface OnLoadMoreListener {
        void loadMore(boolean isNext);
    }
}