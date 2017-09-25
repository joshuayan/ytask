package cn.apier.app.ytask.wakeup

import cn.apier.app.ytask.application.YTaskApplication

/**
 * Created by yanjunhua on 2017/9/23.
 */
object WakeUpHelper {

    private val myWakeUp: MyWakeup

    init {
        val listener = SimpleWakeupListener()
        this.myWakeUp = MyWakeup(YTaskApplication.currentApplication, listener)
    }

    fun startWakeUp() {
        val wakeupParams = WakeupParams()
        val params = wakeupParams.fetch()
        myWakeUp.start(params)
    }

    fun release() {
        this.myWakeUp.release()
    }

}