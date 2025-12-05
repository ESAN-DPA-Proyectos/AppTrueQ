package edu.esandpa202502.apptrueq.exchange.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Trade
import edu.esandpa202502.apptrueq.repository.trade.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de Historial de Trueques (HU-09).
 */
data class TradeHistoryUiState(
    val isLoading: Boolean = false,
    val trades: List<Trade> = emptyList(),
    val error: String? = null
)

class TradeHistoryViewModel : ViewModel() {

    // --- REPOSITORIOS Y AUTENTICACIÓN ---
    private val tradeRepository = TradeRepository()
    private val auth = FirebaseAuth.getInstance()

    // --- ESTADO DE LA UI ---
    private val _uiState = MutableStateFlow(TradeHistoryUiState())
    val uiState = _uiState.asStateFlow()

    // --- FILTROS ---
    private var allTrades: List<Trade> = emptyList() // Lista original sin filtrar
    private val _statusFilter = MutableStateFlow("Todos")
    val statusFilter = _statusFilter.asStateFlow()

    // TODO: Se podría añadir un StateFlow para el filtro de fecha de la misma manera.

    init {
        loadTradeHistory()

        // Lógica reactiva para aplicar filtros.
        // Cada vez que el filtro de estado cambie, se recalculará la lista.
        viewModelScope.launch {
            _statusFilter.collect { status ->
                val filteredList = if (status == "Todos") {
                    allTrades
                } else {
                    allTrades.filter { it.status.equals(status, ignoreCase = true) }
                }
                _uiState.update { it.copy(trades = filteredList) }
            }
        }
    }

    /**
     * Carga el historial completo de trueques del usuario actual.
     */
    private fun loadTradeHistory() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                allTrades = tradeRepository.getTradeHistoryForUser(userId)
                // Al cargar, aplicamos el filtro que esté activo en ese momento.
                onStatusFilterChanged(_statusFilter.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar el historial: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Se llama desde la UI cuando el usuario selecciona un nuevo filtro de estado.
     */
    fun onStatusFilterChanged(newStatus: String) {
        _statusFilter.value = newStatus
    }
}
