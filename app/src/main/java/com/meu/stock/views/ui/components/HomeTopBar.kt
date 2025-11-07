package com.meu.stock.views.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storefront

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onQrcanClick: () -> Unit,
    onPromoListClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onQrcanClick) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onPromoListClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Promoção do App",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sair do App",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = modifier
    )
}

