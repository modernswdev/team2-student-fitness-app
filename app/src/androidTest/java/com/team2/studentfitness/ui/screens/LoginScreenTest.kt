package com.team2.studentfitness.ui.screens

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.team2.studentfitness.viewmodels.LoginViewModel
import com.team2.studentfitness.viewmodels.SecurePinManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var pinManager: SecurePinManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        pinManager = SecurePinManager(context)
        pinManager.clearPin()
        viewModel = LoginViewModel(pinManager)
    }

    @Test
    fun loginScreen_initialState_showsCreatePin() {
        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Create a Security PIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Set PIN & Continue").assertIsDisplayed()
    }

    @Test
    fun loginScreen_entersTooShortPin_showsError() {
        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("••••").performTextInput("123")
        composeTestRule.onNodeWithText("Set PIN & Continue").performClick()

        composeTestRule.onNodeWithText("PIN must be at least 4 digits.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_setsPinSuccessfully_callsOnLoginSuccess() {
        var successCalled = false
        composeTestRule.setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { successCalled = true }
            )
        }

        composeTestRule.onNodeWithText("••••").performTextInput("1234")
        composeTestRule.onNodeWithText("Set PIN & Continue").performClick()

        assert(successCalled)
    }

    @Test
    fun loginScreen_existingPin_showsEnterPin() {
        viewModel.setPin(1234)

        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Enter Your PIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun loginScreen_wrongPin_showsError() {
        viewModel.setPin(1234)

        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("••••").performTextInput("1111")
        composeTestRule.onNodeWithText("Sign In").performClick()

        composeTestRule.onNodeWithText("Incorrect PIN. Please try again.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_devMenuButton_isDisplayed() {
        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Open Dev Menu").assertIsDisplayed()
    }
}
