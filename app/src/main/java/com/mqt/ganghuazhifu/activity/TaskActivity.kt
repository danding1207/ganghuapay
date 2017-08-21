package com.mqt.ganghuazhifu.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.mzule.activityrouter.router.Routers

class TaskActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!this.isTaskRoot) { //判断该Activity是不是任务空间的源Activity
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action.equals(Intent.ACTION_MAIN)) {
                finish()
                return
            }
        }
        Routers.open(this, "mqt://welcome")
        finish()
    }
}
