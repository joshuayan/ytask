package cn.apier.app.voice.nlp

/**
 * Created by yanjunhua on 2017/10/16.
 */
interface NlpListener {
    fun onSuccess(nlpResult: NLPResult)
    fun onFail(nlpResult: NLPResult)
    fun onError(msg: String, throwable: Throwable)
}