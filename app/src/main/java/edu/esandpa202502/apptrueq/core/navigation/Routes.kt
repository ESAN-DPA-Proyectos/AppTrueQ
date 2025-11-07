package edu.esandpa202502.apptrueq.core.navigation

sealed class Routes(val route: String) {
    object Explore : Routes("explore")
    object PublicationDetail : Routes("publicationDetail/{publicationId}") {
        fun createRoute(publicationId: String) = "publicationDetail/$publicationId"
    }
}