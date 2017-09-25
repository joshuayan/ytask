package cn.apier.app.ytask.recognization.all

import android.content.Context
import android.content.SharedPreferences

import com.baidu.speech.asr.SpeechConstant

import java.util.Arrays

import cn.apier.app.ytask.recognization.CommonRecogParams
import cn.apier.app.ytask.recognization.PidBuilder

/**
 * Created by fujiayi on 2017/6/24.
 */

class AllRecogParams(context: Context) : CommonRecogParams(context) {

    init {
        stringParams.addAll(Arrays.asList(
                SpeechConstant.NLU,
                "_language",
                "_model"))

        intParams.addAll(Arrays.asList(
                SpeechConstant.DECODER,
                SpeechConstant.PROP))

        boolParams.addAll(Arrays.asList(SpeechConstant.DISABLE_PUNCTUATION, "_nlu_online"))

        // copyOfflineResource(context);
    }

    override fun fetch(sp: SharedPreferences): Map<String, Any> {

        var map: MutableMap<String, Any> = mutableMapOf<String,Any>()

        map.putAll(super.fetch(sp))

        val builder = PidBuilder()
       val  result = builder.addPidInfo(map)
        //boolean isOfflineEnabled = sp.getBoolean(SpeechConstant.DECODER, false);
        //map.put(SpeechConstant.DECODER, 0);


        /*if ("sp.getString(SpeechConstant.DECODER, "0")){
            // 离线需要额外设置离线资源文件

        }*/
        return result

    }

}
