package cn.apier.app.ytask.common

/**
 * Created by yanjunhua on 2017/9/6.
 */
object Constants {
    private const val APP_KEY = "069ec15660f9aca72e68da74dc9b74"
    private const val TEST_APP_KEY = "effbc5ff1de61716b474e1e1638276"
    private const val SECRET_KEY = "b5163126bf53e5fa458b9f8ffafa1b"
    private const val TEST_APP_SECRET = "bca2402e8b1b20f225eed04898a07d"
    private const val BASE_URL = "http://api.apier.cn"
    private const val TEST_BASE_URL = "http://192.168.3.3:7700"
    const val TAG_LOG = "ytask"
    const val SCENE_ID = 10602

    const val SLOT_TIME = "sys_time"
    const val SLOT_TODO = "user_todo"
    const val SLOT_CMD = "user_cmd"

    const val CMD_ADD_TASK = "添加任务"

    fun baseUrl(debug: Boolean) = if (debug) TEST_BASE_URL else BASE_URL

    fun appKey(debug: Boolean) = if (debug) TEST_APP_KEY else APP_KEY

    fun secretKey(debug: Boolean) = if (debug) TEST_APP_SECRET else SECRET_KEY
}