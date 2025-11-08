package com.meu.stock.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Client(
    var id: Long,
    val name: String,
    val phone: String,
    val cpf: String,
    val email: String,
    val notes: String,
    val address: String
): Parcelable
