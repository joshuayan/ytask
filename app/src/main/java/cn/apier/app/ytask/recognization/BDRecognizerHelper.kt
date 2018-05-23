package cn.apier.app.ytask.recognization

import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.recognization.all.AllRecogParams

/**
 * Created by yanjunhua on 2017/9/23.
 */
object BDRecognizerHelper {


    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            processMsg(msg)
        }
    }

    var result: String = ""
        private set
    private val listener = MessageStatusRecogListener(handler)
    private val myRecognizer: MyRecognizer = MyRecognizer(YTaskApplication.currentApplication, listener)


    fun start() {
        Log.d(Constants.TAG_LOG, "Recognizer start...")

        val sp = PreferenceManager.getDefaultSharedPreferences(YTaskApplication.currentApplication)
        val params = AllRecogParams(YTaskApplication.currentApplication).fetch(sp)
        val pidParams = mutableMapOf<String, Any>()

        pidParams.putAll(params)
        pidParams.put("pid", 15361)
        this.myRecognizer.start(pidParams)
    }

    private fun processMsg(msg: Message) {
        when (msg.what) {
            0 -> this.result = msg.obj as String
        }
    }

    /**
     * 开始录音后，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用。
     */
    fun stop() {
        myRecognizer.stop()
    }

    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    fun cancel() {
        myRecognizer.cancel()
    }

    fun release() {
        this.myRecognizer.release()
    }

}