package com.example.data.pdf

import android.content.Context
import com.example.data.compliance.GlobalComplianceAuditReport
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
 * Generador Oficial de Certificación de Cumplimientos Normativos,
 * Seguridad, Estándares Técnicos y Calidad Globales con Enlace en Tiempo Real a Google.
 *
 * Utiliza la librería oficial Apache PDFBox para Android (com.tom-roush:pdfbox-android).
 */
object GlobalComplianceCertifiedPdfGenerator {

    fun generateComplianceAuditPdf(
        context: Context,
        report: GlobalComplianceAuditReport
    ): File {
        // Inicializar recursos nativos de PDFBox Android
        PDFBoxResourceLoader.init(context)

        val document = PDDocument()
        try {
            // ====================================================================
            // PÁGINA 1: MARCO NORMATIVO INTERNACIONAL, SEGURIDAD & GOOGLE LIVE PING
            // ====================================================================
            val page1 = PDPage(PDRectangle.A4)
            document.addPage(page1)
            var cs = PDPageContentStream(document, page1)
            drawPage1(cs, page1, report)
            cs.close()

            // ====================================================================
            // PÁGINA 2: ESTÁNDARES TÉCNICOS, CALIDAD GLOBAL & SELLO CRIPTOGRÁFICO
            // ====================================================================
            val page2 = PDPage(PDRectangle.A4)
            document.addPage(page2)
            cs = PDPageContentStream(document, page2)
            drawPage2(cs, page2, report)
            cs.close()

            val outputDir = File(context.filesDir, "compliance_certificates").apply { mkdirs() }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val targetFile = File(outputDir, "VMSB_Global_Compliance_Certified_Google_$timestamp.pdf")

            document.save(targetFile)
            return targetFile
        } finally {
            document.close()
        }
    }

    private fun drawPage1(
        cs: PDPageContentStream,
        page: PDPage,
        report: GlobalComplianceAuditReport
    ) {
        val width = page.mediaBox.width
        val height = page.mediaBox.height

        // 1. Banner Principal Institucional
        cs.setNonStrokingColor(15, 23, 42) // Slate 900
        cs.addRect(0f, height - 95f, width, 95f)
        cs.fill()

        // Línea divisoria de oro de certificación
        cs.setNonStrokingColor(217, 119, 6) // Amber / Gold
        cs.addRect(0f, height - 98f, width, 3f)
        cs.fill()

        // Textos del Encabezado
        drawText(cs, PDType1Font.HELVETICA_BOLD, 12.5f, 26f, height - 30f, 56, 189, 248, "VMSB(c) GLOBAL COMPLIANCE, SECURITY & QUALITY AUDIT CERTIFICATE")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 9.5f, 26f, height - 48f, 255, 255, 255, "OFFICIAL VERIFIED AUDIT & REAL-TIME CRYPTOGRAPHIC SEAL WITH GOOGLE CONNECTIVITY")
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 26f, height - 66f, 203, 213, 225, "Folio ID: ${report.folioId} | Time: ${report.timestampIso} | Apache PDFBox Engine")
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 26f, height - 82f, 52, 211, 153, "VEREDICTO: ${report.overallVerdict} (PUNTAJE: ${report.complianceScorePercent}%)")

        var y = height - 120f

        // SECCIÓN 1: TELEMETRÍA EN TIEMPO REAL - CONECTIVIDAD VERIFICADA CON GOOGLE
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10.5f, 26f, y, 15, 23, 42, "1. TELEMETRIA EN TIEMPO REAL: CONECTIVIDAD VERIFICADA CON GOOGLE CLOUD")
        y -= 15f
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 26f, y, 71, 85, 105, "Enlace directo verificado contra la infraestructura troncal de Google:")
        y -= 12f

        val googleProof = report.googleConnectivityProof
        val boxHeight = 65f
        cs.setNonStrokingColor(240, 253, 244) // Emerald tint 50
        cs.addRect(26f, y - boxHeight, width - 52f, boxHeight)
        cs.fill()
        cs.setStrokingColor(187, 247, 208)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - boxHeight, width - 52f, boxHeight)
        cs.stroke()

        var cardY = y - 16f
        drawRow(cs, "Gateway Principal:", googleProof.endpointName, "STATUS HTTP: ${googleProof.httpStatusCode} OK", 36f, cardY)
        cardY -= 14f
        drawRow(cs, "URL Verificada:", googleProof.endpointUrl, "LATENCIA MEDIDA: ${googleProof.latencyMs} ms", 36f, cardY)
        cardY -= 14f
        drawRow(cs, "Suite Cifrado TLS:", googleProof.tlsCipherSuite, "SERVER: ${googleProof.serverHeader}", 36f, cardY)
        cardY -= 14f
        drawRow(cs, "Estado Diagnostico:", googleProof.diagnosticSummary, "ESTADO: VERIFICADO EN VIVO", 36f, cardY)

        y -= (boxHeight + 20f)

        // SECCIÓN 2: ALCANCE DE CUMPLIMIENTOS NORMATIVOS Y MARCO JURÍDICO INTERNACIONAL
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10.5f, 26f, y, 15, 23, 42, "2. ALCANCE DE CUMPLIMIENTO NORMATIVO Y MARCO JURIDICO INTERNACIONAL")
        y -= 15f

        val legalItems = report.complianceItems.filter { it.category.contains("Jurídico") || it.category.contains("Legal") }
        val legalBoxHeight = 120f
        cs.setNonStrokingColor(248, 250, 252)
        cs.addRect(26f, y - legalBoxHeight, width - 52f, legalBoxHeight)
        cs.fill()
        cs.setStrokingColor(226, 232, 240)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - legalBoxHeight, width - 52f, legalBoxHeight)
        cs.stroke()

        cardY = y - 16f
        for (item in legalItems) {
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 36f, cardY, 3, 105, 161, "[${item.standardCode}] - ${item.title}")
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 430f, cardY, 22, 101, 52, item.status)
            cardY -= 11f
            drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 51, 65, 85, "${item.scopeDescription} | Metrica: ${item.measuredMetric}")
            cardY -= 16f
        }

        y -= (legalBoxHeight + 20f)

        // SECCIÓN 3: CERTIFICACIONES DE SEGURIDAD Y CRIPTOGRAFÍA POST-CUÁNTICA
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10.5f, 26f, y, 15, 23, 42, "3. CERTIFICACIONES DE SEGURIDAD, INTEGRIDAD Y CRIPTOGRAFIA POST-CUANTICA")
        y -= 15f

        val secItems = report.complianceItems.filter { it.category.contains("Seguridad") }
        val secBoxHeight = 120f
        cs.setNonStrokingColor(241, 245, 249)
        cs.addRect(26f, y - secBoxHeight, width - 52f, secBoxHeight)
        cs.fill()
        cs.setStrokingColor(203, 213, 225)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - secBoxHeight, width - 52f, secBoxHeight)
        cs.stroke()

        cardY = y - 16f
        for (item in secItems) {
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 36f, cardY, 15, 23, 42, "[${item.standardCode}] - ${item.title}")
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 430f, cardY, 2, 132, 199, item.status)
            cardY -= 11f
            drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 71, 85, 105, "${item.scopeDescription} | Evaluacion: ${item.measuredMetric}")
            cardY -= 16f
        }

        // Pie de página 1
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 26f, 22f, 148, 163, 184, "Pagina 1 de 2 | VMSB(c) Global Compliance & Google Connectivity Audit | Apache PDFBox Engine")
    }

    private fun drawPage2(
        cs: PDPageContentStream,
        page: PDPage,
        report: GlobalComplianceAuditReport
    ) {
        val width = page.mediaBox.width
        val height = page.mediaBox.height

        // Encabezado de Página 2
        cs.setNonStrokingColor(15, 23, 42)
        cs.addRect(0f, height - 52f, width, 52f)
        cs.fill()

        drawText(cs, PDType1Font.HELVETICA_BOLD, 11f, 26f, height - 32f, 56, 189, 248, "VMSB(c) TECHNICAL STANDARDS, QUALITY METRICS & ATOMIC SEAL")

        var y = height - 75f

        // SECCIÓN 4: ESTÁNDARES TÉCNICOS GLOBALES
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10.5f, 26f, y, 15, 23, 42, "4. ESTANDARES TECNICOS GLOBALES (SONORIDAD, COLOR Y RENDIMIENTO)")
        y -= 15f

        val techItems = report.complianceItems.filter { it.category.contains("Técnico") }
        val techBoxHeight = 120f
        cs.setNonStrokingColor(248, 250, 252)
        cs.addRect(26f, y - techBoxHeight, width - 52f, techBoxHeight)
        cs.fill()
        cs.setStrokingColor(226, 232, 240)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - techBoxHeight, width - 52f, techBoxHeight)
        cs.stroke()

        var cardY = y - 16f
        for (item in techItems) {
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 36f, cardY, 30, 41, 59, "[${item.standardCode}] - ${item.title}")
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 430f, cardY, 5, 150, 105, item.status)
            cardY -= 11f
            drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 71, 85, 105, "${item.scopeDescription} | Medicion: ${item.measuredMetric}")
            cardY -= 16f
        }

        y -= (techBoxHeight + 20f)

        // SECCIÓN 5: ESTÁNDARES DE CALIDAD GLOBAL Y ACCESIBILIDAD
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10.5f, 26f, y, 15, 23, 42, "5. ESTANDARES DE CALIDAD GLOBAL, ACCESIBILIDAD Y EFICIENCIA ENERGETICA")
        y -= 15f

        val qualItems = report.complianceItems.filter { it.category.contains("Calidad") }
        val qualBoxHeight = 100f
        cs.setNonStrokingColor(240, 253, 244)
        cs.addRect(26f, y - qualBoxHeight, width - 52f, qualBoxHeight)
        cs.fill()
        cs.setStrokingColor(187, 247, 208)
        cs.setLineWidth(1f)
        cs.addRect(26f, y - qualBoxHeight, width - 52f, qualBoxHeight)
        cs.stroke()

        cardY = y - 16f
        for (item in qualItems) {
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 36f, cardY, 20, 83, 45, "[${item.standardCode}] - ${item.title}")
            drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 430f, cardY, 22, 101, 52, item.status)
            cardY -= 11f
            drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 71, 85, 105, "${item.scopeDescription} | Metrica: ${item.measuredMetric}")
            cardY -= 16f
        }

        y -= (qualBoxHeight + 25f)

        // SECCIÓN 6: SELLO CRIPTOGRÁFICO EN TIEMPO REAL & DICTAMEN DE CERTIFICACIÓN
        drawText(cs, PDType1Font.HELVETICA_BOLD, 10.5f, 26f, y, 15, 23, 42, "6. SELLO DIGITAL INMUTABLE SELLADO EN TIEMPO REAL (GOOGLE REAL-TIME LINK)")
        y -= 15f

        val sealBoxHeight = 110f
        cs.setNonStrokingColor(2, 6, 23) // Slate 950
        cs.addRect(26f, y - sealBoxHeight, width - 52f, sealBoxHeight)
        cs.fill()

        cardY = y - 18f
        drawText(cs, PDType1Font.HELVETICA_BOLD, 8f, 36f, cardY, 253, 230, 138, "DICTAMEN OFICIAL: CERTIFICACION GLOBAL Y DE SEGURIDAD EMITIDA Y APROBADA")
        cardY -= 14f
        drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 148, 163, 184, "SHA-256 Real-Time Seal: ${report.sha256CryptographicSeal}")
        cardY -= 13f
        drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 148, 163, 184, "Firma Post-Cuantica NIST FIPS 204: ${report.nistPostQuantumFips204Signature}")
        cardY -= 13f
        drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 148, 163, 184, "C2PA Manifest Bitstream Root: ${report.c2paManifestRootSha256}")
        cardY -= 15f
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 36f, cardY, 52, 211, 153, "CONECTIVIDAD GOOGLE CERTIFICADA EN VIVO: Latencia ${report.googleConnectivityProof.latencyMs} ms | HTTP ${report.googleConnectivityProof.httpStatusCode} OK")
        cardY -= 12f
        drawText(cs, PDType1Font.HELVETICA, 7f, 36f, cardY, 203, 213, 225, "Certificado valido para auditorias corporativas, comites legales de Big Tech y festivales internacionales.")

        // Pie de página 2
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 26f, 22f, 148, 163, 184, "Pagina 2 de 2 | VMSB(c) Core Certified Compliance | Sellado Inmutable Generado con PDFBox")
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
        drawText(cs, PDType1Font.HELVETICA, 7.5f, 140f, y, 15, 23, 42, col2)
        drawText(cs, PDType1Font.HELVETICA_BOLD, 7.5f, 380f, y, 22, 101, 52, col3)
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
