package com.kotlinx.extend

import android.graphics.*
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

/**
 * 放大缩小图片
 */
fun Bitmap.zoom(w: Int, h: Int): Bitmap {
    val matrix = Matrix()
    val scaleWidth = w.toFloat() / this.width
    val scaleHeight = h.toFloat() / this.height
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * 获得圆角图片的方法
 */
fun Bitmap.round(roundPx: Float): Bitmap {
    val output = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = 0x00000000
    val paint = Paint()
    val rect = Rect(0, 0, this.width, this.height)
    val rectF = RectF(rect)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    return output
}