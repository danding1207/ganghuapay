package com.mqt.ganghuazhifu.utils;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by msc on 2016/9/27.
 */

public class RealmUtils {

    private static RealmUtils mInstance;
    private String realName = "Message.Realm";

    private RealmUtils(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().name(realName).build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    public static RealmUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RealmUtils.class) {
                if (mInstance == null) {
                    mInstance = new RealmUtils(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获得Realm对象
     *
     * @return
     */
    public Realm getRealm() {
        return Realm.getDefaultInstance();
    }

}
