package com.meu.stock.views.ui.screens.clients.list


import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

import com.meu.stock.views.ui.components.ClientListItem
import com.meu.stock.views.ui.components.SearchAppBar
import com.meu.stock.views.ui.components.SearchItemBar

import com.meu.stock.views.ui.routes.AppRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
    navController: NavController,
    isSelectionMode: Boolean,
    clientNameToExpand: String?,
    viewModel: ClientListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var clientIdToDelete by remember { mutableStateOf<String?>(null) }
    var clientNameToDelete by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(clientNameToExpand) {
        viewModel.searchThisClientNavigation(clientNameToExpand)
    }

    if (clientIdToDelete != null && clientNameToDelete != null) {
        AlertDialog(
            onDismissRequest = { clientIdToDelete = null },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza de que deseja deletar o cliente \"$clientNameToDelete\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteClient(clientIdToDelete!!)
                        clientIdToDelete = null
                    }
                ) {
                    Text("Deletar")
                }
            },
            dismissButton = {
                TextButton(onClick = { clientIdToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Clientes") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = uiState.clients.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            if (uiState.clientNameToExpand.isNullOrEmpty())
                                IconButton(onClick = {
                                    viewModel.toggleSearchVisibility()
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index = 0)
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Pesquisar Clientes"
                                    )
                                }
                        }

                    }
                )
                HorizontalDivider()
            }

        },
        floatingActionButton = {
            if (uiState.clientNameToExpand.isNullOrEmpty())
                FloatingActionButton(
                    onClick = { navController.navigate(AppRoutes.CLIENT_FORM) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Cliente")
                }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.clients.isEmpty() -> {
                    EmptyState()
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            if (uiState.clientNameToExpand.isNullOrEmpty())
                                AnimatedVisibility(
                                    visible = uiState.isSearchActive,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    SearchAppBar(
                                        query = uiState.searchQuery,
                                        onQueryChange = viewModel::onSearchQueryChange,
                                        onClose = viewModel::toggleSearchVisibility
                                    )
                                }
                        }
                        items(uiState.clients, key = { it.id }) { client ->
                            ClientListItem(
                                client = client,
                                isSelectionMode = isSelectionMode,
                                startExpanded = uiState.clientNameToExpand != null && uiState.clientNameToExpand!!.isNotEmpty(),
                                onEditClick = { clientId ->
                                    navController.navigate("${AppRoutes.CLIENT_FORM}?clientId=$clientId")
                                },
                                onDeleteClick = { clientId ->
                                    clientIdToDelete = client.id.toString()
                                    clientNameToDelete = client.name
                                },
                                onSelectClick = {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("selectedClient", client)
                                    navController.popBackStack()
                                }
                            )
                        }
                        item {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * Composable para exibir quando a lista de clientes está vazia.
 */
@Composable
private fun BoxScope.EmptyState() {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = "Nenhum cliente",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Nenhum cliente cadastrado ainda.\nClique em '+' para começar.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}