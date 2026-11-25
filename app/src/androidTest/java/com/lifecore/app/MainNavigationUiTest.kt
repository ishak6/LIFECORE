package com.lifecore.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
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
        composeRule.onNodeWithText("Make progress that matters").assertIsDisplayed()

        composeRule.onNodeWithText("Brain").performClick()
        composeRule.onNodeWithText("Your knowledge space").assertIsDisplayed()

        composeRule.onNodeWithText("Plan").performClick()
        composeRule.onNodeWithText("Plan with intention").assertIsDisplayed()

        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Your LIFECORE space").assertIsDisplayed()

        composeRule.onNodeWithText("Home").performClick()
        composeRule.onNodeWithText("Your life, in one place.").assertIsDisplayed()
    }

    @Test
    fun goalFormCreatesAndDisplaysSavedGoal() {
        val goalName = "UI test goal " + System.currentTimeMillis()
        composeRule.onNodeWithText("Goals").performClick()
        composeRule.onNodeWithText("New goal").performTextInput(goalName)
        composeRule.onNodeWithText("Add goal").performClick()
        composeRule.onNodeWithText(goalName).assertIsDisplayed()
    }

    @Test
    fun taskCanBeCreatedEditedAndSaved() {
        val originalTitle = "UI task " + System.currentTimeMillis()
        val updatedTitle = "$originalTitle updated"
        composeRule.onNodeWithText("Plan").performClick()
        composeRule.onNodeWithText("Task name").performTextInput(originalTitle)
        composeRule.onNodeWithText("Save task").performClick()
        composeRule.onNodeWithText(originalTitle).assertIsDisplayed()
        composeRule.onNodeWithText("Edit").performClick()
        composeRule.onAllNodesWithText("Task name")[1].performTextInput(updatedTitle)
        composeRule.onNodeWithText("Save changes").performClick()
        composeRule.onNodeWithText(updatedTitle).assertIsDisplayed()
    }

    @Test
    fun goalsAndHabitsCanBeEditedAndPersistedOnScreen() {
        val suffix = System.currentTimeMillis().toString()
        val goalTitle = "UI goal $suffix"
        val habitTitle = "UI habit $suffix"
        composeRule.onNodeWithText("Goals").performClick()
        composeRule.onNodeWithText("New goal").performTextInput(goalTitle)
        composeRule.onNodeWithText("Add goal").performClick()
        composeRule.onNodeWithText(goalTitle).assertIsDisplayed()
        composeRule.onNodeWithText("Edit").performClick()
        composeRule.onNodeWithText("Goal name").performTextInput(" revised")
        composeRule.onNodeWithText("Save changes").performClick()
        composeRule.onNodeWithText("$goalTitle revised").assertIsDisplayed()

        composeRule.onNodeWithText("New habit").performTextInput(habitTitle)
        composeRule.onNodeWithText("Add habit").performClick()
        composeRule.onNodeWithText(habitTitle).assertIsDisplayed()
        composeRule.onAllNodesWithText("Edit")[1].performClick()
        composeRule.onNodeWithText("Habit name").performTextInput(" revised")
        composeRule.onNodeWithText("Save changes").performClick()
        composeRule.onNodeWithText("$habitTitle revised").assertIsDisplayed()
    }

    @Test
    fun allThemeModesCanBeSelectedAndPrimaryScreensRemainVisible() {
        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Open settings").performClick()
        composeRule.onNodeWithText("Settings & Integrations").assertIsDisplayed()

        listOf("Light", "Dark", "System").forEach { mode ->
            composeRule.onNodeWithText(mode).performClick()
            composeRule.onNodeWithText(mode).assertIsSelected()

            composeRule.onNodeWithText("Home").performClick()
            composeRule.onNodeWithText("Your life, in one place.").assertIsDisplayed()
            composeRule.onNodeWithText("Goals").performClick()
            composeRule.onNodeWithText("Make progress that matters").assertIsDisplayed()
            composeRule.onNodeWithText("Brain").performClick()
            composeRule.onNodeWithText("Your knowledge space").assertIsDisplayed()
            composeRule.onNodeWithText("Plan").performClick()
            composeRule.onNodeWithText("Plan with intention").assertIsDisplayed()
            composeRule.onNodeWithText("Profile").performClick()
            composeRule.onNodeWithText("Your LIFECORE space").assertIsDisplayed()
            composeRule.onNodeWithText("Open settings").performClick()
            composeRule.onNodeWithText("Settings & Integrations").assertIsDisplayed()
        }

        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.onNodeWithText("Your LIFECORE space").assertIsDisplayed()
    }
}
