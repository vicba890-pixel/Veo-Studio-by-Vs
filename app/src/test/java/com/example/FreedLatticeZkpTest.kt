package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.Project
import com.example.data.freed.FreedAuditEngine
import com.example.data.freed.FreedAuditState
import com.example.data.pdf.FreedComplianceReportPdfGenerator
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * =========================================================================================
 * PRUEBAS ROBOLECTRIC: FREED_vmsb_lattice_ZKP INNOVATION SUBSYSTEM & PDF COMPLIANCE REPORT
 * =========================================================================================
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FreedLatticeZkpTest {

    @Test
    fun testFreedAuditEngineComputesDeterministicVerificationAndTelemetry() {
        val project = Project(
            id = 42,
            title = "Cinematic Neon Cyberpunk Horizon",
            prompt = "Anamorphic master shot of a futuristic metropolis powered by residual energy",
            aspectRatio = "16:9",
            resolution = "4K",
            durationSec = 20,
            selectedMood = "Blade Runner 2049 Neo-Noir",
            videoUri = "content://media/external/video/media/42"
        )

        val status = FreedAuditEngine.evaluateProjectFreedAudit(project)

        assertNotNull(status)
        assertEquals(FreedAuditState.VERIFIED, status.state)
        assertTrue("Verification score must be high", status.verificationScore >= 99.0)
        assertTrue("Certificate ID must begin with FREED-VMSB-ZKP-2026", status.auditCertificateId.startsWith("FREED-VMSB-ZKP-2026-"))
        assertEquals(64, status.proofHash.length) // SHA-256 length
        assertTrue(status.dilithiumSignatureFingerprint.contains("DILITHIUM-65-SIG"))

        // Telemetry Assertions
        val telemetry = status.telemetry
        assertTrue("Harvested uW must be positive", telemetry.harvestedMicroWatts > 100.0)
        assertTrue("Energy saving percentage must be between 30% and 50%", telemetry.energySavingPercentage in 30.0..50.0)
        assertTrue("Latency reduction must be between 40% and 50%", telemetry.latencyReductionPercentage in 40.0..50.0)
        assertEquals(-24.8, telemetry.noiseCarrierSnrDb, 0.1)
        assertTrue("Conventional energy offset must be positive", telemetry.conventionalEnergyOffsetJoules > 0.0)
        assertTrue("CO2 saved must be positive", telemetry.co2SavedGrams > 0.0)

        // Incentive & Compliance Assertions
        val incentive = status.incentive
        assertTrue(incentive.taxCreditEligibilityCode.contains("GREEN-AI"))
        assertTrue(incentive.esgRatingTier.contains("Tier AAA+"))
        assertEquals("NIST FIPS 204 (ML-DSA-65) & NIST FIPS 203 (ML-KEM-768)", incentive.nistStandardCompliance)
        assertTrue(status.isComplianceReportReady)
    }

    @Test
    fun testGenerateSignedCompliancePdfProducesValidFile() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val project = Project(
            id = 101,
            title = "Veo Studio Sovereign AI Trailer",
            prompt = "High production teaser demonstrating post-quantum digital integrity",
            aspectRatio = "9:16",
            resolution = "1080p",
            durationSec = 15,
            selectedMood = "Teal & Orange Blockbuster",
            videoUri = "content://media/external/video/media/101"
        )

        val status = FreedAuditEngine.evaluateProjectFreedAudit(project)
        val pdfFile = FreedComplianceReportPdfGenerator.generateSignedCompliancePdf(context, project, status)

        assertNotNull(pdfFile)
        assertTrue("PDF file must exist on disk", pdfFile.exists())
        assertTrue("PDF file size must be greater than 1KB", pdfFile.length() > 1024)
        assertTrue("File name must start with FREED_ZKP_Compliance_", pdfFile.name.startsWith("FREED_ZKP_Compliance_"))
        assertTrue("File extension must be .pdf", pdfFile.name.endsWith(".pdf"))
    }
}
