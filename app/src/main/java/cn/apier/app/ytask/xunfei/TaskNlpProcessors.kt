package cn.apier.app.ytask.xunfei

import android.util.Log
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.TaskApi
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.AlarmHelper
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.common.Utils
import cn.apier.app.ytask.synthesization.SynthesizerHelper
import cn.apier.app.ytask.ui.task.TaskListActivity
import com.alibaba.fastjson.JSON
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AddTaskSemanticProcessor : NlpSemanticProcessor {


    companion object {
        private val SLOT_TASK_CONTENT = "taskContent"
        private val SLOT_TASK_TIMER = "taskTimer"
        private val TAG = AddTaskSemanticProcessor::class.java.simpleName
    }

    override fun intent(): String = "add_task"

    override fun process(semantic: Semantic) {
        var content: String = ""
        var timer: String? = null
        semantic.slots.forEach {
            when (it.name) {
                SLOT_TASK_CONTENT -> {
                    content = it.normValue
                }
                SLOT_TASK_TIMER -> {
                    // normValue={"datetime":"2018-05-31T10:00:00","suggestDatetime":"2018-05-31T10:00:00"}

                    timer = JSON.parseObject(it.normValue).getString("suggestDatetime").replace("T", " ")
                }
            }
        }

        YTaskApplication.currentApplication.showMessage("AddTask[content:$content,timer:$timer]")
        Log.i(TAG, "task content:$content; timer:$timer")


        var normalTimer = timer
        timer?.let {
            normalTimer = Utils.normalizeDateTimeStr(it)
        }

        ApiFactory.apiProxy(TaskApi::class.java).newTask(content, normalTimer).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            if (it.success) {
                val txt = YTaskApplication.currentApplication.resources.getString(R.string.txt_task_add_success)
                AlarmHelper.alarm(normalTimer, content)
                SynthesizerHelper.speak(txt)

                YTaskApplication.currentApplication.showMessage(txt)
                YTaskApplication.currentApplication.refresh()

                YTaskApplication.currentApplication.startActivity(TaskListActivity::class.java)


            } else {
                Log.e(Constants.TAG_LOG, "添加任务失败 ${it.status?.description}")
                val txt = YTaskApplication.currentApplication.resources.getString(R.string.txt_task_add_fail)

                SynthesizerHelper.speak(txt)
            }

        }, {
            Log.e(Constants.TAG_LOG, "添加任务失败${it.message}")
            val txt = YTaskApplication.currentApplication.resources.getString(R.string.txt_task_add_fail)


            SynthesizerHelper.speak(txt)
        }, {})


    }

}


class ShowTaskSemanticProcessor : NlpSemanticProcessor {
    override fun process(semantic: Semantic) {
        YTaskApplication.currentApplication.startActivity(TaskListActivity::class.java)
    }

    override fun intent(): String = "show_task"

}
