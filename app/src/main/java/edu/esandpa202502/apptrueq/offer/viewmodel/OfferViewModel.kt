package edu.esandpa202502.apptrueq.offer.viewmodel

import androidx.lifecycle.ViewModel
import edu.esandpa202502.apptrueq.model.Offer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para el feature Offer.
 * Expone un StateFlow<List<Offer>> con 3 ofertas sembradas + las nuevas que agregue el usuario.
 */
class OfferViewModel : ViewModel() {

    // --- Semilla de datos (3 ofertas simuladas, categorías distintas) ---
    private val seed: List<Offer> = listOf(
        Offer(
            id = 1,
            titulo = "Silla ergonómica",
            descripcion = "En buen estado, color negro ajustable.",
            categoria = "Hogar",
            imagenUrl = "https://via.placeholder.com/600x400?text=Silla+ergonomica"
        ),
        Offer(
            id = 2,
            titulo = "Libro Kotlin para Android",
            descripcion = "Edición reciente. Ideal para estudiantes y desarrolladores.",
            categoria = "Libros",
            imagenUrl = "https://via.placeholder.com/600x400?text=Libro+Kotlin"
        ),
        Offer(
            id = 3,
            titulo = "Cafetera Philips",
            descripcion = "Automática, poco uso, ideal para oficina.",
            categoria = "Servicios" /* o 'Electrodomésticos' si lo prefieres */,
            imagenUrl = "https://via.placeholder.com/600x400?text=Cafetera+Philips"
        )
    )

    // Fuente interna y flujo expuesto
    private val _offers = MutableStateFlow(seed)
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    // --- API de modificación en memoria (sin BD) ---

    /** Agrega una nueva oferta con id autoincremental. */
    fun addOffer(
        titulo: String,
        descripcion: String,
        categoria: String,
        imagenUrl: String
    ) {
        val nextId = (_offers.value.maxOfOrNull { it.id } ?: 0) + 1
        val nueva = Offer(
            id = nextId,
            titulo = titulo,
            descripcion = descripcion,
            categoria = categoria,
            imagenUrl = imagenUrl
        )
        _offers.value = _offers.value + nueva
    }

    /** Elimina una oferta por id. */
    fun deleteOffer(id: Int) {
        _offers.value = _offers.value.filterNot { it.id == id }
    }

    /** Actualiza una oferta por id; si no existe, no hace nada. */
    fun updateOffer(
        id: Int,
        titulo: String,
        descripcion: String,
        categoria: String,
        imagenUrl: String
    ) {
        _offers.value = _offers.value.map { offer ->
            if (offer.id == id) {
                offer.copy(
                    titulo = titulo,
                    descripcion = descripcion,
                    categoria = categoria,
                    imagenUrl = imagenUrl
                )
            } else offer
        }
    }
}
