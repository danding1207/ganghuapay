package com.mqt.ganghuazhifu.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.hwangjr.rxbus.RxBus;
import com.mqt.ganghuazhifu.event.InstallFailureEvent;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;

import java.io.File;

public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long id = EncryptedPreferencesUtils.getDownId();
            if (downloadApkId == id) {
                installApk(context, downloadApkId);
            }
        }
    }

    private static void installApk(Context context, long downloadApkId) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        File apkFile = queryDownloadedApk(context, downloadApkId);
        if(apkFile!=null) {
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        } else {
            RxBus.get().post(new InstallFailureEvent());
        }
    }

    //通过downLoadId查询下载的apk，解决6.0以后安装的问题
    public static File queryDownloadedApk(Context context, long downloadApkId) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadApkId);
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = downloader.query(query);
        if (cur != null) {
            if (cur.moveToFirst()) {
                String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (!TextUtils.isEmpty(uriString)) {
                    targetApkFile = new File(Uri.parse(uriString).getPath());
                }
            }
            cur.close();
        }
        return targetApkFile;
    }

}
