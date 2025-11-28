package edu.esandpa202502.apptrueq.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.notification.repository.NotificationRepository
import edu.esandpa202502.apptrueq.proposal.repository.ProposalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null
)

data class NotificationDetailUiState(
    val isLoading: Boolean = true,
    val proposal: Proposal? = null,
    val error: String? = null,
    val actionCompleted: Boolean = false
)

class NotificationViewModel : ViewModel() {

    private val notificationRepository = NotificationRepository()
    private val proposalsRepository = ProposalsRepository()

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(NotificationDetailUiState())
    val detailUiState: StateFlow<NotificationDetailUiState> = _detailUiState.asStateFlow()

    fun listenForNotifications(userId: String) {
        viewModelScope.launch {
            notificationRepository.getNotifications(userId)
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { notifications ->
                    val sortedNotifications = notifications.sortedByDescending { it.createdAt }
                    _uiState.value = _uiState.value.copy(isLoading = false, notifications = sortedNotifications)
                }
        }
    }

    fun loadProposal(proposalId: String) {
        viewModelScope.launch {
            _detailUiState.value = NotificationDetailUiState(isLoading = true)
            try {
                val proposal = proposalsRepository.getProposalById(proposalId)
                _detailUiState.value = NotificationDetailUiState(isLoading = false, proposal = proposal)
            } catch (e: Exception) {
                _detailUiState.value = NotificationDetailUiState(isLoading = false, error = "Error al cargar la propuesta.")
            }
        }
    }

    fun acceptProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalsRepository.updateProposalStatus(proposal.id, "ACEPTADA")
                notificationRepository.createNotification(
                    NotificationItem(
                        title = "¡Tu propuesta ha sido aceptada!",
                        message = "El dueño de '${proposal.publicationTitle}' ha aceptado tu propuesta.",
                        userId = proposal.proposerId,
                        type = "proposal_accepted",
                        referenceId = proposal.id
                    )
                )
                _detailUiState.value = _detailUiState.value.copy(actionCompleted = true)
            } catch (e: Exception) {
                 _detailUiState.value = _detailUiState.value.copy(error = "Error al aceptar la propuesta.")
            }
        }
    }

    fun rejectProposal(proposal: Proposal) {
        viewModelScope.launch {
             try {
                proposalsRepository.updateProposalStatus(proposal.id, "RECHAZADA")
                notificationRepository.createNotification(
                    NotificationItem(
                        title = "Tu propuesta ha sido rechazada",
                        message = "El dueño de '${proposal.publicationTitle}' ha rechazado tu propuesta.",
                        userId = proposal.proposerId,
                        type = "proposal_rejected",
                        referenceId = proposal.id
                    )
                )
                _detailUiState.value = _detailUiState.value.copy(actionCompleted = true)
            } catch (e: Exception) {
                 _detailUiState.value = _detailUiState.value.copy(error = "Error al rechazar la propuesta.")
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.deleteNotification(notificationId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al eliminar la notificación: ${e.message}")
            }
        }
    }
}