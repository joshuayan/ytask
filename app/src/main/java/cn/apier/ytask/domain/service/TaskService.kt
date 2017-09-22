package cn.apier.ytask.domain.service

import cn.apier.ytask.api.TaskApi
import cn.apier.ytask.application.YTaskApplication

/**
 * Created by yanjunhua on 2017/9/16.
 */
class TaskService {

    private val taskApi = YTaskApplication.currentApplication.apiProxy(TaskApi::class.java)

    fun addTask(task: String, deadLine: String?) {
        taskApi.newTask(task, deadLine)
    }

}