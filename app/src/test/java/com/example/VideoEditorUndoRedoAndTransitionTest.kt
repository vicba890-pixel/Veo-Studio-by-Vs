package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Project
import com.example.data.editor.VideoEditorDraftEntity
import com.example.data.editor.VideoEditorHistoryRepository
import com.example.data.export.VeoProjectExporter
import com.example.data.transition.TransitionType
import com.example.data.transition.VideoTransitionConfig
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * =========================================================================================
 * PRUEBAS UNITARIAS Y ROBOLECTRIC: GESTOR DE DESHACER / REHACER ROOM, TRANSICIONES Y EXPORT
 * =========================================================================================
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VideoEditorUndoRedoAndTransitionTest {

    private lateinit var db: AppDatabase
    private lateinit var historyRepository: VideoEditorHistoryRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        historyRepository = VideoEditorHistoryRepository(db.videoEditorHistoryDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testHistorySnapshotRecordingAndUndoRedoStackNavigation() = runBlocking {
        val projectId = 101

        // Paso 0: Snapshot inicial (Recorte 0.0 - 1.0)
        val idx0 = historyRepository.pushSnapshot(
            projectId = projectId,
            currentIndex = -1,
            actionDescription = "Estado inicial del video",
            trimStart = 0.0f,
            trimEnd = 1.0f,
            overlayText = "",
            reframePanX = 0f,
            reframePanY = 0f,
            reframeZoom = 1.0f,
            colorLutPresetId = "NONE",
            colorBrightness = 0f,
            colorContrast = 1f,
            colorSaturation = 1f,
            colorTemperature = 0f,
            colorLutIntensity = 1f,
            transitionTypeId = "NONE",
            transitionDurationMs = 0L,
            markersJson = "[]"
        )
        assertEquals(0, idx0)

        val countAfterInit = historyRepository.getHistoryListForProject(projectId).size
        assertEquals(1, countAfterInit)

        // Paso 1: Edición 1 - Recorte ajustado (0.1f - 0.9f)
        val idx1 = historyRepository.pushSnapshot(
            projectId = projectId,
            currentIndex = 0,
            actionDescription = "Ajuste de recorte (10% - 90%)",
            trimStart = 0.1f,
            trimEnd = 0.9f,
            overlayText = "",
            reframePanX = 0f,
            reframePanY = 0f,
            reframeZoom = 1.0f,
            colorLutPresetId = "NONE",
            colorBrightness = 0f,
            colorContrast = 1f,
            colorSaturation = 1f,
            colorTemperature = 0f,
            colorLutIntensity = 1f,
            transitionTypeId = "NONE",
            transitionDurationMs = 0L,
            markersJson = "[]"
        )
        assertEquals(1, idx1)

        // Paso 2: Edición 2 - Transición Disolvencia Cruzada agregada
        val idx2 = historyRepository.pushSnapshot(
            projectId = projectId,
            currentIndex = 1,
            actionDescription = "Transición Disolvencia Cruzada",
            trimStart = 0.1f,
            trimEnd = 0.9f,
            overlayText = "",
            reframePanX = 0f,
            reframePanY = 0f,
            reframeZoom = 1.0f,
            colorLutPresetId = "NONE",
            colorBrightness = 0f,
            colorContrast = 1f,
            colorSaturation = 1f,
            colorTemperature = 0f,
            colorLutIntensity = 1f,
            transitionTypeId = "CROSS_DISSOLVE",
            transitionDurationMs = 650L,
            markersJson = "[]"
        )
        assertEquals(2, idx2)

        val totalSnapshots = historyRepository.getHistoryListForProject(projectId).size
        assertEquals(3, totalSnapshots)

        // Simulación Deshacer (Undo): Inspeccionar snapshot de índice 1
        val previousSnapshot = historyRepository.getSnapshotByIndex(projectId, 1)
        assertNotNull(previousSnapshot)
        assertEquals(0.1f, previousSnapshot!!.trimStart)
        assertEquals("NONE", previousSnapshot.transitionTypeId)

        // Simulación Deshacer adicional (Undo): Inspeccionar snapshot de índice 0
        val rootSnapshot = historyRepository.getSnapshotByIndex(projectId, 0)
        assertNotNull(rootSnapshot)
        assertEquals(0.0f, rootSnapshot!!.trimStart)
        assertEquals(1.0f, rootSnapshot.trimEnd)

        // Simulación Rehacer (Redo): Inspeccionar snapshot de índice 1
        val redoSnapshot = historyRepository.getSnapshotByIndex(projectId, 1)
        assertNotNull(redoSnapshot)
        assertEquals(0.1f, redoSnapshot!!.trimStart)
        assertEquals(0.9f, redoSnapshot.trimEnd)

        // Nueva bifurcación de edición a partir del índice 1 (poda el índice 2 anterior)
        val idxFork = historyRepository.pushSnapshot(
            projectId = projectId,
            currentIndex = 1,
            actionDescription = "Nueva bifurcación: Fundido a negro",
            trimStart = 0.2f,
            trimEnd = 0.8f,
            overlayText = "",
            reframePanX = 0f,
            reframePanY = 0f,
            reframeZoom = 1.0f,
            colorLutPresetId = "NONE",
            colorBrightness = 0f,
            colorContrast = 1f,
            colorSaturation = 1f,
            colorTemperature = 0f,
            colorLutIntensity = 1f,
            transitionTypeId = "FADE_TO_BLACK",
            transitionDurationMs = 500L,
            markersJson = "[]"
        )
        assertEquals(2, idxFork)

        val bifurcatedSnapshots = historyRepository.getHistoryListForProject(projectId).size
        assertEquals(3, bifurcatedSnapshots)
        val activeBranch = historyRepository.getSnapshotByIndex(projectId, 2)
        assertNotNull(activeBranch)
        assertEquals("FADE_TO_BLACK", activeBranch!!.transitionTypeId)
    }

    @Test
    fun testVideoTransitionCatalogAndDurations() {
        val dissolveCfg = VideoTransitionConfig(TransitionType.CROSS_DISSOLVE, 750L)
        assertEquals("Disolvencia Cruzada", dissolveCfg.transitionType.displayName)
        assertTrue(dissolveCfg.durationMs in 250L..2000L)

        val fadeBlackCfg = VideoTransitionConfig(TransitionType.FADE_TO_BLACK, 1000L)
        assertEquals("Fundido a Negro (Dip to Black)", fadeBlackCfg.transitionType.displayName)

        val glitchCfg = VideoTransitionConfig(TransitionType.GLITCH_CYBER, 450L)
        assertEquals("Glitch Digital Cyberpunk", glitchCfg.transitionType.displayName)

        val allTypes = TransitionType.entries
        assertTrue("Catalog must contain at least 7 transitions", allTypes.size >= 7)
    }

    @Test
    fun testProjectExporterCreatesValidVeoProjFile() = runBlocking {
        val testProject = Project(
            id = 55,
            title = "Cinematic Cyberpunk Documentary",
            prompt = "Hyper-detailed 4K documentary shot",
            aspectRatio = "16:9",
            resolution = "4K",
            durationSec = 15,
            selectedMood = "Anamorphic Sci-Fi",
            videoUri = "content://media/sample/55"
        )

        val draft = VideoEditorDraftEntity(
            projectId = 55,
            projectTitle = "Cinematic Cyberpunk Documentary",
            trimStart = 0.15f,
            trimEnd = 0.85f,
            reframePanX = 0.05f,
            reframePanY = -0.02f,
            reframeZoom = 1.15f,
            colorLutPresetId = "teal_orange",
            colorLutIntensity = 0.8f,
            markersJson = "[]",
            saveVersion = 4L
        )

        val result = VeoProjectExporter.exportProjectStateToFile(
            context = context,
            project = testProject,
            draft = draft
        )

        assertNotNull(result)
        assertTrue("File must exist", result.file.exists())
        assertTrue("Filename must end with .veoproj", result.file.name.endsWith(".veoproj"))
        assertTrue("File size must be positive", result.fileSizeBytes > 0)
        assertEquals(64, result.sha256Checksum.length) // Valid SHA-256

        val content = result.file.readText()
        assertTrue(content.contains("Cinematic Cyberpunk Documentary"))
        assertTrue(content.contains("16:9"))
    }
}
