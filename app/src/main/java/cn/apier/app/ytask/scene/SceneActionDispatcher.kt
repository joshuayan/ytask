package cn.apier.app.ytask.scene

import android.util.Log
import cn.apier.app.voice.nlp.NLPResult
import cn.apier.app.ytask.scene.processor.SceneActionProcessor
import cn.apier.app.ytask.synthesization.SynthesizerHelper
import com.baidu.aip.unit.model.UnitResponseResult

/**
 * Created by yanjunhua on 2017/9/25.
 */
object SceneActionDispatcher {

    private val TAG = SceneActionDispatcher::class.java.simpleName
    private val processors: MutableMap<String, SceneActionProcessor> = mutableMapOf()


//    @Deprecated("")
//    fun dispatch(unitSceneResponseResult: UnitResponseResult) {
//
//        unitSceneResponseResult.result.actionList.forEach { action ->
//
//            var processed = false
//            val actionId = action.actionId
//            this.processors[actionId]?.let {
//                if (it.canProcess(actionId)) {
//                    it.process(unitSceneResponseResult.result)
//                    processed = true
//                }
//            }
//
//            if (!processed) {
//                Log.w(TAG, "Unknown action: $actionId")
//                SynthesizerHelper.speak("我还不懂你说的话")
//            }
//        }
//    }


    fun dispatch(nlpResult: NLPResult) {

        var processed = false

        val intent = nlpResult.intent
        Log.i(TAG, "intent:$intent")
        this.processors[intent]?.let {
            val responseResult = nlpResult.data as UnitResponseResult
            it.process(responseResult.result)
            processed = true
        }
        if (!processed) {
            Log.w(TAG, "Unknown action: $intent")
            SynthesizerHelper.speak("我还不懂你说的话")
        }
    }


    fun addProcessor(intent: String, processor: SceneActionProcessor) {
        this.processors.put(intent, processor)
    }
}