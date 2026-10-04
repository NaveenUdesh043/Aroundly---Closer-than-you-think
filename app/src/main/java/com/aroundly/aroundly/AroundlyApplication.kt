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

    /**
     * Without this, every screen waits on full network round trips for data
     * Firestore already holds on disk, so a warm start on a poor connection
     * renders empty lists. `Application.onCreate` runs before the first
     * Firestore call, which this setting requires.
     */
    private fun enableOfflinePersistence() {
        runCatching {
            val firestore = FirebaseFirestore.getInstance()
            val current = firestore.firestoreSettings
            if (current.cacheSettings !is PersistentCacheSettings) {
                // CACHE_SIZE_UNLIMITED is a fraction of free disk space, so
                // Firestore caps it and it cannot fill the device.
                val cache = PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
                firestore.firestoreSettings = FirebaseFirestoreSettings.Builder(current)
                    .setLocalCacheSettings(cache)
                    .build()
            }
        }
    }

    /**
     * A shared image cache instead of a per-request one. Place and menu photos
     * are the same handful of URLs revisited on every scroll and tab change, so
     * re-decoding them was most of the scrolling cost.
     */
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
