package com.example.fintrack.presentation.transactiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.fintrack.domain.model.Category
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.repository.CategoryRepository
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TransactionDetailUiState(
    val transaction: Transaction? = null,
    val category: Category? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface TransactionDetailEvent {
    data object Deleted : TransactionDetailEvent
    data class Error(val message: String) : TransactionDetailEvent
}

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {
    private val transactionId = savedStateHandle.toRoute<Destination.TransactionDetail>().transactionId
    private val eventsChannel = Channel<TransactionDetailEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    val uiState = combine(
        transactionRepository.observeById(transactionId),
        categoryRepository.observeAll(),
    ) { transaction, categories ->
        TransactionDetailUiState(
            transaction = transaction,
            category = categories.firstOrNull { it.id == transaction?.categoryId },
            isLoading = false,
            errorMessage = if (transaction == null) "This transaction no longer exists." else null,
        )
    }.catch {
        emit(
            TransactionDetailUiState(
                isLoading = false,
                errorMessage = "Transaction details could not be loaded.",
            ),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TransactionDetailUiState(),
    )

    fun delete() {
        viewModelScope.launch {
            runCatching { transactionRepository.deleteById(transactionId) }
                .onSuccess { eventsChannel.send(TransactionDetailEvent.Deleted) }
                .onFailure {
                    eventsChannel.send(
                        TransactionDetailEvent.Error("Transaction could not be deleted. Please try again."),
                    )
                }
        }
    }
}
