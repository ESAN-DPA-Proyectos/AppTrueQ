package edu.esandpa202502.apptrueq.exchange.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Trade
import edu.esandpa202502.apptrueq.model.TradeStatus
import edu.esandpa202502.apptrueq.repository.trade.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // --- REPOSITORIO Y AUTH ---
    private val tradeRepository = TradeRepository()
    private val auth = FirebaseAuth.getInstance()

    // --- ESTADO DE LA UI ---
    private val _uiState = MutableStateFlow(TradeHistoryUiState())
    val uiState = _uiState.asStateFlow()

    // --- FILTROS ---
    private var allTrades: List<Trade> = emptyList() // Lista original sin filtrar
    private val _statusFilter = MutableStateFlow("Todos")
    val statusFilter = _statusFilter.asStateFlow()

    init {
        loadTradeHistory()

        // Cada vez que cambia el filtro, recalculamos la lista mostrada
        viewModelScope.launch {
            _statusFilter.collect { status ->
                val filteredList = when (status) {
                    "Todos" -> allTrades
                    "Pendiente" -> allTrades.filter { it.status == TradeStatus.PENDING }
                    "Aceptado" -> allTrades.filter { it.status == TradeStatus.ACCEPTED }
                    "Rechazado" -> allTrades.filter { it.status == TradeStatus.REJECTED }
                    "Completado" -> allTrades.filter { it.status == TradeStatus.COMPLETED }
                    "Cancelado" -> allTrades.filter { it.status == TradeStatus.CANCELLED }
                    else -> allTrades
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
                // Usa el método REAL del TradeRepository
                allTrades = tradeRepository.getTradeHistory(userId)
                // Aplica el filtro actual sobre la lista recién cargada
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
