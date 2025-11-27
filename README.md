# kotlinx

## kotlin扩展方法

几乎全采用kotlin扩展方法的方式来扩展kotlin功能。增加易用性。

# 希望更多小伙伴加入和我一起扩充

[![platform](https://img.shields.io/badge/platform-Android-lightgrey.svg)](https://developer.android.google.cn/studio/index.html)
![Gradle](https://img.shields.io/badge/Gradle-9.0.0-brightgreen.svg)
[![last commit](https://img.shields.io/github/last-commit/yutils/kotlinx.svg)](https://github.com/yutils/kotlinx/commits/master)
![repo size](https://img.shields.io/github/repo-size/yutils/kotlinx.svg)
![android studio](https://img.shields.io/badge/android%20studio-2025.1.2-green.svg)
[![maven](https://img.shields.io/badge/maven-address-green.svg)](https://search.maven.org/artifact/com.kotlinx/kotlinx)

## 已经从jitpack.io仓库移动至maven中央仓库

**[releases里面有AAR包。点击前往](https://github.com/yutils/kotlinx/releases)**

## Gradle 引用

[添加依赖，当前最新版：————> 1.1.7　　　　![最新版](https://img.shields.io/badge/%E6%9C%80%E6%96%B0%E7%89%88-1.1.7-green.svg)](https://search.maven.org/artifact/com.kotlinx/kotlinx)

```
dependencies {
     //更新地址  https://github.com/yutils/kotlinx 建议过几天访问看下有没有新版本
     implementation 'com.kotlinx:kotlinx:1.1.7'
}
```

注：如果引用失败，看下面方案

```
allprojects {
    repositories {
     //如果拉取不了，再加入
     maven { url 'http://maven.kotlinx.com:8081/repository/maven-public'; allowInsecureProtocol = true }
    }
```

Github地址：[https://github.com/yutils/kotlinx](https://github.com/yutils/kotlinx)

我的CSDN：[https://blog.csdn.net/Yu1441](https://blog.csdn.net/Yu1441)

感谢关注微博：[细雨若静](https://weibo.com/32005200)

### 引入

```kotlin
//在application或者MainActivity中加入
Kotlinx.init(application)
```

### 举例：

```kotlin

//在ui线程弹出一个toast
"你好".toast()

//调用TTS语音
"你好".speak()

//打印日志
"你好".logI()

//toast，tts，日志，显示行号
"错误".toast().speak().logE().showStackTrace()

//判断字符串是否是int
"123".isInt()

//判断是否是IPv4地址
"192.168.1.1".isIPv4()

//将字符串写入文件
"你好".toFile(File("D:/abc.txt"))

//将字符串转换成base64
"你好".toBase64String()

//将base64字符串转换成String
"5L2g5aW9".toStringFromBase64()

//读取文件并返回String
var s = File("D:/abc.txt").toString()

//将bitmap转换成ByteArray
bitmap.toByteArray()

//给textView增加跑马灯效果
textView.marquee()

//将bitmap转化成byte数组
bitmap.toByteArray()

//recyclerView使用
val list = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
recyclerView.show(R.layout.user_item, list) { holder, position ->
    val binding = holder.binding as UserItemBinding
    val item = list[position]
}.onItemClickListener = { position ->
    "第${position}行被点击了".toast()
}

//滑动完成后，能看到顶部，下拉刷新
recyclerView.scrollToTopListener {
    //刷新逻辑
}

//滑动完成后能看到底部监听，上拉加载
recyclerView.scrollToBottomListener {
    //加载逻辑
}
//点击防抖
view.debounceClick {
    
}

```