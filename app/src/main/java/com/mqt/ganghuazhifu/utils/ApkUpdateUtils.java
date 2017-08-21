package com.mqt.ganghuazhifu.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.mqt.ganghuazhifu.receiver.ApkInstallReceiver;
import com.orhanobut.logger.Logger;

import java.io.File;


/**
 * Created by chiclaim on 2016/05/18
 */
public class ApkUpdateUtils {
    public static final String TAG = ApkUpdateUtils.class.getSimpleName();

    private static final String KEY_DOWNLOAD_ID = "downloadId";

    /**
     * @param context
     * @param url
     * @param title
     * @param versionName
     * @return true:从网络下载新应用;false:应用已下载直接安装
     */
    public static boolean download(Context context, String url, String title, String versionName) {
        long downloadId = EncryptedPreferencesUtils.getDownId();
        Logger.i("downloadId: " + downloadId);
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance();
            int status = fdm.getDownloadStatus(context, downloadId);
            Logger.i("downloadStatus: " + status);
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                //启动更新界面
                Uri uri = fdm.getDownloadUri(downloadId);
                if (uri != null) {
                    if (compare(getApkInfo(context, uri.getPath()), context, versionName)) {
                        startInstall(context, downloadId);
                        return false;
                    } else {
                        fdm.getDm().remove(downloadId);
                    }
                }
                start(context, url, title);
            } else if (status == DownloadManager.STATUS_FAILED) {
                start(context, url, title);
            } else if (status == -1) {
                Logger.i(TAG, "apk is deleted! redownloading...");
                start(context, url, title);
            } else {
                Logger.i(TAG, "apk is already downloading");
            }
        } else {
            start(context, url, title);
        }
        return true;
    }

    private static void start(Context context, String url, String title) {
        long id = FileDownloadManager.getInstance().startDownload(context, url,
                title, "下载完成后点击打开");
        if (id != -1)
            EncryptedPreferencesUtils.setDownId(id);
        Logger.i(TAG, "apk start download:" + id);
    }

    public static void startInstall(Context context, long downloadId) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        File apkFile = ApkInstallReceiver.queryDownloadedApk(context, downloadId);
        install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }


    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    private static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            //String packageName = info.packageName;
            //String version = info.versionName;
            //Log.d(TAG, "packageName:" + packageName + ";version:" + version);
            //String appName = pm.getApplicationLabel(appInfo).toString();
            //Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
            return info;
        }
        return null;
    }


    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    private static boolean compare(PackageInfo apkInfo, Context context, String versionName) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                Logger.i("now_versionCode: " + packageInfo.versionCode);
                Logger.i("new_versionCode: " + apkInfo.versionCode);
                Logger.i("now_versionName: " + packageInfo.versionName);
                Logger.i("new_versionName: " + apkInfo.versionName);
                Logger.i("net_versionName: " + versionName);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    String[] ss = versionName.split("\\.");
                    String[] sss = apkInfo.versionName.split("\\.");
                    int a = Integer.parseInt(ss[0]);
                    int b = Integer.parseInt(ss[1]);
                    int c = Integer.parseInt(ss[2]);
                    int d = Integer.parseInt(ss[3]);
                    int aa = Integer.parseInt(sss[0]);
                    int bb = Integer.parseInt(sss[1]);
                    int cc = Integer.parseInt(sss[2]);
                    int dd = Integer.parseInt(sss[3]);
                    return (a == aa && b == bb && c == cc && d == dd);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}


