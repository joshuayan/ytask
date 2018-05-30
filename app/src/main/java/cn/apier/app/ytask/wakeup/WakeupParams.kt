package cn.apier.app.ytask.wakeup


import cn.apier.app.ytask.common.SpeechConstant
import cn.apier.app.ytask.recognization.PidBuilder
import java.util.*


/**
 * Created by fujiayi on 2017/6/24.
 */

class WakeupParams {

    private val backTrackInMs: Long = 1500L

    fun fetch(): Map<String, Any> {
        val params = HashMap<String, Any>()
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin")
//        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false)
//        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN)
//        val pid = PidBuilder.create().model(PidBuilder.INPUT).toPId() //如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
//        params.put(SpeechConstant.PID, 15361)
        params.put(SpeechConstant.APP_ID,"10181832")
//        params.put(SpeechConstant.AUDIO_MILLS, System.currentTimeMillis() - backTrackInMs);
        return params
    }
}
