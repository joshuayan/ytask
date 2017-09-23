package cn.apier.ytask.wakeup

import android.content.Context
import android.provider.SyncStateContract
import android.util.Log
import cn.apier.ytask.application.YTaskApplication
import cn.apier.ytask.common.Constants
import cn.apier.ytask.recognization.RecognizerHelper
import cn.apier.ytask.synthesization.SynthesizerHelper
import io.reactivex.Observable
import org.jetbrains.anko.toast
import java.util.*


/**
 * Created by fujiayi on 2017/6/21.
 */

class SimpleWakeupListener : IWakeupListener {
    override fun onSuccess(word: String, result: WakeUpResult) {
        Log.i(Constants.TAG_LOG, "唤醒成功，唤醒词" + word)
        YTaskApplication.currentApplication.toast("唤醒成功，唤醒词:$word,original:${result.origalJson}")

//        SynthesizerHelper.speak("唤醒成功,请说指令")

        RecognizerHelper.start()

    }

    override fun onStop() {
        Log.i(Constants.TAG_LOG, "唤醒词识别结束：")
        YTaskApplication.currentApplication.toast("唤醒词识别结束")

    }

    override fun onError(errorCode: Int, errorMessge: String?, result: WakeUpResult) {
        Log.i(Constants.TAG_LOG, "唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.origalJson)
        YTaskApplication.currentApplication.toast("唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.origalJson)
        SynthesizerHelper.speak("唤醒错误")


    }

    override fun onASrAudio(data: ByteArray?, offset: Int, length: Int) {
        Log.e(Constants.TAG_LOG, "audio data： " + data?.size)
        YTaskApplication.currentApplication.toast("audio data：${data?.size}")

    }

}
