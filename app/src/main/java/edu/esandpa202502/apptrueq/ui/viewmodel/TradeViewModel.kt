package edu.esandpa202502.apptrueq.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.data.repository.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TradeViewModel : ViewModel() {

    private val repository = TradeRepository()

    private val _needs = MutableStateFlow<List<Need>>(emptyList())
    val needs: StateFlow<List<Need>> = _needs

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers

    init {
        loadNeeds()
        loadOffers()
    }

    private fun loadNeeds() {
        viewModelScope.launch {
            _needs.value = repository.getNeeds()
        }
    }

    private fun loadOffers() {
        viewModelScope.launch {
            _offers.value = repository.getOffers()
        }
    }

    fun addNeed(need: Need) {
        viewModelScope.launch {
            repository.addNeed(need)
            loadNeeds() // Recargar la lista
        }
    }

    fun addOffer(offer: Offer, imageUris: List<Uri>) {
        viewModelScope.launch {
            repository.addOffer(offer, imageUris)
            loadOffers() // Recargar la lista
        }
    }
}