package cn.apier.app.ytask.wakeup

import android.util.Log
import cn.apier.app.ytask.common.SpeechConstant

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by fujiayi on 2017/6/24.
 */
class WakeUpResult {
    var name: String? = null
    var origalJson: String? = null
    var word: String? = null
    var desc: String? = null
    var errorCode: Int = 0

    fun hasError(): Boolean {
        return errorCode != ERROR_NONE
    }

    companion object {

        private val ERROR_NONE = 0

        private val TAG = "WakeUpResult"

        fun parseJson(name: String, jsonStr: String): WakeUpResult {
            val result = WakeUpResult()
            result.origalJson = jsonStr
            try {
                val json = JSONObject(jsonStr)
                if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS.equals(name)) {
                    val error = json.optInt("errorCode")
                    result.errorCode = error
                    result.desc = json.optString("errorDesc")
                    if (!result.hasError()) {
                        result.word = json.optString("word")
                    }
                } else {
                    val error = json.optInt("error")
                    result.errorCode = error
                    result.desc = json.optString("desc")
                }

            } catch (e: JSONException) {
                Log.e(TAG, "Json parse error" + jsonStr)
                e.printStackTrace()
            }

            return result
        }
    }
}
