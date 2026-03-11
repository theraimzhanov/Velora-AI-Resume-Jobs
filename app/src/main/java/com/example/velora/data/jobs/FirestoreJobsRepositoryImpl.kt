package com.example.velora.data.jobs

import com.example.velora.domain.jobs.JobApplication
import com.example.velora.domain.jobs.JobsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreJobsRepositoryImpl(
    private val db: FirebaseFirestore
) : JobsRepository {

    private fun col(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("applications")

    override fun observe(uid: String): Flow<List<JobApplication>> = callbackFlow {
        val reg = col(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val list = snap?.documents.orEmpty()
                    .mapNotNull { d ->
                        d.toObject(JobApplication::class.java)?.copy(id = d.id)
                    }

                trySend(list).isSuccess
            }

        awaitClose { reg.remove() }
    }

    override suspend fun add(uid: String, company: String, position: String, status: String) {
        val now = System.currentTimeMillis()

        col(uid).add(
            mapOf(
                "company" to company.trim(),
                "position" to position.trim(),
                "status" to status,
                "createdAt" to now,
                "updatedAt" to now
            )
        ).await()
    }

    override suspend fun setStatus(uid: String, id: String, status: String) {
        col(uid)
            .document(id)
            .update(
                mapOf(
                    "status" to status,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    override suspend fun delete(uid: String, id: String) {
        col(uid).document(id).delete().await()
    }
}