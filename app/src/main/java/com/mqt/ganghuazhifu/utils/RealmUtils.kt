package com.mqt.ganghuazhifu.utils

import android.content.Context

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by msc on 2016/9/27.
 */

class RealmUtils private constructor(context: Context) {
    private val realName = "Message.Realm"

    init {
        Realm.init(context)
        val realmConfig = RealmConfiguration.Builder().name(realName).build()
        Realm.setDefaultConfiguration(realmConfig)
    }

    /**
     * 获得Realm对象

     * @return
     */
    val realm: Realm
        get() = Realm.getDefaultInstance()

    companion object {

        private var mInstance: RealmUtils? = null

        fun getInstance(context: Context): RealmUtils? {
            if (mInstance == null) {
                synchronized(RealmUtils::class.java) {
                    if (mInstance == null) {
                        mInstance = RealmUtils(context)
                    }
                }
            }
            return mInstance
        }
    }

}
