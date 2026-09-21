package com.example.fintrack.presentation.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.fintrack.designsystem.theme.FinTrackTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyDashboard_showsPrimaryActionsAndGuidance() {
        composeRule.setContent {
            FinTrackTheme {
                HomeScreen(
                    onAddIncome = {},
                    onAddExpense = {},
                )
            }
        }

        composeRule.onNodeWithText("Income").assertIsDisplayed()
        composeRule.onNodeWithText("Expense").assertIsDisplayed()
        composeRule.onNodeWithText("No transactions yet").assertIsDisplayed()
    }
}
