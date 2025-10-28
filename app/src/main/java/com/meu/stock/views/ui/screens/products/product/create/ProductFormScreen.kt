package com.meu.stock.views.ui.screens.products.product.create

import AppTextArea
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.meu.stock.views.ui.components.AppButton
import com.meu.stock.views.ui.components.AppOutlinedTextField
import com.meu.stock.views.ui.components.DefaultTabBar
import com.meu.stock.views.ui.utils.CurrencyVisualTransformation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    navController: NavController,
    viewModel: ProductFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Efeito para tratar o sucesso ao salvar ou deletar
    LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        if (uiState.deleteSuccess) {
            Toast.makeText(context, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = navController::popBackStack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    title =  {Text(uiState.screenTitle)},
                    actions = {
                        // Botão de deletar só aparece no modo de edição
                        if (uiState.isEditMode) {
                            IconButton(onClick = viewModel::onDeleteProductClick) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Excluir Produto",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )
                HorizontalDivider()
            }

        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Formulário
            item {
                AppOutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nome do Produto",
                    enabled = !uiState.isLoading
                )
            }
            item {
                AppOutlinedTextField(
                    value = uiState.description,
                    modifier = Modifier.height(120.dp),
                    onValueChange = viewModel::onDescriptionChange,
                    label = "Descrição (Opcional)",
                    enabled = !uiState.isLoading
                )
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = uiState.priceUnit,
                        onValueChange = viewModel::onPriceUnitChange,
                        label = "Preço de Custo",
                        keyboardType = KeyboardType.Decimal,
                        visualTransformation = CurrencyVisualTransformation(),
                        enabled = !uiState.isLoading
                    )
                    AppOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = uiState.priceSale,
                        onValueChange = viewModel::onPriceSaleChange,
                        label = "Preço de Venda",
                        visualTransformation = CurrencyVisualTransformation(),
                        keyboardType = KeyboardType.Decimal,
                        enabled = !uiState.isLoading
                    )
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = uiState.stockQuantity,
                        onValueChange = viewModel::onStockQuantityChange,
                        label = "Qtd. em Estoque",
                        keyboardType = KeyboardType.Number,
                        enabled = !uiState.isLoading
                    )
                    AppOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = uiState.lowStockQuantity,
                        onValueChange = viewModel::onLowStockQuantityChange,
                        label = "Estoque Baixo",
                        keyboardType = KeyboardType.Number,
                        enabled = !uiState.isLoading
                    )
                }
            }

            // Espaçamento, Erro e Botão de Salvar
            item {
                Spacer(modifier = Modifier.height(16.dp))
                uiState.errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                }
                AppButton(
                    onClick = viewModel::onSaveProductClick,
                    text = if (uiState.isEditMode) "Atualizar Produto" else "Salvar Produto",
                    isLoading = uiState.isLoading
                )
            }
        }
    }

    // Diálogo de confirmação de exclusão
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissDeleteDialog,
            title = { Text("Excluir Produto") },
            text = { Text("Tem certeza que deseja excluir o produto \"${uiState.name}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = viewModel::onConfirmDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sim, Excluir") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissDeleteDialog) { Text("Cancelar") }
            }
        )
    }
}
