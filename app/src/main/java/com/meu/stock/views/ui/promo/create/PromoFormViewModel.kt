package com.meu.stock.views.ui.promo.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.usecases.promo.GetPromoByIdUseCase
import com.meu.stock.usecases.promo.SavePromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.meu.stock.di.InternalStorageManager
import com.meu.stock.model.Promo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class PromoFormViewModel @Inject constructor(
    private val getPromoByIdUseCase: GetPromoByIdUseCase,
    private val savePromoUseCase: SavePromoUseCase,
    private val storageManager: InternalStorageManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PromoFormContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PromoFormContract.Effect>()
    val effect = _effect.asSharedFlow()


    init {
        val promoId: Long? = savedStateHandle.get<Long>("promoId")
        if (promoId != null && promoId != -1L) {
            _state.update { it.copy(isLoading = true, promoId = promoId) }
            loadPromo(promoId)
        }
    }

    fun sendEvent(event: PromoFormContract.Event) {
        when (event) {
            is PromoFormContract.Event.OnImageUriChanged -> {
                val originalUri = event.uri
                if (originalUri == null) {
                    _state.update { it.copy(imageUri = null) }
                    return
                }

                viewModelScope.launch {
                    _state.update { it.copy(isImageLoading = true) }
                    val internalUri = storageManager.copyImageToInternalStorage(originalUri)
                    _state.update { it.copy(imageUri = internalUri, isImageLoading = false) }
                }
            }
            is PromoFormContract.Event.OnTitleChanged -> {
                _state.update { it.copy(title = event.title) }
            }
            is PromoFormContract.Event.OnDescriptionChanged -> {
                _state.update { it.copy(description = event.description) }
            }
            is PromoFormContract.Event.OnSaveClicked -> {
                _state.update { it.copy(errorMessage = null) }
                savePromo()
            }
            is PromoFormContract.Event.OnPriceChanged -> {
                _state.update { it.copy(price = event.price) }
            }

        }
    }

    private fun loadPromo(id: Long) {
        viewModelScope.launch {
            val promo = getPromoByIdUseCase(id)
            if (promo != null) {
                _state.update {
                    it.copy(
                        title = promo.title,
                        description = promo.desc,
                        price = promo.price.toString(),
                        imageUri = promo.imageUri?.toUri(),
                        isLoading = false
                    )
                }
            } else {
                // Tratar o caso em que a promoção não foi encontrada, se necessário
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    private fun savePromo() {
        viewModelScope.launch {
            val currentState = _state.value

            // --- LÓGICA DE VALIDAÇÃO ---
            if (currentState.title.isBlank()) {
                _state.update { it.copy(errorMessage = "O título não pode estar em branco.") }
                _effect.emit(PromoFormContract.Effect.ShowToast("O título não pode estar em branco."))
                return@launch
            }

            if (currentState.price.isBlank() || currentState.price.toDoubleOrNull() == null) {
                _state.update { it.copy(errorMessage = "O preço é inválido.") }
                _effect.emit(PromoFormContract.Effect.ShowToast("O preço é inválido."))
                return@launch
            }
            // ---------------------------

            val promoToSave = Promo(
                id = currentState.promoId ?: 0, // Se o ID for nulo, é uma nova promo (id = 0 para autoincremento)
                title = currentState.title,
                desc = currentState.description,
                price = currentState.price.toDouble(),
                imageUri = currentState.imageUri?.toString()
            )


             if (currentState.title.isBlank()) { /* Mostrar erro */ return@launch }

            savePromoUseCase(promoToSave)

            _effect.emit(PromoFormContract.Effect.ShowToast("Promoção salva com sucesso!"))
            _effect.emit(PromoFormContract.Effect.NavigateBack)
        }
    }
}
