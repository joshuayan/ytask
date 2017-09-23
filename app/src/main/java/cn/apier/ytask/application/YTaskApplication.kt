package cn.apier.ytask.application

import android.app.Application
import android.os.Handler
import android.os.Message
import android.util.Log
import cn.apier.ytask.R
import cn.apier.ytask.api.UserApi
import cn.apier.ytask.common.Constants
import cn.apier.ytask.common.Utils
import cn.apier.ytask.interceptor.RequestInterceptor
import cn.apier.ytask.recognization.CommonRecogParams
import cn.apier.ytask.recognization.MessageStatusRecogListener
import cn.apier.ytask.recognization.MyRecognizer
import cn.apier.ytask.synthesization.SynthesizerHelper
import cn.apier.ytask.wakeup.MyWakeup
import cn.apier.ytask.wakeup.SimpleWakeupListener
import cn.apier.ytask.wakeup.WakeUpHelper
import cn.apier.ytask.wakeup.WakeupParams
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


    companion object {
        lateinit var currentApplication: YTaskApplication
            private set
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

    override fun onCreate() {
        super.onCreate()
        currentApplication = this
        debug = this.resources.getBoolean(R.bool.debug)
        retrofit = buildRetrofit()

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


    fun getToken(successFun: () -> Unit = {}) {
        val timestampInMs = "${System.currentTimeMillis()}"
        this.apiProxy(UserApi::class.java).getToken(Constants.appKey(this.debug), timestampInMs, sign(timestampInMs)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {

                    Log.d("ytask", it.toString())
                    if (it.success) {
                        val tokenDto = it.data
                        this.token = tokenDto?.code ?: ""
                        Log.d("ytask", "token:${token}")

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
}