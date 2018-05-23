package cn.apier.app.ytask.wakeup

import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.synthesization.SynthesizerHelper
import org.jetbrains.anko.toast


/**
 * Created by fujiayi on 2017/6/21.
 */

class SimpleWakeupListener : IWakeupListener {
    var onWakeUpSuccess: () -> Unit = { }

    override fun onSuccess(word: String, result: WakeUpResult) {
        Log.i(Constants.TAG_LOG, "唤醒成功，唤醒词" + word)
        YTaskApplication.currentApplication.toast("唤醒成功，唤醒词:$word,original:${result.origalJson}")

        SynthesizerHelper.speak("在的", {
            //            BDRecognizerHelper.start()
            this.onWakeUpSuccess()
        })

    }

    override fun onStop() {
        Log.i(Constants.TAG_LOG, "唤醒词识别结束：")
    }

    override fun onError(errorCode: Int, errorMessge: String?, result: WakeUpResult) {
        Log.i(Constants.TAG_LOG, "唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.origalJson)
        SynthesizerHelper.speak("唤醒错误")
    }

    override fun onASrAudio(data: ByteArray?, offset: Int, length: Int) {
        Log.e(Constants.TAG_LOG, "audio data： " + data?.size)
    }

}
