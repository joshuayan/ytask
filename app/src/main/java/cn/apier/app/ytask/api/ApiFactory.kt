package cn.apier.app.ytask.api

import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.interceptor.RequestInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by yanjunhua on 2017/9/26.
 */
object ApiFactory {

    private val caches: MutableMap<String, Any> = mutableMapOf()

    private val retrofit: Retrofit

    var debug: Boolean = false


    init {
        retrofit = buildRetrofit()
    }

    fun <T : Any> apiProxy(clazz: Class<T>): T {
        val result: T

        if (caches.containsKey(clazz.simpleName)) {
            result = caches[clazz.simpleName] as T
        } else {
            result = this.retrofit.create(clazz)
            caches.put(clazz.simpleName, result)
        }

        return result

    }


    private fun buildRetrofit(): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor(RequestInterceptor()).build()


        return Retrofit.Builder().baseUrl(Constants.baseUrl(this.debug))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
    }
}