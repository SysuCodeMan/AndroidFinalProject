package com.example.john.finalproject.Life.account;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by Willo on 2016/12/18.
 */

public class MyPieChart extends View {

    //画圆所在的距形区域
    RectF oval;
    RectF rectF;
    public MyPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        oval = new RectF();
        rectF = new RectF();
    }
    private List<Float> percentage;
    private List<Integer> colors;
    private List<String> labels;
    public void setValues(List<Float> per) {
        percentage = per;
        invalidate();
    }
    public void setColors(List<Integer>col) {
        colors = col;
        invalidate();
    }
    public void setLabels(List<String>lab) {
        labels = lab;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        if(width!=height)
        {
            int min=Math.min(width, height);
            width=min;
            height=min;
        }
        int progressStrokeWidth = 40* width/1328;

        canvas.drawColor(Color.TRANSPARENT); // 白色背景
        //canvas.drawColor(Color.RED); // 白色背景
        //int progressStrokeWidth = 40;

        oval.left = 0; // 左上角x
        oval.top = 0; // 左上角y
        oval.right = width - progressStrokeWidth * 10; // 左下角x
        oval.bottom = height - progressStrokeWidth *10; // 右下角y

        rectF.left = width - progressStrokeWidth * 10;  // 左上角x
        rectF.top = progressStrokeWidth*2; // 左上角y
        rectF.right = width; // 左下角x
        rectF.bottom = height - progressStrokeWidth *10; // 右下角y


        //canvas.drawArc(oval, -90, 360, false, paint); // 绘制白色圆圈，即饼图背景
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.LTGRAY);
        p.setStyle(Paint.Style.FILL);
        p.setAlpha(150);
        p.setStrokeWidth(progressStrokeWidth);

        float max = 0;
        for (int i = 0; i < percentage.size(); i++) {
            max += percentage.get(i);
        }
        float startDegree = -90;
        for (int i = 0; i < percentage.size(); i++) {
            p.setColor(colors.get(i));
            float degreeDraw = (percentage.get(i) / max) * 360;
            canvas.drawArc(oval, startDegree, degreeDraw, true, p);
            startDegree+=degreeDraw;
        }
        p.setColor(Color.BLACK);
        //canvas.drawRect(rectF.left,rectF.width(),rectF.right,rectF.height(),p);
        float interval = rectF.height()/percentage.size();

        float startHeight = rectF.top;
        p.setTextSize(interval/2.2f);
        for (int i = 0; i < percentage.size(); i++) {
            p.setColor(colors.get(i));
            p.setAlpha(150);
            canvas.drawRect(rectF.left+rectF.width()/4,startHeight,rectF.left+rectF.width()/2.5f,startHeight+interval/6,p);
            p.setAlpha(255);
            canvas.drawText(labels.get(i),rectF.left+rectF.width()/2f,startHeight+interval/7,p);
            startHeight+=interval;
        }

    }


    /**
     * 非ＵＩ线程调用
     */
    public void setValuesNotInUiThread(List<Float> per) {
        this.percentage = per;
        this.postInvalidate();
    }
    public void setColorsNotInUiThread(List<Integer>col) {
        this.colors = col;
        this.postInvalidate();
    }
    public void setLabelsNotInUiThread(List<String>lab) {
        this.labels = lab;
        this.postInvalidate();
    }
}
