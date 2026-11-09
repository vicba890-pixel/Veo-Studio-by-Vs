package com.example

import com.example.data.Project
import com.example.data.smartcrop.GeminiSmartCropEngine
import com.example.data.smartcrop.SmartCropAspectRatio
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GeminiSmartCropEngineTest {

    private lateinit var engine: GeminiSmartCropEngine
    private lateinit var sampleProject: Project

    @Before
    fun setUp() {
        engine = GeminiSmartCropEngine()
        sampleProject = Project(
            id = 1,
            title = "Cinematic Presenter Story",
            prompt = "A charismatic speaker talking to the camera in a modern studio.",
            scriptText = "Welcome to the future of cinematic video creation. Let's build together.",
            aspectRatio = "16:9",
            durationSec = 15
        )
    }

    @Test
    fun testSubjectDetectionAndAspectRatioSuggestion() = runBlocking {
        val suggestion = engine.analyzeAndSuggestSmartCrop(sampleProject, 2500L)

        assertNotNull(suggestion)
        assertNotNull(suggestion.detectedSubject)
        assertTrue(suggestion.confidence >= 0.85f)
        assertTrue(suggestion.suggestedAspectRatio == SmartCropAspectRatio.RATIO_9_16 || suggestion.suggestedAspectRatio == SmartCropAspectRatio.RATIO_4_5)
        assertTrue(suggestion.detectedSubject.boundingBox.centerX in 0.1f..0.9f)
        assertTrue(suggestion.detectedSubject.boundingBox.centerY in 0.1f..0.9f)
    }

    @Test
    fun testCropWindowCalculationFor916And45() {
        val crop916 = engine.computeCropWindow(0.5f, 9f / 16f, 16f / 9f)
        assertTrue(crop916.left >= 0f)
        assertTrue(crop916.right <= 1f)
        assertEquals(0.5f, crop916.centerX, 0.05f)

        val crop45 = engine.computeCropWindow(0.5f, 4f / 5f, 16f / 9f)
        assertTrue(crop45.left >= 0f)
        assertTrue(crop45.right <= 1f)
        assertTrue(crop45.width > crop916.width) // 4:5 is wider than 9:16
    }

    @Test
    fun testRealtimeSubjectCenterTracking() = runBlocking {
        val suggestion = engine.analyzeAndSuggestSmartCrop(sampleProject, 0L)
        val (x1, y1) = engine.evaluateRealtimeSubjectCenter(suggestion, 0L, 15000L)
        val (x2, y2) = engine.evaluateRealtimeSubjectCenter(suggestion, 3000L, 15000L)

        assertTrue(x1 in 0.1f..0.9f)
        assertTrue(y1 in 0.1f..0.9f)
        assertTrue(x2 in 0.1f..0.9f)
        assertTrue(y2 in 0.1f..0.9f)
    }
}
