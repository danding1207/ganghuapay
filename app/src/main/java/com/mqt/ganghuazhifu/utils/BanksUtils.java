package com.mqt.ganghuazhifu.utils;

import com.mqt.ganghuazhifu.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BanksUtils {

    public final HashMap<String, Banks> BANKS = new HashMap<>();
    private static BanksUtils instance;

    private ArrayList<Banks> banksAll = new ArrayList<>();
    private ArrayList<String> nameAll = new ArrayList<>(
            Arrays.asList(
                    "中国银行", "交通银行", "农业银行", "工商银行", "建设银行", "邮储银行",
                    "招商银行", "民生银行", "中信银行", "光大银行", "华夏银行", "广发银行",
                    "兴业银行", "浦发银行", "齐鲁银行", "北京银行", "宁波银行", "平安银行",
                    "温州银行", "广州银行", "龙江银行", "大连银行", "河北银行", "杭州银行",
                    "南京银行", "乌鲁木齐商业银行", "锦州银行", "徽商银行", "重庆银行", "哈尔滨银行",
                    "贵阳银行", "兰州银行", "南昌银行", "青岛银行", "九江银行", "青海银行",
                    "台州银行", "长沙银行", "威海市商业银行", "江苏银行", "承德银行", "浙江民泰商业银行",
                    "上饶银行", "东营市商业银行", "浙江稠州商业银行", "鄂尔多斯银行", "江苏常熟农村商业银行",
                    "顺德农村商业银行", "江阴农村商业银行", "潍坊银行", "上海银行", "重庆农村商业银行",
                    "福建省农村信用社", "鄞州银行", "成都农商银行", "吴江农村商业银行", "无锡农村商业银行",
                    "东亚银行", "浙江泰隆商业银行", "包商银行", "上海农商银行", "更多"));

//    工商银行、光大银行、广发银行、华夏银行、建设银行、民生银行、平安银行、兴业银行、招商银行、中国银行
//     ICBC      CEB      GDB     HXB      CCB    CMBC       SPAB     CIB    CMB     BOC

    private ArrayList<String> codeAll = new ArrayList<>(Arrays.asList(
            "BOC", "COMM", "ABC", "ICBC", "CCB", "PSBC", "CMB", "CMBC", "CITIC", "CEB",
            "HXB", "GDB", "CIB", "SPDB", "QLBANK", "BJB", "NBB", "SPAB", "WZCB", "GCB",
            "DAQINGB", "DLB", "BHB", "HZCB", "NJCB", "URMQCCB", "BOJZ", "HSB", "CQB", "HEBB",
            "GYB", "LZB", "NCB", "QDCCB", "JJCCB", "QHB", "TZCB", "CSCB", "WHSHB", "JSB", "CDB", "MTBANK", "SRB", "DYCCB", "CZCB", "ORBANK", "CSRCB", "SDEB", "JRCB", "WFCCB", "SHB", "CRCB", "FJNXB", "YZB", "CDRCB", "WJRCB", "WRCB", "BEA", "ZJTLCB", "BSB", "SHRCB", "KQ"));
    private ArrayList<Integer> residAll = new ArrayList<>(Arrays.asList(R.drawable.boc, R.drawable.comm, R.drawable.abc, R.drawable.icbc, R.drawable.ccb, R.drawable.psbc, R.drawable.cmb, R.drawable.cmbc, R.drawable.citic, R.drawable.ceb, R.drawable.hxb, R.drawable.gdb, R.drawable.cib, R.drawable.spdb, R.drawable.qlbank, R.drawable.bjb, R.drawable.nbb, R.drawable.spab, R.drawable.wzcb, R.drawable.gcb, R.drawable.daqingb, R.drawable.dlb, R.drawable.bhb, R.drawable.hzcb, R.drawable.njcb, R.drawable.urmqccb, R.drawable.bojz, R.drawable.hsb, R.drawable.cqb, R.drawable.hebb, R.drawable.gyb, R.drawable.lzb, R.drawable.ncb,
            R.drawable.qdccb, R.drawable.jjccb, R.drawable.qhb, R.drawable.tzcb, R.drawable.cscb, R.drawable.whshb, R.drawable.jsb, R.drawable.cdb, R.drawable.mtbank, R.drawable.srb, R.drawable.dyccb, R.drawable.czcb, R.drawable.orbank, R.drawable.csrcb, R.drawable.sdeb, R.drawable.jrcb, R.drawable.wfccb, R.drawable.shb, R.drawable.crcb, R.drawable.fjnxb, R.drawable.yzb, R.drawable.cdrcb, R.drawable.wjrcb, R.drawable.wrcb, R.drawable.bea, R.drawable.zjtlcb, R.drawable.bsb, R.drawable.shrcb, R.drawable.gengduo));

    private ArrayList<Banks> banksCreditCard = new ArrayList<>();
    private ArrayList<String> codeCreditCard = new ArrayList<>(Arrays.asList(
            "ICBC", "CEB", "GDB", "HXB",
            "CCB", "CMBC", "SPAB", "CIB", "CMB", "BOC"));

    private ArrayList<Banks> banksDebitCard = new ArrayList<>();
    private ArrayList<String> codeDebitCard = new ArrayList<>(Arrays.asList(
            "ICBC", "CCB", "COMM", "CMB", "CEB", "GDB", "SPAB", "BOC", "ABC",
            "CMBC", "CITIC", "CIB", "SPDB", "KQ"));

    private BanksUtils() {
    }

    /**
     */
    public static BanksUtils getBanksUtils() {
        if (instance == null) {
            instance = new BanksUtils();
        }
        return instance;
    }

    public void initBanks() {
        for (int i = 0; i < nameAll.size(); i++) {
            Banks b = new Banks(nameAll.get(i), codeAll.get(i), residAll.get(i));
            BANKS.put(codeAll.get(i), b);
            banksAll.add(b);
        }
        Logger.t("initBanks").e("BANKS.size()--->"+BANKS.size());
        Logger.t("initBanks").e("banksAll.size()--->"+banksAll.size());
        for (String s : codeCreditCard) {
            banksCreditCard.add(BANKS.get(s));
        }
        Logger.t("initBanks").e("banksCreditCard.size()--->"+banksCreditCard.size());
        for (String s : codeDebitCard) {
            banksDebitCard.add(BANKS.get(s));
        }
        Logger.t("initBanks").e("banksDebitCard.size()--->"+banksDebitCard.size());
    }

    public static String formatBankNum(String bankNum) {
        bankNum = bankNum.replace(" ", "");
        return bankNum;
    }

    public static class Banks {
        public String name = "";
        public String code = "";
        public int resid = -1;

        public Banks(String name, String code, int resid) {
            this.name = name;
            this.code = code;
            this.resid = resid;
        }

        public Banks() {
        }
    }

    public ArrayList<Banks> getBanksCreditCard() {
        return banksCreditCard;
    }

    public ArrayList<Banks> getBanksDebitCard() {
        return banksDebitCard;
    }

    public int getBankIconResId(String gateid) {
        return BANKS.get(gateid).resid;
    }

}
