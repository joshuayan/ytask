package cn.apier.app.ytask.unit

import android.text.TextUtils
import android.util.Log
import cn.apier.app.ytask.api.ApiFactory
import cn.apier.app.ytask.api.TaskApi
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.scene.SceneActionDispatcher
import com.baidu.aip.chatkit.model.Message
import com.baidu.aip.chatkit.model.User
import com.baidu.aip.chatkit.utils.DateFormatter
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.model.CommunicateResponse
import java.util.*

/**
 * Created by yanjunhua on 2017/9/24.
 */
object UnitHelper {


    private var sessionId: String = ""
    private var mid: Long = 1L
    private val sender: User = User("1", "joshua", "Joshua Yan", true)
    private val taskApi: TaskApi = ApiFactory.apiProxy(TaskApi::class.java)


    fun understand(txt: String) {

        APIService.getInstance().communicate(object : OnResultListener<CommunicateResponse> {
            override fun onResult(result: CommunicateResponse) {

                handleResponse(result)
            }

            override fun onError(error: UnitError) {

            }
        }, Constants.SCENE_ID, txt, System.currentTimeMillis().toString())
    }


    private fun handleResponse(response: CommunicateResponse?) {
        response?.let {

            Log.i(Constants.TAG_LOG, "unit response :$it")

            SceneActionDispatcher.dispatch(response)

        }
    }


}