package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.media3.common.Player
import com.example.ui.components.preview.Media3PlayerControlsOverlay
import com.example.ui.components.preview.PlayerScaleMode
import com.example.ui.components.preview.formatMediaTime
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
class Media3VideoPreviewPlayerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test formatMediaTime formats zero, seconds, minutes and hours correctly`() {
        assertEquals("00:00", formatMediaTime(0L))
        assertEquals("00:00", formatMediaTime(-1000L))
        assertEquals("00:45", formatMediaTime(45_000L))
        assertEquals("02:05", formatMediaTime(125_000L))
        assertEquals("01:01:05", formatMediaTime(3_665_000L))
    }

    @Test
    fun `test Media3PlayerControlsOverlay renders all standard controls and responds to clicks`() {
        var playPauseClicked = false
        var rewindClicked = false
        var forwardClicked = false
        var fullscreenClicked = false
        var muteClicked = false
        var loopClicked = false
        var scaleModeClicked = false

        composeTestRule.setContent {
            MyApplicationTheme {
                Media3PlayerControlsOverlay(
                    title = "Cinematic Veo 3.1 4K Demo",
                    isPlaying = false,
                    playbackState = Player.STATE_READY,
                    currentPositionMs = 30_000L,
                    durationMs = 90_000L,
                    bufferedPositionMs = 60_000L,
                    isMuted = false,
                    playbackSpeed = 1.0f,
                    isLooping = true,
                    scaleMode = PlayerScaleMode.FIT,
                    isFullscreen = false,
                    videoWidth = 1920,
                    videoHeight = 1080,
                    onPlayPauseToggle = { playPauseClicked = true },
                    onSeekStarted = {},
                    onSeekChanged = {},
                    onSeekFinished = {},
                    onRewind10s = { rewindClicked = true },
                    onForward10s = { forwardClicked = true },
                    onMuteToggle = { muteClicked = true },
                    onSpeedChange = {},
                    onLoopToggle = { loopClicked = true },
                    onScaleModeToggle = { scaleModeClicked = true },
                    onFullscreenToggle = { fullscreenClicked = true }
                )
            }
        }

        // Verify root controls overlay is displayed
        composeTestRule.onNodeWithTag("media3_player_controls_overlay").assertIsDisplayed()

        // Verify video title is displayed
        composeTestRule.onNodeWithText("Cinematic Veo 3.1 4K Demo").assertIsDisplayed()

        // Verify timecode shows current and total duration (00:30 / 01:30)
        composeTestRule.onNodeWithTag("media3_timecode_display").assertIsDisplayed()
        composeTestRule.onNodeWithText("00:30 / 01:30").assertIsDisplayed()

        // Verify and click play/pause in center HUD
        composeTestRule.onNodeWithTag("media3_center_play_btn").assertIsDisplayed().performClick()
        assertTrue("Play/pause callback should be triggered", playPauseClicked)

        // Verify and click rewind 10s button
        composeTestRule.onNodeWithTag("media3_rewind_10s_btn").assertIsDisplayed().performClick()
        assertTrue("Rewind 10s callback should be triggered", rewindClicked)

        // Verify and click forward 10s button
        composeTestRule.onNodeWithTag("media3_forward_10s_btn").assertIsDisplayed().performClick()
        assertTrue("Forward 10s callback should be triggered", forwardClicked)

        // Verify and click fullscreen toggle button
        composeTestRule.onNodeWithTag("media3_fullscreen_toggle_btn").assertIsDisplayed().performClick()
        assertTrue("Fullscreen toggle callback should be triggered", fullscreenClicked)

        // Verify and click mute toggle button
        composeTestRule.onNodeWithTag("media3_mute_toggle_btn").assertIsDisplayed().performClick()
        assertTrue("Mute toggle callback should be triggered", muteClicked)

        // Verify and click loop toggle button
        composeTestRule.onNodeWithTag("media3_loop_toggle_btn").assertIsDisplayed().performClick()
        assertTrue("Loop toggle callback should be triggered", loopClicked)

        // Verify and click aspect scale mode toggle button
        composeTestRule.onNodeWithTag("media3_scale_mode_btn").assertIsDisplayed().performClick()
        assertTrue("Scale mode toggle callback should be triggered", scaleModeClicked)
    }

    @Test
    fun `test Media3PlayerControlsOverlay in fullscreen mode displays exit fullscreen button`() {
        var fullscreenExited = false

        composeTestRule.setContent {
            MyApplicationTheme {
                Media3PlayerControlsOverlay(
                    title = "Fullscreen Stream",
                    isPlaying = true,
                    playbackState = Player.STATE_READY,
                    currentPositionMs = 15_000L,
                    durationMs = 60_000L,
                    bufferedPositionMs = 30_000L,
                    isMuted = true,
                    playbackSpeed = 1.5f,
                    isLooping = false,
                    scaleMode = PlayerScaleMode.FILL,
                    isFullscreen = true,
                    videoWidth = 3840,
                    videoHeight = 2160,
                    onPlayPauseToggle = {},
                    onSeekStarted = {},
                    onSeekChanged = {},
                    onSeekFinished = {},
                    onRewind10s = {},
                    onForward10s = {},
                    onMuteToggle = {},
                    onSpeedChange = {},
                    onLoopToggle = {},
                    onScaleModeToggle = {},
                    onFullscreenToggle = { fullscreenExited = true }
                )
            }
        }

        // Verify exit fullscreen button at top left exists and triggers exit callback
        composeTestRule.onNodeWithTag("media3_exit_fullscreen_btn").assertIsDisplayed().performClick()
        assertTrue("Exit fullscreen callback should be triggered", fullscreenExited)

        // Verify playback speed indicator reflects 1.5x
        composeTestRule.onNodeWithText("1.5x").assertIsDisplayed()
    }
}
