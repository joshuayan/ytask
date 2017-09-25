package cn.apier.app.ytask.dto

/**
 * Created by yanjunhua on 2017/9/6.
 */


data class TokenDto(val uid: String = "", val code: String = "", val appKey: String = "", val expiredAt: Long = 0)

data class BDApplicationInfo(val appKey: String, val secretKey: String)

data class TaskDto(val uid:String,val content:String,val deadLine:String?)