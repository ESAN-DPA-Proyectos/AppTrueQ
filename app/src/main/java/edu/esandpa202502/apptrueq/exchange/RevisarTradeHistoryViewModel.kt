package edu.esandpa202502.apptrueq.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Trade
import edu.esandpa202502.apptrueq.repository.trade.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TradeHistoryUiState(
    val isLoading: Boolean = true,
    val trades: List<Trade> = emptyList(),
    val error: String? = null
)

class RevisarTradeHistoryViewModel : ViewModel() {

    private val tradeRepository = TradeRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(TradeHistoryUiState())
    val uiState: StateFlow<TradeHistoryUiState> = _uiState.asStateFlow()

    init {
        loadTradeHistory()
    }

    fun loadTradeHistory() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = TradeHistoryUiState(isLoading = false, error = "Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val trades = tradeRepository.getTradeHistory(userId)
                _uiState.value = TradeHistoryUiState(isLoading = false, trades = trades)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
