package cn.apier.app.ytask.api

import java.io.Serializable
import java.util.Objects

/**
 * Created by yanjunhua on 15/5/8.
 */
class Result<T : Any> : Serializable {
    var success: Boolean = false
    var status: ResultStatus? = null
    var data: T? = null



    override fun toString(): String {
        return "Result(success=$success, status=$status, data=$data)"
    }

}
