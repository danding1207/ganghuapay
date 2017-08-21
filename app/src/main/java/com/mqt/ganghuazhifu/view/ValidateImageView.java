package com.mqt.ganghuazhifu.view;

import java.util.Random;

import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 生成验证码图片
 * @author yang.lei
 *
 */
public class ValidateImageView extends View{
	private String TAG = "ValidateImageView";
	private Paint paint = new Paint();
	int width;
	int height;
	/*
	 * 验证码内容
	 */
	private String[] content = null;
	/*
	 * 验证码图片
	 */
	private Bitmap bitmap = null;

	public ValidateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		width = UnitConversionUtils.dipTopx(getContext(), 80);
		height = UnitConversionUtils.dipTopx(getContext(), 30);
	}
	
	public ValidateImageView(Context context) {
		super(context);
		width = UnitConversionUtils.dipTopx(getContext(), 80);
		height = UnitConversionUtils.dipTopx(getContext(), 30);
	}

	@Override
	public void draw(Canvas canvas) {
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, paint);
		} else {
			paint.setColor(Color.GRAY);
			paint.setTextSize(20);
			canvas.drawText("点击换一换", 10, 30, paint);
		}
		paint.setColor(Color.BLACK);
//		canvas.drawLine(1, 0, 1, height, paint);
//		canvas.drawLine(width-1, 0, width-1, height, paint);
//		canvas.drawLine(0, 1, width, 1, paint);
//		canvas.drawLine(0, height-1, width, height-1, paint);
		super.draw(canvas);
	}

	/**
	 * 得到验证码；设置图片
	 * @param strContent 验证码的字符串数组
	 * @return
	 */
	public String getValidataAndSetImage(String[] strContent) {
		content = strContent;
		//产生随机数，并返回
		String strRes = generageRadomStr();
//		Log.i(TAG, "generate validate code: " + strRes);
//		String strRes = generageRadomStr(strContent);
		//传随机串和随机数
		bitmap = generateValidate(content,strRes);
		invalidate();
		return strRes;
	}

	private Bitmap generateValidate(String[] strContent,String strRes) {
		int isRes = isStrContent(strContent);
		if (isRes == 0) {
			return null;
		}
		Bitmap sourceBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(sourceBitmap);
		
		Paint p = new Paint();
		p.setTextSize(height / 2);
		p.setFakeBoldText(true);
		
		int y = (width-4*14)/5;
		
		
		p.setColor(getResources().getColor(R.color.slow_gray));
		canvas.drawARGB(255,216, 216, 216);
		
//		canvas.rotate(getRandomDegree(), width/2, height / 2);
		
		p.setColor(getResources().getColor(R.color.dark_blue));
		canvas.drawText(String.valueOf(strRes.charAt(0)), y, height/2+16, p);
		
//		canvas.rotate(getRandomDegree(), width/2, height / 2);
//		p.setColor(getRandColor(200, 230, 170));
		canvas.drawText(String.valueOf(strRes.charAt(1)), y*2+10, height/2+16, p);
		
//		canvas.rotate(getRandomDegree(), width/2, height / 2);
//		p.setColor(getRandColor(200, 230, 170));
		canvas.drawText(String.valueOf(strRes.charAt(2)), y*3+20, height/2+16, p);
		
//		canvas.rotate(getRandomDegree(), width/2, height / 2);
//		p.setColor(getRandColor(200, 230, 170));
		canvas.drawText(String.valueOf(strRes.charAt(3)), y*4+30, height/2+16, p);
		
		//障碍设置
//		int startX = 0,startY = 0,stopX = 0,stopY = 0;
//		for (int i = 0; i < 55; i++) {
//			startX = pointRadom(width);
//			startY = pointRadom(height);
//			stopX = pointRadom(15);
//			stopY = pointRadom(15);
//			p.setColor(getRandColor(200, 230, 220));
//			canvas.drawLine(startX, startY - 20, startX + stopX, startY + stopY - 20, p);
//		}
		
		
		
		canvas.save();
		return sourceBitmap;
	}
	
	private int isStrContent(String[] strContent) {
		if (strContent == null || strContent.length <= 0) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public int getRandomDegree()
	{
		Random random = new Random();
		int num = random.nextInt(5);
		int she = random.nextInt(10);
		int b = (she>5)?-1:1;
//		Log.i("ASD", "num--->"+(num+b*5));
		return num+b*5;
	}
	
	/**
	 * 从指定数组中随机取出4个字符(数组)
	 * @param strContent
	 * @return
	 */
	private String[] generageRadom(String[] strContent){
		String[] str = new String[4];
		// 随机串的个数
		int count = strContent.length;
		// 生成4个随机数
		Random random = new Random();
		int randomResFirst = random.nextInt(count);
		int randomResSecond = random.nextInt(count);
		int randomResThird = random.nextInt(count);
		int randomResFourth = random.nextInt(count);
		
		str[0] = strContent[randomResFirst].toString().trim();
		str[1] = strContent[randomResSecond].toString().trim();
		str[2] = strContent[randomResThird].toString().trim();
		str[3] = strContent[randomResFourth].toString().trim();
		return str;
	}
	
	/**
	 * 从指定数组中随机取出4个字符(字符串)
	 * @param strContent
	 * @return
	 */
	private String generageRadomStr(){
		StringBuilder str = new StringBuilder();
		// 随机串的个数
		int count = CODE.length;
		// 生成4个随机数
		Random random = new Random();
		int randomResFirst = random.nextInt(count);
		int randomResSecond = random.nextInt(count);
		int randomResThird = random.nextInt(count);
		int randomResFourth = random.nextInt(count);
		
		str.append(CODE[randomResFirst].toString().trim());
		str.append(CODE[randomResSecond].toString().trim());
		str.append(CODE[randomResThird].toString().trim());
		str.append(CODE[randomResFourth].toString().trim());
		return str.toString();
	}
	
	private int pointRadom(int n){
		Random r = new Random();
		return r.nextInt(n);
	}

	private static final String[] CODE = {"0","1","2","3","4","5","6","7",
			"8","9","A","B","C","D","E","F","G","H","I","J",
			"K","L","M","N","O","P","Q","R","S","T","U","V",
			"W","X","Y","Z"};
	
	/**
	 * 给定范围获得随机颜色
	 * 
	 * @param rc
	 *            0-255
	 * @param gc
	 *            0-255
	 * @param bc
	 *            0-255
	 * @return colorValue 颜色值，使用setColor(colorValue)
	 */
	public int getRandColor(int rc, int gc, int bc) {
		Random random = new Random();
		if (rc > 255)
			rc = 255;
		if (gc > 255)
			gc = 255;
		if (bc > 255)
			bc = 255;
		int r = rc + random.nextInt(rc);
		int g = gc + random.nextInt(gc);
		int b = bc + random.nextInt(bc);
		return Color.rgb(r, g, b);
	}
}