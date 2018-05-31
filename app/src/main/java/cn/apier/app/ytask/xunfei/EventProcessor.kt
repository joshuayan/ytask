package cn.apier.app.ytask.xunfei

import android.util.Log
import com.google.gson.Gson
import com.iflytek.aiui.AIUIConstant
import com.iflytek.aiui.AIUIEvent
import com.iflytek.aiui.AIUIListener
import org.json.JSONObject

object EventProcessor {

    private val TAG = EventProcessor::class.java.simpleName
    private val nlpSemanticProcessors: MutableMap<String, NlpSemanticProcessor> = mutableMapOf()

    fun registerNlpProcessor(processor: NlpSemanticProcessor) {
        if (this.nlpSemanticProcessors.containsKey(processor.intent())) {
            Log.w(TAG, "duplicated intent[${processor.intent()}], and old is REPLACED.")
        }
        this.nlpSemanticProcessors[processor.intent()] = processor
    }

    fun processResultEvent(event: AIUIEvent) {

        Log.i(TAG, "on event: " + event.eventType)
        Log.i(TAG, "event info : ${event.info}")

        val eventInfo = Gson().fromJson(event.info, EventInfo::class.java)

        val params = eventInfo.data.get(0).params
        val content = eventInfo.data.get(0).content.get(0)
        Log.i(TAG, "params:$params")
        content.cnt_id?.let {


            val contentStr = String(event.data.getByteArray(it))
            val cntJson = JSONObject(String(event.data.getByteArray(it)!!, Charsets.UTF_8))

            Log.i(TAG, "content:$cntJson")

            if (params.isNLP()) {
                // 解析得到语义结果
//                val resultStr = cntJson.optString("intent")

                val nlpResult = Gson().fromJson(contentStr, NlpResult::class.java)

                Log.i(TAG, "nlp result: $nlpResult")

                if (nlpResult.isSuccess()) {
                    Log.i(TAG, "text:${nlpResult.intent.text}")

                    nlpResult.intent?.semantic?.forEach {
                        this.nlpSemanticProcessors[it.intent]?.process(it)
                    }
                }
//
//                val answer = JSONObject(resultStr).getString("text")
//                Log.i(TAG, "answer: $answer")
//                this.onFinish(answer)
            }


        }
    }


}


class MyEventListener(var onFinish: (answer: String) -> Unit = {}, var onError: (code: String, info: String) -> Unit = { code, info -> Log.e(TAG, "On Error [code:$code,info:$info]") }) : AIUIListener {
    private var mAIUIState = AIUIConstant.STATE_IDLE

    companion object {
        private val TAG = MyEventListener::class.java.simpleName

    }

    override fun onEvent(event: AIUIEvent) {

        when (event.eventType) {
            AIUIConstant.EVENT_WAKEUP -> {
                Log.i(TAG, "on event: ${event.eventType};唤醒事件")
            }
            AIUIConstant.EVENT_RESULT -> {
                Log.i(TAG, "on event: " + event.eventType)
                Log.i(TAG, "event info : ${event.info}")

//                val eventInfo = JSON.parseObject(event.info, EventInfo::class.javaObjectType)

                val eventInfo = Gson().fromJson(event.info, EventInfo::class.java)


//                    val bizParamJson = JSONObject(event.info)
//                    val data = bizParamJson.getJSONArray("data").getJSONObject(0)
//                    val params = data.getJSONObject("params")
//                    val content = data.getJSONArray("content").getJSONObject(0)

                val params = eventInfo.data.get(0).params
                val content = eventInfo.data.get(0).content.get(0)
//                    if (content.has("cnt_id")) {
                Log.i(TAG, "params:$params")
                content.cnt_id?.let {


                    val contentStr = String(event.data.getByteArray(it))
                    val cntJson = JSONObject(String(event.data.getByteArray(it)!!, Charsets.UTF_8))

                    Log.i(TAG, "content:$cntJson")

                    when (params.sub) {
                        "nlp" -> {
                        }

                    }
                    if (params.isNLP()) {
                        // 解析得到语义结果
                        val resultStr = cntJson.optString("intent")


                        val nlpResult = Gson().fromJson(contentStr, NlpResult::class.java)

                        Log.i(TAG, "aiui result: $resultStr")

                        val answer = JSONObject(resultStr).getString("text")
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
                    AIUIHelper.showTip("找到vad_bos")
                } else if (AIUIConstant.VAD_EOS == event.arg1) {
                    AIUIHelper.showTip("找到vad_eos")
                } else {
                    AIUIHelper.showTip("" + event.arg2)
                }
            }

            AIUIConstant.EVENT_START_RECORD -> {
                Log.i(TAG, "on event: " + event.eventType)
                AIUIHelper.showTip("开始录音")
            }

            AIUIConstant.EVENT_STOP_RECORD -> {
                Log.i(TAG, "on event: " + event.eventType)
                AIUIHelper.showTip("停止录音")
            }

            AIUIConstant.EVENT_STATE -> {    // 状态事件
                mAIUIState = event.arg1

                if (AIUIConstant.STATE_IDLE == mAIUIState) {
                    // 闲置状态，AIUI未开启
                    AIUIHelper.showTip("STATE_IDLE")
                } else if (AIUIConstant.STATE_READY == mAIUIState) {
                    // AIUI已就绪，等待唤醒
                    AIUIHelper.showTip("STATE_READY")
                } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                    // AIUI工作中，可进行交互
                    AIUIHelper.showTip("STATE_WORKING")
                }
            }

            AIUIConstant.EVENT_CMD_RETURN -> {
                if (AIUIConstant.CMD_UPLOAD_LEXICON == event.arg1) {
                    AIUIHelper.showTip("上传" + if (0 == event.arg2) "成功" else "失败")
                }
            }

            else -> {
            }
        }


    }
}