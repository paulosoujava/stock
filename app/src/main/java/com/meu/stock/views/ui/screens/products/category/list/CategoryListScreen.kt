package com.meu.stock.views.ui.screens.products.category.list


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.meu.stock.model.Category
import com.meu.stock.ui.theme.StockTheme

import com.meu.stock.views.ui.routes.AppRoutes
import com.meu.stock.views.ui.utils.generateRandomPastelColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    navController: NavController,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Diálogo de confirmação para deletar
    uiState.categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Deletar Categoria") },
            text = { Text("Tem certeza que deseja deletar a categoria '${category.name}'? Todos os produtos vinculados a ela também serão removidos. Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sim, Deletar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Categorias") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(AppRoutes.CATEGORY_FORM) }) {
                            Icon(Icons.Default.Add, "Adicionar Categoria")
                        }
                    }
                )
                HorizontalDivider()
            }

        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Estado 1: Carregando
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // Estado 2: Lista Vazia (após o carregamento)
                uiState.categories.isEmpty() -> {
                    EmptyCategoryState()
                }
                // Estado 3: Lista com Dados
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(uiState.categories, key = { it.id!! }) { category ->
                            CategoryListItem(
                                category = category,
                                onEditClick = {
                                    navController.navigate("${AppRoutes.CATEGORY_FORM}?categoryId=${category.id}")
                                },
                                onDeleteClick = {
                                    viewModel.onShowDeleteDialog(category)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable para exibir quando a lista de categorias está vazia.
 */
@Composable
private fun BoxScope.EmptyCategoryState() {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Category,
            contentDescription = "Nenhuma categoria",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Nenhuma categoria cadastrada ainda.\nClique em '+' para começar.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Show() {
    StockTheme {
        CategoryListItem(
            category = Category(
                id = "12",
                name = "Categoria de Teste",
                description = "teste"
            ),
            onEditClick = {},
            onDeleteClick = {}
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class) // Adicione esta anotação se não estiver presente
@Composable
private fun CategoryListItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = generateRandomPastelColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border =  CardDefaults.outlinedCardBorder()
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
                    Text(
                        text = category.name.uppercase(),
                        maxLines = 1,
                        fontSize = 19.sp,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = category.description,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                    )
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            "Deletar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                    }

                }

            }
        }
    }
}

