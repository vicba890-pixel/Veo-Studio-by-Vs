package com.example

import com.example.data.c2pa.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.RandomAccessFile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class C2paCryptographicServiceTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val service = C2paCryptographicService()

    @Test
    fun testComputeSha256ProducesAccurateHash() {
        val input = "VEO_STUDIO_C2PA_TEST_STREAM".toByteArray(Charsets.UTF_8)
        val hash = service.computeSha256(input)
        assertNotNull(hash)
        assertEquals(64, hash.length)

        // Same input should be deterministic
        val hash2 = service.computeSha256(input)
        assertEquals(hash, hash2)

        // Different input should produce different hash
        val hashDifferent = service.computeSha256("OTHER_STREAM".toByteArray(Charsets.UTF_8))
        assertNotEquals(hash, hashDifferent)
    }

    @Test
    fun testJumbfBoxBuilderCreatesValidC2paBox() {
        val manifest = service.generateManifest(
            title = "Test Master 4K",
            promptText = "Drone coastal flythrough",
            mediaBitstreamSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        )

        val uuidBoxBytes = C2paJumbfBoxBuilder.buildC2paMp4UuidBox(manifest)
        assertTrue(uuidBoxBytes.size > 100)

        // First 4 bytes are size
        val size = java.nio.ByteBuffer.wrap(uuidBoxBytes, 0, 4).int
        assertEquals(uuidBoxBytes.size, size)

        // Next 4 bytes are "uuid"
        val type = String(uuidBoxBytes, 4, 4, Charsets.US_ASCII)
        assertEquals("uuid", type)

        // Next 16 bytes match C2PA_BOX_UUID
        assertTrue(C2paJumbfBoxBuilder.isC2paUuid(uuidBoxBytes, 8))
    }

    @Test
    fun testEmbedAndExtractManifestInMp4() {
        val srcFile = tempFolder.newFile("sample_video.mp4")
        val outFile = File(tempFolder.root, "signed_video.mp4")

        // Construct synthetic valid ISOBMFF MP4 structure: ftyp + mdat
        val ftypPayload = "isommp42".toByteArray()
        val ftypBox = C2paJumbfBoxBuilder.buildGenericBox("ftyp", ftypPayload)
        val mdatPayload = "VIDEO_FRAME_STREAM_DATA_4K_REPRESENTATION".toByteArray()
        val mdatBox = C2paJumbfBoxBuilder.buildGenericBox("mdat", mdatPayload)
        srcFile.writeBytes(ftypBox + mdatBox)

        val rawSha256 = service.computeFileSha256(srcFile)
        val manifest = service.generateManifest(
            title = "Cinematic Shot 01",
            promptText = "A golden hour landscape",
            mediaBitstreamSha256 = rawSha256
        )

        // 1. Embed
        val embedResult = service.embedManifestIntoMp4(srcFile, outFile, manifest)
        assertTrue(embedResult.success)
        assertTrue(outFile.exists())
        assertTrue(outFile.length() > srcFile.length())
        assertEquals(rawSha256, embedResult.assetSha256Fingerprint)

        // 2. Extract
        val extractionResult = service.extractManifestFromMp4(outFile)
        assertTrue(extractionResult.found)
        assertNotNull(extractionResult.manifest)
        assertEquals(manifest.manifestId, extractionResult.manifest?.manifestId)
        assertEquals("Cinematic Shot 01", extractionResult.manifest?.assetTitle)
        assertEquals(rawSha256, extractionResult.manifest?.assetSha256)
    }

    @Test
    fun testVerifyVideoProvenanceAndDetectTampering() {
        val srcFile = tempFolder.newFile("sample_original.mp4")
        val outFile = File(tempFolder.root, "sample_c2pa_signed.mp4")

        val ftypBox = C2paJumbfBoxBuilder.buildGenericBox("ftyp", "isom".toByteArray())
        val mdatBox = C2paJumbfBoxBuilder.buildGenericBox("mdat", "ORIGINAL_UNALTERED_MEDIA_BITSTREAM".toByteArray())
        srcFile.writeBytes(ftypBox + mdatBox)

        // We compute the hash of non-c2pa content
        val rawSha256 = service.computeFileSha256(srcFile)
        val manifest = service.generateManifest(
            title = "Provenance Test Video",
            promptText = "Unaltered media track",
            mediaBitstreamSha256 = rawSha256
        )

        val embedRes = service.embedManifestIntoMp4(srcFile, outFile, manifest)
        assertTrue(embedRes.success)

        // Verify valid provenance
        val verifyOk = service.verifyVideoProvenance(outFile)
        assertTrue(verifyOk.isAuthentic)
        assertFalse(verifyOk.isTampered)
        assertEquals(rawSha256, verifyOk.expectedSha256)
        assertTrue(verifyOk.securityVerdict.contains("AUTENTICIDAD CERTIFICADA"))

        // Now TAMPER with the file by modifying a byte in the media track at the end of the file
        val tamperedFile = File(tempFolder.root, "sample_tampered.mp4")
        tamperedFile.writeBytes(outFile.readBytes())
        RandomAccessFile(tamperedFile, "rw").use { raf ->
            raf.seek(tamperedFile.length() - 5)
            raf.writeByte(0xFF) // Alter media bitstream byte
        }

        // Verify that tampering is instantly caught!
        val verifyTampered = service.verifyVideoProvenance(tamperedFile)
        assertFalse(verifyTampered.isAuthentic)
        assertTrue(verifyTampered.isTampered)
        assertTrue(verifyTampered.securityVerdict.contains("ALERTA CRÍTICA"))
    }
}
