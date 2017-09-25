package cn.apier.app.ytask.recognization

import android.util.Log

import com.baidu.speech.EventListener
import com.baidu.speech.asr.SpeechConstant

import org.json.JSONException
import org.json.JSONObject

import cn.apier.app.ytask.util.Logger
import cn.apier.app.ytask.wakeup.ErrorTranslation

/**
 * Created by fujiayi on 2017/6/14.
 */

class RecogEventAdapter(private val listener: IRecogListener) : EventListener {

    protected var currentJson: String? = null

    override fun onEvent(name: String, params: String?, data: ByteArray?, offset: Int, length: Int) {
        currentJson = params
        val logMessage = "name:$name; params:$params"

        // logcat 中 搜索RecogEventAdapter，即可以看见下面一行的日志
        Log.i(TAG, logMessage)
        if (false) { //此次可以调试，不需要后续逻辑
            return
        }
        if (name == SpeechConstant.CALLBACK_EVENT_ASR_LOADED) {
            listener.onOfflineLoaded()
        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED) {
            listener.onOfflineUnLoaded()
        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_READY) {
            // 引擎准备就绪，可以开始说话
            listener.onAsrReady()

        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_BEGIN) {
            // 检测到用户的已经开始说话
            listener.onAsrBegin()

        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_END) {
            // 检测到用户的已经停止说话
            listener.onAsrEnd()

        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL) {
            val recogResult = RecogResult.parseJson(params!!)
            // 临时识别结果, 长语音模式需要从此消息中取出结果
            val results = recogResult.resultsRecognition
            if (recogResult.isFinalResult) {
                listener.onAsrFinalResult(results, recogResult)
            } else if (recogResult.isPartialResult) {
                listener.onAsrPartialResult(results, recogResult)
            } else if (recogResult.isNluResult) {
                listener.onAsrOnlineNluResult(String(data!!, offset, length))
            }

        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_FINISH) {
            // 识别结束， 最终识别结果或可能的错误
            val recogResult = RecogResult.parseJson(params!!)
            if (recogResult.hasError()) {
                val errorCode = recogResult.error
                Logger.error(TAG, "asr error:" + params)
                listener.onAsrFinishError(errorCode, ErrorTranslation.recogError(errorCode), recogResult.desc ?: "")
            } else {
                listener.onAsrFinish(recogResult)
            }

        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH) { //长语音
            listener.onAsrLongFinish()// 长语音
        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_EXIT) {
            listener.onAsrExit()
        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_VOLUME) {
            // Logger.info(TAG, "asr volume event:" + params);
            val vol = parseVolumeJson(params!!)
            listener.onAsrVolume(vol.volumePercent, vol.volume)
        } else if (name == SpeechConstant.CALLBACK_EVENT_ASR_AUDIO) {
            if (data!!.size != length) {
                Logger.error(TAG, "internal error: asr.audio callback data length is not equal to length param")
            }
            listener.onAsrAudio(data, offset, length)
        }
    }


    private fun parseVolumeJson(jsonStr: String): Volume {
        val vol = Volume()
        vol.origalJson = jsonStr
        try {
            val json = JSONObject(jsonStr)
            vol.volumePercent = json.getInt("volume-percent")
            vol.volume = json.getInt("volume")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return vol
    }

    private inner class Volume {
        var volumePercent = -1
        var volume = -1
        var origalJson: String? = null
    }

    companion object {

        private val TAG = "RecogEventAdapter"
    }

}
