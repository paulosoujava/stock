package com.meu.stock.views.ui.screens.login

sealed class LoginEvent {
    data object NavigateToMain : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}
