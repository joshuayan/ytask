package cn.apier.app.ytask.ui.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.apier.app.ytask.application.YTaskApplication

/**
 * Created by yanjunhua on 2017/9/27.
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        YTaskApplication.currentApplication.currentActivity = this
    }

    override fun onResume() {
        super.onResume()
        YTaskApplication.currentApplication.currentActivity = this
    }

    open fun refresh() {}


    open fun daemonActivity() = false


    override fun startActivity(intent: Intent) {
        YTaskApplication.currentApplication.startActivity(intent)
    }
}