package edu.esandpa202502.apptrueq.core.navigation

sealed class Routes(val route: String) {
    // Ruta principal de Ofertas (pantalla con tabs)
    object Offer : Routes("offer")

    // 🔧 Stub para Explore → Detalle de publicación
    object PublicationDetail : Routes("publicationDetail/{id}") {
        fun createRoute(id: String): String = "publicationDetail/$id"
    }
}
