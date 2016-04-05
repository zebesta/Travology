package com.example.chrissebesta.travology;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by chrissebesta on 4/4/16.
 */
public class SwipeDetector implements View.OnTouchListener {


    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }
    //new variables for swiping with animation
    boolean mItemPressed = false;
    boolean mSwiping = false;
    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    BackgroundContainer mBackgroundContainer;
    ListView mListView;

    float mDownX;
    private int mSwipeSlop = -1;

    //old variables for without animation
    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(final View v, MotionEvent event) {
        boolean remove = false;
        mListView = (ListView) v.getRootView().findViewById(R.id.add_location);
        mBackgroundContainer = (BackgroundContainer) v.getRootView().findViewById(R.id.listViewBackground);


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //new swipe handler with animation
                if (mItemPressed) {
                    // Multi-item swipes not handled
                    return false;
                }
                mItemPressed = true;
                mDownX = event.getX();
                break;

                //old swipe handler without animation
//                downX = event.getX();
//                downY = event.getY();
//                mSwipeDetected = Action.None;
//                return false; // allow other events like Click to be processed
            }

            //swiping has stopped, pulled from new swipe with animation
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(1);
                v.setTranslationX(0);
                mItemPressed = false;
                break;

            case MotionEvent.ACTION_MOVE:
            {
                float x = event.getX() + v.getTranslationX();
                float deltaX = x - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                if (!mSwiping) {
                    if (deltaXAbs > mSwipeSlop) {
                        mSwiping = true;
//                        mListView.requestDisallowInterceptTouchEvent(true);
                        mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                    }
                }
                if (mSwiping) {
                    v.setTranslationX((x - mDownX));
                    v.setAlpha(1 - deltaXAbs / v.getWidth());
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            {
                // User let go - figure out whether to animate the view out, or back into place
                if (mSwiping) {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    float fractionCovered;
                    float endX;
                    float endAlpha;
                    //final boolean remove;
                    if (deltaXAbs > v.getWidth() / 4) {
                        // Greater than a quarter of the width - animate it out
                        fractionCovered = deltaXAbs / v.getWidth();
                        endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                        endAlpha = 0;
                        remove = true;
                    } else {
                        // Not far enough - animate it back
                        fractionCovered = 1 - (deltaXAbs / v.getWidth());
                        endX = 0;
                        endAlpha = 1;
                        remove = false;
                    }
                    // Animate position and alpha of swiped item
                    // NOTE: This is a simplified version of swipe behavior, for the
                    // purposes of this demo about animation. A real version should use
                    // velocity (via the VelocityTracker class) to send the item off or
                    // back at an appropriate speed.
                    long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                    //Null reference?
                    //mListView.setEnabled(false);
                    v.animate().setDuration(duration).
                            alpha(endAlpha).translationX(endX).
                            withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Restore animated values
                                    v.setAlpha(1);
                                    v.setTranslationX(0);

                                    //Simply return weather swipe is true or false
//                                    if (remove) {
//                                        //animateRemoval(mListView, v);
//                                    } else {
//                                        mBackgroundContainer.hideBackground();
//                                        mSwiping = false;
//                                        mListView.setEnabled(true);
//                                    }
                                }
                            });
                }
            }
            break;

            //OLD swipe detection with just true or false
//            case MotionEvent.ACTION_MOVE: {
//                upX = event.getX();
//                upY = event.getY();
//
//                float deltaX = downX - upX;
//                float deltaY = downY - upY;
//
//
//
//                // horizontal swipe detection
//                if (Math.abs(deltaX) > MIN_DISTANCE) {
//                    // left or right
//                    if (deltaX < 0) {
//                        Log.d(logTag, "Swipe Left to Right");
//                        mSwipeDetected = Action.LR;
//                        return true;
//                    }
//                    if (deltaX > 0) {
//                        Log.d(logTag, "Swipe Right to Left");
//                        mSwipeDetected = Action.RL;
//                        return true;
//                    }
//                } else
//
//                    // vertical swipe detection
//                    if (Math.abs(deltaY) > MIN_DISTANCE) {
//                        // top or down
//                        if (deltaY < 0) {
//                            Log.d(logTag, "Swipe Top to Bottom");
//                            mSwipeDetected = Action.TB;
//                            return false;
//                        }
//                        if (deltaY > 0) {
//                            Log.d(logTag, "Swipe Bottom to Top");
//                            mSwipeDetected = Action.BT;
//                            return false;
//                        }
//                    }
//                return true;
//            }
        }
        return remove;

    }

//
//    /**
//     * This method animates all other views in the ListView container (not including ignoreView)
//     * into their final positions. It is called after ignoreView has been removed from the
//     * adapter, but before layout has been run. The approach here is to figure out where
//     * everything is now, then allow layout to run, then figure out where everything is after
//     * layout, and then to run animations between all of those start/end positions.
//     */
//    private void animateRemoval(final ListView listview, View viewToRemove) {
//        int firstVisiblePosition = listview.getFirstVisiblePosition();
//        for (int i = 0; i < listview.getChildCount(); ++i) {
//            View child = listview.getChildAt(i);
//            if (child != viewToRemove) {
//                int position = firstVisiblePosition + i;
//                long itemId = mAdapter.getItemId(position);
//                mItemIdTopMap.put(itemId, child.getTop());
//            }
//        }
//        // Delete the item from the adapter
//        int position = mListView.getPositionForView(viewToRemove);
//        mAdapter.remove(mAdapter.getItem(position));
//
//        final ViewTreeObserver observer = listview.getViewTreeObserver();
//        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            public boolean onPreDraw() {
//                observer.removeOnPreDrawListener(this);
//                boolean firstAnimation = true;
//                int firstVisiblePosition = listview.getFirstVisiblePosition();
//                for (int i = 0; i < listview.getChildCount(); ++i) {
//                    final View child = listview.getChildAt(i);
//                    int position = firstVisiblePosition + i;
//                    long itemId = mAdapter.getItemId(position);
//                    Integer startTop = mItemIdTopMap.get(itemId);
//                    int top = child.getTop();
//                    if (startTop != null) {
//                        if (startTop != top) {
//                            int delta = startTop - top;
//                            child.setTranslationY(delta);
//                            child.animate().setDuration(MOVE_DURATION).translationY(0);
//                            if (firstAnimation) {
//                                child.animate().withEndAction(new Runnable() {
//                                    public void run() {
//                                        mBackgroundContainer.hideBackground();
//                                        mSwiping = false;
//                                        mListView.setEnabled(true);
//                                    }
//                                });
//                                firstAnimation = false;
//                            }
//                        }
//                    } else {
//                        // Animate new views along with the others. The catch is that they did not
//                        // exist in the start state, so we must calculate their starting position
//                        // based on neighboring views.
//                        int childHeight = child.getHeight() + listview.getDividerHeight();
//                        startTop = top + (i > 0 ? childHeight : -childHeight);
//                        int delta = startTop - top;
//                        child.setTranslationY(delta);
//                        child.animate().setDuration(MOVE_DURATION).translationY(0);
//                        if (firstAnimation) {
//                            child.animate().withEndAction(new Runnable() {
//                                public void run() {
//                                    mBackgroundContainer.hideBackground();
//                                    mSwiping = false;
//                                    mListView.setEnabled(true);
//                                }
//                            });
//                            firstAnimation = false;
//                        }
//                    }
//                }
//                mItemIdTopMap.clear();
//                return true;
//            }
//        });
//    }

}