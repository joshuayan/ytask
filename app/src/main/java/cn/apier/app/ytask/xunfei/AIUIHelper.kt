package cn.apier.app.ytask.xunfei

import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import com.iflytek.aiui.AIUIAgent
import com.iflytek.aiui.AIUIConstant
import com.iflytek.aiui.AIUIListener
import com.iflytek.aiui.AIUIMessage
import java.io.IOException


/**
 * Created by yanjunhua on 2017/9/28.
 */
object AIUIHelper {

    private val TAG = AIUIHelper::class.java.simpleName
    private val mAIUIAgent: AIUIAgent
    var mAIUIState = AIUIConstant.STATE_IDLE
    private var onFinish: (answer: String) -> Unit = {}
    private var onError: (code: String, info: String) -> Unit = { code, info -> Log.e(TAG, "On Error [code:$code,info:$info]") }

    private val mAIUIListener = AIUIListener { event ->
        when (event.eventType) {
            AIUIConstant.EVENT_WAKEUP -> {
                Log.i(TAG, "on event: " + event.eventType)
                showTip("进入识别状态")
            }

            AIUIConstant.EVENT_RESULT -> {
                EventProcessor.processResultEvent(event)
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
        confirmWorking()


        //register event processor
        EventProcessor.registerNlpProcessor(AddTaskSemanticProcessor())
        EventProcessor.registerNlpProcessor(ShowTaskSemanticProcessor())
    }

    fun showTip(str: String) {
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
        confirmWorking()
        val params = "data_type=text"
        val textData = txt.toByteArray()
        val msg = AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData)
        mAIUIAgent.sendMessage(msg)
    }

    private fun confirmWorking() {
        if (AIUIConstant.STATE_WORKING != mAIUIState) {
            val wakeupMsg = AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null)
            mAIUIAgent.sendMessage(wakeupMsg)
        }
    }

    fun startRecord() {
        confirmWorking()
        val params = "sample_rate=16000,data_type=audio,pers_param={\"uid\":\"\"},tag=audio-tag"
        val startRecord = AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null)

        mAIUIAgent.sendMessage(startRecord)
    }

    fun stopRecord() {

        Log.i(TAG, "stop voice nlp")
        // 停止录音
        val params = "sample_rate=16000,data_type=audio"
        val stopRecord = AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, params, null)

        mAIUIAgent.sendMessage(stopRecord)
    }


//    private fun startRecordAudio() {
//        sendMessage(AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null))
//    }
//
//    private fun stopRecordAudio() {
//        sendMessage(AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null))
//    }

//
//    private fun beginAudio() {
//        mAudioStart = System.currentTimeMillis()
//        if (mAppendVoiceMsg != null) {
//            //更新上一条未完成的语音消息内容
//            updateMessage(mAppendVoiceMsg)
//            mAppendVoiceMsg = null
//            mInterResultStack.clear()
//        }
//        mAppendVoiceMsg = Message(USER, Voice, byteArrayOf())
//        mAppendVoiceMsg.cacheContent = ""
//        //语音消息msgData为录音时长
//        mAppendVoiceMsg.msgData = ByteBuffer.allocate(4).putFloat(0f).array()
//        addMessageToDB(mAppendVoiceMsg)
//    }
//
//    private fun endAudio() {
//        if (mAppendVoiceMsg != null) {
//            mAppendVoiceMsg.msgData = ByteBuffer.allocate(4).putFloat((System.currentTimeMillis() - mAudioStart) / 1000.0f).array()
//            updateMessage(mAppendVoiceMsg)
//        }
//    }


//    private fun sendMessage(message: AIUIMessage) {
//        if (mAIUIAgent != null) {
//            //确保AIUI处于唤醒状态
////            if (mCurrentState != AIUIConstant.STATE_WORKING && !mCurrentSettings.wakeup) {
////                mAIUIAgent.sendMessage(AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null))
////            }
//
//            mAIUIAgent.sendMessage(message)
//        }
//    }

    fun destroy() {
        if (this.mAIUIState == AIUIConstant.STATE_WORKING) {
            this.stopRecord()
        }
        this.mAIUIAgent.destroy()
    }
}