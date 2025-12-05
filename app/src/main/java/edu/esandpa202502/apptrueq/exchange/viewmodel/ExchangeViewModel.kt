package edu.esandpa202502.apptrueq.exchange.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.repository.exchange.ExchangeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExchangeUiState(
    val isLoading: Boolean = false,
    val offers: List<Offer> = emptyList(),
    val needs: List<Need> = emptyList(),
    val error: String? = null
)

class ExchangeViewModel : ViewModel() {

    private val repository = ExchangeRepository()

    private val _uiState = MutableStateFlow(ExchangeUiState())
    val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

    init {
        loadOffers()
        loadNeeds()
    }

    fun loadOffers() {
        viewModelScope.launch {
            repository.getAvailableOffers().collect { list ->
                _uiState.update { it.copy(offers = list) }
            }
        }
    }

    fun loadNeeds() {
        viewModelScope.launch {
            repository.getPublishedNeeds().collect { list ->
                _uiState.update { it.copy(needs = list) }
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            loadOffers()
            loadNeeds()
            return
        }

        viewModelScope.launch {
            repository.searchOffers(query).collect { offers ->
                _uiState.update { it.copy(offers = offers) }
            }
        }

        viewModelScope.launch {
            repository.searchNeeds(query).collect { needs ->
                _uiState.update { it.copy(needs = needs) }
            }
        }
    }
}
