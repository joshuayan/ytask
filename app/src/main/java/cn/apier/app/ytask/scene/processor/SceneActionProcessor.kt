package cn.apier.app.ytask.scene.processor

import com.baidu.aip.unit.model.ResponseResult

/**
 * Created by yanjunhua on 2017/9/25.
 */
interface SceneActionProcessor {
    fun process(result: ResponseResult.Result)
    fun canProcess(action: String): Boolean
}