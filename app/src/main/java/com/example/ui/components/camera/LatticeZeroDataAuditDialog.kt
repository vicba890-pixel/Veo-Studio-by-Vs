package com.example.ui.components.camera

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.camera.LatticeZeroDataProtector

@Composable
fun LatticeZeroDataAuditDialog(
    protector: LatticeZeroDataProtector,
    onDismiss: () -> Unit
) {
    val status = protector.getSecurityStatus()
    val auditEntries = protector.getAuditLogs()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("lattice_audit_dialog_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFF10B981), Color(0xFF6366F1))
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Lattice Shield",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "LATTICE CERO-DATA • PROTECCIÓN INTERNACIONAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF10B981),
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = "Auditoría de Enclave y Sensores Nativos",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Regulatory Badges Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "GDPR Art. 25 & 32" to Color(0xFF38BDF8),
                        "CCPA Zero-Sale" to Color(0xFF10B981),
                        "Post-Quantum Lattice" to Color(0xFFA855F7),
                        "ISO/IEC 27001" to Color(0xFFF59E0B)
                    ).forEach { (label, color) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(color.copy(alpha = 0.12f))
                                .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF131B2E))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Algoritmo de Retículo:", fontSize = 9.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(status.latticeAlgorithm, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enclave de Memoria:", fontSize = 9.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(status.memoryEnclaveType, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tratamiento Zero-Data:", fontSize = 9.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("0 Bytes a la nube externa • 100% On-Device", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Registro Inviolable de Accesos a Sensores (Hardware LED Audit)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Audit Logs List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(auditEntries) { entry ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF111827))
                                .border(0.8.dp, Color(0xFF1F2937), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    val ledColor = when (entry.privacyIndicator) {
                                        "GREEN_CAM_LED" -> Color(0xFF10B981)
                                        "ORANGE_MIC_LED" -> Color(0xFFF97316)
                                        else -> Color(0xFF38BDF8)
                                    }
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ledColor))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(entry.event, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                        Text("${entry.sensor} • ${entry.regulationCompliance}", fontSize = 8.sp, color = Color.Gray)
                                        Text(entry.latticeProofHash, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFA855F7))
                                    }
                                }
                                Text(
                                    text = entry.timestampFormatted.takeLast(12),
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Cerrar Auditoría de Privacidad", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}
