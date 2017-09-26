package cn.apier.app.ytask.synthesization

import android.os.Handler
import android.os.Message
import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode

/**
 * Created by yanjunhua on 2017/9/23.
 */
object SynthesizerHelper {
    private val speechSynthesizer = SpeechSynthesizer.getInstance()
    private var myListener: MySpeechSynthesizerListener = MySpeechSynthesizerListener()
    private var speechFinishCallback: () -> Unit = {}

    init {
        initSpeech()
        Log.d(Constants.TAG_LOG, "SynthesizerHelper init.")
    }

    private fun initSpeech() {
        this.speechSynthesizer.setContext(YTaskApplication.currentApplication)
        this.speechSynthesizer.setSpeechSynthesizerListener(myListener)

        this.speechSynthesizer.setApiKey("ILo4xDbLmdmIE7peI60cec3n", "iAEX4xSreMCcTp4hUzLHl4fzDrOjpCua")
        this.speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
        this.speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT)
        val authInfo = this.speechSynthesizer.auth(TtsMode.MIX)

        if (authInfo.isSuccess()) {
            Log.d(Constants.TAG_LOG, "auth success")

            // 初始化tts
            speechSynthesizer.initTts(TtsMode.MIX)
        } else {
            val errorMsg = authInfo.getTtsError().getDetailMessage()
            Log.d(Constants.TAG_LOG, "auth failed errorMsg=$errorMsg")
        }

    }


    fun speak(txt: String, onFinish: () -> Unit = {}) {

        this.speechFinishCallback = onFinish
        val result = this.speechSynthesizer.speak(txt)

        Log.d(Constants.TAG_LOG, "speak result: $result")
        if (result < 0) {
            Log.e(Constants.TAG_LOG, "error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ")
        }

    }


    private val speechFinishHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            this@SynthesizerHelper.speechFinishCallback()
        }
    }

    class MySpeechSynthesizerListener : SpeechSynthesizerListener {

        override fun onSynthesizeStart(p0: String?) {
            Log.d(Constants.TAG_LOG, "onSynthesizeStart")
        }

        override fun onSpeechFinish(p0: String?) {
            Log.d(Constants.TAG_LOG, "onSpeechFinish,do callback, parameter: $p0")
            speechFinishHandler.sendEmptyMessage(0)
            Log.d(Constants.TAG_LOG, "speech finished")
        }

        override fun onSpeechProgressChanged(p0: String?, p1: Int) {
            Log.d(Constants.TAG_LOG, "onSpeechProgressChanged")
        }

        override fun onSynthesizeFinish(p0: String?) {
            Log.d(Constants.TAG_LOG, "onSynthesizeFinish")
        }

        override fun onSpeechStart(p0: String?) {
            Log.d(Constants.TAG_LOG, "onSpeechStart")
        }

        override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
            Log.d(Constants.TAG_LOG, "onSynthesizeDataArrived")
        }

        override fun onError(p0: String?, p1: SpeechError?) {
            Log.d(Constants.TAG_LOG, "onError：${p1?.code},${p1?.description}")
        }
    }
}