package com.aroundly.aroundly.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImageCompressor {

    private const val TAG = "ImageCompressor"

    private const val MAX_DIMENSION = 1600

    private const val JPEG_QUALITY = 82

    suspend fun compress(context: Context, uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        val decoded = decodeScaled(context, uri) ?: return@withContext null
        val oriented = applyExifRotation(context, uri, decoded) ?: decoded
        try {
            encode(oriented)
        } finally {
            if (oriented !== decoded) oriented.recycle()
            decoded.recycle()
        }
    }

    private fun decodeScaled(context: Context, uri: Uri): Bitmap? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val longestEdge = maxOf(bounds.outWidth, bounds.outHeight)
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(longestEdge)
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }.onFailure { Log.w(TAG, "Unable to decode image", it) }.getOrNull()

    private fun sampleSizeFor(longestEdge: Int): Int {
        var sampleSize = 1
        while (longestEdge / (sampleSize * 2) >= MAX_DIMENSION) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private fun applyExifRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap? =
        runCatching {
            val orientation = context.contentResolver.openInputStream(uri)?.use { stream ->
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.postRotate(90f); matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.postRotate(270f); matrix.postScale(-1f, 1f)
                }
                else -> return null
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }.onFailure { Log.w(TAG, "Unable to read EXIF orientation", it) }.getOrNull()

    private fun encode(bitmap: Bitmap): ByteArray? = runCatching {
        ByteArrayOutputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            out.toByteArray()
        }
    }.onFailure { Log.w(TAG, "Unable to encode image", it) }.getOrNull()
}
