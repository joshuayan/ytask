package cn.apier.app.ytask.domain.service

import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.TaskApi

/**
 * Created by yanjunhua on 2017/9/16.
 */
class TaskService {

    private val taskApi = ApiFactory.apiProxy(TaskApi::class.java)

    fun addTask(task: String, deadLine: String?) {
        taskApi.newTask(task, deadLine)
    }

}