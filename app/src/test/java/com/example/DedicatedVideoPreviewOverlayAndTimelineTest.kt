package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.data.colorgrading.ColorGradingPresets
import com.example.data.colorgrading.ColorGradingState
import com.example.ui.components.preview.DedicatedVideoPreviewOverlay
import com.example.ui.components.preview.DedicatedVideoTimeline
import com.example.ui.components.preview.TimelineEditMarker
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DedicatedVideoPreviewOverlayAndTimelineTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDedicatedVideoTimelineRendersAndRespondsToActions() {
        var playPauseClicked = false
        var steppedFrameDelta = 0
        var seekPositionTarget = -1L
        var splitPositionTarget = -1L
        var markerAddedPosition = -1L

        composeTestRule.setContent {
            MyApplicationTheme {
                DedicatedVideoTimeline(
                    currentPositionMs = 5000L,
                    durationMs = 20000L,
                    isPlaying = false,
                    fps = 30,
                    trimStart = 0.1f,
                    trimEnd = 0.9f,
                    onTrimRangeChange = { _, _ -> },
                    onSeekTo = { seekPositionTarget = it },
                    onPlayPauseToggle = { playPauseClicked = true },
                    onStepFrame = { steppedFrameDelta = it },
                    onSplitAtPlayhead = { splitPositionTarget = it },
                    onAddMarker = { markerAddedPosition = it },
                    markers = listOf(
                        TimelineEditMarker(positionMs = 3000L, label = "Color Grade", type = "COLOR_GRADE"),
                        TimelineEditMarker(positionMs = 12000L, label = "Reframe 9:16", type = "REFRAME")
                    )
                )
            }
        }

        // Verify root timeline is displayed
        composeTestRule.onNodeWithTag("dedicated_video_timeline").assertIsDisplayed()

        // Test Play/Pause toggle
        composeTestRule.onNodeWithTag("timeline_play_pause_btn").performClick()
        assertTrue("Play/Pause button should trigger callback", playPauseClicked)

        // Test Step -1 Frame
        composeTestRule.onNodeWithTag("timeline_step_back_btn").performClick()
        assertEquals(-1, steppedFrameDelta)

        // Test Step +1 Frame
        composeTestRule.onNodeWithTag("timeline_step_forward_btn").performClick()
        assertEquals(1, steppedFrameDelta)

        // Test Split clip at playhead
        composeTestRule.onNodeWithTag("timeline_split_clip_btn").performClick()
        assertEquals(5000L, splitPositionTarget)

        // Test Add Marker at playhead
        composeTestRule.onNodeWithTag("timeline_add_marker_btn").performClick()
        assertEquals(5000L, markerAddedPosition)

        // Test Jump to In point
        composeTestRule.onNodeWithTag("timeline_jump_in_btn").performClick()
        assertEquals((0.1f * 20000L).toLong(), seekPositionTarget)
    }

    @Test
    fun testDedicatedVideoPreviewOverlayRendersColorAndReframeControls() {
        var updatedColorState: ColorGradingState? = null
        var selectedAspect: String? = null
        var reframeToolToggled = false

        val initialColorState = ColorGradingState(
            selectedLut = ColorGradingPresets.KODAK_VISION3_5219,
            brightness = 0.05f,
            contrast = 1.15f,
            saturation = 1.1f,
            temperature = 0.2f,
            isEnabled = true
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    DedicatedVideoPreviewOverlay(
                        colorGradingState = initialColorState,
                        onColorGradingChange = { updatedColorState = it },
                        selectedAspectRatio = "16:9",
                        onAspectRatioChange = { selectedAspect = it },
                        reframePanX = 0f,
                        reframePanY = 0f,
                        onReframePanChange = { _, _ -> },
                        reframeZoom = 1.0f,
                        onReframeZoomChange = {},
                        currentPositionMs = 2500L,
                        durationMs = 10000L,
                        fps = 30
                    )
                }
            }
        }

        // Verify overlay and canvas layers
        composeTestRule.onNodeWithTag("dedicated_video_preview_overlay").assertIsDisplayed()
        composeTestRule.onNodeWithTag("color_grading_canvas_layer").assertIsDisplayed()
        composeTestRule.onNodeWithTag("reframe_matting_canvas").assertIsDisplayed()

        // Test Aspect Ratio chips
        composeTestRule.onNodeWithTag("reframe_aspect_ratio_chip_9:16").performClick()
        assertEquals("9:16", selectedAspect)

        composeTestRule.onNodeWithTag("reframe_aspect_ratio_chip_4:5").performClick()
        assertEquals("4:5", selectedAspect)

        // Test Reframe Tool Toggle
        composeTestRule.onNodeWithTag("reframe_tool_toggle_btn").performClick()
        composeTestRule.onNodeWithTag("reframe_pan_drag_surface").assertIsDisplayed()

        // Test Quick Color Tweak Drawer Button
        composeTestRule.onNodeWithTag("color_grading_quick_tweak_btn").performClick()
        composeTestRule.onNodeWithText("AJUSTES DE COLOR EN TIEMPO REAL").assertIsDisplayed()
    }
}
