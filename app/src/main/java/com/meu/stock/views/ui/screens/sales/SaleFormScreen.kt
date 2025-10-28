package com.meu.stock.views.ui.screens.sales


import com.meu.stock.views.ui.routes.AppRoutes

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.meu.stock.model.Client
import com.meu.stock.model.Product
import com.meu.stock.model.SaleItem
import com.meu.stock.views.ui.components.ClientListItem

import com.meu.stock.views.ui.utils.generateRandomPastelColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleFormScreen(
    navController: NavController,
    viewModel: SaleFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    val selectedClientResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Client>("selectedClient")

    LaunchedEffect(selectedClientResult) {
        selectedClientResult?.let { client ->
            viewModel.onClientSelected(client)
            // Limpa o resultado para não ser processado novamente se a tela for recomposta.
            navController.currentBackStackEntry?.savedStateHandle?.remove<Client>("selectedClient")
        }
    }


    // Efeito para mostrar mensagem de sucesso e navegar de volta
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Venda finalizada com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Nova Venda") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
                HorizontalDivider()
            }

        },
        floatingActionButton = {
            // Botão flutuante para finalizar a venda, só aparece se houver itens no carrinho
            AnimatedVisibility(visible = uiState.saleItems.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onFinalizeSaleClick() },
                    icon = { Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null) },
                    text = { Text("Finalizar Venda") },
                )
            }
        },

        ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SELETOR DE CLIENTE ---
            item {

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                )
                {
                    ClientSelector(
                        selectedClient = uiState.selectedClient,
                        onSelectClientClick = { navController.navigate("client_list?isSelectionMode=true") },
                    )
                    if (uiState.selectedClient == null && uiState.saleItems.isNotEmpty()) {
                        Text(
                            "Selecione um cliente clicando aqui",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    Text(
                        "Buscar por código, nome ou categoria",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("digite...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Pesquisar"
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        singleLine = true
                    )
                }
            }
            // --- LISTA DE RESULTADOS DA PESQUISA ---
            if (uiState.searchResults.isEmpty() && uiState.searchQuery.isNotBlank()) {
                item {
                    Text(
                        text = "Nenhum produto encontrado.",
                        modifier = Modifier.padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(uiState.searchResults, key = { it.id!! }) { product ->
                    // Encontra a quantidade atual do produto no carrinho
                    val quantityInCart =
                        uiState.saleItems.find { it.product.id == product.id }?.quantity ?: 0

                    ProductSearchResultItem(
                        product = product,
                        quantityInCart = quantityInCart,
                        onQuantityChange = { newQuantity ->
                            product.id?.let {
                                viewModel.updateSaleItemQuantity(it, newQuantity)
                            }
                        }
                    )
                }
            }

            // --- ITENS DA VENDA (CARRINHO) ---
            if (uiState.saleItems.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            "Itens da Venda",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                uiState.saleItems.forEach { saleItem ->
                                    SaleItemRow(
                                        saleItem = saleItem,
                                        onQuantityChange = { newQuantity ->
                                            saleItem.product.id?.let {
                                                viewModel.updateSaleItemQuantity(it, newQuantity)
                                            }
                                        }
                                    )
                                    HorizontalDivider(
                                        Modifier.padding(vertical = 8.dp),
                                        DividerDefaults.Thickness,
                                        DividerDefaults.color
                                    )
                                }
                                // --- TOTAL DA VENDA ---
                                Text(
                                    text = "Total: R$ ${"%.2f".format(uiState.totalAmount)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Espaço extra para não ser coberto pelo FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}


@Preview
@Composable
private fun Show() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProductSearchResultItem(
            product = Product(
                id = 1,
                name = "Nome do Produto",
                description = "Descrição do Produto",
                stockQuantity = 2,
                lowStockQuantity = 2,
                categoryId = 1,
                priceSale = 10.0,
                priceUnit = 11.0,
                categoryName = "teste"
            ),
            quantityInCart = 0,
            onQuantityChange = { }
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSearchResultItem(
    product: Product,
    quantityInCart: Int,
    onQuantityChange: (Int) -> Unit
) {
    val isLowStock = product.stockQuantity < product.lowStockQuantity
    val isTheSame = product.stockQuantity == product.lowStockQuantity



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (!isLowStock) generateRandomPastelColor() else Color.Red
        ),
        onClick = {
            if (!isLowStock) onQuantityChange(1)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    if (product.description.isNotBlank()) {
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }


                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = "R$ ${"%.2f".format(product.priceSale)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 19.sp
                            )
                        )
                    }
                }

                // Controle de quantidade / botão
                if (quantityInCart == 0) {
                    IconButton(
                        onClick = { onQuantityChange(1) },
                        enabled = !isLowStock
                    ) {
                        Icon(
                            Icons.Default.AddShoppingCart,
                            contentDescription = "Adicionar ao Carrinho",
                            tint = if (isLowStock)
                                Color.LightGray
                            else
                                Color.Black
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        tonalElevation = 6.dp,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            IconButton(
                                onClick = { onQuantityChange(quantityInCart - 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Diminuir",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = quantityInCart.toString(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                            IconButton(
                                onClick = { onQuantityChange(quantityInCart + 1) },
                                enabled = quantityInCart < product.stockQuantity,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Aumentar",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Rodapé: alerta de estoque
            if(isTheSame){
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Cyan)
                        .padding(6.dp)
                ) {
                    Text("\uD83D\uDE31")
                    Text(
                        text ="Cuidado o estoque  esta baixo: restam ${product.stockQuantity}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Text("\uD83D\uDE31")
                }
            }
            if (isLowStock) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red)
                        .padding(6.dp)
                ) {
                    Text("⚠️")
                    Text(
                        text = if (isLowStock)
                            "Produto esgotado!"
                        else
                            "Estoque baixo: restam ${product.stockQuantity}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text("⚠️")
                }
            }
        }
    }
}


/**
 * Composable para exibir um item que já está na venda (no carrinho).
 */
@Composable
private fun SaleItemRow(
    saleItem: SaleItem,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(saleItem.product.name, modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onQuantityChange(saleItem.quantity - 1) },
                enabled = saleItem.quantity > 0
            ) {
                Icon(Icons.Default.Remove, "Diminuir quantidade")
            }
            Text(saleItem.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
            // ✅ CORREÇÃO APLICADA AQUI
            IconButton(
                onClick = { onQuantityChange(saleItem.quantity + 1) },
                // Habilita o botão somente se a quantidade no carrinho for MENOR que o estoque disponível.
                enabled = saleItem.quantity < saleItem.product.stockQuantity
            ) {
                Icon(Icons.Default.Add, "Aumentar quantidade")
            }
        }
    }
}


/**
 * Composable customizado para o campo de seleção de cliente.
 */
@Composable
private fun ClientSelector(
    selectedClient: Client?,
    onSelectClientClick: () -> Unit,
) {
    Column {
        Text(
            "Cliente",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        if (selectedClient != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = generateRandomPastelColor()
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .background(Color.White)
                )
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Coluna para as informações de texto
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Nome do Cliente
                            Text(
                                text = selectedClient.name.uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            HorizontalDivider(modifier = Modifier.padding(end = 16.dp))

                            // Detalhes do cliente (CPF, Telefone, etc.)
                            // Usando o mesmo InfoRow, mas com um texto de rótulo para clareza
                            InfoRow(icon = Icons.Default.Person, text = selectedClient.cpf)
                            InfoRow(icon = Icons.Default.Phone, text = selectedClient.phone)
                            InfoRow(icon = Icons.Default.Email, text = selectedClient.email)

                            if (selectedClient.notes.isNotBlank()) {
                                InfoRow(icon = Icons.Default.Notes, text = selectedClient.notes)
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = generateRandomPastelColor()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        TextButton(onClick = onSelectClientClick) {
                            Text(
                                "Trocar Cliente",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.displaySmall
                            )
                        }

                    }
                }
            }

        } else {
            // Mostra os botões de ação se nenhum cliente foi selecionado (código inalterado)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onSelectClientClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.PersonSearch,
                        contentDescription = null,
                        Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Selecione o cliente clicando aqui...")
                }
            }
        }
    }
}

/**
 * Composable auxiliar para criar uma linha de informação com ícone e texto.
 */
@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}