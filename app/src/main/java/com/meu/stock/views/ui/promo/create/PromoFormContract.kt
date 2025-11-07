package com.meu.stock.views.ui.promo.create


import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Contrato para a tela do formulário, seguindo o padrão que você já usa.
object PromoFormContract {
    data class State(
        val promoId: Long? = null,
        val imageUri: Uri? = null,
        val title: String = "",
        val description: String = "",
        val price: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Event {
        data class OnImageUriChanged(val uri: Uri?) : Event
        data class OnTitleChanged(val title: String) : Event
        data class OnDescriptionChanged(val description: String) : Event
        data class OnPriceChanged(val price: String) : Event
        object OnSaveClicked : Event
    }
    //para eventos de única ocorrência
    sealed interface Effect {
        data class ShowToast(val message: String) : Effect
        object NavigateBack : Effect
    }//
}

