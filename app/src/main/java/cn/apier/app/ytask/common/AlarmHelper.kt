package cn.apier.app.ytask.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.ui.task.TaskAlarmActivity
import java.text.SimpleDateFormat

object AlarmHelper {

    private val TAG = AlarmHelper::class.java.simpleName

    fun alarm(timer: String?, msg: String) {
        var deadLine: String? = null

        timer?.also {
            with(it) {
                when {
                    matches("""\d{4}-\d{2}-\d{2}\|\d{2}:\d{2}:\d{2}""".toRegex()) -> {
                        deadLine = replace("|", " ")
                    }
                    matches("""\d{4}-\d{2}-\d{2}""".toRegex()) -> {
                        deadLine = "$it 00:00:00"
                    }
                    matches("""\d{2}:\d{2}:\d{2}""".toRegex()) -> {
                        val now = java.util.Date(java.lang.System.currentTimeMillis())
                        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd").format(now)
                        deadLine = "$dateStr $it"
                    }
                    isEmpty() -> {
                        android.util.Log.d(cn.apier.app.ytask.common.Constants.TAG_LOG, "No Time info")
                    }
                    else -> {
                        android.util.Log.d(cn.apier.app.ytask.common.Constants.TAG_LOG, "Unknown")
                    }
                }
            }
        }
        Log.d(Constants.TAG_LOG, "deadLine:$deadLine")



        deadLine?.let {

            val alarmManager = YTaskApplication.currentApplication.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deadLine).time
            val intent = Intent(YTaskApplication.currentApplication.currentActivity, TaskAlarmActivity::class.java)

            intent.putExtra("task", msg)
            val pendingIntent = PendingIntent.getActivity(YTaskApplication.currentApplication.currentActivity, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            Log.d(TAG, "Added Alarm.")

        }

    }

}