package edu.esandpa202502.apptrueq.exchange.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.repository.exchange.ExchangeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class OffersUiState(
    val isLoading: Boolean = true,
    val offers: List<Offer> = emptyList(),
    val error: String? = null
)

data class HistoryUiState(
    val isLoading: Boolean = true,
    val completedOffers: List<Offer> = emptyList(),
    val error: String? = null
)

class ExchangeViewModel : ViewModel() {

    private val repository = ExchangeRepository()

    private val _offersUiState = MutableStateFlow(OffersUiState())
    val offersUiState: StateFlow<OffersUiState> = _offersUiState.asStateFlow()

    fun listenForReceivedOffers(userId: String) {
        viewModelScope.launch {
            repository.getReceivedOffers(userId)
                .onStart { _offersUiState.value = OffersUiState(isLoading = true) }
                .catch { e -> _offersUiState.value = OffersUiState(isLoading = false, error = e.message) }
                .collect { offers ->
                    // El repositorio ya ordena, no es necesario hacerlo aquí
                    _offersUiState.value = OffersUiState(isLoading = false, offers = offers)
                }
        }
    }

    fun onAcceptOffer(offer: Offer) {
        viewModelScope.launch { repository.acceptOffer(offer) }
    }

    fun onRejectOffer(offer: Offer) {
        viewModelScope.launch { repository.rejectOffer(offer) }
    }

    private val _historyUiState = MutableStateFlow(HistoryUiState())
    val historyUiState: StateFlow<HistoryUiState> = _historyUiState.asStateFlow()

    fun listenForHistory(userId: String) {
        viewModelScope.launch {
            repository.getOfferHistory(userId)
                .onStart { _historyUiState.value = HistoryUiState(isLoading = true) }
                .catch { e -> _historyUiState.value = HistoryUiState(isLoading = false, error = e.message) }
                .collect { offers ->
                    // El repositorio ya combina y ordena, no es necesario hacerlo aquí
                    _historyUiState.value = HistoryUiState(isLoading = false, completedOffers = offers)
                }
        }
    }
}
