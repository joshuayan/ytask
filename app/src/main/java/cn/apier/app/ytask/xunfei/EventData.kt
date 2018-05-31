package cn.apier.app.ytask.xunfei

/**
 * Created by yanjunhua on 2017/10/10.
 */


class EventInfo {
    var data: Array<InfoData> = arrayOf()
}


class InfoData {
    var params: Params = Params()
    var content: Array<Content> = arrayOf()
}

class Params {
    companion object {
        /**
         * 听写结果(iat)
         * 语义结果(nlp)
         * 后处理服务结果(tpp)
         */
        var SUB_IAT = "iat"
        var SUB_NLP = "nlp"
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


data class IatResult(
        val text: IatText
)

data class IatText(
        val bg: Int,
        val sn: Int,
        val ws: List<W>,
        val ls: Boolean,
        val ed: Int
)

data class W(
        val bg: Int,
        val cw: List<Cw>
)

data class Cw(
        val w: String,
        val sc: Int
)


data class NlpResult(
        val intent: NlpIntent
) {
    fun isSuccess() = intent.isSuccess()

}

data class NlpIntent(
        val category: String,
        val intentType: String,
        val rc: Int,
        val service: String,
        val uuid: String,
        val vendor: String,
        val version: String,
        val semantic: List<Semantic>,
        val state: Any,
        val sid: String,
        val text: String
) {
    fun isSuccess() = this.rc == 0
}

data class Semantic(
        val entrypoint: String,
        val intent: String,
        val score: Double,
        val slots: List<Slot>
)

data class Slot(
        val begin: Int,
        val end: Int,
        val name: String,
        val normValue: String,
        val value: String
)