package com.example.velora.data.auth

import com.example.velora.domain.auth.AuthRepository
import com.example.velora.domain.auth.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val authState = callbackFlow<AuthState> {
        trySend(AuthState.Loading)

        val listener = FirebaseAuth.AuthStateListener { a ->
            val u = a.currentUser
            if (u == null) trySend(AuthState.SignedOut)
            else trySend(AuthState.SignedIn(u.uid, u.email))
        }

        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun loginWithGoogleIdToken(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
    }
}