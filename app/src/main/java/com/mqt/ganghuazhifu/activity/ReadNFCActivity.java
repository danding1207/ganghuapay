package com.mqt.ganghuazhifu.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSONObject;
import com.mqt.ganghuazhifu.BaseActivity;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.xtkj.nfcjar.StringUtil;
import com.xtkj.nfcjar.SunNFC;
import com.xtkj.nfcjar.bean.ParamBean;
import com.xtkj.nfcjar.bean.PayResultBean;
import com.xtkj.nfcjar.bean.ReadResultBean;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 读NFC燃气表
 *
 * @author yang.lei
 * @date 2014-12-24
 */
@SuppressLint("NewApi")
public class ReadNFCActivity extends BaseActivity {

    private static final int NFC_QUERY = 1;// 读表信息
    private static final int NFC_QUERYING = 0;// 读表信息
    private static final int ADD_NUM = 2;// 充值
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private String userNumInput = "00000000000000000000";// 用户号
    private int clickBtNum;// 标记点击的按钮
    private String UserNb;
    private String OrderNb;
    private String SignMsg;
    private String DesCmd;
    private int NFCPayTime;
    private boolean isSuccessed = false;
    //    private MaterialDialog.Builder builder;
    private ParamBean paramBean;

    private Toolbar toolbar;
    private TextView tv_version;

    private int shebeiType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_nfc);
        initView();
    }

    private void initView() {
        UserNb = getIntent().getStringExtra("UserNb");
        OrderNb = getIntent().getStringExtra("OrderNb");

        shebeiType = getIntent().getIntExtra("Type", 1);

        userNumInput = getMeterNum(UserNb);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_version = (TextView) findViewById(R.id.tv_version);

        Logger.i("userNumInput--->" + userNumInput);
        setSupportActionBar(toolbar);
        switch (shebeiType) {
            case 1:
                getSupportActionBar().setTitle("NFC金额表");
                break;
            case 2:
                getSupportActionBar().setTitle("NFC气量表");
                break;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clickBtNum = NFC_QUERY;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);

//        builder = new MaterialDialog.Builder(this)
//                .title("提示")
//                .onPositive((dialog, which) -> finish())
//                .cancelable(false)
//                .canceledOnTouchOutside(false)
//                .positiveText("确定");
    }

    /**
     * 获取用户号，保证20位
     */
    private String getMeterNum(String address) {
        String meterAddr = null;
        int length = address.length();
        if (length < 20) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 20 - length; i++) {
                builder.append("0");
            }
            builder.append(address);
            meterAddr = builder.toString();
        } else if (length == 20) {
            meterAddr = address;
        } else {
            meterAddr = address.substring(0, 21);
        }
        return meterAddr;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNFC();
        // Log.e(TAG, "SunNFC.getParams()--->"+SunNFC.getParams());
        if (mNfcAdapter != null) {
            setNfcIntentFilter(this, mNfcAdapter, mPendingIntent);
        }
        time = System.currentTimeMillis();
        stopPlay();
        startPlay();
    }

    private ScheduledExecutorService scheduledExecutorService = null;
    long time;

    private void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 1, TimeUnit.SECONDS);
    }

    private void stopPlay() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NotNull Bundle savedInstanceState) {

    }

    @Override
    public void onActivityRestoreInstanceState(@NotNull Bundle savedInstanceState) {

    }

    /**
     * @author yang.lei
     */
    private class SlideShowTask implements Runnable {
        @Override
        public void run() {
            handler.obtainMessage().sendToTarget();

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ((System.currentTimeMillis() - time) > 30000l) {
                stopPlay();
                new MaterialDialog.Builder(ReadNFCActivity.this)
                        .title("提示")
                        .content("充值超时,请重试！")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .onPositive((dialog, which) -> finish())
                        .positiveText("确定")
                        .build().show();
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Tag getTag = (Tag) tag;
        if (getTag != null) {
            dealTheAction(getTag);
        }
    }

    /**
     * 根据点击的按钮进行相应的处理
     *
     * @param getTag 检测到的标签
     */
    private void dealTheAction(Tag getTag) {
        switch (clickBtNum) {
            case NFC_QUERY:// 读表信息
                queryLast(getTag, userNumInput);
                break;
            case ADD_NUM:// 读表信息
                addMoney(getTag, SignMsg, DesCmd, false);
                break;
            default:
                break;
        }
    }

    /**
     * NFC充值
     *
     * @param getTag       检测到的标签
     * @param encodeString 用户号，开发时从后台获取
     */
    private void addMoney(Tag getTag, String encodeString, String desCmd) {
        IsoDep isoDep = IsoDep.get(getTag);
        time = System.currentTimeMillis();
        if (isoDep != null) {
            try {
                isoDep.connect();// 连接
                PayResultBean bean = null;
                Logger.d("encodeString=" + encodeString + "\ndesCmd=" + desCmd);
                bean = SunNFC.addMoney(this, isoDep, encodeString, false, paramBean, desCmd);
                // bean = SunNFC.addMoney(isoDep, encodeString);
                // 返回对象PayResultBean说明如下：
                // result,1--充值成功；0--充值失败；
                // payTimes--充值次数；remianMoney--剩余金额;totalUse--累计用气量；totalPay--累计购气金额
                showMusic();

//                new MaterialDialog.Builder(ReadNFCActivity.this)
//                        .title("提示")
//                        .content("充值:调用老表接口！")
//                        .cancelable(false)
//                        .canceledOnTouchOutside(false)
//                        .positiveText("确定")
//                        .build().show();

                if (bean != null) {
                    if (bean.result == 1) {
                        if (NFCPayTime == bean.payTimes) {
                            ToastUtil.Companion.toastSuccess("充值成功");
                            Intent intent = new Intent(ReadNFCActivity.this, NFCInfoActivity.class);
                            intent.putExtra("PayResultBean", Parcels.wrap(bean));
                            intent.putExtra("Ordernb", OrderNb);
                            startActivity(intent);
                            finish();
                        } else {
                            new MaterialDialog.Builder(ReadNFCActivity.this)
                                    .title("提示")
                                    .content("充值失败:充值次数错误！")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("确定")
                                    .build().show();
                        }
                    } else if (bean.result == 0) {
                        new MaterialDialog.Builder(ReadNFCActivity.this)
                                .title("提示")
                                .content("充值失败:返回校验失败！")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .positiveText("确定")
                                .build().show();
                    } else {
//                        new MaterialDialog.Builder(ReadNFCActivity.this)
//                                .title("提示")
//                                .content("充值失败:调用老表接口，居然也会反   bean.result=2！")
//                                .cancelable(false)
//                                .canceledOnTouchOutside(false)
//                                .positiveText("确定")
//                                .build().show();
                    }
                } else {
                    new MaterialDialog.Builder(ReadNFCActivity.this)
                            .title("提示")
                            .content("充值失败：返回值为空！")
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .positiveText("确定")
                            .build().show();
                }
            } catch (IOException e) {
                new MaterialDialog.Builder(ReadNFCActivity.this)
                        .title("提示")
                        .content("充值失败:写表出错！")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .positiveText("确定")
                        .build().show();
                e.printStackTrace();
//            } finally {
//                try {
//                    clickBtNum = NFC_QUERYING;
//                    SignMsg = null;
//                    isoDep.close();
//                } catch (IOException e) {
//                    new MaterialDialog.Builder(ReadNFCActivity.this)
//                            .title("提示")
//                            .content("充值失败:重试出错！")
//                            .cancelable(false)
//                            .canceledOnTouchOutside(false)
//                            .positiveText("确定")
//                            .build().show();
//                    e.printStackTrace();
//                }
            }
        } else {
            new MaterialDialog.Builder(ReadNFCActivity.this)
                    .title("提示")
                    .content("不支持该标签！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show();
        }
    }

    /**
     * NFC充值
     *
     * @param getTag       检测到的标签
     * @param encodeString 充值金额
     * @param isSet        充值序号，开发时从后台获取
     */
    private void addMoney(Tag getTag, String encodeString, String desCmd, Boolean isSet) {
        IsoDep isoDep = IsoDep.get(getTag);
        time = System.currentTimeMillis();
        if (isoDep != null) {
            try {
                isoDep.connect();// 连接
                Logger.e("encodeString=" + encodeString + "\ndesCmd=" + desCmd);
                PayResultBean bean = SunNFC.addMoney(this, isoDep, encodeString, isSet, paramBean, desCmd);
                // 充值，返回充值信息对象PayResultBean
                // 充值参数说明：第一个为检测到的标签；第二个充值字符串
//				bean = SunNFC.addMoney(isoDep, encodeString, Params, isSet);
                // 返回对象PayResultBean说明如下：
                // result,1--充值成功；0--充值失败；
                // payTimes--充值次数；remianMoney--剩余金额;totalUse--累计用气量；totalPay--累计购气金额
                showMusic();

                if (bean != null) {
                    if (bean.result == 1) {
                        if (NFCPayTime == bean.payTimes) {
                            ToastUtil.Companion.toastSuccess("充值成功");
                            Intent intent = new Intent(ReadNFCActivity.this, NFCInfoActivity.class);
                            intent.putExtra("PayResultBean", Parcels.wrap(bean));
                            intent.putExtra("ShebeiType", shebeiType);
                            intent.putExtra("Ordernb", OrderNb);
                            startActivity(intent);
                            finish();
                        } else {
                            new MaterialDialog.Builder(this)
                                    .title("提示")
                                    .content("充值失败:充值次数错误！")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("确定")
                                    .build().show();
                        }
                    } else if (bean.result == 0) {
                        new MaterialDialog.Builder(this)
                                .title("提示")
                                .content("充值失败:返回校验失败！")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .positiveText("确定")
                                .build().show();
                    } else if (bean.result == 2) {
                        Logger.d("bean.result == 2");
                        isoDep.close();
                        addMoney(getTag, encodeString, desCmd);
                    }
                } else {
                    new MaterialDialog.Builder(this)
                            .title("提示")
                            .content("充值失败：返回值为空！")
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .positiveText("确定")
                            .build().show();
                }
            } catch (IOException e) {
                new MaterialDialog.Builder(this)
                        .title("提示")
                        .content("充值失败:写表出错！")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .positiveText("确定")
                        .build().show();
                e.printStackTrace();
            }
        } else {
            new MaterialDialog.Builder(this)
                    .title("提示")
                    .content("不支持该标签！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show();
        }
    }

    /**
     * 读表信息
     *
     * @param getTag  检测到的标签
     * @param userNum 用户号，开发时从后台获取
     */
    private void queryLast(Tag getTag, String userNum) {
        IsoDep isoDep = IsoDep.get(getTag);
        clickBtNum = NFC_QUERYING;
        time = System.currentTimeMillis();
        if (isoDep != null) {
            try {
                isoDep.connect();
                // 读表信息，返回读取信息对象ReadResultBean
                // 参数说明：第一个参数为检测到的标签；第二个参数为用户号
                ReadResultBean bean = SunNFC.readMeter(isoDep, userNum);
                // 返回对象ReadReadResultBean说明如下：
                // result,1--读取成功；0--读取失败
                // remainedMoney--剩余金额；totalBuy--累计购气金额;totalUse--累计用气量；meterState--表状态;
                // nfcTimes--充值次数；nfcBuy--nfc购气金额；nfcTotalBuy--nfc累计购气金额
                // historyList--月用量，list中保存24个月用量，依此为当月、上月、上上月
                showMusic();
                if (bean.result == 1) {
                    if (!bean.meterNum.equals(userNumInput)) {
                        new MaterialDialog.Builder(ReadNFCActivity.this)
                                .title("提示")
                                .content("充值户号与表户号不对应，请检查充值信息！")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .positiveText("确定")
                                .build().show();
                        return;
                    }
                    getEncode(bean);
                } else {
                    new MaterialDialog.Builder(ReadNFCActivity.this)
                            .title("提示")
                            .content("读表失败,可能是用户号不匹配！")
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .positiveText("确定")
                            .build().show();
                }
            } catch (IOException e) {
                new MaterialDialog.Builder(ReadNFCActivity.this)
                        .title("提示")
                        .content("读表失败,可能是用户号不匹配！")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .positiveText("确定")
                        .build().show();
                e.printStackTrace();
                clickBtNum = NFC_QUERY;
            } finally {
            }
        } else {
            new MaterialDialog.Builder(ReadNFCActivity.this)
                    .title("提示")
                    .content("不支持该标签！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show();
        }
    }

    private boolean isActiveNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Logger.d("isConnected--->" + isConnected);
        return isConnected;
    }

    private void getEncode(final ReadResultBean bean) {
        Logger.i("getEncode");
        if (isActiveNetwork()) {
            clickBtNum = NFC_QUERYING;
            tv_version.setText("第二步:获取加密信息");
            StringBuffer sumTotalTwentyFour = new StringBuffer();
            StringBuffer threeSecurityCheck = new StringBuffer();
            if (bean.securityRecords.size() > 0) {
                for (int i = 0; i < bean.securityRecords.size(); i++) {
                    if (i == bean.securityRecords.size() - 1) {
                        threeSecurityCheck.append(bean.securityRecords.get(i) + "");
                    } else {
                        threeSecurityCheck.append(bean.securityRecords.get(i) + "|");
                    }
                }
            }
            if (bean.historyList.size() > 0) {
                for (int i = 0; i < bean.historyList.size(); i++) {
                    if (i == bean.historyList.size() - 1) {
                        sumTotalTwentyFour.append(bean.historyList.get(i) + "");
                    } else {
                        sumTotalTwentyFour.append(bean.historyList.get(i) + "|");
                    }
                }
            }

            BigDecimal nowPrice = new BigDecimal(bean.currentPrice);
            BigDecimal nowRemainMoney = new BigDecimal(bean.remainedMoney);
            BigDecimal toalBuyMoney = new BigDecimal(bean.totalBuy);
            BigDecimal nfcMoney = new BigDecimal(bean.nfcBuy);
            BigDecimal nfcTotalMoney = new BigDecimal(bean.nfcTotalBuy);

            BigDecimal toatalUseGas = new BigDecimal(bean.totalUse);

            BigDecimal f = new BigDecimal(100);
            BigDecimal ten = new BigDecimal(10);

            String nowPriceString = null;
            String nowRemainMoneyString = null;
            String toalBuyMoneyString = null;
            String nfcMoneyString = null;
            String nfcTotalMoneyString = null;
            String toatalUseGasString = null;

            switch (shebeiType) {
                case 1:
                    nowPriceString = nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nowRemainMoneyString = nowRemainMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toalBuyMoneyString = toalBuyMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcMoneyString = nfcMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcTotalMoneyString = nfcTotalMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    break;
                case 2:
                    nowPriceString = nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nowRemainMoneyString = nowRemainMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toalBuyMoneyString = toalBuyMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcMoneyString = nfcMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcTotalMoneyString = nfcTotalMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    break;
            }

            CusFormBody body = HttpRequestParams.INSTANCE.getParamsForGetNFCCode(
                    OrderNb,
                    bean.nfcTimes + 1,
                    UserNb,
                    bean.currentTime + "",
                    nowPriceString,
                    nowRemainMoneyString,
                    toalBuyMoneyString,
                    bean.noUseDay + "",
                    bean.noUseSecond + "",
                    bean.meterState + "",
                    bean.dealWord + "",
                    toatalUseGasString,
                    sumTotalTwentyFour.toString(),
                    bean.securityCount + "", threeSecurityCheck.toString(),
                    nfcMoneyString,
                    nfcTotalMoneyString,
                    null);
            time = System.currentTimeMillis();
            Logger.i("nfcTimes--->" + (bean.nfcTimes + 1));
            HttpRequest.Companion.getInstance().httpPost(ReadNFCActivity.this, HttpURLS.INSTANCE.getNFCReadNumberLoopBackAndNFCSignMsg(), true, "NFCSignMsg",
                    body, (isError, response, type, error) -> {
                        if (isError) {
                            Logger.e(error.toString());
                            new MaterialDialog.Builder(ReadNFCActivity.this)
                                    .title("提示")
                                    .content("请求加密信息失败，请重试！")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("确定")
                                    .build().show();
                            clickBtNum = NFC_QUERY;
                            tv_version.setText("第一步:读取燃气表信息");
                        } else {
                            Logger.i(response.toString());
                            JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                            JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                            String ProcessCode = ResponseHead.getString("ProcessCode");
                            String ProcessDes = ResponseHead.getString("ProcessDes");
                            if (ProcessCode.equals("0000") && ResponseFields != null) {
                                SignMsg = ResponseFields.getString("SignMsg");
                                NFCPayTime = ResponseFields.getInteger("NFCPayTime");
                                Logger.d("SignMsg--->" + SignMsg);
                                Logger.d("NFCPayTime--->" + NFCPayTime);
                                getDesCmd(bean);
                            } else {
                                new MaterialDialog.Builder(ReadNFCActivity.this)
                                        .title("提示")
                                        .content(ProcessDes)
                                        .cancelable(false)
                                        .canceledOnTouchOutside(false)
                                        .positiveText("确定")
                                        .build().show();
                            }
                        }
                    });
        } else {
            new MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("下一步需要连接网络，请确保您已连接网络再试")
                    .onPositive((dialog, which) -> getEncode(bean))
                    .onNegative((dialog, which) -> finish())
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .negativeText("退出刷卡")
                    .show();
        }
    }

    private void getDesCmd(final ReadResultBean bean) {
        Logger.i("getDesCmd");
        if (isActiveNetwork()) {
            clickBtNum = NFC_QUERYING;
            tv_version.setText("第二步:获取加密信息");
            String random = StringUtil.createRanHex8();// 随机数

            StringBuffer sumTotalTwentyFour = new StringBuffer();
            StringBuffer threeSecurityCheck = new StringBuffer();
            if (bean.securityRecords.size() > 0) {
                for (int i = 0; i < bean.securityRecords.size(); i++) {
                    if (i == bean.securityRecords.size() - 1) {
                        threeSecurityCheck.append(bean.securityRecords.get(i) + "");
                    } else {
                        threeSecurityCheck.append(bean.securityRecords.get(i) + "|");
                    }
                }
            }
            if (bean.historyList.size() > 0) {
                for (int i = 0; i < bean.historyList.size(); i++) {
                    if (i == bean.historyList.size() - 1) {
                        sumTotalTwentyFour.append(bean.historyList.get(i) + "");
                    } else {
                        sumTotalTwentyFour.append(bean.historyList.get(i) + "|");
                    }
                }
            }

            BigDecimal nowPrice = new BigDecimal(bean.currentPrice);
            BigDecimal nowRemainMoney = new BigDecimal(bean.remainedMoney);
            BigDecimal toalBuyMoney = new BigDecimal(bean.totalBuy);
            BigDecimal nfcMoney = new BigDecimal(bean.nfcBuy);
            BigDecimal nfcTotalMoney = new BigDecimal(bean.nfcTotalBuy);

            BigDecimal toatalUseGas = new BigDecimal(bean.totalUse);

            BigDecimal f = new BigDecimal(100);
            BigDecimal ten = new BigDecimal(10);

            String nowPriceString = null;
            String nowRemainMoneyString = null;
            String toalBuyMoneyString = null;
            String nfcMoneyString = null;
            String nfcTotalMoneyString = null;
            String toatalUseGasString = null;

            switch (shebeiType) {
                case 1:
                    nowPriceString = nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nowRemainMoneyString = nowRemainMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toalBuyMoneyString = toalBuyMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcMoneyString = nfcMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcTotalMoneyString = nfcTotalMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    break;
                case 2:
                    nowPriceString = nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nowRemainMoneyString = nowRemainMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toalBuyMoneyString = toalBuyMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcMoneyString = nfcMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    nfcTotalMoneyString = nfcTotalMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                    break;
            }

            CusFormBody body = HttpRequestParams.INSTANCE.getParamsForGetNFCCode(
                    OrderNb, bean.nfcTimes + 1,
                    UserNb, bean.currentTime + "",
                    nowPriceString,
                    nowRemainMoneyString,
                    toalBuyMoneyString,
                    bean.noUseDay + "", bean.noUseSecond + "",
                    bean.meterState + "", bean.dealWord + "",
                    toatalUseGasString,
                    sumTotalTwentyFour.toString(),
                    bean.securityCount + "",
                    threeSecurityCheck.toString(),
                    nfcMoneyString,
                    nfcTotalMoneyString,
                    random);
            HttpRequest.Companion.getInstance().httpPost(ReadNFCActivity.this, HttpURLS.INSTANCE.getNFCReadNumberLoopBackAndNFCSignMsg(), true, "DesCmd",
                    body, (isError, response, type, error) -> {
                        if (isError) {
                            Logger.e(error.toString());
                            new MaterialDialog.Builder(ReadNFCActivity.this)
                                    .title("提示")
                                    .content("请求加密信息失败，请重试！")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("确定")
                                    .build().show();
                            clickBtNum = NFC_QUERY;
                            tv_version.setText("第一步:读取燃气表信息");
                        } else {
                            Logger.i(response.toString());
                            JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                            JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                            String ProcessCode = ResponseHead.getString("ProcessCode");
                            String ProcessDes = ResponseHead.getString("ProcessDes");
                            if (ProcessCode.equals("0000") && ResponseFields != null) {
                                DesCmd = ResponseFields.getString("SignMsg");
                                Logger.i("SignMsg--->" + SignMsg);
                                add();
                            } else {
                                new MaterialDialog.Builder(ReadNFCActivity.this)
                                        .title("提示")
                                        .content(ProcessDes)
                                        .cancelable(false)
                                        .canceledOnTouchOutside(false)
                                        .positiveText("确定")
                                        .build().show();
                            }
                        }
                    });
        } else {
            new MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("下一步需要连接网络，请确保您已连接网络再试")
                    .onPositive((dialog, which) -> getDesCmd(bean))
                    .onNegative((dialog, which) -> finish())
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .negativeText("退出刷卡")
                    .show();
        }
    }

    /**
     * 充值
     */
    public void add() {
        tv_version.setText("第三步:充值到燃气表");
        clickBtNum = ADD_NUM;
    }

    /**
     * 自定义声音播放
     */
    private void showMusic() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(beepListener);
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(0.9f, 0.9f);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            mediaPlayer = null;
        }
    }

    private final OnCompletionListener beepListener = mediaPlayer -> mediaPlayer.seekTo(0);

    /**
     * 检测设备是否支持NFC并检测是否已开启NFC
     */
    @SuppressLint("NewApi")
    private void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
        if (mNfcAdapter == null) {
            new MaterialDialog.Builder(ReadNFCActivity.this)
                    .title("提示")
                    .content("不支持NFC！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show();
        } else {
            mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            checkNFC();
        }
    }

    /**
     * 检测NFC是否已打开
     */
    private void checkNFC() {
        if (!mNfcAdapter.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请打开NFC");
            final AlertDialog alertDialog = builder.create();
            // 确定
            builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                // 前往NFC设置中心
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            });
            // 取消
            builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                // 关闭对话框
                alertDialog.dismiss();
            });
            // 显示对话框
            builder.show();
        }
    }

    private void setNfcIntentFilter(Activity activity, NfcAdapter nfcAdapter, PendingIntent seder) {
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[]{ndef, tech, tag};
        String[][] techLists = new String[][]{new String[]{IsoDep.class.getName()},
                new String[]{NfcA.class.getName()}, new String[]{MifareClassic.class.getName()}};
        nfcAdapter.enableForegroundDispatch(activity, seder, filters, techLists);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        stopPlay();
        super.onDestroy();
    }

    @Override
    public void OnViewClick(View v) {
    }

}
