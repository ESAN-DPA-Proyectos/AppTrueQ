package edu.esandpa202502.apptrueq.offer.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.repository.offer.OfferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OfferViewModel : ViewModel() {

    private val repository = OfferRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers

    init {
        // Al iniciar, cargamos solo las ofertas del usuario actual
        loadMyOffers()
    }

    // Carga solo las ofertas del usuario logueado
    private fun loadMyOffers() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                _offers.value = repository.getMyOffers(userId)
            }
        }
    }

    fun addOffer(offer: Offer, imageUris: List<Uri>) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val offerWithOwner = offer.copy(ownerId = userId)
                repository.addOffer(offerWithOwner, imageUris)
                loadMyOffers() // Recargamos solo la lista del usuario
            }
        }
    }
}