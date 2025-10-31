package com.meu.stock.views.ui.screens.login

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.flow.collectLatest
import androidx.core.net.toUri
import com.meu.stock.views.ui.components.DottedDivider
import com.meu.stock.views.ui.components.GradientButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- ESTADO PARA CONTROLAR O POP-UP ---
    var showRequestAccessDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // --- POP-UP DE SOLICITAÇÃO DE ACESSO ---
    if (showRequestAccessDialog) {
        AlertDialog(
            onDismissRequest = { showRequestAccessDialog = false },
            title = {
                Column {
                    Text("Solicitar Acesso")
                    Text(
                        "Entre em contato conosco.\nClique no botão abaixo para solicitar acesso:",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider()
                    // Opção de Ligar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:48996297813".toUri()
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Phone, "Ícone de telefone")
                        Spacer(Modifier.width(16.dp))
                        Text("(48) 99629-7813", fontWeight = FontWeight.Bold)
                    }

                    Text("OU")

                    // Opção de Enviar E-mail
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:paulosoujava@gmail.com".toUri()
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Email, "Ícone de e-mail")
                        Spacer(Modifier.width(16.dp))
                        Text("paulosoujava@gmail.com", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                }
            },
            confirmButton = {
                TextButton(onClick = { showRequestAccessDialog = false }) {
                    Text("FECHAR")
                }
            }
        )
    }


    LaunchedEffect(key1 = true) {
        loginViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginEvent.NavigateToMain -> {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }

                is LoginEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // A UI agora é construída dentro de uma Box para sobrepor os elementos
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0052D4),
                        Color(0xFF4364F7),
                        Color(0xFF6FB1FC),
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.27f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Usando seu logo
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        // 2. Card Branco para o formulário
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = this.maxHeight * 0.25f) // Começa um pouco antes do fim do header
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp))
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Spacer(
                    modifier = Modifier.height(86.dp)
                )

                // Campo de E-mail
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = loginViewModel::onEmailChange,
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
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Senha
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = loginViewModel::onPasswordChange,
                    label = { Text("Password") },
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
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage != null
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Exibe mensagem de erro, se houver
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
                    onClick = loginViewModel::onLoginClick,
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
                        Text("Login", fontSize = 16.sp, color = Color.White)
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(top = 56.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DottedDivider(
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        "OU", fontSize = 16.sp, color = Color.Black,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    DottedDivider(
                        modifier = Modifier.weight(2f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // Empurra o próximo item para baixo


                // --- TEXTO INFERIOR ATUALIZADO ---
                val annotatedString = buildAnnotatedString {
                    pushLink(
                        LinkAnnotation.Clickable(
                            tag = "RequestAccess",
                            // Ao clicar, muda o estado para exibir o diálogo
                            linkInteractionListener = { showRequestAccessDialog = true }
                        ))
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("solicite a sua") // NOVO TEXTO
                    }
                    pop() // Remove a anotação do link
                }

                Text(text = annotatedString)
                Spacer(modifier = Modifier.height(16.dp)) // Espaço na base
            }
        }
        // SnackbarHost para exibir erros
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
