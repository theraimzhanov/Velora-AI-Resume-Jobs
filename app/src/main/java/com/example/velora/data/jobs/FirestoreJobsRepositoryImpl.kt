package com.example.velora.data.jobs

import com.example.velora.domain.jobs.JobApplication
import com.example.velora.domain.jobs.JobsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreJobsRepositoryImpl(
    private val db: FirebaseFirestore
) : JobsRepository {

    private fun col(uid: String) =
        db.collection("users").document(uid).collection("applications")

    override fun observe(uid: String) = callbackFlow<List<JobApplication>> {
        val reg = col(uid)
            .orderBy("createdAt")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents.orEmpty()
                    .map { d -> (d.toObject(JobApplication::class.java) ?: JobApplication()).copy(id = d.id) }
                    .asReversed()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun add(uid: String, company: String, position: String, status: String) {
        col(uid).add(
            mapOf(
                "company" to company.trim(),
                "position" to position.trim(),
                "status" to status,
                "createdAt" to System.currentTimeMillis()
            )
        ).await()
    }

    override suspend fun setStatus(uid: String, id: String, status: String) {
        col(uid).document(id).update("status", status).await()
    }

    override suspend fun delete(uid: String, id: String) {
        col(uid).document(id).delete().await()
    }

}