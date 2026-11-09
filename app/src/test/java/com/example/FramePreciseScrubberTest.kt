package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.components.preview.FramePreciseScrubber
import com.example.ui.components.preview.TimecodeDisplayMode
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FramePreciseScrubberTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test scrubber renders with test tags and triggers frame stepping`() {
        var seekedPositionMs = -1L
        var steppedFramesDelta = 0
        var playPauseToggled = false

        composeTestRule.setContent {
            MyApplicationTheme {
                FramePreciseScrubber(
                    currentPositionMs = 1500L,
                    durationMs = 10000L,
                    isPlaying = false,
                    fps = 30,
                    trimStart = 0f,
                    trimEnd = 1f,
                    onSeekTo = { seekedPositionMs = it },
                    onPlayPauseToggle = { playPauseToggled = true },
                    onStepFrame = { steppedFramesDelta = it }
                )
            }
        }

        // Verify root scrubber exists
        composeTestRule.onNodeWithTag("frame_precise_scrubber_root").assertIsDisplayed()

        // Verify timecode display
        composeTestRule.onNodeWithTag("timecode_display_box").assertIsDisplayed()

        // Test step forward 1 frame button
        composeTestRule.onNodeWithTag("scrub_step_fwd_1_btn").performClick()
        assertEquals(1, steppedFramesDelta)

        // Test step backward 1 frame button
        composeTestRule.onNodeWithTag("scrub_step_back_1_btn").performClick()
        assertEquals(-1, steppedFramesDelta)

        // Test step forward 10 frames button
        composeTestRule.onNodeWithTag("scrub_step_fwd_10_btn").performClick()
        assertEquals(10, steppedFramesDelta)

        // Test step backward 10 frames button
        composeTestRule.onNodeWithTag("scrub_step_back_10_btn").performClick()
        assertEquals(-10, steppedFramesDelta)

        // Test play/pause button
        composeTestRule.onNodeWithTag("scrub_play_pause_btn").performClick()
        assertTrue(playPauseToggled)

        // Test skip to start
        composeTestRule.onNodeWithTag("scrub_skip_start_btn").performClick()
        assertEquals(0L, seekedPositionMs)

        // Test skip to end
        composeTestRule.onNodeWithTag("scrub_skip_end_btn").performClick()
        assertEquals(10000L, seekedPositionMs)
    }

    @Test
    fun `test timecode math for 30 fps and 60 fps`() {
        // 30 fps frame duration
        val fps30 = 30
        val frameDuration30 = 1000.0 / fps30
        assertEquals(33.333, frameDuration30, 0.01)

        // Frame at 1000ms is exactly 30 frames with rounding
        val frameAt1s = Math.round(1000.0 / frameDuration30).toInt()
        assertEquals(30, frameAt1s)

        // 60 fps frame duration
        val fps60 = 60
        val frameDuration60 = 1000.0 / fps60
        assertEquals(16.666, frameDuration60, 0.01)

        val frameAt1s_60 = Math.round(1000.0 / frameDuration60).toInt()
        assertEquals(60, frameAt1s_60)
    }

    @Test
    fun `test zoom controls dynamic scaling presets and minimap navigator`() {
        composeTestRule.setContent {
            MyApplicationTheme {
                FramePreciseScrubber(
                    currentPositionMs = 2000L,
                    durationMs = 10000L,
                    isPlaying = false,
                    fps = 30,
                    trimStart = 0f,
                    trimEnd = 1f,
                    onSeekTo = {},
                    onPlayPauseToggle = {},
                    onStepFrame = {}
                )
            }
        }

        // Verify initial zoom badge is 1.0x
        composeTestRule.onNodeWithTag("scrub_zoom_level_badge").assertIsDisplayed()
        composeTestRule.onNodeWithTag("scrub_zoom_level_badge").assertTextEquals("1.0x")

        // Minimap should NOT be visible at 1.0x
        composeTestRule.onNodeWithTag("scrub_minimap_navigator").assertDoesNotExist()

        // Switch to 2.5x
        composeTestRule.onNodeWithTag("scrub_zoom_2_5x_btn").performClick()
        composeTestRule.onNodeWithTag("scrub_zoom_level_badge").assertTextEquals("2.5x")
        // Minimap navigator should now be displayed
        composeTestRule.onNodeWithTag("scrub_minimap_navigator").assertIsDisplayed()

        // Switch to 5x
        composeTestRule.onNodeWithTag("scrub_zoom_5x_btn").performClick()
        composeTestRule.onNodeWithTag("scrub_zoom_level_badge").assertTextEquals("5.0x")

        // Switch to 10x (micro-frame precision)
        composeTestRule.onNodeWithTag("scrub_zoom_10x_btn").performClick()
        composeTestRule.onNodeWithTag("scrub_zoom_level_badge").assertTextEquals("10.0x")

        // Reset to FIT (1.0x)
        composeTestRule.onNodeWithTag("scrub_zoom_fit_btn").performClick()
        composeTestRule.onNodeWithTag("scrub_zoom_level_badge").assertTextEquals("1.0x")
        composeTestRule.onNodeWithTag("scrub_minimap_navigator").assertDoesNotExist()
    }

    @Test
    fun `test keyframe controls and snapping toggle`() {
        var selectedKeyframe: com.example.ui.components.preview.TimelineKeyframe? = null
        var keyframesList: List<com.example.ui.components.preview.TimelineKeyframe>? = null

        composeTestRule.setContent {
            MyApplicationTheme {
                FramePreciseScrubber(
                    currentPositionMs = 0L,
                    durationMs = 10000L,
                    isPlaying = false,
                    fps = 30,
                    trimStart = 0f,
                    trimEnd = 1f,
                    onSeekTo = {},
                    onPlayPauseToggle = {},
                    onStepFrame = {},
                    onKeyframesChange = { keyframesList = it },
                    onKeyframeSelected = { selectedKeyframe = it }
                )
            }
        }

        // Verify keyframe manager row is displayed
        composeTestRule.onNodeWithTag("scrub_keyframe_manager_row").assertIsDisplayed()

        // Toggle magnetic keyframe snapping
        composeTestRule.onNodeWithTag("scrub_toggle_snap_btn").assertIsDisplayed()
        composeTestRule.onNodeWithText("SNAP ON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("scrub_toggle_snap_btn").performClick()
        composeTestRule.onNodeWithText("SNAP OFF").assertIsDisplayed()

        // Test next keyframe navigation
        composeTestRule.onNodeWithTag("scrub_next_keyframe_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("scrub_next_keyframe_btn").performClick()

        // Test prev keyframe navigation
        composeTestRule.onNodeWithTag("scrub_prev_keyframe_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("scrub_prev_keyframe_btn").performClick()
    }
}
