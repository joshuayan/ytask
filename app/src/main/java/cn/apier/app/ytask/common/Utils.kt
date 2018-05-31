package cn.apier.app.ytask.common

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


/**
 * Created by yanjunhua on 2017/9/6.
 */
object Utils {

    fun md5(input: String): String? {
        Objects.requireNonNull(input)
        var result: String? = null
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(input.toByteArray())
            val b = md.digest()
            val buf = StringBuffer()
            for (i in 1..b.size - 1) {

                var c = (b[i].toInt() ushr 4) and 0xf
                buf.append(Integer.toHexString(c))
                c = b[i].toInt() and 0xf
                buf.append(Integer.toHexString(c))
            }
            result = buf.toString()
        } catch (e: NoSuchAlgorithmException) {
            println("No MD5 Algorithm.")
        }

        return result
    }

    fun normalizeDateTimeStr(str: String): String {
        var result = str
        with(str) {
            when {
                matches("""\d{4}-\d{2}-\d{2}\|\d{2}:\d{2}:\d{2}""".toRegex()) -> {
                    result = replace("|", " ")
                }
                matches("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}""".toRegex()) -> {
                    result = replace("T", " ")
                }
                matches("""\d{4}-\d{2}-\d{2}""".toRegex()) -> {
                    result = "$this 00:00:00"
                }
                matches("""\d{2}:\d{2}:\d{2}""".toRegex()) -> {
                    val now = java.util.Date(java.lang.System.currentTimeMillis())
                    val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd").format(now)
                    result = "$dateStr $this"
                }
//                isEmpty() -> {
//                    android.util.Log.d(cn.apier.app.ytask.common.Constants.TAG_LOG, "No Time info")
//                }
//                else -> {
//                    android.util.Log.d(cn.apier.app.ytask.common.Constants.TAG_LOG, "Unknown")
//                }
            }
        }
        return result
    }

}