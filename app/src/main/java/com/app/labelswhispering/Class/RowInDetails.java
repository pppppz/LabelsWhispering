package com.app.labelswhispering.Class;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public class RowInDetails {

    protected LinearLayout headerRow, expandRow;
    protected ValueAnimator mAnimator;
    protected int paddingSize;

    public RowInDetails(LinearLayout header, LinearLayout expand, int addPaddingSize) {
        headerRow = header;
        expandRow = expand;
        paddingSize = addPaddingSize;
    }


    public RowInDetails init() {
        //Add onPreDrawListener
        expandRow.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {


                        View parent = (View) expandRow.getParent();
                        expandRow.getViewTreeObserver().removeOnPreDrawListener(this);
                        expandRow.setVisibility(View.GONE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.AT_MOST);

                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

                        expandRow.measure(widthSpec, heightSpec);

                        Log.e("RowInDetails", "measuredHeight : " + expandRow.getMeasuredHeight());

                        mAnimator = slideAnimator(0, expandRow.getMeasuredHeight() + paddingSize);
                        return true;
                    }


                });

        headerRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (expandRow.getVisibility() == View.GONE) {
                    expand();
                } else {
                    collapse();
                }
            }
        });
        return null;
    }


    private void expand() {
        //set Visible
        expandRow.setVisibility(View.VISIBLE);
        mAnimator.start();
    }


    private void collapse() {

        int finalHeight = expandRow.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                expandRow.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        Log.e("RowInDetails", "start " + start + " end " + end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = expandRow.getLayoutParams();
                layoutParams.height = value;
                expandRow.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
