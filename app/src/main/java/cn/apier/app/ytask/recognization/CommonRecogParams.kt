package cn.apier.app.ytask.recognization

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment

import com.baidu.speech.asr.SpeechConstant

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import cn.apier.app.ytask.R
import cn.apier.app.ytask.util.FileUtil
import cn.apier.app.ytask.util.Logger

/**
 * Created by fujiayi on 2017/6/20.
 */

open class CommonRecogParams(context: Context) {

    protected lateinit var samplePath: String
    /**
     * 字符串格式的参数
     */
    protected var stringParams = ArrayList<String>()

    /**
     * int格式的参数
     */
    protected var intParams = ArrayList<String>()

    /**
     * bool格式的参数
     */
    protected var boolParams = ArrayList<String>()

    init {

        stringParams.addAll(Arrays.asList(
                SpeechConstant.VAD,
                SpeechConstant.IN_FILE
        ))
        intParams.addAll(Arrays.asList(
                SpeechConstant.VAD_ENDPOINT_TIMEOUT
        ))
        boolParams.addAll(Arrays.asList(
                SpeechConstant.ACCEPT_AUDIO_DATA,
                SpeechConstant.ACCEPT_AUDIO_VOLUME
        ))
        initSamplePath(context)
    }

    /**
     * 创建保存OUTFILE的临时目录. 仅用于OUTFILE参数。不使用demo中的OUTFILE参数可忽略此段
     * @param context
     */
    protected fun initSamplePath(context: Context) {
        val sampleDir = "baiduASR"
        samplePath = Environment.getExternalStorageDirectory().toString() + "/" + sampleDir
        if (!FileUtil.makeDir(samplePath)) {
            samplePath = context.getExternalFilesDir(sampleDir)!!.absolutePath
            if (!FileUtil.makeDir(samplePath)) {
                throw RuntimeException("创建临时目录失败 :" + samplePath)
            }
        }
    }

    open fun fetch(sp: SharedPreferences): Map<String, Any> {
        val map = HashMap<String, Any>()

        parseParamArr(sp, map)

        if (sp.getBoolean("_tips_sound", false)) { // 声音回调
            map.put(SpeechConstant.SOUND_START, R.raw.bdspeech_recognition_start)
            map.put(SpeechConstant.SOUND_END, R.raw.bdspeech_speech_end)
            map.put(SpeechConstant.SOUND_SUCCESS, R.raw.bdspeech_recognition_success)
            map.put(SpeechConstant.SOUND_ERROR, R.raw.bdspeech_recognition_error)
            map.put(SpeechConstant.SOUND_CANCEL, R.raw.bdspeech_recognition_cancel)
        }

        if (sp.getBoolean("_outfile", false)) { // 保存录音文件
            map.put(SpeechConstant.OUT_FILE, samplePath + "/outfile.pcm")
            Logger.info(TAG, "语音录音文件将保存在：$samplePath/outfile.pcm")
        }

        return map
    }

    /**
     * 根据 stringParams intParams boolParams中定义的参数名称，提取SharedPreferences相关字段
     * @param sp
     * @param map
     */
    private fun parseParamArr(sp: SharedPreferences, map: MutableMap<String, Any>) {
        for (name in stringParams) {
            if (sp.contains(name)) {
                val tmp = sp.getString(name, "")!!.replace(",.*".toRegex(), "").trim { it <= ' ' }
                if (null != tmp && "" != tmp) {
                    map.put(name, tmp)
                }
            }
        }
        for (name in intParams) {
            if (sp.contains(name)) {
                val tmp = sp.getString(name, "")!!.replace(",.*".toRegex(), "").trim { it <= ' ' }
                if (null != tmp && "" != tmp) {
                    map.put(name, Integer.parseInt(tmp))
                }
            }
        }
        for (name in boolParams) {
            if (sp.contains(name)) {
                map.put(name, sp.getBoolean(name, false))
            }
        }
    }

    companion object {

        private val TAG = "CommonRecogParams"
    }
}

