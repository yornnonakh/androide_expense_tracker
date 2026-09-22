package com.example.fintrack.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fintrack.designsystem.theme.FinTrackSpacing
import com.example.fintrack.designsystem.theme.FinTrackTheme

@Composable
fun HomeScreen(
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = FinTrackSpacing.large),
    ) {
        Spacer(Modifier.height(FinTrackSpacing.extraLarge))
        Text(
            text = "Good morning",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Your finances",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(FinTrackSpacing.extraLarge))

        BalanceCard()
        Spacer(Modifier.height(FinTrackSpacing.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FinTrackSpacing.medium),
        ) {
            Button(
                onClick = onAddIncome,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Income")
            }
            OutlinedButton(
                onClick = onAddExpense,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Expense")
            }
        }

        Spacer(Modifier.height(FinTrackSpacing.extraLarge))
        Text(
            text = "Recent transactions",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(FinTrackSpacing.medium))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(Modifier.padding(FinTrackSpacing.extraLarge)) {
                Text(
                    text = "No transactions yet",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(FinTrackSpacing.small))
                Text(
                    text = "Add your first income or expense to start tracking your money.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(FinTrackSpacing.huge))
    }
}

@Composable
private fun BalanceCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(Modifier.padding(FinTrackSpacing.extraLarge)) {
            Text(
                text = "Total balance",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(FinTrackSpacing.small))
            Text(
                text = "—",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(FinTrackSpacing.extraLarge))
            Text(
                text = "Your live balance will appear after the local database is connected.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FinTrackTheme {
        Surface {
            HomeScreen(
                onAddIncome = {},
                onAddExpense = {},
            )
        }
    }
}
