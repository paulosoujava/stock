package com.meu.stock.views.ui.promo.list

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

import coil.compose.AsyncImage
import com.meu.stock.R // Supondo que você tenha um ícone para WhatsApp
import com.meu.stock.model.Promo
import com.meu.stock.views.ui.routes.AppRoutes
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale
import kotlin.text.toDoubleOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoListScreen(
    navController: NavController,
    viewModel: PromoListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PromoListContract.Effect.NavigateToPromoForm -> {
                    navController.navigate(AppRoutes.buildPromoFormRoute(effect.promoId))
                }
                is PromoListContract.Effect.SendWhatsAppMessage -> {
                    sendWhatsAppMessage(context, effect.promo)
                }

                is PromoListContract.Effect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Promoções") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(AppRoutes.PROMO_FORM)
                        }) {
                            Icon(
                                Icons.Default.Add, contentDescription = "Criar Promo",
                                 tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.promos.isEmpty()) {
                EmptyState(
                    onAddClick = { viewModel.sendEvent(PromoListContract.Event.OnAddPromoClicked) },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.promos, key = { it.id }) { promo ->
                        PromoItemCard(
                            promo = promo,
                            onEditClick = {
                                viewModel.sendEvent(
                                    PromoListContract.Event.OnEditPromoClicked(
                                        promo.id
                                    )
                                )
                            },
                            onDeleteClick = {
                                viewModel.sendEvent(
                                    PromoListContract.Event.OnDeletePromoClicked(
                                        promo
                                    )
                                )
                            },
                            onSendClick = {
                                viewModel.sendEvent(
                                    PromoListContract.Event.OnSendPromoClicked(
                                        promo
                                    )
                                )
                            }
                        )
                    }
                }
            }

            // Diálogo de confirmação para deletar
            state.promoToDelete?.let { promo ->
                DeleteConfirmationDialog(
                    promoTitle = promo.title,
                    onConfirm = { viewModel.sendEvent(PromoListContract.Event.OnDeletePromoConfirmed) },
                    onDismiss = { viewModel.sendEvent(PromoListContract.Event.OnDismissDeleteDialog) }
                )
            }
        }
    }
}

@Composable
private fun PromoItemCard(
    promo: Promo,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        .format( promo.price / 100.0)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column {
            AsyncImage(
                model = promo.imageUri,
                contentDescription = promo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.loading),
                error = painterResource(id = R.drawable.error_image)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = promo.title.split(" ").joinToString(" ") { it.replaceFirstChar(Char::titlecase) },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = promo.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)


                    )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    IconButton(onClick = onSendClick) {
                        Icon(
                            painterResource(id = R.drawable.whtas),
                            contentDescription = "Enviar via WhatsApp",
                            tint = Color.Green,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = "Nenhuma promoção",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhuma promoção cadastrada.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Clique no botão '+' para adicionar a sua primeira promoção.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Criar Promoção")
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    promoTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Exclusão") },
        text = { Text("Você tem certeza que deseja deletar a promoção '$promoTitle'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Deletar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun sendWhatsAppMessage(context: Context, promo: Promo) {
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(promo.price)
    val message = "🔥 *${promo.title}* 🔥\n${promo.desc}\n💰 Valor: $formattedPrice"

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        setPackage("com.whatsapp") // Para abrir o WhatsApp diretamente

        if (promo.imageUri != null) {
            type = "image/*"
            val imageUri = Uri.parse(promo.imageUri)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            type = "text/plain"
        }
    }

    try {
        // Usar um chooser previne crashes se o WhatsApp não estiver instalado,
        // embora setPackage force o WhatsApp. Uma abordagem mais segura seria verificar
        // a instalação antes.
        val chooser = Intent.createChooser(sendIntent, "Enviar promoção via...").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    } catch (e: ActivityNotFoundException) {
        // Tratar o caso em que o WhatsApp não está instalado (talvez com um Toast/Snackbar)
    }
}
