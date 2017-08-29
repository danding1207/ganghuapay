package com.mqt.ganghuazhifu.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSONObject;
import com.hwangjr.rxbus.RxBus;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.mqt.ganghuazhifu.BaseActivity;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.bean.RecordChangedEvent;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.xt.bluecard.CardInfo;
import com.xt.bluecard.CardManager;
import com.xt.bluecard.Constant;
import com.xt.bluecard.Utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * 读NFC燃气表
 *
 * @author yang.lei
 * @date 2014-12-24
 */
@SuppressLint("NewApi")
public class BluetoothActivity extends BaseActivity implements OnItemClickListener {

    private final int SHOW_DEVICE_LIST = 0;// 显示蓝牙列表
    private final int CONNECT_RESULT = 1;// 蓝牙连接
    private final int READ_CARD_INFO = 2;// 读取卡信息
    private final int PAY_MONEY = 3;// 充值
    protected static final int POWER_ON = 4;// 上电;
    private final int POWER_OFF = 5;

    private List<BluetoothDevice> devices = new ArrayList<>();// 搜索到的蓝牙集合
    private MyAdapter mAdapter;
    private MaterialDialog dialog;
    private AlertDialog bluetoothDialog;
    private ListView lv_devices;
    private CardManager manager;
    private boolean isConnected = false;
    private String money;
    private String times;
    private String orderNb;
    private String UserNb;
    private String ICcardNo;
    private int NFCICSumCount;

    private Toolbar toolbar;
    private ImageView ib_pic_right;
    private TextView tv_name, tv_addr, tv_status, tv_res;
    private Button bt_read, bt_pay;

    private int shebeiType;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Logger.d("msg==" + msg.what);
            closeProgress();
            MaterialDialog.Builder builder = new MaterialDialog.Builder(BluetoothActivity.this)
                    .title("提醒")
                    .cancelable(false)
                    .positiveText("确定")
                    .canceledOnTouchOutside(false);
            switch (msg.what) {
                case SHOW_DEVICE_LIST:// 显示蓝牙列表
                    showList();
                    break;
                case Constant.BLE_CONNECT_FAIL://
                    tv_status.setText("未连接");
                    break;
                case Constant.BLE_CONNECT_SUCCESS://
                    tv_status.setText("已连接");
                    break;
                case Constant.POWER_ON_FAIL:// 上电失败
                    tv_res.append("上电失败");
                    builder.content("上电失败,请确认将燃气CPU卡放在读卡器上。错误信息： " + msg.obj);
                    builder.show();
                    break;
                case Constant.POWER_ON_SUCCESS:// 上电成功
                    tv_res.append("上电成功");
                    readCard((Integer) msg.obj);
                    break;
                case Constant.POWER_OFF_FAIL:
                    tv_res.append("下电失败");
                    break;
                case Constant.POWER_OFF_SUCCESS:
                    tv_res.append("下电完成");
                    break;
                case Constant.READ_CARD_FAIL:
                    builder.content("读取失败。错误信息： " + msg.obj);
                    builder.show();
                    break;
                case Constant.READ_CARD_SUCCESS:
                    CardInfo cardInfo = (CardInfo) msg.obj;
                    if (cardInfo != null) {
                        int type = msg.arg1;

                        times = String.valueOf(Integer.valueOf(cardInfo.buyTimes) + 1);
                        Logger.i("times---->" + times);

//                        UserNb = cardInfo.userCode;
                        if (type == 1)
                            showInfo(cardInfo);
                        else
                            payMoney();
                    } else {
                        tv_res.setText("读取失败");
                        builder.content("读取失败,请确认将燃气CPU卡放在读卡器上。");
                        builder.show();
                    }
                    break;
                case Constant.CARD_NOT_FLUSH:
                    builder.content("请先刷表后再进行充值");
                    builder.show();
                    break;
                case Constant.GET_SERIALNUM_FAIL:
                    builder.content("获取序列号失败");
                    builder.show();
                    break;
                case Constant.CHECK_SERIALNUM_FAIL:
                    builder.content("卡号与订单中系统卡号不匹配" + msg.obj);
                    builder.show();
                    break;
                case Constant.GET_RANDOM8_FAIL:
                    builder.content("获取8个字节随机数失败");
                    builder.show();
                    break;
                case Constant.IP_PORT_ERROR:
                    builder.content("IP或端口错误");
                    builder.show();
                    break;
                case Constant.NET_OUTAUTH_LOGIN_FAIL:
                    builder.content("联网外部认证登录失败");
                    builder.show();
                    break;
                case Constant.NET_OUTAUTH_VALUE_ERROR:
                    builder.content("联网外部认证返回值错误");
                    builder.show();
                    break;
                case Constant.TRANS_OUTAUTH_FAIL:
                    builder.content("指令外部认证失败");
                    builder.show();
                    break;
                case Constant.GET_RANDOM4_FAIL:
                    builder.content("获取4个字节随机数失败");
                    builder.show();
                    break;
                case Constant.GET_CARDINFO_FAIL:
                    builder.content("读取卡中数据失败");
                    builder.show();
                    break;
                case Constant.GET_DESDATA_FAIL:
                    builder.content("获取加密数据失败");
                    builder.show();
                    break;
                case Constant.SEND_DESDATA_FAIL:
                    builder.content("发送加密数据失败");
                    builder.show();
                    break;
                case Constant.SEND_MONEY_FAIL:
                    builder.content("发送充值金额数据指令失败");
                    builder.show();
                    break;
                case Constant.GET_QUAN_FAIL:
                    builder.content("加密系统圈存失败");
                    builder.show();
                    break;
                case Constant.SEND_QUAN_MAC:
                    builder.content("发现个圈存指令失败");
                    builder.show();
                    break;
                case Constant.SELECT_FILE_FAIL:
                    builder.content("选择文件失败");
                    builder.show();
                    break;
                case Constant.SEND_200_FAIL:
                    builder.content("返写字节失败");
                    builder.show();
                    break;
                case Constant.WRITE_CARD_OK:
                    builder.content("写卡成功");
                    builder.show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 蓝牙主要操作对象，建议单例。
     */
    private static LiteBluetooth liteBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initView();
        initSearchDialog();
        manager = new CardManager(this);
        mAdapter = new MyAdapter(this, devices);
        lv_devices.setAdapter(mAdapter);
    }

    private void initView() {
        UserNb = getIntent().getStringExtra("UserNb");
        orderNb = getIntent().getStringExtra("OrderNb");
        money = getIntent().getStringExtra("OrderMoney");
        NFCICSumCount = getIntent().getIntExtra("NFCICSumCount", 0);
        ICcardNo = getIntent().getStringExtra("ICcardNo");

        shebeiType = getIntent().getIntExtra("Type", 1);

        Logger.i("shebeiType->" + shebeiType);
        Logger.i("UserNb->" + UserNb + ",    orderNb->" + orderNb);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ib_pic_right = (ImageView) findViewById(R.id.ib_pic_right);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_res = (TextView) findViewById(R.id.tv_res);
        bt_read = (Button) findViewById(R.id.bt_read);
        bt_pay = (Button) findViewById(R.id.bt_pay);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("蓝牙读卡器写卡");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ib_pic_right.setOnClickListener(this);
        bt_read.setOnClickListener(this);
        bt_pay.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isSupport = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (!isSupport) {
            new MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("不支持蓝牙")
                    .positiveText("确定")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .onPositive((dialog1, which) -> finish())
                    .show();
        } else {
            manager.disConnectBlE();
            if (liteBluetooth == null) {
                liteBluetooth = new LiteBluetooth(this);
            }
            liteBluetooth.enableBluetoothIfDisabled(this, 1);
            search();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disConnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            finish();
        }
    }

    private void initSearchDialog() {
        if (bluetoothDialog == null) {
            Builder builder = new AlertDialog.Builder(this);
            bluetoothDialog = builder.create();
            bluetoothDialog.setCanceledOnTouchOutside(false);
            View view = View.inflate(this, R.layout.device_list, null);
            lv_devices = (ListView) view.findViewById(R.id.list);
            pb = (ProgressBar) view.findViewById(R.id.pb);
            lv_devices.setOnItemClickListener(this);
            bluetoothDialog.setView(view);
        }
    }

    /**
     * 搜索蓝牙对话框
     */
    private void showSearchDialog() {
        pb.setVisibility(View.VISIBLE);
        bluetoothDialog.show();
    }

    /**
     * 列表显示搜到的蓝牙
     */
    protected void showList() {
        Logger.i("size--->" + devices.size());
        mAdapter.setDevices(devices);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (devices.size() > position) {
            tv_name.setText(devices.get(position).getName());
            tv_addr.setText(devices.get(position).getAddress());
            connect(devices.get(position).getAddress());
        }
        tv_status.setText("未连接");
        bluetoothDialog.dismiss();
        liteBluetooth.stopScan(mLeScanCallback);
    }

    /**
     * 充值
     */
    private void payMoney() {
        if (!manager.isBlEConnected()) {
            if (!isConnected)
                ToastUtil.Companion.toastWarning("请连接蓝牙");
            else
                ToastUtil.Companion.toastError("超时，请重新连接蓝牙");
            return;
        }
        showPayDialog();
    }

    /**
     * 获取用户号，保证20位
     */
    private String getMeterNum(String address) {
        String meterAddr;
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

    //测试流程，控制第一次写卡信息失败，第二次写卡信息成功
    private boolean isFristError = true;
    private boolean isQuanCunSuccessed = false;

    /**
     * 显示充值时需要输入的数据对话框
     */
    private void showPayDialog() {
        CardInfo cardInfo = manager.readCard();
        //户号检查
        if (!cardInfo.userCode.equals(getMeterNum(UserNb))) {
            new MaterialDialog.Builder(BluetoothActivity.this)
                    .title("提醒")
                    .cancelable(false)
                    .positiveText("确定")
                    .canceledOnTouchOutside(false)
                    .content("充值户号与卡户号不对应，请检查充值信息！")
                    .show();
            return;
        }
        //充值次数检查
//        if (NFCICSumCount != (cardInfo.buyTimes + 1)) {
//            new MaterialDialog.Builder(BluetoothActivity.this)
//                    .title("提醒")
//                    .cancelable(false)
//                    .positiveText("确定")
//                    .canceledOnTouchOutside(false)
//                    .content("卡片充值次数异常，请前往营业厅查询(系统次数：" + NFCICSumCount + ", 卡片次数：" + cardInfo.buyTimes + ")！")
//                    .show();
//            return;
//        }

        boolean enable = manager.isEnable(cardInfo);
        if (!enable) {
            handler.obtainMessage(Constant.CARD_NOT_FLUSH).sendToTarget();
            return;
        }

        showProgress("正在充值,请稍后……");

        // 第一步
        final String[] serialNum = manager.getSerialNum();
        if (!serialNum[0].equals("1")) {
            handler.obtainMessage(Constant.GET_SERIALNUM_FAIL, serialNum[1]).sendToTarget();
            return;
        } else if (!("00000000" + serialNum[1]).equals(ICcardNo)) {
            handler.obtainMessage(Constant.CHECK_SERIALNUM_FAIL, "充值卡号与写卡号不对应（系统卡号：" + ICcardNo + ",  卡号：" + ("00000000" + serialNum[1]) + ")， 请检查充值信息！").sendToTarget();
            //卡号检查
            return;
        }
        boolean enter3f02 = manager.enter3F02();
        if (!enter3f02) {
            handler.obtainMessage(Constant.ENTER_3F02_FAIL).sendToTarget();
            return;
        }
        final String[] random8Hex = manager.getRandom8Hex();
        if (!random8Hex[0].equals("1")) {
            handler.obtainMessage(Constant.GET_RANDOM8_FAIL, random8Hex[1]).sendToTarget();
            return;
        }

        Observable.create((Observable.OnSubscribe<String>) subscriber -> {

            CusFormBody body = HttpRequestParams.INSTANCE.getParamsForBluetoothSignMsg(orderNb, UserNb, "10",
                    serialNum[1], random8Hex[1], null, null, null, null, null);
            Logger.i("sendToAuth");
            HttpRequest.Companion.getInstance().httpPost(BluetoothActivity.this, HttpURLS.INSTANCE.getBluetoothSignMsg(), false,
                    "bluetooth", body, (isError, response, type, error) -> {
                        if (isError) {
                            Logger.e(error.toString());
                            subscriber.onError(error);
                        } else {
                            Logger.t("CardManager").i(response.toString());
                            JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                            JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                            String ProcessCode = ResponseHead.getString("ProcessCode");
                            String ProcessDes = ResponseHead.getString("ProcessDes");
                            if (ProcessCode.equals("0000")) {
                                String auth = ResponseFields.getString("SignMsg");
                                auth = Utils.getMapValue(auth, "DesRandm");
                                if (auth.equals("-1")) {
                                    subscriber.onError(new Throwable("IP端口错误 "));
                                } else if (auth.equals("-2")) {
                                    subscriber.onError(new Throwable("外部认证登录失败 "));
                                } else if (auth.equals("-3")) {
                                    subscriber.onError(new Throwable("返回值错误 "));
                                } else {
                                    subscriber.onNext(auth);
                                }
                            } else {
                                subscriber.onError(new Throwable(ProcessDes));
                            }
                        }
                    });
        }).flatMap(authResult -> {
            Logger.t("CardManager").i("联网外部认证结果==" + authResult);
            String[] transOutAuth = manager.transOutAuth(authResult);
            if (!transOutAuth[0].equals("1")) {
                return Observable.error(new Throwable("外部认证指令错误1：错误码：" + transOutAuth[1]));
            }
            //第一次外部认证成功,接下来先写金额

            int fen = 0;
            switch (shebeiType) {
                case 1:
                    fen = (int) Math.ceil(Double.parseDouble(money) * 100);
                    break;
                case 2:
                    fen = (int) Math.ceil(Double.parseDouble(money) * 10);
                    break;
            }

            String[] sendMoney = manager.sendMoney(fen);
            if (!sendMoney[0].equals("1")) {
                return Observable.error(new Throwable("发送金额数据指令失败：错误码："));
            }
            String time = Utils.getNowTime();
            return Observable.create((Observable.OnSubscribe<String[]>) subscriber -> {

                CusFormBody body = HttpRequestParams.INSTANCE.getParamsForBluetoothSignMsg(orderNb, UserNb, "12",
                        serialNum[1], null, null, null, time, sendMoney[1], money);
                Logger.i(body.toString());
                HttpRequest.Companion.getInstance().httpPost(BluetoothActivity.this, HttpURLS.INSTANCE.getBluetoothSignMsg(), false,
                        "bluetooth", body, (isError, response, type, error) -> {
                            if (isError) {
                                Logger.e(error.toString());
                                subscriber.onError(error);
                            } else {
                                Logger.t("CardManager").i(response.toString());
                                JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                                JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                                String ProcessCode = ResponseHead.getString("ProcessCode");
                                String ProcessDes = ResponseHead.getString("ProcessDes");
                                if (ProcessCode.equals("0000")) {
                                    String quan = ResponseFields.getString("SignMsg");
                                    quan = Utils.getMapValue(quan, "MAC");
                                    if (quan.equals("-1")) {
                                        subscriber.onError(new Throwable("圈存失败 "));
                                    } else {
                                        subscriber.onNext(new String[]{time, quan});
                                    }
                                } else {
                                    subscriber.onError(new Throwable(ProcessDes));
                                }
                            }
                        });
            });
        }).flatMap(sendQuan -> {

            isQuanCunSuccessed = false;
            String[] sendQuanMac = manager.sendQuanMac(sendQuan[0], sendQuan[1]);
            if (!sendQuanMac[0].equals("1")) {
                return Observable.error(new Throwable("圈存指令失败：错误码："));
            }
            isQuanCunSuccessed = true;
            //圈存成功

            String[] random4Hex = manager.getRandom4Hex();
            if (!random4Hex[0].equals("1")) {
                return Observable.error(new Throwable("获取随机数失败"));
            }
            String[] cardMsg = manager.getCardMsg();
            if (!cardMsg[0].equals("1")) {
                return Observable.error(new Throwable("获取卡待加密数据失败"));
            }
            return Observable.create((Observable.OnSubscribe<String[]>) subscriber -> {

                StringBuffer sumTotalTwentyFour = new StringBuffer();
                StringBuffer threeSecurityCheck = new StringBuffer();
                if (cardInfo.securityRecord.size() > 0) {
                    for (int i = 0; i < cardInfo.securityRecord.size(); i++) {
                        if (i == cardInfo.securityRecord.size() - 1) {
                            threeSecurityCheck.append(cardInfo.securityRecord.get(i) + "");
                        } else {
                            threeSecurityCheck.append(cardInfo.securityRecord.get(i) + "|");
                        }
                    }
                }
                if (cardInfo.historyMonthList.size() > 0) {
                    for (int i = 0; i < cardInfo.historyMonthList.size(); i++) {
                        if (i == cardInfo.historyMonthList.size() - 1) {
                            sumTotalTwentyFour.append(cardInfo.historyMonthList.get(i) + "");
                        } else {
                            sumTotalTwentyFour.append(cardInfo.historyMonthList.get(i) + "|");
                        }
                    }
                }

                BigDecimal nowPrice = new BigDecimal(cardInfo.nowPrice);
                BigDecimal nowRemainMoney = new BigDecimal(cardInfo.nowRemainMoney);
                BigDecimal toalBuyMoney = new BigDecimal(cardInfo.toalBuyMoney);
                BigDecimal nfcTotalMoney = new BigDecimal(cardInfo.nfcTotalMoney);

                BigDecimal toatalUseGas = new BigDecimal(cardInfo.toatalUseGas);
//                BigDecimal f = new BigDecimal(1);
                BigDecimal f = new BigDecimal(100);
                BigDecimal ten = new BigDecimal(10);

                String nowPriceString = null;
                String nowRemainMoneyString = null;
                String toalBuyMoneyString = null;
                String nfcTotalMoneyString = null;
                String toatalUseGasString = null;

                switch (shebeiType) {
                    case 1:
                        nowPriceString = nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        nowRemainMoneyString = nowRemainMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        toalBuyMoneyString = toalBuyMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        nfcTotalMoneyString = nfcTotalMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        break;
                    case 2:
                        nowPriceString = nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        nowRemainMoneyString = nowRemainMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        toalBuyMoneyString = toalBuyMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        nfcTotalMoneyString = nfcTotalMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                        break;
                }

                CusFormBody body = HttpRequestParams.INSTANCE.getParamsForBluetoothSignMsg(orderNb, UserNb, "11",
                        null, random4Hex[1], times, cardMsg[1], null, null, null,
                        cardInfo.nowTime,
                        nowPriceString,
                        nowRemainMoneyString,
                        toalBuyMoneyString,
                        cardInfo.noUseDayCount + "", cardInfo.noUseSecondsCount + "",
                        cardInfo.nowStatus, cardInfo.dealWords,
                        toatalUseGasString,
                        sumTotalTwentyFour.toString(), cardInfo.securityCounts + "",
                        threeSecurityCheck.toString(),
                        nfcTotalMoneyString
                );
                Logger.i(body.toString());
                HttpRequest.Companion.getInstance().httpPost(BluetoothActivity.this, HttpURLS.INSTANCE.getBluetoothSignMsg(), false,
                        "bluetooth", body, (isError, response, type, error) -> {
                            if (isError) {
                                Logger.t("CardManager").i(error.toString());
                                subscriber.onError(error);
                            } else {
                                Logger.t("CardManager").i(response.toString());
                                JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                                JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                                String ProcessCode = ResponseHead.getString("ProcessCode");
                                String ProcessDes = ResponseHead.getString("ProcessDes");
                                if (ProcessCode.equals("0000")) {
                                    String SignMsg = ResponseFields.getString("SignMsg");
                                    String WriteCardFlag = ResponseFields.getString("WriteCardFlag");
                                    if (WriteCardFlag != null && WriteCardFlag.equals("10")) {
                                        subscriber.onError(new Throwable("success"));
                                    } else {
                                        String[] desResult = null;
                                        if (!Utils.isNullOrEmpty(SignMsg)) {
                                            String enStr = null;
                                            String mac = null;
                                            enStr = Utils.getMapValue(SignMsg, "DesData");
                                            mac = Utils.getMapValue(SignMsg, "MAC");
                                            desResult = new String[]{enStr, mac};
                                        } else {
                                            desResult = null;
                                        }
                                        if (desResult == null) {
                                            subscriber.onError(new Throwable("获取加密数据失败"));
                                        } else {
                                            subscriber.onNext(desResult);
                                        }
                                    }
                                } else {
                                    subscriber.onError(new Throwable(ProcessDes));
                                }
                            }
                        });
            });
        }).flatMap(desResult -> {
            String[] sendDesData = manager.sendDesData(desResult[0], desResult[1]);
            if (!sendDesData[0].equals("1")) {
                //写卡信息（充值次数）失败，消费卡金额
                return Observable.error(new Throwable("写卡信息失败!"));
            }
            //写卡信息（充值次数）成功

            final String[] random8Hex1 = manager.getRandom8Hex();
            if (!random8Hex[0].equals("1")) {
                return Observable.error(new Throwable("获取随机数失败"));
            }
            Logger.t("CardManager").i("随机数==8字节====2=" + random8Hex1);
            return Observable.create((Observable.OnSubscribe<String>) subscriber -> {
                CusFormBody body = HttpRequestParams.INSTANCE.getParamsForBluetoothSignMsg(orderNb, UserNb, "10",
                        serialNum[1], random8Hex1[1], null, null, null, null, null);
                Logger.i(body.toString());
                HttpRequest.Companion.getInstance().httpPost(BluetoothActivity.this, HttpURLS.INSTANCE.getBluetoothSignMsg(), false,
                        "bluetooth", body, (isError, response, type, error) -> {
                            if (isError) {
                                Logger.e(error.toString());
                                subscriber.onError(error);
                            } else {
                                Logger.i(response.toString());
                                JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                                JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                                String ProcessCode = ResponseHead.getString("ProcessCode");
                                String ProcessDes = ResponseHead.getString("ProcessDes");
                                if (ProcessCode.equals("0000")) {
                                    String auth = ResponseFields.getString("SignMsg");
                                    auth = Utils.getMapValue(auth, "DesRandm");
                                    if (auth.equals("-1")) {
                                        subscriber.onError(new Throwable("IP端口错误 "));
                                    } else if (auth.equals("-2")) {
                                        subscriber.onError(new Throwable("外部认证登录失败 "));
                                    } else if (auth.equals("-3")) {
                                        subscriber.onError(new Throwable("返回值错误 "));
                                    } else {
                                        subscriber.onNext(auth);
                                    }
                                } else {
                                    subscriber.onError(new Throwable(ProcessDes));
                                }
                            }
                        });
            });
        }).flatMap(auth -> {
            Logger.e("联网外部认证结果===2=" + auth);

            String[] transOutAuth = manager.transOutAuth(auth);
            if (!transOutAuth[0].equals("1")) {
                return Observable.error(new Throwable("外部认证指令错误2：错误码：" + transOutAuth[1]));
            }
            boolean selectFile = manager.selectFile();
            if (!selectFile) {
                return Observable.error(new Throwable("选择文件失败"));
            }
            boolean send200 = manager.writeToZero();
            if (!send200) {
                return Observable.error(new Throwable("写200字节失败"));
            }
//            handler.obtainMessage(Constant.WRITE_CARD_OK).sendToTarget();
            return Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
                CusFormBody body = HttpRequestParams.INSTANCE.getParamsForUpdateNFCPayStatus(
                        orderNb, "11", Utils.getString(cardInfo.addjustBottom), Utils.getString(cardInfo.payBottom));
                Logger.i(body.toString());
                HttpRequest.Companion.getInstance().httpPost(BluetoothActivity.this, HttpURLS.INSTANCE.getUpdateNFCPayStatus(), false,
                        "NFCSignMsg", body, (isError, response, type, error) -> {
                            if (isError) {
                                Logger.e(error.toString());
                                subscriber.onError(error);
                            } else {
                                Logger.t("CardManager").i(response.toString());
                                JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                                String ProcessCode = ResponseHead.getString("ProcessCode");
                                String ProcessDes = ResponseHead.getString("ProcessDes");
                                if (ProcessCode.equals("0000")) {
                                    subscriber.onNext(true);
                                } else {
                                    subscriber.onError(new Throwable(ProcessDes));
                                }
                            }
                        });
            });

        }).onErrorReturn(error1 -> {
            if (error1 != null && "success".equals(error1.getMessage())) {
                return true;
            }

            if (isQuanCunSuccessed) {
                int fen = 0;
                switch (shebeiType) {
                    case 1:
                        fen = (int) Math.ceil(Double.parseDouble(money) * 100);
                        break;
                    case 2:
                        fen = (int) Math.ceil(Double.parseDouble(money) * 10);
                        break;
                }

                String[] sendMoney = manager.sendMoney(-fen);
                if (!sendMoney[0].equals("1")) {
                    return false;
                }
                String time = Utils.getNowTime();
                Observable.create(subscriber -> {
                    CusFormBody body = HttpRequestParams.INSTANCE.getParamsForBluetoothSignMsg(orderNb, UserNb, "12",
                            serialNum[1], null, null, null, time, sendMoney[1], money);
                    Logger.i(body.toString());
                    HttpRequest.Companion.getInstance().httpPost(BluetoothActivity.this, HttpURLS.INSTANCE.getBluetoothSignMsg(), false,
                            "bluetooth", body, (isError, response, type, error) -> {
                                if (isError) {
                                    Logger.e(error.toString());
                                    subscriber.onError(error);
                                } else {
                                    Logger.t("CardManager").i(response.toString());
                                    JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                                    JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                                    String ProcessCode = ResponseHead.getString("ProcessCode");
                                    String ProcessDes = ResponseHead.getString("ProcessDes");
                                    String errorMessage;
                                    if (ProcessCode.equals("0000")) {
                                        String quan = ResponseFields.getString("SignMsg");
                                        quan = Utils.getMapValue(quan, "MAC");
                                        if (quan.equals("-1")) {
                                            errorMessage = "圈存失败 ";
                                        } else {
                                            errorMessage = "写卡失败，重写卡，或到营业厅处理! ";
                                        }

                                        String[] sendQuanMac = manager.sendQuanMac(time, quan);
                                        if (!sendQuanMac[0].equals("1")) {
                                            errorMessage = "圈存指令失败：错误码! ";
                                        }

                                    } else {
                                        errorMessage = ProcessDes;
                                    }
                                    if (error != null && error.getMessage() != null) {
                                        errorMessage = error.getMessage();
                                    } else {
                                        errorMessage = "写卡失败,请确认将燃气CPU卡放在读卡器上，确认网络畅通，然后重试。";
                                    }
                                    new MaterialDialog.Builder(BluetoothActivity.this)
                                            .title("提醒")
                                            .content(errorMessage)
                                            .positiveText("确定")
                                            .cancelable(false)
                                            .canceledOnTouchOutside(false)
                                            .show();
                                }
                            });
                });
            } else {
                String errorMessage;
                if (error1 != null && error1.getMessage() != null) {
                    errorMessage = error1.getMessage();
                } else {
                    errorMessage = "写卡失败,请确认将燃气CPU卡放在读卡器上，确认网络畅通，然后重试。";
                }
                new MaterialDialog.Builder(BluetoothActivity.this)
                        .title("提醒")
                        .content(errorMessage)
                        .positiveText("确定")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .show();
            }
            return false;
        }).subscribe(isOk -> {
            closeProgress();
            Logger.t("CardManager").i(isOk + "");
            MaterialDialog.Builder builder = new MaterialDialog.Builder(BluetoothActivity.this)
                    .title("提醒")
                    .positiveText("确定")
                    .cancelable(false)
                    .onPositive((dialog1, which) -> {
                        RxBus.get().post(new RecordChangedEvent());
                        bt_pay.setVisibility(View.INVISIBLE);
                    })
                    .canceledOnTouchOutside(false);
            if (isOk) {
                builder.content("写卡成功！");
                builder.show();
            }
        });
    }

    private static int TIME_OUT_SCAN = 10000;

    /**
     * 搜索蓝牙
     */
    private void search() {
        disConnect();
        devices = new ArrayList<>();
        liteBluetooth.startLeScan(mLeScanCallback);
        showSearchDialog();
    }

    @Override
    protected void onStop() {
        manager.disConnectBlE();
        super.onStop();
    }

    /**
     * 读卡
     */
    private void readCard(final int type) {
        if (!manager.isBlEConnected()) {
            if (!isConnected)
                ToastUtil.Companion.toastWarning("请连接蓝牙");
            else
                ToastUtil.Companion.toastError("超时，请重新连接蓝牙");
            return;
        }
        showProgress("正在进行读卡,请稍后……");
        new Thread() {
            public void run() {
                CardInfo cardInfo = manager.readCard();
                if (!Utils.isNullOrEmpty(cardInfo.result) && cardInfo.result.equals("0")) {
                    handler.obtainMessage(Constant.READ_CARD_FAIL, cardInfo.errorCode).sendToTarget();
                    return;
                }
                handler.obtainMessage(Constant.READ_CARD_SUCCESS, type, 0, cardInfo).sendToTarget();
            }
        }.start();
    }

    /**
     * 显示读取的卡内容
     */
    private void showInfo(CardInfo cardInfo) {
        BigDecimal thisMoney = new BigDecimal(cardInfo.thisMoney);
        BigDecimal nowPrice = new BigDecimal(cardInfo.nowPrice);
        BigDecimal nowRemainMoney = new BigDecimal(cardInfo.nowRemainMoney);
        BigDecimal toalBuyMoney = new BigDecimal(cardInfo.toalBuyMoney);
        BigDecimal toatalUseGas = new BigDecimal(cardInfo.toatalUseGas);

//        BigDecimal f = new BigDecimal(1);
        BigDecimal f = new BigDecimal(100);
        BigDecimal ten = new BigDecimal(10);

        String thisMoneyString = null;
        String nowRemainMoneyString = null;
        String toalBuyMoneyString = null;

        tv_res.setText("");
        tv_res.append("用户号：" + cardInfo.userCode + "\n");
        tv_res.append("购气次数：" + cardInfo.buyTimes + "\n");
        tv_res.append("表当前时间：" + cardInfo.nowTime + "\n");
        tv_res.append("表当前单价：" + nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");

        switch (shebeiType) {
            case 1:
                thisMoneyString = thisMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                nowRemainMoneyString = nowRemainMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                toalBuyMoneyString = toalBuyMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                tv_res.append("本次购气金额：" + thisMoneyString + "元\n");
                tv_res.append("表剩余金额：" + nowRemainMoneyString + "元\n");
                tv_res.append("表累计购气金额：" + toalBuyMoneyString + "元\n");
                break;
            case 2:
                thisMoneyString = thisMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                nowRemainMoneyString = nowRemainMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                toalBuyMoneyString = toalBuyMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
                tv_res.append("本次购气气量：" + thisMoneyString + "m³\n");
                tv_res.append("表剩余气量：" + nowRemainMoneyString + "m³\n");
                tv_res.append("表累计购气气量：" + toalBuyMoneyString + "m³\n");
                break;
        }

        tv_res.append("表累计用气量：" + toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");


//        tv_res.append("NFC购气次数：" + cardInfo.nfcTimes + "\n");
//        tv_res.append("NFC购气金额：" + nfcMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
//        tv_res.append("NFC总购气金额：" + nfcTotalMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");

//        tv_res.append("用户类型：" + cardInfo.userType + "\n");
//        tv_res.append("公司代码：" + cardInfo.companyCode + "\n");
//        tv_res.append("城市代码：" + cardInfo.cityCode + "\n");
//        tv_res.append("发卡时间：" + cardInfo.openTime + "\n");
//        tv_res.append("用户姓名：" + cardInfo.userName + "\n");
//        tv_res.append("身份证号：" + cardInfo.userID + "\n");
//        tv_res.append("卡类型：" + cardInfo.cardType + "\n");
//        tv_res.append("用户卡标志：" + cardInfo.userFlag + "\n");
//        tv_res.append("参数修改状态：" + cardInfo.paramModifyFlag + "\n");
//        tv_res.append("密钥版本号：" + cardInfo.keyVerson + "\n");
//        tv_res.append("用户卡购气日期：" + cardInfo.buyTime + "\n");
//        tv_res.append("用户卡充值有效期：" + cardInfo.validTime + "\n");
//        tv_res.append("泄漏功能：" + (cardInfo.leakFunction == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("连续用气小时数：" + cardInfo.continuousHours + "\n");
//        tv_res.append("报警联动自动锁功能：" + (cardInfo.wanrAutoLock == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("长期不用气自动锁功能：" + (cardInfo.noUseAutoLock == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("不用气自动锁天数1：" + cardInfo.lockDay1 + "\n");
//        tv_res.append("不用气自动锁天数2：" + cardInfo.lockDay2 + "\n");
//        tv_res.append("过流功能：" + (cardInfo.overflowFun == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("过流量：" + cardInfo.overflowCount + "\n");
//        tv_res.append("过流时间启用：" + (cardInfo.overflowTimeStart == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("过流时间：" + cardInfo.overflowTime + "\n");
//        tv_res.append("限购功能：" + (cardInfo.limitBuy == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("限购充值上限：" + cardInfo.limitMoney + "\n");
//        tv_res.append("蜂鸣器低额提醒金额：" + cardInfo.lowWarnMoney + "\n");
//        tv_res.append("自动报警1功能：" + (cardInfo.autoWarnStart1 == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("报警值1：" + cardInfo.warnValue1 + "\n");
//        tv_res.append("自动报警2功能：" + (cardInfo.autoWarnStart2 == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("报警值2：" + cardInfo.warnValue2 + "\n");
//        tv_res.append("0元关阀功能：" + (cardInfo.zeroClose == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("启动安检功能：" + (cardInfo.securityCheckStart == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("安检月份：" + cardInfo.securityMonth + "\n");
//        tv_res.append("启动报废表功能：" + (cardInfo.scrapStart == 1 ? "开启" : "关闭") + "\n");
//        tv_res.append("报废表年期：" + cardInfo.scrapYear + "\n");
//        tv_res.append("结算方式：" + cardInfo.versonFlag + "\n");
//        tv_res.append("单价类型：" + cardInfo.userFlag2 + "\n");
//        tv_res.append("累计消耗记录日：" + cardInfo.recordDate + "\n");
//        tv_res.append("当前使用价格组1：" + cardInfo.priceGroupC1 + "\n");
//        tv_res.append("当期使用价格组2：" + cardInfo.priceGroupC2 + "\n");
//        tv_res.append("新价格组1：" + cardInfo.priceGroupN1 + "\n");
//        tv_res.append("新价格组2：" + cardInfo.priceGroupN2 + "\n");
//        tv_res.append("新单价生效日期：" + cardInfo.newPriceStart + "\n");
//        tv_res.append("价格启用循环：" + cardInfo.priceStartCycling + "\n");
//        tv_res.append("新价格启用循环：" + cardInfo.newPriceStartRepeat + "\n");
//        tv_res.append("计费方式：" + (cardInfo.valWay == 85 ? "计量" : "计金额") + "\n");
//        tv_res.append("应用信息备用字节：" + cardInfo.backupData + "\n");
//        tv_res.append("无用气天数：" + cardInfo.noUseDayCount + "\n");
//        tv_res.append("无用气秒数：" + cardInfo.noUseSecondsCount + "\n");
//        tv_res.append("表当前状态：" + cardInfo.nowStatus + "\n");
//        tv_res.append("消费交易状态字：" + cardInfo.dealWords + "\n");
//        tv_res.append("月用量记录：" + cardInfo.monthUseList + "\n");
//        tv_res.append("安检返写条数：" + cardInfo.securityCounts + "\n");
//        tv_res.append("安检返写记录：" + cardInfo.securityRecord + "\n");
//        tv_res.append("最近一次关阀记录：" + cardInfo.recentClose + "\n");
//
//        tv_res.append(
//                "月累计消耗量日期：" + (cardInfo.historyMonthList == null ? "" : cardInfo.historyMonthList.toString()) + "\n");
//        tv_res.append("TCIS调价日：" + Utils.getString(cardInfo.addjustDate) + "\n");
//        tv_res.append("调价日底数：" + Utils.getString(cardInfo.addjustBottom) + "\n");
//        tv_res.append("刷卡充值日：" + Utils.getString(cardInfo.payDate) + "\n");
//        tv_res.append("刷卡充值日底数：" + Utils.getString(cardInfo.payBottom) + "\n");
//        tv_res.append("反馈信息备用字节：" + Utils.getString(cardInfo.backupData2) + "\n");
    }

    /**
     * 下电
     */
    private void powerOff() {
        if (!manager.isBlEConnected()) {
            if (!isConnected)
                ToastUtil.Companion.toastWarning("请连接蓝牙");
            else
                ToastUtil.Companion.toastError("超时，请重新连接蓝牙");
            return;
        }
        showProgress("正在进行下电,请稍后……");
        new Thread() {
            public void run() {
                String[] result = manager.powerOff();
                if (result[0].equals("1")) {
                    handler.obtainMessage(Constant.POWER_OFF_SUCCESS).sendToTarget();
                } else {
                    handler.obtainMessage(Constant.POWER_OFF_FAIL, result[1]).sendToTarget();
                }
            }
        }.start();
    }

    /**
     * 上电
     */
    private void powerOn(final int type) {
        if (!manager.isBlEConnected()) {
            if (!isConnected)
                ToastUtil.Companion.toastWarning("请连接蓝牙");
            else
                ToastUtil.Companion.toastError("超时，请重新连接蓝牙");
            return;
        }
        showProgress("正在进行上电,请稍后……");
        new Thread() {
            public void run() {
                String[] res = manager.powerOn();
                if (res[0].equals("1")) {
                    handler.obtainMessage(Constant.POWER_ON_SUCCESS, type).sendToTarget();
                } else {
                    handler.obtainMessage(Constant.POWER_ON_FAIL, res[1]).sendToTarget();
                }
            }
        }.start();
    }

    /**
     * 连接蓝牙
     */
    private void connect(String device) {

        tv_status.setText("正在连接……");
        showProgress("正在连接蓝牙,请稍后……");
        new Thread() {
            public void run() {
                boolean isConn = manager.connectBlE(device);
                if (isConn) {
                    handler.obtainMessage(Constant.BLE_CONNECT_SUCCESS).sendToTarget();
                } else {
                    handler.obtainMessage(Constant.BLE_CONNECT_FAIL).sendToTarget();
                }
            }
        }.start();
    }

    /**
     * 断开蓝牙
     */
    private void disConnect() {
//        if (!manager.isBlEConnected()) {
//            ToastUtil.Companion.toastWarning("蓝牙未连接");
//            return;
//        }
        manager.disConnectBlE();
        tv_status.setText("未连接");
        tv_res.setText("蓝牙已断开");
    }

    private void showProgress(String msg) {
        dialog = new MaterialDialog.Builder(this)
                .content(msg)
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void closeProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private final PeriodScanCallback mLeScanCallback = new PeriodScanCallback(TIME_OUT_SCAN) {
        @Override
        public void onScanTimeout() {
            if (pb != null) {
                pb.setVisibility(View.GONE);
            }
            showList();
            handler.sendEmptyMessage(SHOW_DEVICE_LIST);
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Logger.i("device: " + device.getName() + "  mac: " + device.getAddress()
                    + "  rssi: " + rssi + "  scanRecord: " + Arrays.toString(scanRecord));
            if (!devices.contains(device) && !TextUtils.isEmpty(device.getName())) {
                devices.add(device);
                showList();
                handler.sendEmptyMessage(SHOW_DEVICE_LIST);
            }
        }
    };

    private ProgressBar pb;

    private static final String STATE_MONEY = "money";
    private static final String STATE_TIME = "times";
    private static final String STATE_ORDERNB = "orderNb";
    private static final String STATE_USERNB = "UserNb";

    @Override
    public void onActivitySaveInstanceState(@NotNull Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_MONEY, money);
        savedInstanceState.putString(STATE_TIME, times);
        savedInstanceState.putString(STATE_ORDERNB, orderNb);
        savedInstanceState.putString(STATE_USERNB, UserNb);
    }

    @Override
    public void onActivityRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        money = savedInstanceState.getString(STATE_MONEY);
        times = savedInstanceState.getString(STATE_TIME);
        orderNb = savedInstanceState.getString(STATE_ORDERNB);
        UserNb = savedInstanceState.getString(STATE_USERNB);
    }

    @SuppressLint("ViewHolder")
    private static class MyAdapter extends BaseAdapter {

        private List<BluetoothDevice> devices;
        private Context context;

        public MyAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            this.devices = devices;
        }

        public void setDevices(List<BluetoothDevice> devices) {
            this.devices = devices;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return devices == null ? 0 : devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.name);
                holder.tv_addr = (TextView) convertView.findViewById(R.id.addr);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name.setText(devices.get(position).getName());
            holder.tv_addr.setText(devices.get(position).getAddress());
            return convertView;
        }

        class ViewHolder {
            TextView tv_name;
            TextView tv_addr;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (progressDialog != null && progressDialog.isShowing()) {// 如果搜索蓝牙对话框显示则消去
//                progressDialog.dismiss();
            liteBluetooth.stopScan(mLeScanCallback);
//            } else {
            finish();
//            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnViewClick(View v) {
        tv_res.setText("");
        switch (v.getId()) {
            case R.id.ib_pic_right:// 搜索
                search();
                break;
            case R.id.bt_read:// 读取
                powerOn(1);
                break;
            case R.id.bt_pay:// 充值（交易宝）
                powerOn(2);
                break;
        }
    }

}
