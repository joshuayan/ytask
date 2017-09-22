package cn.apier.ytask.wakeup

import android.content.Context
import cn.apier.ytask.common.SpeechConstant


import java.util.HashMap

/**
 * Created by fujiayi on 2017/6/24.
 */

class WakeupParams(context: Context) {

    private val TAG = "WakeupParams"

    fun fetch(): Map<String, Any> {
        val params = HashMap<String, Any>()
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin")
        //   params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        //params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        return params
    }
}
