package cn.apier.ytask.recognization

/**
 * Created by fujiayi on 2017/6/14.
 */

interface IStatus {
    companion object {

        val STATUS_NONE = 2

        val STATUS_READY = 3
        val STATUS_SPEAKING = 4
        val STATUS_RECOGNITION = 5

        val STATUS_FINISHED = 6
        val STATUS_STOPPED = 10

        val STATUS_WAITING_READY = 8001
        val WHAT_MESSAGE_STATUS = 9001
    }
}
