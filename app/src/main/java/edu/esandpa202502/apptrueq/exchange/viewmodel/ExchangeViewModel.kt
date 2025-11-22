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

/**
 * Estado de la UI para la pantalla de ofertas recibidas (HU-07), adaptado al flujo de `needs/offers`.
 */
data class OffersUiState(
    val isLoading: Boolean = true,
    val offers: List<Offer> = emptyList(),
    val error: String? = null
)

class ExchangeViewModel : ViewModel() {

    private val repository = ExchangeRepository()

    private val _uiState = MutableStateFlow(OffersUiState())
    val uiState: StateFlow<OffersUiState> = _uiState.asStateFlow()

    /**
     * Inicia la escucha de ofertas recibidas para un usuario específico.
     */
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

    /**
     * Delega la aceptación de una oferta al repositorio.
     */
    fun onAcceptOffer(offer: Offer) {
        viewModelScope.launch {
            repository.acceptOffer(offer)
        }
    }

    /**
     * Delega el rechazo de una oferta al repositorio.
     */
    fun onRejectOffer(offer: Offer) {
        viewModelScope.launch {
            repository.rejectOffer(offer)
        }
    }
}
