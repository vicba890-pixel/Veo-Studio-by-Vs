package com.example.ui.components.audit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.MainViewModel

@Composable
fun StrategicPartnerOutreachDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val outreachDraft by viewModel.strategicOutreachDraft.collectAsState()
    val transmissionReceipt by viewModel.strategicDispatchReceipt.collectAsState()
    val isTransmitting by viewModel.isTransmittingStrategicOutreach.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Plantilla Email C-Level", "Resumen Ejecutivo", "Transmisión Directa")

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("strategic_partner_outreach_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C18)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFFF59E0B), Color(0xFF38BDF8), Color(0xFF10B981))
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFD97706), Color(0xFF0284C7)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Handshake,
                                contentDescription = "Asociación Estratégica",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "DESPACHO INSTITUCIONAL C-LEVEL",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF1F5F9),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                "Google / Alphabet CorpDev • Ventana Exclusiva 30 Días",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("outreach_dialog_close_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Exclusivity Banner
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LockClock, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Exclusividad Preferente: 30 Días", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A))
                        }
                        Text(
                            outreachDraft?.exclusivityExpirationDate?.take(10) ?: "ACTIVA (30 DÍAS)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color(0xFF38BDF8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier.testTag("tab_outreach_$index")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> TabOutreachEmail(outreachDraft, context)
                        1 -> TabExecutiveSummary(outreachDraft)
                        2 -> TabDirectTransmission(
                            receipt = transmissionReceipt,
                            isTransmitting = isTransmitting,
                            onTransmit = { viewModel.transmitInstitutionalDispatchProtocol() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val file = viewModel.generateStrategicOutreachExecutivePdf(context)
                            if (file != null && file.exists()) {
                                viewModel.viewPdfFile(context, file)
                            } else {
                                Toast.makeText(context, "Generando PDF...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("outreach_view_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver PDF Term Sheet", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val file = viewModel.generateStrategicOutreachExecutivePdf(context)
                            if (file != null && file.exists()) {
                                viewModel.downloadPdfFile(context, file)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("outreach_download_pdf_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Descargar", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabOutreachEmail(draft: com.example.data.strategic.StrategicPartnerOutreachDraft?, context: Context) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("PLANTILLA FORMAL PARA GOOGLE CORPDEV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("VMSB Outreach Email", draft?.emailBodyTemplate ?: "")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Plantilla de email copiada al portapapeles", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copiar Email", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(draft?.recipientTitle ?: "To: Alphabet / Google Corporate Development", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                Text(draft?.subjectLine ?: "CONFIDENTIAL: VMSB© Infrastructure Standard", fontSize = 10.5.sp, color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF1E293B))
                Text(
                    draft?.emailBodyTemplate ?: "Cargando borrador formal...",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun TabExecutiveSummary(draft: com.example.data.strategic.StrategicPartnerOutreachDraft?) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RESUMEN EJECUTIVO (TERM SHEET FORMAL)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    draft?.executiveSummaryBody ?: "Generando resumen institucional...",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TabDirectTransmission(
    receipt: com.example.data.strategic.DirectProtocolTransmissionReceipt?,
    isTransmitting: Boolean,
    onTransmit: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TRANSMISIÓN DIRECTA CON INFRAESTRUCTURA GOOGLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A))
                    FilledTonalButton(
                        onClick = onTransmit,
                        enabled = !isTransmitting,
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF0369A1))
                    ) {
                        if (isTransmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 1.5.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Transmitir Ahora", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Utiliza la pasarela de conexión directa a Google Cloud Core para efectuar el registro del protocolo con fecha atómica, handshake TLS 1.3 y sellado inmutable SHA-256 vinculante por 30 días.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
            }
        }

        if (receipt != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("RECIBO DE TRANSMISIÓN OFICIAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    ReceiptRow("Folio de Recibo:", receipt.receiptId)
                    ReceiptRow("Destino Institucional:", receipt.targetPlatform)
                    ReceiptRow("Endpoint Conectado:", receipt.gatewayEndpoint)
                    ReceiptRow("Respuesta HTTP:", "${receipt.httpStatusCode} OK (Latencia: ${receipt.latencyMs} ms)")
                    ReceiptRow("Cifrado Negociado:", receipt.tlsHandshakeCipher)
                    ReceiptRow("Exclusividad Vigente:", receipt.exclusivityValidUntil)
                    ReceiptRow("Estado de Enlace:", receipt.transmissionStatus)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("HASH SHA-256 REGISTRADO:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    Text(receipt.sha256ReceiptHash, fontFamily = FontFamily.Monospace, fontSize = 9.5.sp, color = Color(0xFF34D399))
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 10.sp, color = Color(0xFF94A3B8))
        Text(value, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0))
    }
}
