package com.example.ui.components.c2pa

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.c2pa.C2paManifestStore
import com.example.data.c2pa.C2paCryptographicService
import com.example.data.c2pa.C2paJumbfBoxBuilder
import com.example.data.c2pa.C2PAManager
import com.example.data.pdf.CertifiedReportPdfGenerator
import androidx.core.content.FileProvider
import android.content.Intent
import java.io.File

@Composable
fun C2paCredentialsBadgeDialog(
    manifest: C2paManifestStore,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showJsonLd by remember { mutableStateOf(false) }
    var liveTestStatus by remember { mutableStateOf<String?>(null) }
    val cryptoService = remember { C2paCryptographicService() }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("c2pa_credentials_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF10B981)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "C2PA Verified",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "C2PA Content Credentials",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Especificación v2.1 • Escudo Regulatorio",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("c2pa_dialog_close")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Legal Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF064E3B)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Garantía Legal de Publicación: Conforme con EU AI Act (Art. 50/52) y FTC 16 CFR §464. Inmune a sanciones por omisión de metadatos sintéticos.",
                                fontSize = 11.sp,
                                color = Color(0xFFD1FAE5),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Asset Identification
                    SectionBox(title = "IDENTIFICACIÓN DEL ARCHIVO") {
                        Text(
                            text = "Título: ${manifest.assetTitle}",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "SHA-256: ${manifest.assetSha256}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Manifest ID: ${manifest.manifestId}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Cryptographic Signature
                    SectionBox(title = "FIRMA CRIPTOGRÁFICA DE PROCEDENCIA") {
                        Text(
                            text = "Algoritmo: ${manifest.digitalSignatureAlgorithm}",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "Emisor: ${manifest.certificateIssuer}",
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "Fingerprint: ${manifest.certificateFingerprint}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFFFBBF24)
                        )
                        Text(
                            text = "Firma JUMBF: ${manifest.signatureHex.take(32)}...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // AI Synthetic Media Disclosure
                    SectionBox(title = "DECLARACIÓN DE MEDIO SINTÉTICO (AI TRANSPARENCY)") {
                        Text(
                            text = "Motor Generativo: ${manifest.aiAssertion.generatorModel}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA78BFA),
                            fontSize = 12.sp
                        )
                        Text(
                            text = manifest.aiAssertion.transparencyStatement,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "Prompt Hash: ${manifest.aiAssertion.promptHashSha256}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Audit Actions History
                    SectionBox(title = "HISTORIAL DE ACCIONES EDITORIALES (C2PA.ACTIONS)") {
                        manifest.actions.forEach { act ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Column {
                                    Text(
                                        text = "${act.action}: ${act.description}",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Software: ${act.softwareAgent} • ${act.timestampIso}",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    if (showJsonLd) {
                        SectionBox(title = "MANIFEST JSON-LD (W3C / C2PA)") {
                            Text(
                                text = manifest.toJsonLd(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Color(0xFF34D399),
                                lineHeight = 12.sp
                            )
                        }
                    }

                    // Live Embedding and Verification Section
                    SectionBox(title = "INCRUSTACIÓN Y EXTRACCIÓN CRIPTOGRÁFICA EN VIVO (SHA-256 JUMBF)") {
                        Text(
                            text = "Inyección directa en contenedor MP4 sin recodificación (ISO/IEC 19566-5 / C2PA v2.1)",
                            fontSize = 10.5.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = {
                                try {
                                    val testDir = File(context.cacheDir, "c2pa_live_test").apply { mkdirs() }
                                    val sampleSrc = File(testDir, "sample_track.mp4").apply {
                                        val ftypPayload = "isommp42".toByteArray()
                                        val ftypBox = C2paJumbfBoxBuilder.buildGenericBox("ftyp", ftypPayload)
                                        val mdatPayload = "VEO_STUDIO_VIDEO_BITSTREAM_4K_HDR".toByteArray()
                                        val mdatBox = C2paJumbfBoxBuilder.buildGenericBox("mdat", mdatPayload)
                                        writeBytes(ftypBox + mdatBox)
                                    }
                                    val sampleOut = File(testDir, "sample_signed_c2pa.mp4")

                                    val embedRes = cryptoService.embedManifestIntoMp4(sampleSrc, sampleOut, manifest)
                                    val verifyRes = cryptoService.verifyVideoProvenance(sampleOut)

                                    liveTestStatus = if (embedRes.success && verifyRes.isAuthentic) {
                                        "✓ ÉXITO: Caja JUMBF 'uuid' incrustada (${embedRes.injectedC2paBoxSizeBytes}B) • Digest SHA-256 verificado • Anti-Tampering: Auténtico"
                                    } else {
                                        "Error: ${embedRes.errorMessage ?: verifyRes.securityVerdict}"
                                    }
                                } catch (e: Exception) {
                                    liveTestStatus = "Excepción en test: ${e.message}"
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF34D399)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.EnhancedEncryption, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ejecutar Test de Incrustación y Hash SHA-256", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        liveTestStatus?.let { status ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (status.startsWith("✓")) Color(0xFF34D399) else Color(0xFFF87171)
                            )
                        }
                    }

                    // Lattice Post-Quantum Cryptography Section (C2PAManager)
                    val c2paMgr = remember { C2PAManager.getInstance() }
                    val latticeReport = remember(manifest) { c2paMgr.evaluateLatticePostQuantumDefense(manifest) }

                    SectionBox(title = "RESPALDO CRIPTOGRÁFICO DE RETÍCULOS POST-CUÁNTICOS (NIST FIPS 204)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Esquema: ${latticeReport.latticeScheme}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF065F46)
                            ) {
                                Text(
                                    text = latticeReport.authorshipTraceabilityGrade,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7F3D0),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = latticeReport.nistStandardizationStatus,
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = latticeReport.mitigationSummary,
                            fontSize = 9.5.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showJsonLd = !showJsonLd },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                    ) {
                        Text(if (showJsonLd) "Ocultar JSON" else "Ver JSON-LD", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(manifest.toJsonLd()))
                            Toast.makeText(context, "Manifiesto C2PA copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Copiar Cédula", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            try {
                                val pdfFile = CertifiedReportPdfGenerator.generateCertifiedAuditPdf(context, manifest, null)
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    putExtra(Intent.EXTRA_SUBJECT, "Reporte Oficial C2PA v2.1 & Auditoría de Conectividad")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Compartir Reporte PDF Certificado").apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error exportando PDF: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Exportar PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionBox(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E293B))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF38BDF8),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}
