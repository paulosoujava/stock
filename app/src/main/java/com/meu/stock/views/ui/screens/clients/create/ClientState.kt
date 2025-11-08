package com.meu.stock.views.ui.screens.clients.create

/**
 * Represents the UI state for the Client screen.
 *
 * @param fullName The client's full name.
 * @param cpf The client's CPF document number.
 * @param phone The client's phone number.
 * @param email The client's email address.
 * @param address A address the client lives at.
 * @param notes A free text field for additional notes.
 * @param isLoading Indicates if a save operation is in progress.
 * @param saveSuccess Indicates if the client was saved successfully.
 * @param errorMessage An error message to be displayed, if any.
 */
data class ClientState(
    val id: String = "",
    val fullName: String = "",
    val cpf: String = "",
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val address: String = "",
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)
