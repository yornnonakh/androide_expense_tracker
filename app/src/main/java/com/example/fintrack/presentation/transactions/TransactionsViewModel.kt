package com.example.fintrack.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.domain.model.Category
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.repository.CategoryRepository
import com.example.fintrack.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: Map<Long, Category> = emptyMap(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {
    val uiState = combine(
        transactionRepository.observeAll(),
        categoryRepository.observeAll(),
    ) { transactions, categories ->
        TransactionsUiState(
            transactions = transactions,
            categories = categories.associateBy(Category::id),
            isLoading = false,
        )
    }.catch {
        emit(
            TransactionsUiState(
                isLoading = false,
                errorMessage = "Transactions could not be loaded. Please try again.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransactionsUiState(),
    )
}
