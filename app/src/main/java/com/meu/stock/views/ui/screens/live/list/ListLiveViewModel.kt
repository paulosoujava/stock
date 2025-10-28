package com.meu.stock.views.ui.screens.live.list


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.model.LiveSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ListLiveViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ListLiveState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLiveSessions()
    }


    private fun loadLiveSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simula chamada de rede

            // Dados de exemplo
            val sampleData = listOf(
                LiveSession(
                    id = "1",
                    title = "Live de Lançamento - Coleção Verão",
                    startTime = LocalDateTime.now().minusDays(1).withHour(20).withMinute(0),
                    endTime = LocalDateTime.now().minusDays(1).withHour(22).withMinute(15),
                    viewerCount = 1250,
                    salesCount = 85
                ),
                LiveSession(
                    id = "2",
                    title = "Queima de Estoque - Camisetas",
                    startTime = LocalDateTime.now().minusDays(7).withHour(19).withMinute(30),
                    endTime = LocalDateTime.now().minusDays(7).withHour(21).withMinute(0),
                    viewerCount = 800,
                    salesCount = 120
                ),
                LiveSession(
                    id = "3",
                    title = "Live Relâmpago - Acessórios",
                    startTime = LocalDateTime.now().minusHours(1), // Live acontecendo agora
                    endTime = null, // Sem hora de fim, pois está ao vivo
                    viewerCount = 350,
                    salesCount = 42
                )
            )

            _uiState.update { it.copy(isLoading = false, liveSessions = sampleData) }
        }
    }
}

