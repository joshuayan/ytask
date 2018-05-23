package cn.apier.app.voice

import android.util.Log
import cn.apier.app.voice.nlp.NLPResult

/**
 * Created by yanjunhua on 2017/10/11.
 */
class VoiceEngine {

    private val defaultAfterAsr: (txt: String) -> Unit = {}


    private val TAG = VoiceEngine::class.java.simpleName
    private var phase: VoiceEnginePhase = VoiceEnginePhase.NEW

    private val typeSkills: MutableMap<VoiceSkillType, VoiceSkillTypeHolder> = mutableMapOf()


    fun addVoiceSkill(skill: VoiceSkill) {
        val pvh = this.typeSkills.getOrDefault(skill.type(), VoiceSkillTypeHolder(skill.type()))
        pvh.addSkill(skill)
        this.typeSkills.put(skill.type(), pvh)
    }


    fun setTypeCurrentSkill(type: VoiceSkillType, name: String) {
        checkPhase(VoiceEnginePhase.NEW, errMsg = "Can not set Skill,as engine has been inited")
        this.typeSkills[type]?.setCurrent(name)
    }


    fun init() {
        checkPhase(VoiceEnginePhase.NEW, errMsg = "Can not be Inited again.")
        this.typeSkills.values.forEach { it.currentSkill?.init() }
        this.phase = VoiceEnginePhase.INIT
    }

    fun start() {
        checkPhase(VoiceEnginePhase.STOPPED, VoiceEnginePhase.INIT, errMsg = "Can not start.")
        this.typeSkills.values.forEach { it.currentSkill?.start() }
        this.phase = VoiceEnginePhase.STARTED
    }


    fun asr(onResult: (txt: String) -> Unit) {
        checkPhase(VoiceEnginePhase.STARTED, errMsg = "Engine has to be started.")

        val currentSkill = this.getCurrentSkill(cn.apier.app.voice.VoiceSkillType.ASR)
        currentSkill?.let {
            val asrSkill = it as AsrSkill
            asrSkill.asr(onResult)
        }
    }

    fun wakeup() {
        checkPhase(VoiceEnginePhase.STARTED, errMsg = "Engine has to be started.")

        val currentSkill = this.getCurrentSkill(cn.apier.app.voice.VoiceSkillType.WAKEUP)
        currentSkill?.let {
            val wuSkill = it as WakeUpSkill
            wuSkill.wakeup()
        }
    }


    fun waitToWakeUp() {
        checkPhase(VoiceEnginePhase.STARTED, errMsg = "Engine has to be started.")

        val currentSkill = this.getCurrentSkill(cn.apier.app.voice.VoiceSkillType.WAKEUP)
        currentSkill?.let {
            val wuSkill = it as WakeUpSkill
            wuSkill.waitToWakeUp()
        }
    }


    fun nlp(txt: String): NLPResult {

        checkPhase(VoiceEnginePhase.STARTED, errMsg = "Engine has to be started.")
        val currentSkill = this.getCurrentSkill(cn.apier.app.voice.VoiceSkillType.NLP)
        currentSkill?.let {
            val wuSkill = it as NlpSkill
            wuSkill.understand(txt, {})
        }


        return NLPResult.fail(txt)

    }


    private fun getCurrentSkill(type: VoiceSkillType): VoiceSkill? {

        val currentSkill = this.typeSkills[type]?.currentSkill
        if (currentSkill == null) {
            Log.w(TAG, "No current voice skill of type [$type].")
        }
        return currentSkill
    }

    private fun checkPhase(vararg expectPhase: VoiceEnginePhase, errMsg: String) {
        if (!expectPhase.contains(this.phase)) {
            throw RuntimeException(errMsg)
        }
    }


    private class VoiceSkillTypeHolder(private val type: VoiceSkillType) {
        var currentSkill: VoiceSkill? = null
            private set
        private val skills: MutableMap<String, VoiceSkill> = mutableMapOf()

        fun addSkill(skill: VoiceSkill) {
            if (this.type == skill.type()) {
                skills.put(skill.name(), skill)
            }
        }

        fun setCurrent(name: String) {
            skills[name]?.let { this.currentSkill = it }
        }
    }
}


enum class VoiceEnginePhase {
    NEW, INIT, STARTED, STOPPED, RELEASED
}