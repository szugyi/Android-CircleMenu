/*
 * Copyright dmitry.zaicew@gmail.com Dmitry Zaitsev
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
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.szugyi.circlemenu.R;

import java.util.ArrayList;
import java.util.List;

public class WheelLayout extends CircleLayout {
    // Background image
    private ImageView bgImageView;
    protected List<WheelLayout.WheelItem> mItems;
    private ObjectAnimator animator;
    private int mItemCount;
    private int mCurrentItem;
    private int mSelectedChild;
    private final String TAG = "WheelMenu";

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
        final int bgRes = a.getResourceId(R.styleable.CircleLayout_wheel_bg,0);
        if (bgRes != 0) {
            // Handel background here,
        }
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
        if (wheelCallBack !=null)
            wheelCallBack.onRotate(degrees);
    }

    protected void onFixRotation(View child, float localAngle) {
        child.setRotation(localAngle + getFirstChildPosition().getAngle());  // Fix view rotation of children
    }

    @Override
    protected void animateTo(float endDegree, long duration) {
        if (bgImageView !=null)
            rotateBackground(getAngle(),endDegree, duration);
        if (wheelCallBack !=null)
            wheelCallBack.onSettleRotation(endDegree, duration);
        super.animateTo(endDegree, duration);
    }

    private void resetRotation() {
        setAngle(getFirstChildPosition().getAngle());
        resetPage();
    }

    private void resetPage() {
        mCurrentItem = 0;
    }

    @Override
    protected void onItemSelected(View child) {
        final int childIndex = indexOfChild(child);
        // When Item not changed or list less than equals nr of views.
        if (mSelectedChild == childIndex || mItemCount<=getChildCount())
            return;

        final boolean next = (mSelectedChild == getChildCount()-1 && childIndex == 0)
                || ((childIndex != getChildCount()-1 || mSelectedChild!=0) && childIndex > mSelectedChild);

        mCurrentItem += next ? +1 : -1;
        if (mCurrentItem <0)
            mCurrentItem = mItemCount -1;
        else if (mCurrentItem >= mItemCount)
            mCurrentItem = 0;
        Log.d(TAG,"CurrentItem: " + mCurrentItem);
        Log.d(TAG,"Selected: " + childIndex);
        displayBufferItem(childIndex,next);
        mSelectedChild = childIndex;
    }

    @Override
    protected void stopAnimation() {
        super.stopAnimation();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
        if (wheelCallBack !=null)
            wheelCallBack.onStopAnimation();
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

    public void setItems(List<? extends WheelLayout.WheelItem> items, boolean display) {
        clear();
        addItems(items);
        if (!display)
            return;
        displayData();
    }

    protected void clear() {
        resetRotation();
        if (mItems == null)
            new ArrayList<WheelItem>(0);
        else
            mItems.clear();
        clearWheel();
    }

    private void displayData() {
        if (mItems !=null && !mItems.isEmpty())
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
        if (mItemCount> getChildCount())
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
            itemIndex = mCurrentItem + halfItems;
            viewIndex = selectedIndex + halfItems - 1;
            if (itemIndex + BUFFER_COUNT >= mItemCount)
                loadMore(true);  // Load more
        }else {
            itemIndex = mCurrentItem - halfItems;
            if (itemIndex - BUFFER_COUNT < 0)
                loadMore(false); // Load more from Z-A

            if (itemIndex <0)
                itemIndex += mItemCount;
            viewIndex = selectedIndex + halfItems + 1;
        }

        final View children = getChildAt(viewIndex); // Get View
        final WheelItem item = getItem(itemIndex); // Get item
        if (children != null && item != null)
            setChildrenValue(children, item);
    }

    /**
     * Display all item on first page item count less than or equals #getChildCount().
     */
    private void displayFirstPage() {
        // Display next items
        for (int index = 0; index < getChildCount(); index++) {
            final View children = getChildAt(index); // Get View
            final WheelItem item = getItem(index); // Get item
            if (children == null || item == null)
                continue;
            setChildrenValue(children, item);
        }
    }

    /**
     * Display first page as mirror when items count more than #getChildCount to enable next previous scrolling.
     */
    private void displayFirstPageMirror() {
        final int itemCount = getChildCount()/2;
        // Display next items
        for (int index=0; index< itemCount; index++){
            final View children = getChildAt(index); // Get View
            final WheelItem item = getItem(index); // Get item
            if (children == null || item == null)
                continue;
            setChildrenValue(children, item);
        }

        int itemIndexStartReversed = mItemCount - itemCount;
        // Display previous items
        for (int index=getChildCount()/2; index< getChildCount(); index++){
            final View children = getChildAt(index); // Get View
            final WheelItem item = getItem(itemIndexStartReversed); // Get item
            if (children == null || item == null)
                continue;
            setChildrenValue(children, item);
            itemIndexStartReversed++; // update Item index
        }
    }

    @Override
    public View getChildAt(int index) {
        return super.getChildAt(index%getChildCount());
    }

    private void setChildrenValue(View children, WheelItem item) {
        if (children instanceof TextView) {
            ((TextView) children).setText(item.getName());
            children.setTag(R.id.wheel_item_id,item);
        }
    }

    public void addItems(List<? extends WheelLayout.WheelItem> cats) {
        if (cats == null || cats.isEmpty())
            return;
        if (mItems == null)
            mItems = new ArrayList<>();
        mItems.addAll(cats);
        setPageCount();
    }

    private void setPageCount() {
        mItemCount = mItems.size();
    }

    public int getItemCount(){
        return mItems !=null ? mItems.size() : -1;
    }

    private WheelItem getItem(int index) {
        if (mItems ==null || mItems.isEmpty() ||  index < 0)
            return null;
        index = index%mItemCount; // repeat displaying items on wheel
        return mItems.get(index);
    }

    private void loadMore(boolean isNext){
        if (wheelCallBack !=null)
            wheelCallBack.loadMore(isNext);
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

    public interface WheelItem{
        String getName();
        long getId();
    }
}