package com.meu.stock.model


data class Note(
    val id: Long? = null,
    val title: String,
    val content: String,
    val lastUpdated: String,
    val reminderDate: String?,
    val reminderTime: String?
)
