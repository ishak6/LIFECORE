package com.lifecore.app

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainNavigationUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationOpensEveryPrimaryScreen() {
        composeRule.onNodeWithText("Goals").performClick()
        composeRule.onNodeWithText("Make progress that matters").assertExists()

        composeRule.onNodeWithText("Brain").performClick()
        composeRule.onNodeWithText("Your knowledge space").assertExists()

        composeRule.onNodeWithText("Plan").performClick()
        composeRule.onNodeWithText("Plan with intention").assertExists()

        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Your LIFECORE space").assertExists()

        composeRule.onNodeWithText("Home").performClick()
        composeRule.onNodeWithText("Your life, in one place.").assertExists()
    }

    @Test
    fun goalFormCreatesAndDisplaysSavedGoal() {
        val goalName = "UI test goal " + System.currentTimeMillis()
        composeRule.onNodeWithText("Goals").performClick()
        composeRule.onNodeWithText("New goal").performTextInput(goalName)
        composeRule.onNodeWithText("Add goal").performClick()
        composeRule.onNodeWithText(goalName).assertExists()
    }

    @Test
    fun profileSettingsSupportsSystemBackAndThemeSelection() {
        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Open settings").performClick()
        composeRule.onNodeWithText("Settings & Integrations").assertExists()

        composeRule.onNodeWithText("Dark").performClick()
        composeRule.onNodeWithText("Dark").assertIsSelected()

        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.onNodeWithText("Your LIFECORE space").assertExists()
    }
}
