package com.meu.stock.views.ui.screens.live.create


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meu.stock.views.ui.components.AppButton
import com.meu.stock.views.ui.components.AppOutlinedTextField
import com.meu.stock.views.ui.components.DefaultTabBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLiveScreen(
    navController: NavController,
    viewModel: AddLiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Efeito para observar o sucesso do salvamento e navegar de volta
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Live agendada com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            DefaultTabBar(
                title = "Agendar Nova Live",
                navController = navController
            )


        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            item {
                AppOutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = "Título da Live",
                    enabled = !uiState.isLoading
                )
            }

            // Data e Hora em uma linha
            item {

                AppOutlinedTextField(
                    value = uiState.startDate,
                    onValueChange = viewModel::onDateChange,
                    label = "Data (DD/MM/AAAA)",
                    enabled = !uiState.isLoading
                )
            }
            item {
                AppOutlinedTextField(
                    value = uiState.startTime,
                    onValueChange = viewModel::onTimeChange,
                    label = "Hora (HH:MM)",
                    enabled = !uiState.isLoading
                )

            }

            // Botão e Mensagem de Erro
            item {
                Spacer(Modifier.height(16.dp))

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                AppButton(
                    onClick = viewModel::onSaveLiveClick,
                    text = "Agendar Live",
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}
