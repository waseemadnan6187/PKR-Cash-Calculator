package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.components.TopSummaryCard
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CashCounterUiState
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun cash_summary_screenshot() {
        val sampleState = CashCounterUiState(
            noteCounts = mapOf(
                5000 to 10,
                1000 to 25,
                500 to 100,
                100 to 50,
                75 to 0,
                50 to 20,
                20 to 10,
                10 to 0
            )
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                TopSummaryCard(
                    uiState = sampleState,
                    onClearAll = {},
                    onSaveClick = {},
                    onShareClick = {},
                    onTargetClick = {},
                    onToggleCoins = {},
                    onToggleFormat = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
