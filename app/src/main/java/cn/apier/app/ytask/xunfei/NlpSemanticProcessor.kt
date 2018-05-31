package cn.apier.app.ytask.xunfei


interface NlpSemanticProcessor {
    fun process(semantic: Semantic)
    fun intent(): String
}



