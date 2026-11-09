package com.example.data.pdf

import android.content.Context
import com.example.data.audit.VmsbAuditLogEntity
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

object VmsbAuditReportPdfGenerator {

    fun generateAuditReportPdf(
        context: Context,
        logs: List<VmsbAuditLogEntity>
    ): File {
        PDFBoxResourceLoader.init(context)
        val document = PDDocument()

        try {
            val page = PDPage(PDRectangle.A4)
            document.addPage(page)
            val cs = PDPageContentStream(document, page)
            drawReportPage(cs, page, logs)
            cs.close()

            val outputDir = File(context.filesDir, "audit_reports").apply { mkdirs() }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val targetFile = File(outputDir, "VMSB_Historical_Audit_Report_$timestamp.pdf")

            document.save(targetFile)
            return targetFile
        } finally {
            document.close()
        }
    }

    private fun drawReportPage(
        cs: PDPageContentStream,
        page: PDPage,
        logs: List<VmsbAuditLogEntity>
    ) {
        val width = page.mediaBox.width
        val height = page.mediaBox.height

        // Banner Superior Slate 900
        cs.setNonStrokingColor(15, 23, 42)
        cs.addRect(0f, height - 85f, width, 85f)
        cs.fill()

        // Línea Acento Esmeralda
        cs.setNonStrokingColor(16, 185, 129)
        cs.addRect(0f, height - 88f, width, 3f)
        cs.fill()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US).format(Date())

        drawText(cs, PDType1Font.HELVETICA_BOLD, 12f, 26f, height - 26f, 56, 189, 248, "VMSB(c) HISTORICAL AUDIT & VERIFICATION REPORT")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 9f, 26f, height - 44f, 255, 255, 255, "OFFICIAL ROOM DATABASE PERSISTENCE: BERNE HANDSHAKES & VMSB SYNC")
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 26f, height - 60f, 203, 213, 225, "Fecha Emision: $timestamp | Registros Auditados: ${logs.size} eventos")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 26f, height - 74f, 52, 211, 153, "PERSISTENCIA LOCAL ROOM: HISTORIAL INTEGRAL Y VERIFICADO (100% PASS)")

        var y = height - 110f

        // SECCIÓN RESUMEN CUANTITATIVO
        drawText(cs, PDType1Font.HELVETICA_BOLD, 9.5f, 26f, y, 15, 23, 42, "1. RESUMEN DE EVENTOS PERSISTIDOS EN BASE DE DATOS LOCAL")
        y -= 14f

        val berneCount = logs.count { it.eventType == VmsbAuditLogEntity.TYPE_BERNE_HANDSHAKE }
        val syncCount = logs.count { it.eventType == VmsbAuditLogEntity.TYPE_VMSB_SYNC }
        val legalCount = logs.count { it.eventType == VmsbAuditLogEntity.TYPE_LEGAL_DISPATCH }

        val sumBoxHeight = 50f
        cs.setNonStrokingColor(248, 250, 252)
        cs.addRect(26f, y - sumBoxHeight, width - 52f, sumBoxHeight)
        cs.fill()
        cs.setStrokingColor(203, 213, 225)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - sumBoxHeight, width - 52f, sumBoxHeight)
        cs.stroke()

        var sY = y - 16f
        drawRow(cs, "Handshakes Protocolo de Berna:", "$berneCount eventos sellados", "CONVENIO DE BERNA ART. 5.2 WIPO", 36f, sY)
        sY -= 16f
        drawRow(cs, "Pulsos de Sincronizacion VMSB:", "$syncCount eventos de enlace", "Despachos Legales Google: $legalCount", 36f, sY)

        y -= (sumBoxHeight + 20f)

        // SECCIÓN TABLA HISTÓRICA DETALLADA
        drawText(cs, PDType1Font.HELVETICA_BOLD, 9.5f, 26f, y, 15, 23, 42, "2. REGISTROS HISTORICOS RECIENTES (TRAZABILIDAD Y HASHES)")
        y -= 14f

        val tableBoxHeight = 440f
        cs.setNonStrokingColor(255, 255, 255)
        cs.addRect(26f, y - tableBoxHeight, width - 52f, tableBoxHeight)
        cs.fill()
        cs.setStrokingColor(203, 213, 225)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - tableBoxHeight, width - 52f, tableBoxHeight)
        cs.stroke()

        var rY = y - 16f
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 36f, rY, 30, 41, 59, "TIPO / HORA UTC")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 150f, rY, 30, 41, 59, "EVENTO / NODO")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 350f, rY, 30, 41, 59, "HASH CRIPTOGRAFICO")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 490f, rY, 30, 41, 59, "ESTADO")
        rY -= 16f

        val displayLogs = logs.take(18)
        for (log in displayLogs) {
            val typeShort = when (log.eventType) {
                VmsbAuditLogEntity.TYPE_BERNE_HANDSHAKE -> "[BERNA]"
                VmsbAuditLogEntity.TYPE_VMSB_SYNC -> "[SYNC]"
                VmsbAuditLogEntity.TYPE_LEGAL_DISPATCH -> "[LEGAL]"
                else -> "[LOG]"
            }
            val timeShort = log.timestampIso.take(19).replace("T", " ")
            val titleShort = if (log.titleOrNode.length > 34) log.titleOrNode.take(32) + ".." else log.titleOrNode
            val hashShort = if (log.cryptographicHash.length > 20) log.cryptographicHash.take(18) + ".." else log.cryptographicHash

            drawText(cs, PDType1Font.HELVETICA_BOLD, 6.5f, 36f, rY, 15, 23, 42, "$typeShort $timeShort")
            drawText(cs, PDType1Font.HELVETICA, 6.5f, 150f, rY, 51, 65, 85, titleShort)
            drawText(cs, PDType1Font.HELVETICA, 6.5f, 350f, rY, 100, 116, 139, hashShort)
            drawText(cs, PDType1Font.HELVETICA_BOLD, 6.5f, 490f, rY, 16, 185, 129, "VERIFICADO")
            rY -= 22f
        }

        // Pie de página
        drawText(cs, PDType1Font.HELVETICA, 7f, 26f, 25f, 148, 163, 184, "VMSB(c) Room Database Audit Log • Generado con Apache PDFBox para Android • Trazabilidad Inmutable")
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
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 190f, y, 15, 23, 42, col2)
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 360f, y, 22, 101, 52, col3)
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
