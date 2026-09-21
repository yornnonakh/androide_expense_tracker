package com.example.fintrack.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fintrack.core.util.formatMoney
import com.example.fintrack.designsystem.theme.ExpenseColor
import com.example.fintrack.designsystem.theme.FinTrackSpacing
import com.example.fintrack.designsystem.theme.IncomeColor
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType

@Composable
fun TransactionItem(
    transaction: Transaction,
    categoryName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = FinTrackSpacing.large,
                vertical = FinTrackSpacing.medium,
            ),
            horizontalArrangement = Arrangement.spacedBy(FinTrackSpacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = categoryName.take(1).uppercase(),
                    modifier = Modifier.padding(FinTrackSpacing.medium),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Column(Modifier.weight(1f)) {
                Text(transaction.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = buildString {
                    append(if (transaction.type == TransactionType.INCOME) "+" else "−")
                    append(formatMoney(transaction.amountMinor))
                },
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.type == TransactionType.INCOME) IncomeColor else ExpenseColor,
            )
        }
    }
}
