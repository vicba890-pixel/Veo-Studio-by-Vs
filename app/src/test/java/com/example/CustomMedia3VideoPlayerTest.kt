package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.media3.common.Player
import com.example.ui.components.preview.CustomPlayerControlsOverlay
import com.example.ui.components.preview.PlayerScaleMode
import com.example.ui.components.preview.formatSmpteTimecode
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
class CustomMedia3VideoPlayerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFormatSmpteTimecodeFormatsAccurately() {
        assertEquals("00:00:00:00", formatSmpteTimecode(0L, 30))
        assertEquals("00:00:00:00", formatSmpteTimecode(-1000L, 30))
        // 1000ms at 30 fps = 1 second, 0 frames
        assertEquals("00:00:01:00", formatSmpteTimecode(1000L, 30))
        // 1500ms at 30 fps = 1 second, 15 frames
        assertEquals("00:00:01:15", formatSmpteTimecode(1500L, 30))
        // 65000ms = 1 min, 5 sec
        assertEquals("00:01:05:00", formatSmpteTimecode(65000L, 30))
    }

    @Test
    fun testCustomPlayerControlsOverlayRendersPlayPauseSeekAndFullscreen() {
        var playPauseClicked = false
        var seekStarted = false
        var seekChangedValue = 0L
        var seekFinishedValue = 0L
        var stepBackwardClicked = false
        var stepForwardClicked = false
        var rewind10sClicked = false
        var forward10sClicked = false
        var fullscreenClicked = false
        var muteClicked = false
        var loopClicked = false

        composeTestRule.setContent {
            MyApplicationTheme {
                CustomPlayerControlsOverlay(
                    title = "VMSB Cinematic Editor 4K",
                    isPlaying = false,
                    playbackState = Player.STATE_READY,
                    currentPositionMs = 15_000L,
                    durationMs = 60_000L,
                    bufferedPositionMs = 30_000L,
                    isMuted = false,
                    playbackSpeed = 1.0f,
                    isLooping = true,
                    scaleMode = PlayerScaleMode.FIT,
                    isFullscreen = false,
                    fps = 30,
                    onPlayPauseToggle = { playPauseClicked = true },
                    onSeekStarted = { seekStarted = true },
                    onSeekChanged = { seekChangedValue = it },
                    onSeekFinished = { seekFinishedValue = it },
                    onStepBackward = { stepBackwardClicked = true },
                    onStepForward = { stepForwardClicked = true },
                    onRewind10s = { rewind10sClicked = true },
                    onForward10s = { forward10sClicked = true },
                    onMuteToggle = { muteClicked = true },
                    onSpeedChange = {},
                    onLoopToggle = { loopClicked = true },
                    onScaleModeToggle = {},
                    onFullscreenToggle = { fullscreenClicked = true }
                )
            }
        }

        // 1. Verify Root Overlay & Title
        composeTestRule.onNodeWithTag("custom_media3_controls_overlay").assertIsDisplayed()
        composeTestRule.onNodeWithText("VMSB Cinematic Editor 4K").assertIsDisplayed()

        // 2. Verify Timecode Display
        composeTestRule.onNodeWithTag("custom_player_timecode_text").assertIsDisplayed()

        // 3. Verify and Click Center Play/Pause Button
        composeTestRule.onNodeWithTag("custom_player_center_play_btn").assertIsDisplayed().performClick()
        assertTrue("Play/Pause toggle callback should be triggered", playPauseClicked)

        // 4. Verify Bottom Play/Pause Button
        composeTestRule.onNodeWithTag("custom_player_bottom_play_btn").assertIsDisplayed()

        // 5. Verify and Click Step Backward -1 Frame & Step Forward +1 Frame Buttons
        composeTestRule.onNodeWithTag("custom_player_step_backward_btn").assertIsDisplayed().performClick()
        assertTrue("Step backward callback should be triggered", stepBackwardClicked)

        composeTestRule.onNodeWithTag("custom_player_step_forward_btn").assertIsDisplayed().performClick()
        assertTrue("Step forward callback should be triggered", stepForwardClicked)

        // 6. Verify and Click Rewind 10s and Forward 10s
        composeTestRule.onNodeWithTag("custom_player_rewind_10s_btn").assertIsDisplayed().performClick()
        assertTrue("Rewind 10s callback should be triggered", rewind10sClicked)

        composeTestRule.onNodeWithTag("custom_player_forward_10s_btn").assertIsDisplayed().performClick()
        assertTrue("Forward 10s callback should be triggered", forward10sClicked)

        // 7. Verify Seek Slider Exists
        composeTestRule.onNodeWithTag("custom_player_seek_slider").assertIsDisplayed()

        // 8. Verify and Click Fullscreen Toggle Button
        composeTestRule.onNodeWithTag("custom_player_fullscreen_btn").assertIsDisplayed().performClick()
        assertTrue("Fullscreen toggle callback should be triggered", fullscreenClicked)

        // 9. Verify Mute & Loop Toggles
        composeTestRule.onNodeWithTag("custom_player_mute_btn").assertIsDisplayed().performClick()
        assertTrue("Mute callback should be triggered", muteClicked)

        composeTestRule.onNodeWithTag("custom_player_loop_btn").assertIsDisplayed().performClick()
        assertTrue("Loop callback should be triggered", loopClicked)
    }

    @Test
    fun testFullscreenModeShowsExitFullscreenButton() {
        var exitFullscreenClicked = false

        composeTestRule.setContent {
            MyApplicationTheme {
                CustomPlayerControlsOverlay(
                    title = "Theater Preview",
                    isPlaying = true,
                    playbackState = Player.STATE_READY,
                    currentPositionMs = 5000L,
                    durationMs = 20000L,
                    bufferedPositionMs = 10000L,
                    isMuted = false,
                    playbackSpeed = 1.0f,
                    isLooping = false,
                    scaleMode = PlayerScaleMode.FIT,
                    isFullscreen = true,
                    fps = 24,
                    onPlayPauseToggle = {},
                    onSeekStarted = {},
                    onSeekChanged = {},
                    onSeekFinished = {},
                    onStepBackward = {},
                    onStepForward = {},
                    onRewind10s = {},
                    onForward10s = {},
                    onMuteToggle = {},
                    onSpeedChange = {},
                    onLoopToggle = {},
                    onScaleModeToggle = {},
                    onFullscreenToggle = { exitFullscreenClicked = true }
                )
            }
        }

        // In fullscreen mode, the top-left exit button must be displayed and trigger callback
        composeTestRule.onNodeWithTag("custom_player_exit_fullscreen_btn").assertIsDisplayed().performClick()
        assertTrue("Exit fullscreen callback should be triggered", exitFullscreenClicked)
    }
}
