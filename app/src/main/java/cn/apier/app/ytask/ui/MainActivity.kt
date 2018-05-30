package cn.apier.app.ytask.ui

import android.os.Bundle
import cn.apier.app.ytask.R
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick


class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnRec.onClick {
            YTaskApplication.currentApplication.startRecognize()
        }
    }

    override fun daemonActivity(): Boolean = true
}
