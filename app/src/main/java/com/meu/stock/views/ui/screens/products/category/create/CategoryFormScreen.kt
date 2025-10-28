package com.meu.stock.views.ui.screens.products.category.create

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.NavController
import com.meu.stock.views.ui.components.AppButton
import com.meu.stock.views.ui.components.AppOutlinedTextField
import com.meu.stock.views.ui.components.DefaultTabBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    navController: NavController,
    viewModel: CategoryFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.popBackStack()
        }
    }


    Scaffold(
        topBar = {
            DefaultTabBar(
                title = "Nova Categoria",
                navController = navController
            )

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppOutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = "Nome da Categoria",
                enabled = !uiState.isLoading
            )

            AppOutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange, // Conecte ao ViewModel
                label = "Descrição",
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false
            )
            Spacer(Modifier.height(16.dp))

            if(uiState.errorMessage != null ){
                Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            AppButton(
                onClick = viewModel::saveCategory,
                text = "Salvar Categoria",
                isLoading = uiState.isLoading
            )
        }
    }
}
