package com.aroundly.aroundly

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AroundlyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        enableOfflinePersistence()
    }

    private fun enableOfflinePersistence() {
        runCatching {
            val firestore = FirebaseFirestore.getInstance()
            val current = firestore.firestoreSettings
            if (current.cacheSettings !is PersistentCacheSettings) {
                val cache = PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
                firestore.firestoreSettings = FirebaseFirestoreSettings.Builder(current)
                    .setLocalCacheSettings(cache)
                    .build()
            }
        }
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .respectCacheHeaders(false)
        .crossfade(true)
        .build()
}
