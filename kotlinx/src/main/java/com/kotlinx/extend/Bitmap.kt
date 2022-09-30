package com.kotlinx.extend

import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream


/**
 * 将bitmap转换成ByteArray
 */
/*举例： bitmap.toByteArray() */
fun Bitmap.toByteArray(): ByteArray {
    val bos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}

/**
 * 图片压缩返回byte[]，不一定绝对小于对应大小
 *
 * @param Kb    大小kb
 * @return byte[]
 */
fun Bitmap.compressToBytes(Kb: Int): ByteArray? {
    val bos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, bos) // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
    Log.d("图片压缩", "图片原始大小:" + bos.toByteArray().size / 1024.0 + "KB")
    if (bos.toByteArray().size < 1024 * Kb) return bos.toByteArray()
    //开始质量减少一点点，体积会减少很多，后面减少影响不大。质量低于10，就可能压缩成黑白照片。
    val qualityList = intArrayOf(100, 95, 88, 80, 70, 58, 44, 28, 10)
    var i = 0
    while (i < qualityList.size && bos.toByteArray().size > 1024 * Kb) {
        val quality = qualityList[i]
        bos.reset()
        this.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        Log.d("图片压缩", "图片压缩后大小:" + bos.toByteArray().size / 1024.0 + "KB  质量:" + quality)
        i++
    }
    return bos.toByteArray()
}