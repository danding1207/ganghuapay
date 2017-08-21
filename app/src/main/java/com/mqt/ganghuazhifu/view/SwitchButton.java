package com.mqt.ganghuazhifu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.mqt.ganghuazhifu.R;

public class SwitchButton extends View {

	public static final int SHAPE_RECT = 1;
	public static final int SHAPE_CIRCLE = 2;
	private static final int RIM_SIZE = 8;
	private static final int COLOR_THEME = Color.parseColor("#DDF67373");

	private ViewParent mParent;
	// 3 attributes
	private int color_theme;
	private boolean isOpen;   //1:开 0:关
	private int shape;
	// varials of drawing
	private Paint paint;
	private Rect backRect;
	private Rect backRect2;
	private Rect frontRect;
	private int alpha;
	private int max_left;
	private int min_left;
	private int frontRect_left;
	private int frontRect_left_begin = RIM_SIZE;
	private int eventStartX;
	private int eventLastX;
	private int diffX = 0;
	private boolean slideable = true;
	private int mWidth = 100;
	private int mHeight = 100;

	private int height2 = 0;
	private int width2 = 0;
	private int slid = 28;
	int i = 0;

	/**
	 * Pause icon will be converted to Bitmap
	 */
	private Bitmap mBitmapBackground;


	/**
	 * Pause icon will be converted to Bitmap
	 */
	private Bitmap mBitmapBackgroundOpen;

	/**
	 * Pause icon will be converted to Bitmap
	 */
	private Bitmap mBitmapSwitch;

	private SwitchButtonStateChangedListener listener;

	public interface SwitchButtonStateChangedListener {
		public void SwitchButtonStateChanged(boolean isOpen);
	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		listener = null;
		paint = new Paint();
		paint.setAntiAlias(true);
		setLayerType(LAYER_TYPE_SOFTWARE, paint);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.slideswitch);
		color_theme = a.getColor(R.styleable.slideswitch_themeColor,
				COLOR_THEME);
		isOpen = a.getBoolean(R.styleable.slideswitch_isOpen, false);
		shape = a.getInt(R.styleable.slideswitch_shape, SHAPE_CIRCLE);
		a.recycle();

//		color_theme = getResources().getColor(R.color.primaryDark);
		color_theme = Color.parseColor("#14BE03");

		mBitmapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.switch_button_bg);
		mBitmapBackgroundOpen = BitmapFactory.decodeResource(getResources(), R.drawable.switch_button_open_new_bg);
		mBitmapSwitch = BitmapFactory.decodeResource(getResources(), R.drawable.switch_bg);
//		Log.e("","Width--->"+mBitmapSwitch.getWidth());
//		Log.e("","Height--->"+mBitmapSwitch.getHeight());


	}

	public SwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwitchButton(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureDimension(220, widthMeasureSpec);
		int height = measureDimension(100, heightMeasureSpec);
		if (shape == SHAPE_CIRCLE) {
			if (width < height)
				width = height * 2;
		}
		setMeasuredDimension(width, height);
		Log.e("","mun---->"+(i++));
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		Log.e("","Width--->"+mWidth);
		Log.e("", "Height--->" + mHeight);
		initDrawingVal();
	}

	public void initDrawingVal() {

		Log.e("","Width--->"+mWidth);
		Log.e("","Height--->"+mHeight);

		mBitmapBackground = Bitmap.createScaledBitmap(mBitmapBackground, mWidth,mHeight, true);
		mBitmapBackgroundOpen = Bitmap.createScaledBitmap(mBitmapBackgroundOpen, mWidth,mHeight, true);
		height2 = mHeight*58/65;
		width2 = mBitmapSwitch.getWidth()*(height2)/mBitmapSwitch.getHeight();

		mBitmapSwitch = Bitmap.createScaledBitmap(mBitmapSwitch, width2, height2, true);
		backRect = new Rect(slid/2+2, slid/2+3, mWidth-slid/2-2, mHeight-slid/2-2);
		backRect2 = new Rect(RIM_SIZE, RIM_SIZE, mWidth-RIM_SIZE, mHeight-RIM_SIZE);
		min_left = RIM_SIZE;

		if (shape == SHAPE_RECT)
			max_left = mWidth / 2;
		else
			max_left = mWidth - (mHeight - 2 * RIM_SIZE);
		if (isOpen) {
			frontRect_left = max_left;
			alpha = 255;
		} else {
			frontRect_left = RIM_SIZE;
			alpha = 0;
		}
		frontRect_left_begin = frontRect_left;

	}

	public int measureDimension(int defaultSize, int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
//		backRect = new Rect(slid/2+2, slid/2+3, 144 * frontRect_left / 172+mBitmapSwitch.getWidth()-slid/2-2, getMeasuredHeight()-slid/2-2);
//		int radius;
//		radius = backRect.height() / 2;
//		paint.setColor(color_theme);
////		paint.setAlpha(alpha);
//		canvas.drawRoundRect(new RectF(backRect), radius, radius, paint);

		paint.setAlpha(255);
		canvas.drawBitmap(mBitmapBackground, 0, 0, paint);

		canvas.drawBitmap(mBitmapBackgroundOpen,new Rect(0,0,frontRect_left+width2/2,mHeight), new Rect(0,0,frontRect_left+width2/2,mHeight), paint);

		Log.e("", "frontRect_left--->" + frontRect_left);
		canvas.drawBitmap(mBitmapSwitch, frontRect_left, slid / 2, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (slideable == false)
			return super.onTouchEvent(event);
		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			attemptClaimDrag();
			eventStartX = (int) event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			eventLastX = (int) event.getRawX();
			diffX = eventLastX - eventStartX;
			int tempX = diffX + frontRect_left_begin;
			tempX = (tempX > max_left ? max_left : tempX);
			tempX = (tempX < min_left ? min_left : tempX);
			if (tempX >= min_left && tempX <= max_left) {
				frontRect_left = tempX;
				alpha = (int) (255 * (float) tempX / (float) max_left);
				invalidateView();
			}
			break;
		case MotionEvent.ACTION_UP:
			int wholeX = (int) (event.getRawX() - eventStartX);
			frontRect_left_begin = frontRect_left;
			boolean toRight;
			toRight = (frontRect_left_begin > max_left / 2 ? true : false);
			if (Math.abs(wholeX) < 3) {
				toRight = !toRight;
			}
			moveToDest(toRight);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 通知父类不要拦截touch事件 Tries to claim the user's drag motion, and requests
	 * disallowing any ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		mParent = getParent();
		if (mParent != null) {
			// 通知父类不要拦截touch事件
			mParent.requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * draw again
	 */
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	/**
	 * Resize bitmap with @newHeight and @newWidth parameters
	 *
	 * @param bm
	 * @param newHeight
	 * @param newWidth
	 * @return
	 */
	private Bitmap getResizedBitmap(Bitmap bm, float newHeight, float newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	public void setOnSwitchButtonStateChangedListener(SwitchButtonStateChangedListener listener) {
		this.listener = listener;
	}

	public void moveToDest(final boolean toRight) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					listener.SwitchButtonStateChanged(true);
				} else {
					listener.SwitchButtonStateChanged(false);
				}
			}

		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (toRight) {
					while (frontRect_left <= max_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
						invalidateView();
						frontRect_left += 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					alpha = 255;
					frontRect_left = max_left;
					isOpen = true;
					if (listener != null)
						handler.sendEmptyMessage(1);
					frontRect_left_begin = max_left;

				} else {
					while (frontRect_left >= min_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
						invalidateView();
						frontRect_left -= 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					alpha = 0;
					frontRect_left = min_left;
					isOpen = false;
					if (listener != null)
						handler.sendEmptyMessage(0);
					frontRect_left_begin = min_left;

				}
			}
		}).start();

	}

	public boolean getState() {
		return isOpen;
	}
	
	public void setState(boolean isOpen) {
		this.isOpen = isOpen;
		initDrawingVal();
		invalidateView();
		if (listener != null)
			if (isOpen == true) {
				listener.SwitchButtonStateChanged(true);
			} else {
				listener.SwitchButtonStateChanged(false);
			}
	}

	public void setShapeType(int shapeType) {
		this.shape = shapeType;
	}

	public void setSlideable(boolean slideable) {
		this.slideable = slideable;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			this.isOpen = bundle.getBoolean("isOpen");
			state = bundle.getParcelable("instanceState");
		}
		super.onRestoreInstanceState(state);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("instanceState", super.onSaveInstanceState());
		bundle.putBoolean("isOpen", this.isOpen);
		return bundle;
	}
}
