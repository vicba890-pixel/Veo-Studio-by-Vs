package com.example.ui.components.camera

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.camera.BerneCopyrightCertificate

@Composable
fun BerneLedgerCertificateDialog(
    certificate: BerneCopyrightCertificate?,
    onDismiss: () -> Unit
) {
    if (certificate == null) return

    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("berne_certificate_dialog_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            ),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFFF59E0B), Color(0xFF10B981))
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header: Gold Seal & Title
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
                                .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Sello Berna",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PROTOCOLO DE BERNA (WIPO/OMPI)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF59E0B),
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Certificado de Derechos Reservados VMSB©",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Metadata Quick Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("BLOQUE LEDGER", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text("#${certificate.blockIndex}", fontSize = 12.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("JURISDICCIÓN INTERNACIONAL", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text("181 Estados Miembros WIPO", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("SELLO POST-CUÁNTICO", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text("Lattice Crystals-Dilithium", fontSize = 10.sp, color = Color(0xFFA855F7), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Legal Certificate Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF030712))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = certificate.formattedLegalDocument,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(certificate.formattedLegalDocument))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF38BDF8)
                        )
                    ) {
                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar Certificado", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Aceptar y Cerrar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}
