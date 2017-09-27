package cn.apier.app.ytask.ui.task

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.TaskApi
import cn.apier.app.ytask.dto.TaskDto
import cn.apier.app.ytask.synthesization.SynthesizerHelper
import cn.apier.app.ytask.ui.base.BaseActivity
import com.baidu.aip.chatkit.utils.DateFormatter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list_task.*
import org.jetbrains.anko.textColor
import java.util.*

class TaskListActivity : BaseActivity() {
    override fun refresh() {
        this.taskViewAdapter.updateData()
    }

    private val taskApi: TaskApi = ApiFactory.apiProxy(TaskApi::class.java)
    private val taskViewAdapter = TaskViewAdapter()

    companion object {
        private val TAG = TaskListActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)
        rvTaskList.layoutManager = LinearLayoutManager(this)
        rvTaskList.adapter = taskViewAdapter
        this.taskViewAdapter.updateData()
    }


    private val updateDataHandler = Handler({

        when (it.what) {
            0 -> {
                this.taskApi.list(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    Log.d(TAG, "update data,result:$it")
                    if (it.success) {
                        taskViewAdapter.clear()
                        val tasks = it.data
                        Log.d(TAG, "tasks: ${tasks ?: "No tasks"}")
                        tasks?.let {
                            it.forEach { task ->
                                val taskDto = TaskDto(task.uid, task.content, task.deadLine)
                                taskViewAdapter.addTask(taskDto)
                            }
                        }
                        taskViewAdapter.notifyDataSetChanged()
                    }
                }
            }
            1 -> {
                taskApi.finish(it.obj as String).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    if (it.success) {
                        SynthesizerHelper.speak("完成任务")
                        this.taskViewAdapter.updateData()
                    }
                }
            }
            else -> {
                Log.w(TAG, "Unknown Message")
            }
        }


        true
    })

    inner class TaskViewAdapter() : RecyclerView.Adapter<TaskViewHolder>() {

        private var tasks: MutableList<TaskDto> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_task, parent, false)
            return TaskViewHolder(view)

        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {


            val deadLine: Date? = tasks[position].deadLine?.let { Date(it.toLong()) }

            holder.updateTask(tasks[position].uid, tasks[position].content, deadLine)
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


        fun updateData() {
            this@TaskListActivity.updateDataHandler.sendEmptyMessage(0)
        }
    }


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTask = itemView.findViewById<TextView>(R.id.tvTask)
        private val tvDeadline = itemView.findViewById<TextView>(R.id.tvDeadline)
        private val rbSelect = itemView.findViewById<RadioButton>(R.id.rbSelect)
        private var taskId: String = ""
        private var deadLine: Date? = null
        private val oldColor = this.tvTask.currentTextColor

        init {
            rbSelect.isChecked = false
            rbSelect.setOnClickListener {
                val finishMsg = Message.obtain()
                finishMsg.obj = taskId
                finishMsg.what = 1

                this@TaskListActivity.updateDataHandler.sendMessage(finishMsg)
            }
        }


        fun updateTask(uid: String, task: String, deadLine: Date?) {
            this.deadLine = deadLine
            this.rbSelect.isChecked = false
            this.tvTask.text = task
            this.taskId = uid
            this.tvDeadline.visibility = View.GONE
            deadLine?.let {
                this.tvDeadline.text = DateFormatter.format(it, "yyyy-MM-dd HH:mm:ss");this.tvDeadline.visibility = View.VISIBLE
                if (System.currentTimeMillis() > deadLine.time) {
                    this.tvTask.textColor = Color.RED
                } else {
                    this.tvTask.textColor = oldColor
                }
            }

        }
    }


}
