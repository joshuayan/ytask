package cn.apier.app.ytask.api

import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by yanjunhua on 2017/9/16.
 */
interface TaskApi {

    @POST("/task/new")
    fun newTask(@Query("content") task: String, @Query("deadLine") deadline: String?): Observable<Result<Any>>
}