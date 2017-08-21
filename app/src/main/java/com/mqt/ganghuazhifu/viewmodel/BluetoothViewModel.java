//package com.mqt.ganghuazhifu.viewmodel;
//
//import android.content.Intent;
//import android.databinding.BaseObservable;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.View;
//
//import com.litesuits.bluetooth.LiteBluetooth;
//import com.mqt.ganghuazhifu.activity.BluetoothActivity;
//
//import java.util.ArrayList;
//
///**
// * Created by danding1207 on 16/10/14.
// */
//
//public class BluetoothViewModel extends BaseObservable implements ViewModel {
//
//    private BluetoothActivity bluetoothActivity;
//
//    public BluetoothViewModel(BluetoothActivity bluetoothActivity) {
//        this.bluetoothActivity = bluetoothActivity;
//    }
//
//
//    /**
//     * 蓝牙主要操作对象，建议单例。
//     */
//    private static LiteBluetooth liteBluetooth;
//
//
//
//
//
//
//
//    /**  生命周期 */
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    public void onStart() {
//
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//    }
//
//    @Override
//    public void onStop() {
//
//    }
//
//    @Override
//    public void onDestroyView() {
//
//    }
//
//    @Override
//    public void onDestroy() {
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//    }
//
//
//    /**  数据绑定 */
//
//
//
//
//    /**  业务处理 */
//    public void refresh() {
//
//    }
//
//    /**
//     * 搜索蓝牙
//     */
//    private void search() {
//        manager.disConnectBlE();
//        devices = new ArrayList<>();
//        liteBluetooth.startLeScan(mLeScanCallback);
//        showSearchDialog();
//    }
//
//
//}
