package cn.apier.app.ytask.api

import cn.apier.app.ytask.dto.BDApplicationInfo
import cn.apier.app.ytask.dto.TokenDto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by yanjunhua on 2017/9/5.
 */
interface UserApi {

    @GET("/auth/token")
    fun getToken(@Query("appKey") appKey: String, @Query("timestamp") timestamp: String, @Query("signature") signature: String): Observable<Result<TokenDto>>

    @POST("/auth/signIn")
    fun signIn(@Query("mobile") mobile: String, @Query("password") password: String): Observable<Result<String>>

    @GET("/auth/bdappinfo")
    fun queryBDApplicationInfo(): Observable<Result<BDApplicationInfo>>


}