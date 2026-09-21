package com.example.fintrack.presentation.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fintrack.designsystem.theme.FinTrackSpacing
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.presentation.components.TransactionItem
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TransactionsRoute(
    onTransactionClick: (Long) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TransactionsScreen(state, onTransactionClick)
}

@Composable
fun TransactionsScreen(
    state: TransactionsUiState,
    onTransactionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        state.errorMessage != null -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(state.errorMessage, modifier = Modifier.padding(FinTrackSpacing.extraLarge))
        }
        state.transactions.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(FinTrackSpacing.small),
                modifier = Modifier.padding(FinTrackSpacing.extraLarge),
            ) {
                Text("No transactions yet", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Add your first income or expense to start tracking your money.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        else -> TransactionGroups(state, onTransactionClick, modifier)
    }
}

@Composable
private fun TransactionGroups(
    state: TransactionsUiState,
    onTransactionClick: (Long) -> Unit,
    modifier: Modifier,
) {
    val zone = ZoneId.systemDefault()
    val groups = state.transactions.groupBy { it.occurredAt.atZone(zone).toLocalDate() }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = FinTrackSpacing.large),
    ) {
        item {
            Text(
                "Transactions",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(
                    horizontal = FinTrackSpacing.large,
                    vertical = FinTrackSpacing.small,
                ),
            )
        }
        groups.forEach { (date, transactions) ->
            item(key = "header-$date") {
                Text(
                    date.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = FinTrackSpacing.large,
                        vertical = FinTrackSpacing.small,
                    ),
                )
            }
            items(transactions, key = Transaction::id) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    categoryName = state.categories[transaction.categoryId]?.name ?: "Category",
                    onClick = { onTransactionClick(transaction.id) },
                )
            }
        }
    }
}
