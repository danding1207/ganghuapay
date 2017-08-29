package com.mqt.ganghuazhifu;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.hwangjr.rxbus.RxBus;
import com.mqt.ganghuazhifu.activity.CaptureActivity;
import com.mqt.ganghuazhifu.activity.MessageCenterActivity;
import com.mqt.ganghuazhifu.activity.ReLockPatternActivity;
import com.mqt.ganghuazhifu.activity.SelectUnityListActivity;
import com.mqt.ganghuazhifu.adapter.ViewPagerMainAdapter;
import com.mqt.ganghuazhifu.bean.User;
import com.mqt.ganghuazhifu.dao.MessageDao;
import com.mqt.ganghuazhifu.dao.MessageDaoImpl;
import com.mqt.ganghuazhifu.event.RunningInfoEvent;
import com.mqt.ganghuazhifu.fragment.AccountFragment;
import com.mqt.ganghuazhifu.fragment.HomePageFragment;
import com.mqt.ganghuazhifu.fragment.MoreFragment;
import com.mqt.ganghuazhifu.fragment.RecordFragment;
import com.mqt.ganghuazhifu.receiver.Features;
import com.mqt.ganghuazhifu.service.RunningInfoService;
import com.mqt.ganghuazhifu.utils.DataBaiduPush;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.mqt.ganghuazhifu.utils.ScreenManager;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import com.hyphenate.chat.ChatClient;
//import com.hyphenate.helpdesk.callback.Callback;

public class MainActivity extends BaseActivity implements OnPageChangeListener {

    public static int NORMALVIEWHOLDER = 8;
    private static boolean isExit = false;
    public static Toolbar toolbar;
    public static TextView tv_title, tv_title_right;
    public static ImageView ib_pic_right, ib_pic_left;
    public static AHBottomNavigation bottom_navigation;
    public static ViewPager viewPager;
    private List<Fragment> fragments;
    public static RecordFragment recordFragment;
    public static HomePageFragment homePageFragment;
    public static MoreFragment moreFragment;
    public static AccountFragment accountFragment;
    private ViewPagerMainAdapter pagerMainAdapter;
    private Handler handler;
    public static boolean isShowPopupWindow = false;
    public static boolean isNewMessage = false;
    private Intent bindIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        BindService();
        registerScreenACTIONSCREENONReceiver();
        registerScreenACTIONSCREENOFFReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.Companion.setAppCount(App.Companion.getAppCount() + 1);
        Logger.t("LockPattern").i("App--->MainActivity:onActivityStarted+" + App.Companion.getAppCount());
        RxBus.get().post(new RunningInfoEvent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.Companion.setAppCount(App.Companion.getAppCount() - 1);
        Logger.t("LockPattern").i("App--->MainActivity:onActivityStoped+" + App.Companion.getAppCount());
        RxBus.get().post(new RunningInfoEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        accountFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void BindService() {
        bindIntent = new Intent(MainActivity.this, RunningInfoService.class);
        startService(bindIntent);
//        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title_right = (TextView) findViewById(R.id.tv_title_right);
        ib_pic_right = (ImageView) findViewById(R.id.ib_pic_right);
        ib_pic_left = (ImageView) findViewById(R.id.ib_pic_left);
        viewPager = (ViewPager) findViewById(R.id.viewPager_main);
        bottom_navigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.login_logo_top);
        if (logo != null)
            logo.setBounds(0, 0, UnitConversionUtils.dipTopx(this, 34), UnitConversionUtils.dipTopx(this, 34));
        tv_title.setCompoundDrawables(logo, null, null, null); // 设置左图

        tv_title_right.setVisibility(View.GONE);

        viewPager.setOffscreenPageLimit(4);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.main_menu_main, R.drawable.main_home, R.color.blue_bg);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.main_menu_recorder, R.drawable.main_recoder, R.color.blue_bg);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.main_menu_account, R.drawable.main_account, R.color.blue_bg);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.main_menu_more, R.drawable.main_more, R.color.blue_bg);
        // Add items
        bottom_navigation.addItem(item1);
        bottom_navigation.addItem(item2);
        bottom_navigation.addItem(item3);
        bottom_navigation.addItem(item4);

        // Set background color
        bottom_navigation.setDefaultBackgroundColor(Color.parseColor("#F5F5F5"));

        // Disable the translation inside the CoordinatorLayout
        bottom_navigation.setBehaviorTranslationEnabled(false);

        // Change colors
        bottom_navigation.setAccentColor(ContextCompat.getColor(this, R.color.blue_bg));//xuanzhong
        bottom_navigation.setInactiveColor(Color.parseColor("#747474"));//

        // Force to tint the drawable (useful for font with icon for example)
        bottom_navigation.setForceTint(true);

        // Force the titles to be displayed (against Material Design guidelines!)
        bottom_navigation.setForceTitlesDisplay(true);

        // Use colored navigation with circle reveal effect
        bottom_navigation.setColored(false);

        // Set current item programmatically
        bottom_navigation.setCurrentItem(0);

        // Customize notification (title, background, typeface)
        bottom_navigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        // Add or remove notification for each item
        bottom_navigation.setNotification("", 1);

        // Set listeners
        bottom_navigation.setOnTabSelectedListener((position, wasSelected) -> {
            viewPager.setCurrentItem(position, false);
            if (isShowPopupWindow) {
                isShowPopupWindow = false;
                if (position == 1)
                    tv_title_right.setText("筛选");
                handler.sendEmptyMessage(2);
            }
            notifyTitleView(position);
            return true;
        });
        User user = EncryptedPreferencesUtils.getUser();
        if ("0".equals(user.getFunction1())) {
            bottom_navigation.setNotification(" ", 2);
        }
        fragments = new ArrayList<>();
        homePageFragment = new HomePageFragment();
        homePageFragment.newInstence();
        recordFragment = new RecordFragment();
        recordFragment.newInstence(tv_title_right);
        accountFragment = new AccountFragment();
        accountFragment.newInstence();
        moreFragment = new MoreFragment();
        moreFragment.newInstence();
        handler = recordFragment.getHandler();
        fragments.add(homePageFragment);
        fragments.add(recordFragment);
        fragments.add(accountFragment);
        fragments.add(moreFragment);
        pagerMainAdapter = new ViewPagerMainAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerMainAdapter);
        viewPager.addOnPageChangeListener(this);
        initUnityDialog();
    }

    private void initUnityDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("提醒")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .onPositive((dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, SelectUnityListActivity.class);
                    intent.putExtra("TYPE", 2);
                    startActivity(intent);
                })
                .positiveText("确定")
                .content("请您选择常用缴费单位！");
        User user = EncryptedPreferencesUtils.getUser();
        if (user != null && user.getAscriptionFlag() != null && "11".equals(user.getAscriptionFlag())) {
            builder.build().show();
        }
    }

    protected void notifyTitleView(int pager) {
        switch (pager) {
            case 0:
                if (isNewMessage) {
                    ib_pic_right.setImageResource(R.drawable.message_new);
                } else {
                    ib_pic_right.setImageResource(R.drawable.message);
                }
                ib_pic_right.setVisibility(View.VISIBLE);
                tv_title_right.setVisibility(View.GONE);
                ib_pic_left.setVisibility(View.GONE);
                ib_pic_left.setOnClickListener(null);
                ib_pic_right.setOnClickListener(arg0 -> startActivity(new Intent(MainActivity.this, MessageCenterActivity.class)));
                if (homePageFragment.list.size() == 0) {
                    homePageFragment.initAdvertisementViews();
                }
                getSupportActionBar().show();
                if (DataBaiduPush.getGOW() != 0) {
                    if (DataBaiduPush.getGOW() == 1) {
                        tv_title.setText("燃气业务");
                    } else if (DataBaiduPush.getGOW() == 2) {
                        tv_title.setText("水务业务");
                    }
                    tv_title.setCompoundDrawables(null, null, null, null); // 设置左图
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    Drawable logo = ContextCompat.getDrawable(this, R.drawable.login_logo_top);
                    logo.setBounds(0, 0, UnitConversionUtils.dipTopx(this, 34), UnitConversionUtils.dipTopx(this, 34));
                    tv_title.setCompoundDrawables(logo, null, null, null); // 设置左图
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    tv_title.setText("港华交易宝");
                }
                break;
            case 1:
                recordFragment.initdata();
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                tv_title.setCompoundDrawables(null, null, null, null); // 设置左图
                ib_pic_right.setVisibility(View.GONE);
                ib_pic_left.setVisibility(View.GONE);
                ib_pic_left.setOnClickListener(null);
                tv_title_right.setVisibility(View.VISIBLE);
                tv_title.setText("交易记录");
                tv_title_right.setText("筛选");
                tv_title_right.setOnClickListener(v -> {
                    if (isShowPopupWindow) {
                        isShowPopupWindow = false;
                        tv_title_right.setText("筛选");
                        handler.sendEmptyMessage(3);
                    } else {
                        isShowPopupWindow = true;
                        tv_title_right.setText("确定");
                        handler.sendEmptyMessage(1);
                    }
                });
                break;
            case 2:
                accountFragment.initData();
                accountFragment.downLoadFile();
//                getSupportActionBar().hide();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                tv_title.setCompoundDrawables(null, null, null, null); // 设置左图
                ib_pic_right.setVisibility(View.GONE);
                ib_pic_left.setVisibility(View.GONE);
                ib_pic_left.setOnClickListener(null);
                tv_title_right.setVisibility(View.GONE);
                tv_title.setText("账户管理");
                break;
            case 3:
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                tv_title.setCompoundDrawables(null, null, null, null); // 设置左图
                ib_pic_right.setVisibility(View.VISIBLE);
                ib_pic_left.setVisibility(View.INVISIBLE);
                tv_title_right.setVisibility(View.GONE);
                tv_title.setText("更多");
                ib_pic_right.setImageResource(R.drawable.rightmore);
                ib_pic_right.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CaptureActivity.class)));
                break;
        }
    }

    @Override
    public void OnViewClick(View v) {
    }

    private void registerScreenACTIONSCREENOFFReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
        }
    };

    private void registerScreenACTIONSCREENONReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver2, filter);
    }

    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override //
        public void onReceive(final Context context, final Intent intent) {
            User user = EncryptedPreferencesUtils.getUser();
            if (user != null && !TextUtils.isEmpty(user.getGesturePwd()) && !user.getGesturePwd().equals("9DD4E461268C8034F5C8564E155C67A6") && Features.isFront && !ScreenManager.getScreenManager().isContainActivity(ReLockPatternActivity.class) && ScreenManager.getScreenManager().isContainActivity(MainActivity.class)) {
                Intent intent2 = new Intent(getApplicationContext(), ReLockPatternActivity.class);
                startActivity(intent2);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowPopupWindow && viewPager.getCurrentItem() == 1) {
                isShowPopupWindow = false;
                tv_title_right.setText("筛选");
                handler.sendEmptyMessage(2);
                return false;
            }
            if (HomePageFragment.isShowSecondMenu && viewPager.getCurrentItem() == 0) {
                homePageFragment.resetVisibility(false);
                Drawable logo = ContextCompat.getDrawable(this, R.drawable.login_logo_top);
                logo.setBounds(0, 0, UnitConversionUtils.dipTopx(this, 34), UnitConversionUtils.dipTopx(this, 34));
                tv_title.setCompoundDrawables(logo, null, null, null); // 设置左图
                if (isNewMessage) {
                    ib_pic_right.setImageResource(R.drawable.message_new);
                } else {
                    ib_pic_right.setImageResource(R.drawable.message);
                }
                ib_pic_right.setVisibility(View.VISIBLE);
                tv_title_right.setVisibility(View.GONE);
                ib_pic_right.setOnClickListener(arg0 -> startActivity(new Intent(MainActivity.this, MessageCenterActivity.class)));

                tv_title.setText("港华交易宝");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return false;
            }

            if (!isExit) {
                isExit = true;
                ToastUtil.Companion.toastInfo(getResources().getString(R.string.exit));
                mHandler.sendEmptyMessageDelayed(0, 5000);
            } else {
//                ScreenManager.getScreenManager().popAllActivity();
                moveTaskToBack(true);
                return true;
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onResume() {
        if (viewPager.getCurrentItem() == 2) {
            accountFragment.initView();
        }
        if (viewPager.getCurrentItem() == 1 || viewPager.getCurrentItem() == 0) {
            recordFragment.initdata();
        }
        isNewMessage = false;

        MessageDao messageDao = new MessageDaoImpl(this);
        try {
            isNewMessage = messageDao.hasUnreadMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (viewPager.getCurrentItem() == 0) {
            if (isNewMessage) {
                ib_pic_right.setImageResource(R.drawable.message_new);
            } else {
                ib_pic_right.setImageResource(R.drawable.message);
            }
            ib_pic_right.setVisibility(View.VISIBLE);
            ib_pic_right.setOnClickListener(arg0 -> startActivity(new Intent(MainActivity.this, MessageCenterActivity.class)));
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopService(bindIntent);
//        viewPager = null;
//        toolbar = null;
//        bottom_navigation = null;
        unregisterReceiver(receiver);
        unregisterReceiver(receiver2);
        super.onDestroy();
    }

    @Override
    public void onPageScrollStateChanged(int page) {
        Logger.d("onPageScrollStateChanged:page--->" + page);
    }

    @Override
    public void onPageScrolled(int page, float arg1, int arg2) {
        Logger.d("onPageScrolled:page--->" + page);
    }

    @Override
    public void onPageSelected(int page) {
        Logger.i("onPageSelected:page--->" + page);
        switch (page) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
        bottom_navigation.setCurrentItem(page);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //主界面左上角的icon点击反应
                homePageFragment.resetVisibility(false);
                Drawable logo = ContextCompat.getDrawable(this, R.drawable.login_logo_top);
                logo.setBounds(0, 0, UnitConversionUtils.dipTopx(this, 34), UnitConversionUtils.dipTopx(this, 34));
                tv_title.setCompoundDrawables(logo, null, null, null); // 设置左图
                if (isNewMessage) {
                    ib_pic_right.setImageResource(R.drawable.message_new);
                } else {
                    ib_pic_right.setImageResource(R.drawable.message);
                }
                ib_pic_right.setVisibility(View.VISIBLE);
                tv_title_right.setVisibility(View.GONE);
                ib_pic_right.setOnClickListener(arg0 -> startActivity(new Intent(MainActivity.this, MessageCenterActivity.class)));
                tv_title.setText("港华交易宝");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
        }
        return true;
    }

    private static final String STATE_EXIT = "isExit";
    private static final String STATE_PopupWindow = "popupWindow";
    private static final String STATE_isNewMessage = "isNewMessage";

    @Override
    public void onActivitySaveInstanceState(@NotNull Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_EXIT, isExit);
        savedInstanceState.putBoolean(STATE_PopupWindow, isShowPopupWindow);
        savedInstanceState.putBoolean(STATE_isNewMessage, isNewMessage);
    }

    @Override
    public void onActivityRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        if (fragments == null) {
            fragments = new ArrayList<>();
            if (homePageFragment == null) {
                homePageFragment = new HomePageFragment();
                homePageFragment.newInstence();
            }
            if (recordFragment == null) {
                recordFragment = new RecordFragment();
                recordFragment.newInstence(tv_title_right);
            }
            if (accountFragment == null) {
                accountFragment = new AccountFragment();
                accountFragment.newInstence();
            }
            if (moreFragment == null) {
                moreFragment = new MoreFragment();
                moreFragment.newInstence();
            }
            if (handler == null)
                handler = recordFragment.getHandler();
            fragments.add(homePageFragment);
            fragments.add(recordFragment);
            fragments.add(accountFragment);
            fragments.add(moreFragment);
        }
        if (pagerMainAdapter == null)
            pagerMainAdapter = new ViewPagerMainAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerMainAdapter);
        isExit = savedInstanceState.getBoolean(STATE_EXIT);
        isShowPopupWindow = savedInstanceState.getBoolean(STATE_PopupWindow);
        isNewMessage = savedInstanceState.getBoolean(STATE_isNewMessage);
    }
}
