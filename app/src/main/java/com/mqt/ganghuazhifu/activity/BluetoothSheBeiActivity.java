package com.mqt.ganghuazhifu.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import com.litesuits.bluetooth.LiteBleGattCallback;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.BleCharactCallback;
import com.litesuits.bluetooth.conn.BleDescriptorCallback;
import com.litesuits.bluetooth.conn.LiteBleConnector;
import com.litesuits.bluetooth.exception.BleException;
import com.litesuits.bluetooth.log.BleLog;
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
import com.xt.bluecard.Constant;
import com.xt.bluecard.lib.util.HexDump;
import com.xtkj.nfcjar.StringUtil;
import com.xtkj.zd.service.BleResultLogic;
import com.xtkj.zd.service.SendOrderLogic;
import com.xtkj.zd.service.bean.PayResultBean;
import com.xtkj.zd.service.bean.ReadResultBean;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 读NFC燃气表
 *
 * @author yang.lei
 * @date 2014-12-24
 */
@SuppressLint("NewApi")
public class BluetoothSheBeiActivity extends BaseActivity implements OnItemClickListener {

    protected static final int POWER_ON = 4;// 上电;
    private static final String STATE_MONEY = "money";
    private static final String STATE_TIME = "times";
    private static final String STATE_ORDERNB = "orderNb";
    private static final String STATE_USERNB = "UserNb";

    /**
     * 蓝牙主要操作对象，建议单例。
     */
    private static LiteBluetooth liteBluetooth;
    private static int TIME_OUT_SCAN = 10000;
    private final int SHOW_DEVICE_LIST = 0;// 显示蓝牙列表

    private static final int NFC_QUERY = 1;// 读表信息
    private static final int ADD_NUM = 2;// 充值
    private int clickBtNum;// 标记点击的按钮


    private String UUID_SERVICE = "0000FF12-0000-1000-8000-00805F9B34FB";
    private String UUID_WRITE = "0000FF01-0000-1000-8000-00805F9B34FB";
    private String UUID_NOTIFY = "0000FF02-0000-1000-8000-00805F9B34FB";
    private String UUID_DESCRIPTOR_READ = "00002902-0000-1000-8000-00805F9B34FB";

    private List<BluetoothDevice> devices = new ArrayList<>();// 搜索到的蓝牙集合
    private BluetoothDevice device = null;
    private MyAdapter mAdapter;
    private MaterialDialog dialog;
    private AlertDialog bluetoothDialog;
    private ListView lv_devices;
    private boolean isConnected = false;
    private String money;
    private String times;
    private String orderNb;
    private String UserNb;
    private String SignMsg;
    private String DesCmd;
    private Toolbar toolbar;
    private ImageView ib_pic_right;
    private TextView tv_name, tv_addr, tv_status, tv_res;
    private Button bt_read, bt_pay;


    private StringBuilder readMeterResult = new StringBuilder();

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Logger.i("msg==" + msg.what);
            closeProgress();
            MaterialDialog.Builder builder = new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
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
                    isConnected = false;
                    break;
                case Constant.BLE_CONNECT_SUCCESS://
                    tv_status.setText("已连接");
                    LiteBleConnector connector = liteBluetooth.newBleConnector();
                    connector.withUUIDString(UUID_SERVICE, UUID_NOTIFY, null)
                            .enableCharacteristicNotification(bleCharactCallback);
                    connector.withUUIDString(UUID_SERVICE, UUID_NOTIFY, UUID_DESCRIPTOR_READ)
                            .writeDescriptor(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, new BleDescriptorCallback() {
                                @Override
                                public void onSuccess(BluetoothGattDescriptor bluetoothGattDescriptor) {
                                    isConnected = true;
                                    BleLog.i("",
                                            "write descriptor Success, DATA: " + Arrays.toString(bluetoothGattDescriptor.getValue()));
                                }
                                @Override
                                public void onFailure(BleException exception) {
                                    BleLog.i("", "write descriptor failure : " + exception);
                                }
                            });
                    connector.withUUIDString(UUID_SERVICE, UUID_NOTIFY, UUID_DESCRIPTOR_READ)
                            .enableDescriptorNotification(new BleDescriptorCallback() {
                                @Override
                                public void onSuccess(BluetoothGattDescriptor descriptor) {
                                    BleLog.i("",
                                            "Notification descriptor Success, DATA: " + Arrays.toString(descriptor.getValue()));
                                }

                                @Override
                                public void onFailure(BleException exception) {
                                    BleLog.i("", "Notification descriptor failure : " + exception);
                                }
                            });
                    break;
                case Constant.READ_CARD_SUCCESS://
                    readMeterResult.append(msg.obj.toString());
                    Logger.e("readMeterResult--->" + readMeterResult.toString());
                    if (msg.obj.toString().length() < 40 && msg.obj.toString().endsWith("16")) {
                        if (readMeterResult.toString().length() % 336 == 0) {
                            int num = readMeterResult.toString().length() / 336;
                            if (num > 1) {
                                StringBuilder value = new StringBuilder();
                                for (int i = 0; i < 8; i++) {
                                    value.append(readMeterResult.toString().substring(i * 40 * num, i * 40 * num + 40));
                                    Logger.e("value--->" + value);
                                }
                                value.append(readMeterResult.toString().substring(8 * 40 * num, 8 * 40 * num + 16));
                                Logger.e("value--->" + value);
                                readMeterResult = value;
                            }
                            showInfo();
                        } else if (readMeterResult.toString().length() % 382 == 0) {
                            int num = readMeterResult.toString().length() / 382;
                            if (num > 1) {
                                StringBuilder value = new StringBuilder();
                                for (int i = 0; i < 9; i++) {
                                    value.append(readMeterResult.toString().substring(i * 40 * num, i * 40 * num + 40));
                                    Logger.e("value--->" + value);
                                }
                                value.append(readMeterResult.toString().substring(9 * 40 * num, 9 * 40 * num + 22));
                                Logger.e("value--->" + value);
                                readMeterResult = value;
                            }
                            successInfo();
                        } else if (readMeterResult.toString().length() == 34
                                && readMeterResult.toString().substring(4, 6).equalsIgnoreCase("FF")
                                && readMeterResult.toString().endsWith("16")) {
                            int errorCode = BleResultLogic.validateFrame(readMeterResult.toString());
                            String errorDesc = "其他错误！";
                            switch (errorCode) {
                                case 1:
                                    errorDesc = "报文头不合法！";
                                    break;
                                case 2:
                                    errorDesc = "报文尾不合法！";
                                    break;
                                case 3:
                                    errorDesc = "报文校验错误！";
                                    break;
                                case 4:
                                    errorDesc = "充值报文解密错误！";
                                    break;
                                case 5:
                                    errorDesc = "订单用户号与表不一致！";
                                    break;
                                case 6:
                                    errorDesc = "订单用户号与表不一致！";
                                    break;
                                case 8:
                                    errorDesc = "报文密码错误！";
                                    break;
                                case 9:
                                    errorDesc = "密钥过有效期！";
                                    break;
                                case 10:
                                    errorDesc = "报文数据域长度不合法！";
                                    break;
                                case 11:
                                    errorDesc = "非法控制字！";
                                    break;
                            }
                            new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
                                    .title("提醒")
                                    .content(errorDesc)
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("确定")
                                    .build().show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private ProgressBar pb;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_shebei);
        initView();
        initSearchDialog();
        mAdapter = new MyAdapter(this, devices);
        lv_devices.setAdapter(mAdapter);
    }

    private void initView() {
        UserNb = getIntent().getStringExtra("UserNb");
        orderNb = getIntent().getStringExtra("OrderNb");
        money = getIntent().getStringExtra("OrderMoney");
//        NFCICSumCount = getIntent().getIntExtra("NFCICSumCount", 0);
//        ICcardNo = getIntent().getStringExtra("ICcardNo");

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
        getSupportActionBar().setTitle("蓝牙读卡器写表");
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
            Builder builder = new Builder(this);
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
        Logger.d("size--->" + devices.size());
        mAdapter.setDevices(devices);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        tv_name.setText(devices.get(position).getName());
        tv_addr.setText(devices.get(position).getAddress());
        tv_status.setText("未连接");
        bluetoothDialog.dismiss();
        liteBluetooth.stopScan(mLeScanCallback);
        device = devices.get(position);
        liteBluetooth.connect(device, true, liteBleGattCallback);
        showProgress("正在连接蓝牙表具...");
    }

    private LiteBleGattCallback liteBleGattCallback = new LiteBleGattCallback() {
        @Override
        public void onConnectSuccess(BluetoothGatt bluetoothGatt, int i) {
            bluetoothGatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            Log.e("", "notifyDevice start");
            handler.obtainMessage(Constant.BLE_CONNECT_SUCCESS).sendToTarget();
        }

        @Override
        public void onConnectFailure(BleException e) {
            isConnected = false;
            handler.obtainMessage(Constant.BLE_CONNECT_FAIL).sendToTarget();
        }
    };

    private BleCharactCallback bleCharactCallback = new BleCharactCallback() {
        @Override
        public void onSuccess(final BluetoothGattCharacteristic characteristic) {
            BleLog.i("", "Notification characteristic Success, DATA: " + Arrays
                    .toString(characteristic.getValue()));
            handler.obtainMessage(Constant.READ_CARD_SUCCESS, HexDump.toHexString(characteristic.getValue())).sendToTarget();
        }

        @Override
        public void onFailure(BleException exception) {
            BleLog.i("", "Notification characteristic failure: " + exception);
        }
    };

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

    /**
     * 搜索蓝牙
     */
    private void search() {
        devices = new ArrayList<>();
        if (device != null && liteBluetooth.getBluetoothGatt() != null) {
            liteBluetooth.getBluetoothGatt().disconnect();
            liteBluetooth.getBluetoothGatt().close();
            liteBluetooth.refreshDeviceCache();
            device = null;
        }
        tv_status.setText("未连接");
        isConnected = false;
        liteBluetooth.startLeScan(mLeScanCallback);
        showSearchDialog();
    }

    @Override
    protected void onStop() {
        if (device != null && liteBluetooth.getBluetoothGatt() != null) {
            liteBluetooth.getBluetoothGatt().disconnect();
            liteBluetooth.getBluetoothGatt().close();
            liteBluetooth.refreshDeviceCache();
            device = null;
            tv_status.setText("未连接");
            isConnected = false;
        }
        super.onStop();
    }

    /**
     * 显示读取的卡内容
     */
    private void showInfo() {
        tv_res.setText("");
        try {
            ReadResultBean readResultBean = BleResultLogic.readMeter(readMeterResult.toString());
            if (readResultBean != null) {

                switch (clickBtNum) {
                    case NFC_QUERY:
                        BigDecimal currentPrice = new BigDecimal(readResultBean.currentPrice);
                        BigDecimal remainedMoney = new BigDecimal(readResultBean.remainedMoney);
                        BigDecimal totalBuy = new BigDecimal(readResultBean.totalBuy);
                        BigDecimal nfcBuy = new BigDecimal(readResultBean.nfcBuy);
                        BigDecimal nfcTotalBuy = new BigDecimal(readResultBean.nfcTotalBuy);
                        BigDecimal totalUse = new BigDecimal(readResultBean.totalUse);

                        BigDecimal f = new BigDecimal(100);
                        BigDecimal ten = new BigDecimal(10);

                        tv_res.append("表户号： " + readResultBean.meterNum + "\n");
                        tv_res.append("表当前时间： " + readResultBean.currentTime + "\n");
                        tv_res.append("表当前单价(元)： " + currentPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                        tv_res.append("剩余金额(元)： " + remainedMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                        tv_res.append("累计购气金额(元)： " + totalBuy.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                        tv_res.append("累计用气量(m³)： " + totalUse.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                        tv_res.append("无用气天数： " + readResultBean.noUseDay + "\n");
                        tv_res.append("无用气秒数： " + readResultBean.noUseSecond + "\n");
                        tv_res.append("表状态： " + readResultBean.meterState + "\n");
                        tv_res.append("消费交易状态字： " + readResultBean.dealWord + "\n");
                        tv_res.append("安检返写条数： " + readResultBean.securityCount + "\n");
                        for (int i = 0; i < readResultBean.securityRecords.size(); i++) {
                            tv_res.append("安检返写记录" + (i + 1) + "： " + readResultBean.securityRecords.get(i) + "\n");
                        }
                        tv_res.append("蓝牙充值次数： " + readResultBean.nfcTimes + "\n");
                        tv_res.append("蓝牙购气金额(元)： " + nfcBuy.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                        tv_res.append("蓝牙总购气金额(元)： " + nfcTotalBuy.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");

                        for (int i = 0; i < readResultBean.historyList.size(); i++) {
                            tv_res.append("月用量" + (i + 1) + "： " + readResultBean.historyList.get(i) + "\n");
                        }
                        tv_res.append("\n\n");
                        break;
                    case ADD_NUM:
                        getEncode(readResultBean);
                        readMeterResult = new StringBuilder();
                        break;
                }


            } else {
                ToastUtil.Companion.toastWarning("失败，请重试！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 显示读取的卡内容
     */
    private void successInfo() {
        tv_res.setText("");
        try {
            PayResultBean payResultBean = BleResultLogic.addMoney(readMeterResult.toString(), false);
            if (payResultBean != null) {
                BigDecimal remainMoney = new BigDecimal(payResultBean.remainMoney);
                BigDecimal totalPay = new BigDecimal(payResultBean.totalPay);
                BigDecimal totalUse = new BigDecimal(payResultBean.totalUse);
                BigDecimal f = new BigDecimal(100);
                BigDecimal ten = new BigDecimal(10);
                tv_res.append("剩余金额(元)： " + remainMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                tv_res.append("累计购气金额(元)： " + totalPay.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                tv_res.append("累计用气量(m³)： " + totalUse.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "\n");
                tv_res.append("表状态： " + payResultBean.meterState + "\n");
                tv_res.append("蓝牙充值次数： " + payResultBean.payTimes + "\n");
                tv_res.append("\n\n");

                updateStatus(payResultBean);

            } else {
                ToastUtil.Companion.toastWarning("失败，请重试！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (liteBluetooth.isInScanning()) {// 如果搜索蓝牙对话框显示则消去
                liteBluetooth.stopScan(mLeScanCallback);
            } else {
                finish();
            }
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
                clickBtNum = NFC_QUERY;
                readShebei();
                break;
            case R.id.bt_pay:// 充值（交易宝）
                clickBtNum = ADD_NUM;
                readShebei();
                break;
        }
    }

    private void readShebei() {
        if (!isConnected) {
            ToastUtil.Companion.toastWarning("蓝牙连接已断开，请重新连接。");
            return;
        }
        if (device == null) {
            return;
        }
        try {
            readMeterResult = new StringBuilder();
            showProgress("正在读取表具信息...");
            LiteBleConnector connector = liteBluetooth.newBleConnector();
            String value = SendOrderLogic.readMeter(getMeterNum(UserNb));
            BleLog.i("", "value: " + value);
            String result;
            //判断数据长度是否大于40
            int num = value.length() / 40;
            if (num < 1) {
                // 长度小于40直接发送
                connector.withUUIDString(UUID_SERVICE, UUID_WRITE, null)
                        .writeCharacteristic(HexDump.hexStringToByteArray(value),
                                new BleCharactCallback() {
                                    @Override
                                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                                        BleLog.i("", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                                    }

                                    @Override
                                    public void onFailure(final BleException exception) {
                                        BleLog.i("", "Write failure: " + exception);
                                    }
                                });
            } else {
                // 长度大于40分批发送, 一次40
                for (int i = 0; i < num; i++) {
                    result = value.substring(i * 40, (i + 1) * 40);
                    connector.withUUIDString(UUID_SERVICE, UUID_WRITE, null)
                            .writeCharacteristic(HexDump.hexStringToByteArray(result),
                                    new BleCharactCallback() {
                                        @Override
                                        public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                                            BleLog.i("", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                                        }

                                        @Override
                                        public void onFailure(final BleException exception) {
                                            BleLog.i("", "Write failure: " + exception);
                                        }
                                    });
                    SystemClock.sleep(12L);
                }
                if (num * 40 < value.length()) {
                    result = value.substring(num * 40);
                    connector.withUUIDString(UUID_SERVICE, UUID_WRITE, null)
                            .writeCharacteristic(HexDump.hexStringToByteArray(result),
                                    new BleCharactCallback() {
                                        @Override
                                        public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                                            BleLog.i("", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                                        }

                                        @Override
                                        public void onFailure(final BleException exception) {
                                            BleLog.i("", "Write failure: " + exception);
                                        }
                                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void add() {
        if (!isConnected) {
            ToastUtil.Companion.toastWarning("蓝牙连接已断开，请重新连接。");
            return;
        }
        if (device == null) {
            return;
        }
        try {
            readMeterResult = new StringBuilder();
            showProgress("正在圈存金额至表具...");
            LiteBleConnector connector = liteBluetooth.newBleConnector();
            String value = SendOrderLogic.addMoney(SignMsg, false, null, DesCmd);
            BleLog.i("", "value: " + value);
            String result;
            //判断数据长度是否大于40
            int num = value.length() / 40;
            if (num < 1) {
                // 长度小于40直接发送
                connector.withUUIDString(UUID_SERVICE, UUID_WRITE, null)
                        .writeCharacteristic(HexDump.hexStringToByteArray(value),
                                new BleCharactCallback() {
                                    @Override
                                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                                        BleLog.i("", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                                    }

                                    @Override
                                    public void onFailure(final BleException exception) {
                                        BleLog.i("", "Write failure: " + exception);
                                    }
                                });
            } else {
                // 长度大于40分批发送, 一次40
                for (int i = 0; i < num; i++) {
                    result = value.substring(i * 40, (i + 1) * 40);
                    connector.withUUIDString(UUID_SERVICE, UUID_WRITE, null)
                            .writeCharacteristic(HexDump.hexStringToByteArray(result),
                                    new BleCharactCallback() {
                                        @Override
                                        public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                                            BleLog.i("", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                                        }

                                        @Override
                                        public void onFailure(final BleException exception) {
                                            BleLog.i("", "Write failure: " + exception);
                                        }
                                    });
                    SystemClock.sleep(12L);
                }
                if (num * 40 < value.length()) {
                    result = value.substring(num * 40);
                    connector.withUUIDString(UUID_SERVICE, UUID_WRITE, null)
                            .writeCharacteristic(HexDump.hexStringToByteArray(result),
                                    new BleCharactCallback() {
                                        @Override
                                        public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                                            BleLog.i("", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                                        }

                                        @Override
                                        public void onFailure(final BleException exception) {
                                            BleLog.i("", "Write failure: " + exception);
                                        }
                                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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
            BigDecimal ten = new BigDecimal(10);
            if (bean.historyList.size() > 0) {
                for (int i = 0; i < bean.historyList.size(); i++) {
                    if (i == bean.historyList.size() - 1) {
                        sumTotalTwentyFour.append(
                                new BigDecimal(
                                        bean.historyList.get(i))
                                        .divide(ten, 2, BigDecimal.ROUND_HALF_UP)
                                        .toString());
                    } else {
                        sumTotalTwentyFour.append(
                                new BigDecimal(
                                        bean.historyList.get(i))
                                        .divide(ten, 2, BigDecimal.ROUND_HALF_UP)
                                        .toString() + "|");
                    }
                }
            }
            BigDecimal fen = new BigDecimal(100);
            BigDecimal totalUse = new BigDecimal(bean.totalUse);
            BigDecimal nfcTotalBuy = new BigDecimal(bean.nfcTotalBuy);
            BigDecimal currentPrice = new BigDecimal(bean.currentPrice);
            BigDecimal remainedMoney = new BigDecimal(bean.remainedMoney);
            BigDecimal totalBuy = new BigDecimal(bean.totalBuy);
            BigDecimal nfcBuy = new BigDecimal(bean.nfcBuy);

            CusFormBody body = HttpRequestParams.INSTANCE.getParamsForGetNFCCode(
                    orderNb, bean.nfcTimes + 1, UserNb, bean.currentTime + "",
                    currentPrice.divide(fen, 2, BigDecimal.ROUND_HALF_UP).toString(),
                    remainedMoney.divide(fen, 2, BigDecimal.ROUND_HALF_UP).toString(),
                    totalBuy.divide(fen, 2, BigDecimal.ROUND_HALF_UP).toString(),
                    bean.noUseDay + "", bean.noUseSecond + "",
                    bean.meterState + "", bean.dealWord + "",
                    totalUse.divide(ten, 2, BigDecimal.ROUND_HALF_UP).toString(),
                    sumTotalTwentyFour.toString(), bean.securityCount + "", threeSecurityCheck.toString(),
                    nfcBuy.divide(fen, 2, BigDecimal.ROUND_HALF_UP).toString(),
                    nfcTotalBuy.divide(fen, 2, BigDecimal.ROUND_HALF_UP).toString(),
                    null);
//            time = System.currentTimeMillis();
            Logger.i("nfcTimes--->" + (bean.nfcTimes + 1));
            HttpRequest.Companion.getInstance().httpPost(BluetoothSheBeiActivity.this, HttpURLS.INSTANCE.getNFCReadNumberLoopBackAndNFCSignMsg(), true, "NFCSignMsg",
                    body, (isError, response, type, error) -> {
                        if (isError) {
                            Logger.e(error.toString());
                            new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
                                    .title("提示")
                                    .content("请求加密信息失败，请重试！")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("确定")
                                    .build().show();
                        } else {
                            Logger.i(response.toString());
                            JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                            JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                            String ProcessCode = ResponseHead.getString("ProcessCode");
                            String ProcessDes = ResponseHead.getString("ProcessDes");
                            if (ProcessCode.equals("0000") && ResponseFields != null) {
                                SignMsg = ResponseFields.getString("SignMsg");
//                                NFCPayTime = ResponseFields.getInteger("NFCPayTime");
                                Logger.i("SignMsg--->" + SignMsg);
//                                Logger.i("NFCPayTime--->" + NFCPayTime);
                                getDesCmd(bean);
                            } else {
                                new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
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

            CusFormBody body = HttpRequestParams.INSTANCE.getParamsForGetNFCCode(orderNb, bean.nfcTimes + 1,
                    UserNb, bean.currentTime + "",
                    nowPrice.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    nowRemainMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    toalBuyMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    bean.noUseDay + "", bean.noUseSecond + "",
                    bean.meterState + "", bean.dealWord + "",
                    toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    sumTotalTwentyFour.toString(), bean.securityCount + "", threeSecurityCheck.toString(),
                    nfcMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    nfcTotalMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    random);
            HttpRequest.Companion.getInstance().httpPost(BluetoothSheBeiActivity.this, HttpURLS.INSTANCE.getNFCReadNumberLoopBackAndNFCSignMsg(), true, "DesCmd",
                    body, (isError, response, type, error) -> {
                        if (isError) {
                            Logger.e(error.toString());
                            new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
                                    .title("提示")
                                    .content("请求加密信息失败，请重试！")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("确定")
                                    .build().show();
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
                                new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
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

    private void updateStatus(PayResultBean payResultBean) {
        if (isActiveNetwork()) {
            CusFormBody body = HttpRequestParams.INSTANCE.getParamsForUpdateNFCPayStatus(
                    orderNb,
                    "11", payResultBean.adjustBottomNum,
                    payResultBean.payBottomNum);
            HttpRequest.Companion.getInstance().httpPost(BluetoothSheBeiActivity.this,
                    HttpURLS.INSTANCE.getUpdateNFCPayStatus(), true,
                    "NFCSignMsg",
                    body, (isError, response, type, error) -> {
                        if (isError) {
                            Logger.e(error.toString());
                            new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
                                    .title("提醒")
                                    .content("充值结果回抄需要连接网络，请确保您已连接网络再试！")
                                    .onPositive((dialog, which) -> updateStatus(payResultBean))
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText("重试")
                                    .build().show();

                            bt_pay.setEnabled(false);

                        } else {
                            Logger.i(response.toString());
                            JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                            JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                            String ProcessCode = ResponseHead.getString("ProcessCode");
                            String ProcessDes = ResponseHead.getString("ProcessDes");
                            if (ProcessCode.equals("0000")) {
                                Log.e("TAG", "EventBus.getDefault().post");
                                RxBus.get().post(new RecordChangedEvent());
                                new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
                                        .title("提示")
                                        .content("回抄成功，感谢您的使用，如有问题请联系我们!")
                                        .positiveText("确定")
                                        .build().show();
                            } else {
                                ToastUtil.Companion.toastError(ProcessDes);
                            }
                        }
                    });

        } else {
            new MaterialDialog.Builder(BluetoothSheBeiActivity.this)
                    .title("提醒")
                    .content("充值结果回抄需要连接网络，请确保您已连接网络再试！")
                    .onPositive((dialog, which) -> updateStatus(payResultBean))
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("重试")
                    .build().show();
        }
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


}
