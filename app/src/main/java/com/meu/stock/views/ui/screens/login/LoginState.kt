package com.meu.stock.views.ui.screens.login

/**
 * Representa os possíveis estados da UI da tela de login.
 *
 * @param email O valor atual do campo de e-mail.
 * @param password O valor atual do campo de senha.
 * @param isLoading Indica se uma operação de login está em andamento.
 * @param errorMessage Uma mensagem de erro a ser exibida, se houver.
 * @param loginSuccess Indica se o login foi bem-sucedido.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)