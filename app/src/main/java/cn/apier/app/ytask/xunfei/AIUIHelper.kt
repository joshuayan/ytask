package cn.apier.app.ytask.xunfei

import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.synthesization.SynthesizerHelper
import com.alibaba.fastjson.JSON
import com.iflytek.aiui.AIUIAgent
import com.iflytek.aiui.AIUIConstant
import com.iflytek.aiui.AIUIListener
import com.iflytek.aiui.AIUIMessage
import org.json.JSONObject
import java.io.IOException


/**
 * Created by yanjunhua on 2017/9/28.
 */
object AIUIHelper {

    private val TAG = AIUIHelper::class.java.simpleName
    private val mAIUIAgent: AIUIAgent
    private var mAIUIState = AIUIConstant.STATE_IDLE
    private var onFinish: (answer: String) -> Unit = {}
    private var onError: (code: String, info: String) -> Unit = { code, info -> Log.e(TAG, "On Error [code:$code,info:$info]") }

    private val mAIUIListener = AIUIListener { event ->
        when (event.eventType) {
            AIUIConstant.EVENT_WAKEUP -> {
                Log.i(TAG, "on event: " + event.eventType)
                showTip("进入识别状态")
            }

            AIUIConstant.EVENT_RESULT -> {
                Log.i(TAG, "on event: " + event.eventType)
                Log.i(TAG, "event info : ${event.info}")

                val eventInfo = JSON.parseObject(event.info, EventInfo::class.java)


//                    val bizParamJson = JSONObject(event.info)
//                    val data = bizParamJson.getJSONArray("data").getJSONObject(0)
//                    val params = data.getJSONObject("params")
//                    val content = data.getJSONArray("content").getJSONObject(0)

                val params = eventInfo.data?.get(0)?.params
                val content = eventInfo.data?.get(0)?.content?.get(0)
//                    if (content.has("cnt_id")) {
                content?.cnt_id?.let {


                    val cntJson = JSONObject(String(event.data.getByteArray(it)!!, Charsets.UTF_8))

                    if (params?.isNLP() == true) {
                        // 解析得到语义结果
                        val resultStr = cntJson.optString("intent")
                        Log.i(TAG, "aiui result: $resultStr")

                        val answer = JSONObject(resultStr).getJSONObject("answer").getString("text")
                        Log.i(TAG, "answer: $answer")

                        this.onFinish(answer)
//                            SynthesizerHelper.speak(answer)
                    }
                }

            }

            AIUIConstant.EVENT_ERROR -> {
                this.onError(event.arg1.toString(), event.info)
            }

            AIUIConstant.EVENT_VAD -> {
                if (AIUIConstant.VAD_BOS == event.arg1) {
                    showTip("找到vad_bos")
                } else if (AIUIConstant.VAD_EOS == event.arg1) {
                    showTip("找到vad_eos")
                } else {
                    showTip("" + event.arg2)
                }
            }

            AIUIConstant.EVENT_START_RECORD -> {
                Log.i(TAG, "on event: " + event.eventType)
                showTip("开始录音")
            }

            AIUIConstant.EVENT_STOP_RECORD -> {
                Log.i(TAG, "on event: " + event.eventType)
                showTip("停止录音")
            }

            AIUIConstant.EVENT_STATE -> {    // 状态事件
                mAIUIState = event.arg1

                if (AIUIConstant.STATE_IDLE == mAIUIState) {
                    // 闲置状态，AIUI未开启
                    showTip("STATE_IDLE")
                } else if (AIUIConstant.STATE_READY == mAIUIState) {
                    // AIUI已就绪，等待唤醒
                    showTip("STATE_READY")
                } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                    // AIUI工作中，可进行交互
                    showTip("STATE_WORKING")
                }
            }

            AIUIConstant.EVENT_CMD_RETURN -> {
                if (AIUIConstant.CMD_UPLOAD_LEXICON == event.arg1) {
                    showTip("上传" + if (0 == event.arg2) "成功" else "失败")
                }
            }

            else -> {
            }
        }
    }

    init {
        mAIUIAgent = AIUIAgent.createAgent(YTaskApplication.currentApplication, getAIUIParams(), mAIUIListener)
        val startMsg = AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null)
        mAIUIAgent.sendMessage(startMsg)
        val wakeupMsg = AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null)
        mAIUIAgent.sendMessage(wakeupMsg)
    }


    private fun showTip(str: String) {
    }

    private fun getAIUIParams(): String {
        var params = ""

        val assetManager = YTaskApplication.currentApplication.currentActivity?.resources?.assets!!
        try {
            val ins = assetManager.open("cfg/aiui_phone.cfg")
            val buffer = ByteArray(ins.available())

            ins.read(buffer)
            ins.close()

            params = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return params
    }

    fun understand(txt: String, onFinish: (answer: String) -> Unit = {}, onError: (code: String, info: String) -> Unit = { code, info -> }) {
        val params = "data_type=text"
        val textData = txt.toByteArray()
        val msg = AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData)
        mAIUIAgent.sendMessage(msg)
    }
}