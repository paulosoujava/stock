package com.meu.stock.views.ui.promo.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.model.Promo
import com.meu.stock.usecases.DeletePromoUseCase
import com.meu.stock.usecases.GetAllPromosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromoListViewModel @Inject constructor(
    private val getAllPromosUseCase: GetAllPromosUseCase,
    private val deletePromoUseCase: DeletePromoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PromoListContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PromoListContract.Effect>()
    val effect = _effect.asSharedFlow()

    init {
        sendEvent(PromoListContract.Event.OnViewCreated)
    }

    fun sendEvent(event: PromoListContract.Event) {
        when (event) {
            is PromoListContract.Event.OnAddPromoClicked ->   navigateToCreate()
            is PromoListContract.Event.OnEditPromoClicked -> onEditPromoClicked(event.promoId)
            is PromoListContract.Event.OnViewCreated -> loadPromos()
            is PromoListContract.Event.OnDeletePromoClicked -> _state.update { it.copy(promoToDelete = event.promo) }
            is PromoListContract.Event.OnDismissDeleteDialog -> _state.update { it.copy(promoToDelete = null) }
            is PromoListContract.Event.OnDeletePromoConfirmed -> deletePromo()
            is PromoListContract.Event.OnSendPromoClicked -> sendPromo(event.promo)
        }
    }

    private  fun navigateToCreate() {
        viewModelScope.launch {
            _effect.emit(
                PromoListContract.Effect.NavigateToPromoForm(promoId = null)
            )
        }
    }
    private fun onEditPromoClicked(promoId: Long) {
        viewModelScope.launch {
            _effect.emit(
                PromoListContract.Effect.NavigateToPromoForm(promoId = promoId )
            )
        }
    }

    private fun loadPromos() {
        viewModelScope.launch {
            getAllPromosUseCase()
                .onStart { _state.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(PromoListContract.Effect.ShowSnackbar("Erro ao carregar promoções: ${e.message}"))
                }
                .collect { promos ->
                    _state.update { it.copy(isLoading = false, promos = promos) }
                }
        }
    }

    private fun deletePromo() {
        _state.value.promoToDelete?.let { promo ->
            viewModelScope.launch {
                try {
                    deletePromoUseCase(promo)
                    _effect.emit(PromoListContract.Effect.ShowSnackbar("Promoção '${promo.title}' deletada."))
                } catch (e: Exception) {
                    _effect.emit(PromoListContract.Effect.ShowSnackbar("Erro ao deletar: ${e.message}"))
                } finally {
                    // Esconde o diálogo de confirmação independentemente do resultado
                    _state.update { it.copy(promoToDelete = null) }
                }
            }
        }
    }

    private fun navigateToForm(promoId: Long? = null) {
        viewModelScope.launch {
            _effect.emit(PromoListContract.Effect.NavigateToPromoForm(promoId))
        }
    }

    private fun sendPromo(promo: Promo) {
        viewModelScope.launch {
            _effect.emit(PromoListContract.Effect.SendWhatsAppMessage(promo))
        }
    }
}