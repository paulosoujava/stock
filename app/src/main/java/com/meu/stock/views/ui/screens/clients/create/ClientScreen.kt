package com.meu.stock.views.ui.screens.clients.create

import AppTextArea
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.meu.stock.views.ui.utils.CpfVisualTransformation
import com.meu.stock.views.ui.utils.PhoneVisualTransformation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(
    navController: NavController,
    clientViewModel: ClientViewModel = hiltViewModel(),
    clientId: String?
) {
    val uiState by clientViewModel.uiState.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(key1 = clientId) {
        clientViewModel.loadClient(clientId)
    }

    val screenTitle = if (clientId == null) "Novo Cliente" else "Editar Cliente"


    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Cliente salvo com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            DefaultTabBar(
                title = screenTitle,
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
            // Formulário
            item {
                AppOutlinedTextField(
                    value = uiState.fullName,
                    onValueChange = clientViewModel::onFullNameChange,
                    label = "Nome Completo",
                    enabled = !uiState.isLoading
                )
            }
            item {
                AppOutlinedTextField(
                    value = uiState.cpf,
                    onValueChange = clientViewModel::onCpfChange,
                    label = "CPF",
                    visualTransformation = CpfVisualTransformation(),
                    keyboardType = KeyboardType.Number,
                    enabled = !uiState.isLoading
                )
            }
            item {
                AppOutlinedTextField(
                    value = uiState.phone,
                    visualTransformation = PhoneVisualTransformation(),
                    onValueChange = clientViewModel::onPhoneChange,
                    label = "Telefone",
                    keyboardType = KeyboardType.Phone,
                    enabled = !uiState.isLoading
                )
            }
            item {
                AppOutlinedTextField(
                    value = uiState.email,
                    onValueChange = clientViewModel::onEmailChange,
                    label = "E-mail",
                    keyboardType = KeyboardType.Email,
                    enabled = !uiState.isLoading
                )
            }
            item {
                AppTextArea(
                    value = uiState.notes,
                    onValueChange = clientViewModel::onNotesChange,
                    label = "Texto Livre (Observações)",
                    enabled = !uiState.isLoading
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                AppButton(
                    onClick = clientViewModel::onSaveClientClick,
                    text = "Salvar Cliente",
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}
