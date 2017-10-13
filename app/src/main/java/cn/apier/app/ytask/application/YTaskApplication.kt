package cn.apier.app.ytask.application

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.UserApi
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.common.Utils
import cn.apier.app.ytask.interceptor.RequestInterceptor
import cn.apier.app.ytask.recognization.BDRecognizerHelper
import cn.apier.app.ytask.recognization.CommonRecogParams
import cn.apier.app.ytask.recognization.MessageStatusRecogListener
import cn.apier.app.ytask.recognization.MyRecognizer
import cn.apier.app.ytask.scene.SceneActionDispatcher
import cn.apier.app.ytask.scene.processor.AddTaskProcessor
import cn.apier.app.ytask.ui.base.BaseActivity
import cn.apier.app.ytask.wakeup.WakeUpHelper
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.model.AccessToken
import com.iflytek.cloud.SpeechUtility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by yanjunhua on 2017/9/5.
 */
class YTaskApplication : Application() {

    var currentActivity: BaseActivity? = null

    companion object {
        lateinit var currentApplication: YTaskApplication
            private set
        private val TAG = YTaskApplication.javaClass.simpleName
    }


    private var debug: Boolean = false

    var bdToken: String? = null
    var token: String? = null
    var signedIn: Boolean = false
        private set
    var signedUserId: String = ""
        private set


    init {
        SceneActionDispatcher.addProcessor(AddTaskProcessor())
    }

    override fun onCreate() {
        super.onCreate()
        currentApplication = this
        debug = this.resources.getBoolean(R.bool.debug)
        ApiFactory.debug = debug

        initToken { initBDToken() }

        initAIUI()
        //init synthesizer
    }


    private fun initAIUI() {
        SpeechUtility.createUtility(this, "appid=" + Constants.AIUI_APP_ID);

    }

    private fun sign(timestampInMs: String): String = Utils.md5(Constants.appKey(this.debug) + Constants.secretKey(this.debug) + timestampInMs) ?: ""


    private fun initToken(successFun: () -> Unit = {}) {
        val timestampInMs = "${System.currentTimeMillis()}"
        ApiFactory.apiProxy(UserApi::class.java).getToken(Constants.appKey(this.debug), timestampInMs, sign(timestampInMs)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {

                    Log.d(TAG, it.toString())
                    if (it.success) {
                        val tokenDto = it.data
                        this.token = tokenDto?.code ?: ""
                        Log.d(TAG, "token:${token}")

                        successFun()

                    } else {
                        toast(this.getString(R.string.err_get_token_failed))
                    }
                },
                {
                    toast("error:$it")
                })
    }

    override fun startActivity(intent: Intent) {
//        super.startActivity(intent)

        this.currentActivity?.finish()
        super.startActivity(intent)
    }

    fun signedIn(userId: String) {
        this.signedIn = true
        this.signedUserId = userId
    }

    fun tokenExist(): Boolean = this.token != null


    override fun onTerminate() {
        WakeUpHelper.release()
        super.onTerminate()
    }

    private fun processMsg(msg: Message) {

        msg.obj?.let { toast("message:$it") }
    }

    private fun initBDToken() {

        APIService.getInstance().init(applicationContext)
        ApiFactory.apiProxy(UserApi::class.java).queryBDApplicationInfo().subscribeOn(Schedulers.io()).subscribe {

            APIService.getInstance().initAccessToken(object : OnResultListener<AccessToken> {
                override fun onResult(result: AccessToken) {
                    val accessToken = result.accessToken
                    Log.i(TAG, "BD AccessToken->" + result.accessToken)
                    this@YTaskApplication.bdToken = accessToken
                }

                override fun onError(error: UnitError) {
                    Log.i(TAG, "BD AccessToken->" + error.errorMessage)
                }
            }, it.data?.appKey, it.data?.secretKey)
        }
    }

    fun startWakeUp() {
        WakeUpHelper.startWakeUp({ BDRecognizerHelper.start() })
    }
}