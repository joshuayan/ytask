package cn.apier.app.ytask.wakeup

import cn.apier.app.ytask.application.YTaskApplication

/**
 * Created by yanjunhua on 2017/9/23.
 */
object WakeUpHelper {

    private var listener = SimpleWakeupListener()
    private val myWakeUp: MyWakeup = MyWakeup(YTaskApplication.currentApplication, listener)

    fun startWakeUp(onSuccess: () -> Unit = {}) {
        val wakeupParams = WakeupParams()
        val params = wakeupParams.fetch()
        listener.onWakeUpSuccess = onSuccess
        myWakeUp.start(params)
    }

    fun stop() {
        this.myWakeUp.stop()
    }

    fun release() {
        this.myWakeUp.release()
    }
}