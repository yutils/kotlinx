@file:Suppress("DEPRECATION")

package com.kotlinx.utils

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import com.kotlinx.Kotlinx
import com.kotlinx.extend.logE
import com.kotlinx.extend.logI
import com.kotlinx.extend.toast
import com.kotlinx.utils.TTS.speak
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

private const val SG_TTS_PACKAGE = "org.nobody.sgtts"

/**
 * 默认 TTS 引擎包名尝试顺序（[init] 未传入非空的 `enginePackages` 且 [TTS.enginePackageOrder] 为 null 时使用）。
 * 靠前的优先；某一引擎初始化并设置语言成功后即停止。
 * 若列表中的引擎均不可用且 [TTS.fallbackToSystemDefaultEngine] 为 true，会再选用系统设置的「默认朗读引擎」
 * （即 [TextToSpeech] 构造函数第三参传 `null`）。
 */
val DEFAULT_TTS_ENGINE_PACKAGES: List<String> = listOf()

/**
 * TTS 语音
 * @author yujing 2022年4月24日17:31:12
 */
/*
用法：

//播放语音
TTS.speak("你是张三吗？")
//语音队列
TTS.speakQueue("是的，你是谁？")


//速度
TTS.speechRate=1.1F
//音调
TTS.pitch=1.1F
//任意位置可以设置过滤器
TTS.filter={ it.replace("张三", "李四") }

//退出时关闭，释放资源
TTS.destroy()

//自定义引擎顺序（可选，二选一；init 传入 enginePackages 时优先生效）
TTS.enginePackageOrder = listOf(
    "com.iflytek.speechsuite",          // 讯飞 64 位
    "com.iflytek.speechcloud",          // 讯飞 32 位
    "com.xiaomi.mibrain.speech",        // 小米 TTS
    "org.nobody.sgtts",                 // 搜狗 TTS（成功时自动将 [TTS.speechRate] 设为 2.5）
    "com.hikvision.hikttsservice",      // 海康威视
    "com.baidu.duersdk.opensdk",        // 度秘语音
    "com.vivo.aiservice"                // vivo
)
TTS.init(context)

//或单次指定顺序
TTS.init(context, listOf(
    "com.iflytek.speechsuite",          // 讯飞 64 位
    "com.iflytek.speechcloud",          // 讯飞 32 位
    "com.xiaomi.mibrain.speech",        // 小米 TTS
    "org.nobody.sgtts",                 // 搜狗 TTS（成功时自动将 [TTS.speechRate] 设为 2.5）
    "com.hikvision.hikttsservice",      // 海康威视
    "com.baidu.duersdk.opensdk",        // 度秘语音
    "com.vivo.aiservice"                // vivo
)) { ok -> }

//列表里没有该机型自带的引擎：在工程里写明包名，或依赖「系统默认」（白名单全失败后自动尝试，可关掉）
TTS.fallbackToSystemDefaultEngine = true
TTS.init(context)
//只听系统默认（不先试白名单）：
TTS.fallbackToSystemDefaultEngine = true
TTS.enginePackageOrder = emptyList()
TTS.init(context)

必须 AndroidManifest.xml添加
<queries>
    <!-- 允许查询所有 TTS 服务 -->
    <!-- 讯飞64位 -->
    <package android:name="com.iflytek.speechsuite" />
    <!-- 讯飞32位 -->
    <package android:name="com.iflytek.speechcloud" />
    <!-- 小米TTS -->
    <package android:name="com.xiaomi.mibrain.speech" />
    <!-- 搜狗TTS -->
    <package android:name="org.nobody.sgtts" />
    <!-- 海康威视 -->
    <package android:name="com.hikvision.hikttsservice" />
    <!-- 度秘语音 -->
    <package android:name="com.baidu.duersdk.opensdk" />
    <!-- 谷歌TTS -->
    <package android:name="com.google.android.tts" />
    <!-- vivo -->
    <package android:name="com.vivo.aiservice" />
</queries>
 */
object TTS {
    private const val TAG = "TTS"

    @Volatile
    var initState: Int = -1 //初始化状态,-1未初始化，0完成，1语言包丢失，2语音不支持
        private set
    var textToSpeech: TextToSpeech? = null
        private set
    var speechRate = 1.0f //速度
    var pitch = 1.0f //音调
    var filter: ((String) -> String?)? = null
    var SHOW_LOG = false //是否显示log
    var history = mutableListOf<String>()//历史记录，倒序，最多1000条

    /**
     * 自定义引擎包名顺序。
     * [init] 的 `enginePackages` 非 null 时仅用传入列表；否则用本属性；本属性仍为 null 时用 [DEFAULT_TTS_ENGINE_PACKAGES]。
     */
    @Volatile
    var enginePackageOrder: List<String>? = null

    /**
     * 白名单中引擎全部失败（或未安装）时，是否最后用系统设置的默认 TTS（[TextToSpeech] 绑定 `engine = null`）。
     */
    @Volatile
    var fallbackToSystemDefaultEngine: Boolean = true

    /**
     * 初始化：按引擎列表依次尝试，直到某一引擎成功且中文可用为止。
     *
     * @param context 上下文
     * @param enginePackages 引擎包名顺序，null 则使用 [enginePackageOrder]，再默认为 [DEFAULT_TTS_ENGINE_PACKAGES]
     * @param initListener 初始化监听
     */
    @JvmOverloads
    @Synchronized
    @JvmStatic
    fun init(
        context: Context?,
        enginePackages: List<String>? = null,
        initListener: ((Boolean) -> Unit)? = null,
    ) {
        if (context == null || initState == 0 || textToSpeech != null) return
        val appContext = context.applicationContext
        val packages = (
                enginePackages
                    ?: enginePackageOrder
                    ?: DEFAULT_TTS_ENGINE_PACKAGES
                ).map { it.trim() }.filter { it.isNotEmpty() }
        tryInitEngine(appContext, packages, 0, initListener)
    }

    /** @see init */
    @JvmStatic
    fun init(context: Context?, initListener: ((Boolean) -> Unit)?) {
        init(context, enginePackages = null, initListener = initListener)
    }

    private fun initAllEnginesFailed(initListener: ((Boolean) -> Unit)?) {
        if (SHOW_LOG) "TTS初始化失败，已尝试全部候选引擎".logE(TAG)
        initState = 3
        initListener?.invoke(false)
    }

    /**
     * @param enginePkg 若为 null，使用系统设置的默认朗读引擎。
     */
    private fun bindOneEngine(
        context: Context,
        enginePkg: String?,
        initListener: ((Boolean) -> Unit)?,
        onFailTryNext: () -> Unit,
    ) {
        val logTag = enginePkg ?: "(系统默认引擎)"
        textToSpeech = TextToSpeech(context, { status: Int ->
            val tts = textToSpeech
            if (tts == null) {
                initState = -1
                initListener?.invoke(false)
                return@TextToSpeech
            }
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.CHINA)
                initState = when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        if (SHOW_LOG) "TTS初始化失败[$logTag]，语言包丢失".logE(TAG)
                        1
                    }

                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        if (SHOW_LOG) "TTS初始化失败[$logTag]，语音不支持".logE(TAG)
                        2
                    }

                    else -> {
                        if (SHOW_LOG) "TTS初始化成功: $logTag".logI(TAG)
                        if (enginePkg == SG_TTS_PACKAGE) {
                            speechRate = 2.5f
                        }
                        0
                    }
                }
                if (initState == 0) {
                    initListener?.invoke(true)
                } else {
                    tts.shutdown()
                    textToSpeech = null
                    initState = -1
                    onFailTryNext()
                }
            } else {
                if (SHOW_LOG) "TTS初始化失败[$logTag]:$status".logE(TAG)
                tts.shutdown()
                textToSpeech = null
                initState = -1
                onFailTryNext()
            }
        }, enginePkg)
    }

    private fun tryInitEngine(
        context: Context,
        packages: List<String>,
        index: Int,
        initListener: ((Boolean) -> Unit)?,
    ) {
        if (index < packages.size) {
            bindOneEngine(context, packages[index], initListener) {
                tryInitEngine(context, packages, index + 1, initListener)
            }
            return
        }
        if (fallbackToSystemDefaultEngine) {
            bindOneEngine(context, null, initListener) {
                initAllEnginesFailed(initListener)
            }
        } else {
            initAllEnginesFailed(initListener)
        }
    }

    /**
     * 播放语音并显示Toast
     */
    @JvmStatic
    fun speakToast(str: String?) {
        speak(str)
        str?.toast()
    }

    /**
     * 语音播放
     *
     * @param str 语音播放文字内容
     */
    @JvmStatic
    fun speak(str: String?) {
        if (initState == -1) return init(Kotlinx.app) { if (it) speak(str) }
        if (initState != 0 || str.isNullOrEmpty() || textToSpeech == null) return
        val speak: String? = if (filter != null) filter?.invoke(str) else str
        if (speak.isNullOrEmpty()) return
        textToSpeech?.setSpeechRate(speechRate) //速度
        textToSpeech?.setPitch(pitch) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_FLUSH, null)
        }
        if (SHOW_LOG) speak.logI(TAG)
        history.add(0, speak)
        if (history.size > 1000) history.removeAt(history.size - 1)
    }

    /**
     * 播放语音并显示Toast
     */
    @JvmStatic
    fun speakQueueToast(str: String?) {
        speakQueue(str)
        str?.toast()
    }


    /**
     * 循环播放语音，直到下一条，或者loopClose()
     */
    @Synchronized
    @JvmStatic
    fun loopSpeak(str: String?, intervalTime: Long) {
        loop(intervalTime) { speak(str) }
    }

    /**
     * 循环播放语音，直到下一条，或者loopClose()
     */
    @Synchronized
    @JvmStatic
    fun loopSpeakQueue(str: String?, intervalTime: Long) {
        loop(intervalTime) { speakQueue(str) }
    }

    private var job: Job? = null

    @Synchronized
    @JvmStatic
    fun loop(intervalTime: Long, listener: () -> Unit) {
        job?.cancel()
        job = io {
            while (this.isActive) {
                try {
                    listener.invoke()
                    delay(intervalTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }
            }
        }
    }

    @JvmStatic
    fun loopClose() {
        job?.cancel()
    }

    /**
     * 语音队列播放
     *
     * @param speak 语音播放文字内容
     */
    @JvmStatic
    fun speakQueue(str: String?) {
        if (initState == -1) return init(Kotlinx.app) { if (it) speakQueue(str) }
        if (initState != 0 || str.isNullOrEmpty() || textToSpeech == null) return
        val speak: String? = if (filter != null) filter?.invoke(str) else str
        if (speak.isNullOrEmpty()) return
        textToSpeech?.setSpeechRate(speechRate) //速度
        textToSpeech?.setPitch(pitch) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_ADD, null, null)
        } else {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_ADD, null)
        }
        if (SHOW_LOG) speak.logI(TAG)
        history.add(0, speak)
        if (history.size > 1000) history.removeAt(history.size - 1)
    }

    /**
     * 停止,TTS都被打断，包含队列
     */
    @JvmStatic
    fun onStop() {
        textToSpeech?.let { if (it.isSpeaking) it.stop() }
    }

    /**
     * 关闭，释放资源
     */
    @JvmStatic
    fun destroy() {
        textToSpeech?.shutdown() // 关闭，释放资源
        textToSpeech = null
        initState = -1
    }
}