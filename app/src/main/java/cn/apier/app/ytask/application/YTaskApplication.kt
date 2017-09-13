package cn.apier.app.ytask.application

import android.app.Application
import android.util.Log
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.UserApi
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.common.Utils
import cn.apier.app.ytask.interceptor.RequestInterceptor
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

    var bdToken:String?=null
    var token: String? = null
    var signedIn: Boolean = false
        private set
    var signedUserId: String = ""
        private set

    override fun onCreate() {
        super.onCreate()
        currentApplication = this
    }

    fun retrofit(): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor(RequestInterceptor()).build()


        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
    }

    private fun sign(timestampInMs: String): String = Utils.md5(Constants.APP_KEY + Constants.APP_SECRET + timestampInMs) ?: ""


    fun getToken(successFun: () -> Unit={}) {
        val timestampInMs = "${System.currentTimeMillis()}"
        this.retrofit().create(UserApi::class.java).getToken(Constants.APP_KEY, timestampInMs, sign(timestampInMs)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
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


}