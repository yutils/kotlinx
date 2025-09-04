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
     * 初始化
     * @param context 上下文
     * @param initListener 初始化监听
     */
    @Synchronized
    @JvmStatic
    fun init(context: Context?, initListener: ((Boolean) -> Unit)? = null) {
        if (context == null || initState == 0 || textToSpeech != null) return
        textToSpeech = TextToSpeech(context) { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.CHINA)
                initState = when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        if (SHOW_LOG) "TTS初始化失败，语言包丢失".logE(TAG)
                        1
                    }

                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        if (SHOW_LOG) "TTS初始化失败，语音不支持".logE(TAG)
                        2
                    }

                    else -> {
                        if (SHOW_LOG) "TTS初始化成功".logI(TAG)
                        0
                    }
                }
            } else {
                if (SHOW_LOG) "TTS初始化失败:$status".logE(TAG)
                initState = 3
            }
            initListener?.invoke(initState == 0)
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
        if (SHOW_LOG) "$speak".logI(TAG)
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
        if (SHOW_LOG) "$speak".logI(TAG)
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