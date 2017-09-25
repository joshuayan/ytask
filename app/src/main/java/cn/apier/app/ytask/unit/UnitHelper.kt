package cn.apier.app.ytask.unit

import android.text.TextUtils
import android.util.Log
import cn.apier.app.ytask.api.TaskApi
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import com.baidu.aip.chatkit.model.Message
import com.baidu.aip.chatkit.model.User
import com.baidu.aip.chatkit.utils.DateFormatter
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.model.CommunicateResponse
import java.util.*

/**
 * Created by yanjunhua on 2017/9/24.
 */
object UnitHelper {


    private var sessionId: String = ""
    private var mid: Long = 1L
    private val sender: User = User("1", "joshua", "Joshua Yan", true)
    private val taskApi: TaskApi = YTaskApplication.currentApplication.apiProxy(TaskApi::class.java)


    fun understand(txt: String) {

        APIService.getInstance().communicate(object : OnResultListener<CommunicateResponse> {
            override fun onResult(result: CommunicateResponse) {

                handleResponse(result)
            }

            override fun onError(error: UnitError) {

            }
        }, Constants.SCENE_ID, txt, System.currentTimeMillis().toString())
    }


    private fun handleResponse(response: CommunicateResponse?) {
        response?.let {

            Log.d(Constants.TAG_LOG, "unit response :$it")
            sessionId = it.result.sessionId

            //  如果有对于的动作action，请执行相应的逻辑
            val actionList = it.result.actionList
            for (action in actionList) {

                if (!TextUtils.isEmpty(action.say)) {
                    val sb = StringBuilder()
                    sb.append(action.say)

                    val message = Message(mid++.toString(), sender, sb.toString(), Date())
//                    messagesAdapter.addToStart(message, true)
//                    if (action.hintList.size > 0) {
//                        message.hintList = action.hintList
//                    }

                }

                Log.d(Constants.TAG_LOG, "actionId:${action.actionId}")
                when (action.actionId) {
                    "add_task_cmd_satisfy" -> {

                        handleBusiness(it)
                    }

                    "add_task_satisfy" -> {
                        handleBusiness(it)
                    }
                    else -> Log.e(Constants.TAG_LOG, "Unknown ActionId ${action.actionId}")
                }

//                // 执行自己的业务逻辑
//                if ("start_work_satisfy" == action.actionId) {
//                    Log.i("wtf", "开始扫地")
//                } else if ("stop_work_satisfy" == action.actionId) {
//                    Log.i("wtf", "停止工作")
//                } else if ("move_action_satisfy" == action.actionId) {
//                    Log.i("wtf", "移动")
//                } else if ("timed_charge_satisfy" == action.actionId) {
//                    Log.i("wtf", "定时充电")
//                } else if ("timed_task_satisfy" == action.actionId) {
//                    Log.i("wtf", "定时扫地")
//                } else if ("sing_song_satisfy" == action.actionId) {
//                    Log.i("wtf", "唱歌")
//                }
            }
        }
    }

    private fun handleBusiness(response: CommunicateResponse) {
        if (response.result.schema != null) {
            val schema = response.result.schema!!
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
                            val dateStr = DateFormatter.format(now, "yyyy-MM-dd")
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

        }
    }

}