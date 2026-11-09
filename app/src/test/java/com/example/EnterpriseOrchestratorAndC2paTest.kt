package com.example

import com.example.data.orchestrator.VmsbNativeOrchestratorMatrix
import com.example.data.orchestrator.StreamPartitionType
import com.example.data.c2pa.C2paContentCredentialsEngine
import com.example.data.verticals.B2BVerticalRegistry
import com.example.data.verticals.B2BVerticalCategory
import com.example.data.deterministic.DeterministicCharacterContinuityEngine
import com.example.data.deterministic.CameraTrajectoryType
import com.example.data.credits.B2BHybridCreditsManager
import com.example.data.credits.B2BSubscriptionTier
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class EnterpriseOrchestratorAndC2paTest {

    @Test
    fun testVmsbOrchestratorMatrixGeneratesValidPartitionsAndZkp() {
        val orchestrator = VmsbNativeOrchestratorMatrix()
        val partitions = orchestrator.generateLivePartitions()

        assertEquals(5, partitions.size)
        assertTrue(partitions.any { it.type == StreamPartitionType.VIDEO_FRAME_STREAM })
        assertTrue(partitions.any { it.type == StreamPartitionType.AUDIO_EBU_STREAM })
        assertTrue(partitions.any { it.type == StreamPartitionType.ZKP_PROVENANCE_PROOFS })

        val zkp = orchestrator.generateSnarkProof("TEST_PAYLOAD".toByteArray())
        assertNotNull(zkp.proofHash)
        assertTrue(zkp.isVerified)
        assertTrue(zkp.circuitName.contains("Circom"))

        val jurisdictions = orchestrator.getJurisdictionMatrix()
        assertEquals(3, jurisdictions.size)
        assertTrue(jurisdictions.any { it.jurisdictionCode == "EU" && it.regulationName.contains("2024/1689") })
        assertTrue(jurisdictions.any { it.jurisdictionCode == "US" && it.regulationName.contains("FTC") })
    }

    @Test
    fun testC2paContentCredentialsGeneratesValidManifestAndJsonLd() {
        val manifest = C2paContentCredentialsEngine.generateManifest(
            title = "Cinematic Master Shot",
            promptText = "Drone view of coastal cliffs at dusk"
        )

        assertNotNull(manifest.manifestId)
        assertTrue(manifest.manifestId.startsWith("urn:c2pa:manifest:uuid:"))
        assertEquals(64, manifest.assetSha256.length) // SHA-256 is 64 hex chars
        assertEquals(4, manifest.actions.size)
        assertTrue(manifest.actions.any { it.action == "c2pa.created" })
        assertTrue(manifest.actions.any { it.action == "c2pa.audio_normalized" })
        assertTrue(manifest.aiAssertion.isSyntheticMedia)

        val jsonLd = manifest.toJsonLd()
        assertTrue(jsonLd.contains("c2pa.org/spec/v2.1"))
        assertTrue(jsonLd.contains(manifest.assetSha256))
        assertTrue(jsonLd.contains("EU AI Act Art. 50"))
    }

    @Test
    fun testB2BVerticalRegistryPresets() {
        val presets = B2BVerticalRegistry.presets
        assertEquals(3, presets.size)

        val realEstate = presets.first { it.category == B2BVerticalCategory.REAL_ESTATE }
        assertEquals("16:9", realEstate.targetAspectRatio)
        assertEquals(30, realEstate.durationSec)
        assertTrue(realEstate.onScreenOverlays.isNotEmpty())

        val ecom = presets.first { it.category == B2BVerticalCategory.ECOMMERCE_RETAIL }
        assertEquals("9:16", ecom.targetAspectRatio)
        assertEquals(15, ecom.durationSec)

        val edtech = presets.first { it.category == B2BVerticalCategory.EDTECH_CORPORATE }
        assertEquals(45, edtech.durationSec)
    }

    @Test
    fun testDeterministicContinuityEngineSeedLockAndStoryline() {
        val engine = DeterministicCharacterContinuityEngine()
        val anchor = engine.getActiveAnchor()
        assertTrue(anchor.isSeedLocked)
        assertTrue(anchor.identityPreservationScore > 0.95f)

        val report = engine.generateDeterministicStoryline("CEO interview sequence in corporate office")
        assertEquals(3, report.totalScenes)
        assertTrue(report.averageFidelityScorePercent > 95f)
        assertTrue(report.isZeroDriftGuaranteed)

        assertEquals(CameraTrajectoryType.CRANE_PEDESTAL, report.nodes[0].cameraTrajectory)
        assertEquals(CameraTrajectoryType.DOLLY_PUSH_IN, report.nodes[1].cameraTrajectory)
        assertEquals(CameraTrajectoryType.ORBITAL_360, report.nodes[2].cameraTrajectory)
    }

    @Test
    fun testB2BHybridCreditsManager() {
        val manager = B2BHybridCreditsManager()
        val initial = manager.getStatus()
        assertEquals(B2BSubscriptionTier.PRO_ENTERPRISE, initial.tier)
        assertTrue(initial.totalCreditsRemaining > 4000)

        val consumed = manager.consumeCloudCredits(50)
        assertTrue(consumed)
        assertEquals(initial.totalCreditsRemaining - 50, manager.getStatus().totalCreditsRemaining)

        val freeRenderStatus = manager.recordFreeLocalRender()
        assertTrue(freeRenderStatus.localRendersCompletedFree > 0)

        val switched = manager.switchTier(B2BSubscriptionTier.FREEMIUM_COMMUNITY)
        assertEquals(B2BSubscriptionTier.FREEMIUM_COMMUNITY, switched.tier)
        assertTrue(switched.tier.isUnlimitedLocalNle)
    }

    @Test
    fun testEnterpriseAuditReportAndConnectivityMetrics() {
        val connectivity = com.example.data.vmsb.ConnectivityBenchmarkData()
        assertTrue(connectivity.isGoogleMetaLiveHandshakeVerified)
        assertTrue(connectivity.googleLatencyMs in 10.0..200.0)
        assertTrue(connectivity.metaLatencyMs in 10.0..200.0)
        assertEquals(200, connectivity.googleHttpStatus)

        val milestones = com.example.data.vmsb.VmsbEnterpriseAuditReportGenerator.defaultMilestones
        assertEquals(4, milestones.size)
        assertTrue(milestones.any { it.period.contains("3 Meses") })
        assertTrue(milestones.any { it.period.contains("6 Meses") })
        assertTrue(milestones.any { it.period.contains("1 Año") })
        assertTrue(milestones.any { it.period.contains("3 Años") && it.cumulativeRoiPercent.contains("18.4x") })
    }
}
