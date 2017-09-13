package cn.apier.app.ytask.common

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and


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
}