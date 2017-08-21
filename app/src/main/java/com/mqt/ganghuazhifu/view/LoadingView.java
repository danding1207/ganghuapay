package com.mqt.ganghuazhifu.view;

import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LoadingView extends View {

	private int exradius,radius;
	private Paint paint;
	private int startAngle = 0;
	
	public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint(context);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint(context);
	}

	public LoadingView(Context context) {
		super(context);
		initPaint(context);
	}

	private void initPaint(Context context) {
		paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setColor(getResources().getColor(R.color.dark_green_slow));
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setStrokeWidth(1F);
		paint.setAlpha(255);
		
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		exradius = getWidth();
		radius = exradius/6;
		paint.setStrokeWidth(1F);
		paint.setAlpha(255);
		
		canvas.drawArc(new RectF(1,1, exradius-1, exradius-1), 0, 360, false, paint);
		
		paint.setStrokeWidth(UnitConversionUtils.px2dip(getContext(), 14));
		paint.setAlpha(255);
		
		canvas.drawArc(new RectF(radius,radius, exradius-radius, exradius-radius), 0+startAngle, 90, false, paint);
		
		canvas.drawArc(new RectF(radius,radius, exradius-radius, exradius-radius), 180+startAngle, 90, false, paint);
		
		canvas.drawArc(new RectF(radius*2,radius*2, exradius-radius*2, exradius-radius*2), 90-startAngle, 90, false, paint);
		
		canvas.drawArc(new RectF(radius*2,radius*2, exradius-radius*2, exradius-radius*2), 270-startAngle, 90, false, paint);
		
		paint.setAlpha(50);
		
		canvas.drawArc(new RectF(radius,radius, exradius-radius, exradius-radius), 90+startAngle, 90, false, paint);
		
		canvas.drawArc(new RectF(radius,radius, exradius-radius, exradius-radius), 270+startAngle, 90, false, paint);
		
		canvas.drawArc(new RectF(radius*2,radius*2, exradius-radius*2, exradius-radius*2), 0-startAngle, 90, false, paint);
		
		canvas.drawArc(new RectF(radius*2,radius*2, exradius-radius*2, exradius-radius*2), 180-startAngle, 90, false, paint);
		
		startAngle = (startAngle +10)%360;
		
		invalidate();
	}
	
}
