package com.meu.stock.repositories

import androidx.core.util.remove
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meu.stock.contracts.IAuthRepository
import com.meu.stock.contracts.UserStatus
import kotlin.io.path.exists



import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IAuthRepository {

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override val isUserLoggedIn: Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    /**
     * REFATORADO: Agora verifica o status apenas com base no Firebase Authentication.
     * Se o usuário estiver logado no Firebase, o status é considerado ATIVO.
     * Caso contrário, é NÃO ENCONTRADO.
     */
    override suspend fun checkUserStatus(): UserStatus {
        // Usamos .first() para obter o estado de login atual uma única vez.
        val isUserCurrentlyLoggedIn = isUserLoggedIn.first()

        return if (isUserCurrentlyLoggedIn) {
            UserStatus.ACTIVE
        } else {
            UserStatus.NOT_FOUND
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
