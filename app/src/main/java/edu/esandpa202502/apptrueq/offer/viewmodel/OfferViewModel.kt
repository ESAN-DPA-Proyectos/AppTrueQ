package edu.esandpa202502.apptrueq.offer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OfferViewModel : ViewModel() {
    // Estado interno con lista de ofertas
    private val _offers = MutableStateFlow<List<String>>(emptyList())
    val offers: StateFlow<List<String>> = _offers

    // Simula agregar una nueva oferta
    fun addOffer(title: String) {
        viewModelScope.launch {
            _offers.value = _offers.value + title
        }
    }
}
