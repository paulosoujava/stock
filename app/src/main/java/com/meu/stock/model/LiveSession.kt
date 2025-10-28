package com.meu.stock.model


import java.time.LocalDateTime

/**
 * Representa uma sessão de live de vendas.
 */
data class LiveSession(
    val id: String,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?, // Pode ser nulo se a live ainda estiver acontecendo
    val viewerCount: Int,
    val salesCount: Int
)

