package com.kotlinx.extend

import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.kotlinx.Kotlinx.app
import com.kotlinx.Kotlinx.toast
import com.kotlinx.utils.ExternalFile
import com.kotlinx.utils.TTS
import com.kotlinx.utils.ui
import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

/** 判断是否是Int*/
/*举例： "123".isInt() */
fun String.isInt(): Boolean {
    return this.matches(Regex("""^-?(([1-9]\d*$)|0)"""))
}

/** 判断是否是double*/
fun String.isDouble(): Boolean {
    return this.matches(Regex("""^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$"""))
}

/** 判断是否是Int或者double*/
fun String.isIntOrDouble(): Boolean {
    return this.isInt() || this.isDouble()
}

/** 判断是否是数字（Int或者double）*/
fun String.isNumber(): Boolean {
    return this.isIntOrDouble()
}

/** 判断是否是空串或者Int*/
fun String.isEmptyOrInt(): Boolean {
    return this.isEmpty() || this.isInt()
}

/** 判断是否是空串或者Int或者double*/
fun String.isEmptyOrIntOrDouble(): Boolean {
    return this.isEmpty() || this.isInt() || this.isDouble()
}

/** 判断是否是Email*/
fun String.isEmail(): Boolean {
    return this.matches(Regex("""\w+(\.\w+)*@\w+(\.\w+)+"""))
}

/** 判断是否是26个英文字母*/
fun String.isEnglish(): Boolean {
    return this.matches(Regex("""^[A-Za-z]+$"""))
}

/** 判断是否是数字、26个英文字母*/
fun String.isEnglishAndNumber(): Boolean {
    return this.matches(Regex("""^[A-Za-z0-9]+"""))
}

/** 判断是否是数字、26个英文字母或者下划线组成的字符串*/
fun String.isisEnglishAndNumberAndUnderline(): Boolean {
    return this.matches(Regex("""^\w+$"""))
}

/** 判断是否是中文字符*/
fun String.isChinese(): Boolean {
    return this.matches(Regex("""^[\u4E00-\u9FA5]+$"""))
}

/** 判断是否是网址 */
fun String.isUrl(): Boolean {
    val pattern = """^(http|https|www|ftp|)?(://)?(\w+(-\w+)*)(\.(\w+(-\w+)*))*((:\d+)?)(/(\w+(-\w+)*))*(\.?(\w)*)(\?)?""" +
            """(((\w*%)*(\w*\?)*(\w*:)*(\w*\+)*(\w*\.)*(\w*&)*(\w*-)*(\w*=)*(\w*%)*(\w*\?)*""" +
            """(\w*:)*(\w*\+)*(\w*\.)*""" +
            """(\w*&)*(\w*-)*(\w*=)*)*(\w*)*)$"""
    return this.matches(Regex(pattern))
}

/** 判断是否是http或https */
fun String.isHttp(): Boolean {
    val pattern = """(https?://(w{3}\.)?)?\w+\.\w+(\.[a-zA-Z]+)*(:\d{1,5})?(/\w*)*(\??(.+=.*)?(&.+=.*)?)?"""
    return this.matches(Regex(pattern))
}

/** 判断是否是IPv4 */
/*举例： "192.168.1.1".isIPv4() */
fun String.isIPv4(): Boolean {
    val pattern = """((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))"""
    return this.matches(Regex(pattern))
}

/** 判断是否是IPv6 */
fun String.isIPv6(): Boolean {
    val pattern =
        """^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:)|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}(:[0-9A-Fa-f]{1,4}){1,2})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){1,3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){1,4})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){1,5})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){1,6})|(:(:[0-9A-Fa-f]{1,4}){1,7})|(([0-9A-Fa-f]{1,4}:){6}(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){5}:(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}:(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}:(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}:(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){0,4}:(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})|(:(:[0-9A-Fa-f]{1,4}){0,5}:(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3}))$"""
    return this.matches(Regex(pattern))
}

/** 判断是否是端口*/
fun String.isPort(): Boolean {
    val pattern = """([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])"""
    return this.matches(Regex(pattern))
}

/** 判断是否是银行卡号*/
fun String.isBankCard(): Boolean {
    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeBankCard 不含校验位的银行卡卡号
     * @return 是否通过
     */
    fun getBankCardCheckCode(nonCheckCodeBankCard: String?): Char {
        if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim { it <= ' ' }.isEmpty() || !nonCheckCodeBankCard.matches(Regex("""\d+"""))) return 'N'
        val chs = nonCheckCodeBankCard.trim { it <= ' ' }.toCharArray()
        var sum = 0
        var i = chs.size - 1
        var j = 0
        while (i >= 0) {
            var k = chs[i] - '0'
            if (j % 2 == 0) {
                k *= 2
                k = k / 10 + k % 10
            }
            sum += k
            i--
            j++
        }
        return if (sum % 10 == 0) '0' else (10 - sum % 10 + '0'.code).toChar()
    }

    /**
     * 校验银行卡卡号
     *
     * @param bankCard 银行卡卡号
     * @return 是否通过
     */
    fun isBankCard(bankCard: String): Boolean {
        if (bankCard.length < 15 || bankCard.length > 19) return false
        val bit: Char = getBankCardCheckCode(bankCard.substring(0, bankCard.length - 1))
        return bit != 'N' && bankCard[bankCard.length - 1] == bit
    }
    return isBankCard(this)
}


/**调用TTS播放文字语音*/
/*举例： "你好".speak() */
fun String.speak(): String {
    TTS.speak(this)
    return this
}

/**调用TTS播放文字语音放入队列*/
/*举例： "你好".speakQueue() */
fun String.speakQueue(): String {
    TTS.speakQueue(this)
    return this
}

/**打印日志*/
/*举例： "注意".logV() */
fun String.logV(tag: String = "VERBOSE"): String {
    Log.v(tag, this)
    LogListener?.invoke(Log.VERBOSE, tag, this, null)
    return this
}

/**打印日志*/
/*举例： "注意".logD() */
fun String.logD(tag: String = "DEBUG"): String {
    Log.d(tag, this)
    LogListener?.invoke(Log.DEBUG, tag, this, null)
    return this
}

/**打印日志*/
/*举例： "注意".logI() */
fun String.logI(tag: String = "INFO"): String {
    Log.i(tag, this)
    LogListener?.invoke(Log.INFO, tag, this, null)
    return this
}

/**打印日志*/
/*举例： "注意".logW() */
fun String.logW(tag: String = "WARN"): String {
    Log.w(tag, this)
    LogListener?.invoke(Log.WARN, tag, this, null)
    return this
}

/**打印日志*/
/*举例： "注意".logE() */
fun String.logE(tag: String = "ERROR", t: Throwable? = null): String {
    if (t == null) Log.e(tag, this) else Log.e(tag, this, t)
    LogListener?.invoke(Log.ERROR, tag, this, t)
    return this
}

/**在ui线程弹出一个toast*/
/*举例： "你好".toast() */
fun String.toast(): String {
    return this.toastShort()
}

/**在ui线程弹出一个toast*/
fun String.toastShort(): String {
    val string = this
    ui {
        toast?.let {
            toast?.cancel()
            toast = null
        }
        toast = Toast.makeText(app, string, Toast.LENGTH_SHORT)
        toast?.show()
    }
    return this
}

/**在ui线程弹出一个toast*/
fun String.toastLong(): String {
    val string = this
    ui {
        toast?.let {
            toast?.cancel()
            toast = null
        }
        toast = Toast.makeText(app, string, Toast.LENGTH_LONG)
        toast?.show()
    }
    return this
}

/**
 * base64编码返回ByteArray
 */
fun String.base64Decode(): ByteArray {
    return Base64.decode(this.toByteArray(), Base64.NO_WRAP) //不自动换行
}

/** 如果是int就转化成int 否则返回 default*/
fun String.isIntToInt(default: Int = 0): Int {
    return if (this.isInt()) this.toInt() else default
}

/** 如果是Double就转化成Double 否则返回 default*/
fun String.isDoubleToDouble(default: Double = 0.0): Double {
    return if (isDouble()) this.toDouble() else default
}

/** 如果是Double或者int就转化成Double 否则返回 default*/
fun String.isDoubleOrIntToDouble(default: Double = 0.0): Double {
    return if (this.isInt() || isDouble()) this.toDouble() else default
}

/**将字符串写入文件*/
/*举例 "你好".toFile(File("D:/abc.txt")) */
fun String.toFile(file: File, charset: Charset = Charset.defaultCharset()) {
    this.toByteArray(charset).toFile(file)
}

fun String.toFile(path: String, charset: Charset = Charset.defaultCharset()) {
    this.toFile(File(path),charset)
}

/**外部储存读取文件成String*/
/*
var ip: String
    get() = "服务器IP.txt".readPath() ?: "127.0.0.1"
    set(value) = value.writePath("服务器IP.txt" )
 */
fun String.readPath(): String? {
    return File(ExternalFile.getDir() + "/" + this).string()
}

/**写入文本到外部储存*/
/*
var port: Int
    get() = "端口.txt".readPath()?.toInt() ?: 8080
    set(value) = value.toString().writePath("端口.txt")
 */
fun String?.writePath(fileName: String) {
    (this ?: "").toFile(ExternalFile.getDir() + "/" + fileName)
}

/**将字符串添加到文件*/
/*举例 "你好".addFile(File("D:/abc.txt")) */
fun String.addFile(file: File, charset: Charset = Charset.defaultCharset()) {
    this.toByteArray(charset).addFile(file)
}
fun String.addFile(path: String, charset: Charset = Charset.defaultCharset()) {
    this.addFile(File(path),charset)
}

/**将字符串转换成base64*/
fun String.toBase64(charset: Charset = Charset.defaultCharset()): ByteArray {
    return this.toByteArray(charset).toBase64Encode()
}

/**将字符串转换成base64*/
/*举例："你好".toBase64String()*/
fun String.toBase64String(charset: Charset = Charset.defaultCharset()): String {
    return this.toByteArray(charset).toBase64EncodeToString()
}

/**将base64字符串转换成String*/
/*举例："5L2g5aW9".toStringFromBase64()*/
fun String.toStringFromBase64(charset: Charset = Charset.defaultCharset()): String {
    return String(this.toByteArray(charset).toBase64Decode(), charset)
}

/** 将日期格式的文本转换成Date**/
/*举例："5L2g5aW9".toStringFromBase64()*/
fun String.parseDate(dateStyle: String = "yyyy-MM-dd HH:mm:ss"): Date? {
    val sdf = SimpleDateFormat(dateStyle, Locale.getDefault())
    return sdf.parse(this)
}