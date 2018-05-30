package cn.apier.app.ytask.scene.processor

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.TaskApi
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.scene.SceneActions
import cn.apier.app.ytask.synthesization.SynthesizerHelper
import cn.apier.app.ytask.ui.task.TaskAlarmActivity
import cn.apier.app.ytask.ui.task.TaskListActivity
import com.baidu.aip.unit.model.ResponseResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yanjunhua on 2017/9/26.
 */

class AddTaskProcessor : SceneActionProcessor {

    private val TAG = AddTaskProcessor::class.java.simpleName
    override fun canProcess(action: String): Boolean = (SceneActions.ACTION_ADD_TASK + SceneActions.POSTFIX_ACTION_RESPONSE) == action

    private var sessionId: String = ""
    override fun process(result: ResponseResult.Result) {
        sessionId = result.sessionId

        //  如果有对于的动作action，请执行相应的逻辑
        val actionList = result.actionList
        for (action in actionList) {
            Log.d(Constants.TAG_LOG, "actionId:${action.actionId}")

            if (canProcess(action.actionId)) {

                if (!TextUtils.isEmpty(action.say)) {
                    val sb = StringBuilder()
                    sb.append(action.say)
                }

                handleBusiness(result)
//
//                val listIntent = Intent(TaskActivityFilters.ACTION_TASK_LIST)
//                listIntent.addCategory(TaskActivityFilters.CATEGORY_TASK)
                showTaskList()

            } else {
                Log.i(this.javaClass.simpleName, "Can not process action [${action.actionId}]")
            }
        }
    }

    private fun handleBusiness(result: ResponseResult.Result) {
        if (result.schema != null) {
            val schema = result.schema!!
            val cmd = schema.getSlotsByType(Constants.SLOT_CMD)
            val items = schema.getSlotsByType(Constants.SLOT_TIME)
            val todos = schema.getSlotsByType(Constants.SLOT_TODO)

            val task = todos.map { it.normalizedWord }.joinToString()


            val msg = "command:${if (cmd.isNotEmpty()) cmd[0]?.normalizedWord else ""},time:${if (items.isNotEmpty()) items[0]?.normalizedWord else ""},task:${task}"
//            msgAdapter.addMessage(msg)
            Log.d(Constants.TAG_LOG, "message:$msg")


            var deadLine: String? = null
            if (items.isNotEmpty()) {

                val timeStr = items[0].normalizedWord
                with(timeStr) {
                    when {
                        matches("""\d{4}-\d{2}-\d{2}\|\d{2}:\d{2}:\d{2}""".toRegex()) -> {
                            deadLine = replace("|", " ")
                        }
                        matches("""\d{4}-\d{2}-\d{2}""".toRegex()) -> {
                            deadLine = "$timeStr 00:00:00"
                        }
                        matches("""\d{2}:\d{2}:\d{2}""".toRegex()) -> {
                            val now = Date(System.currentTimeMillis())
                            val dateStr = SimpleDateFormat("yyyy-MM-dd").format(now)
                            deadLine = "$dateStr $timeStr"
                        }
                        isEmpty() -> {
                            Log.d(Constants.TAG_LOG, "No Time info")
                        }
                        else -> {
                            Log.d(Constants.TAG_LOG, "Unknown")
                        }
                    }
                }
                Log.d(Constants.TAG_LOG, "deadLine:$deadLine")
                Log.d(Constants.TAG_LOG, "task:$task")
            }



            ApiFactory.apiProxy(TaskApi::class.java).newTask(task, deadLine).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.success) {
                    val txt = YTaskApplication.currentApplication.resources.getString(R.string.txt_task_add_success)


                    deadLine?.let {

                        val alarmManager = YTaskApplication.currentApplication.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        val alarmTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deadLine).time
                        val intent = Intent(YTaskApplication.currentApplication.currentActivity, TaskAlarmActivity::class.java)

                        intent.putExtra("task", task)
                        val pendingIntent = PendingIntent.getActivity(YTaskApplication.currentApplication.currentActivity, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                        Log.d(TAG, "Added Alarm.")

                    }



                    SynthesizerHelper.speak(txt)

                } else {
                    Log.e(Constants.TAG_LOG, "添加任务失败 ${it.status?.description}")
                    val txt = YTaskApplication.currentApplication.resources.getString(R.string.txt_task_add_fail)


                    SynthesizerHelper.speak(txt)
                }

            }, {
                Log.e(Constants.TAG_LOG, "添加任务失败${it.message}")
                val txt = YTaskApplication.currentApplication.resources.getString(R.string.txt_task_add_fail)


                SynthesizerHelper.speak(txt)
            }, {

            })

        }
    }


}

class ListTaskProcessor : SceneActionProcessor {
    override fun process(result: ResponseResult.Result) {
        showTaskList()
    }

    override fun canProcess(action: String): Boolean = action == SceneActions.ACTION_LIST_TASK + SceneActions.POSTFIX_ACTION_RESPONSE
}

private fun showTaskList() {
    val listIntent = Intent(YTaskApplication.currentApplication, TaskListActivity::class.java)
    YTaskApplication.currentApplication.startActivity(listIntent)
}