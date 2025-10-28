package com.meu.stock.views.ui.screens.sales

import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.add
import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.contracts.IProductRepository
import com.meu.stock.contracts.ISaleHistoryRepository
import com.meu.stock.model.Client
import com.meu.stock.model.Product // Certifique-se de importar seus modelos
import com.meu.stock.model.SaleItem
import com.meu.stock.repositories.ProductRepositoryImpl
import com.meu.stock.repositories.SaleHistoryRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.find
import kotlin.collections.remove
import kotlin.collections.sumOf
import kotlin.collections.toMutableList

@HiltViewModel
class SaleFormViewModel @Inject constructor(
    private val productRepository: IProductRepository,
    private val saleHistoryRepository: ISaleHistoryRepository
) : ViewModel() {

    // Substituímos SaleFormState pelo novo SaleFormUiState
    private val _uiState = MutableStateFlow(SaleFormState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    // --- LÓGICA DE PESQUISA DE PRODUTOS ---

    /**
     * Chamado sempre que o texto no campo de pesquisa é alterado.
     * Implementa um "debounce" para evitar buscas a cada caractere digitado.
     */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Cancela a busca anterior para iniciar uma nova
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            // Aguarda 300ms antes de executar a busca para o usuário terminar de digitar
            delay(300L)
            searchProducts(query)
        }
    }

    /**
     * Busca produtos no banco de dados usando o ProductRepository.
     * @param query O texto para buscar produtos.
     */
    private suspend fun searchProducts(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        try {
            _uiState.update { it.copy(isLoading = true) }

            val results = withContext(Dispatchers.IO) {
                productRepository.searchProducts(query)
            }

            // Atualiza o estado com os resultados encontrados
            _uiState.update { it.copy(searchResults = results, isLoading = false) }

        } catch (e: Exception) {
            // Se ocorrer um erro durante a busca no banco de dados
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Erro ao buscar produtos: ${e.message}",
                    searchResults = emptyList() // Limpa os resultados em caso de erro
                )
            }
        }
    }

    // --- LÓGICA DO CARRINHO DE VENDAS ---



    /**
     * Atualiza a quantidade de um item específico na venda.
     * Se a nova quantidade for zero ou menos, o item é removido.
     * @param productId O ID do produto a ser atualizado.
     * @param newQuantity A nova quantidade do item.
     */
    fun updateSaleItemQuantity(productId: Long, newQuantity: Int) {
        // Pega a lista de itens atual.
        val currentItems = _uiState.value.saleItems

        // Encontra o índice do item que queremos alterar. Usar o índice é crucial.
        val itemIndex = currentItems.indexOfFirst { it.product.id == productId }

        // Cria uma nova lista mutável a partir da original.
        val newItems = currentItems.toMutableList()

        if (itemIndex != -1) {
            // LÓGICA 1: O item JÁ EXISTE no carrinho.
            val itemToUpdate = newItems[itemIndex]

            if (newQuantity > 0) {
                // Valida o estoque.
                if (newQuantity <= itemToUpdate.product.stockQuantity) {
                    // ✅ A MÁGICA: Cria uma NOVA instância de SaleItem com a quantidade atualizada
                    // e substitui o item antigo na lista pelo novo.
                    val updatedItem = itemToUpdate.copy(quantity = newQuantity)
                    newItems[itemIndex] = updatedItem
                }
            } else {
                // Se a nova quantidade for 0 ou menor, remove o item.
                newItems.removeAt(itemIndex)
            }
        } else if (newQuantity > 0) {
            // LÓGICA 2: O item NÃO EXISTE no carrinho, então deve ser adicionado.
            val productToAdd = _uiState.value.searchResults.find { it.id == productId }
            if (productToAdd != null) {
                // Adiciona uma nova instância de SaleItem.
                newItems.add(SaleItem(product = productToAdd, quantity = newQuantity))
            }
        }

        // Atualiza o estado da UI com a lista completamente nova e recalcula o total.
        _uiState.update { it.copy(saleItems = newItems) }
        recalculateTotal()
    }


    /**
     * Recalcula o valor total da venda com base nos itens do carrinho.
     */
    private fun recalculateTotal() {
        val total = _uiState.value.saleItems.sumOf { it.product.priceSale * it.quantity }
        _uiState.update { it.copy(totalAmount = total) }
    }


// --- LÓGICA GERAL DA VENDA ---

    /**
     * Atualiza o cliente selecionado para a venda.
     */
    fun onClientSelected(client: Client) {
        _uiState.update { it.copy(selectedClient = client) }
    }



    /**
     * Ação de finalizar a venda.
     * Valida os dados e salva no banco de dados.
     */
    fun onFinalizeSaleClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val state = _uiState.value
            if (state.saleItems.isEmpty() || state.selectedClient == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Adicione pelo menos um produto e selecione um cliente."
                    )
                }
                return@launch
            }

            try {
                // 2. Chama o repositório para salvar a venda
                saleHistoryRepository.saveSale(
                    clientId = state.selectedClient.id, // Assumindo que o ID não é nulo
                    clientName = state.selectedClient.name,
                    saleItems = state.saleItems,
                    totalAmount = state.totalAmount
                )
                // 3. Sucesso!
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Falha ao salvar a venda: ${e.message}"
                    )
                }
            }
        }
    }
}