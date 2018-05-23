package cn.apier.app.ytask.unit

import android.util.Log
import cn.apier.app.voice.nlp.NLPResult
import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.TaskApi
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.scene.SceneActionDispatcher
import cn.apier.app.ytask.scene.SceneActions
import com.baidu.aip.chatkit.model.User
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.model.UnitResponseResult
import java.util.*

/**
 * Created by yanjunhua on 2017/9/24.
 */
object UnitHelper {

    var onResult: (result: NLPResult) -> Unit = { SceneActionDispatcher.dispatch(it) }

    private var sessionId: String = ""
    private var mid: Long = 1L
    private val sender: User = User("1", "joshua", "Joshua Yan", true)
    private val taskApi: TaskApi = ApiFactory.apiProxy(TaskApi::class.java)


    fun understand(txt: String) {

        APIService.getInstance().communicate(object : OnResultListener<UnitResponseResult> {
            override fun onResult(result: UnitResponseResult) {

//                handleResponse(result)

                if(result.result!=null) {

                    val actionId = result.result.actionList[0].actionId
                    val intent = actionId.substringBefore(SceneActions.POSTFIX_ACTION_RESPONSE)
                    val nlpResult = NLPResult.ok(txt, intent, result)
                    onResult(nlpResult)
                }

            }

            override fun onError(error: UnitError) {

            }
        }, Constants.SCENE_ID, txt, System.currentTimeMillis().toString())
    }

}