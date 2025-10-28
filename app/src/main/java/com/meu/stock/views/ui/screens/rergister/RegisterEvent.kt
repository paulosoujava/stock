package com.meu.stock.views.ui.screens.rergister

/**
 * Define os eventos que podem ser enviados da ViewModel para a UI na tela de cadastro.
 * Estes são eventos únicos que acontecem uma vez, como navegação ou exibição de diálogos.
 */
sealed class RegisterEvent {
    /**
     * Evento para navegar para a tela principal após o cadastro bem-sucedido.
     */
    data object NavigateToMain : RegisterEvent()

    /**
     * Evento para exibir o pop-up de ajuda.
     */
    data object ShowHelpDialog : RegisterEvent()
}
