package com.example.ui.components.diagnostic

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.MainViewModel

/**
 * Diálogo Modal del Panel de Diagnóstico Integral del Motor VMSB©
 * Incluye Floating Action Button (FAB) para la exportación y revisión inmediata
 * del informe consolidado en PDF de registros de auditoría y rendimiento.
 */
@Composable
fun VmsbDiagnosticDashboardDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF070B14)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Contenido escroleable del dashboard
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp)
                        .padding(bottom = 72.dp)
                ) {
                    VmsbDiagnosticDashboardView(
                        viewModel = viewModel,
                        onClose = onDismissRequest
                    )
                }

                // ==============================================================
                // FLOATING ACTION BUTTON (FAB): EXPORTAR PDF AUDITORÍA & RENDIMIENTO
                // ==============================================================
                ExtendedFloatingActionButton(
                    onClick = {
                        val file = viewModel.exportAuditLogsAndPerformancePdf(context)
                        if (file != null && file.exists()) {
                            viewModel.viewPdfFile(context, file)
                            Toast.makeText(
                                context,
                                "PDF de Auditoría y Rendimiento generado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Generando reporte de auditoría en PDF...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .testTag("fab_export_audit_performance_pdf"),
                    containerColor = Color(0xFF0284C7),
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "Exportar PDF de Auditoría y Rendimiento",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Exportar PDF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

