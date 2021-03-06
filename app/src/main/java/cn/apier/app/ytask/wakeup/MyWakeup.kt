package cn.apier.app.ytask.wakeup

import android.content.Context
import android.util.Log

import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

import org.json.JSONObject

/**
 * Created by fujiayi on 2017/6/20.
 */

class MyWakeup(context: Context, private val eventListener: EventListener) {

    private val wp: EventManager


    init {
        if (isInited) {
            Log.e(TAG, "还未调用release()，请勿新建一个新类")
            throw RuntimeException("还未调用release()，请勿新建一个新类")
        }
        isInited = true
        wp = EventManagerFactory.create(context, "wp")
        wp.registerListener(eventListener)
    }

    constructor(context: Context, eventListener: IWakeupListener) : this(context, WakeupEventAdapter(eventListener)) {}

    fun start(params: Map<String, Any>) {
        val json = JSONObject(params).toString()
        Log.i(TAG + ".Debug", "wakeup params(反馈请带上此行日志):" + json)
        Log.i(TAG + ".Debug", "sending command Wakeup.")
        wp.send(WAKEUP_START, json, null, 0, 0)
        Log.i(TAG + ".Debug", "Wakeup Sent")

    }


    fun stop() {
        Log.i(TAG, "唤醒结束")
        wp.send(WAKEUP_STOP, null, null, 0, 0)
    }

    fun release() {
        stop()
        wp.unregisterListener(eventListener)
        isInited = false
    }

    companion object {


        private var isInited = false

        private val TAG = "MyWakeup"
        private const val WAKEUP_START = "wp.start"
        private const val WAKEUP_STOP = "wp.stop"
    }
}
