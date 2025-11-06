package com.meu.stock.views.ui.screens.sellers


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.meu.stock.model.BestSellingProductInfo
import com.meu.stock.model.CustomerInfo
import com.meu.stock.views.ui.routes.AppRoutes
import com.meu.stock.views.ui.utils.generateRandomPastelColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestSellersScreen(
    navController: NavController,
    viewModel: BestSellersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Produtos mais vendidos") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    )
    { paddingValues -> // O Scaffold nos fornece o preenchimento (padding) correto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "Ocorreu um erro.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.bestSellers.isEmpty() -> {
                    Text(
                        text = "Ainda não há vendas registradas para exibir.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    BestSellersList(items = uiState.bestSellers, navController)
                }
            }
        }
    }
}

@Composable
private fun BestSellersList(items: List<BestSellingProductInfo>, navController: NavController) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(items) { index, item ->
            BestSellerItemCard(item = item, rank = index + 1, navController = navController)
        }
    }
}

@Composable
private fun BestSellerItemCard(item: BestSellingProductInfo, rank: Int, navController: NavController) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = generateRandomPastelColor()
        )
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .background(Color.White)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 39.sp,
                    modifier = Modifier.width(40.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productName.split(' ').joinToString(" ") { it.replaceFirstChar(Char::titlecase) },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),

                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector =  Icons.Default.ShoppingCartCheckout,
                                contentDescription = "vendas",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "${item.totalSalesCount} vendas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "compradores",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Blue,
                            )
                            Icon(
                                imageVector =  if(isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Ver compradores",
                                tint = Color.Blue
                            )
                        }

                    }

                }
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                CustomerList(customers = item.customers, navController = navController)
            }
        }
    }
}

@Composable
private fun CustomerList(customers: List<CustomerInfo>, navController: NavController) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
        Text(
            text = "Comprado por:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        customers.forEach { customer ->
            Row(
                modifier = Modifier
                    .clickable {
                        navController.navigate("client_list?isSelectionMode=false&searchQuery=${customer.customerName}")
                    }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "• ${customer.customerName.uppercase()} (ID: ${customer.customerId})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp)

                )
                Text(
                    text = "▶",
                    fontSize = 9.sp,
                    color = Color.Blue,
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp)
                )
            }

        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun BestSellersScreenPreview() {
    val fakeItems = listOf(
        BestSellingProductInfo(
            productId = 1L,
            productName = "cadeira gamer xpto",
            totalSalesCount = 42,
            customers = listOf(
                CustomerInfo(customerId = 101, customerName = "ana beatriz"),
                CustomerInfo(customerId = 102, customerName = "carlos eduardo")
            )
        ),
        BestSellingProductInfo(
            productId = 2L,
            productName = "Monitor UltraWide 34\"",
            totalSalesCount = 27,
            customers = listOf(
                CustomerInfo(customerId = 103, customerName = "Fernanda Lima"),
                CustomerInfo(customerId = 101, customerName = "Ana Beatriz")
            )
        ),
        BestSellingProductInfo(
            productId = 3L,
            productName = "Teclado Mecânico RGB",
            totalSalesCount = 15,
            customers = listOf(
                CustomerInfo(customerId = 104, customerName = "Ricardo Mendes")
            )
        )
    )

    // Para simular a tela inteira, envolvemos a lista em um Scaffold.
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Produtos mais vendidos") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            BestSellersList(items = fakeItems, navController = NavController(LocalContext.current))
        }
    }
}