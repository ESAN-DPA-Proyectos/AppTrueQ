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

sealed class OfferState {
    object Idle : OfferState()
    object Loading : OfferState()
    data class Success(val offer: Offer) : OfferState()
    data class Error(val message: String) : OfferState()
}

class OfferViewModel(
    private val repository: OfferRepository = OfferRepository()
) : ViewModel() {

    private val _offerState = MutableStateFlow<OfferState>(OfferState.Idle)
    val offerState: StateFlow<OfferState> = _offerState

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers

    /**
     * Publicar una oferta nueva (HU-03).
     */
    fun publishOffer(
        title: String,
        offerText: String,
        needText: String,
        category: String,
        imageUris: List<Uri>
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return setError("Usuario no autenticado.")

        // Validaciones HU-03
        when {
            title.isBlank() -> return setError("El título es obligatorio.")
            title.length < 5 -> return setError("El título debe tener al menos 5 caracteres.")
            offerText.isBlank() -> return setError("La descripción es obligatoria.")
            offerText.length < 20 -> return setError("La descripción debe tener al menos 20 caracteres.")
            category.isBlank() -> return setError("Debe seleccionar una categoría.")
            imageUris.isEmpty() -> return setError("Debe subir al menos una foto.")
        }

        val offer = Offer(
            id = "",
            title = title,
            offerText = offerText,
            needText = needText,
            category = category,
            status = "ACTIVE",
            ownerId = userId,
            ownerName = user.displayName ?: "",
            photos = emptyList(),
            createdAt = null
        )

        viewModelScope.launch {
            try {
                _offerState.value = OfferState.Loading
                val created = repository.addOffer(offer, imageUris)
                _offerState.value = OfferState.Success(created)
                loadMyOffers()
            } catch (e: Exception) {
                setError(e.message ?: "Error al publicar la oferta.")
            }
        }
    }

    /**
     * Cargar "Mis publicaciones".
     */
    fun loadMyOffers() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _offers.value = repository.getOffersByUser(userId)
        }
    }

    /**
     * Actualizar una oferta existente. newImageUris permite reemplazar las fotos.
     */
    fun updateOffer(offer: Offer, newImageUris: List<Uri>) {
        viewModelScope.launch {
            try {
                repository.updateOffer(offer, newImageUris)
                loadMyOffers()
            } catch (e: Exception) {
                setError(e.message ?: "Error al actualizar la oferta.")
            }
        }
    }

    /**
     * Eliminar una oferta.
     */
    fun deleteOffer(offerId: String) {
        viewModelScope.launch {
            try {
                repository.deleteOffer(offerId)
                loadMyOffers()
            } catch (e: Exception) {
                setError(e.message ?: "Error al eliminar la oferta.")
            }
        }
    }

    fun resetState() {
        _offerState.value = OfferState.Idle
    }

    private fun setError(msg: String) {
        _offerState.value = OfferState.Error(msg)
    }
}
