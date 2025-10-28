package com.meu.stock.contracts



import kotlinx.coroutines.flow.Flow// Um enum para representar o status do usuário que viria do seu banco de dados (ex: Firestore)
enum class UserStatus {
    ACTIVE,
    NOT_FOUND
}

interface IAuthRepository {
    /**
     * Retorna o ID do usuário atualmente logado.
     */
    val currentUserId: String?

    /**
     * Um fluxo que emite o estado de autenticação atual do usuário.
     * True se o usuário estiver logado, false caso contrário.
     */
    val isUserLoggedIn: Flow<Boolean>

    /**
     * Realiza o login de um usuário com email e senha.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Realiza o logout do usuário atual.
     */
    suspend fun logout()

    /**
     * Verifica o status do usuário (ativo ou inativo) no banco de dados.
     */
    suspend fun checkUserStatus(): UserStatus

    /**
     * Tenta registrar um novo usuário com e-mail e senha.
     * @return Um Result que encapsula o sucesso (Unit) ou a falha (Exception).
     */
    suspend fun register(email: String, password: String): Result<Unit>

}
