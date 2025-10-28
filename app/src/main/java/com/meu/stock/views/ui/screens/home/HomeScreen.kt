package com.meu.stock.views.ui.screens.home

import LowStockWarningCard
import MonthlySummaryCard
import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.meu.stock.views.ui.components.AlertCloseMonth
import com.meu.stock.views.ui.components.DashboardCard
import com.meu.stock.views.ui.components.HomeTopBar
import com.meu.stock.views.ui.routes.AppRoutes
import com.meu.stock.views.ui.screens.login.LoginViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val clientCount by homeViewModel.clientCount.collectAsState()
    val categoryCount by homeViewModel.categoryCount.collectAsState()
    val productCount by homeViewModel.productCount.collectAsState()
    val closeMonth = remember { mutableStateOf(false) }
    val noteCount by homeViewModel.noteCount.collectAsState()

    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { homeViewModel.onDismissLogoutDialog() },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            title = { Text("Encerrar Sessão") },
            text = { Text("Você tem certeza que deseja sair?") },
            confirmButton = {
                Button(
                    onClick = {
                        homeViewModel.onConfirmLogout()
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { homeViewModel.onDismissLogoutDialog() }
                ) {
                    Text("Não")
                }
            }
        )
    }
    if (closeMonth.value) {
        AlertCloseMonth(
            onDismissRequest = {
                closeMonth.value = false
            },
            onConfirmCloseMonth = {
                closeMonth.value = false
            },
            onDismissCloseMonthDialog = {
                closeMonth.value = false
            },
            month = "Outubro",
        )
    }


    Scaffold(
        topBar = {
            Column {
                HomeTopBar(
                    onLogoutClick = { homeViewModel.onLogoutIconClick() },
                    onQrcanClick = {
                        navController.navigate(AppRoutes.SCANNER)
                    }
                )
                HorizontalDivider()
            }

        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        homeViewModel.onCardClicked(AppRoutes.SALES_YEAR_LIST, navController)
                    }
                ) {
                    Text(
                        "Historico de vendas",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Blue,
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                LowStockWarningCard(
                    products = uiState.lowStockProducts,
                    onProductClick = { productId, categoryId ->
                        //?productId={productId}&categoryId={categoryId}
                        navController.navigate("${AppRoutes.PRODUCT_FORM}?productId=${productId}&categoryId=${categoryId}")
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Clientes",
                    subtitle = "Registre seus clientes ",
                    icon = Icons.Default.Group,
                    count = clientCount,
                    onClick = { homeViewModel.onCardClicked(AppRoutes.CLIENT_LIST, navController) },
                )
            }
            item {
                DashboardCard(
                    title = "Categoria",
                    count = categoryCount,
                    subtitle = "Criar categoria de um produto",
                    icon = Icons.Default.Category,
                    onClick = {
                        homeViewModel.onCardClicked(
                            AppRoutes.CATEGORY_LIST,
                            navController
                        )
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Produtos",
                    count = productCount,
                    subtitle = "Registre os produtos da loja",
                    icon = Icons.Default.Inventory2,
                    onClick = {
                        homeViewModel.onCardClicked(
                            AppRoutes.LIST_BY_CATEGORY,
                            navController
                        )
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Vendas",
                    icon = Icons.Default.PointOfSale,
                    count = -1,
                    subtitle = "Registra de saídas de produtos",
                    onClick = { homeViewModel.onCardClicked(AppRoutes.SALE_FORM, navController) }
                )
            }
            item {
                DashboardCard(
                    title = "Os mais vendidos",
                    icon = Icons.Default.ShoppingCartCheckout,
                    count = -1,
                    subtitle = "Produtos mais vendidos",
                    onClick = {
                        homeViewModel.onCardClicked(
                            AppRoutes.BEST_SELLERS, navController
                        )
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Notes",
                    icon = Icons.AutoMirrored.Filled.Notes,
                    count = noteCount,
                    subtitle = "Crie notas para não esquecer de mais nada.",
                    onClick = {
                        homeViewModel.onCardClicked(
                            AppRoutes.NOTES, navController
                        )
                    }
                )
            }
            //TODO deixar a live para a versao 2
            /*item {
                DashboardCard(
                    title = "Live",
                    count = -1,
                    subtitle = "Registrar vendas em live",
                    onClick = { homeViewModel.onCardClicked(AppRoutes.LIVE_LIST, navController) }
                )
            }*/
            item {
                SalesSummaryCard(
                    totalSales = uiState.totalSalesFormatted,
                    period = uiState.currentPeriod,
                    salesCount = uiState.salesCount
                )
            }
            /* item {
                 HorizontalDivider()
             }
             item {
                 MonthlySummaryCard(
                     month = "Outubro",
                     revenue = 12540.75,
                     expenses = 8320.50,
                     onHistoryClick = {
                         homeViewModel.onCardClicked(AppRoutes.SALES_YEAR_LIST, navController)
                     },
                     onCloseMonth = {
                         closeMonth.value = true
                     }
                 )
             }*/
        }
    }
}

@Composable
fun SalesSummaryCard(totalSales: String, period: String, salesCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
        ) {
            // Linha do Título e Ícone
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Resumo do Mês",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    imageVector = Icons.Default.PointOfSale,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = period,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Linha do Total e da Contagem
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Coluna do Total
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Faturamento",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = totalSales,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                // Coluna da Contagem
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Nº de Vendas",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = salesCount.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}