package cn.apier.ytask.recognization

import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import cn.apier.ytask.application.YTaskApplication
import cn.apier.ytask.common.Constants
import cn.apier.ytask.recognization.all.AllRecogParams
import org.jetbrains.anko.toast

/**
 * Created by yanjunhua on 2017/9/23.
 */
object RecognizerHelper {

    private val myRecognizer: MyRecognizer

    init {
        val handler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                processMsg(msg)
            }
        }

        val listener = MessageStatusRecogListener(handler)
        myRecognizer = MyRecognizer(YTaskApplication.currentApplication, listener)
    }

    fun start() {
        Log.d(Constants.TAG_LOG, "Recognizer start...")

        val sp = PreferenceManager.getDefaultSharedPreferences(YTaskApplication.currentApplication)
        val params = AllRecogParams(YTaskApplication.currentApplication).fetch(sp)
        this.myRecognizer.start(params)
    }

    private fun processMsg(msg: Message?) {
        msg?.let {
            YTaskApplication.currentApplication.toast("识别结果：${it.obj}")
            Log.i(Constants.TAG_LOG, "识别结果：${it.obj}")
        }
    }

    /**
     * 开始录音后，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用。
     */
    private fun stop() {
        myRecognizer.stop()
    }

    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    private fun cancel() {
        myRecognizer.cancel()
    }

    private fun release() {
        this.myRecognizer.release()
    }

}