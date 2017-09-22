package cn.apier.ytask.wakeup

import android.util.Log


/**
 * Created by fujiayi on 2017/6/21.
 */

class SimpleWakeupListener : IWakeupListener {
    override fun onSuccess(word: String, result: WakeUpResult) {
        Log.i(TAG, "唤醒成功，唤醒词" + word)
    }

    override fun onStop() {
        Log.i(TAG, "唤醒词识别结束：")
    }

    override fun onError(errorCode: Int, errorMessge: String?, result: WakeUpResult) {
        Log.i(TAG, "唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.origalJson)
    }

    override fun onASrAudio(data: ByteArray?, offset: Int, length: Int) {
        Log.e(TAG, "audio data： " + data?.size)
    }

    companion object {

        private val TAG = "SimpleWakeupListener"
    }
}
