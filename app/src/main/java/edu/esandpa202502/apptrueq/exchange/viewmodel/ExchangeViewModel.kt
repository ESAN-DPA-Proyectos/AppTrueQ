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

class ExchangeViewModel : ViewModel() {

    private val repository = ExchangeRepository()

    private val _uiState = MutableStateFlow(OffersUiState())
    val uiState: StateFlow<OffersUiState> = _uiState.asStateFlow()

    fun listenForReceivedOffers(userId: String) {
        viewModelScope.launch {
            repository.getReceivedOffers(userId)
                .onStart { _uiState.value = OffersUiState(isLoading = true) }
                .catch { e -> _uiState.value = OffersUiState(isLoading = false, error = e.message) }
                .collect { offers ->
                    _uiState.value = OffersUiState(isLoading = false, offers = offers)
                }
        }
    }

    fun onAcceptOffer(offer: Offer) {
        viewModelScope.launch {
            repository.acceptOffer(offer)
        }
    }

    fun onRejectOffer(offer: Offer) {
        viewModelScope.launch {
            repository.rejectOffer(offer)
        }
    }
}
