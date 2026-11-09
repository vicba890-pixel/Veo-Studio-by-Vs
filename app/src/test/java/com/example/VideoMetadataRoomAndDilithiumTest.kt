package com.example

import com.example.data.metadata.VideoMetadataDao
import com.example.data.metadata.VideoMetadataEntity
import com.example.data.metadata.VideoMetadataRepository
import com.example.data.security.DilithiumKeyPair
import com.example.data.security.DilithiumQuantumSecurityEngine
import com.example.data.security.DilithiumSignablePayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * =========================================================================================
 * PRUEBAS UNITARIAS: MOTOR POST-CUÁNTICO DILITHIUM (NIST FIPS 204 ML-DSA)
 * & CAPA DE METADATOS DE VIDEO EN ROOM DATABASE (PROTECCIÓN Q-DAY)
 * =========================================================================================
 */
class VideoMetadataRoomAndDilithiumTest {

    private lateinit var quantumEngine: DilithiumQuantumSecurityEngine
    private lateinit var keyPair: DilithiumKeyPair

    @Before
    fun setUp() {
        quantumEngine = DilithiumQuantumSecurityEngine()
        keyPair = quantumEngine.generateDilithiumKeyPair()
    }

    @Test
    fun testDilithiumKeyPairGenerationProducesValidLatticeParameters() {
        assertNotNull(keyPair.publicKey)
        assertNotNull(keyPair.privateKey)
        assertEquals("NIST-FIPS-204-ML-DSA-65", keyPair.publicKey.algorithm)
        assertEquals(keyPair.publicKey.keyId, keyPair.privateKey.keyId)
        assertTrue(keyPair.publicKey.keyId.startsWith("ML-DSA-"))
        assertEquals(64, keyPair.publicKey.matrixSeedHex.length)
        assertTrue(keyPair.publicKey.rawBytesHex.isNotEmpty())
    }

    @Test
    fun testSignAndVerifyVideoMetadataProducesQDayImmunity() {
        val payload = DilithiumSignablePayload(
            title = "Amanecer Volcánico 4K HDR",
            description = "Toma aérea cinematográfica capturada a 60 FPS con gradación Teal & Orange",
            tags = "4K, VeoStudio, Drone, Cinema",
            videoUri = "content://media/external/video/media/108",
            author = "Director VMSB",
            copyright = "© 2026 Sovereign Creator",
            timestamp = 1761700000000L
        )

        // 1. Firmar con Dilithium ML-DSA-65
        val signatureResult = quantumEngine.signMetadata(payload, keyPair)

        assertNotNull(signatureResult)
        assertTrue(signatureResult.signatureHex.startsWith("ML-DSA-65:"))
        assertTrue(signatureResult.quantumVaultNonce.startsWith("QVAULT-"))
        assertEquals(64, signatureResult.challengeHashHex.length)
        assertEquals(64, signatureResult.metadataSha256Digest.length)

        // 2. Verificar la firma original (debe ser 100% auténtica e inmune a Q-Day)
        val verdict = quantumEngine.verifySignature(payload, signatureResult, keyPair.publicKey)

        assertTrue(verdict.isValid)
        assertFalse(verdict.isTampered)
        assertEquals(100, verdict.qDayImmunityScorePercent)
        assertTrue(verdict.quantumResistanceClass.contains("Q-DAY IMMUNE"))
        assertTrue(verdict.details.contains("NIST FIPS 204 ML-DSA-65"))
    }

    @Test
    fun testTamperDetectionWhenTitleOrDescriptionIsAltered() {
        val originalPayload = DilithiumSignablePayload(
            title = "Documental Glaciar Patagónico",
            description = "Sinopsis autorizada original protegida con derechos de autor",
            tags = "Nature, 8K, Wildlife",
            videoUri = "content://media/video/200",
            author = "Expedición Sur",
            copyright = "© 2026 Propietario Original",
            timestamp = 1761700000000L
        )

        val signature = quantumEngine.signMetadata(originalPayload, keyPair)

        // Intentar alterar el título de los metadatos (Ataque de Falsificación de Metadatos)
        val tamperedPayload = originalPayload.copy(
            title = "Documental Glaciar Patagónico (Copia Pirata / Remezcla No Autorizada)"
        )

        val verdict = quantumEngine.verifySignature(tamperedPayload, signature, keyPair.publicKey)

        assertFalse(verdict.isValid)
        assertTrue(verdict.isTampered)
        assertEquals(0, verdict.qDayImmunityScorePercent)
        assertTrue(verdict.quantumResistanceClass.contains("COMPROMETIDO"))
        assertTrue(verdict.details.contains("modificación en título, descripción o tags"))
    }

    @Test
    fun testQDayAuditDossierReportsPostQuantumImmunity() {
        val dossier = quantumEngine.getQDayAuditDossier()

        assertTrue(dossier.isQDayImmune)
        assertEquals(128, dossier.quantumHardnessBits)
        assertTrue(dossier.securityStandard.contains("FIPS 204"))
        assertTrue(dossier.shorAlgorithmVulnerability.contains("INMUNE"))
        assertTrue(dossier.groverAlgorithmVulnerability.contains("RESISTENTE"))
        assertTrue(dossier.recommendations.isNotEmpty())
    }

    @Test
    fun testVideoMetadataEntityTagsParsing() {
        val entity = VideoMetadataEntity(
            title = "Video Test",
            tags = "Veo3.1,  4KHDR , Cinema,PostQuantum  "
        )

        val tags = entity.getTagList()
        assertEquals(4, tags.size)
        assertEquals("Veo3.1", tags[0])
        assertEquals("4KHDR", tags[1])
        assertEquals("Cinema", tags[2])
        assertEquals("PostQuantum", tags[3])
    }

    @Test
    fun testVideoMetadataRepositorySaveWithDilithium() = runBlocking {
        val fakeDao = FakeVideoMetadataDao()
        val repository = VideoMetadataRepository(fakeDao, quantumEngine)

        val savedEntity = repository.saveOrUpdateMetadataWithDilithium(
            title = "Trailer Cinemático Veo",
            description = "Prompt generado con Gemini Vision",
            tags = "Action, SciFi, 4K",
            projectId = 7
        )

        assertNotNull(savedEntity)
        assertEquals(1L, savedEntity.id)
        assertEquals("Trailer Cinemático Veo", savedEntity.title)
        assertTrue(savedEntity.dilithiumSignature.startsWith("ML-DSA-65:"))
        assertTrue(savedEntity.qDayImmunityStatus.contains("Q-DAY IMMUNE"))
        assertTrue(savedEntity.isQuantumVerified)
        assertEquals(1, fakeDao.insertCount)
    }
}

class FakeVideoMetadataDao : VideoMetadataDao {
    private val memoryStore = mutableListOf<VideoMetadataEntity>()
    var insertCount = 0

    override fun getAllMetadata(): Flow<List<VideoMetadataEntity>> = flowOf(memoryStore)

    override fun getMetadataByProjectId(projectId: Int): Flow<VideoMetadataEntity?> =
        flowOf(memoryStore.firstOrNull { it.projectId == projectId })

    override suspend fun getMetadataByProjectIdSync(projectId: Int): VideoMetadataEntity? =
        memoryStore.firstOrNull { it.projectId == projectId }

    override fun getMetadataById(id: Long): Flow<VideoMetadataEntity?> =
        flowOf(memoryStore.firstOrNull { it.id == id })

    override fun getMetadataByVideoUri(videoUri: String): Flow<VideoMetadataEntity?> =
        flowOf(memoryStore.firstOrNull { it.videoUri == videoUri })

    override fun searchMetadata(query: String): Flow<List<VideoMetadataEntity>> =
        flowOf(memoryStore.filter { it.title.contains(query, ignoreCase = true) })

    override suspend fun insertMetadata(metadata: VideoMetadataEntity): Long {
        insertCount++
        val newId = if (metadata.id == 0L) (memoryStore.size + 1).toLong() else metadata.id
        val entityWithId = metadata.copy(id = newId)
        memoryStore.removeAll { it.id == newId }
        memoryStore.add(entityWithId)
        return newId
    }

    override suspend fun updateMetadata(metadata: VideoMetadataEntity) {
        memoryStore.removeAll { it.id == metadata.id }
        memoryStore.add(metadata)
    }

    override suspend fun deleteMetadataById(id: Long) {
        memoryStore.removeAll { it.id == id }
    }

    override suspend fun deleteMetadataByProjectId(projectId: Int) {
        memoryStore.removeAll { it.projectId == projectId }
    }

    override fun getMetadataCount(): Flow<Int> = flowOf(memoryStore.size)
}
