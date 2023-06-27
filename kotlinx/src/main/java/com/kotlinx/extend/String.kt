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

/**打印日志，可以打印任意长度字符串，超过4000Bit（二进制数位）自动换行*/
/*举例： "注意".logV() */
fun String.logV(tag: String = "VERBOSE"): String {
    LogListener?.invoke(Log.VERBOSE, tag, this, null)
    this.groupActual(4000).forEach { Log.v(tag, it.toString()) }
    return this
}

/**打印日志，可以打印任意长度字符串，超过4000Bit（二进制数位）自动换行*/
/*举例： "注意".logD() */
fun String.logD(tag: String = "DEBUG"): String {
    LogListener?.invoke(Log.DEBUG, tag, this, null)
    this.groupActual(4000).forEach { Log.d(tag, it.toString()) }
    return this
}

/**打印日志，可以打印任意长度字符串，超过4000Bit（二进制数位）自动换行*/
/*举例： "注意".logI() */
fun String.logI(tag: String = "INFO"): String {
    LogListener?.invoke(Log.INFO, tag, this, null)
    this.groupActual(4000).forEach { Log.i(tag, it.toString()) }
    return this
}

/**打印日志，可以打印任意长度字符串，超过4000Bit（二进制数位）自动换行*/
/*举例： "注意".logW() */
fun String.logW(tag: String = "WARN"): String {
    LogListener?.invoke(Log.WARN, tag, this, null)
    this.groupActual(4000).forEach { Log.w(tag, it.toString()) }
    return this
}

/**打印日志，可以打印任意长度字符串，超过4000Bit（二进制数位）自动换行*/
/*举例： "注意".logE() */
fun String.logE(tag: String = "ERROR", t: Throwable? = null): String {
    LogListener?.invoke(Log.ERROR, tag, this, t)
    this.groupActual(4000).forEach {
        if (t == null) Log.e(tag, it.toString()) else Log.e(tag, it.toString(), t)
    }
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
    this.toFile(File(path), charset)
}

/**外部储存读取文件成String*/
/*
var ip: String
    get() = "服务器IP.txt".readPath() ?: "127.0.0.1"
    set(value) = value.writePath("服务器IP.txt" )
 */
fun String.readPath(path: String = ExternalFile.getDir().toString()): String? {
    return File("$path/$this").string()
}

/**写入文本到外部储存*/
/*
var port: Int
    get() = "端口.txt".readPath()?.toInt() ?: 8080
    set(value) = value.toString().writePath("端口.txt")
 */
fun String?.writePath(fileName: String, path: String = ExternalFile.getDir().toString()) {
    (this ?: "").toFile("$path/$fileName")
}

/**将字符串添加到文件*/
/*举例 "你好".addFile(File("D:/abc.txt")) */
fun String.addFile(file: File, charset: Charset = Charset.defaultCharset()) {
    this.toByteArray(charset).addFile(file)
}

fun String.addFile(path: String, charset: Charset = Charset.defaultCharset()) {
    this.addFile(File(path), charset)
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

/**
 * 字符串分组，每digit位字符拆分一次字符串，中文英文都算一个字符
 * @param digit 位
 * @return 拆分后的字符串
 */
/*
举例：
    "你好啊1234567890".group(3).forEach {
        println(it.toString())
    }
结果：
    你好啊
    123
    456
    789
    0
*/
fun String.group(digit: Int): List<StringBuilder> {
    if (this.length < digit) {
        val strings: MutableList<StringBuilder> = ArrayList()
        strings.add(StringBuilder(this))
        return strings
    }
    val strings: MutableList<StringBuilder> = ArrayList()
    var sb = StringBuilder()
    for (i in 0 until this.length) {
        val c = this[i] //获取每一个字
        sb.append(c)
        if (i % digit == digit - 1) { //如果是digit的倍数就换行
            strings.add(sb)
            sb = StringBuilder()
        }
    }
    if (sb.isNotEmpty()) strings.add(sb)
    return strings
}


/**
 * 字符串分组，每digit位字符拆分一次字符串，英文算一个字符，中文算两个字符
 *
 * @param digit 位
 * @return 拆分后的字符串
 */
/*
举例：
    "你好啊1234567890".groupDouble(3).forEach {
        println(it.toString())
    }
结果：
    你
    好
    啊1
    234
    567
    890
*/
fun String.groupDouble(digit: Int): List<StringBuilder> {
    if (this.length < digit / 2) {
        val strings: MutableList<StringBuilder> = ArrayList()
        strings.add(StringBuilder(this))
        return strings
    }
    val strings: MutableList<StringBuilder> = ArrayList()
    var sb = StringBuilder()
    var index = 0
    var i = 0
    while (i < this.length) {
        val c = this[i] //获取每一个字
        index = if (c.code in 1..127) index + 1 else index + 2
        if (index > digit) { //如果大于就换行
            index = 0
            strings.add(sb)
            sb = StringBuilder()
            continue
        }
        sb.append(c)
        if (index >= digit) { //如果大于2倍就换行
            index = 0
            strings.add(sb)
            sb = StringBuilder()
        }
        i++
    }
    if (sb.isNotEmpty()) strings.add(sb)
    return strings
}


/**
 * 字符串分组，每digit位字符拆分一次字符串，英文算一个字符，中文根据字符串编码算个字符
 *
 * @param digit 位
 * @return 拆分后的字符串
 */
/*
举例：
    "你好啊1234567890".groupActual(3).forEach {
        println(it.toString())
    }
结果：
    你
    好
    啊
    123
    456
    789
    0
*/
fun String.groupActual(digit: Int, charset: Charset? = Charset.defaultCharset()): List<StringBuilder> {
    if (digit < 3) return this.group(digit)
    if (this.length < digit / 3) {
        val strings: MutableList<StringBuilder> = ArrayList()
        strings.add(StringBuilder(this))
        return strings
    }
    val strings: MutableList<StringBuilder> = ArrayList()
    var sb = StringBuilder()
    var index = 0
    val length = this.length
    var i = 0
    while (i < length) {
        val c = this[i] //获取每一个字
        index += c.toString().toByteArray(charset ?: Charset.defaultCharset()).size
        //如果大于就换行
        if (index > digit) {
            index = 0
            strings.add(sb)
            sb = StringBuilder()
            continue
        }
        //连接字符串
        sb.append(c)
        if (index == digit) { //如果大于就换行
            index = 0
            strings.add(sb)
            sb = StringBuilder()
        }
        i++
    }
    if (sb.isNotEmpty()) strings.add(sb)
    return strings
}

/**
 * 字符串每隔digit位添加一个符号
 * @param digit        每隔digit位添加一个符号
 * @param insertString 添加的符号
 * @return 结果
 */
/*
* 举例：println("你好啊1234567890".insert(3,"⊙"))
* 结果：你好啊⊙123⊙456⊙789⊙0
*/
fun String.insert(digit: Int, insertString: String): String {
    val regex = "(.{$digit})"
    return this.replace(regex.toRegex(), "$1$insertString")
}