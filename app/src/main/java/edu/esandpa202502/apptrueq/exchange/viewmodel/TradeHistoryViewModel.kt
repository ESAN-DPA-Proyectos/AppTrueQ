package edu.esandpa202502.apptrueq.exchange.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Trade
import edu.esandpa202502.apptrueq.repository.exchange.TradeRepository
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

    private val tradeRepository = TradeRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(TradeHistoryUiState())
    val uiState = _uiState.asStateFlow()

    private var allTrades: List<Trade> = emptyList()
    private val _statusFilter = MutableStateFlow("Todos")
    val statusFilter = _statusFilter.asStateFlow()

    init {
        loadTradeHistory()
    }

    private fun loadTradeHistory() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                allTrades = tradeRepository.getTradeHistory(userId)
                // ¡SOLUCIÓN! Forzamos la actualización del filtro inicial.
                applyFilter(allTrades, _statusFilter.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar el historial: ${e.message}") }
            } finally {
                 _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onStatusFilterChanged(newStatus: String) {
        _statusFilter.value = newStatus
        // Aplicar filtro cada vez que el usuario cambia la selección
        applyFilter(allTrades, newStatus)
    }

    /**
     * Función privada que centraliza la lógica de filtrado y actualiza la UI.
     */
    private fun applyFilter(trades: List<Trade>, status: String) {
        val filteredList = if (status == "Todos") {
            trades
        } else {
            trades.filter { it.status.equals(status, ignoreCase = true) }
        }
        _uiState.update { it.copy(trades = filteredList) }
    }
}