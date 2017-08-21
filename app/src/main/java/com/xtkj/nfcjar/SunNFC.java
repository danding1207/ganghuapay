//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xtkj.nfcjar;

import android.content.Context;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.xtkj.nfcjar.DataManager;
import com.xtkj.nfcjar.IUtil;
import com.xtkj.nfcjar.bean.CheckCardBean;
import com.xtkj.nfcjar.bean.ParamBean;
import com.xtkj.nfcjar.bean.PayResultBean;
import com.xtkj.nfcjar.bean.ReadResultBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SunNFC {
    private static String tag = "TAG";

    public SunNFC() {
    }

    public static ReadResultBean readMeter(IsoDep isoDep, String userNum) throws IOException {
        byte[] cmd = DataManager.getReadByte(userNum);
        Logger.e(tag, "读表信息=======发送======" + IUtil.byte2HexString(cmd));
        byte[] result = isoDep.transceive(cmd);
        if(result != null && result.length != 0) {
            int resultLength = result.length;
            if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                return new ReadResultBean(0);
            } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                return new ReadResultBean(0);
            } else {
                String readResult = IUtil.byte2HexString(result).replace(" ", "");
                Logger.e(tag, "读表信息=======接收======" + readResult);
                return readResult.substring(6, 10).equalsIgnoreCase("a9a2")?DataManager.parseReadResult(readResult):new ReadResultBean(0);
            }
        } else {
            return new ReadResultBean(0);
        }
    }

    public static String checkTime(IsoDep isoDep, String userNum, String time) throws IOException {
        byte[] cmd = DataManager.getCheckTimeByte(userNum, time);
        Logger.e(tag, "校时=======发送======" + IUtil.byte2HexString(cmd));
        byte[] result = isoDep.transceive(cmd);
        if(result != null && result.length != 0) {
            int resultLength = result.length;
            if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                return "";
            } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                return "";
            } else {
                String readResult = IUtil.byte2HexString(result).replace(" ", "");
                Logger.e(tag, "校时=======接收======" + readResult);
                if(readResult.substring(6, 8).equalsIgnoreCase("E9")) {
                    String res = readResult.substring(30, 42);
                    return res;
                } else {
                    return "";
                }
            }
        } else {
            return "";
        }
    }

    public static List<String> readFreezeDayUse(IsoDep isoDep, String userNum, int index) throws IOException {
        ArrayList list = new ArrayList();
        byte[] cmd = DataManager.getHistoryData(userNum, index);
        Logger.e(tag, "历史回抄=======发送======" + IUtil.byte2HexString(cmd));
        byte[] result = isoDep.transceive(cmd);
        if(result != null && result.length != 0) {
            int resultLength = result.length;
            if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                return list;
            } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                return list;
            } else {
                String readResult = IUtil.byte2HexString(result).replace(" ", "");
                Logger.e(tag, "历史回抄=======接收======" + readResult);
                if(readResult.substring(6, 8).equalsIgnoreCase("c0")) {
                    int len = Integer.parseInt(readResult.substring(8, 10), 16);
                    String d = readResult.substring(32, 10 + len * 2);

                    for(int i = 0; i < d.length(); i += 14) {
                        String us = IUtil.getPositiveNum(d.substring(i, i + 8));
                        String da = d.substring(i + 8, i + 14);
                        if(Integer.parseInt(da) != 0) {
                            list.add(da + us);
                        }
                    }
                }

                return list;
            }
        } else {
            return list;
        }
    }

    public static boolean setBottom(IsoDep isoDep, String userNum, int botNum) throws IOException {
        byte[] cmd = DataManager.getSetBottomData(userNum, botNum);
        String sendStr = IUtil.byte2HexString(cmd);
        Logger.e(tag, "设置底数=======发送======" + sendStr);
        byte[] result = isoDep.transceive(cmd);
        if(result != null && result.length != 0) {
            int resultLength = result.length;
            if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                return false;
            } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                return false;
            } else {
                String readResult = IUtil.byte2HexString(result).replace(" ", "");
                Logger.e(tag, "设置底数=======回复======" + readResult);
                return sendStr.substring(38, 46).equalsIgnoreCase(readResult.substring(30, 38));
            }
        } else {
            return false;
        }
    }

    public static boolean clear(IsoDep isoDep, String userNum, String valiDate, String desCmd) throws IOException {
        String random = "";
        if(desCmd.length() > 16) {
            random = desCmd.substring(desCmd.length() - 16);
            desCmd = desCmd.substring(0, 16);
            byte[] cmd = DataManager.getClearData(userNum, valiDate, desCmd, random);
            String sendStr = IUtil.byte2HexString(cmd);
            Logger.e(tag, "清表=======发送======" + sendStr);
            byte[] result = isoDep.transceive(cmd);
            if(result != null && result.length != 0) {
                int resultLength = result.length;
                if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                    return false;
                } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                    return false;
                } else {
                    String readResult = IUtil.byte2HexString(result).replace(" ", "");
                    Logger.e(tag, "清表=======回复======" + readResult);
                    return readResult != null && readResult.length() > 32?readResult.substring(30, 32).equals("55"):false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static int meetEmergency(IsoDep isoDep, String userNum, String warn2, String valiDate, String desCmd) throws IOException {
        String random = "";
        if(desCmd.length() > 16) {
            random = desCmd.substring(desCmd.length() - 16);
            desCmd = desCmd.substring(0, 16);
            byte[] cmd = DataManager.getEmergencyData(userNum, warn2, valiDate, desCmd, random);
            Logger.e(tag, "应急=======发送======" + IUtil.byte2HexString(cmd));
            byte[] result = isoDep.transceive(cmd);
            if(result != null && result.length != 0) {
                int resultLength = result.length;
                if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                    return -1;
                } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                    return -1;
                } else {
                    String readResult = IUtil.byte2HexString(result).replace(" ", "");
                    Logger.e(tag, "应急=======回复======" + readResult);
                    return readResult.substring(6, 8).equalsIgnoreCase("C5")?Integer.parseInt(readResult.substring(30, 32), 16):-1;
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static boolean securityCheck(IsoDep isoDep, String userNum, String securityNum, String valiDate, String desCmd) throws IOException {
        String random = "";
        if(desCmd.length() > 16) {
            random = desCmd.substring(desCmd.length() - 16);
            desCmd = desCmd.substring(0, 16);
            Logger.e(tag, "安检=======随机数======" + random);
            byte[] cmd = DataManager.getSecurityData(userNum, securityNum, valiDate, desCmd, random);
            Logger.e(tag, "安检=======发送======" + IUtil.byte2HexString(cmd));
            byte[] result = isoDep.transceive(cmd);
            if(result != null && result.length != 0) {
                int resultLength = result.length;
                if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                    return false;
                } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                    return false;
                } else {
                    String readResult = IUtil.byte2HexString(result).replace(" ", "");
                    Logger.e(tag, "安检=======回复======" + readResult);
                    return readResult != null && readResult.length() > 32?readResult.substring(30, 32).equals("55"):false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static CheckCardBean checkCard(IsoDep isoDep, String userNum, String valiDate, String desCmd) throws IOException {
        String random = "";
        if(desCmd.length() <= 16) {
            return new CheckCardBean(0);
        } else {
            random = desCmd.substring(desCmd.length() - 16);
            desCmd = desCmd.substring(0, 16);
            byte[] cmd = DataManager.getCheckData(userNum, 1, valiDate, desCmd, random);
            Logger.e(tag, "检查===1====发送======" + IUtil.byte2HexString(cmd));
            byte[] result = isoDep.transceive(cmd);
            if(result != null && result.length != 0) {
                int resultLength = result.length;
                if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                    return new CheckCardBean(0);
                } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                    return new CheckCardBean(0);
                } else {
                    String readResult1 = IUtil.byte2HexString(result).replace(" ", "");
                    Logger.e(tag, "检查====1===回复======" + readResult1);
                    if(readResult1.substring(6, 8).equalsIgnoreCase("C1")) {
                        cmd = DataManager.getCheckData(userNum, 2, valiDate, desCmd, random);
                        Logger.e(tag, "检查====2===发送======" + IUtil.byte2HexString(cmd));
                        result = isoDep.transceive(cmd);
                        if(result == null || result.length == 0) {
                            return new CheckCardBean(0);
                        }

                        resultLength = result.length;
                        if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                            return new CheckCardBean(0);
                        }

                        if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                            return new CheckCardBean(0);
                        }

                        String readResult2 = IUtil.byte2HexString(result).replace(" ", "");
                        if(readResult2.substring(6, 8).equalsIgnoreCase("C2")) {
                            Logger.e(tag, "检查卡===2====回复======" + readResult2);
                            String data1 = readResult1.substring(10, readResult1.length() - 8);
                            String data2 = readResult2.substring(10, readResult2.length() - 8);
                            CheckCardBean bean = DataManager.parseCheckRes(data1, data2);
                            return bean;
                        }
                    }

                    return new CheckCardBean(0);
                }
            } else {
                return new CheckCardBean(0);
            }
        }
    }

    public static boolean setParams(IsoDep isoDep, String userNum, ParamBean paramBean, String valiDate, String desCmd) throws IOException {
        String random = "";
        if(desCmd.length() <= 16) {
            return false;
        } else {
            random = desCmd.substring(desCmd.length() - 16);
            desCmd = desCmd.substring(0, 16);
            if(IUtil.isNullOrEmpty(valiDate) || valiDate.length() < 6) {
                valiDate = "000000";
            }

            byte[] cmd = DataManager.getParamData(userNum, paramBean, valiDate, desCmd, random);
            Logger.e(tag, "参数=======发送======" + IUtil.byte2HexString(cmd));
            byte[] result = isoDep.transceive(cmd);
            if(result != null && result.length != 0) {
                int resultLength = result.length;
                if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                    return false;
                } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                    return false;
                } else {
                    String readResult = IUtil.byte2HexString(result).replace(" ", "");
                    Logger.e(tag, "参数=======回复======" + readResult);
                    return readResult.substring(30, 32).equals("55");
                }
            } else {
                return false;
            }
        }
    }

    public static PayResultBean addMoney(Context context, IsoDep isoDep, String EncodeString, boolean isSet, ParamBean paramBean, String desCmd) throws IOException {
        Object cmd = null;
        byte[] cmd1;
        if(isSet) {
            byte set = -86;
            cmd1 = DataManager.getPayData(EncodeString, set, paramBean, desCmd);
        } else {
            byte set1 = 85;
            cmd1 = DataManager.getPayDataWithoutP(EncodeString, set1);
        }

        Logger.e(tag, "充值=======发送======" + IUtil.byte2HexString(cmd1));

        byte[] result = isoDep.transceive(cmd1);
        String addResult = IUtil.byte2HexString(result).replace(" ", "");
        Logger.e(tag, "充值=======返回======" + addResult);

        if(result != null && result.length != 0) {
            int resultLength = result.length;
            if(result[resultLength - 2] == 109 && result[resultLength - 1] == 0) {
                Logger.e(tag, "result[resultLength - 2] == 109 && result[resultLength - 1] == 0");
            	return new PayResultBean(2);
            } else if(result[resultLength - 2] == 109 && result[resultLength - 1] == 1) {
                Logger.e(tag, "result[resultLength - 2] == 109 && result[resultLength - 1] == 1");
            	return new PayResultBean(0);
            } else if(result[resultLength - 2] != 144 && result[resultLength - 1] != 0) {
                Logger.e(tag, "result[resultLength - 2] != 144 && result[resultLength - 1] != 0");
            	return new PayResultBean(0);
            } else {
//                String addResult = IUtil.byte2HexString(result).replace(" ", "");
                if(addResult.substring(6, 8).equals("89")) {
                	String flag = addResult.substring(60, 62);
                    Logger.e(tag, "充值=======返回======" + addResult);
                    return flag.equals("55")?(isSet?DataManager.parsePayResult(addResult):DataManager.parsePayResultShort(addResult)):new PayResultBean(0);
                } else {
                    Logger.e(tag, "！addResult.substring(6, 8).equals(\"89\")");
                    return new PayResultBean(0);
                }
            }
        } else {
            Logger.e(tag, "result == null || result.length == 0");
            return new PayResultBean(0);
        }
    }
}
