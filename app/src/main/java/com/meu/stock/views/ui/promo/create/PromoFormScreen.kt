package com.meu.stock.views.ui.promo.create


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.meu.stock.R
import com.meu.stock.views.ui.utils.CurrencyVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoFormScreen(
    navController: NavController,
    viewModel: PromoFormViewModel = hiltViewModel()
) {

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.sendEvent(PromoFormContract.Event.OnImageUriChanged(uri))
        }
    )

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observa os efeitos do ViewModel
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PromoFormContract.Effect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is PromoFormContract.Effect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        val titleText = if (state.promoId == null) {
                            "Criar Promoção"
                        } else {
                            "Editar Promoção"
                        }
                        Text(titleText)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
                HorizontalDivider()
            }

        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.sendEvent(PromoFormContract.Event.OnSaveClicked) }) {
                Icon(Icons.Default.Save, contentDescription = "Salvar Promoção")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .imePadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Componente de Imagem
            ImageSelector(
                selectedUri = state.imageUri,
                isLoading = state.isImageLoading,
                onClick = {
                    // Lança o seletor de mídia para imagens
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Campo de Título
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.sendEvent(PromoFormContract.Event.OnTitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Título da Promoção") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.price,
                onValueChange = { newPrice ->
                    val digitsOnly = newPrice.filter { it.isDigit() }
                    if (digitsOnly.length <= 9) {
                        viewModel.sendEvent(PromoFormContract.Event.OnPriceChanged(digitsOnly))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Preço (Ex: 19,90)") },
                visualTransformation = CurrencyVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Descrição
            OutlinedTextField(
                value = state.description,
                onValueChange = {
                    viewModel.sendEvent(
                        PromoFormContract.Event.OnDescriptionChanged(
                            it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Altura maior para o campo de descrição
                label = { Text("Descrição") }
            )


            Spacer(modifier = Modifier.height(80.dp)) // Espaço para o FAB não cobrir conteúdo
        }
    }
}

/*
@Composable
private fun ImageSelector(
    selectedUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selectedUri != null) {
            AsyncImage(
                model = selectedUri,
                contentDescription = "Imagem da Promoção",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                // Placeholder e Error podem ser úteis durante o carregamento
                placeholder = painterResource(id = R.drawable.logo),
                error = painterResource(id = R.drawable.logo)
            )
        } else {
            // Estado inicial, quando nenhuma imagem foi selecionada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Adicionar Foto",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Toque para adicionar uma imagem",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

*/

@Composable
private fun ImageSelector(
    selectedUri: Uri?,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (selectedUri != null) {
                AsyncImage(
                    model = selectedUri,
                    error = painterResource(id = R.drawable.error_image),
                    placeholder = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem da Promoção",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Adicionar Foto",
                        modifier = Modifier.size(48.dp)
                    )
                    Text("Toque para adicionar uma imagem")
                }
            }
        }
    }
}
