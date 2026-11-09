package com.example.data.metadata

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * =========================================================================================
 * VMSB© 2026 - ENTIDAD ROOM DE METADATOS DE VIDEO CON SEGURIDAD POST-CUÁNTICA DILITHIUM
 * =========================================================================================
 *
 * Almacena en la base de datos local SQLite (Room) los metadatos completos de video:
 * - Título, Descripción / Sinopsis, Etiquetas (Tags) y Categoría.
 * - Información técnica de formato (Resolución, Relación de Aspecto, Duración, URI).
 * - Sello criptográfico digital post-cuántico CRYSTALS-Dilithium (NIST FIPS 204 ML-DSA-65)
 *   inmune a ataques cuánticos ("Q-Day") y ataques "Store Now, Decrypt Later" (SNDL).
 */
@Entity(
    tableName = "video_metadata",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["videoUri"]),
        Index(value = ["updatedAt"])
    ]
)
data class VideoMetadataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Int = 0,
    val videoUri: String = "",
    val title: String,
    val description: String = "",
    val tags: String = "",
    val category: String = "Cinematic Short",
    val author: String = "Veo Studio Creator",
    val copyrightNotice: String = "© 2026 Sovereign Creator • All Rights Reserved",
    val resolution: String = "1080p (FHD)",
    val aspectRatio: String = "16:9",
    val durationSeconds: Int = 15,

    // =========================================================================
    // PARÁMETROS CRIPTOGRÁFICOS DE SEGURIDAD DILITHIUM & DÍA CUÁNTICO (Q-DAY)
    // =========================================================================
    val dilithiumSignature: String = "",
    val dilithiumPublicKey: String = "",
    val dilithiumChallenge: String = "",
    val qDayImmunityStatus: String = "NIST FIPS 204 ML-DSA-65 (Q-DAY PROTECTED)",
    val quantumVaultNonce: String = "",
    val metadataSha256Digest: String = "",
    val isQuantumVerified: Boolean = true,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable {

    fun getTagList(): List<String> {
        if (tags.isBlank()) return emptyList()
        return tags.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
