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

/**
 * Diálogo de Verificación de Cumplimientos Normativos, Certificaciones de Seguridad,
 * Estándares Técnicos y Calidad Globales con enlace en tiempo real a Google y sellado PDF.
 */
@Composable
fun GlobalComplianceCertificationDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val complianceReport by viewModel.globalComplianceAuditReport.collectAsState()
    val isVerifying by viewModel.isVerifyingGlobalCompliance.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val categories = listOf("Todos", "Normativa Legal", "Seguridad & Cripto", "Técnicos", "Calidad & Ergonomía")

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("global_compliance_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C18)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFF38BDF8), Color(0xFF10B981), Color(0xFFF59E0B))
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
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
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF059669))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cumplimientos Normativos & Calidad Global",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Certificado y Sellado en Tiempo Real con Google Cloud",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // CONTENIDO CON SCROLL
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // BANNER DE CONECTIVIDAD GOOGLE EN TIEMPO REAL
                    complianceReport?.let { report ->
                        val google = report.googleConnectivityProof
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0B192C),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(if (google.isReachable) Color(0xFF10B981) else Color(0xFFEF4444))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "CONEXIÓN A GOOGLE EN TIEMPO REAL",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF34D399)
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.verifyGlobalComplianceAndPingGoogle() },
                                        enabled = !isVerifying,
                                        modifier = Modifier.height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isVerifying) "Verificando..." else "Re-Verificar",
                                            fontSize = 9.sp,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Gateway:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                                        Text(google.endpointName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("URL: ${google.endpointUrl}", fontSize = 7.5.sp, color = Color(0xFF64748B), maxLines = 1)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Latencia:", fontSize = 8.sp, color = Color(0xFF94A3B8))
                                        Text("${google.latencyMs} ms", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                                        Text("HTTP ${google.httpStatusCode} OK (TLS 1.3)", fontSize = 7.5.sp, color = Color(0xFF34D399))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "TLS Cipher: ${google.tlsCipherSuite} | Server: ${google.serverHeader}",
                                    fontSize = 7.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // TARJETA DE VEREDICTO Y FOLIO
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Folio Oficial: ${report.folioId}",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFFDE68A)
                                    )
                                    Text(
                                        text = report.overallVerdict,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF047857), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${report.complianceScorePercent}% OK",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // SELECTOR DE PESTAÑAS DE CATEGORÍA
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF38BDF8),
                            edgePadding = 0.dp,
                            divider = {}
                        ) {
                            categories.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            fontSize = 9.sp,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                        )
                                    }
                                )
                            }
                        }

                        // LISTA DE ELEMENTOS DE CUMPLIMIENTO FILTRADOS
                        val filteredItems = when (selectedTab) {
                            1 -> report.complianceItems.filter { it.category.contains("Jurídico") || it.category.contains("Legal") }
                            2 -> report.complianceItems.filter { it.category.contains("Seguridad") }
                            3 -> report.complianceItems.filter { it.category.contains("Técnico") }
                            4 -> report.complianceItems.filter { it.category.contains("Calidad") }
                            else -> report.complianceItems
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            filteredItems.forEach { item ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0D1527),
                                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.title,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF065F46), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = item.status,
                                                    fontSize = 7.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF6EE7B7)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Código: ${item.standardCode} | Ente: ${item.issuingBody}",
                                            fontSize = 8.sp,
                                            color = Color(0xFF38BDF8)
                                        )
                                        Text(
                                            text = item.scopeDescription,
                                            fontSize = 7.5.sp,
                                            color = Color(0xFFCBD5E1)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Métrica Verificada: ${item.measuredMetric}",
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFBBF24)
                                        )
                                    }
                                }
                            }
                        }

                        // CAJA DE SELLO CRIPTOGRÁFICO EN TIEMPO REAL
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF030712),
                            border = BorderStroke(1.dp, Color(0xFF475569))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SELLO CRIPTOGRÁFICO EN TIEMPO REAL",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFDE68A)
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("VMSB-Seal", report.sha256CryptographicSeal)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Sello SHA-256 copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                    }
                                }

                                Text(
                                    text = "SHA-256: ${report.sha256CryptographicSeal}",
                                    fontSize = 7.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8),
                                    lineHeight = 10.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Post-Quantum: ${report.nistPostQuantumFips204Signature}",
                                    fontSize = 7.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF64748B),
                                    maxLines = 1
                                )
                            }
                        }
                    } ?: run {
                        // Estado de carga o sin reporte previo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF38BDF8), modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Verificando normativas y realizando ping a Google...", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BOTONES DE ACCIÓN PARA PDF (VER, BAJAR, IMPRIMIR)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // BOTÓN VER PDF
                    Button(
                        onClick = {
                            val pdf = viewModel.generateGlobalCompliancePdf(context)
                            if (pdf != null) {
                                viewModel.viewPdfFile(context, pdf)
                            } else {
                                Toast.makeText(context, "Error generando PDF de cumplimiento", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("view_compliance_pdf_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Ver PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // BOTÓN BAJAR PDF
                    Button(
                        onClick = {
                            val pdf = viewModel.generateGlobalCompliancePdf(context)
                            if (pdf != null) {
                                viewModel.downloadPdfFile(context, pdf)
                            } else {
                                Toast.makeText(context, "Error descargando PDF", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("download_compliance_pdf_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Bajar PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // BOTÓN IMPRIMIR
                    Button(
                        onClick = {
                            val pdf = viewModel.generateGlobalCompliancePdf(context)
                            if (pdf != null) {
                                viewModel.printPdfFile(context, pdf)
                            } else {
                                Toast.makeText(context, "Error preparando impresión", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("print_compliance_pdf_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309))
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Imprimir", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
