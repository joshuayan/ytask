package cn.apier.app.ytask.recognization

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by fujiayi on 2017/6/24.
 */
class RecogResult {

    var origalJson: String? = null
    var resultsRecognition: Array<String> = arrayOf()
    var origalResult: String? = null
    var sn: String? = null // 日志id， 请求有问题请提问带上sn
    var desc: String? = null
    var resultType: String? = null
    var error = -1

    fun hasError(): Boolean {
        return error != ERROR_NONE
    }

    val isFinalResult: Boolean
        get() = "final_result" == resultType


    val isPartialResult: Boolean
        get() = "partial_result" == resultType

    val isNluResult: Boolean
        get() = "nlu_result" == resultType

    companion object {
        private val ERROR_NONE = 0

        fun parseJson(jsonStr: String): RecogResult {
            val result = RecogResult()
            result.origalJson = jsonStr
            try {
                val json = JSONObject(jsonStr)
                val error = json.optInt("error")
                result.error = error
                result.desc = json.optString("desc")
                result.resultType = json.optString("result_type")
                if (error == ERROR_NONE) {
                    result.origalResult = json.getString("origin_result")
                    val arr = json.optJSONArray("results_recognition")
                    if (arr != null) {
                        val size = arr.length()
                        val recogs = Array<String>(size, { "" })
                        if (size > 0) {
                            for (i in 0 until size) {
                                recogs[i] = arr.getString(i)
                            }
                        }
                        result.resultsRecognition = recogs
                    }


                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return result
        }
    }


}
