/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.voicerecognition.android.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.baidu.android.voicedemo.control.MyRecognizer;
import com.baidu.android.voicedemo.recognization.ChainRecogListener;
import com.baidu.android.voicedemo.recognization.IRecogListener;
import com.baidu.android.voicedemo.recognization.RecogResult;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.speech.recognizerdemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度语音识别对话框，完成语音识别状态维护，API调用。实现不同样式的对话对话框，只需继承此类
 *
 * @author yangliang02
 */
public abstract class BaiduASRDialog extends Activity {

    /**
     * 提示语
     */
    public static final String PARAM_PORMPT_TEXT = "prompt_text";

    /**
     * 是否正在识别
     */
    private volatile boolean mIsRunning = false;



    /**
     * 提示语
     */
    protected String mPrompt;


    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    protected int status = STATUS_None;


    private Bundle mParams = new Bundle();
    private DigitalDialogInput input;
    private MyRecognizer myRecognizer;
    /**
     * 有2个listner，一个是用户自己的业务逻辑，如MessageStatusRecogListener。另一个是UI对话框的。
     * 使用这个ChainRecogListener把两个listener和并在一起
     */
    private ChainRecogListener listener;

    private static final String TAG = "BaiduASRDialog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConfig();
        initRecog();
        Log.i(TAG, "DigitalDialogInput obtained");
        ChainRecogListener listener = input.getListener();
        listener.addListener(new DialogListener());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mParams.putAll(extras);
        }

    }

    protected void initRecog() {
        listener = new ChainRecogListener();
        // DigitalDialogInput 输入 ，MessageStatusRecogListener可替换为用户自己业务逻辑的listener
        // listener.addListener(new MessageStatusRecogListener(null));
        myRecognizer = new MyRecognizer(this, listener); // DigitalDialogInput 输入
        // apiParams = getApiParams();
        // status = STATUS_NONE;
        Map<String, Object> params = new HashMap<String, Object>();  // params可以手动填入
        // params.put("vad.endpoint-timeout", 2230);
        params.put("vad.endpoint-timeout", 1230);
        params.put("accept-audio-data", false);
        params.put("disable-punctuation", false);
        params.put("accept-audio-volume", false);
        params.put(SpeechConstant.SOUND_START, R.raw.bdspeech_recognition_start);
        params.put(SpeechConstant.SOUND_END, R.raw.bdspeech_speech_end);
        params.put(SpeechConstant.SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        params.put(SpeechConstant.SOUND_ERROR, R.raw.bdspeech_recognition_error);
        params.put(SpeechConstant.SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        input = new DigitalDialogInput(myRecognizer, listener, params);
    }


    /**
     * 启动识别
     */
    protected void startRecognition() {
        mPrompt = mParams.getString(PARAM_PORMPT_TEXT);
        mIsRunning = true;
        onRecognitionStart();
        Map<String, Object> params = input.getStartParams();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, true);
        myRecognizer.start(input.getStartParams());
    }

    /**
     * 手动结束输入
     */
    protected void speakFinish() {
        myRecognizer.stop();
    }

    /**
     * 取消当前识别
     */
    protected void cancleRecognition() {
        myRecognizer.cancel();
        status = STATUS_None;
    }

    /**
     * 获取识别参数，可以再次设置相关参数
     *
     * @return
     */
    public Bundle getParams() {
        return mParams;
    }

    private void checkConfig() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(new ComponentName(getPackageName(), getClass().getName()), PackageManager.GET_META_DATA);
            boolean exported = info.exported;
            if (exported) {
                throw new AndroidRuntimeException(getClass().getName() + ", 'android:exported' should be false, please modify AndroidManifest.xml");
            }
            Log.d("export", "exported:" + exported);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动识别
     */
    protected abstract void onRecognitionStart();

    /**
     * 引擎准备就绪
     */
    protected abstract void onPrepared();

    /**
     * 检测到用户语音起点
     */
    protected abstract void onBeginningOfSpeech();

    /**
     * 音量变化回调
     *
     * @param volume 音量值，0-100。100为最大
     */
    protected abstract void onVolumeChanged(float volume);

    /**
     * 检测到用户说话结束
     */
    protected abstract void onEndOfSpeech();

    protected abstract void onFinish(int errorType, int errorCode);

    /**
     * 部分结果返回，
     *
     * @param results
     */
    protected abstract void onPartialResults(String[] results);


    @Override
    protected void onPause() {
        super.onPause();
        //myRecognizer.cancel(); // 注意：在android 4.3及以下系统中，调用destroy方法会将立即与service解绑，cancel和destroy一起调用时，cancel不起作用，故需分开！
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRecognizer.release(); // 注意：在android 4.3及以下系统中，调用destroy方法会将立即与service解绑，cancel和destroy一起调用时，cancel不起作用，故需分开！
    }


    protected class DialogListener implements IRecogListener {

        /**
         * ASR_START 输入事件调用后，引擎准备完毕
         */
        @Override
        public void onAsrReady() {
            status = STATUS_Ready;
            onPrepared();
        }

        /**
         * onAsrReady后检查到用户开始说话
         */
        @Override
        public void onAsrBegin() {
            status = STATUS_Speaking;
            onBeginningOfSpeech();
        }

        /**
         * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
         */
        @Override
        public void onAsrEnd() {
            status = STATUS_Recognition;
            BaiduASRDialog.this.onEndOfSpeech();
        }

        /**
         * onAsrBegin 后 随着用户的说话，返回的临时结果
         *
         * @param results     可能返回多个结果，请取第一个结果
         * @param recogResult 完整的结果
         */
        @Override
        public void onAsrPartialResult(String[] results, RecogResult recogResult) {
            onPartialResults(results);
        }

        /**
         * 最终的识别结果
         *
         * @param results     可能返回多个结果，请取第一个结果
         * @param recogResult 完整的结果
         */
        @Override
        public void onAsrFinalResult(String[] results, RecogResult recogResult) {
            status = STATUS_None;
            mIsRunning = false;

            //  BaiduASRDialog.this.onPartialResults(results);
            onFinish(0, 0);// TODO

            Intent intentResult = new Intent();
            ArrayList list = new ArrayList();
            list.addAll(Arrays.asList(results));
            intentResult.putStringArrayListExtra("results", list);
            setResult(RESULT_OK, intentResult);
        }

        @Override
        public void onAsrFinish(RecogResult recogResult) {
            mIsRunning = false;
            finish();
        }

        @Override
        public void onAsrFinishError(int errorCode, int subErrorCode, String errorMessage, String descMessage) {
            onFinish(errorCode, subErrorCode);
        }

        /**
         * 长语音识别结束
         */
        @Override
        public void onAsrLongFinish() {
            mIsRunning = false;
            finish();
        }

        @Override
        public void onAsrVolume(int volumePercent, int volume) {
            onVolumeChanged(volumePercent);
        }

        @Override
        public void onAsrAudio(byte[] data, int offset, int length) {

        }

        @Override
        public void onAsrExit() {

        }

        @Override
        public void onAsrOnlineNluResult(String nluResult) {

        }

        @Override
        public void onOfflineLoaded() {

        }

        @Override
        public void onOfflineUnLoaded() {

        }
    }


}
