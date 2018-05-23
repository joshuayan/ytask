package cn.apier.app.voice.asr

/**
 * Created by yanjunhua on 2017/10/16.
 */
interface AsrListener {
    fun onSuccess(txt: String)
    fun onError(msg: String, throwable: Throwable)
}