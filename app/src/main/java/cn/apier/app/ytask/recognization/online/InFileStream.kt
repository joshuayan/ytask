package cn.apier.app.ytask.recognization.online

import android.app.Activity


import java.io.IOException
import java.io.InputStream

import cn.apier.app.ytask.util.Logger

/**
 * Created by fujiayi on 2017/6/20.
 */

object InFileStream {

    private var context: Activity? = null

    private val TAG = "InFileStream"
    fun setContext(context: Activity) {
        InFileStream.context = context
    }

    fun create16kStream(): InputStream? {
        var `is`: InputStream? = null
        Logger.info(TAG, "cmethod call")
        try {
            `is` = context!!.assets.open("outfile.pcm")
            Logger.info(TAG, "create input stream ok" + `is`!!.available())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return `is`
    }
}