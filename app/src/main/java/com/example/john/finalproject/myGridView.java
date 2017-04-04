package com.example.john.finalproject;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Willo on 2016/12/21.
 */

public class myGridView extends GridView {
    public myGridView(Context context) {
        super(context);
    }

    public myGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
