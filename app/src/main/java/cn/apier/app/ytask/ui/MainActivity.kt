package cn.apier.app.ytask.ui

import android.os.Bundle
import cn.apier.app.ytask.R
import cn.apier.app.ytask.ui.base.BaseActivity


class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun daemonActivity(): Boolean = true
}
