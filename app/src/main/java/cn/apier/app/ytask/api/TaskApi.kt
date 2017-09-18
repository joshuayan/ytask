package cn.apier.app.ytask.api

import cn.apier.app.ytask.dto.TaskDto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by yanjunhua on 2017/9/16.
 */
interface TaskApi {

    @POST("/task/new")
    fun newTask(@Query("content") task: String, @Query("deadLine") deadline: String?): Observable<Result<Any>>

    @GET("/task/list")
    fun list(@Query("finished") finished: Boolean): Observable<Result<List<TaskDto>>>
}