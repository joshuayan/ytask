package cn.apier.app.voice

/**
 * Created by yanjunhua on 2017/9/28.
 */
class NLPResult(val success: Boolean, val originalText: String) {
    var data: Any? = null
        private set
    var intent: String? = null
        private set

    companion object {
        fun ok(originalText: String, intent: String, data: Any): NLPResult {
            val result = NLPResult(true, originalText)
            result.data = data
            result.intent = intent
            return result
        }

        fun fail(originalText: String): NLPResult = NLPResult(false, originalText)
    }
}


data class NLPSlotItem(val type: String, val normalizedWord: String, val originalWord: String)