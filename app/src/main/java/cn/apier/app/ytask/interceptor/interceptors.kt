package cn.apier.app.ytask.interceptor

import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by yanjunhua on 2017/9/6.
 */

class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {

        var request = chain?.request()!!

        if (YTaskApplication.currentApplication.tokenExist()) {
            val urlBuilder = chain.request().url().newBuilder().addQueryParameter("token", YTaskApplication.currentApplication.token)
            request = request.newBuilder().url(urlBuilder.build()).build()
            Log.d("ytask","add token in request")
        }

        return chain.proceed(request)
    }
}