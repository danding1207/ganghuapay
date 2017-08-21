package com.mqt.ganghuazhifu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class BoradImageView extends ImageView {
	
	private final Paint mPaint = new Paint();
	
	private int r;
	private int d = 2;

	public BoradImageView(Context context) {
		super(context);
		init();
	}

	public BoradImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoradImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.parseColor("#FF6D6E72"));
		mPaint.setStrokeWidth(2*d);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = this.getWidth();
		int h = this.getHeight();
		
		r = w/24;
		
		Path drawPath = new Path();
		drawPath.moveTo(w/3, d);
		drawPath.lineTo(r, d);
		drawPath.addArc(new RectF(d, d, 2*r+d, 2*r+d), 270f, -90f);
		drawPath.lineTo(d, h/3);
		canvas.drawPath(drawPath, mPaint);
		
		drawPath.reset();
		drawPath.moveTo(w-w/3, d);
		drawPath.lineTo(w-r, d);
		drawPath.addArc(new RectF(w-2*r-d, d, w-d, 2*r+d), -90f, 90f);
		drawPath.lineTo(w-d, h/3);
		canvas.drawPath(drawPath, mPaint);
		
		drawPath.reset();
		drawPath.moveTo(d, h-h/3);
		drawPath.lineTo(d, h-r);
		drawPath.addArc(new RectF(d, h-2*r-d, 2*r+d, h-d), 180f, -90f);
		drawPath.lineTo(w/3, h-d);
		canvas.drawPath(drawPath, mPaint);
		
		
		drawPath.reset();
		drawPath.moveTo(w-w/3, h-d);
		drawPath.lineTo(w-r, h-d);
		drawPath.addArc(new RectF(w-2*r-d, h-2*r-d, w-d, h-d), 90f, -90f);
		drawPath.lineTo(w-d, h-h/3);
		canvas.drawPath(drawPath, mPaint);
		
		super.onDraw(canvas);
	}
	

}
