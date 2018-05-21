package com.mqt.ganghuazhifu.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.mqt.ganghuazhifu.MainActivity;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.activity.MovableActivity;
import com.mqt.ganghuazhifu.activity.PayTheGasFeeActivity;
import com.mqt.ganghuazhifu.activity.WelcomeActivity;
import com.mqt.ganghuazhifu.adapter.NetworkImageHolderView;
import com.mqt.ganghuazhifu.bean.BeanBinnal;
import com.mqt.ganghuazhifu.event.ConstantKotlin;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener;
import com.mqt.ganghuazhifu.utils.DataBaiduPush;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.orhanobut.logger.Logger;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * 首页
 *
 * @author yang.lei
 * @date 2014-12-24
 *
 * 2018-02-06（yang.lei）：整合优化各个界面传递订单类型的方式-枚举；修改首页轮播图样式
 *
 *
 */
public class HomePageFragment extends BaseFragment implements OnItemClickListener {

    private MZBannerView convenientBanner;
    public static ArrayList<BeanBinnal> list;
    private RelativeLayout rl_gas_gasbill;
    private CardView cardView_gas_or_water, cardView_water;
    private LinearLayout ll_gas_gasbill, ll_water, lay_gas, lay_gasbill;
    public static boolean isShowSecondMenu = false;
    public Activity activity;

    public void newInstence(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, null);
        convenientBanner = (MZBannerView) view.findViewById(R.id.convenientBanner);
        ll_gas_gasbill = (LinearLayout) view.findViewById(R.id.ll_gas_gasbill);
        cardView_gas_or_water = (CardView) view.findViewById(R.id.cardView_gas_or_water);
        lay_gas = (LinearLayout) view.findViewById(R.id.lay_gas);
        ll_water = (LinearLayout) view.findViewById(R.id.ll_water);
        lay_gasbill = (LinearLayout) view.findViewById(R.id.lay_gasbill);
        cardView_water = (CardView) view.findViewById(R.id.cardView_water);
        rl_gas_gasbill = (RelativeLayout) view.findViewById(R.id.rl_gas_gasbill);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                UnitConversionUtils.getScreenWidth(getActivity()),
                (int) getResources().getDimension(R.dimen.home_menu_height));
        rl_gas_gasbill.setLayoutParams(params);

        if (WelcomeActivity.Companion.getScreenwidth() == 0 || WelcomeActivity.Companion.getScreenhigh() == 0) {
            WelcomeActivity.Companion.setScreenwidth(EncryptedPreferencesUtils.getScreenSize()[0]);
            WelcomeActivity.Companion.setScreenhigh(EncryptedPreferencesUtils.getScreenSize()[1]);
        }
        int height = (int) ((float) WelcomeActivity.Companion.getScreenwidth() * 429 / 572);
        LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(
                WelcomeActivity.Companion.getScreenwidth(),
                height);
        paramsx.topMargin = UnitConversionUtils.dipTopx(getActivity(), 12);
//        paramsx.leftMargin = UnitConversionUtils.dipTopx(getActivity(), 14);
//        paramsx.rightMargin = UnitConversionUtils.dipTopx(getActivity(), 14);
        paramsx.bottomMargin = UnitConversionUtils.dipTopx(getActivity(), 12);
        convenientBanner.setLayoutParams(paramsx);
//        convenientBanner.stopTurning();
//        convenientBanner.startTurning(3000);
        //设置BannerView 的切换时间间隔
        convenientBanner.setDelayedTime(4000);
        //设置ViewPager（Banner）切换速度
        convenientBanner.setDuration(4000);

        setViewClick(view, R.id.button_gas);
        setViewClick(view, R.id.button_water);
        setViewClick(view, R.id.button_gas_fee);
        setViewClick(view, R.id.button_busi_fee);
        setViewClick(view, R.id.button_gas_bill);
        setViewClick(view, R.id.button_busi_bill);
        setViewClick(view, R.id.button_water_fee);
        setViewClick(view, R.id.button_water_bill);
        setViewClick(view, R.id.button_gas_blue_jine);// 金额表 蓝牙
        setViewClick(view, R.id.button_gas_nfc_qiliang);// 气量表 NFC
        setViewClick(view, R.id.button_gas_blue_qiliang);// 气量表 蓝牙

        list = new ArrayList<>();
        initAdvertisementViews();
        return view;
    }

    public void initAdvertisementViews() {
        CusFormBody body = HttpRequestParams.INSTANCE.getParamsForAdvertisement(null, "03");
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getProcessQuery(), false, "Advertisement", body,
                new OnHttpRequestListener() {
                    @Override
                    public void OnCompleted(Boolean isError, JSONObject response, int type, IOException error) {
                        if (isError) {
                            Logger.e(error.toString());
                        } else {
                            Logger.i(response.toString());
                            JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                            String ResponseFields = response.getString("ResponseFields");
                            String ProcessCode = ResponseHead.getString("ProcessCode");
                            if (ProcessCode.equals("0000") && ResponseFields != null) {
                                list = (ArrayList<BeanBinnal>) JSONObject.parseArray(ResponseFields, BeanBinnal.class);
                                for (BeanBinnal bean : list) {
                                    if (bean.comval.contains("prize")) {
                                        ArrayList<BeanBinnal> s = new ArrayList<>();
                                        s.add(bean);

                                        convenientBanner.setPages(s, new MZHolderCreator<NetworkImageHolderView>() {
                                            @Override
                                            public NetworkImageHolderView createViewHolder() {
                                                return new NetworkImageHolderView();
                                            }
                                        });
                                        convenientBanner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
                                            @Override
                                            public void onPageClick(View view, int position) {
                                                startActivity(new Intent(getActivity(), MovableActivity.class));
                                            }
                                        });
                                        convenientBanner.start();
//                                        convenientBanner.setPages(
//                                                new CBViewHolderCreator<NetworkImageHolderView>() {
//                                                    @Override
//                                                    public NetworkImageHolderView createHolder() {
//                                                        return new NetworkImageHolderView();
//                                                    }
//                                                }, s)
//                                                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//                                                .setPageIndicator(new int[]{R.drawable.dot_while, R.drawable.dot_blue})
//                                                //设置指示器的方向
//                                                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
////                .setOnPageChangeListener(this)//监听翻页事件
//                                                .setOnItemClickListener(HomePageFragment.this);
                                        return;
                                    }
                                }
//                                convenientBanner.setPages(
//                                        new CBViewHolderCreator<NetworkImageHolderView>() {
//                                            @Override
//                                            public NetworkImageHolderView createHolder() {
//                                                return new NetworkImageHolderView();
//                                            }
//                                        }, list)
//                                        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//                                        .setPageIndicator(new int[]{R.drawable.dot_while, R.drawable.dot_blue})
//                                        //设置指示器的方向
//                                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
////                .setOnPageChangeListener(this)//监听翻页事件
//                                        .setOnItemClickListener(null);

                                convenientBanner.setPages(list, new MZHolderCreator<NetworkImageHolderView>() {
                                    @Override
                                    public NetworkImageHolderView createViewHolder() {
                                        return new NetworkImageHolderView();
                                    }
                                });

                                convenientBanner.setBannerPageClickListener(null);
                                convenientBanner.start();
                            }
                        }
                    }
                });
    }

    @Override
    public void OnViewClick(View v) {
        // 010001 气费
        // 010002 营业费
        // 020001 水费
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_gas:
                isShowSecondMenu = true;
                ll_water.setVisibility(View.INVISIBLE);
                moveToLeft(cardView_gas_or_water, rl_gas_gasbill, UnitConversionUtils.getScreenWidth(getActivity()));
                MainActivity.tv_title.setCompoundDrawables(null, null, null, null); // 设置左图
                MainActivity.tv_title.setText("燃气");
                DataBaiduPush.setGOW(1);
                ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.button_water:
                isShowSecondMenu = true;
                if (ll_gas_gasbill != null)
                    ll_gas_gasbill.setVisibility(View.INVISIBLE);
                if (ll_gas_gasbill != null && cardView_gas_or_water != null)
                    moveToLeft(cardView_gas_or_water, rl_gas_gasbill, UnitConversionUtils.getScreenWidth(getActivity()));
                DataBaiduPush.setGOW(2);
                MainActivity.tv_title.setCompoundDrawables(null, null, null, null); // 设置左图
                MainActivity.tv_title.setText("水务");
                ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.button_gas_fee:
                intent = new Intent(getActivity(), PayTheGasFeeActivity.class);
                intent.putExtra("OrderType", ConstantKotlin.OrderType.GASFEEARREARS.name());
                DataBaiduPush.setPmttp("010001");
                DataBaiduPush.setPmttpType("01");
                break;
            case R.id.button_busi_fee:
                intent = new Intent(getActivity(), PayTheGasFeeActivity.class);
                intent.putExtra("OrderType", ConstantKotlin.OrderType.OPERATINGFEEARREARS.name());
                DataBaiduPush.setPmttp("010002");
                DataBaiduPush.setPmttpType("01");
                break;
            case R.id.button_gas_bill:
                intent = new Intent(getActivity(), PayTheGasFeeActivity.class);
                intent.putExtra("OrderType", ConstantKotlin.OrderType.GASFEEBILL.name());
                DataBaiduPush.setPmttp("010001");
                DataBaiduPush.setPmttpType("01");
                break;
            case R.id.button_busi_bill:
                intent = new Intent(getActivity(), PayTheGasFeeActivity.class);
                intent.putExtra("OrderType", ConstantKotlin.OrderType.OPERATINGFEEBILL.name());
                DataBaiduPush.setPmttp("010002");
                DataBaiduPush.setPmttpType("01");
                break;
            case R.id.button_water_fee:
                intent = new Intent(getActivity(), PayTheGasFeeActivity.class);
                intent.putExtra("OrderType", ConstantKotlin.OrderType.WATERFEEARREARS.name());
                DataBaiduPush.setPmttp("020001");
                DataBaiduPush.setPmttpType("02");
                break;
            case R.id.button_water_bill:
                intent = new Intent(getActivity(), PayTheGasFeeActivity.class);
                intent.putExtra("OrderType", ConstantKotlin.OrderType.WATERFEEBILL.name());
                DataBaiduPush.setPmttp("020001");
                DataBaiduPush.setPmttpType("02");
                break;
        }

        if (intent != null)
            startActivity(intent);
    }

    private void moveToLeft(View target1, View target2, float distance) {
        AnimatorSet scaleDown = new AnimatorSet();
        // TODO 平移
        scaleDown.playTogether(ObjectAnimator.ofFloat(target1, "translationX", -distance),
                ObjectAnimator.ofFloat(target2, "translationX", -distance));
        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.anim.decelerate_interpolator));
        scaleDown.setDuration(250);
        scaleDown.start();
    }

    private void moveToRight(View target1, View target2, boolean isFast) {
        AnimatorSet scaleDown = new AnimatorSet();
        // TODO 平移
        scaleDown.playTogether(ObjectAnimator.ofFloat(target1, "translationX", 0),
                ObjectAnimator.ofFloat(target2, "translationX", 0));
        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.anim.decelerate_interpolator));
        if (isFast)
            scaleDown.setDuration(1);
        else
            scaleDown.setDuration(250);
        scaleDown.start();
    }

    public void resetVisibility(boolean isFast) {
        isShowSecondMenu = false;
        if (ll_gas_gasbill != null)
            ll_gas_gasbill.setVisibility(View.VISIBLE);
        if (ll_water != null)
            ll_water.setVisibility(View.VISIBLE);
        DataBaiduPush.setGOW(0);
        if (cardView_gas_or_water != null && rl_gas_gasbill != null)
            moveToRight(cardView_gas_or_water, rl_gas_gasbill, isFast);
    }

    @Override
    public void onStart() {
        super.onStart();
        convenientBanner.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        convenientBanner.pause();
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) {
            startActivity(new Intent(getActivity(), MovableActivity.class));
        }
    }
}
