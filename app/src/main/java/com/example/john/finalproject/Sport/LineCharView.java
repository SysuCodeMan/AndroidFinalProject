package com.example.john.finalproject.Sport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.john.finalproject.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LineCharView extends View {
	private int xori;//圆点x坐标
	private int yori;//圆点y坐标
	private int xinit;//第一个点x坐标
	private int minXinit;//在移动时，第一个点允许最小的x坐标
	private int maxXinit;//在移动时，第一个点允许允许最大的x坐标
	private int xylinecolor;//xy坐标轴颜色
	private int xylinewidth;//xy坐标轴大小
	private int xytextcolor;//xy坐标轴文字颜色
	private int xytextsize;//xy坐标轴文字大小
	private int linecolor;//折线的颜色
	private int interval;//坐标间的间隔
    private float yfactor;//y轴间距倒数
	private int bgColor;//背景颜色
	private List<String> x_coords;//x坐标点的值
	private List<String> x_coord_values;//每个点状态值
	
	
	private int width;//控件宽度
	private int heigth;//控件高度
	private int imageWidth;//表情的宽度
	private float textwidth;//y轴文字的宽度
	float startX=0;//滑动时候，上一次手指的x坐标

	public LineCharView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 TypedArray typedArray= context.obtainStyledAttributes(attrs, R.styleable.LineChar);
		 xylinecolor=typedArray.getColor(R.styleable.LineChar_xylinecolor, Color.GRAY);
		 xylinewidth=typedArray.getInt(R.styleable.LineChar_xylinewidth, 5);
		 xytextcolor=typedArray.getColor(R.styleable.LineChar_xytextcolor, Color.BLACK);
		 xytextsize=typedArray.getLayoutDimension(R.styleable.LineChar_xytextsize, 20);
		 linecolor=typedArray.getColor(R.styleable.LineChar_linecolor, Color.CYAN);
		 interval=typedArray.getLayoutDimension(R.styleable.LineChar_interval, 100);
         yfactor = typedArray.getLayoutDimension(R.styleable.LineChar_yfactor, 10);
		 bgColor=typedArray.getColor(R.styleable.LineChar_bgcolor, Color.WHITE);
		 typedArray.recycle();
		 x_coords=new ArrayList<String>();
		 x_coord_values=new ArrayList<String>();
	}
	
	
	public LineCharView(Context context) {
		super(context);
	}

	
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if(changed){
			width=getWidth();
			heigth=getHeight();
			Paint paint=new Paint();
			paint.setTextSize(xytextsize);
		    textwidth= paint.measureText("A");
			xori=(int) (textwidth+6+2*xylinewidth);//6 为与y轴的间隔
			yori=heigth-2*xytextsize-2*xylinewidth-3;
			xinit=interval/2+xori;
			imageWidth= 10;
			minXinit=width-xori-x_coords.size()*interval;
			maxXinit=xinit;
			setBackgroundColor(bgColor);
		}
		super.onLayout(changed, left, top, right, bottom);
	}


	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		drawX(canvas);
		drawXY(canvas);
		//drawY(canvas);
	}
	
	
	//画坐标轴
	private void drawXY(Canvas canvas){
		Paint paint=new Paint();
		paint.setColor(xylinecolor);
		paint.setStrokeWidth(xylinewidth);
		//canvas.drawLine(xori, 0, xori, yori, paint);
		canvas.drawLine(xori, yori, width, yori, paint);
	}
	
	//画Y轴坐标点
	private void drawY(Canvas canvas){
		Paint paint=new Paint();
		paint.setColor(xylinecolor);
		paint.setStyle(Paint.Style.FILL);
		for(int i=1;i<5 ;i++){
			canvas.drawCircle(xori, yori-(i*interval/2), xylinewidth*2, paint);
		}

	}
	
	//画X轴坐标点，折线
	@SuppressLint("ResourceAsColor")
	private void drawX (Canvas canvas) {
		Paint x_coordPaint =new Paint();
		
		Log.i("xinit", xinit+"");
		x_coordPaint.setTextSize(xytextsize);
		x_coordPaint.setStyle(Paint.Style.FILL);
		Path path=new Path();
		Path pathshade = new Path();
		//画坐标点
		for(int i=0;i<x_coords.size();i++){
			int x=i*interval+xinit;
			if(i==0){
				path.moveTo(x,yori);
				pathshade.moveTo(x, getYValue(x_coord_values.get(i)));
				path.lineTo(x, getYValue(x_coord_values.get(i)));
			}else if(i == x_coords.size()-1) {
				pathshade.lineTo(x, getYValue(x_coord_values.get(i)));
				path.lineTo(x, getYValue(x_coord_values.get(i)));
				path.lineTo(x,yori);
			}else {
				pathshade.lineTo(x, getYValue(x_coord_values.get(i)));
				path.lineTo(x, getYValue(x_coord_values.get(i)));
			}
			x_coordPaint.setColor(xylinecolor);
			canvas.drawCircle(x, yori, xylinewidth*2, x_coordPaint);
			String text=x_coords.get(i);
			x_coordPaint.setColor(xytextcolor);
			canvas.drawText(text, x-x_coordPaint.measureText(text)/2, yori+xytextsize+xylinewidth*2, x_coordPaint);
		}
		
		x_coordPaint.setStyle(Paint.Style.FILL);
		x_coordPaint.setStrokeWidth(xylinewidth);
		//x_coordPaint.setColor(Color.argb(120,0xCC,0xFF,0xFF));
		Shader lg = new LinearGradient(width/2,heigth,width/2,heigth,Color.argb(225,0xCC,0xFF,0xFF),Color.argb(80,0xCC,0xFF,0xFF), Shader.TileMode.CLAMP);
		Shader shader = new LinearGradient(width/2,heigth,width/2,heigth-yori,
				new int[] {Color.argb(80,0xCC,0xFF,0xFF),Color.argb(100,0xCC,0xFF,0xFF),Color.argb(120,0xCC,0xFF,0xFF),Color.argb(140,0xCC,0xFF,0xFF),
						Color.argb(160,0xCC,0xFF,0xFF),Color.argb(180,0xCC,0xFF,0xFF),
						Color.argb(200,0xCC,0xFF,0xFF),Color.argb(225,0xCC,0xFF,0xFF)},
				null, Shader.TileMode.CLAMP);
		x_coordPaint.setShader(shader);

		Paint p = new Paint();
        p.setColor(Color.CYAN);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(xylinewidth);
        p.setAntiAlias(true);
        p.setTextSize(50);

		//画折线

		canvas.drawPath(path, x_coordPaint);
		canvas.drawPath(pathshade, p);
		//canvas.clipPath(path);
		
		//画描点
		for(int i=0;i<x_coords.size();i++){
			int x=i*interval+xinit;
            float y = getYValue(x_coord_values.get(i));
            p.setStyle(Paint.Style.FILL);
			p.setAlpha(250);
			canvas.drawCircle(x,y , 15, p);
            canvas.drawText(x_coord_values.get(i),x,y-50,p);
		}
		
		
		//将折线超出x轴坐标的部分截取掉
		x_coordPaint.setStyle(Paint.Style.FILL);
		x_coordPaint.setColor(bgColor);
		x_coordPaint.setXfermode(new PorterDuffXfermode( PorterDuff.Mode.SRC_OVER));
		RectF rectF=new RectF(0, 0, xori, heigth);
		canvas.drawRect(rectF, x_coordPaint);
		
	}
	

	//得到y坐标
	private float getYValue(String value)
	{
        float fvalue = Float.valueOf(value);
        return yori-fvalue/yfactor;
	}
	
	public void setYfactor(int factor) {
		yfactor = factor;
		if (yfactor <= 0.5f) yfactor = 0.5f;
		invalidate();
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		//如果不用滑动就可以展示所有数据，就不让滑动
		if(interval*x_coord_values.size()<=width-xori){
			return false;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX=event.getX();
			break;

		case MotionEvent.ACTION_MOVE:
			float dis=event.getX()-startX;
			startX=event.getX();
			if(xinit+dis>maxXinit){
				xinit=maxXinit;
			}else if(xinit+dis<minXinit){
				xinit=minXinit;
			}else{
				xinit=(int) (xinit+dis);
			}
			invalidate();
			
			break;
		}
		return true;
	}


	public int getXori() {
		return xori;
	}


	public void setXori(int xori) {
		this.xori = xori;
	}


	public int getYori() {
		return yori;
	}


	public void setYori(int yori) {
		this.yori = yori;
	}


	public int getXinit() {
		return xinit;
	}


	public void setXinit(int xinit) {
		this.xinit = xinit;
	}


	public int getMinXinit() {
		return minXinit;
	}


	public void setMinXinit(int minXinit) {
		this.minXinit = minXinit;
	}


	public int getMaxXinit() {
		return maxXinit;
	}


	public void setMaxXinit(int maxXinit) {
		this.maxXinit = maxXinit;
	}


	public int getXylinecolor() {
		return xylinecolor;
	}


	public void setXylinecolor(int xylinecolor) {
		this.xylinecolor = xylinecolor;
	}


	public int getXylinewidth() {
		return xylinewidth;
	}


	public void setXylinewidth(int xylinewidth) {
		this.xylinewidth = xylinewidth;
	}


	public int getXytextcolor() {
		return xytextcolor;
	}


	public void setXytextcolor(int xytextcolor) {
		this.xytextcolor = xytextcolor;
	}


	public int getXytextsize() {
		return xytextsize;
	}


	public void setXytextsize(int xytextsize) {
		this.xytextsize = xytextsize;
	}


	public int getLinecolor() {
		return linecolor;
	}


	public void setLinecolor(int linecolor) {
		this.linecolor = linecolor;
	}


	public int getInterval() {
		return interval;
	}


	public void setInterval(int interval) {
		this.interval = interval;
	}


	public int getBgColor() {
		return bgColor;
	}


	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}


	public List<String> getX_coords() {
		return x_coords;
	}


	


	public List<String> getX_coord_values() {
		return x_coord_values;
	}


	

	// 设置坐标折线图值 x_coords 横坐标坐标点 x_coord_values 每个点的值

	public void  setValue(List<String> x_coords , List<String> x_coord_values) {
		if(x_coord_values.size()!=x_coords.size()){
			throw new IllegalArgumentException("坐标轴点和坐标轴点的值的个数必须一样!");
		}
		this.x_coord_values=x_coord_values;
		this.x_coords=x_coords;
		invalidate();
	}
	
	
	
	
	

}
