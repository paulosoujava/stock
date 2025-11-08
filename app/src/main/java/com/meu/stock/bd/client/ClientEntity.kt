package com.meu.stock.bd.client

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa a tabela 'clients' no banco de dados Room.
 * Cada instância desta classe é uma linha na tabela.
 */
@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0, // Chave primária que se auto-incrementa

    val fullName: String,
    val cpf: String,
    val phone: String,
    val email: String,
    val notes: String,
    val address: String,
)