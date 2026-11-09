package com.example

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.media.*
import com.example.data.multitrack.*
import com.example.ui.MainViewModel
import com.example.ui.components.media.VeoMediaPickerSheet
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w1080dp-h2400dp-xxhdpi")
@OptIn(ExperimentalCoroutinesApi::class)
class VeoMediaPickerServiceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var application: Application
    private lateinit var mediaPickerService: VeoMediaPickerService
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        mediaPickerService = VeoMediaPickerService(application)
        viewModel = MainViewModel(application)
    }

    @Test
    fun `test curated studio videos generation and properties`() {
        val curatedVideos = mediaPickerService.getCuratedStudioVideos()
        assertTrue("Curated videos should not be empty", curatedVideos.isNotEmpty())
        assertEquals(4, curatedVideos.size)

        val firstVideo = curatedVideos.first()
        assertNotNull(firstVideo.metadata)
        assertTrue(firstVideo.metadata.durationMs > 0)
        assertTrue(firstVideo.metadata.width > 0)
        assertTrue(firstVideo.metadata.height > 0)
        assertEquals("16:9", firstVideo.metadata.aspectRatio)
        assertEquals("4K UHD", firstVideo.metadata.resolutionLabel)
        assertEquals("MP4", firstVideo.metadata.format)
    }

    @Test
    fun `test build timeline clip with video payload`() {
        val curatedVideos = mediaPickerService.getCuratedStudioVideos()
        val meta = curatedVideos[0].metadata

        val clip = mediaPickerService.buildTimelineClip(
            metadata = meta,
            trackId = "track_v1_main",
            startMs = 2000L,
            isMaster = true
        )

        assertEquals("track_v1_main", clip.trackId)
        assertEquals(TrackType.VIDEO, clip.trackType)
        assertEquals(2000L, clip.startMs)
        assertEquals(meta.durationMs, clip.durationMs)
        assertEquals(2000L + meta.durationMs, clip.endMs)

        assertNotNull(clip.videoPayload)
        val payload = clip.videoPayload!!
        assertEquals(meta.uri, payload.uriString)
        assertEquals(meta.name, payload.fileName)
        assertEquals(meta.width, payload.width)
        assertEquals(meta.height, payload.height)
        assertEquals(meta.aspectRatio, payload.aspectRatio)
        assertTrue(payload.isMasterTrack)
    }

    @Test
    fun `test multi track composition getActiveVideoClips`() {
        val curatedVideos = mediaPickerService.getCuratedStudioVideos()
        val clipA = mediaPickerService.buildTimelineClip(
            metadata = curatedVideos[0].metadata,
            trackId = "track_v1",
            startMs = 0L
        )
        val clipB = mediaPickerService.buildTimelineClip(
            metadata = curatedVideos[1].metadata,
            trackId = "track_v1",
            startMs = 15000L
        )

        val videoTrack = EditorTrack(
            id = "track_v1",
            name = "V1 Master Track",
            type = TrackType.VIDEO,
            orderIndex = 0,
            clips = listOf(clipA, clipB)
        )

        val composition = MultiTrackComposition(
            tracks = listOf(videoTrack)
        )

        // At 2000ms, clipA is active
        val activeAt2s = composition.getActiveVideoClips(2000L)
        assertEquals(1, activeAt2s.size)
        assertEquals(clipA.id, activeAt2s.first().id)

        // At 16000ms, clipB is active
        val activeAt16s = composition.getActiveVideoClips(16000L)
        assertEquals(1, activeAt16s.size)
        assertEquals(clipB.id, activeAt16s.first().id)

        // If track is hidden, no active video clips
        val hiddenComp = composition.copy(
            tracks = listOf(videoTrack.copy(isHidden = true))
        )
        val activeWhenHidden = hiddenComp.getActiveVideoClips(2000L)
        assertTrue(activeWhenHidden.isEmpty())
    }

    @Test
    fun `test load video into timeline with SET_AS_MASTER mode`() = runTest {
        val curatedVideos = mediaPickerService.getCuratedStudioVideos()
        val video = curatedVideos.first()

        // Create default composition
        viewModel.multiTrackComposition.value = MultiTrackStateManager.createDefaultComposition(15000L)

        viewModel.loadVideoIntoTimeline(
            metadata = video.metadata,
            mode = VideoPlacementMode.SET_AS_MASTER,
            currentPositionMs = 0L
        )

        val updatedComp = viewModel.multiTrackComposition.value
        val videoTrack = updatedComp.tracks.first { it.type == TrackType.VIDEO }
        assertTrue(videoTrack.clips.isNotEmpty())

        val masterClip = videoTrack.clips.first()
        assertNotNull(masterClip.videoPayload)
        assertEquals(video.metadata.name, masterClip.videoPayload?.fileName)
        assertTrue(masterClip.videoPayload?.isMasterTrack == true)
    }

    @Test
    fun `test load video into timeline with INSERT_AT_PLAYHEAD mode`() = runTest {
        val curatedVideos = mediaPickerService.getCuratedStudioVideos()
        val video = curatedVideos[1]

        viewModel.multiTrackComposition.value = MultiTrackStateManager.createDefaultComposition(30000L)
        val playheadMs = 5000L

        viewModel.loadVideoIntoTimeline(
            metadata = video.metadata,
            mode = VideoPlacementMode.INSERT_AT_PLAYHEAD,
            currentPositionMs = playheadMs
        )

        val updatedComp = viewModel.multiTrackComposition.value
        val videoTrack = updatedComp.tracks.first { it.type == TrackType.VIDEO }
        val insertedClip = videoTrack.clips.firstOrNull { it.startMs == playheadMs }
        assertNotNull(insertedClip)
        assertEquals(video.metadata.name, insertedClip?.videoPayload?.fileName)
    }

    @Test
    fun `test veo media picker sheet renders correctly and triggers curated selection`() {
        viewModel.openMediaPickerSheet()

        composeTestRule.setContent {
            MyApplicationTheme {
                VeoMediaPickerSheet(
                    viewModel = viewModel,
                    currentPositionMs = 1200L,
                    onDismiss = { viewModel.closeMediaPickerSheet() }
                )
            }
        }

        // Verify Dialog & Launchers presence
        composeTestRule.onNodeWithTag("veo_media_picker_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_video_system_gallery_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_video_file_browser_btn").assertIsDisplayed()

        // Select a curated studio video via ViewModel or direct trigger
        val curated = viewModel.curatedStudioVideos.value.first()
        viewModel.selectCuratedVideo(curated)
        composeTestRule.waitForIdle()

        // Inspected video card should now exist with technical metadata
        composeTestRule.onNodeWithTag("inspected_video_card").assertExists()

        // Verify placement mode options exist
        composeTestRule.onNodeWithTag("placement_mode_set_as_master").assertExists()
        composeTestRule.onNodeWithTag("placement_mode_insert_at_playhead").assertExists()
        composeTestRule.onNodeWithTag("placement_mode_append_to_timeline").assertExists()
        composeTestRule.onNodeWithTag("placement_mode_create_new_project").assertExists()

        // Action button is enabled and clickable
        composeTestRule.onNodeWithTag("load_video_into_timeline_btn").assertIsDisplayed().assertIsEnabled().performClick()

        // After clicking, sheet is dismissed
        assertFalse(viewModel.isMediaPickerSheetOpen.value)
    }
}
