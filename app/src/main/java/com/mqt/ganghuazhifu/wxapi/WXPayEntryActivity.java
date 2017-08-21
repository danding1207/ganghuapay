package com.mqt.ganghuazhifu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mqt.ganghuazhifu.App;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.activity.UnityPayResultActivity;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity_pay_result);
        api = WXAPIFactory.createWXAPI(this, "wx2a5538052969956e");//appid需换成商户自己开放平台appid
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            // resp.errCode == -1 原因：支付错误,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            // resp.errCode == -2 原因 用户取消,无需处理。发生场景：用户不支付了，点击取消，返回APP
            if (resp.errCode == 0) {// 支付成功
//                ToastUtil.Companion.toastSuccess(resp.errStr);
                UnityPayResultActivity.Companion.startActivity(this, EncryptedPreferencesUtils.getWXPayId());
                finish();
            } else {
                ToastUtil.Companion.toastError(resp.errStr);
                finish();
            }
        }
    }
}