package com.example.ui.components.audit

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.audit.VmsbAuditLogEntity
import com.example.ui.MainViewModel

@Composable
fun VmsbAuditLogHistoryDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val allLogs by viewModel.allAuditLogs.collectAsState()
    var selectedFilterIndex by remember { mutableStateOf(0) }
    val filterTabs = listOf("Todos", "Protocolo Berna", "Sync VMSB", "Despacho Legal", "MCTS Rollout")

    val filteredLogs = remember(allLogs, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> allLogs.filter { it.eventType == VmsbAuditLogEntity.TYPE_BERNE_HANDSHAKE }
            2 -> allLogs.filter { it.eventType == VmsbAuditLogEntity.TYPE_VMSB_SYNC }
            3 -> allLogs.filter { it.eventType == VmsbAuditLogEntity.TYPE_LEGAL_DISPATCH }
            4 -> allLogs.filter { it.eventType == VmsbAuditLogEntity.TYPE_MCTS_ROLLOUT }
            else -> allLogs
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("vmsb_audit_log_history_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C18)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFF10B981), Color(0xFF38BDF8), Color(0xFFA78BFA))
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
                                .background(Brush.linearGradient(listOf(Color(0xFF047857), Color(0xFF0284C7)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HistoryEdu,
                                contentDescription = "Historial Room",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "REGISTRO DE AUDITORÍA (ROOM DB)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF1F5F9),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                "Persistencia Local • Protocolo de Berna & Sync VMSB",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("audit_dialog_close_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metrics Ribbon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MiniKpi(title = "TOTAL LOGS", value = "${allLogs.size}", color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
                    MiniKpi(title = "BERNA H/S", value = "${allLogs.count { it.eventType == VmsbAuditLogEntity.TYPE_BERNE_HANDSHAKE }}", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                    MiniKpi(title = "VMSB SYNC", value = "${allLogs.count { it.eventType == VmsbAuditLogEntity.TYPE_VMSB_SYNC }}", color = Color(0xFF10B981), modifier = Modifier.weight(1f))
                    MiniKpi(title = "LEGAL GOOGLE", value = "${allLogs.count { it.eventType == VmsbAuditLogEntity.TYPE_LEGAL_DISPATCH }}", color = Color(0xFFA78BFA), modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedFilterIndex,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color(0xFF10B981),
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    filterTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index },
                            text = {
                                Text(
                                    title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedFilterIndex == index) Color(0xFF10B981) else Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier.testTag("tab_audit_filter_$index")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Logs List
                if (filteredLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No hay registros en esta categoría aún", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredLogs, key = { it.id }) { log ->
                            AuditLogRowCard(log)
                        }
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
                            val file = viewModel.generateAuditLogPdfReport(context)
                            if (file != null && file.exists()) {
                                viewModel.viewPdfFile(context, file)
                            } else {
                                Toast.makeText(context, "Generando PDF...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("btn_view_audit_pdf")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver Reporte PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val file = viewModel.generateAuditLogPdfReport(context)
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
                            .testTag("btn_download_audit_pdf")
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
private fun AuditLogRowCard(log: VmsbAuditLogEntity) {
    val (badgeText, badgeColor) = when (log.eventType) {
        VmsbAuditLogEntity.TYPE_BERNE_HANDSHAKE -> "BERNA" to Color(0xFFD97706)
        VmsbAuditLogEntity.TYPE_VMSB_SYNC -> "VMSB SYNC" to Color(0xFF10B981)
        VmsbAuditLogEntity.TYPE_LEGAL_DISPATCH -> "LEGAL" to Color(0xFF38BDF8)
        VmsbAuditLogEntity.TYPE_MCTS_ROLLOUT -> "MCTS" to Color(0xFFA855F7)
        else -> "LOG" to Color(0xFF64748B)
    }

    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            badgeText,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        log.titleOrNode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1F5F9),
                        maxLines = 1
                    )
                }

                if (log.latencyMs > 0) {
                    Surface(
                        color = Color(0xFF064E3B),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "${log.latencyMs} ms",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(log.statusVerdict, fontSize = 9.5.sp, color = Color(0xFF94A3B8))

            if (log.detailsPayload.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(log.detailsPayload, fontSize = 9.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Hash: ${log.cryptographicHash.take(16)}...",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp,
                    color = Color(0xFF38BDF8)
                )
                Text(
                    log.timestampIso.take(19).replace("T", " "),
                    fontSize = 8.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun MiniKpi(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 7.5.sp, color = Color(0xFF94A3B8))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
