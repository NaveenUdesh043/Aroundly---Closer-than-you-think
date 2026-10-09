package com.aroundly.aroundly.uiLibrary.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.aroundly.aroundly.data.util.ImageCompressor
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberImagePicker(onImageLoaded: (Uri, ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val onLoaded = remember(onImageLoaded) { onImageLoaded }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            ImageCompressor.compress(context, uri)?.let { onLoaded(uri, it) }
        }
    }

    return remember(launcher) {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

@Composable
fun rememberMultiImagePicker(
    maxItems: Int = 5,
    onImagesLoaded: (uris: List<Uri>, bytes: List<ByteArray>) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val onLoaded = remember(onImagesLoaded) { onImagesLoaded }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems)
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            val compressed = coroutineScope {
                uris.map { uri -> async { ImageCompressor.compress(context, uri) } }.awaitAll()
            }
            val usable = compressed.filterNotNull()
            if (usable.isNotEmpty()) {
                onLoaded(uris.take(usable.size), usable)
            }
        }
    }

    return remember(launcher) {
        { launcher.launch(PickVisualMediaRequest()) }
    }
}
