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

    /** Longest edge, in pixels, of an uploaded image. */
    private const val MAX_DIMENSION = 1600

    /** JPEG quality for the re-encoded upload. */
    private const val JPEG_QUALITY = 82

    /**
     * Reads [uri] and returns downscaled, EXIF-corrected, JPEG-compressed bytes.
     *
     * Runs entirely on [Dispatchers.IO]; decoding and re-encoding a
     * multi-megabyte camera photo on the main thread froze the UI for as long
     * as the read took, and a five-image selection did it five times over.
     *
     * The result is always JPEG, matching the `.jpg` storage path used by the
     * upload repositories, so the served Content-Type cannot disagree with the
     * bytes. Returns null only when the URI cannot be read at all.
     */
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

    /**
     * Two-pass decode: read the bounds to pick an [BitmapFactory.Options.inSampleSize]
     * power of two, then decode straight into a bitmap of roughly the target size.
     * This avoids ever materialising the full-resolution bitmap.
     *
     * RGB_565 halves peak bitmap memory; at a 1600px cap the result is a few
     * megabytes instead of tens, which is what kept a five-image pick from
     * risking an OutOfMemoryError.
     */
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

    /**
     * Camera photos carry their orientation in EXIF rather than in the pixel
     * data, so without this they display sideways and the user re-picks the
     * photo, multiplying both storage and upload bandwidth.
     */
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
