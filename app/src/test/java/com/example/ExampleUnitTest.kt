package com.example

import com.example.data.EcoComputeMode
import com.example.data.PlatformRegistry
import com.example.data.Project
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPlatformRegistryMajorPlatforms() {
    val platforms = PlatformRegistry.majorPlatforms
    assertTrue("Major platforms registry should not be empty", platforms.isNotEmpty())
    
    val youtube = platforms.find { it.id == "youtube" }
    assertNotNull("YouTube spec should exist", youtube)
    assertEquals("16:9", youtube?.primaryAspectRatio)
    assertEquals(-14.0f, youtube?.targetLufs ?: 0f, 0.01f)

    val tiktok = platforms.find { it.id == "tiktok" }
    assertNotNull("TikTok spec should exist", tiktok)
    assertEquals("9:16", tiktok?.primaryAspectRatio)
    assertEquals(-16.0f, tiktok?.targetLufs ?: 0f, 0.01f)

    val instagram = platforms.find { it.id == "instagram" }
    assertNotNull("Instagram spec should exist", instagram)
    assertEquals("9:16", instagram?.primaryAspectRatio)

    val linkedin = platforms.find { it.id == "linkedin" }
    assertNotNull("LinkedIn spec should exist", linkedin)
    assertTrue("LinkedIn should have safe zones", linkedin?.safeZoneDescription?.isNotBlank() == true)
  }

  @Test
  fun testEcoComputeModes() {
    val greenMode = EcoComputeMode.GREEN_ECO
    assertTrue(greenMode.energySavingPercent >= 50)

    val turboMode = EcoComputeMode.ULTRA_LOW_LATENCY
    assertTrue(turboMode.latencyMs <= 100)

    val cinemaMode = EcoComputeMode.CINEMA_MASTER
    assertTrue(cinemaMode.latencyMs >= 200)
  }

  @Test
  fun testRenderPalettesAndDynamics() {
    val palettes = com.example.data.RenderPaletteRegistry.allPalettes
    assertTrue("Render palettes should have at least 5 luxury options", palettes.size >= 6)

    val champagne = com.example.data.RenderPaletteRegistry.CHAMPAGNE_VELVET
    assertEquals("Champagne & Cashmere", champagne.name)
    assertEquals(3200, champagne.temperatureKelvin)
    assertTrue("Champagne should be skin-tone optimized", champagne.skinToneOptimized)
    assertTrue("Gradient colors should have 5 stops", champagne.gradientColors.size >= 4)

    val profile = com.example.data.DynamicOptimizationProfile()
    assertEquals(60, profile.framePacingTargetFps)
    assertTrue("Smooth lerp scrubbing should be enabled by default", profile.isSmoothLerpScrubbingEnabled)
    assertTrue("Thermal efficiency should be optimal", profile.currentThermalEfficiencyScore >= 95)
  }

  @Test
  fun testVmsbBinaryProtocolZeroCopySerialization() {
    val payload = "Veo 3.1 Cinematic Frame Vector Data".toByteArray(Charsets.UTF_8)
    val frameIndex = 1001L
    val timestamp = System.nanoTime()
    val crc = com.example.data.vmsb.VmsbFramePacket.computeCrc32(payload)
    val hmac = com.example.data.vmsb.VmsbFramePacket.computeHmacSha256(frameIndex, timestamp, payload)

    val packet = com.example.data.vmsb.VmsbFramePacket(
        frameIndex = frameIndex,
        timestampNanos = timestamp,
        frameType = com.example.data.vmsb.VmsbFrameType.I_FRAME_KEY,
        flags = com.example.data.vmsb.VmsbFramePacket.FLAG_ZERO_COPY,
        payloadSizeBytes = payload.size,
        crc32Checksum = crc,
        hmacSha256Seal = hmac
    )

    val directBuffer = java.nio.ByteBuffer.allocateDirect(1024)
    com.example.data.vmsb.VmsbFramePacket.serializeToDirectBuffer(packet, payload, directBuffer)

    val deserialized = com.example.data.vmsb.VmsbFramePacket.deserializeFromDirectBuffer(directBuffer)
    assertNotNull("Deserialized packet should not be null", deserialized)
    assertEquals(packet.frameIndex, deserialized?.first?.frameIndex)
    assertEquals(packet.crc32Checksum, deserialized?.first?.crc32Checksum)
    assertArrayEquals(hmac, deserialized?.first?.hmacSha256Seal)
    assertArrayEquals(payload, deserialized?.second)
  }

  @Test
  fun testVmsbFakfaStreamingRingBuffer() {
    val cluster = com.example.data.vmsb.FakfaStreamingCluster()
    val part0 = cluster.partition0VideoMaster

    val payload = "Video Chunk Zero-Copy Payload".toByteArray()
    val index = 42L
    val ts = System.nanoTime()
    val crc = com.example.data.vmsb.VmsbFramePacket.computeCrc32(payload)
    val hmac = com.example.data.vmsb.VmsbFramePacket.computeHmacSha256(index, ts, payload)

    val packet = com.example.data.vmsb.VmsbFramePacket(
        frameIndex = index,
        timestampNanos = ts,
        frameType = com.example.data.vmsb.VmsbFrameType.P_FRAME_PREDICTED,
        flags = com.example.data.vmsb.VmsbFramePacket.FLAG_ZERO_COPY,
        payloadSizeBytes = payload.size,
        crc32Checksum = crc,
        hmacSha256Seal = hmac
    )

    val offset = part0.produce(packet, payload)
    assertEquals(0L, offset)

    val polled = part0.poll()
    assertNotNull("Polled item should exist", polled)
    assertEquals(index, polled?.first?.frameIndex)

    part0.commit(offset + 1)
    val metrics = part0.getMetrics()
    assertEquals(0L, metrics.lag)
    assertEquals(1L, metrics.packetsProcessed)
    assertTrue("Direct memory should be allocated", metrics.directMemoryUsedKb > 0)
  }

  @Test
  fun testVmsbMctsOptimizationTree() {
    val mcts = com.example.data.vmsb.VmsbMctsEngine()
    val plan = mcts.optimizePipeline(
        iterations = 250,
        batteryPercent = 90,
        thermalThrottling = false,
        preferUltraLowLatency = true
    )

    assertNotNull("MCTS plan should not be null", plan)
    assertTrue("Should run requested iterations", plan.totalSimulationsRun == 250)
    assertTrue("Winning decision should have positive latency", plan.estimatedLatencyMicroseconds > 0)
    assertTrue("Estimated energy should be calculated in microjoules", plan.estimatedEnergyMicrojoules > 0)
    assertTrue("SSIM quality should be high", plan.qualityScoreSsim >= 0.70)
    assertTrue("Explored summary should have candidate branches", plan.exploredNodesSummary.isNotEmpty())
  }

  @Test
  fun testIosCameraModesAndLenses() {
    val modes = com.example.data.camera.IosCameraMode.values()
    assertTrue("Should include Cinematic mode", modes.contains(com.example.data.camera.IosCameraMode.CINEMATIC))
    assertTrue("Should include ProRes Log mode", modes.contains(com.example.data.camera.IosCameraMode.PRORES_LOG))
    assertTrue("Should include Spatial 3D mode", modes.contains(com.example.data.camera.IosCameraMode.SPATIAL_3D))

    val cinematic = com.example.data.camera.IosCameraMode.CINEMATIC
    assertEquals("CINEMÁTICO", cinematic.label)
    assertEquals(60, cinematic.maxFps)

    val lenses = com.example.data.camera.IosLens.values()
    assertEquals(4, lenses.size)
    assertEquals(".5", com.example.data.camera.IosLens.ULTRA_WIDE.label)
    assertEquals("1x", com.example.data.camera.IosLens.WIDE.label)
    assertEquals("2x", com.example.data.camera.IosLens.TELE_2X.label)
    assertEquals("5x", com.example.data.camera.IosLens.TELE_5X.label)
  }

  @Test
  fun testLatticeZeroDataProtector() {
    val protector = com.example.data.camera.LatticeZeroDataProtector()
    val status = protector.getSecurityStatus()

    assertTrue("Lattice cryptography should be active", status.isPostQuantumLatticeActive)
    assertTrue("Zero-Data should be guaranteed", status.isZeroDataGuaranteed)
    assertTrue("GDPR Art. 25 compliant", status.gdprArticle25Compliant)
    assertTrue("CCPA compliant", status.ccpaZeroSaleCertified)

    val signature = protector.signLatticeFrame(1L, "Sample Payload".toByteArray())
    assertTrue("Lattice signature should start with prefix", signature.startsWith("LAT-DILITHIUM-"))

    val logs = protector.getAuditLogs()
    assertTrue("Audit logs should contain initialization event", logs.isNotEmpty())
  }

  @Test
  fun testBerneConventionLedgerSealingAndCertificate() {
    val protector = com.example.data.camera.LatticeZeroDataProtector()
    val ledger = com.example.data.camera.BerneConventionLedger(protector)

    val block = ledger.sealMediaWork(
        title = "Test Master Shot 4K",
        payloadBytes = "RAW_CAMERA_BYTES_TEST".toByteArray()
    )

    assertNotNull("Block should be created", block)
    assertTrue("Block index should be valid", block.blockIndex >= 1000L)
    assertTrue("SHA-256 hash should be 64 hex chars", block.payloadSha256.length == 64)
    assertTrue("Lattice signature should be present", block.latticePostQuantumSignature.startsWith("LAT-DILITHIUM-"))
    assertTrue("Legal notice should mention Protocolo de Berna", block.creatorLegalNotice.contains("PROTOCOLO DE BERNA"))

    val certificate = ledger.generateCertificate(block)
    assertNotNull("Certificate should be generated", certificate)
    assertTrue("Certificate should have Berna ID", certificate.certificateId.startsWith("BERNE-VMSB-"))
    assertTrue("Document text should contain legal rights reserved text", certificate.formattedLegalDocument.contains("TODOS LOS DERECHOS RESERVADOS"))
  }

  @Test
  fun testUniversalConnectivityHubFcpxmlAndEdl() {
    val hub = com.example.data.camera.NativeUniversalConnectivityHub()
    val clip = com.example.data.camera.CameraRecordedClip(
        id = "CLIP-001",
        title = "Scene 1 Take 1",
        timestampMillis = System.currentTimeMillis(),
        durationSeconds = 12.5,
        mode = com.example.data.camera.IosCameraMode.CINEMATIC,
        lens = com.example.data.camera.IosLens.WIDE,
        resolution = "4K (3840x2160)",
        fps = 60,
        totalVmsbFramesStreamed = 750L,
        berneLedgerHash = "abcdef1234567890abcdef1234567890",
        latticeProofSignature = "LAT-DILITHIUM-998877",
        zeroDataVerified = true
    )

    val fcpxml = hub.generateFcpxmlProject("Master Film", listOf(clip))
    assertTrue("Should generate valid XML", fcpxml.startsWith("<?xml"))
    assertTrue("Should include FCPXML version", fcpxml.contains("<fcpxml version=\"1.11\">"))
    assertTrue("Should embed Berne ledger hash in asset-clip metadata", fcpxml.contains("com.apple.proapps.studio.berneLedgerHash"))

    val edl = hub.generateCmx3600Edl("Master Film", listOf(clip))
    assertTrue("Should generate EDL header", edl.contains("TITLE: Master Film"))
    assertTrue("Should include SMPTE event timecode", edl.contains("001  AX       V     C"))
    assertTrue("Should embed Berne hash in EDL note", edl.contains("BERNE LEDGER HASH:"))
  }
}

