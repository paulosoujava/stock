package com.meu.stock.views.ui.screens.live.list


import com.meu.stock.model.LiveSession

data class ListLiveState(
    val isLoading: Boolean = false,
    val liveSessions: List<LiveSession> = emptyList()
)