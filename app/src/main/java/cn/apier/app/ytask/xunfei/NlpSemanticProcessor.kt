package cn.apier.app.ytask.xunfei

import android.util.Log
import cn.apier.app.ytask.application.YTaskApplication
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject


interface NlpSemanticProcessor {
    fun process(semantic: Semantic)
    fun intent(): String
}



