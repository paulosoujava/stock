package com.meu.stock.views.ui.screens.live.create

data class AddLiveState(
    val title: String = "",
    val startDate: String = "",
    val startTime: String = "",
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)
