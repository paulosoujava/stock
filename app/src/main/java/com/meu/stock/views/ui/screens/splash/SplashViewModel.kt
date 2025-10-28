package com.meu.stock.views.ui.screens.splash

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.contracts.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NavigationDestination {
    LOGIN_SCREEN,
    MAIN_SCREEN,
    UNKNOWN // Estado inicial antes da verificação
}


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    // Um StateFlow para que a UI possa observar o destino da navegação
    private val _navigationDestination = MutableStateFlow(NavigationDestination.UNKNOWN)
    val navigationDestination = _navigationDestination.asStateFlow()

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        viewModelScope.launch {
            // Usamos .first() para obter o estado de login atual uma única vez.
            val isUserLoggedIn = authRepository.isUserLoggedIn.first()

            _navigationDestination.value = if (isUserLoggedIn) {
                NavigationDestination.MAIN_SCREEN
            } else {
                NavigationDestination.LOGIN_SCREEN
            }
        }
    }
}