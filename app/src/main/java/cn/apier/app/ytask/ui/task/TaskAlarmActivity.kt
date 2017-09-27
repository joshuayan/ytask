package cn.apier.app.ytask.ui.task

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.apier.app.ytask.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_task_alarm.*
import java.util.concurrent.TimeUnit

class TaskAlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_alarm)

        val task = this.intent.getStringExtra("task")
        tvTask.text = task


        val dis = Observable.timer(10000, TimeUnit.MILLISECONDS).subscribe { this.finish() }

        tvStop.setOnClickListener {
            this.finish()
            dis.dispose()
        }


    }
}
