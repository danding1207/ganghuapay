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

public class CoverImageView extends ImageView {
	
private final Paint topPaint = new Paint();
private final Paint bottomPaint = new Paint();
	
	public CoverImageView(Context context) {
		super(context);
		init();
	}

	public CoverImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CoverImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		topPaint.setStyle(Paint.Style.FILL);
		topPaint.setAntiAlias(true);
		topPaint.setColor(Color.parseColor("#FFE6E6E6"));
		topPaint.setStrokeWidth(2);
		bottomPaint.setStyle(Paint.Style.FILL);
		bottomPaint.setAntiAlias(true);
		bottomPaint.setColor(Color.parseColor("#FFE6E6E6"));
		bottomPaint.setStrokeWidth(2);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = this.getWidth();
		int h = this.getHeight();
		
		int r = w/36;
		
		Path drawPath = new Path();
		drawPath.moveTo(0, 0);
		drawPath.lineTo(r, 0);
		drawPath.addArc(new RectF(0, 0, 2*r, 2*r), 270f, -90f);
		drawPath.lineTo(0, 0);
		canvas.drawPath(drawPath, topPaint);
		
		drawPath.reset();
		drawPath.moveTo(w-r, 0);
		drawPath.lineTo(w, 0);
		drawPath.lineTo(w, r);
		drawPath.addArc(new RectF(w-2*r, 0, w, 2*r), 0f, -90f);
		canvas.drawPath(drawPath, topPaint);
		
		drawPath.reset();
		drawPath.moveTo(0, h-r);
		drawPath.addArc(new RectF(0, h-2*r, 2*r, h), 180f, -90f);
		drawPath.lineTo(0, h);
		drawPath.lineTo(0, h-r);
		canvas.drawPath(drawPath, bottomPaint);
		
		
		drawPath.reset();
		drawPath.moveTo(w-r, h);
		drawPath.lineTo(w, h);
		drawPath.lineTo(w, h-r);
		drawPath.addArc(new RectF(w-2*r, h-2*r, w, h), 0f, 90f);
		canvas.drawPath(drawPath, bottomPaint);
		
		super.onDraw(canvas);
	}
	

}
