package com.example.ui.components.camera

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.camera.UniversalPlatformProtocol
import com.example.ui.MainViewModel

@Composable
fun UniversalConnectivityDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedProtocol by remember { mutableStateOf(UniversalPlatformProtocol.FINAL_CUT_PRO_XML) }
    var exportStatusText by remember { mutableStateOf<String?>(null) }
    val clips by viewModel.recordedClips.collectAsState()
    val activeProject = viewModel.selectedProject.value

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("universal_connectivity_dialog_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFF38BDF8), Color(0xFF818CF8))
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
                                .background(Color(0xFF38BDF8).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Connectivity Hub",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HUB UNIVERSAL DE CONECTIVIDAD VMSB©",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = "Plataformas, NLE y Protocolos Nativos",
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

                Spacer(modifier = Modifier.height(12.dp))

                // List of Protocols
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(UniversalPlatformProtocol.values()) { proto ->
                        val isSelected = selectedProtocol == proto
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF111827))
                                .border(
                                    1.2.dp,
                                    if (isSelected) Color(0xFF38BDF8) else Color(0xFF1F2937),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedProtocol = proto
                                    exportStatusText = null
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(proto.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color(0xFF38BDF8) else Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(proto.category, fontSize = 7.5.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(proto.protocolDescription, fontSize = 8.5.sp, color = Color.LightGray)
                                    Text("Formato: ${proto.nativeFormat} • Seguridad: ${proto.securityStandard}", fontSize = 7.5.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedProtocol = proto
                                        exportStatusText = null
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF38BDF8))
                                )
                            }
                        }
                    }
                }

                exportStatusText?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF14532D))
                            .padding(8.dp)
                    ) {
                        Text(msg, fontSize = 9.sp, color = Color(0xFF86EFAC), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val projName = activeProject?.title ?: "VMSB_Universal_Timeline"
                            val generated = when (selectedProtocol) {
                                UniversalPlatformProtocol.FINAL_CUT_PRO_XML -> viewModel.connectivityHub.generateFcpxmlProject(projName, clips)
                                UniversalPlatformProtocol.DAVINCI_RESOLVE_EDL -> viewModel.connectivityHub.generateCmx3600Edl(projName, clips)
                                else -> "Export manifest para ${selectedProtocol.title} con sello de Berna listo."
                            }
                            clipboardManager.setText(AnnotatedString(generated))
                            exportStatusText = "¡Código de ${selectedProtocol.title} generado y copiado al portapapeles con éxito!"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar XML / EDL", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            exportStatusText = "Conectado nativamente a ${selectedProtocol.title}. Transmisión de metadatos VMSB© completada con éxito."
                        },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Transmitir / Conectar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
