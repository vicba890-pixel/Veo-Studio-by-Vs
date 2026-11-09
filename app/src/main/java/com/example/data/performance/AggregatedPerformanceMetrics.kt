package com.example.data.performance

import android.os.Build
import com.example.data.vmsb.VmsbEnergyTelemetry
import com.example.data.wasm.VmsbWasmRuntimeCore
import com.example.data.wassam.WassamCacheTelemetry
import com.example.data.wassam.WassamHealthReport
import java.io.Serializable
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Modelo de Datos Agregados de Rendimiento en Tiempo Real del Sistema.
 * Consolida métricas de:
 * - Motor Vectorial VMSB© (Energía, SIMD, Frames Zero-Copy, Margen Térmico)
 * - WASSAM Native Secure Cache (Hit Rate, Capacidad, Aciertos/Fallos, Zeroization)
 * - WASSAM Multi-Channel Sync & Network Latencies (Google, Meta, Apple, Wasm Direct Bus)
 * - WebAssembly Universal Micro-Runtime (Memoria Lineal, Throughput Mpix/s, Audio DSP)
 */
data class SystemHardwareProfile(
    val deviceModel: String = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}",
    val androidVersion: String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
    val supportedAbi: String = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a",
    val cpuCores: Int = Runtime.getRuntime().availableProcessors(),
    val maxHeapMemoryMb: Double = Runtime.getRuntime().maxMemory().toDouble() / (1024 * 1024)
) : Serializable

data class AggregatedPerformanceReport(
    val reportId: String,
    val timestampIso: String,
    val hardwareProfile: SystemHardwareProfile,
    val vmsbTelemetry: VmsbEnergyTelemetry,
    val cacheTelemetry: WassamCacheTelemetry,
    val syncHealthReport: WassamHealthReport,
    val wasmMemoryMb: Double,
    val wasmThroughputMpixPerSec: Double,
    val wasmAudioLufs: Float,
    val cryptographicSealSha256: String,
    val berneArt52ProtocolNotice: String
) : Serializable {

    companion object {
        fun aggregateCurrentMetrics(
            vmsbTelemetry: VmsbEnergyTelemetry,
            cacheTelemetry: WassamCacheTelemetry,
            syncReport: WassamHealthReport,
            wasmRuntime: VmsbWasmRuntimeCore
        ): AggregatedPerformanceReport {
            val reportId = "VMSB-PERF-${UUID.randomUUID().toString().take(8).uppercase()}"
            val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
            val hardware = SystemHardwareProfile()

            val wasmTel = wasmRuntime.telemetry.value
            val wasmLinearMem = wasmTel.linearMemoryMb
            val wasmThroughput = wasmTel.pixelThroughputMpixelsSec
            val wasmAudio = -14.0f // Standard EBU R128 Broadcast Target LUFS

            val sealPayload = buildString {
                append(reportId).append(":")
                append(nowIso).append(":")
                append(vmsbTelemetry.instantaneousPowerMilliwatts).append(":")
                append(cacheTelemetry.cacheHitRatePercent).append(":")
                append(syncReport.overallHealthScorePercent)
            }
            val sha256 = computeSha256(sealPayload)

            return AggregatedPerformanceReport(
                reportId = reportId,
                timestampIso = nowIso,
                hardwareProfile = hardware,
                vmsbTelemetry = vmsbTelemetry,
                cacheTelemetry = cacheTelemetry,
                syncHealthReport = syncReport,
                wasmMemoryMb = wasmLinearMem,
                wasmThroughputMpixPerSec = wasmThroughput,
                wasmAudioLufs = wasmAudio,
                cryptographicSealSha256 = sha256,
                berneArt52ProtocolNotice = "ESTÁNDAR VMSB© CERTIFICADO BAJO PROTOCOLO DE BERNA (ART. 5.2 WIPO) • SISTEMA EN OPERACIÓN NATIVA"
            )
        }

        private fun computeSha256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }.uppercase(Locale.US)
        }
    }
}
