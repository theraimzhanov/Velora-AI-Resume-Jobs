package com.velora.mobile.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.velora.mobile.domain.auth.AuthRepository
import com.velora.mobile.domain.auth.AuthState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val authState: Flow<AuthState> = callbackFlow {
        trySend(AuthState.Loading)

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                trySend(AuthState.SignedOut)
            } else {
                trySend(AuthState.SignedIn(user.uid, user.email))
            }
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

    override suspend fun linkGoogleIdToken(idToken: String) {
        val currentUser = auth.currentUser ?: error("No signed-in user")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        currentUser.linkWithCredential(credential).await()
    }

    override suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }
}