package com.meu.stock.views.ui.screens.rergister


/**
 * Representa o estado da UI para a tela de cadastro.
 *
 * @property email O e-mail inserido pelo usuário.
 * @property password A senha inserida pelo usuário.
 * @property confirmPassword A confirmação da senha inserida.
 * @property isLoading Indica se uma operação de carregamento está em andamento.
 * @property errorMessage Contém mensagens de erro a serem exibidas na UI.
 */
data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
