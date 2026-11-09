package com.example

import com.example.data.diagnostic.StressPhaseType
import com.example.data.diagnostic.TestStatus
import com.example.data.diagnostic.VmsbSystemStressAndBalanceSuite
import com.example.data.vmsb.antirea.VmsbAntiReaEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para la Suite de Sistema, Conectividad, Seguridad Anti-REA,
 * Stress Extremo y Balance de Carga (VmsbSystemStressAndBalanceSuite).
 */
class VmsbSystemStressAndBalanceSuiteTest {

    private lateinit var antiReaEngine: VmsbAntiReaEngine
    private lateinit var suite: VmsbSystemStressAndBalanceSuite

    @Before
    fun setUp() {
        antiReaEngine = VmsbAntiReaEngine()
        suite = VmsbSystemStressAndBalanceSuite(antiReaEngine)
    }

    @Test
    fun testExecuteFullSuiteProducesCertifiedReportWithFivePhases() = runBlocking {
        val progressPhases = mutableListOf<StressPhaseType>()
        val report = suite.executeFullSystemStressAndBalanceSuite { phase, _, _ ->
            if (!progressPhases.contains(phase)) {
                progressPhases.add(phase)
            }
        }

        assertNotNull(report)
        assertTrue(report.auditId.startsWith("VMSB-AUDIT-"))
        assertTrue(report.overallScorePercent >= 95f)
        assertTrue(report.overallStatus.contains("CERTIFICADO"))
        assertEquals(64, report.sha256CertificateSeal.length)
        assertEquals(64, report.c2paAttestationHash.length)
        assertTrue(report.berneConventionRegistration.contains("OMPI/WIPO"))

        // Verificar que las 5 fases se ejecutaron
        assertEquals(5, report.phases.size)
        val phaseTypes = report.phases.map { it.phase }
        assertTrue(phaseTypes.contains(StressPhaseType.SYSTEM_PIPELINE))
        assertTrue(phaseTypes.contains(StressPhaseType.CONNECTIVITY_RESILIENCE))
        assertTrue(phaseTypes.contains(StressPhaseType.SECURITY_ANTI_REA_2026))
        assertTrue(phaseTypes.contains(StressPhaseType.STRESS_CONCURRENCY))
        assertTrue(phaseTypes.contains(StressPhaseType.DISPATCHER_BALANCE))

        // Todas las fases deben estar en estado aprobatorio
        report.phases.forEach { phaseResult ->
            assertTrue(
                "Fase ${phaseResult.phase} falló con estado ${phaseResult.status}",
                phaseResult.status.isPassing
            )
            assertTrue(phaseResult.metrics.isNotEmpty())
            assertTrue(phaseResult.optimizationsApplied.isNotEmpty())
        }
    }

    @Test
    fun testSystemPipelinePhaseCalculatesValidMetrics() = runBlocking {
        val report = suite.executeFullSystemStressAndBalanceSuite()
        val sysPhase = report.phases.first { it.phase == StressPhaseType.SYSTEM_PIPELINE }

        assertEquals(TestStatus.PASS_OPTIMIZED, sysPhase.status)
        assertTrue(sysPhase.scorePercent >= 98f)
        assertTrue(sysPhase.primaryMetric.contains("FPS"))

        val fpsMetric = sysPhase.metrics.firstOrNull { it.label == "Throughput de Frames" }
        assertNotNull(fpsMetric)
        assertEquals("FPS", fpsMetric!!.unit)
    }

    @Test
    fun testSecurityAntiReaPhaseValidatesAllSixVectors() = runBlocking {
        val report = suite.executeFullSystemStressAndBalanceSuite()
        val secPhase = report.phases.first { it.phase == StressPhaseType.SECURITY_ANTI_REA_2026 }

        assertEquals(TestStatus.PASS_OPTIMIZED, secPhase.status)
        assertEquals(100f, secPhase.scorePercent)
        assertTrue(secPhase.primaryMetric.contains("100/100"))

        val vectorsMetric = secPhase.metrics.firstOrNull { it.label == "Vectores Soberanos Blindados" }
        assertNotNull(vectorsMetric)
        assertEquals("6 / 6", vectorsMetric!!.value)
    }

    @Test
    fun testStressConcurrencyPhaseExecutesMctsAndTracks() = runBlocking {
        val report = suite.executeFullSystemStressAndBalanceSuite()
        val strPhase = report.phases.first { it.phase == StressPhaseType.STRESS_CONCURRENCY }

        assertTrue(strPhase.status.isPassing)
        assertTrue(strPhase.primaryMetric.contains("5,000 MCTS"))
        assertTrue(strPhase.primaryMetric.contains("16 Tracks"))

        val mctsMetric = strPhase.metrics.firstOrNull { it.label == "Simulaciones MCTS UCB1" }
        assertNotNull(mctsMetric)
        assertEquals("5000", mctsMetric!!.value)

        val frameDropMetric = strPhase.metrics.firstOrNull { it.label == "Tasa de Fotogramas Perdidos" }
        assertNotNull(frameDropMetric)
        assertEquals("0.00", frameDropMetric!!.value)
    }

    @Test
    fun testDispatcherBalancePhaseMeasuresEtaAbove95Percent() = runBlocking {
        val report = suite.executeFullSystemStressAndBalanceSuite()
        val balPhase = report.phases.first { it.phase == StressPhaseType.DISPATCHER_BALANCE }

        assertTrue(balPhase.status.isPassing)
        assertTrue(balPhase.scorePercent >= 95f)

        val etaMetric = balPhase.metrics.firstOrNull { it.label == "Load Balance Factor (η)" }
        assertNotNull(etaMetric)
        val etaVal = etaMetric!!.value.toDoubleOrNull()
        assertNotNull(etaVal)
        assertTrue(etaVal!! >= 95.0)
    }
}
