package com.meu.stock.views.ui.screens.register

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.password
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.meu.stock.R
import com.meu.stock.views.ui.routes.AppRoutes
import com.meu.stock.views.ui.screens.rergister.RegisterEvent
import com.meu.stock.views.ui.screens.rergister.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showHelpDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current // Contexto para os Intents

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegisterEvent.NavigateToMain -> {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                // O ViewModel agora controla a exibição do diálogo
                is RegisterEvent.ShowHelpDialog -> showHelpDialog = true
            }
        }
    }

    // --- ALERTDIALOG DETALHADO RESTAURADO ---
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Precisa de Ajuda?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Para suporte, entre em contato:")
                    // Opção de Ligar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:48996297813")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Ícone de telefone"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("(48) 99629-7813", fontWeight = FontWeight.Bold)
                    }
                    // Opção de Enviar E-mail
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:paulosoujasa@gmail.com")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Ícone de e-mail"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("paulosoujasa@gmail.com", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) { Text("FECHAR") }
            }
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Cabeçalho Preto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        // 2. Card Branco para o formulário
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = this.maxHeight * 0.25f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Criar Conta",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Campos de E-mail, Senha e Repetir Senha...
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !uiState.isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !uiState.isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = { Text("Repita a Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    isError = uiState.errorMessage?.contains("senhas") == true,
                    enabled = !uiState.isLoading
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Mensagem de erro e botão...
                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(
                    onClick = viewModel::onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Cadastrar", fontSize = 16.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- TEXTOS INFERIORES CORRIGIDOS ---

                // Texto para navegar para o Login
                val loginAnnotatedString = buildAnnotatedString {
                    pushLink(LinkAnnotation.Clickable(
                        tag = "Voltar",
                        linkInteractionListener = { navController.navigate(AppRoutes.LOGIN) }
                    ))
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )) {
                        append("Voltar")
                    }
                    pop()
                }
                Text(text = loginAnnotatedString)

                Spacer(modifier = Modifier.height(8.dp))

                // Texto para abrir o pop-up de Ajuda
                val helpAnnotatedString = buildAnnotatedString {
                    append("Precisa de ajuda? ")
                    pushLink(LinkAnnotation.Clickable(
                        tag = "Help",
                        // Dispara o evento no ViewModel
                        linkInteractionListener = { viewModel.onHelpClick() }
                    ))
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )) {
                        append("Clique aqui")
                    }
                    pop()
                }
                Text(text = helpAnnotatedString)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
