package com.mqt.ganghuazhifu.view;

import java.util.ArrayList;

import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.activity.WelcomeActivity;
import com.mqt.ganghuazhifu.bean.City;
import com.mqt.ganghuazhifu.bean.Unit;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.orhanobut.logger.Logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * 滑动选择
 *
 * @author yang.lei
 */
public class ScrollerNumberPicker extends View {
    /**
     * 控件宽度
     */
    private float controlWidth;
    /**
     * 控件高度
     */
    private float controlHeight;
    /**
     * 是否滑动中
     */
    private boolean isScrolling = false;
    /**
     * 选择的内容
     */
    private ArrayList<ItemObject> itemList = new ArrayList<ScrollerNumberPicker.ItemObject>();
    /**
     * 设置数据
     */
    private ArrayList<City> cityList = new ArrayList<City>();
    /**
     * 设置数据
     */
    private ArrayList<Unit> unitList = new ArrayList<Unit>();


    /**
     * 触摸时按下的点
     **/
    PointF downP = new PointF();
    /**
     * 触摸时当前的点
     **/
    PointF curP = new PointF();
    OnSingleTouchListener onSingleTouchListener;

    /**
     * 按下的坐标
     */
    private int downY;
    /**
     * 按下的时间
     */
    private long downTime = 0;
    /**
     * 短促移动
     */
    private long goonTime = 200;
    /**
     * 短促移动距离
     */
    private int goonDistence = 100;
    /**
     * 画线画笔
     */
    private Paint linePaint;
    /**
     * 线的默认颜色
     */
    private int lineColor = 0xff000000;
    /**
     * 选中的时候字体
     */
    private float selectedFont = WelcomeActivity.Companion.getScreenwidth() / 23;
    /**
     * 默认字体
     */
    private float normalFont = selectedFont * 0.7f;
    /**
     * 单元格高度
     */
    private int unitHeight = 50;
    /**
     * 显示多少个内容
     */
    private int itemNumber = 7;
    /**
     * 默认字体颜色
     */
    private int normalColor = 0xff000000;
    /**
     * 选中时候的字体颜色
     */
    private int selectedColor = 0xffff0000;
    /**
     * 蒙板高度
     */
    private float maskHight = 48.0f;
    /**
     * 选择监听
     */
    private OnSelectListener onSelectListener;
    /**
     * 是否可用
     */
    private boolean isEnable = true;
    /**
     * 刷新界面
     */
    private static final int REFRESH_VIEW = 0x001;
    /**
     * 移动距离
     */
    private static final int MOVE_NUMBER = 5;
    /**
     * 是否允许选空
     */
    private boolean noEmpty = false;
    /**
     * 正在修改数据，避免ConcurrentModificationException异常
     */
    private boolean isClearing = false;

    private int type;

    public ScrollerNumberPicker(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public ScrollerNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollerNumberPicker(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnable)
            return true;
        int y = (int) event.getY();

        curP.x = event.getX();
        curP.y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isScrolling = true;
                downY = (int) event.getY();
                downTime = System.currentTimeMillis();
                downP.x = event.getX();
                downP.y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(y - downY);
                onSelectListener();
                break;
            case MotionEvent.ACTION_UP:

                if (Math.abs(downP.x - curP.x) <= 12 && Math.abs(downP.y - curP.y) <= 12) {
                    if (downP.x > 40 && downP.x < controlWidth - 40
                            && downP.y > (controlHeight / 2 - unitHeight / 2 + 4)
                            && downP.y < (controlHeight / 2 + unitHeight / 2 - 2)) {
                        onSingleTouch();
                    }
                } else {

                    // 移动距离的绝对值
                    int move = (y - downY);
                    move = move > 0 ? move : move * (-1);
                    // 判断段时间移动的距离
                    if (System.currentTimeMillis() - downTime < goonTime
                            && move > goonDistence) {
                        goonMove(y - downY);
                    } else {
                        actionUp(y - downY);
                    }
                    noEmpty();
                    isScrolling = false;

                }

                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawList(canvas);
        drawMask(canvas);
    }

    private synchronized void drawList(Canvas canvas) {
        if (isClearing)
            return;
        try {
            for (ItemObject itemObject : itemList) {
                itemObject.drawSelf(canvas);
            }
        } catch (Exception e) {
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (type) {
            case 1:
                controlWidth = WelcomeActivity.Companion.getScreenwidth()/2;
                break;
            case 2:
                controlWidth = WelcomeActivity.Companion.getScreenwidth();
                break;
        }
    }

    /**
     * 继续移动一定距离
     */
    private synchronized void goonMove(final int move) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int distence = 0;
                while (distence < unitHeight * MOVE_NUMBER) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    actionThreadMove(move > 0 ? distence : distence * (-1));
                    distence += 10;
                }
                actionUp(move > 0 ? distence - 10 : distence * (-1) + 10);
                noEmpty();
            }
        }).start();
    }

    /**
     * 不能为空，必须有选项
     */
    private void noEmpty() {
        if (!noEmpty)
            return;
        for (ItemObject item : itemList) {
            if (item.isSelected())
                return;
        }
        if (itemList.size() > 0) {
            int move = (int) itemList.get(0).moveToSelected();
            if (move < 0) {
                defaultMove(move);
            } else {
                defaultMove((int) itemList.get(itemList.size() - 1)
                        .moveToSelected());
            }
            for (ItemObject item : itemList) {
                if (item.isSelected()) {
                    if (onSelectListener != null)
                        onSelectListener.endSelect(item.id, item.itemText);
                    break;
                }
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData(int type) {
        isClearing = true;
        itemList.clear();

        this.type = type;

        switch (type) {
            case 1:
                if (cityList == null) {
                    cityList = new ArrayList<City>();
                }
                for (int i = 0; i < cityList.size(); i++) {
                    ItemObject itmItemObject = new ItemObject();
                    itmItemObject.id = i;
                    itmItemObject.itemText = cityList.get(i).CityName;
                    itmItemObject.x = 0;
                    itmItemObject.y = i * unitHeight;
                    itemList.add(itmItemObject);
                }
                break;
            case 2:
                if (unitList == null) {
                    unitList = new ArrayList<Unit>();
                }
                for (int i = 0; i < unitList.size(); i++) {
                    ItemObject itmItemObject = new ItemObject();
                    itmItemObject.id = i;
                    itmItemObject.itemText = unitList.get(i).getPayeeNm();
                    itmItemObject.x = 0;
                    itmItemObject.y = i * unitHeight;
                    itemList.add(itmItemObject);
                }
                break;
        }

        isClearing = false;
    }

    /**
     * 移动的时候
     *
     * @param move
     */
    private void actionMove(int move) {
        for (ItemObject item : itemList) {
            item.move(move);
        }
        invalidate();
    }

    /**
     * 移动，线程中调用
     *
     * @param move
     */
    private void actionThreadMove(int move) {
        for (ItemObject item : itemList) {
            item.move(move);
        }
        Message rMessage = new Message();
        rMessage.what = REFRESH_VIEW;
        handler.sendMessage(rMessage);
    }

    /**
     * 松开的时候
     *
     * @param move
     */
    private void actionUp(int move) {
        int newMove = 0;
        if (move > 0) {
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).isSelected()) {
                    newMove = (int) itemList.get(i).moveToSelected();
                    if (onSelectListener != null)
                        onSelectListener.endSelect(itemList.get(i).id,
                                itemList.get(i).itemText);
                    break;
                }
            }
        } else {
            for (int i = itemList.size() - 1; i >= 0; i--) {
                if (itemList.get(i).isSelected()) {
                    newMove = (int) itemList.get(i).moveToSelected();
                    if (onSelectListener != null)
                        onSelectListener.endSelect(itemList.get(i).id,
                                itemList.get(i).itemText);
                    break;
                }
            }
        }
        for (ItemObject item : itemList) {
            item.newY(move + 0);
        }
        slowMove(newMove);
        Message rMessage = new Message();
        rMessage.what = REFRESH_VIEW;
        handler.sendMessage(rMessage);
    }

    /**
     * 缓慢移动
     *
     * @param move
     */
    private synchronized void slowMove(final int move) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 判断正负
                int m = move > 0 ? move : move * (-1);
                int i = move > 0 ? 1 : (-1);
                // 移动速度
                int speed = 1;
                while (true) {
                    m = m - speed;
                    if (m <= 0) {
                        for (ItemObject item : itemList) {
                            item.newY(m * i);
                        }
                        Message rMessage = new Message();
                        rMessage.what = REFRESH_VIEW;
                        handler.sendMessage(rMessage);
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
//					for (ItemObject item : itemList) {
//						item.newY(speed * i);
//					}
                    for (int j = 0; j < itemList.size(); j++) {
                        ItemObject item = itemList.get(j);
                        item.newY(speed * i);
                    }
                    Message rMessage = new Message();
                    rMessage.what = REFRESH_VIEW;
                    handler.sendMessage(rMessage);
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (ItemObject item : itemList) {
                    if (item.isSelected()) {
                        if (onSelectListener != null)
                            onSelectListener.endSelect(item.id, item.itemText);
                        break;
                    }
                }

            }
        }).start();
    }

    /**
     * 移动到默认位置
     *
     * @param move
     */
    private void defaultMove(int move) {
        for (ItemObject item : itemList) {
            item.newY(move);
        }
        Message rMessage = new Message();
        rMessage.what = REFRESH_VIEW;
        handler.sendMessage(rMessage);
    }

    /**
     * 滑动监听
     */
    private void onSelectListener() {
        if (onSelectListener == null)
            return;
        for (ItemObject item : itemList) {
            if (item.isSelected()) {
                onSelectListener.selecting(item.id, item.itemText);
            }
        }
    }

    /**
     * 绘制线条
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        if (linePaint == null) {
            linePaint = new Paint();
            linePaint.setColor(lineColor);
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(1f);
        }
        canvas.drawLine(0, controlHeight / 2 - unitHeight / 2 + 2,
                controlWidth, controlHeight / 2 - unitHeight / 2 + 2, linePaint);
        canvas.drawLine(0, controlHeight / 2 + unitHeight / 2 - 2,
                controlWidth, controlHeight / 2 + unitHeight / 2 - 2, linePaint);


//		canvas.drawLine(40, (controlHeight / 2 + unitHeight / 2 - 2),
//				40, (controlHeight / 2 - unitHeight / 2 + 4), linePaint);
//		canvas.drawLine(controlWidth-40, (controlHeight / 2 + unitHeight / 2 - 2),
//				controlWidth-40, (controlHeight / 2 - unitHeight / 2 + 4), linePaint);
//		canvas.drawLine(40, (controlHeight / 2 + unitHeight / 2 - 2),
//				controlWidth-40, (controlHeight / 2 + unitHeight / 2 - 2), linePaint);
//		canvas.drawLine(40, (controlHeight / 2 - unitHeight / 2 + 4),
//				controlWidth-40, (controlHeight / 2 - unitHeight / 2 + 4), linePaint);
//
//		float x1 = 0;
//		float y1 = 0;
//		float x2 = 0;
//		float y2 = 0;
//		
//		for(int i = 0;i<=controlWidth-80+unitHeight-6;i=i+12) {
//			
//			if(i<unitHeight-6) {
//				x1 = 40;
//				y1 = (controlHeight / 2 - unitHeight / 2 + 4)+i;
//				x2 = 40+i;
//				y2 = (controlHeight / 2 - unitHeight / 2 + 4);
//			} else if(i<controlWidth-80) {
//				x1 = 40+i-unitHeight+6;
//				y1 = (controlHeight / 2 + unitHeight / 2 - 2);
//				x2 = 40+i;
//				y2 = (controlHeight / 2 - unitHeight / 2 + 4);
//			} else {
//				x1 = 40+i-unitHeight+6;
//				y1 = (controlHeight / 2 + unitHeight / 2 - 2);
//				x2 = controlWidth-40;
//				y2 = (controlHeight / 2 - unitHeight / 2 + 4)+i-controlWidth+80;
//			}
//			canvas.drawLine(x2, y2, x1, y1, linePaint);
//
//			
//		}


    }

    /**
     * 绘制遮盖板
     *
     * @param canvas
     */
    private void drawMask(Canvas canvas) {
        LinearGradient lg = new LinearGradient(0, 0, 0, maskHight, 0x00f2f2f2,
                0x00f2f2f2, TileMode.MIRROR);
        Paint paint = new Paint();
        paint.setShader(lg);
        canvas.drawRect(0, 0, controlWidth, maskHight, paint);
        LinearGradient lg2 = new LinearGradient(0, controlHeight - maskHight,
                0, controlHeight, 0x00f2f2f2, 0x00f2f2f2, TileMode.MIRROR);
        Paint paint2 = new Paint();
        paint2.setShader(lg2);
        canvas.drawRect(0, controlHeight - maskHight, controlWidth,
                controlHeight, paint2);
    }

    /**
     * 初始化，获取设置的属性
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray attribute = context.obtainStyledAttributes(attrs,
                R.styleable.NumberPicker);
        unitHeight = (int) attribute.getDimension(
                R.styleable.NumberPicker_unitHight, 32);
        itemNumber = attribute.getInt(R.styleable.NumberPicker_itemNumber, 7);
        normalColor = attribute.getColor(
                R.styleable.NumberPicker_normalTextColor, 0xff000000);
        selectedColor = attribute.getColor(
                R.styleable.NumberPicker_selecredTextColor, 0xffff0000);
        lineColor = attribute.getColor(R.styleable.NumberPicker_lineColor,
                0xff000000);
        maskHight = attribute.getDimension(R.styleable.NumberPicker_maskHight,
                48.0f);
        noEmpty = attribute.getBoolean(R.styleable.NumberPicker_noEmpty, false);
        isEnable = attribute
                .getBoolean(R.styleable.NumberPicker_isEnable, true);
        attribute.recycle();
        controlHeight = itemNumber * unitHeight;

        if (WelcomeActivity.Companion.getScreenwidth() == 0 || WelcomeActivity.Companion.getScreenhigh() == 0) {
            WelcomeActivity.Companion.setScreenwidth(EncryptedPreferencesUtils.getScreenSize()[0]);
            WelcomeActivity.Companion.setScreenhigh(EncryptedPreferencesUtils.getScreenSize()[1]);
        }
        Logger.i("screenwidth--->" + WelcomeActivity.Companion.getScreenwidth());
        Logger.i("screenhigh--->" + WelcomeActivity.Companion.getScreenhigh());

        /** 选中的时候字体 */
        selectedFont = WelcomeActivity.Companion.getScreenwidth() / 23;
        /** 默认字体 */
        normalFont = selectedFont * 0.7f;
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setCity(ArrayList<City> data) {
        this.cityList = data;
        initData(1);
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setUnit(ArrayList<Unit> data) {
        this.unitList = (ArrayList<Unit>) data;
        initData(2);
    }

    /**
     * 获取返回项
     *
     * @return
     */
    public int getSelected() {
        for (ItemObject item : itemList) {
            if (item.isSelected())
                return item.id;
        }
        return -1;
    }

    /**
     * 获取返回的内容
     *
     * @return
     */
    public String getSelectedText() {
        for (ItemObject item : itemList) {
            if (item.isSelected())
                return item.itemText;
        }
        return "";
    }

    /**
     * 是否正在滑动
     *
     * @return
     */
    public boolean isScrolling() {
        return isScrolling;
    }

    /**
     * 是否可用
     *
     * @return
     */
    public boolean isEnable() {
        return isEnable;
    }

    /**
     * 设置是否可用
     *
     * @param isEnable
     */
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * 设置默认选项
     *
     * @param index
     */
    public void setDefault(int index) {
        if (itemList != null && itemList.size() > 0) {
            float move = itemList.get(index).moveToSelected();
            defaultMove((int) move);
        }
    }

    /**
     * 获取列表大小
     *
     * @return
     */
    public int getListSize() {
        if (itemList == null)
            return 0;
        return itemList.size();
    }

    /**
     * 获取某项的内容
     *
     * @param index
     * @return
     */
    public String getItemText(int index) {
        if (itemList == null)
            return "";
        return itemList.get(index).itemText;
    }

    /**
     * 监听
     *
     * @param onSelectListener
     */
    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_VIEW:
                    invalidate();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 单条内容
     *
     * @author zoudong
     */
    private class ItemObject {
        /**
         * id
         */
        public int id = 0;
        /**
         * 内容
         */
        public String itemText = "";
        /**
         * x坐标
         */
        public int x = 0;
        /**
         * y坐标
         */
        public int y = 0;
        /**
         * 移动距离
         */
        public int move = 0;
        /**
         * 字体画笔
         */
        private Paint textPaint;
        /**
         * 字体范围矩形
         */
        private Rect textRect;

        public ItemObject() {
            super();
        }

        /**
         * 绘制自身
         *
         * @param canvas
         */
        public void drawSelf(Canvas canvas) {
            if (textPaint == null) {
                textPaint = new Paint();
                textPaint.setAntiAlias(true);
            }
            if (textRect == null)
                textRect = new Rect();
            // 判断是否被选择
            if (isSelected()) {
                textPaint.setColor(selectedColor);
                // 获取距离标准位置的距离
                float moveToSelect = moveToSelected();
                moveToSelect = moveToSelect > 0 ? moveToSelect : moveToSelect
                        * (-1);
                // 计算当前字体大小
                float textSize = (float) normalFont
                        + ((float) (selectedFont - normalFont) * (1.0f - (float) moveToSelect
                        / (float) unitHeight));
                textPaint.setTextSize(textSize);
            } else {
                textPaint.setColor(normalColor);
                textPaint.setTextSize(normalFont);
            }
            // 返回包围整个字符串的最小的一个Rect区域
            textPaint.getTextBounds(itemText, 0, itemText.length(), textRect);
            // 判断是否可视
            if (!isInView())
                return;
            // 绘制内容
            canvas.drawText(itemText, x + controlWidth / 2 - textRect.width()
                            / 2, y + move + unitHeight / 2 + textRect.height() / 2,
                    textPaint);
        }

        /**
         * 是否在可视界面内
         *
         * @return
         */
        public boolean isInView() {
            if (y + move > controlHeight
                    || (y + move + unitHeight / 2 + textRect.height() / 2) < 0)
                return false;
            return true;
        }

        /**
         * 移动距离
         *
         * @param _move
         */
        public void move(int _move) {
            this.move = _move;
        }

        /**
         * 设置新的坐标
         */
        public void newY(int _move) {
            this.move = 0;
            this.y = y + _move;
        }

        /**
         * 判断是否在选择区域内
         *
         * @return
         */
        public boolean isSelected() {
            if ((y + move) >= controlHeight / 2 - unitHeight / 2 + 2
                    && (y + move) <= controlHeight / 2 + unitHeight / 2 - 2)
                return true;
            if ((y + move + unitHeight) >= controlHeight / 2 - unitHeight / 2
                    + 2
                    && (y + move + unitHeight) <= controlHeight / 2
                    + unitHeight / 2 - 2)
                return true;
            if ((y + move) <= controlHeight / 2 - unitHeight / 2 + 2
                    && (y + move + unitHeight) >= controlHeight / 2
                    + unitHeight / 2 - 2)
                return true;
            return false;
        }

        /**
         * 获取移动到标准位置需要的距离
         */
        public float moveToSelected() {
            return (controlHeight / 2 - unitHeight / 2) - (y + move);
        }
    }

    /**
     * 选择监听监听
     *
     * @author zoudong
     */
    public interface OnSelectListener {
        /**
         * 结束选择
         *
         * @param id
         * @param text
         */
        public void endSelect(int id, String text);

        /**
         * 选中的内容
         *
         * @param id
         * @param text
         */
        public void selecting(int id, String text);

    }

    /**
     * 单击
     *
     * @author yang.lei
     */
    public void onSingleTouch() {
        if (onSingleTouchListener != null) {
            onSingleTouchListener.onSingleTouch();
        }
    }

    /**
     * 创建点击事件接口
     *
     * @author yang.lei
     */
    public interface OnSingleTouchListener {
        public void onSingleTouch();
    }

    public void setOnSingleTouchListener(
            OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }

    /**
     * 仅用于当城市没有燃气公司获取type=0时，用于刷新燃气公司滑动的view
     *
     * @author Uncle_Sun
     */
    public void refresh() {
        invalidate();
    }

}
