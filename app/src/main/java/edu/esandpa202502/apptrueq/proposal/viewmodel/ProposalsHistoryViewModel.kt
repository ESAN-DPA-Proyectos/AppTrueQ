package edu.esandpa202502.apptrueq.proposal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Trade
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

/**
 * ViewModel ÚNICO Y CONSOLIDADO para el historial de propuestas/trueques.
 */
class ProposalsHistoryViewModel : ViewModel() {

    private val tradeRepository = TradeRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(TradeHistoryUiState())
    val uiState = _uiState.asStateFlow()

    private var allTrades: List<Trade> = emptyList()
    private val _statusFilter = MutableStateFlow("Todos")
    val statusFilter = _statusFilter.asStateFlow()

    init {
        loadTradeHistory()

        viewModelScope.launch {
            _statusFilter.collect { status ->
                val filteredList = if (status.equals("Todos", ignoreCase = true)) {
                    allTrades
                } else {
                    allTrades.filter { it.status.equals(status, ignoreCase = true) }
                }
                _uiState.update { it.copy(trades = filteredList) }
            }
        }
    }

    private fun loadTradeHistory() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                allTrades = tradeRepository.getTradeHistory(userId)
                onStatusFilterChanged(_statusFilter.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar el historial: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onStatusFilterChanged(newStatus: String) {
        _statusFilter.value = newStatus
    }
}