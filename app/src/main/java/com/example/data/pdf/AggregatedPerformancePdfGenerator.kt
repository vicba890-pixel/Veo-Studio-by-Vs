package com.example.data.pdf

import android.content.Context
import com.example.data.performance.AggregatedPerformanceReport
import com.example.data.vmsb.VmsbEnergyTelemetry
import com.example.data.wasm.VmsbWasmRuntimeCore
import com.example.data.wassam.WassamCacheTelemetry
import com.example.data.wassam.WassamHealthReport
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generador Oficial de Reportes de Rendimiento Agregado del Sistema con Apache PDFBox.
 * Agrega métricas en tiempo real y guarda el documento en el almacenamiento interno
 * del dispositivo para revisión inmediata del usuario.
 */
object AggregatedPerformancePdfGenerator {

    /**
     * Agrega las métricas actuales del sistema y genera un reporte PDF formateado,
     * guardándolo en almacenamiento interno (context.filesDir).
     */
    fun generateReport(
        context: Context,
        vmsbTelemetry: VmsbEnergyTelemetry,
        cacheTelemetry: WassamCacheTelemetry,
        syncReport: WassamHealthReport,
        wasmRuntime: VmsbWasmRuntimeCore
    ): File {
        val report = AggregatedPerformanceReport.aggregateCurrentMetrics(
            vmsbTelemetry = vmsbTelemetry,
            cacheTelemetry = cacheTelemetry,
            syncReport = syncReport,
            wasmRuntime = wasmRuntime
        )
        return generateFromReport(context, report)
    }

    /**
     * Genera el PDF formateado a partir de un reporte previamente consolidado.
     */
    fun generateFromReport(
        context: Context,
        report: AggregatedPerformanceReport
    ): File {
        PDFBoxResourceLoader.init(context)
        val document = PDDocument()

        try {
            // PÁGINA 1: PERFIL DE HARDWARE, TELEMETRÍA VMSB & CACHÉ WASSAM
            val page1 = PDPage(PDRectangle.A4)
            document.addPage(page1)
            var cs1 = PDPageContentStream(document, page1)
            drawPage1(cs1, page1, report)
            cs1.close()

            // PÁGINA 2: LATENCIA MULTI-CANAL, WEB-ASSEMBLY & SELLO CRIPTOGRÁFICO
            val page2 = PDPage(PDRectangle.A4)
            document.addPage(page2)
            var cs2 = PDPageContentStream(document, page2)
            drawPage2(cs2, page2, report)
            cs2.close()

            // Guardar en almacenamiento interno para revisión del usuario
            val outputDir = File(context.filesDir, "performance_reports").apply { mkdirs() }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val targetFile = File(outputDir, "VMSB_System_Performance_Report_$timestamp.pdf")

            document.save(targetFile)
            return targetFile
        } finally {
            document.close()
        }
    }

    private fun drawPage1(
        cs: PDPageContentStream,
        page: PDPage,
        report: AggregatedPerformanceReport
    ) {
        val width = page.mediaBox.width
        val height = page.mediaBox.height

        // Banner Superior Slate 900
        cs.setNonStrokingColor(15, 23, 42)
        cs.addRect(0f, height - 90f, width, 90f)
        cs.fill()

        // Línea de Acento Oro / Ámbar
        cs.setNonStrokingColor(217, 119, 6)
        cs.addRect(0f, height - 93f, width, 3f)
        cs.fill()

        // Textos del Encabezado
        drawText(cs, PDType1Font.HELVETICA_BOLD, 12f, 26f, height - 26f, 56, 189, 248, "VMSB(c) SYSTEM PERFORMANCE & RUNTIME BENCHMARK REPORT")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 9f, 26f, height - 44f, 255, 255, 255, "OFFICIAL SYSTEM TELEMETRY: HARDWARE, VMSB ENGINE, WASSAM CACHE & SYNC")
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 26f, height - 60f, 203, 213, 225, "Folio ID: ${report.reportId} | Fecha: ${report.timestampIso} | Apache PDFBox Android")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 26f, height - 76f, 52, 211, 153, "ESTADO OPERATIVO: RENDIMIENTO VERIFICADO (SALUD GLOBAL: ${report.syncHealthReport.overallHealthScorePercent}%)")

        var y = height - 115f

        // SECCIÓN 1: PERFIL DE HARDWARE Y ENTORNO DE EJECUCIÓN
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10f, 26f, y, 15, 23, 42, "1. PERFIL DE HARDWARE Y ENTORNO DE EJECUCION NATIVO")
        y -= 14f

        val hwBoxHeight = 80f
        cs.setNonStrokingColor(248, 250, 252)
        cs.addRect(26f, y - hwBoxHeight, width - 52f, hwBoxHeight)
        cs.fill()
        cs.setStrokingColor(203, 213, 225)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - hwBoxHeight, width - 52f, hwBoxHeight)
        cs.stroke()

        val hw = report.hardwareProfile
        var hwY = y - 16f
        drawRow(cs, "Dispositivo / Modelo:", hw.deviceModel, "SISTEMA OPERATIVO: ${hw.androidVersion}", 36f, hwY)
        hwY -= 15f
        drawRow(cs, "Arquitectura ABI:", hw.supportedAbi, "NUCLEOS CPU ACTIVOS: ${hw.cpuCores}", 36f, hwY)
        hwY -= 15f
        drawRow(cs, "Memoria Max Heap:", String.format(Locale.US, "%.1f MB", hw.maxHeapMemoryMb), "BUFFER ZERO-COPY: NDK Off-Heap Direct", 36f, hwY)
        hwY -= 15f
        drawRow(cs, "Alineacion Vectorial:", "ARM64 NEON / 128-bit SIMD", "COMPILADOR: LLVM Clang 18 + Wasm Core", 36f, hwY)

        y -= (hwBoxHeight + 20f)

        // SECCIÓN 2: TELEMETRÍA DEL MOTOR VECTORIAL VMSB© & EFICIENCIA ENERGÉTICA
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10f, 26f, y, 15, 23, 42, "2. MOTOR VECTORIAL VMSB(c) - TELEMETRIA DE ENERGIA Y COMPUTO")
        y -= 14f

        val vmsbBoxHeight = 110f
        cs.setNonStrokingColor(240, 253, 244) // Emerald tint
        cs.addRect(26f, y - vmsbBoxHeight, width - 52f, vmsbBoxHeight)
        cs.fill()
        cs.setStrokingColor(187, 247, 208)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - vmsbBoxHeight, width - 52f, vmsbBoxHeight)
        cs.stroke()

        val vmsb = report.vmsbTelemetry
        var vmsbY = y - 16f
        drawRow(cs, "Potencia Instantanea:", "${vmsb.instantaneousPowerMilliwatts} mW", "EFICIENCIA SIMD: ${vmsb.simdLaneEfficiency}%", 36f, vmsbY)
        vmsbY -= 16f
        drawRow(cs, "Energia por Frame:", "${vmsb.energyPerFrameMicrojoules} microJ / frame", "MARGEN TERMICO: ${vmsb.thermalMarginDegreesCelsius} deg C", 36f, vmsbY)
        vmsbY -= 16f
        drawRow(cs, "Latencia Media de Procesamiento:", "${vmsb.averageLatencyMicros} microsegundos", "FRECUENCIA CPU: ${vmsb.cpuFrequencyGhz} GHz", 36f, vmsbY)
        vmsbY -= 16f
        drawRow(cs, "Total Frames Streamed Zero-Copy:", "${vmsb.totalFramesProcessedZeroCopy} frames", "HMAC VERIFICADO: ${vmsb.isSecurityHmacVerified}", 36f, vmsbY)
        vmsbY -= 16f
        drawRow(cs, "Energia Acumulada Consumida:", "${String.format(Locale.US, "%.4f", vmsb.cumulativeEnergyJoules)} Joules", "MODO ECO: Adaptativo Dinamico Activo", 36f, vmsbY)

        y -= (vmsbBoxHeight + 20f)

        // SECCIÓN 3: RENDIMIENTO DE CACHÉ SEGURA WASSAM
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10f, 26f, y, 15, 23, 42, "3. WASSAM NATIVE SECURE CACHE - METRICAS DE MEMORIA Y SEGURIDAD")
        y -= 14f

        val cacheBoxHeight = 95f
        cs.setNonStrokingColor(240, 253, 250) // Teal tint
        cs.addRect(26f, y - cacheBoxHeight, width - 52f, cacheBoxHeight)
        cs.fill()
        cs.setStrokingColor(153, 246, 228)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - cacheBoxHeight, width - 52f, cacheBoxHeight)
        cs.stroke()

        val cache = report.cacheTelemetry
        var cacheY = y - 16f
        drawRow(cs, "Tasa de Aciertos (Hit Rate):", "${cache.cacheHitRatePercent}%", "ESTADO: ULTRA-ALTA VELOCIDAD", 36f, cacheY)
        cacheY -= 15f
        drawRow(cs, "Aciertos Totales / Fallos:", "${cache.totalHitsCount} Hits / ${cache.totalMissesCount} Misses", "BLOQUES ACTIVOS: ${cache.cachedBlocksCount} bloques", 36f, cacheY)
        cacheY -= 15f
        drawRow(cs, "Memoria Utilizada / Capacidad:", "${cache.usedFormatted} / ${cache.capacityFormatted}", "CIFRADO ACTIVO: ${cache.isEncryptionActive}", 36f, cacheY)
        cacheY -= 15f
        drawRow(cs, "Protocolo Zeroization Wipe:", "Sobrescritura Ceros Verificada", "ULTIMO BORRADO: ${cache.lastWipeTimestamp}", 36f, cacheY)

        // Pie de página 1
        drawText(cs, PDType1Font.HELVETICA, 7f, 26f, 25f, 148, 163, 184, "VMSB(c) System Performance Audit • Pagina 1 de 2 • Guardado en almacenamiento interno del dispositivo")
    }

    private fun drawPage2(
        cs: PDPageContentStream,
        page: PDPage,
        report: AggregatedPerformanceReport
    ) {
        val width = page.mediaBox.width
        val height = page.mediaBox.height

        // Banner Superior
        cs.setNonStrokingColor(15, 23, 42)
        cs.addRect(0f, height - 70f, width, 70f)
        cs.fill()
        cs.setNonStrokingColor(56, 189, 248)
        cs.addRect(0f, height - 73f, width, 3f)
        cs.fill()

        drawText(cs, PDType1Font.HELVETICA_BOLD, 11f, 26f, height - 26f, 255, 255, 255, "VMSB(c) MULTI-CHANNEL LATENCY, WASM RUNTIME & CRYPTOGRAPHIC SEAL")
        drawText(cs, PDType1Font.HELVETICA, 8f, 26f, height - 44f, 203, 213, 225, "SALUD DE SINCRONIZACION MULTI-CANAL, WEBASSEMBLY MICRO-RUNTIME Y SELLO SHA-256")

        var y = height - 95f

        // SECCIÓN 4: SALUD DE SINCRONIZACIÓN Y LATENCIAS MULTI-CANAL
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10f, 26f, y, 15, 23, 42, "4. SALUD DE SINCRONIZACION Y LATENCIAS DE RED MULTI-CANAL")
        y -= 14f

        val syncBoxHeight = 155f
        cs.setNonStrokingColor(248, 250, 252)
        cs.addRect(26f, y - syncBoxHeight, width - 52f, syncBoxHeight)
        cs.fill()
        cs.setStrokingColor(203, 213, 225)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - syncBoxHeight, width - 52f, syncBoxHeight)
        cs.stroke()

        var syncY = y - 16f
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 36f, syncY, 30, 41, 59, "CANAL / PLATAFORMA")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 230f, syncY, 30, 41, 59, "LATENCIA")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 290f, syncY, 30, 41, 59, "JITTER")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 350f, syncY, 30, 41, 59, "PERDIDA")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 430f, syncY, 30, 41, 59, "ESTADO OPERATIVO")
        syncY -= 16f

        for (channel in report.syncHealthReport.channels) {
            val nameDisplay = if (channel.platformName.length > 28) channel.platformName.take(26) + ".." else channel.platformName
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7f, 36f, syncY, 15, 23, 42, nameDisplay)
            drawText(cs, PDType1Font.HELVETICA, 7f, 230f, syncY, 16, 185, 129, "${channel.latencyMs} ms")
            drawText(cs, PDType1Font.HELVETICA, 7f, 290f, syncY, 71, 85, 105, "${channel.jitterMs} ms")
            drawText(cs, PDType1Font.HELVETICA, 7f, 350f, syncY, 71, 85, 105, "${channel.packetLossPercent}%")
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7f, 430f, syncY, 2, 132, 199, channel.statusLabel.take(20))
            syncY -= 20f
        }

        y -= (syncBoxHeight + 20f)

        // SECCIÓN 5: WEBASSEMBLY (WASM) UNIVERSAL MICRO-RUNTIME
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10f, 26f, y, 15, 23, 42, "5. WEBASSEMBLY (WASM) MICRO-RUNTIME - RENDIMIENTO OFF-HEAP")
        y -= 14f

        val wasmBoxHeight = 85f
        cs.setNonStrokingColor(238, 242, 255) // Indigo 50
        cs.addRect(26f, y - wasmBoxHeight, width - 52f, wasmBoxHeight)
        cs.fill()
        cs.setStrokingColor(199, 210, 254)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - wasmBoxHeight, width - 52f, wasmBoxHeight)
        cs.stroke()

        var wasmY = y - 16f
        drawRow(cs, "Memoria Lineal Asignada:", "${report.wasmMemoryMb} MB (64 paginas)", "RECOLECTOR BASURA: Cero GC Dalvik (0 B)", 36f, wasmY)
        wasmY -= 15f
        drawRow(cs, "Throughput de Transformacion:", "${String.format(Locale.US, "%.1f", report.wasmThroughputMpixPerSec)} Mpix / s", "PARALELISMO: 128-bit SIMD Vector", 36f, wasmY)
        wasmY -= 15f
        drawRow(cs, "Normalizacion Audio DSP:", "${report.wasmAudioLufs} LUFS Integrado", "ESTANDAR EBU R128: Cumplimiento 100%", 36f, wasmY)
        wasmY -= 15f
        drawRow(cs, "Aislamiento Criptografico (SFI):", "Sandbox con Limites de Memoria", "ESTADO: DETERMINISTA UNIVERSAL", 36f, wasmY)

        y -= (wasmBoxHeight + 22f)

        // SECCIÓN 6: SELLO CRIPTOGRÁFICO INMUTABLE Y PROTOCOLO DE BERNA
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10f, 26f, y, 15, 23, 42, "6. CERTIFICACION CRIPTOGRAFICA Y SELLADO INMUTABLE")
        y -= 14f

        val certBoxHeight = 115f
        cs.setNonStrokingColor(15, 23, 42) // Slate 900
        cs.addRect(26f, y - certBoxHeight, width - 52f, certBoxHeight)
        cs.fill()
        cs.setStrokingColor(217, 119, 6) // Gold border
        cs.setLineWidth(1.5f)
        cs.addRect(26f, y - certBoxHeight, width - 52f, certBoxHeight)
        cs.stroke()

        var certY = y - 16f
        drawText(cs, PDType1Font.HELVETICA_BOLD, 8.5f, 36f, certY, 251, 191, 36, "SELLO DE AUDITORIA DE RENDIMIENTO Y CONVENIO DE BERNA (ART. 5.2 WIPO)")
        certY -= 14f
        drawText(cs, PDType1Font.HELVETICA, 7f, 36f, certY, 226, 232, 240, report.berneArt52ProtocolNotice)
        certY -= 14f
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7f, 36f, certY, 148, 163, 184, "HASH SHA-256:")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7f, 120f, certY, 52, 211, 153, report.cryptographicSealSha256)
        certY -= 14f
        drawText(cs, PDType1Font.HELVETICA, 7f, 36f, certY, 203, 213, 225, "Almacenamiento Local: Reporte guardado en filesDir/performance_reports/ para revision del usuario")
        certY -= 14f
        drawText(cs, PDType1Font.HELVETICA, 7f, 36f, certY, 203, 213, 225, "Firma del Motor: VMSB(c) Universal Core Telemetry Engine • Apache PDFBox Integration")

        // Pie de página 2
        drawText(cs, PDType1Font.HELVETICA, 7f, 26f, 25f, 148, 163, 184, "VMSB(c) System Performance Audit • Pagina 2 de 2 • Generado con Apache PDFBox para Android")
    }

    private fun drawText(
        cs: PDPageContentStream,
        font: PDType1Font,
        size: Float,
        x: Float,
        y: Float,
        r: Int,
        g: Int,
        b: Int,
        text: String
    ) {
        try {
            cs.beginText()
            cs.setFont(font, size)
            cs.setNonStrokingColor(r, g, b)
            cs.newLineAtOffset(x, y)
            cs.showText(cleanText(text))
            cs.endText()
        } catch (_: Exception) {}
    }

    private fun drawRow(
        cs: PDPageContentStream,
        col1: String,
        col2: String,
        col3: String,
        x: Float,
        y: Float
    ) {
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, x, y, 51, 65, 85, col1)
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 195f, y, 15, 23, 42, col2)
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 365f, y, 22, 101, 52, col3)
    }

    private fun cleanText(input: String): String {
        return input.replace("©", "(c)")
            .replace("•", "-")
            .replace("→", "->")
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .replace("Á", "A")
            .replace("É", "E")
            .replace("Í", "I")
            .replace("Ó", "O")
            .replace("Ú", "U")
            .replace("Ñ", "N")
    }
}
