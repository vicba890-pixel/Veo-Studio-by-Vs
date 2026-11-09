package com.example.data.c2pa

import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Constructor de Cajas Binarias JUMBF (ISO/IEC 19566-5) y Cajas MP4 'uuid'
 * Conforme con la especificación C2PA (Coalition for Content Provenance and Authenticity) v2.1.
 */
object C2paJumbfBoxBuilder {

    // UUID oficial C2PA Manifest Store: d8fec3d3-1b0f-416c-bf42-064a0ad4cb4e
    val C2PA_BOX_UUID: ByteArray = byteArrayOf(
        0xD8.toByte(), 0xFE.toByte(), 0xC3.toByte(), 0xD3.toByte(),
        0x1B.toByte(), 0x0F.toByte(), 0x41.toByte(), 0x6C.toByte(),
        0xBF.toByte(), 0x42.toByte(), 0x06.toByte(), 0x4A.toByte(),
        0x0A.toByte(), 0xD4.toByte(), 0xCB.toByte(), 0x4E.toByte()
    )

    // Content Type UUID para la caja de descripción JUMD de C2PA
    val JUMD_CONTENT_TYPE_C2PA: ByteArray = byteArrayOf(
        0x63.toByte(), 0x32.toByte(), 0x70.toByte(), 0x61.toByte(), // "c2pa"
        0x00.toByte(), 0x11.toByte(), 0x00.toByte(), 0x10.toByte(),
        0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 0xAA.toByte(),
        0x00.toByte(), 0x38.toByte(), 0x9B.toByte(), 0x71.toByte()
    )

    const val BOX_TYPE_UUID = "uuid"
    const val BOX_TYPE_JUMB = "jumb"
    const val BOX_TYPE_JUMD = "jumd"
    const val BOX_TYPE_JSON = "json"

    /**
     * Construye una caja binaria genérica ISOBMFF:
     * [4 bytes tamaño][4 bytes tipo ascii][payload]
     */
    fun buildGenericBox(type: String, payload: ByteArray): ByteArray {
        val typeBytes = type.toByteArray(Charsets.US_ASCII)
        val totalSize = 4 + 4 + payload.size
        val buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(totalSize)
        buffer.put(typeBytes, 0, 4)
        buffer.put(payload)
        return buffer.array()
    }

    /**
     * Construye la caja de descripción JUMD:
     * [tamaño][jumd][16 bytes contentType][1 byte toggles][label null-terminated]
     */
    fun buildJumdDescriptionBox(label: String = "c2pa"): ByteArray {
        val labelBytes = label.toByteArray(Charsets.UTF_8)
        val payloadSize = JUMD_CONTENT_TYPE_C2PA.size + 1 + labelBytes.size + 1
        val buffer = ByteBuffer.allocate(payloadSize).order(ByteOrder.BIG_ENDIAN)
        buffer.put(JUMD_CONTENT_TYPE_C2PA)
        buffer.put(0x01.toByte()) // Request toggle: label present
        buffer.put(labelBytes)
        buffer.put(0x00.toByte()) // Null terminator
        return buildGenericBox(BOX_TYPE_JUMD, buffer.array())
    }

    /**
     * Construye el JUMBF Superbox completo conteniendo JUMD + JSON Manifest
     */
    fun buildJumbfSuperbox(jsonManifestPayload: ByteArray): ByteArray {
        val jumdBox = buildJumdDescriptionBox("c2pa")
        val jsonBox = buildGenericBox(BOX_TYPE_JSON, jsonManifestPayload)

        val superboxPayload = ByteArrayOutputStream().apply {
            write(jumdBox)
            write(jsonBox)
        }.toByteArray()

        return buildGenericBox(BOX_TYPE_JUMB, superboxPayload)
    }

    /**
     * Construye la caja superior MP4 'uuid' de C2PA:
     * [4 bytes tamaño][4 bytes 'uuid'][16 bytes C2PA_BOX_UUID][JUMBF Superbox]
     */
    fun buildC2paMp4UuidBox(manifest: C2paManifestStore): ByteArray {
        val jsonBytes = manifest.toJsonLd().toByteArray(Charsets.UTF_8)
        val jumbfBox = buildJumbfSuperbox(jsonBytes)

        val totalBoxSize = 4 + 4 + 16 + jumbfBox.size
        val buffer = ByteBuffer.allocate(totalBoxSize).order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(totalBoxSize)
        buffer.put(BOX_TYPE_UUID.toByteArray(Charsets.US_ASCII))
        buffer.put(C2PA_BOX_UUID)
        buffer.put(jumbfBox)
        return buffer.array()
    }

    /**
     * Comprueba si un fragmento binario de 16 bytes coincide con el UUID oficial de C2PA
     */
    fun isC2paUuid(bytes: ByteArray, offset: Int = 0): Boolean {
        if (bytes.size < offset + 16) return false
        for (i in 0 until 16) {
            if (bytes[offset + i] != C2PA_BOX_UUID[i]) return false
        }
        return true
    }
}
