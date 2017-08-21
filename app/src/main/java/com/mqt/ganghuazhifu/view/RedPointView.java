package com.mqt.ganghuazhifu.view;

import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.utils.TextFormater;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class RedPointView extends View {

	private Paint paintBackground;
	private Paint paintText;
	private int num = 0;
	/**  */
	private float textSize;
	/** 默认字体颜色 */
	private int backgroundColor = 0xffff0000;
	/** 默认字体颜色 */
	private int textColor = 0xffffffff;

	public RedPointView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
		initPaint(context);
	}

	public RedPointView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
		initPaint(context);
	}

	public RedPointView(Context context) {
		super(context);
		initPaint(context);
	}

	/**
	 * 初始化，获取设置的属性
	 * 
	 * @param context
	 * @param attrs
	 */
	private void init(Context context, AttributeSet attrs) {
		TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.RedPointView);
		textSize = (int) attribute.getDimension(R.styleable.RedPointView_textSize,
				UnitConversionUtils.dipTopx(getContext(), 18) * 4 / 5);
		num = attribute.getInt(R.styleable.RedPointView_textNum, 7);
		backgroundColor = attribute.getColor(R.styleable.RedPointView_backgroundColor, 0xffff0000);
		textColor = attribute.getColor(R.styleable.RedPointView_textColor, 0xffffffff);
		attribute.recycle();
	}

	private void initPaint(Context context) {
		paintBackground = new Paint(Paint.FILTER_BITMAP_FLAG);
		paintBackground.setColor(backgroundColor);
		paintBackground.setStyle(Style.FILL);
		paintBackground.setAntiAlias(true);
		paintBackground.setFilterBitmap(true);
		paintBackground.setDither(true);
		paintBackground.setStrokeWidth(1F);
		paintBackground.setAlpha(255);

		paintText = new Paint(Paint.FILTER_BITMAP_FLAG);
		paintText.setColor(textColor);
		paintText.setTextSize(textSize);// 设置字体大小
		// paintText.setTypeface();//设置字体类型
		paintText.setAlpha(255);

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
		// Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		int width = getWidth();
		int height = getHeight();
		if (num > 0) {
			canvas.drawCircle(width / 2, width / 2, width / 2, paintBackground);
			float textX = (width - TextFormater.getFontlength(paintText, num + "")) / 2;
			float textY = (height - TextFormater.getFontHeight(paintText)) / 2 + TextFormater.getFontLeading(paintText);
			canvas.drawText(num + "", textX, textY, paintText);
		} else if(num==-1) {
			canvas.drawCircle(width / 2, width / 2, width / 4, paintBackground);
		}
	}

	public void setNum(int num) {
		this.num = num;
		invalidate();
	}

	public int getNum() {
		return num;
	}

}
