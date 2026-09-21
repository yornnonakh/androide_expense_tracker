package com.example.fintrack.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.presentation.home.HomeScreen
import com.example.fintrack.presentation.addtransaction.TransactionFormRoute
import com.example.fintrack.presentation.transactiondetail.TransactionDetailRoute
import com.example.fintrack.presentation.transactions.TransactionsRoute
import com.example.fintrack.domain.model.TransactionType

@Composable
fun FinTrackApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isTopLevelDestination = topLevelDestinations.any { destination ->
        currentDestination?.hierarchy?.any {
            it.hasRoute(destination.route::class)
        } == true
    }

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(destination.route::class)
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                )
                            },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (isTopLevelDestination) {
                FloatingActionButton(
                    onClick = { navController.navigate(Destination.TransactionForm()) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add transaction",
                    )
                }
            }
        },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home,
            modifier = Modifier.padding(contentPadding),
        ) {
            composable<Destination.Home> {
                HomeScreen(
                    onAddIncome = {
                        navController.navigate(
                            Destination.TransactionForm(initialType = TransactionType.INCOME.name),
                        )
                    },
                    onAddExpense = {
                        navController.navigate(
                            Destination.TransactionForm(initialType = TransactionType.EXPENSE.name),
                        )
                    },
                )
            }
            composable<Destination.Transactions> {
                TransactionsRoute(
                    onTransactionClick = {
                        navController.navigate(Destination.TransactionDetail(it))
                    },
                )
            }
            composable<Destination.Budget> {
                PlaceholderScreen("Budget")
            }
            composable<Destination.Analytics> {
                PlaceholderScreen("Analytics")
            }
            composable<Destination.Settings> {
                PlaceholderScreen("Settings")
            }
            composable<Destination.TransactionForm> {
                TransactionFormRoute(
                    onBack = navController::popBackStack,
                    onSaved = navController::popBackStack,
                )
            }
            composable<Destination.TransactionDetail> {
                TransactionDetailRoute(
                    onBack = navController::popBackStack,
                    onEdit = {
                        navController.navigate(Destination.TransactionForm(transactionId = it))
                    },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) { Text(text = "$title is coming in a later phase") }
}
