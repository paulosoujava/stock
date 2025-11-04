package com.meu.stock.views.ui.screens.products.product.listCategories


import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.meu.stock.model.Category
import com.meu.stock.ui.theme.StockTheme
import com.meu.stock.views.ui.components.CountBadge
import com.meu.stock.views.ui.components.DefaultTabBar
import com.meu.stock.views.ui.routes.AppRoutes
import com.meu.stock.views.ui.utils.generateRandomPastelColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCategoriesScreen(
    navController: NavController,
    viewModel: ListCategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DefaultTabBar(
                title = "Categorias",
                navController = navController
            )
        },

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.categories.isEmpty()) {
                EmptyStateWarning(
                    onNavigateToCreate = {
                        navController.navigate(AppRoutes.CATEGORY_FORM)
                    }
                )
            } else {
                // 3. Mostra a lista de categorias
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.categories, key = { it.id!! }) { category ->
                        CategoryListItem(
                            category = category,
                            onClick = {
                                val encodedCategoryName = Uri.encode(category.name)
                                val route = "${AppRoutes.PRODUCT_LIST}/${category.id}?name=$encodedCategoryName"
                                navController.navigate(route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Show() {
    StockTheme {
        CategoryListItem(
            category = Category(
                id = "",
                name = "Frutas",
                description = "Frutas",
                productCount = 1

            ),
            onClick = {})
    }
}

@Composable
private fun CategoryListItem(
    category: Category,
    onClick: () -> Unit
) {
    val randomColor = remember(category.id) { generateRandomPastelColor() }

    Box{
        Card(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

        )
        {
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(40.dp)
                        .background(
                            randomColor
                        ),
                    contentAlignment = Alignment.Center,

                    ){
                    Text(
                        text = category.name.firstOrNull()?.toString()?.uppercase() ?: "",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                        text = category.name.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                    )

                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Ver detalhes")
            }
        }
        Row(
            modifier = Modifier
                .padding(end = 6.dp)
                .align(Alignment.TopEnd)

        ) {
            if (category.productCount > 0) {
                CountBadge(count = category.productCount)
            }
        }

    }

}

@Composable
private fun EmptyStateWarning(onNavigateToCreate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Category,
            contentDescription = "Nenhuma categoria encontrada",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Nenhuma Categoria",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Para adicionar produtos, você primeiro precisa cadastrar uma categoria.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateToCreate) {
            Text("Criar Primeira Categoria")
        }
    }
}
