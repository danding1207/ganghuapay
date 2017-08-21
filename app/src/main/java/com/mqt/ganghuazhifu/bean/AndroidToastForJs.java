package com.mqt.ganghuazhifu.bean;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

/**
 * Created by danding1207 on 17/1/9.
 */

public class AndroidToastForJs {

    private KuaiQian kuaiQian;

    public AndroidToastForJs(KuaiQian kuaiQian) {
        this.kuaiQian = kuaiQian;
    }

    @JavascriptInterface
    public String getKuaiQianPayType() {
        return TextUtils.isEmpty(kuaiQian.InterFaceName) ? "1" : "2";
    }

    @JavascriptInterface
    public String getMerchantAcctId() {
        return TextUtils.isEmpty(kuaiQian.MerchantAcctId) ? "" : kuaiQian.MerchantAcctId;
    }

    @JavascriptInterface
    public String getInputCharset() {
        return TextUtils.isEmpty(kuaiQian.InputCharset) ? "" : kuaiQian.InputCharset;
    }

    @JavascriptInterface
    public String getPageUrl() {
        return TextUtils.isEmpty(kuaiQian.PageUrl) ? "" : kuaiQian.PageUrl;
    }

    @JavascriptInterface
    public String getBgUrl() {
        return TextUtils.isEmpty(kuaiQian.BgUrl) ? "" : kuaiQian.BgUrl;
    }

    @JavascriptInterface
    public String getVersion() {
        return TextUtils.isEmpty(kuaiQian.Version) ? "" : kuaiQian.Version;
    }

    @JavascriptInterface
    public String getLanguage() {
        return TextUtils.isEmpty(kuaiQian.Language) ? "" : kuaiQian.Language;
    }

    @JavascriptInterface
    public String getSignType() {
        return TextUtils.isEmpty(kuaiQian.SignType) ? "" : kuaiQian.SignType;
    }

    @JavascriptInterface
    public String getPayerName() {
        return TextUtils.isEmpty(kuaiQian.PayerName) ? "" : kuaiQian.PayerName;
    }

    @JavascriptInterface
    public String getPayerContactType() {
        return TextUtils.isEmpty(kuaiQian.PayerContactType) ? "" : kuaiQian.PayerContactType;
    }

    @JavascriptInterface
    public String getPayerContact() {
        return TextUtils.isEmpty(kuaiQian.PayerContact) ? "" : kuaiQian.PayerContact;
    }

    @JavascriptInterface
    public String getOrderId() {
        return TextUtils.isEmpty(kuaiQian.OrderId) ? "" : kuaiQian.OrderId;
    }

    @JavascriptInterface
    public String getOrderAmount() {
        return TextUtils.isEmpty(kuaiQian.OrderAmount) ? "" : kuaiQian.OrderAmount;
    }

    @JavascriptInterface
    public String getOrderTime() {
        return TextUtils.isEmpty(kuaiQian.OrderTime) ? "" : kuaiQian.OrderTime;
    }

    @JavascriptInterface
    public String getProductName() {
        return TextUtils.isEmpty(kuaiQian.ProductName) ? "" : kuaiQian.ProductName;
    }

    @JavascriptInterface
    public String getProductNum() {
        return TextUtils.isEmpty(kuaiQian.ProductNum) ? "" : kuaiQian.ProductNum;
    }

    @JavascriptInterface
    public String getProductId() {
        return TextUtils.isEmpty(kuaiQian.ProductId) ? "" : kuaiQian.ProductId;
    }

    @JavascriptInterface
    public String getProductDesc() {
        return TextUtils.isEmpty(kuaiQian.ProductDesc) ? "" : kuaiQian.ProductDesc;
    }

    @JavascriptInterface
    public String getMobileGateway() {
        return TextUtils.isEmpty(kuaiQian.MobileGateway) ? "" : kuaiQian.MobileGateway;
    }

    @JavascriptInterface
    public String getExt1() {
        return TextUtils.isEmpty(kuaiQian.Ext1) ? "" : kuaiQian.Ext1;
    }

    @JavascriptInterface
    public String getExt2() {
        return TextUtils.isEmpty(kuaiQian.Ext2) ? "" : kuaiQian.Ext2;
    }

    @JavascriptInterface
    public String getPayType() {
        return TextUtils.isEmpty(kuaiQian.PayType) ? "" : kuaiQian.PayType;
    }

    @JavascriptInterface
    public String getBankId() {
        return TextUtils.isEmpty(kuaiQian.BankId) ? "" : kuaiQian.BankId;
    }

    @JavascriptInterface
    public String getRedoFlag() {
        return TextUtils.isEmpty(kuaiQian.RedoFlag) ? "" : kuaiQian.RedoFlag;
    }

    @JavascriptInterface
    public String getPid() {
        return TextUtils.isEmpty(kuaiQian.Pid) ? "" : kuaiQian.Pid;
    }

    @JavascriptInterface
    public String getSignMsg() {
        return TextUtils.isEmpty(kuaiQian.SignMsg) ? "" : kuaiQian.SignMsg;
    }

    @JavascriptInterface
    public String getPayerIdType() {
        return TextUtils.isEmpty(kuaiQian.PayerIdType) ? "" : kuaiQian.PayerIdType;
    }

    @JavascriptInterface
    public String getPayerId() {
        return TextUtils.isEmpty(kuaiQian.PayerId) ? "" : kuaiQian.PayerId;
    }

    @JavascriptInterface
    public String getInterFaceName() {
        return TextUtils.isEmpty(kuaiQian.InterFaceName) ? "" : kuaiQian.InterFaceName;
    }

    @JavascriptInterface
    public String getInterFaceVersion() {
        return TextUtils.isEmpty(kuaiQian.InterFaceVersion) ? "" : kuaiQian.InterFaceVersion;
    }

    @JavascriptInterface
    public String getTranData() {
        return TextUtils.isEmpty(kuaiQian.TranData) ? "" : kuaiQian.TranData;
    }

    @JavascriptInterface
    public String getMerSignMsg() {
        return TextUtils.isEmpty(kuaiQian.MerSignMsg) ? "" : kuaiQian.MerSignMsg;
    }

    @JavascriptInterface
    public String getMerCert() {
        return TextUtils.isEmpty(kuaiQian.MerCert) ? "" : kuaiQian.MerCert;
    }

    @JavascriptInterface
    public String getSubmitURL() {
        return TextUtils.isEmpty(kuaiQian.SubmitURL) ? "" : kuaiQian.SubmitURL;
    }

}
