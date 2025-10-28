package com.meu.stock.views.ui.screens.products.product.listProductsByCategory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.meu.stock.views.ui.components.ProductListItem
import com.meu.stock.views.ui.components.SearchAppBar

import com.meu.stock.views.ui.routes.AppRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListProductsByCategoryScreen(
    navController: NavController,
    viewModel: ListProductsByCategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(uiState.categoryName) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = uiState.products.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(onClick = {
                                viewModel.toggleSearchVisibility()
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index = 0)
                                }
                            }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Pesquisar produtos"
                                )
                            }
                        }
                        IconButton(onClick = {
                            navController.navigate("${AppRoutes.PRODUCT_FORM}?categoryId=${uiState.categoryId}")
                        }) {
                            Icon(Icons.Default.Add, "Adicionar Categoria")
                        }
                    }
                )
                HorizontalDivider()
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.products.isEmpty() -> {
                    EmptyState(categoryName = uiState.categoryName)
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            AnimatedVisibility(
                                visible = uiState.isSearchActive,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                SearchAppBar(
                                    placeholder = "Pesquisar produtos",
                                    query = uiState.searchQuery,
                                    onQueryChange = viewModel::onSearchQueryChange,
                                    onClose = viewModel::toggleSearchVisibility
                                )
                            }
                        }
                        if (uiState.filteredProducts.isEmpty() && uiState.searchQuery.isNotBlank()) {
                            item {
                                Text(
                                    text = "Nenhum produto encontrado.",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        items(uiState.filteredProducts, key = { it.id!! }) { product ->
                            ProductListItem(
                                product = product,
                                onEditClick = {
                                    navController.navigate("${AppRoutes.PRODUCT_FORM}?productId=${product.id}")
                                },
                                onPrintClick = {
                                    navController.navigate("${AppRoutes.PRINT_SCREEN}?name=${product.name}&desc=${product.description}&price=${product.priceSale}&id=${product.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(categoryName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Text(
                text = "Nenhum produto cadastrado na categoria \"$categoryName\".",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Clique no botão '+' para adicionar o primeiro produto.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
