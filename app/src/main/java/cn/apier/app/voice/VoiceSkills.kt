package cn.apier.app.voice

import cn.apier.app.voice.nlp.NLPResult
import cn.apier.app.ytask.recognization.BDRecognizerHelper
import cn.apier.app.ytask.unit.UnitHelper

/**
 * Created by yanjunhua on 2017/9/29.
 */

enum class VoiceSkillType {
    ASR, WAKEUP, NLP
}

interface VoiceSkill {
    fun init()
    fun start()
    fun stop()
    fun release()
    fun type(): VoiceSkillType
    fun name(): String
}

interface AsrSkill : VoiceSkill {
    override fun type(): VoiceSkillType = VoiceSkillType.ASR
    fun asr(onResult: (txt: String) -> Unit)
}

interface WakeUpSkill : VoiceSkill {
    override fun type(): VoiceSkillType = VoiceSkillType.WAKEUP
    fun waitToWakeUp()
    fun wakeup()
}


interface NlpSkill : VoiceSkill {
    fun understand(text: String, onResult: (result: NLPResult) -> Unit)
    override fun type(): VoiceSkillType = VoiceSkillType.NLP
}


abstract class BaseAsrSkill : AsrSkill {
    override fun init() {
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun release() {
    }
}

class BDASRSkill : BaseAsrSkill() {
    override fun asr(onResult: (txt: String) -> Unit) {
        BDRecognizerHelper.start()
    }

    companion object {
        val NAME = "bd-asr"
    }

    override fun name(): String = NAME
}


class BDNlpSkill : NlpSkill {
    override fun understand(text: String, onResult: (result: NLPResult) -> Unit) {
        UnitHelper.understand(text)

    }

    override fun init() {
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun release() {
    }

    override fun name(): String = "bd-nlp"


}