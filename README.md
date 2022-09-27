# kotlinx #

## kotlin扩展方法

几乎全采用kotlin扩展方法的方式来扩展kotlin功能。增加易用性。

[![platform](https://img.shields.io/badge/platform-Android-lightgrey.svg)](https://developer.android.google.cn/studio/index.html)
![Gradle](https://img.shields.io/badge/Gradle-7.5.1-brightgreen.svg)
[![last commit](https://img.shields.io/github/last-commit/yutils/kotlinx.svg)](https://github.com/yutils/kotlinx/commits/master)
![repo size](https://img.shields.io/github/repo-size/yutils/kotlinx.svg)
![android studio](https://img.shields.io/badge/android%20studio-2021.3.1-green.svg)
[![maven](https://img.shields.io/badge/maven-address-green.svg)](https://search.maven.org/artifact/com.kotlinx/kotlinx)

## 已经从jitpack.io仓库移动至maven中央仓库

**[releases里面有AAR包。点击前往](https://github.com/yutils/kotlinx/releases)**

## Gradle 引用

[添加依赖，当前最新版：————> 1.0.1　　　　![最新版](https://img.shields.io/badge/%E6%9C%80%E6%96%B0%E7%89%88-1.0.1-green.svg)](https://search.maven.org/artifact/com.kotlinx/kotlinx)

```
dependencies {
     //更新地址  https://github.com/yutils/kotlinx 建议过几天访问看下有没有新版本
     implementation 'com.kotlinx:kotlinx:1.0.1'
}
```

注：如果引用失败，看下面方案
```
allprojects {
    repositories {
     //如果拉取不了
     maven { url 'http://maven.kotlinx.com:8081/repository/maven-public'; allowInsecureProtocol = true }
    }
```

Github地址：[https://github.com/yutils/kotlinx](https://github.com/yutils/kotlinx)

我的CSDN：[https://blog.csdn.net/Yu1441](https://blog.csdn.net/Yu1441)

感谢关注微博：[细雨若静](https://weibo.com/32005200)


### 举例：
```kotlin
//给textView增加跑马灯效果
tv.marquee()

//将bitmap转化成byte数组
bitmap.toByteArray()

//recyclerView使用
val list = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
recyclerView.show(R.layout.user_item, list) { holder, position ->
    val binding = holder.binding as UserItemBinding
    val item = list[position]
}.onItemClickListener = { position ->
    YToast.show("第$position行被点击了")
}

//滑动完成后，能看到顶部，下拉刷新
recyclerView.scrollToTopListener{
    //刷新逻辑
}

//滑动完成后能看到底部监听，上拉加载
recyclerView.scrollToBottomListener{
    //加载逻辑
}
```