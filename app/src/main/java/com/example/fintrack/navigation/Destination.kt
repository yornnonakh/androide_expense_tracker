package com.example.fintrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Home : Destination

    @Serializable
    data object Transactions : Destination

    @Serializable
    data object Budget : Destination

    @Serializable
    data object Analytics : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data class TransactionForm(
        val transactionId: Long? = null,
        val initialType: String? = null,
    ) : Destination

    @Serializable
    data class TransactionDetail(val transactionId: Long) : Destination
}

data class TopLevelDestination(
    val route: Destination,
    val label: String,
    val icon: ImageVector,
)

val topLevelDestinations = listOf(
    TopLevelDestination(Destination.Home, "Home", Icons.Default.Home),
    TopLevelDestination(Destination.Transactions, "Transactions", Icons.AutoMirrored.Filled.List),
    TopLevelDestination(Destination.Budget, "Budget", Icons.Default.Star),
    TopLevelDestination(Destination.Analytics, "Analytics", Icons.Default.Info),
    TopLevelDestination(Destination.Settings, "Settings", Icons.Default.Settings),
)
