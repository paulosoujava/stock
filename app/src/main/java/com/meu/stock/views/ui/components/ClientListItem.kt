package com.meu.stock.views.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meu.stock.model.Client

import com.meu.stock.ui.theme.StockTheme
import com.meu.stock.views.ui.utils.CpfVisualTransformation
import com.meu.stock.views.ui.utils.PhoneVisualTransformation
import com.meu.stock.views.ui.utils.generateRandomPastelColor
import androidx.core.net.toUri


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Show() {
    StockTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            ClientListItem(
                client = Client(
                    name = "João da Silva",
                    cpf = "123.456.789-00",
                    phone = "(11) 98765-4321",
                    email = "william.henry.moody@my-own-personal-domain.com",
                    notes = "Cliente padrão com notas.",
                    id = 1,
                    address = ""
                ),
                isSelectionMode = false,
                onEditClick = { },
                onDeleteClick = { },
                onSelectClick = { }

            )
        }


    }

}


@Composable
fun ClientListItem(
    modifier: Modifier = Modifier,
    client: Client,
    isSelectionMode: Boolean,
    startExpanded: Boolean = false,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onSelectClick: () -> Unit
) {

    val context = LocalContext.current
    val cpfFormatter = CpfVisualTransformation()
    val phoneFormatter = PhoneVisualTransformation()
    val formattedCpf = cpfFormatter.filter(AnnotatedString(client.cpf)).text.toString()
    val formattedPhone =
        phoneFormatter.filter(androidx.compose.ui.text.AnnotatedString(client.phone)).text.toString()

    var isExpanded by remember { mutableStateOf(startExpanded) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = generateRandomPastelColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelectionMode) CardDefaults.outlinedCardBorder() else null
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
                    .clickable { isExpanded = !isExpanded }
                    .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = client.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 4. ENVOLVA TODOS OS DETALHES EM UM AnimatedVisibility
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
            )
            {
                // Todo o conteúdo que deve ser expandido/recolhido vai aqui dentro
                Column(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                    // Coluna para os detalhes do cliente (CPF, Telefone, etc.)
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow(icon = Icons.Default.Person, text = formattedCpf)
                        InfoRow(
                            icon = Icons.Default.AddLocation,
                            text = client.address,
                        )

                        InfoRow(
                            icon = Icons.Default.Phone,
                            text = formattedPhone,
                            onClick = {
                                val intent =
                                    Intent(Intent.ACTION_DIAL, "tel:${client.phone}".toUri())
                                context.startActivity(intent)
                            }
                        )
                        InfoRow(
                            icon = Icons.Default.Email,
                            text = client.email,
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:${client.email}".toUri()
                                }
                                context.startActivity(intent)
                            }
                        )
                        InfoRow(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            text = "WhatsApp",
                            isLast = client.notes.isEmpty(),
                            onClick = {
                                try {
                                    val internationalNumber = "55${client.phone}"
                                    val url =
                                        "https://api.whatsapp.com/send?phone=$internationalNumber"
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = url.toUri()
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Log.e("WhatsAppError", "Não foi possível abrir o WhatsApp.", e)
                                }
                            }
                        )



                        if (client.notes.isNotBlank()) {
                            InfoRow(
                                icon = Icons.AutoMirrored.Filled.Notes,
                                text = client.notes,
                                isLast = true
                            )
                        }
                    }
                }
            }

            // A parte dos botões de Editar/Deletar continua fora do AnimatedVisibility
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = generateRandomPastelColor()
            )
            Column(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            )
            {
                if (isSelectionMode) {
                    TextButton(onClick = onSelectClick) {
                        Text("Selecionar")
                    }
                } else {

                    Row {
                        IconButton(onClick = { onEditClick(client.id.toString()) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Cliente",
                            )
                        }
                        IconButton(onClick = { onDeleteClick(client.id.toString()) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar Cliente",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Um Composable auxiliar para criar uma linha com ícone e texto.
 */
@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isLast: Boolean = false
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = rowModifier.padding(
                top = 10.dp,
                bottom = 10.dp,
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // O texto já descreve a informação
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 18.sp
            )
        }
        if (!isLast)
            HorizontalDivider(modifier = Modifier.padding(top = 10.dp))
    }

}


