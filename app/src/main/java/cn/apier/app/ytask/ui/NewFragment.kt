package cn.apier.app.ytask.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.TaskApi
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.dto.TaskDto
import com.baidu.aip.chatkit.message.MessageInput
import com.baidu.aip.chatkit.model.Message
import com.baidu.aip.chatkit.model.User
import com.baidu.aip.chatkit.utils.DateFormatter
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.listener.VoiceRecognizeCallback
import com.baidu.aip.unit.model.CommunicateResponse
import com.baidu.aip.unit.voice.VoiceRecognizer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.support.v4.toast
import java.util.*

class NewFragment : Fragment(), MessageInput.InputListener,
        MessageInput.VoiceInputListener {


    private lateinit var voiceRecognizer: VoiceRecognizer
    private var mid: Int = 0
    private val taskApi: TaskApi = YTaskApplication.currentApplication.apiProxy(TaskApi::class.java)


    private lateinit var messageInput: MessageInput
    private lateinit var sender: User

//    private var messageInput: MessageInput? = null

    private var sessionId: String = ""
    private lateinit var taskViewAdapter: TaskViewAdapter

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sender = User("0", "kf", "", true)


    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_new, container, false)

        val rvTask: RecyclerView = view.findViewById(R.id.rv_task)

        rvTask.layoutManager = LinearLayoutManager(view.context)
        taskViewAdapter = TaskViewAdapter()
        rvTask.adapter = taskViewAdapter

        messageInput = view.findViewById(R.id.input)


        voiceRecognizer = VoiceRecognizer()
        voiceRecognizer.init(this.activity, messageInput.getVoiceInputButton())
        voiceRecognizer.setVoiceRecognizerCallback(VoiceRecognizeCallback { text ->
            Log.d("ytask", "voice text:$text")
//            messageInput.inputEditText.setText(text)


            val txtMsg = if (text.startsWith(Constants.CMD_ADD_TASK)) text else Constants.CMD_ADD_TASK + text

            Log.d(Constants.TAG_LOG, "msg to send:$txtMsg")
//            msgAdapter.addMessage(txtMsg)

            val msg = Message(mid++.toString(), sender, txtMsg)

            sendMessage(msg)

        })
        messageInput.inputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onSubmit(v.editableText)
                v.text = ""
            }
            true
        })

        messageInput.setInputListener(this)
        messageInput.setAudioInputListener(this)

        updateData()
        return view
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)


//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        voiceRecognizer.onActivityResult(requestCode, resultCode, data)
    }


    private fun sendMessage(message: Message) {

        APIService.getInstance().communicate(object : OnResultListener<CommunicateResponse> {
            override fun onResult(result: CommunicateResponse) {

                handleResponse(result)
            }

            override fun onError(error: UnitError) {

            }
        }, Constants.SCENE_ID, message.text, System.currentTimeMillis().toString())

    }

    private fun handleResponse(response: CommunicateResponse?) {
        if (response != null) {
            sessionId = response.result.sessionId

            //  如果有对于的动作action，请执行相应的逻辑
            val actionList = response.result.actionList
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

                        handleBusiness(response)
                    }

                    "add_task_satisfy" -> {
                        handleBusiness(response)
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


            this.taskApi.newTask(task, deadLine).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.success) {
                    toast("添加任务成功")

                    updateData()

                } else {
                    Log.e(Constants.TAG_LOG, "添加任务失败 ${it.status?.description}")

                    toast("添加任务失败")
                }

            }, {
                Log.e(Constants.TAG_LOG, "添加任务失败${it.message}")
                toast("添加任务失败")
            }, {

            })
        }
    }


    private fun updateData() {
        this.taskApi.list(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.d(Constants.TAG_LOG, "update data,result:$it")
            if (it.success) {
                taskViewAdapter.clear()
                val tasks = it.data
                Log.d(Constants.TAG_LOG, "tasks: ${tasks ?: "No tasks"}")
                tasks?.let {
                    it.forEach { task ->
                        run {
                            val taskDto = TaskDto(task.uid, task.content, task.deadLine)
                            taskViewAdapter.addTask(taskDto)
                        }
                    }
                }
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }


    override fun onSubmit(input: CharSequence): Boolean {
        val message = Message(mid++.toString(), sender, input.toString())
        sendMessage(message)
        return true
    }

    override fun onVoiceInputClick() {
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.RECORD_AUDIO) != PackageManager
                .PERMISSION_GRANTED) {
            this.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 100)
            return
        }
        voiceRecognizer.onClick()
    }


    private class TaskViewAdapter : RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder>() {

        private var tasks: MutableList<TaskDto> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_task, parent, false)
            return TaskViewHolder(view)

        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

            var deadLineStr: String? = null


            tasks[position].deadLine?.let { deadLineStr = DateFormatter.format(Date(it.toLong()), "yyyy-MM-dd HH:mm:ss") }


            holder.updateTask(tasks[position].content, deadLineStr)


        }


        fun addTask(taskDto: TaskDto) {
            this.tasks.add(taskDto)
            notifyDataSetChanged()
        }


        fun clear() {
            this.tasks.clear()
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return tasks.size
        }

        inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val tvMsg: TextView = itemView.findViewById(R.id.tvMsg)

        }

        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvTask = itemView.findViewById<TextView>(R.id.tvTask)
            private val tvDeadline = itemView.findViewById<TextView>(R.id.tvDeadline)

            fun updateTask(task: String, deadLine: String?) {
                this.tvTask.text = task
                this.tvDeadline.visibility = View.GONE
                deadLine?.let { this.tvDeadline.text = it;this.tvDeadline.visibility = View.VISIBLE }
            }
        }
    }


}// Required empty public constructor



