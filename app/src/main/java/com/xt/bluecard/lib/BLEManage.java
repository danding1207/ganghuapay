//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard.lib;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.SystemClock;

import com.xt.bluecard.lib.util.DataTypeHelper;
import com.xt.bluecard.lib.util.HexDump;
import com.xt.bluecard.lib.util.LocalUUID;
import com.xt.bluecard.lib.util.LogUtil;
import com.xt.bluecard.lib.util.ProtocolFormat;

@SuppressLint({"NewApi"})
public class BLEManage implements IConnectState {
    private static final String TAG = "BLEManager";
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGatt mBluetoothGatt = null;
    private String mBluetoothDeviceAddress = null;
    private Context mContext = null;
    private BluetoothGattCharacteristic mReadGattCharacteristic = null;
    private BluetoothGattCharacteristic mWriteGattCharacteristic = null;
    private BluetoothGattCharacteristic mBatreryGattCharacteristic = null;
    private DataFlag mDataFlag;
    private byte[] resultData = null;
    private String batteryData = null;
    private boolean ibattery = false;
    private int currentConnectState = -1;
    private final int OPEN_ERROR_SUCCESS = 0;
    private final int OPEN_ERROR_OTHER_FAILED = -9;
    private final int OPEN_ERROR_TIMOUT = -1;
    private final long SELEEP_TIME = 100L;
    private StringBuffer mStringBuffer = null;
    private int BWT_TIMS = 0;
    private int BWT_TIME_BASE = 3000;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch(newState) {
            case 0:
                BLEManage.this.setState(0);
                LogUtil.d("BLEManager", "onConnectionStateChange newState = CONN_STATE_DISCONNECTED");
                BLEManage.this.close();
                break;
            case 1:
                BLEManage.this.setState(1);
                LogUtil.d("BLEManager", "onConnectionStateChange newState = CONN_STATE_CONNECTING");
                break;
            case 2:
                BLEManage.this.setState(2);
                LogUtil.d("BLEManager", "onConnectionStateChange newState = CONN_STATE_CONNECTED");
            }

        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == 0) {
                BluetoothGattService mBluetoothGattService_Write = gatt.getService(LocalUUID.UUID_ISSC_SERVICE_WRITE);
                BluetoothGattService mBluetoothGattService_Read = gatt.getService(LocalUUID.UUID_ISSC_SERVICE_READ);
                BluetoothGattService mBluetoothGattService_Batrery = gatt.getService(LocalUUID.UUID_ISSC_SERVICE_BATTERY);
                if(mBluetoothGattService_Batrery != null) {
                    BLEManage.this.mBatreryGattCharacteristic = mBluetoothGattService_Batrery.getCharacteristic(LocalUUID.UUID_ISSC_CHARACTERISTIC_BATTERY);
                }

                if(mBluetoothGattService_Write != null && mBluetoothGattService_Read != null) {
                    BLEManage.this.mWriteGattCharacteristic = mBluetoothGattService_Write.getCharacteristic(LocalUUID.UUID_ISSC_CHARACTERISTIC_WRITE);
                    BLEManage.this.mReadGattCharacteristic = mBluetoothGattService_Read.getCharacteristic(LocalUUID.UUID_ISSC_CHARACTERISTIC_READ);
                    if(BLEManage.this.mWriteGattCharacteristic != null && BLEManage.this.mReadGattCharacteristic != null) {
                        BLEManage.this.setCharacteristicNotification(BLEManage.this.mReadGattCharacteristic, true);
                        LogUtil.d("BLEManager", "mWriteGattCharacteristic & mReadGattCharacteristic is ready!");
                    }
                } else {
                    LogUtil.d("BLEManager", "没有获取到银联UUID的服务，重新尝试新定义的UUID ");
                    mBluetoothGattService_Write = gatt.getService(LocalUUID.UUID_ISSC_SERVICE_WRITE_1);
                    mBluetoothGattService_Read = gatt.getService(LocalUUID.UUID_ISSC_SERVICE_READ_1);
                    if(mBluetoothGattService_Write != null && mBluetoothGattService_Read != null) {
                        BLEManage.this.mWriteGattCharacteristic = mBluetoothGattService_Write.getCharacteristic(LocalUUID.UUID_ISSC_CHARACTERISTIC_WRITE_1);
                        BLEManage.this.mReadGattCharacteristic = mBluetoothGattService_Read.getCharacteristic(LocalUUID.UUID_ISSC_CHARACTERISTIC_READ_1);
                        if(BLEManage.this.mWriteGattCharacteristic != null && BLEManage.this.mReadGattCharacteristic != null) {
                            BLEManage.this.setCharacteristicNotification(BLEManage.this.mReadGattCharacteristic, true);
                            LogUtil.d("BLEManager", "mWriteGattCharacteristic & mReadGattCharacteristic is ready!");
                        }
                    }
                }
            } else {
                LogUtil.w("BLEManager", "onServicesDiscovered received: " + status);
            }

        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == 0) {
                LogUtil.e("BLEManager", "onCharRead " + gatt.getDevice().getName() + " read " + characteristic.getUuid().toString() + " -> " + HexDump.toHexString(characteristic.getValue()));
                if(characteristic.getUuid().equals(LocalUUID.UUID_ISSC_CHARACTERISTIC_BATTERY)) {
                    byte[] r = characteristic.getValue();

                    try {
                        int e = DataTypeHelper.BigEndian.bytesToInt(r, 0);
                        if(e >= 300) {
                            e -= 300;
                        }

                        BLEManage.this.batteryData = "" + e;
                        LogUtil.d("BLEManager", "成功读取电池电量 : " + e);
                    } catch (Exception var6) {
                        LogUtil.e("BLEManager", "电池电量格式错误 : " + var6.getMessage());
                    }

                    BLEManage.this.ibattery = true;
                }
            }

        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == 0) {
                LogUtil.e("BLEManager", "onCharWrite " + gatt.getDevice().getName() + " write " + characteristic.getUuid().toString() + " -> " + HexDump.toHexString(characteristic.getValue()));
            }

        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            LogUtil.e("onCharChanged " + gatt.getDevice().getName() + " changed " + characteristic.getUuid().toString() + " -> " + HexDump.toHexString(characteristic.getValue()));
            byte[] result = characteristic.getValue();
            BLEManage.this.handleResult(result);
        }
    };

    public BLEManage(Context mContext) {
        this.mContext = mContext;
    }

    public boolean init() {
        this.disConnect();
        return this.initBLE();
    }

    public boolean connectTo(String address) {
        int code = this.openChannel(address, 5000L);
        return code == 0;
    }

    public boolean disConnect() {
        return this.closeChannel();
    }

    public int transCommand(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, long paramLong) {
        if(paramArrayOfByte1 != null && paramArrayOfByte1.length != 0) {
            this.sendData(paramArrayOfByte1, paramLong);
            byte[] result = this.receiveData(paramLong);
            if(result != null && result.length != 0) {
                System.arraycopy(result, 0, paramArrayOfByte2, 0, result.length);
                return result.length;
            }
        }

        return 0;
    }

    private synchronized void setState(int state) {
        this.currentConnectState = state;
    }

    public synchronized int getState() {
        return this.currentConnectState;
    }

    private boolean initBLE() {
        if(!this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            LogUtil.e("BLEManager", "NO FEATURE_BLUETOOTH_LE");
            return false;
        } else {
            if(this.mBluetoothManager == null) {
                this.mBluetoothManager = (BluetoothManager)this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            }

            if(this.mBluetoothManager == null) {
                LogUtil.e("BLEManager", "Unable to initialize BluetoothManager.");
                return false;
            } else {
                if(this.mBluetoothAdapter == null) {
                    this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
                }

                if(this.mBluetoothAdapter == null) {
                    LogUtil.e("BLEManager", "Unable to obtain a BluetoothAdapter.");
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private boolean connect(String address) {
        if(this.mBluetoothAdapter != null && address != null && BluetoothAdapter.checkBluetoothAddress(address)) {
            if(this.mBluetoothDeviceAddress != null && address.equals(this.mBluetoothDeviceAddress) && this.mBluetoothGatt != null) {
                LogUtil.d("BLEManager", "Trying to use an existing mBluetoothGatt for connection.");
                return this.mBluetoothGatt.connect();
            } else {
                BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
                if(device == null) {
                    LogUtil.w("BLEManager", "Device not found.  Unable to connect.");
                    return false;
                } else {
                    this.mBluetoothGatt = device.connectGatt(this.mContext, false, this.mGattCallback);
                    LogUtil.d("BLEManager", "Trying to create a new connection.");
                    this.mBluetoothDeviceAddress = address;
                    return true;
                }
            }
        } else {
            LogUtil.w("BLEManager", "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
    }

    private void disconnect() {
        if(this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        } else {
            LogUtil.w("disconnect", "BluetoothAdapter || mBluetoothGatt not initialized");
        }
    }

    private void close() {
        if(this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    private void handleResult(byte[] result) {
        if(this.mDataFlag != DataFlag.DATA_SUCCESS && result != null) {
            try {
                int e = ProtocolFormat.checkWaitTime(result);
                if(e > 0) {
                    this.BWT_TIMS = e;
                    LogUtil.e("ProtocolFormat", "wait times request = " + e);
                    this.mDataFlag = DataFlag.DATA_WAIT_REQUEST;
                    return;
                }

                this.mStringBuffer.append(HexDump.toHexString(result));
                this.resultData = HexDump.hexStringToByteArray(this.mStringBuffer.toString());
                if(this.isOver(this.resultData)) {
                    switch(ProtocolFormat.checkPacketdata(this.resultData)) {
                    case 0:
                    case 3:
                        this.mDataFlag = DataFlag.DATA_SUCCESS;
                    case 1:
                    default:
                        break;
                    case 2:
                    case 4:
                        this.writeData(ProtocolFormat.Protocol_Subpackage_Cmd_Surplus);
                    }

                    LogUtil.d("handleResult", "mDataFlag = DataFlag.DATA_SUCCESS");
                }
            } catch (Exception var3) {
                LogUtil.e("handleResult", "handleResult error");
                var3.printStackTrace();
            }
        }

    }

    private boolean isOver(byte[] result) {
        try {
            byte[] e = new byte[]{result[1], result[2], result[3], result[4]};
            int responseDataLength = (e[3] & 255) << 24 | (e[2] & 255) << 16 | (e[1] & 255) << 8 | e[0] & 255;
            if(responseDataLength == result.length - 10) {
                return true;
            }
        } catch (Exception var4) {
            LogUtil.e("isOver", var4.getMessage());
        }

        return false;
    }

    private boolean discoverServices() {
        return this.mBluetoothGatt != null && this.currentConnectState == 2?this.mBluetoothGatt.discoverServices():false;
    }

    private boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            return this.mBluetoothGatt.readCharacteristic(characteristic);
        } else {
            LogUtil.w("readCharacteristic", "BluetoothAdapter or mBluetoothGatt not initialized");
            return false;
        }
    }

    private boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if(this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            return true;
        } else {
            LogUtil.w("BLEManager", "BluetoothAdapter or mBluetoothGatt not initialized");
            return false;
        }
    }

    private boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            return this.mBluetoothGatt.writeCharacteristic(characteristic);
        } else {
            LogUtil.w("BLEManager", "BluetoothAdapter or mBluetoothGatt not initialized");
            return false;
        }
    }

    private int openChannel(String address, long timeout) {
        if(this.currentConnectState == 2) {
            this.disconnect();
        }

        this.currentConnectState = -1;
        this.mWriteGattCharacteristic = null;
        this.mReadGattCharacteristic = null;
        long timout = timeout;
        long timeCount = 0L;
        if(address != null && !BluetoothAdapter.checkBluetoothAddress(address)) {
            LogUtil.d("checkBluetoothAddress", "address is invalid");
            return -9;
        } else {
            if(this.connect(address)) {
                while(this.currentConnectState != 2) {
                    timeCount = SystemClock.elapsedRealtime();

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }

                    if((timout -= SystemClock.elapsedRealtime() - timeCount) <= 0L) {
                        LogUtil.w("BLEManager", "connect time out");
                        return -1;
                    }
                }
            }

            if(this.discoverServices()) {
                while(this.mWriteGattCharacteristic == null || this.mReadGattCharacteristic == null) {
                    timeCount = SystemClock.elapsedRealtime();

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var9) {
                        var9.printStackTrace();
                    }

                    if((timout -= SystemClock.elapsedRealtime() - timeCount) <= 0L) {
                        LogUtil.w("BLEManager", "discoverServices time out");
                        return -1;
                    }
                }
            }

            return 0;
        }
    }

    private boolean closeChannel() {
        this.disconnect();
        return true;
    }

    private boolean sendData(byte[] onlyData, long timeout) {
        this.mStringBuffer = new StringBuffer();
        this.mStringBuffer.setLength(0);
        this.mDataFlag = DataFlag.DATA_NONE;
        this.resultData = null;
        byte[] dataTotal = ProtocolFormat.pakaging(onlyData);
        if(dataTotal == null) {
            return false;
        } else {
            int len = dataTotal.length;
            int n = len / 20;
            if(n < 1) {
                return this.writeData(dataTotal);
            } else {
                int tail;
                byte[] arrayTmp;
                for(tail = 0; tail < n; ++tail) {
                    arrayTmp = new byte[20];
                    System.arraycopy(dataTotal, tail * 20, arrayTmp, 0, 20);
                    if(!this.writeData(arrayTmp)) {
                        return false;
                    }

                    SystemClock.sleep(10L);
                }

                if(n * 20 < len) {
                    tail = len % 20;
                    arrayTmp = new byte[tail];
                    System.arraycopy(dataTotal, n * 20, arrayTmp, 0, tail);
                    if(!this.writeData(arrayTmp)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private byte[] receiveData(long timeout) {
        this.BWT_TIMS = 0;
        long bTime = SystemClock.elapsedRealtime();

        while(SystemClock.elapsedRealtime() - bTime < timeout && this.mDataFlag != DataFlag.DATA_SUCCESS) {
            if(this.mDataFlag == DataFlag.DATA_WAIT_REQUEST) {
                timeout += (long)(this.BWT_TIME_BASE * this.BWT_TIMS);
                this.BWT_TIMS = 0;
            }
        }

        return ProtocolFormat.unpakaging(this.resultData);
    }

    private boolean writeData(byte[] dataArray) {
        if(this.mWriteGattCharacteristic == null) {
            LogUtil.w("BLEManager", "mWriteGattCharacteristic not initialized");
            return false;
        } else {
            LogUtil.d("writeData", HexDump.toHexString(dataArray));
            if(dataArray != null && this.mWriteGattCharacteristic.setValue(dataArray)) {
                this.mWriteGattCharacteristic.setWriteType(1);
                return this.writeCharacteristic(this.mWriteGattCharacteristic);
            } else {
                return false;
            }
        }
    }

    public String getBatteryLevel(long timeout) {
        this.batteryData = null;
        this.ibattery = false;
        if(this.mBatreryGattCharacteristic != null) {
            if(this.mBatreryGattCharacteristic != null) {
                this.setCharacteristicNotification(this.mBatreryGattCharacteristic, true);
            }

            if(this.readCharacteristic(this.mBatreryGattCharacteristic)) {
                long bTime = SystemClock.elapsedRealtime();

                while(SystemClock.elapsedRealtime() - bTime < timeout && !this.ibattery) {
                    ;
                }
            }
        }

        return this.batteryData;
    }

    public static enum DataFlag {
        DATA_NONE,
        DATA_SUCCESS,
        DATA_WAIT_REQUEST;

        private DataFlag() {
        }
    }
}
