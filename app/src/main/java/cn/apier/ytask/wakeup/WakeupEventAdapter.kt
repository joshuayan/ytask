package cn.apier.ytask.wakeup

import android.util.Log
import cn.apier.ytask.common.SpeechConstant
import com.baidu.speech.EventListener

/**
 * Created by fujiayi on 2017/6/20.
 */

class WakeupEventAdapter(private val listener: IWakeupListener) : EventListener {

    override fun onEvent(name: String, params: String, data: ByteArray?, offset: Int, length: Int) {
        // android studio日志Monitor 中搜索 WakeupEventAdapter即可看见下面一行的日志
        Log.i(TAG, "wakeup name:$name; params:$params")
        if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS.equals(name)) { //识别唤醒词成功
            val result = WakeUpResult.parseJson(name, params)
            val errorCode = result.errorCode
            if (result.hasError()) { // error不为0依旧有可能是异常情况
                listener.onError(errorCode, ErrorTranslation.WakeupError(errorCode), result)
            } else {
                val word = result.word
                listener.onSuccess(word!!, result)

            }
        } else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR.equals(name)) { // 识别唤醒词报错
            val result = WakeUpResult.parseJson(name, params)
            val errorCode = result.errorCode
            if (result.hasError()) {
                listener.onError(errorCode, ErrorTranslation.WakeupError(errorCode), result)
            }
        } else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED.equals(name)) { //关闭唤醒词
            listener.onStop()
        } else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_AUDIO.equals(name)) { // 音频回调
            listener.onASrAudio(data, offset, length)
        }
    }

    companion object {

        private val TAG = "WakeupEventAdapter"
    }


}
