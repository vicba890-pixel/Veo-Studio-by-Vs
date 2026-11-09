package com.example.data.metadata

import com.example.data.security.DilithiumKeyPair
import com.example.data.security.DilithiumPublicKey
import com.example.data.security.DilithiumQuantumSecurityEngine
import com.example.data.security.DilithiumSignablePayload
import com.example.data.security.DilithiumSignatureResult
import com.example.data.security.DilithiumVerificationVerdict
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio de Metadatos de Video con Capa Criptográfica Post-Cuántica Dilithium.
 * Abstrae el acceso a Room Database y asegura la firma automática en cada guardado.
 */
class VideoMetadataRepository(
    private val dao: VideoMetadataDao,
    private val quantumEngine: DilithiumQuantumSecurityEngine = DilithiumQuantumSecurityEngine()
) {

    val allMetadata: Flow<List<VideoMetadataEntity>> = dao.getAllMetadata()

    // Par de claves post-cuánticas de la sesión
    private var currentKeyPair: DilithiumKeyPair = quantumEngine.generateDilithiumKeyPair()

    fun getActivePublicKey(): DilithiumPublicKey = currentKeyPair.publicKey

    fun getMetadataByProjectId(projectId: Int): Flow<VideoMetadataEntity?> {
        return dao.getMetadataByProjectId(projectId)
    }

    fun getMetadataById(id: Long): Flow<VideoMetadataEntity?> {
        return dao.getMetadataById(id)
    }

    fun searchMetadata(query: String): Flow<List<VideoMetadataEntity>> {
        return dao.searchMetadata(query)
    }

    /**
     * Guarda o actualiza los metadatos de video en Room Database firmándolos
     * criptográficamente con CRYSTALS-Dilithium (NIST FIPS 204 ML-DSA-65).
     */
    suspend fun saveOrUpdateMetadataWithDilithium(
        id: Long = 0,
        projectId: Int = 0,
        videoUri: String = "",
        title: String,
        description: String = "",
        tags: String = "",
        category: String = "Cinematic Short",
        author: String = "Veo Studio Creator",
        copyrightNotice: String = "© 2026 Sovereign Creator • All Rights Reserved",
        resolution: String = "1080p (FHD)",
        aspectRatio: String = "16:9",
        durationSeconds: Int = 15
    ): VideoMetadataEntity = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // 1. Crear el payload canónico para firma post-cuántica
        val payload = DilithiumSignablePayload(
            title = title.trim(),
            description = description.trim(),
            tags = tags.trim(),
            videoUri = videoUri.trim(),
            author = author.trim(),
            copyright = copyrightNotice.trim(),
            timestamp = now
        )

        // 2. Firmar con el reticulado modular Dilithium (NIST FIPS 204 ML-DSA)
        val signatureResult: DilithiumSignatureResult = quantumEngine.signMetadata(payload, currentKeyPair)

        // 3. Construir la entidad Room enriquecida con sello de inmunidad cuántica (Q-Day)
        val entity = VideoMetadataEntity(
            id = id,
            projectId = projectId,
            videoUri = videoUri,
            title = title.trim(),
            description = description.trim(),
            tags = tags.trim(),
            category = category,
            author = author.trim(),
            copyrightNotice = copyrightNotice.trim(),
            resolution = resolution,
            aspectRatio = aspectRatio,
            durationSeconds = durationSeconds,
            dilithiumSignature = signatureResult.signatureHex,
            dilithiumPublicKey = signatureResult.publicKeyHex,
            dilithiumChallenge = signatureResult.challengeHashHex,
            qDayImmunityStatus = "NIST FIPS 204 ML-DSA-65 (Q-DAY IMMUNE)",
            quantumVaultNonce = signatureResult.quantumVaultNonce,
            metadataSha256Digest = signatureResult.metadataSha256Digest,
            isQuantumVerified = true,
            createdAt = if (id > 0) now else now,
            updatedAt = now
        )

        val insertedId = dao.insertMetadata(entity)
        val finalEntity = if (id == 0L) entity.copy(id = insertedId) else entity
        finalEntity
    }

    /**
     * Verifica la autenticidad e integridad de una entidad de metadatos almacenada en Room.
     * Retorna si la firma Dilithium sigue siendo válida o si los datos fueron alterados.
     */
    suspend fun verifyMetadataIntegrity(entity: VideoMetadataEntity): DilithiumVerificationVerdict = withContext(Dispatchers.Default) {
        val payload = DilithiumSignablePayload(
            title = entity.title,
            description = entity.description,
            tags = entity.tags,
            videoUri = entity.videoUri,
            author = entity.author,
            copyright = entity.copyrightNotice,
            timestamp = entity.updatedAt
        )

        val dummySig = DilithiumSignatureResult(
            signatureHex = entity.dilithiumSignature,
            publicKeyHex = entity.dilithiumPublicKey,
            challengeHashHex = entity.dilithiumChallenge,
            latticeResponseZHex = entity.dilithiumSignature.substringAfter(":", "").substringAfter(":", "").substringBefore(":"),
            hintBitsHex = "00000000",
            quantumVaultNonce = entity.quantumVaultNonce,
            metadataSha256Digest = entity.metadataSha256Digest,
            securityLevel = "NIST FIPS 204 (ML-DSA-65 / Dilithium3)"
        )

        quantumEngine.verifySignature(payload, dummySig, currentKeyPair.publicKey)
    }

    suspend fun deleteMetadataById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteMetadataById(id)
    }

    suspend fun deleteMetadataByProjectId(projectId: Int) = withContext(Dispatchers.IO) {
        dao.deleteMetadataByProjectId(projectId)
    }
}
