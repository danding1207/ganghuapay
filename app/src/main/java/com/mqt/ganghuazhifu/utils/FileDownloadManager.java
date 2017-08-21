package com.mqt.ganghuazhifu.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * Created by chiclaim on 2016/05/18
 */
public class FileDownloadManager {

    private static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private DownloadManager dm;
    private static FileDownloadManager instance;
    public MaterialDialog materialDialog;
    private DownloadChangeObserver downloadObserver;

    private FileDownloadManager() {
        //10.采用内容观察者模式实现进度
//        downloadObserver = new DownloadChangeObserver(context, null);
//        context.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
    }

    public static FileDownloadManager getInstance() {
        if (instance == null) {
            instance = new FileDownloadManager();
        }
        return instance;
    }

    /**
     * @param context
     * @param uri
     * @param title
     * @param description
     * @return download id
     */
    long startDownload(Context context, String uri, String title, String description) {
        if (FileUtils.checkExternalFilesDirs(context)) {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(uri));
//          req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            //req.setAllowedOverRoaming(false);
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //设置文件的保存的位置[三种方式]
            //第一种
            File file = new File(ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DOWNLOADS)[0], "ganghuazhifu.apk");
            Uri uri1 = Uri.fromFile(file);
            //file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
            //req.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "ganghuazhifu.apk");
            //第二种
            //file:///storage/emulated/0/Download/update.apk
            //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk");
            //第三种 自定义文件路径
            Logger.e(uri1.getPath());
            req.setDestinationUri(uri1);
            // 设置一些基本显示信息
            req.setTitle(title);
            req.setDescription(description);
//        req.setMimeType("application/vnd.android.package-archive");
            dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadObserver = new DownloadChangeObserver(context, null);
            context.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
            materialDialog = new MaterialDialog.Builder(context)
                    .title("版本升级")
                    .content("正在下载安装包，请稍候")
                    .progress(false, 100, false)
                    .cancelable(false)
                    .build();
            materialDialog.show();
            return dm.enqueue(req);
            //long downloadId = dm.enqueue(req);
            //Log.d("DownloadManager", downloadId + "");
            //dm.openDownloadedFile()
        }
        return -1;
    }


    /**
     * 获取文件保存的路径
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @return file path
     * @see FileDownloadManager#getDownloadUri(long)
     */
    private String getDownloadPath(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = dm.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                }
            } finally {
                c.close();
            }
        }
        return null;
    }


    /**
     * 获取保存文件的地址
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @see FileDownloadManager#getDownloadPath(long)
     */
    Uri getDownloadUri(long downloadId) {
        return dm.getUriForDownloadedFile(downloadId);
    }

    DownloadManager getDm() {
        return dm;
    }


    /**
     * 获取下载状态
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @return int
     * @see DownloadManager#STATUS_PENDING
     * @see DownloadManager#STATUS_PAUSED
     * @see DownloadManager#STATUS_RUNNING
     * @see DownloadManager#STATUS_SUCCESSFUL
     * @see DownloadManager#STATUS_FAILED
     */
    int getDownloadStatus(Context context, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        if (dm == null)
            dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = dm.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                }
            } finally {
                c.close();
            }
        }
        return -1;
    }

    //用于显示下载进度
    private class DownloadChangeObserver extends ContentObserver {

        private Context context;

        DownloadChangeObserver(Context context, Handler handler) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(EncryptedPreferencesUtils.getDownId());
            DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final Cursor cursor = dManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                final int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalSize = cursor.getInt(totalColumn);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                int progress = Math.round(percent * 100);
                materialDialog.setProgress(progress);
                if (progress == 100) {
                    materialDialog.dismiss();
                    context.getContentResolver().unregisterContentObserver(downloadObserver);
                }
            }
        }
    }

}
