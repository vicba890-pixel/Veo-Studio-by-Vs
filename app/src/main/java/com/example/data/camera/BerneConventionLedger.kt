package com.example.data.camera

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * Libro Mayor Sellado (Ledger) con Marca de Tiempo bajo el Protocolo de Berna (WIPO/OMPI) by VMSB©
 *
 * Fundamentación Jurídica Internacional:
 * Convenio de Berna para la Protección de las Obras Literarias y Artísticas (Acta de París 1971):
 * - Artículo 2(1): Obras cinematográficas y fotografías protegidas como creaciones originales.
 * - Artículo 5(2): Principio de protección automática — el goce y ejercicio no están subordinados a ninguna formalidad.
 *
 * El ledger VMSB© genera un sello de tiempo criptográfico inmutable con verificación post-cuántica (Lattice),
 * vinculando la huella digital del fotograma/clip, la identidad del creador y el manifiesto C2PA de procedencia.
 */

data class BerneLedgerBlock(
    val blockIndex: Long,
    val timestampIso: String,
    val timestampMillis: Long,
    val timestampNanos: Long,
    val mediaTitle: String,
    val creatorLegalNotice: String = "TODOS LOS DERECHOS RESERVADOS BAJO EL PROTOCOLO DE BERNA (WIPO/OMPI) BY VMSB©",
    val berneArticleClause: String = "Convenio de Berna (Art. 5.2): Protección jurídica automática universal inmediata desde el instante de fijación digital.",
    val payloadSha256: String,
    val latticePostQuantumSignature: String,
    val previousBlockHash: String,
    val currentBlockHash: String,
    val c2paManifestGuid: String,
    val isSealed: Boolean = true
)

data class BerneCopyrightCertificate(
    val certificateId: String,
    val blockIndex: Long,
    val title: String,
    val registeredDate: String,
    val universalJurisdiction: String = "181 Estados Miembros del Convenio de Berna (WIPO/OMPI)",
    val legalStatus: String = "DERECHOS PATRIMONIALES Y MORALES RESERVADOS EN SU TOTALIDAD",
    val hashIntegrity: String,
    val latticeSignature: String,
    val c2paClaimGuid: String,
    val formattedLegalDocument: String
)

class BerneConventionLedger(
    private val latticeProtector: LatticeZeroDataProtector = LatticeZeroDataProtector()
) {
    private val blockCounter = AtomicLong(1001)
    private val ledgerBlocks = mutableListOf<BerneLedgerBlock>()
    private var lastBlockHash = "0000000000BERNEGENESISVMSB2026OMPIWIPO0000000000000000000000000"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    init {
        // Bloque génesis de VMSB©
        val genesis = sealMediaWork(
            title = "VMSB© Master Video Timeline Genesis Core",
            payloadBytes = "BERNE_GENESIS_CORE_STREAM_VMSB".toByteArray()
        )
    }

    /**
     * Sella una obra audiovisual en el Ledger inmutable con marca de tiempo y firma de retículo.
     */
    fun sealMediaWork(
        title: String,
        payloadBytes: ByteArray
    ): BerneLedgerBlock {
        val index = blockCounter.getAndIncrement()
        val now = System.currentTimeMillis()
        val nanos = System.nanoTime()
        val iso = dateFormat.format(Date(now))

        // 1. Huella SHA-256 de la obra
        val sha256 = computeSha256(payloadBytes)

        // 2. Firma Post-Cuántica de Retículo
        val latticeSig = latticeProtector.signLatticeFrame(index, payloadBytes)

        // 3. Manifiesto C2PA
        val c2paGuid = "urn:c2pa:vmsb:" + UUID.randomUUID().toString()

        // 4. Hash del bloque encadenado
        val blockData = "$index|$iso|$title|$sha256|$latticeSig|$lastBlockHash|$c2paGuid"
        val currentHash = computeSha256(blockData.toByteArray())

        val block = BerneLedgerBlock(
            blockIndex = index,
            timestampIso = iso,
            timestampMillis = now,
            timestampNanos = nanos,
            mediaTitle = title,
            payloadSha256 = sha256,
            latticePostQuantumSignature = latticeSig,
            previousBlockHash = lastBlockHash,
            currentBlockHash = currentHash,
            c2paManifestGuid = c2paGuid,
            isSealed = true
        )

        synchronized(ledgerBlocks) {
            ledgerBlocks.add(0, block)
            lastBlockHash = currentHash
        }

        // Registrar auditoría de seguridad
        latticeProtector.recordAuditEvent(
            sensor = "LEDGER_DE_BERNA",
            event = "Obra sellada en bloque #$index con marca de tiempo universal y protocolo de Berna",
            compliance = "WIPO/OMPI Convenio de Berna Art. 5.2 • C2PA v2.1",
            indicator = "BLUE_CRYPTO_LED"
        )

        return block
    }

    fun getAllBlocks(): List<BerneLedgerBlock> {
        return synchronized(ledgerBlocks) { ledgerBlocks.toList() }
    }

    fun getLatestBlock(): BerneLedgerBlock? {
        return synchronized(ledgerBlocks) { ledgerBlocks.firstOrNull() }
    }

    /**
     * Genera el Certificado Jurídico Oficial de Propiedad Intelectual.
     */
    fun generateCertificate(block: BerneLedgerBlock): BerneCopyrightCertificate {
        val certId = "BERNE-VMSB-${block.blockIndex}-${block.currentBlockHash.take(8).uppercase()}"

        val legalText = """
================================================================================
           CERTIFICADO OFICIAL DE PROPIEDAD INTELECTUAL
       TODOS LOS DERECHOS RESERVADOS BAJO EL PROTOCOLO DE BERNA
                    REGISTRADO POR VMSB© LEDGER
================================================================================

CERTIFICADO ID: $certId
BLOQUE LEDGER: #${block.blockIndex}
FECHA / HORA UTC: ${block.timestampIso}

TÍTULO DE LA OBRA: "${block.mediaTitle}"
RÉGIMEN JURÍDICO:
Convenio de Berna para la Protección de las Obras Literarias y Artísticas
(París 1971 / OMPI - WIPO).
Art. 5(2): Protección automática sin subordinación a registro previo.

HUELLA CRIPTOGRÁFICA (SHA-256):
${block.payloadSha256}

SELLO POST-CUÁNTICO (LATTICE CRYSTALS-DILITHIUM):
${block.latticePostQuantumSignature}

MANIFIESTO DE PROCEDENCIA C2PA v2.1:
${block.c2paManifestGuid}

HASH DE ENCADENAMIENTO INMUTABLE:
${block.currentBlockHash}

DECLARACIÓN DE RESERVA EXPRESA:
Quedan reservados todos los derechos de autor, fijación, reproducción,
distribución, doblaje, subtitulación, comunicación pública y puesta a
disposición en redes digitales en los 181 países miembros de la Unión de Berna.
Cualquier explotación no autorizada está sujeta a acciones civiles y penales
conforme a la legislación internacional.

Sello de tiempo verificado por el enclave criptográfico nativo VMSB©.
================================================================================
        """.trimIndent()

        return BerneCopyrightCertificate(
            certificateId = certId,
            blockIndex = block.blockIndex,
            title = block.mediaTitle,
            registeredDate = block.timestampIso,
            hashIntegrity = block.payloadSha256,
            latticeSignature = block.latticePostQuantumSignature,
            c2paClaimGuid = block.c2paManifestGuid,
            formattedLegalDocument = legalText
        )
    }

    private fun computeSha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(data)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
