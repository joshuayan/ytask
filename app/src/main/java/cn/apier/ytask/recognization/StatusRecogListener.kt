package cn.apier.ytask.recognization

/**
 * Created by fujiayi on 2017/6/14.
 */

import android.util.Log

open class StatusRecogListener : IRecogListener, IStatus {

    /**
     * 识别的引擎当前的状态
     */
    protected var status = IStatus.STATUS_NONE

    override fun onAsrReady() {
        status = IStatus.STATUS_READY
    }

    override fun onAsrBegin() {
        status = IStatus.STATUS_SPEAKING
    }

    override fun onAsrEnd() {
        status = IStatus.STATUS_RECOGNITION
    }

    override fun onAsrPartialResult(results: Array<String>, recogResult: RecogResult) {


    }

    override fun onAsrFinalResult(results: Array<String>, recogResult: RecogResult) {
        status = IStatus.STATUS_FINISHED
    }

    override fun onAsrFinish(recogResult: RecogResult) {
        status = IStatus.STATUS_FINISHED
    }


    override fun onAsrFinishError(errorCode: Int, errorMessage: String, descMessage: String) {
        status = IStatus.STATUS_FINISHED
    }

    /**
     * 长语音识别结束
     */
    override fun onAsrLongFinish() {
        status = IStatus.STATUS_FINISHED
    }

    override fun onAsrVolume(volumePercent: Int, volume: Int) {
        Log.i(TAG, "音量百分比$volumePercent ; 音量$volume")
    }

    override fun onAsrAudio(data: ByteArray, offset: Int, length: Int) {
        var data = data
        if (offset != 0 || data.size != length) {
            val actualData = ByteArray(length)
            System.arraycopy(data, 0, actualData, 0, length)
            data = actualData
        }

        Log.i(TAG, "音频数据回调, length:" + data.size)
    }

    override fun onAsrExit() {
        status = IStatus.STATUS_NONE
    }

    override fun onAsrOnlineNluResult(nluResult: String) {
        status = IStatus.STATUS_FINISHED
    }

    override fun onOfflineLoaded() {

    }

    override fun onOfflineUnLoaded() {

    }

    companion object {

        private val TAG = "StatusRecogListener"
    }


}
