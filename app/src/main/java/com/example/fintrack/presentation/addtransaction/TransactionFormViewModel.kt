package com.example.fintrack.presentation.addtransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.fintrack.domain.model.Category
import com.example.fintrack.domain.model.PaymentMethod
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.CategoryRepository
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.domain.usecase.TransactionInputErrors
import com.example.fintrack.domain.usecase.ValidateTransactionInput
import com.example.fintrack.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class TransactionFormUiState(
    val transactionId: Long? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val title: String = "",
    val categoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    val occurredAt: Instant = Instant.now(),
    val note: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val errors: TransactionInputErrors = TransactionInputErrors(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val loadError: String? = null,
) {
    val isEditing: Boolean get() = transactionId != null
}

sealed interface TransactionFormEvent {
    data object Saved : TransactionFormEvent
    data class Error(val message: String) : TransactionFormEvent
}

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val validateInput: ValidateTransactionInput,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Destination.TransactionForm>()
    private val mutableUiState = MutableStateFlow(
        TransactionFormUiState(
            transactionId = route.transactionId,
            type = route.initialType?.let { runCatching { TransactionType.valueOf(it) }.getOrNull() }
                ?: TransactionType.EXPENSE,
        ),
    )
    val uiState: StateFlow<TransactionFormUiState> = mutableUiState.asStateFlow()

    private val eventsChannel = Channel<TransactionFormEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private var original: Transaction? = null
    private var allCategories: List<Category> = emptyList()

    init {
        viewModelScope.launch {
            runCatching {
                allCategories = categoryRepository.observeAll().first()
                val existing = route.transactionId?.let {
                    transactionRepository.observeById(it).first()
                        ?: error("Transaction does not exist")
                }
                original = existing
                val currentType = existing?.type ?: mutableUiState.value.type
                mutableUiState.value = mutableUiState.value.copy(
                    type = currentType,
                    amount = existing?.amountMinor?.let(::minorUnitsToInput).orEmpty(),
                    title = existing?.title.orEmpty(),
                    categoryId = existing?.categoryId,
                    categories = categoriesFor(currentType),
                    occurredAt = existing?.occurredAt ?: Instant.now(),
                    note = existing?.note.orEmpty(),
                    paymentMethod = existing?.paymentMethod ?: PaymentMethod.CASH,
                    isLoading = false,
                )
            }.onFailure {
                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    loadError = "The transaction form could not be loaded.",
                )
            }
        }
    }

    fun setType(type: TransactionType) {
        mutableUiState.value = mutableUiState.value.copy(
            type = type,
            categoryId = null,
            categories = categoriesFor(type),
            errors = mutableUiState.value.errors.copy(category = null),
        )
    }

    fun setAmount(value: String) {
        if (value.length <= 18 && value.all { it.isDigit() || it == '.' || it == ',' }) {
            mutableUiState.value = mutableUiState.value.copy(
                amount = value,
                errors = mutableUiState.value.errors.copy(amount = null),
            )
        }
    }

    fun setTitle(value: String) {
        if (value.length <= 80) {
            mutableUiState.value = mutableUiState.value.copy(
                title = value,
                errors = mutableUiState.value.errors.copy(title = null),
            )
        }
    }

    fun setCategory(id: Long) {
        mutableUiState.value = mutableUiState.value.copy(
            categoryId = id,
            errors = mutableUiState.value.errors.copy(category = null),
        )
    }

    fun setDate(instant: Instant) {
        mutableUiState.value = mutableUiState.value.copy(occurredAt = instant)
    }

    fun setNote(value: String) {
        if (value.length <= 500) mutableUiState.value = mutableUiState.value.copy(note = value)
    }

    fun setPaymentMethod(value: PaymentMethod) {
        mutableUiState.value = mutableUiState.value.copy(paymentMethod = value)
    }

    fun save() {
        val state = mutableUiState.value
        val (validated, errors) = validateInput.validate(
            amount = state.amount,
            title = state.title,
            categoryId = state.categoryId,
        )
        if (validated == null) {
            mutableUiState.value = state.copy(errors = errors)
            return
        }

        mutableUiState.value = state.copy(isSaving = true, errors = errors)
        viewModelScope.launch {
            val now = Instant.now()
            val transaction = Transaction(
                id = state.transactionId ?: 0,
                title = validated.title,
                amountMinor = validated.amountMinor,
                type = state.type,
                categoryId = requireNotNull(state.categoryId),
                paymentMethod = state.paymentMethod,
                note = state.note.trim().ifEmpty { null },
                occurredAt = state.occurredAt,
                createdAt = original?.createdAt ?: now,
                updatedAt = now,
            )
            runCatching { transactionRepository.save(transaction) }
                .onSuccess { eventsChannel.send(TransactionFormEvent.Saved) }
                .onFailure {
                    mutableUiState.value = mutableUiState.value.copy(isSaving = false)
                    eventsChannel.send(
                        TransactionFormEvent.Error("Transaction could not be saved. Please try again."),
                    )
                }
        }
    }

    private fun categoriesFor(type: TransactionType) = allCategories.filter { it.type == type }

    private fun minorUnitsToInput(value: Long): String =
        BigDecimal.valueOf(value, 2).stripTrailingZeros().toPlainString()
}
