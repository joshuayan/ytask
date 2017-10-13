package cn.apier.app.ytask.scene

import android.util.Log
import cn.apier.app.ytask.scene.processor.SceneActionProcessor
import cn.apier.app.ytask.synthesization.SynthesizerHelper
import cn.apier.app.ytask.xunfei.AIUIHelper
import com.baidu.aip.unit.model.CommunicateResponse

/**
 * Created by yanjunhua on 2017/9/25.
 */
object SceneActionDispatcher {

    private val TAG = SceneActionDispatcher::class.java.simpleName
    private val processors: MutableList<SceneActionProcessor> = mutableListOf()

    fun dispatch(unitSceneResponseResult: CommunicateResponse) {

        unitSceneResponseResult.result.actionList.forEach { action ->

            var processed = false
            val actionId = action.actionId
            this.processors.forEach { processor ->
                if (processor.canProcess(actionId)) {
                    processor.process(unitSceneResponseResult.result)
                    processed = true
                }
            }

            if (!processed) {
                Log.w(TAG, "Unknown action: $actionId")


//                SynthesizerHelper.speak("我还不懂你说的话")
            }
        }
    }

    fun addProcessor(processor: SceneActionProcessor) {
        this.processors.add(processor)
    }
}