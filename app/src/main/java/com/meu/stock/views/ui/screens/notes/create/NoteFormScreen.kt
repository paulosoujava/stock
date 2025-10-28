package com.meu.stock.views.ui.screens.notes.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.meu.stock.service.AlarmPermissionHelper

import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteFormScreen(
    navController: NavController,
    viewModel: NoteFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDeleteDialog by viewModel.showDeleteConfirmDialog.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado para controlar a exibição do diálogo de permissão de alarme
    var showAlarmPermissionDialog by remember { mutableStateOf(false) }

    // Lançador para abrir as configurações de "Alarmes e Lembretes"
    val alarmSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult() 
    ) {
        // Após o usuário retornar, salvamos a nota se ele tiver um lembrete definido
        if (AlarmPermissionHelper.canScheduleExactAlarms(context)) {
            viewModel.saveNote()
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            showDatePicker = true
        } 
    }

    val checkAndRequestPermissions: () -> Unit = {
        val reminderIsSet = uiState.reminderDate.isNotBlank() && uiState.reminderTime.isNotBlank()
        if (reminderIsSet && !AlarmPermissionHelper.canScheduleExactAlarms(context)) {
            showAlarmPermissionDialog = true
        } else {
            viewModel.saveNote()
        }
    }

    // Exibe o diálogo para guiar o usuário às configurações de alarme
    if (showAlarmPermissionDialog) {
        AlarmPermissionDialog(
            onDismiss = { showAlarmPermissionDialog = false },
            onConfirm = {
                showAlarmPermissionDialog = false
                val intent = AlarmPermissionHelper.getAlarmSettingsIntent(context)
                alarmSettingsLauncher.launch(intent)
            }
        )
    }

    // Efeito para exibir Snackbars de sucesso ou erro
    LaunchedEffect(uiState.saveSuccess, uiState.errorMessage) {
        if (uiState.saveSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Nota salva com sucesso!")
                viewModel.onSaveSuccessShown() // Consome o evento
            }
        }
        uiState.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.onErrorMessageShown() // Consome o evento
            }
        }
    }

    // Efeito para navegar de volta após sucesso (criação ou deleção)
    LaunchedEffect(uiState.saveSuccess, deleteSuccess) {
        if (deleteSuccess) {
            navController.popBackStack()
        }
        if (uiState.saveSuccess && !uiState.isEditing) {
            navController.popBackStack()
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onConfirm = viewModel::confirmDeletion,
            onDismiss = viewModel::onDismissDeleteDialog
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.onDateSelected(it) }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onTimeSelected(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(if (uiState.isEditing) "Editar Nota" else "Nova Nota") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    actions = {
                        if (uiState.isEditing) {
                            IconButton(onClick = viewModel::onDeleteRequest) {
                                Icon(Icons.Default.Delete, contentDescription = "Deletar Nota", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        IconButton(onClick = checkAndRequestPermissions) { // <- AÇÃO DE SALVAR MUDOU AQUI
                            Icon(Icons.Default.Save, contentDescription = "Salvar Nota", tint = Color(0xFF008000))
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = viewModel::onContentChange,
                    label = { Text("Conteúdo") },
                    modifier = Modifier.fillMaxSize().weight(2f),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lembrar-me",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "* Campo não obrigatórios.",
                        style = MaterialTheme.typography.displaySmall,
                        fontSize = 12.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DateSelector(
                        modifier = Modifier.weight(1f),
                        date = uiState.reminderDate,
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                showDatePicker = true
                            }
                        }
                    )

                    TimeSelector(
                        modifier = Modifier.weight(1f),
                        time = uiState.reminderTime,
                        onClick = { showTimePicker = true }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun AlarmPermissionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permissão Necessária") },
        text = { Text("Para usar lembretes, o aplicativo precisa da sua permissão para definir alarmes. Por favor, ative a permissão na tela de configurações.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Abrir Configurações") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DeleteConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Exclusão") },
        text = { Text("Você tem certeza de que deseja deletar esta nota? Esta ação não pode ser desfeita.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Deletar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun DateSelector(modifier: Modifier = Modifier, date: String, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Data", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = date.ifEmpty { "DD/MM/AAAA" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (date.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TimeSelector(modifier: Modifier = Modifier, time: String, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = "Hora", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = time.ifEmpty { "00:00" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (time.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
