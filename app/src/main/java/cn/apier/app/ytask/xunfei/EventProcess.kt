package cn.apier.app.ytask.xunfei

/**
 * Created by yanjunhua on 2017/10/10.
 */


class EventInfo {
    var data: Array<InfoData>? = null
}


class InfoData {
    var params: Params? = null
    var content: Array<Content>? = null
}

class Params {
    companion object {
        /**
         * 听写结果(iat)
         * 语义结果(nlp)
         * 后处理服务结果(tpp)
         */
        var SUB_IAT = "iat"
        var SUB_NLP = "NLP"
        var SUB_TPP = "tpp"
    }

    var sub: String = "iat"

    fun isNLP() = SUB_NLP == sub
    fun isIAT() = SUB_IAT == sub
    fun isTPP() = SUB_TPP == sub
}

class Content {
    var dte: String = "utf8"
    var dtf: String = "json"
    var cnt_id: String? = null

    fun containCntId() = cnt_id != null
}