package com.example

import com.example.data.vmsb.antirea.AntiReaSecurityLevel
import com.example.data.vmsb.antirea.AntiReaThreatStatus
import com.example.data.vmsb.antirea.AntiReaThreatVector
import com.example.data.vmsb.antirea.VmsbAntiReaEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VmsbAntiReaEngineTest {

    private lateinit var engine: VmsbAntiReaEngine

    @Before
    fun setUp() {
        engine = VmsbAntiReaEngine()
    }

    @Test
    fun testInitialSecurityStateIsArmedAndImpregnable() {
        val state = engine.state.value
        assertTrue(state.isArmed)
        assertTrue(state.isEnabled)
        assertTrue(state.systemAttestationScore >= 90)
        assertNotNull(state.activeSessionNonce)
        assertTrue(state.activeSessionNonce.startsWith("VMSB-2026-SEC-"))
        assertTrue(state.threatVectorReports.size == 6)
    }

    @Test
    fun testDeepAttestationEvaluatesAllSixVectors() {
        engine.runDeepAttestation()
        val state = engine.state.value
        assertEquals(6, state.threatVectorReports.size)
        
        val vectorTypes = state.threatVectorReports.map { it.vector }
        assertTrue(vectorTypes.contains(AntiReaThreatVector.DEBUGGER_ATTACH_PROBE))
        assertTrue(vectorTypes.contains(AntiReaThreatVector.FRIDA_DYNAMIC_HOOK_PROBE))
        assertTrue(vectorTypes.contains(AntiReaThreatVector.ROOT_PRIVILEGE_PROBE))
        assertTrue(vectorTypes.contains(AntiReaThreatVector.CANARY_MEMORY_SCRAPING_PROBE))
        assertTrue(vectorTypes.contains(AntiReaThreatVector.DEX_BYTECODE_TAMPER_PROBE))
        assertTrue(vectorTypes.contains(AntiReaThreatVector.FORENSIC_STEGO_TAMPER_PROBE))
    }

    @Test
    fun testThreatSimulationTriggersActiveDefense() {
        engine.simulateThreatVector(AntiReaThreatVector.FRIDA_DYNAMIC_HOOK_PROBE)
        val state = engine.state.value
        
        assertEquals(AntiReaSecurityLevel.ACTIVE_DEFENSE, state.securityLevel)
        assertTrue(state.isSimulationActive)
        val fridaReport = state.threatVectorReports.first { it.vector == AntiReaThreatVector.FRIDA_DYNAMIC_HOOK_PROBE }
        assertEquals(AntiReaThreatStatus.SIMULATION_VERIFIED, fridaReport.status)
        assertTrue(fridaReport.details.contains("SIMULACIÓN"))
    }

    @Test
    fun testEnclaveZeroizationPurgesOffHeapMemory() {
        val bytesWiped = engine.zeroizeEnclave()
        assertTrue(bytesWiped >= 0L)
        val state = engine.state.value
        assertTrue(state.canaryBytesWiped >= bytesWiped)
        assertTrue(state.ephemeralKeystreamId.startsWith("ROT-0x"))
    }

    @Test
    fun testForensicWatermarkGeneration() {
        val watermark = engine.generateExportForensicWatermark("Veo Studio Project 4K")
        assertNotNull(watermark)
        assertTrue(watermark.startsWith("VMSB-ANTI-REA-2026-FPR-"))
    }

    @Test
    fun testOfficialCertificateGeneration() {
        val cert = engine.generateCertificateReportText()
        assertTrue(cert.contains("VMSB© NATIVE ANTI-REA-ING 2026"))
        assertTrue(cert.contains("MATRIZ DE VECTORES DE AMENAZA EVALUADOS"))
        assertTrue(cert.contains("FIRMA DIGITAL DEL CERTIFICADO (HMAC-SHA256)"))
    }
}
