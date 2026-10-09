package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.domain.model.MenuItem
import com.aroundly.aroundly.domain.repository.MenuRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : MenuRepository {

    private val menuCollection = firestore.collection("menuImages")

    override suspend fun getMenuImages(shopId: String): Result<List<MenuItem>> {
        return try {
            val snapshot = menuCollection
                .whereEqualTo("shopId", shopId)
                .get()
                .await()
            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
            }
            Result.success(items.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadMenuImage(shopId: String, imageBytes: ByteArray): Result<String> {
        return try {
            val ref = storage.reference.child("menuImages/$shopId/${UUID.randomUUID()}.jpg")
            ref.putBytes(imageBytes).await()
            val url = ref.downloadUrl.await().toString()
            val docRef = menuCollection.add(
                MenuItem(
                    shopId = shopId,
                    imageUrl = url,
                    createdAt = System.currentTimeMillis()
                )
            ).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMenuImage(imageId: String, shopId: String): Result<Unit> {
        return try {
            val doc = menuCollection.document(imageId).get().await()
            val item = doc.toObject(MenuItem::class.java)
            item?.imageUrl?.let { url ->
                try {
                    storage.getReferenceFromUrl(url).delete().await()
                } catch (_: Exception) {}
            }
            menuCollection.document(imageId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
