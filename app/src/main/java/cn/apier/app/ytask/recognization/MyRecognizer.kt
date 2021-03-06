package cn.apier.app.ytask.recognization

import android.content.Context
import cn.apier.app.ytask.util.Logger
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import org.json.JSONObject

/**
 * Created by fujiayi on 2017/6/13.
 * EventManager内的方法如send 都可以在主线程中进行，SDK中做过处理
 */

class MyRecognizer
/**
 * 初始化 提供 EventManagerFactory需要的Context和EventListener
 * @param context
 * @param eventListener
 */
(context: Context,
 /**
  * SDK 内部核心 事件回调类， 用于开发者写自己的识别回调逻辑
  */
 private val eventListener: EventListener) {
    /**
     * SDK 内部核心 EventManager 类
     */
    private var asr: EventManager? = null

    /**
     * 初始化
     * @param context
     * @param IRecogListener 将EventListener结果做解析的DEMO回调。使用RecogEventAdapter 适配EventListener
     */
    constructor(context: Context, IRecogListener: IRecogListener) : this(context, RecogEventAdapter(IRecogListener)) {}

    init {
        if (isInited) {
            Logger.error(TAG, "还未调用release()，请勿新建一个新类")
            throw RuntimeException("还未调用release()，请勿新建一个新类")
        }
        isInited = true
        asr = EventManagerFactory.create(context, "asr")
        asr!!.registerListener(eventListener)
    }


    /**
     *
     * @param params
     */
    fun loadOfflineEngine(params: Map<String, Any>) {
        val json = JSONObject(params).toString()
        Logger.info(TAG + ".Debug", "loadOfflineEngine params:" + json)
        asr!!.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json, null, 0, 0)
        isOfflineEngineLoaded = true
        // 没有ASR_KWS_LOAD_ENGINE这个回调表试失败，如缺少第一次联网时下载的正式授权文件。
    }

    fun start(params: Map<String, Any>) {
        val json = JSONObject(params).toString()
        Logger.info(TAG + ".Debug", "asr params(反馈请带上此行日志):" + json)
        asr!!.send(SpeechConstant.ASR_START, json, null, 0, 0)
    }

    /**
     * 提前结束录音等待识别结果。
     */
    fun stop() {
        Logger.info(TAG, "停止录音")
        asr!!.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0)
    }

    /**
     * 取消本次识别，取消后将立即停止不会返回识别结果。
     * cancel 与stop的区别是 cancel在stop的基础上，完全停止整个识别流程，
     */
    fun cancel() {
        Logger.info(TAG, "取消识别")
        asr!!.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0)
    }


    fun release() {
        cancel()
        if (isOfflineEngineLoaded) {
            asr!!.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0)
            isOfflineEngineLoaded = false
        }
        asr!!.unregisterListener(eventListener)
        asr = null
        isInited = false
    }

    companion object {

        private var isOfflineEngineLoaded = false

        private var isInited = false

        private val TAG = "MyRecognizer"
    }
}
