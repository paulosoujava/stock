package com.meu.stock.views.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SearchAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Pesquisar por nome, CPF..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange, // Conecta a digitação à função do ViewModel
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ícone de Pesquisa") },
        trailingIcon = {
            IconButton(onClick = onClose) { // Botão para fechar a busca
                Icon(Icons.Default.Close, contentDescription = "Fechar Pesquisa")
            }
        },
        singleLine = true
    )
}
