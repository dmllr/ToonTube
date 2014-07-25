package com.armedarms.toontube.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

public class RelativeLayout16x9 extends android.widget.RelativeLayout {

    public RelativeLayout16x9(Context context) {
        super(context);
    }

    public RelativeLayout16x9(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayout16x9(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();
        setMeasuredDimension(w, w * 9 / 16);
    }
}
