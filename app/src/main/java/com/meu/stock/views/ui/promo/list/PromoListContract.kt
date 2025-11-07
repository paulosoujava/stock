package com.meu.stock.views.ui.promo.list


import com.meu.stock.model.Promo

// Usaremos 'PromoListContract' para evitar conflito com outros contratos
class PromoListContract {

    /**
     * Representa o estado da UI da tela de listagem de promoções.
     *
     * @param promos A lista de promoções a ser exibida.
     * @param isLoading Indica se a lista está sendo carregada.
     * @param promoToDelete Guarda a promoção para a qual a confirmação de exclusão foi solicitada.
     *                      Se for nulo, o diálogo de confirmação não é exibido.
     */
    data class State(
        val promos: List<Promo> = emptyList(),
        val isLoading: Boolean = true,
        val promoToDelete: Promo? = null
    )

    /**
     * Representa os eventos que podem ser disparados pela UI.
     */
    sealed class Event {
        // Eventos do ciclo de vida

        object OnViewCreated : Event()

        // Eventos de Ação do Usuário
        object OnAddPromoClicked : Event()
        data class OnDeletePromoClicked(val promo: Promo) : Event()
        object OnDeletePromoConfirmed : Event()
        object OnDismissDeleteDialog : Event()
        data class OnEditPromoClicked(val promoId: Long) : Event()
        data class OnSendPromoClicked(val promo: Promo) : Event()
    }

    /**
     * Representa os efeitos colaterais que o ViewModel pode solicitar à UI.
     * São eventos que não alteram o estado, como navegação ou exibição de Snackbars.
     */
    sealed class Effect {
        data class ShowSnackbar(val message: String) : Effect()
        data class NavigateToPromoForm(val promoId: Long? = null) : Effect()
        data class SendWhatsAppMessage(val promo: Promo) : Effect()
    }
}