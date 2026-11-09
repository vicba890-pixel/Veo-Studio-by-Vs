package com.example

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.data.multitrack.*
import com.example.data.multitrack.command.*
import com.example.ui.components.preview.MultiTrackOverlayRenderer
import com.example.ui.components.preview.MultiTrackTimelineView
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MultiTrackArchitectureTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test default composition initializes four core tracks`() {
        val durationMs = 12000L
        val composition = MultiTrackStateManager.createDefaultComposition(durationMs, "Sunset Horizon")

        assertEquals(4, composition.tracks.size)
        val trackTypes = composition.tracks.map { it.type }
        assertTrue(trackTypes.contains(TrackType.VIDEO))
        assertTrue(trackTypes.contains(TrackType.TEXT))
        assertTrue(trackTypes.contains(TrackType.AUDIO))
        assertTrue(trackTypes.contains(TrackType.EFFECT))

        // Check active text clips at timestamp 1000ms
        val activeTexts = composition.getActiveTextClips(1000L)
        assertTrue(activeTexts.isNotEmpty())
        assertEquals(TrackType.TEXT, activeTexts.first().trackType)

        // Check active audio clips at timestamp 1000ms
        val activeAudios = composition.getActiveAudioClips(1000L)
        assertTrue(activeAudios.isNotEmpty())
        assertEquals(TrackType.AUDIO, activeAudios.first().trackType)

        // Check active effect clips at timestamp 1000ms
        val activeEffects = composition.getActiveEffectClips(1000L)
        assertTrue(activeEffects.isNotEmpty())
        assertEquals(TrackType.EFFECT, activeEffects.first().trackType)
    }

    @Test
    fun `test track mute and visibility filters active clips`() {
        val composition = MultiTrackStateManager.createDefaultComposition(10000L)
        val textTrack = composition.tracks.first { it.type == TrackType.TEXT }
        val audioTrack = composition.tracks.first { it.type == TrackType.AUDIO }

        // Initial check: active clips present
        assertTrue(composition.getActiveTextClips(500L).isNotEmpty())
        assertTrue(composition.getActiveAudioClips(500L).isNotEmpty())

        // Hide text track
        val hiddenTextComp = MultiTrackStateManager.toggleTrackVisibility(composition, textTrack.id)
        assertTrue(hiddenTextComp.findTrack(textTrack.id)!!.isHidden)
        assertTrue(hiddenTextComp.getActiveTextClips(500L).isEmpty())

        // Mute audio track
        val mutedAudioComp = MultiTrackStateManager.toggleTrackMute(composition, audioTrack.id)
        assertTrue(mutedAudioComp.findTrack(audioTrack.id)!!.isMuted)
        assertTrue(mutedAudioComp.getActiveAudioClips(500L).isEmpty())
    }

    @Test
    fun `test add, update, duplicate, and remove clip mutations`() {
        var comp = MultiTrackStateManager.createDefaultComposition(10000L)
        val textTrack = comp.tracks.first { it.type == TrackType.TEXT }
        val initialClipCount = textTrack.clips.size

        // 1. Add clip
        val newClip = TimelineClip(
            trackId = textTrack.id,
            trackType = TrackType.TEXT,
            label = "Special Quote",
            startMs = 5000L,
            durationMs = 2000L,
            colorHex = 0xFFF59E0B,
            textPayload = TextOverlayPayload(text = "Hello Veo")
        )
        comp = MultiTrackStateManager.addClip(comp, textTrack.id, newClip)
        assertEquals(initialClipCount + 1, comp.findTrack(textTrack.id)!!.clips.size)
        assertEquals(newClip.id, comp.selectedClipId)

        // 2. Update clip
        val updated = newClip.copy(label = "Updated Quote", durationMs = 3000L)
        comp = MultiTrackStateManager.updateClip(comp, updated)
        assertEquals("Updated Quote", comp.findClip(newClip.id)!!.label)
        assertEquals(3000L, comp.findClip(newClip.id)!!.durationMs)

        // 3. Duplicate clip
        comp = MultiTrackStateManager.duplicateClip(comp, newClip.id, offsetMs = 200L, maxDurationMs = 15000L)
        assertEquals(initialClipCount + 2, comp.findTrack(textTrack.id)!!.clips.size)

        // 4. Remove clip
        comp = MultiTrackStateManager.removeClip(comp, newClip.id)
        assertNull(comp.findClip(newClip.id))
    }

    @Test
    fun `test split clip at playhead divides into two sequential parts`() {
        var comp = MultiTrackStateManager.createDefaultComposition(10000L)
        val fxTrack = comp.tracks.first { it.type == TrackType.EFFECT }
        val targetClip = fxTrack.clips.first()

        val splitTime = targetClip.startMs + targetClip.durationMs / 2
        comp = MultiTrackStateManager.splitClipAt(comp, targetClip.id, splitTime)

        val updatedFxTrack = comp.findTrack(fxTrack.id)!!
        val pt1 = updatedFxTrack.clips.firstOrNull { it.id == targetClip.id }
        assertNotNull(pt1)
        assertEquals(splitTime - targetClip.startMs, pt1!!.durationMs)

        val pt2 = updatedFxTrack.clips.firstOrNull { it.startMs == splitTime }
        assertNotNull(pt2)
        assertEquals(targetClip.endMs - splitTime, pt2!!.durationMs)
    }

    @Test
    fun `test magnetic snapping timestamp calculations`() {
        val comp = MultiTrackStateManager.createDefaultComposition(10000L)
        val targetClip = comp.tracks.first { it.type == TrackType.TEXT }.clips.first()

        // Test snap near clip start
        val nearStart = targetClip.startMs + 50L
        val snappedStart = MultiTrackStateManager.calculateSnappedTimestamp(nearStart, comp, thresholdMs = 100L)
        assertEquals(targetClip.startMs, snappedStart)

        // Test snap near clip end
        val nearEnd = targetClip.endMs - 40L
        val snappedEnd = MultiTrackStateManager.calculateSnappedTimestamp(nearEnd, comp, thresholdMs = 100L)
        assertEquals(targetClip.endMs, snappedEnd)

        // Test far from any boundary does not snap
        val farTimestamp = 99999L
        val notSnapped = MultiTrackStateManager.calculateSnappedTimestamp(farTimestamp, comp, thresholdMs = 50L)
        assertEquals(farTimestamp, notSnapped)
    }

    @Test
    fun `test MultiTrackTimelineView renders toolbar and responds to actions`() {
        var compositionState = MultiTrackStateManager.createDefaultComposition(10000L)
        var seekedMs = -1L

        composeTestRule.setContent {
            MyApplicationTheme {
                MultiTrackTimelineView(
                    composition = compositionState,
                    currentPositionMs = 2000L,
                    durationMs = 10000L,
                    onSeekTo = { seekedMs = it },
                    onCompositionChange = { compositionState = it }
                )
            }
        }

        // Verify root container and badge
        composeTestRule.onNodeWithTag("multi_track_timeline_root").assertIsDisplayed()
        composeTestRule.onNodeWithTag("multi_track_badge").assertIsDisplayed()

        // Test toggle magnetic snap button
        val initialSnap = compositionState.isSnappingEnabled
        composeTestRule.onNodeWithTag("multi_track_snap_toggle_btn").performClick()
        assertEquals(!initialSnap, compositionState.isSnappingEnabled)

        // Test add text track button
        val textTrack = compositionState.tracks.first { it.type == TrackType.TEXT }
        val prevTextCount = textTrack.clips.size
        composeTestRule.onNodeWithTag("add_text_track_btn").performClick()
        val postTextCount = compositionState.tracks.first { it.type == TrackType.TEXT }.clips.size
        assertEquals(prevTextCount + 1, postTextCount)

        // Test add audio track button
        val audioTrack = compositionState.tracks.first { it.type == TrackType.AUDIO }
        val prevAudioCount = audioTrack.clips.size
        composeTestRule.onNodeWithTag("add_audio_track_btn").performClick()
        val postAudioCount = compositionState.tracks.first { it.type == TrackType.AUDIO }.clips.size
        assertEquals(prevAudioCount + 1, postAudioCount)

        // Test add FX track button
        val fxTrack = compositionState.tracks.first { it.type == TrackType.EFFECT }
        val prevFxCount = fxTrack.clips.size
        composeTestRule.onNodeWithTag("add_fx_track_btn").performClick()
        val postFxCount = compositionState.tracks.first { it.type == TrackType.EFFECT }.clips.size
        assertEquals(prevFxCount + 1, postFxCount)
    }

    @Test
    fun `test MultiTrackOverlayRenderer renders active text and effect nodes`() {
        val testTextClip = TimelineClip(
            id = "test_txt_1",
            trackId = "track_t1",
            trackType = TrackType.TEXT,
            label = "Sample Text",
            startMs = 0L,
            durationMs = 5000L,
            colorHex = 0xFFF59E0B,
            textPayload = TextOverlayPayload(text = "DYNAMIC OVERLAY ACTIVE", position = TextPosition.TOP)
        )

        val testFxClip = TimelineClip(
            id = "test_fx_1",
            trackId = "track_fx1",
            trackType = TrackType.EFFECT,
            label = "Teal Orange",
            startMs = 0L,
            durationMs = 5000L,
            colorHex = 0xFFA855F7,
            effectPayload = EffectClipPayload(effectType = EffectType.CINEMATIC_TEAL_ORANGE)
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                MultiTrackOverlayRenderer(
                    activeTextClips = listOf(testTextClip),
                    activeEffectClips = listOf(testFxClip),
                    currentPositionMs = 1500L,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Verify overlay renderer and specific nodes exist
        composeTestRule.onNodeWithTag("multi_track_overlay_renderer").assertIsDisplayed()
        composeTestRule.onNodeWithTag("active_fx_teal_orange").assertIsDisplayed()
        composeTestRule.onNodeWithTag("active_text_overlay_${testTextClip.id}").assertIsDisplayed()
    }

    @Test
    fun `test command pattern undo and redo lifecycle with MultiTrackHistoryManager`() {
        val initialComp = MultiTrackStateManager.createDefaultComposition(10000L)
        val history = MultiTrackHistoryManager(maxHistorySize = 20)
        val textTrack = initialComp.tracks.first { it.type == TrackType.TEXT }

        assertFalse(history.canUndo)
        assertFalse(history.canRedo)

        val newClip = TimelineClip(
            trackId = textTrack.id,
            trackType = TrackType.TEXT,
            label = "Command Test",
            startMs = 1000L,
            durationMs = 2000L,
            colorHex = 0xFFF59E0B
        )
        val addCmd = MultiTrackCommandFactory.createAddClipCommand(textTrack.id, newClip)
        val stateAfterAdd = history.execute(addCmd, initialComp)

        assertTrue(history.canUndo)
        assertFalse(history.canRedo)
        assertEquals(addCmd.title, history.lastActionTitle)
        assertNotNull(stateAfterAdd.findClip(newClip.id))

        // Undo
        val undoResult = history.undo(stateAfterAdd)
        assertNotNull(undoResult)
        val (stateAfterUndo, _) = undoResult!!
        assertFalse(history.canUndo)
        assertTrue(history.canRedo)
        assertEquals(addCmd.title, history.nextRedoTitle)
        assertNull(stateAfterUndo.findClip(newClip.id))

        // Redo
        val redoResult = history.redo(stateAfterUndo)
        assertNotNull(redoResult)
        val (stateAfterRedo, _) = redoResult!!
        assertTrue(history.canUndo)
        assertFalse(history.canRedo)
        assertNotNull(stateAfterRedo.findClip(newClip.id))
    }

    @Test
    fun `test MultiTrackTimelineView undo redo and history dialog display`() {
        var compositionState = MultiTrackStateManager.createDefaultComposition(10000L)
        var undoInvoked = false
        var redoInvoked = false

        composeTestRule.setContent {
            MyApplicationTheme {
                MultiTrackTimelineView(
                    composition = compositionState,
                    currentPositionMs = 1000L,
                    durationMs = 10000L,
                    canUndo = true,
                    canRedo = true,
                    undoDescription = "Add Text",
                    redoDescription = "Split Clip",
                    undoHistoryList = listOf("Add Text", "Mute Audio"),
                    redoHistoryList = listOf("Split Clip"),
                    onUndo = { undoInvoked = true },
                    onRedo = { redoInvoked = true },
                    onSeekTo = {},
                    onCompositionChange = { compositionState = it }
                )
            }
        }

        // Verify undo and redo buttons are displayed and trigger callbacks
        composeTestRule.onNodeWithTag("multi_track_undo_btn").assertIsDisplayed().performClick()
        assertTrue(undoInvoked)

        composeTestRule.onNodeWithTag("multi_track_redo_btn").assertIsDisplayed().performClick()
        assertTrue(redoInvoked)

        // Open history dialog
        composeTestRule.onNodeWithTag("multi_track_history_toggle_btn").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("multi_track_history_dialog").assertIsDisplayed()
    }
}
