package cn.apier.app.ytask.application

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Message
import android.util.Log
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.UserApi
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.common.Utils
import cn.apier.app.ytask.interceptor.RequestInterceptor
import cn.apier.app.ytask.recognization.CommonRecogParams
import cn.apier.app.ytask.recognization.MessageStatusRecogListener
import cn.apier.app.ytask.recognization.MyRecognizer
import cn.apier.app.ytask.scene.SceneActionDispatcher
import cn.apier.app.ytask.scene.processor.AddTaskProcessor
import cn.apier.app.ytask.wakeup.WakeUpHelper
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.model.AccessToken
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

    var currentActivity: Activity? = null

    companion object {
        lateinit var currentApplication: YTaskApplication
            private set
        private val TAG = YTaskApplication.javaClass.simpleName
    }


    lateinit var myRecognizer: MyRecognizer
        private set


    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            processMsg(msg)
        }
    }
    private var apiParams: CommonRecogParams = CommonRecogParams(this)


    private var debug: Boolean = false

    private lateinit var retrofit: Retrofit
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
        retrofit = buildRetrofit()


        initToken { initBDToken() }


//        initWakeUp()
//        startWakeUp()

        //init synthesizer

        WakeUpHelper.startWakeUp()
    }


    fun <T : Any> apiProxy(clazz: Class<T>): T = this.retrofit.create(clazz)


    private fun buildRetrofit(): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor(RequestInterceptor()).build()


        return Retrofit.Builder().baseUrl(Constants.baseUrl(this.debug))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
    }

    private fun sign(timestampInMs: String): String = Utils.md5(Constants.appKey(this.debug) + Constants.secretKey(this.debug) + timestampInMs) ?: ""


    private fun initToken(successFun: () -> Unit = {}) {
        val timestampInMs = "${System.currentTimeMillis()}"
        this.apiProxy(UserApi::class.java).getToken(Constants.appKey(this.debug), timestampInMs, sign(timestampInMs)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
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


    fun signedIn(userId: String) {
        this.signedIn = true
        this.signedUserId = userId
    }

    fun tokenExist(): Boolean = this.token != null


    override fun onTerminate() {
        WakeUpHelper.release()
        super.onTerminate()
    }


    protected fun initRecog() {
        val listener = MessageStatusRecogListener(handler)
        myRecognizer = MyRecognizer(this, listener)
//        apiParams = getApiParams()
//        status = STATUS_NONE
//        if (enableOffline) {
//            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams())
//        }
    }

    private fun processMsg(msg: Message) {

        msg.obj?.let { toast("message:$it") }
    }


    private fun initBDToken() {

        APIService.getInstance().init(applicationContext)
        this.apiProxy(UserApi::class.java).queryBDApplicationInfo().subscribeOn(Schedulers.io()).subscribe {

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

}