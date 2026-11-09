package com.example.ui.components.audit

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.audit.ProofOfHumanIntentCertificate
import com.example.data.pdf.CertificateGenerator
import androidx.core.content.FileProvider
import android.content.Intent

/**
 * Diálogo Interactivo de Prueba de Valor Auténtico e Intención Humana (PoHI / PoAV).
 * Certificación criptográfica verificable de aporte cognitivo humano y elegibilidad autoral.
 */
@Composable
fun ProofOfHumanIntentDialog(
    certificate: ProofOfHumanIntentCertificate,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showRawJson by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("pohi_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF38BDF8))
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // HEADER
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
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFB45309), Color(0xFF047857))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Human Intent",
                                tint = Color(0xFFFDE68A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Prueba de Intención Humana (PoHI)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Proof-of-Authentic-Value • Escudo Copyright",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFBBF24),
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("pohi_dialog_close")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SCROLLABLE BODY
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // MAIN BADGE: AUTHENTIC VALUE & HUMAN INTENT
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF131D2E),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "VALOR AUTÉNTICO CERTIFICADO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF34D399),
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "${certificate.authenticValueQuotient}% AVQ",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF064E3B),
                                    border = BorderStroke(1.dp, Color(0xFF059669))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "COEFICIENTE INTENCIÓN",
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFA7F3D0)
                                        )
                                        Text(
                                            text = "${certificate.humanIntentCoefficient}% HIC",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF34D399)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "• Proyecto: ${certificate.projectTitle}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0)
                            )
                            Text(
                                text = "• Trabajo Directivo Humano Estimado: ${certificate.humanLaborHoursEstimated} horas directas (${certificate.deliberateActionCount} interacciones decisivas)",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // SECTION: VECTORES CREATIVOS DIRECTIVOS HUMANOS
                    PohiSectionBox(title = "VECTORES DE INTENCIÓN CREATIVA REGISTRADOS") {
                        certificate.humanCreativeVectors.forEach { vector ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "✓",
                                    color = Color(0xFFFBBF24),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = vector,
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    // SECTION: ELEGIBILIDAD COPYRIGHT Y TRIBUNALES
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF064E3B).copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, Color(0xFF059669))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DICTAMEN LEGAL DE AUTORÍA HUMANA (USCO & BERNA)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF34D399)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = certificate.copyrightOfficeVerdict,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "El nivel de intervención directiva humana excede el umbral 'De Minimis' establecido por la Oficina de Copyright de EE.UU. y el Convenio de Berna.",
                                fontSize = 9.sp,
                                color = Color(0xFFA7F3D0),
                                lineHeight = 12.sp
                            )
                        }
                    }

                    // SECTION: ATESTACIÓN CRIPTOGRÁFICA (ZKP & LATTICE)
                    PohiSectionBox(title = "ATESTACIÓN CRIPTOGRÁFICA DE VALOR INMUTABLE") {
                        Text(
                            text = "Prueba ZK-SNARK (Circom/Arkworks):",
                            fontSize = 8.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = certificate.zkpProofHash,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color(0xFF38BDF8)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Firma de Retículo Post-Cuántica (NIST FIPS 204):",
                            fontSize = 8.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = certificate.latticeQuantumSignature.take(48) + "...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp,
                            color = Color(0xFFA78BFA)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "SHA-256 Digest de la Prueba:",
                            fontSize = 8.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = certificate.sha256ProofDigest,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color(0xFF34D399)
                        )
                    }

                    if (showRawJson) {
                        PohiSectionBox(title = "TOKEN DE PRUEBA JSON VERIFICABLE") {
                            Text(
                                text = certificate.toVerifiableJson(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp,
                                color = Color(0xFFFDE68A),
                                lineHeight = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTTOM BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { showRawJson = !showRawJson },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(if (showRawJson) "Ocultar" else "Ver JSON", fontSize = 10.sp)
                    }

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(certificate.toVerifiableJson()))
                            Toast.makeText(context, "Certificado PoHI copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Copiar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            try {
                                val pdfFile = CertificateGenerator.generateProofOfHumanIntentPdf(
                                    context = context,
                                    project = null,
                                    manifest = null,
                                    auditReport = null,
                                    pohiCertificate = certificate
                                )
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    putExtra(Intent.EXTRA_SUBJECT, "Certificado Oficial Proof of Human Intent (PoHI) v2.1")
                                    putExtra(Intent.EXTRA_TEXT, "Adjunto certificado firmado de Prueba de Intención Humana y Valor Auténtico emitido por CertificateGenerator.")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Compartir Certificado PoHI PDF").apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error exportando PDF: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.weight(1.4f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("PDF Firmado", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun PohiSectionBox(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFF59E0B),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}
