package cn.apier.ytask.api

import java.io.Serializable

/**
 * Created by yanjunhua on 15/5/8.
 */
class ResultStatus : Serializable {
    var code: String = ""
    var description: String = ""


    override fun toString(): String {
        return "ResultStatus(code='$code', description='$description')"
    }

}
