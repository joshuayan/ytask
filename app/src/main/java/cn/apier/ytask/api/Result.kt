package cn.apier.ytask.api

import java.io.Serializable

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
