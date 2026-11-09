package com.example

import com.example.data.Project
import com.example.data.multitrack.AudioCategory
import com.example.data.multitrack.TrackType
import com.example.data.voiceover.VmsbVoiceCatalog
import com.example.data.voiceover.VmsbVoiceOverConfig
import com.example.data.voiceover.VmsbVoiceOverStudioEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VmsbVoiceOverStudioEngineTest {

    private val engine = VmsbVoiceOverStudioEngine()

    @Test
    fun testVoiceCatalogActorsAreAvailable() {
        val actors = VmsbVoiceCatalog.ALL_ACTORS
        assertTrue(actors.isNotEmpty())
        assertTrue(actors.size >= 5)

        val categories = VmsbVoiceCatalog.CATEGORIES
        assertTrue(categories.contains("Todos"))
        assertTrue(categories.contains("Trailer Cinemático & Épico"))
        assertTrue(categories.contains("Documental & Masterclass Tech"))
    }

    @Test
    fun testVoiceOverDefaultConfig() {
        val config = VmsbVoiceOverConfig()
        assertNotNull(config.selectedActor)
        assertEquals("English", config.language)
        assertTrue(config.ebuStandard.contains("EBU R128"))
        assertEquals(1.0f, config.volume, 0.001f)
        assertEquals(1.0f, config.speechRate, 0.001f)
        assertEquals(1.0f, config.pitch, 0.001f)
        assertTrue(config.isDeEsserActive)
        assertTrue(config.isNoiseGateActive)
    }

    @Test
    fun testSynthesizeVoiceOverReturnsValidClip() = runBlocking {
        val project = Project(
            title = "Test Commercial",
            prompt = "A high-end cinematic perfume spot",
            durationSec = 15
        )
        val config = VmsbVoiceOverConfig(
            scriptText = "Este es un guion de prueba para el Voice Over Studio Nativo VMSB.",
            speechRate = 1.0f
        )

        val (updatedConfig, clip) = engine.synthesizeVoiceOver(project, config)

        assertNotNull(updatedConfig)
        assertNotNull(clip)
        assertEquals(TrackType.AUDIO, clip.trackType)
        assertNotNull(clip.audioPayload)
        assertEquals(AudioCategory.VOICEOVER, clip.audioPayload?.category)
        assertTrue(clip.durationMs > 0L)
        assertTrue(updatedConfig.waveformSamples.isNotEmpty())
        assertTrue(clip.audioPayload?.waveformSamples?.isNotEmpty() == true)
    }
}
