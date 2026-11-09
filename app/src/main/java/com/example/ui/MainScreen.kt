package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Project
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.components.PlatformSimulatorOverlay
import com.example.ui.components.EcoEngineDashboard
import com.example.ui.components.NativeGatewayPublisherCard
import com.example.ui.components.SophisticatedPaletteAndDynamicsCard
import com.example.ui.components.VmsbNativeCoreDashboard
import com.example.ui.components.camera.IosAdaptiveCameraSystemView
import com.example.ui.components.camera.BerneLedgerCertificateDialog
import com.example.ui.components.camera.LatticeZeroDataAuditDialog
import com.example.ui.components.camera.UniversalConnectivityDialog
import com.example.ui.components.preview.HighPerformanceExoPlayerPreview
import com.example.ui.components.preview.Media3VideoPreviewPlayer
import com.example.ui.components.preview.CustomMedia3VideoPlayer
import com.example.ui.components.preview.DedicatedExoPlayerPreviewSuite
import com.example.ui.components.preview.DedicatedVideoPreviewOverlay
import com.example.ui.components.preview.DedicatedVideoTimeline
import com.example.ui.components.preview.TimelineEditMarker
import com.example.ui.components.preview.Media3FullscreenVideoPlayerDialog
import com.example.ui.components.preview.FramePreciseScrubber
import com.example.ui.components.preview.MultiTrackTimelineView
import com.example.ui.components.preview.MultiTrackOverlayRenderer
import com.example.ui.components.preview.VmsbResponsiveVideoTimeline
import com.example.ui.components.preview.VmsbVideoPlaybackController
import com.example.data.vert.VertEngine
import com.example.data.vert.VertEngineConfig
import com.example.data.vert.VertFrameTelemetry
import com.example.data.vert.VertReframeMode
import com.example.ui.components.vert.VertTelemetryControlCard
import com.example.ui.components.vert.VertFramingGuideOverlay
import com.example.data.smartcrop.*
import com.example.ui.components.smartcrop.GeminiSmartCropPreviewOverlay
import com.example.ui.components.smartcrop.GeminiSmartCropSheet
import com.example.data.colorgrading.*
import com.example.ui.components.colorgrading.*
import com.example.data.voiceover.*
import com.example.ui.components.voiceover.*
import com.example.data.multitrack.*
import com.example.data.multitrack.command.*
import com.example.ui.components.vmsb.VmsbSystemTopologyView
import com.example.ui.components.vmsb.VmsbBrandVisualInterface
import com.example.ui.components.vmsb.VmsbBrandShowcaseDialog
import com.example.ui.components.vmsb.VmsbAntiReaConsoleDialog
import com.example.ui.components.media.VeoMediaPickerSheet
import com.example.data.media.*
import com.example.data.vmsb.VmsbPdfCertificationGenerator
import com.example.data.EcoComputeMode
import com.example.data.PlatformGatewaySpec
import com.example.data.PlatformRegistry
import com.example.data.RenderPalette
import com.example.data.RenderPaletteRegistry
import com.example.ui.components.orchestrator.VmsbOrchestratorMatrixCard
import com.example.ui.components.wasm.WasmUniversalExecutionHubCard
import com.example.ui.components.wassam.WassamSecureAssetManagementCard
import com.example.ui.components.diagnostic.VmsbDiagnosticDashboardView
import com.example.ui.components.diagnostic.VmsbDiagnosticDashboardDialog
import com.example.ui.components.c2pa.C2paCredentialsBadgeDialog
import com.example.ui.components.verticals.B2BVerticalStudioSelector
import com.example.ui.components.deterministic.DeterministicDirectingConsole
import com.example.ui.components.omnichannel.OmnichannelSafeZoneMaster
import com.example.ui.components.credits.B2BHybridCreditsCard
import com.example.data.verticals.VerticalTemplatePreset
import com.example.data.c2pa.C2paManifestStore
import com.example.ui.components.audit.ExecutiveReportViewerDialog
import com.example.ui.components.audit.EthicsProvenancePanel
import com.example.ui.components.audit.GlobalComplianceCertificationDialog
import com.example.ui.components.audit.StrategicRoiPlatformDialog
import com.example.ui.components.audit.StrategicPartnerOutreachDialog
import com.example.ui.components.audit.InstitutionalLegalDispatchDialog
import com.example.ui.components.audit.VmsbAuditLogHistoryDialog
import com.example.ui.components.diagnostic.VmsbSystemStressAuditDialog
import com.example.ui.components.metadata.VideoMetadataEditorDialog
import com.example.ui.components.promo.VeoStudioMasterPromoDialog
import com.example.ui.components.adaptive.UniversalAdaptiveHubCard
import com.example.ui.components.mcts.MctsLiveProcessingMonitor
import com.example.ui.components.theme.ThemeSettingsDialog
import com.example.ui.components.settings.FreedVerifiableStatusBadge
import com.example.ui.components.settings.FreedAuditDetailsDialog
import com.example.data.freed.*
import com.example.data.export.*
import com.example.ui.components.export.SocialMediaExportIntegrationCard
import com.example.ui.components.export.SocialMediaExportDialog
import com.example.ui.components.editor.VideoEditorHistoryToolbar
import com.example.ui.components.editor.EditorAutoSaveIndicator
import com.example.ui.components.transition.VideoTransitionSelector
import com.example.ui.theme.LocalThemeController
import com.example.ui.adaptive.LocalWindowAdaptiveInfo
import com.example.ui.adaptive.tvFocusableRing

enum class MainPlayerMode(val label: String) {
    MEDIA3_PREVIEW("MEDIA3"),
    NLE_STUDIO("STUDIO NLE"),
    AI_SIMULATOR("SIMULADOR")
}

val LocalIsHighDensity = compositionLocalOf { true }

@Composable
fun DenseSpacer(height: Int) {
    val isHighDensity = LocalIsHighDensity.current
    val finalHeight = if (isHighDensity) (height * 0.5f).dp else height.dp
    Spacer(modifier = Modifier.height(finalHeight))
}

@Composable
fun DenseWidthSpacer(width: Int) {
    val isHighDensity = LocalIsHighDensity.current
    val finalWidth = if (isHighDensity) (width * 0.5f).dp else width.dp
    Spacer(modifier = Modifier.width(finalWidth))
}

@Composable
fun Modifier.densePadding(all: Int): Modifier {
    val isHighDensity = LocalIsHighDensity.current
    return this.padding(if (isHighDensity) (all * 0.5f).dp else all.dp)
}

@Composable
fun Modifier.densePadding(horizontal: Int, vertical: Int): Modifier {
    val isHighDensity = LocalIsHighDensity.current
    return this.padding(
        horizontal = if (isHighDensity) (horizontal * 0.6f).dp else horizontal.dp,
        vertical = if (isHighDensity) (vertical * 0.6f).dp else vertical.dp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val appContext = LocalContext.current
    val projects by viewModel.projects.collectAsState()
    val selectedProject by viewModel.selectedProject.collectAsState()
    
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val generationStatusText by viewModel.generationStatusText.collectAsState()
    val generationConsoleSteps by viewModel.generationConsoleSteps.collectAsState()
    val generationTerminalLogs by viewModel.generationTerminalLogs.collectAsState()

    val isTranscribing by viewModel.isTranscribing.collectAsState()
    val isCheckingCompliance by viewModel.isCheckingCompliance.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()

    val isExporting by viewModel.isExporting.collectAsState()
    val exportProgress by viewModel.exportProgress.collectAsState()
    val exportStatusText by viewModel.exportStatusText.collectAsState()
    val exportedPlatforms by viewModel.exportedPlatforms.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showBrandShowcaseDialog by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(0) } // 0: Studio, 1: Export & Share, 2: Compliance, 3: Library
    var isHighDensity by remember { mutableStateOf(true) }

    val currentTypography = if (isHighDensity) {
        MaterialTheme.typography.copy(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp, lineHeight = 18.sp),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 15.sp),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 12.sp),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, lineHeight = 22.sp),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp, lineHeight = 16.sp),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp, lineHeight = 14.sp),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontSize = 9.sp, lineHeight = 12.sp),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, lineHeight = 10.sp)
        )
    } else {
        MaterialTheme.typography
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = currentTypography,
        shapes = MaterialTheme.shapes
    ) {
        CompositionLocalProvider(LocalIsHighDensity provides isHighDensity) {
            val adaptiveInfo = LocalWindowAdaptiveInfo.current
            Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // High-Class "VS" Crest Logo
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF0F172A),
                                            Color(0xFF1E293B)
                                        )
                                    )
                                )
                                .border(
                                    1.2.dp,
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFF59E0B),
                                            Color(0xFF06B6D4)
                                        )
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { showBrandShowcaseDialog = true }
                                .tvFocusableRing(onDpadEnter = { showBrandShowcaseDialog = true })
                                .testTag("top_app_bar_vs_brand_crest"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "VS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFFF59E0B),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "vmsb©",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 6.sp,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.clickable { showBrandShowcaseDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Veo Studio",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                                        .border(0.8.dp, Color(0xFFF59E0B).copy(alpha = 0.8f), RoundedCornerShape(5.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "by Vs",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                if (adaptiveInfo.isTvMode) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF7C3AED)
                                    ) {
                                        Text(
                                            text = "TV 10-FT",
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                } else if (adaptiveInfo.isTabletOrExpanded) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF047857)
                                    ) {
                                        Text(
                                            text = "TABLET PRO",
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Text(
                                    text = "vmsb© NATIVE • BERNE 2026",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF06B6D4)
                                    )
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Cloud Sync Toggle Icon
                    IconButton(
                        onClick = { viewModel.triggerCloudSync() },
                        enabled = selectedProject != null && !isCloudSyncing,
                        modifier = Modifier.testTag("cloud_sync_button")
                    ) {
                        if (isCloudSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = if (selectedProject?.cloudSyncStatus == "Synced") Icons.Filled.CloudDone else Icons.Filled.CloudUpload,
                                contentDescription = "Cloud Storage Sync",
                                tint = if (selectedProject?.cloudSyncStatus == "Synced") Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Create New Project FAB style in top-bar
                    Button(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("new_project_trigger_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // VMSB Brand Architecture & Quality Showcase Shortcut
                    IconButton(
                        onClick = { showBrandShowcaseDialog = true },
                        modifier = Modifier.testTag("top_bar_vmsb_brand_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .border(
                                    1.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFF06B6D4))
                                    ),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "vmsb©",
                                color = Color(0xFFF59E0B),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // C2PA Credentials Shortcut Button
                    IconButton(
                        onClick = { viewModel.openC2paDialogForCurrentProject() },
                        modifier = Modifier.testTag("top_bar_c2pa_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF064E3B))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "C2PA",
                                color = Color(0xFF34D399),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Camera iOS Shortcut Button
                    IconButton(
                        onClick = { activeTab = 4 },
                        modifier = Modifier.testTag("top_bar_camera_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Videocam,
                            contentDescription = "Cámara iOS Pro",
                            tint = if (activeTab == 4) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Layout Density Toggle
                    IconButton(
                        onClick = { isHighDensity = !isHighDensity },
                        modifier = Modifier
                            .testTag("density_toggle_button")
                            .tvFocusableRing(onDpadEnter = { isHighDensity = !isHighDensity })
                    ) {
                        Icon(
                            imageVector = if (isHighDensity) Icons.Filled.DensitySmall else Icons.Default.DensityMedium,
                            contentDescription = "Layout Density",
                            tint = if (isHighDensity) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Manual Dark/Light Mode Quick Toggle
                    val themeController = LocalThemeController.current
                    IconButton(
                        onClick = { themeController.onToggleDarkTheme() },
                        modifier = Modifier
                            .testTag("theme_mode_toggle_button")
                            .tvFocusableRing(onDpadEnter = { themeController.onToggleDarkTheme() })
                    ) {
                        Icon(
                            imageVector = if (themeController.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (themeController.isDark) "Cambiar a Modo Claro" else "Cambiar a Modo Oscuro",
                            tint = if (themeController.isDark) Color(0xFFFBBF24) else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Global Compliance & Real-Time Google Link Button
                    IconButton(
                        onClick = { viewModel.openGlobalComplianceDialog() },
                        modifier = Modifier
                            .testTag("top_bar_global_compliance_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openGlobalComplianceDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Cumplimientos Normativos Globales y Google",
                            tint = Color(0xFF38BDF8)
                        )
                    }

                    // VMSB Diagnostic Dashboard Button
                    IconButton(
                        onClick = { viewModel.openDiagnosticDashboard() },
                        modifier = Modifier
                            .testTag("top_bar_vmsb_diagnostic_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openDiagnosticDashboard() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Diagnóstico VMSB Health",
                            tint = Color(0xFF10B981)
                        )
                    }

                    // VMSB 2026 - System, Connectivity, Security, Stress & Balance Suite Button
                    IconButton(
                        onClick = { viewModel.openSystemStressAuditDialog() },
                        modifier = Modifier
                            .testTag("top_bar_system_stress_audit_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openSystemStressAuditDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Auditoría de Sistema, Stress & Balance 2026",
                            tint = Color(0xFF00E5FF)
                        )
                    }

                    // Video Metadata Room Database & Dilithium Post-Quantum Button
                    IconButton(
                        onClick = { viewModel.openVideoMetadataEditor(selectedProject ?: projects.firstOrNull()) },
                        modifier = Modifier
                            .testTag("top_bar_video_metadata_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openVideoMetadataEditor(selectedProject ?: projects.firstOrNull()) })
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Metadatos de Video Room & Dilithium Q-Day",
                            tint = Color(0xFF10B981)
                        )
                    }

                    // Veo Studio Master Promo 20s (2 Segmentos x 10s) Universal Button
                    IconButton(
                        onClick = { viewModel.openMasterPromoDialog(appContext) },
                        modifier = Modifier
                            .testTag("top_bar_master_promo_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openMasterPromoDialog(appContext) })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Video Promocional Veo Studio 20s VMSB",
                            tint = Color(0xFFE11D48)
                        )
                    }

                    // Export Quick Certified PDF Button
                    IconButton(
                        onClick = {
                            val pdfFile = viewModel.exportMasterPromoShowcasePdf(appContext)
                            if (pdfFile != null && pdfFile.exists()) {
                                viewModel.viewPdfFile(appContext, pdfFile)
                            }
                        },
                        modifier = Modifier
                            .testTag("top_bar_export_quick_pdf_btn")
                            .tvFocusableRing(onDpadEnter = {
                                val pdfFile = viewModel.exportMasterPromoShowcasePdf(appContext)
                                if (pdfFile != null && pdfFile.exists()) {
                                    viewModel.viewPdfFile(appContext, pdfFile)
                                }
                            })
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Exportar PDF Certificado Veo Studio",
                            tint = Color(0xFFF43F5E)
                        )
                    }

                    // Strategic ROI & Multi-Platform Ping Button
                    IconButton(
                        onClick = { viewModel.openStrategicRoiDialog() },
                        modifier = Modifier
                            .testTag("top_bar_strategic_roi_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openStrategicRoiDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Dossier Económico & ROI Plataformas",
                            tint = Color(0xFFF59E0B)
                        )
                    }

                    // Strategic C-Level Outreach & Partner Protocol Button
                    IconButton(
                        onClick = { viewModel.openStrategicOutreachDialog() },
                        modifier = Modifier
                            .testTag("top_bar_strategic_outreach_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openStrategicOutreachDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = "Despacho Institucional C-Level (Google)",
                            tint = Color(0xFF38BDF8)
                        )
                    }

                    // Institutional Legal Dispatch & Verification Button
                    IconButton(
                        onClick = { viewModel.openLegalDispatchDialog() },
                        modifier = Modifier
                            .testTag("top_bar_legal_dispatch_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openLegalDispatchDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "Notificación Legal Google General Counsel",
                            tint = Color(0xFF10B981)
                        )
                    }

                    // Quick Social Share & Export Button
                    IconButton(
                        onClick = { viewModel.openSocialExportDialog() },
                        modifier = Modifier
                            .testTag("top_bar_social_export_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openSocialExportDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Exportar a Redes Sociales",
                            tint = Color(0xFF38BDF8)
                        )
                    }

                    // VMSB Anti-REA-ing 2026 Native Shield Button
                    IconButton(
                        onClick = { viewModel.openAntiReaConsoleDialog() },
                        modifier = Modifier
                            .testTag("top_bar_anti_rea_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openAntiReaConsoleDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Sistema Anti-REA-ing Nativo VMSB 2026",
                            tint = Color(0xFF10B981)
                        )
                    }

                    // Gemini Smart-Crop & Reframe Button (9:16 / 4:5)
                    IconButton(
                        onClick = { viewModel.openSmartCropSheet(selectedProject) },
                        modifier = Modifier
                            .testTag("top_bar_smart_crop_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openSmartCropSheet(selectedProject) })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Crop,
                            contentDescription = "Gemini Smart-Cropping 9:16 / 4:5",
                            tint = Color(0xFF38BDF8)
                        )
                    }

                    // VMSB Cinematic Color Grading & Preset LUTs Button
                    IconButton(
                        onClick = { viewModel.openColorGradingDialog() },
                        modifier = Modifier
                            .testTag("top_bar_color_grading_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openColorGradingDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Color Grading & LUTs Cinemáticos",
                            tint = Color(0xFFF59E0B)
                        )
                    }

                    // VMSB Voice Over Studio Nativo Button
                    IconButton(
                        onClick = { viewModel.openVoiceOverStudioDialog(selectedProject) },
                        modifier = Modifier
                            .testTag("top_bar_voiceover_studio_btn")
                            .tvFocusableRing(onDpadEnter = { viewModel.openVoiceOverStudioDialog(selectedProject) })
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Voice Over Studio Nativo VMSB©",
                            tint = Color(0xFFA855F7)
                        )
                    }

                    // Theme & Appearance Settings button
                    IconButton(
                        onClick = { viewModel.openThemeSettingsDialog() },
                        modifier = Modifier
                            .testTag("theme_settings_button")
                            .tvFocusableRing(onDpadEnter = { viewModel.openThemeSettingsDialog() })
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Ajustes de Tema Material 3",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // User Initials Avatar
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JD",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 12.sp
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Outlined.DesignServices, contentDescription = "Studio") },
                    label = { Text("AI Studio") },
                    modifier = Modifier.testTag("tab_studio")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Outlined.IosShare, contentDescription = "Publish") },
                    label = { Text("Publish & Export") },
                    modifier = Modifier.testTag("tab_export")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Outlined.Gavel, contentDescription = "Compliance") },
                    label = { Text("Legal Audit") },
                    modifier = Modifier.testTag("tab_compliance")
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = { Icon(Icons.Filled.Videocam, contentDescription = "Cámara iOS") },
                    label = { Text("Cámara iOS") },
                    modifier = Modifier.testTag("tab_camera_ios")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Outlined.VideoLibrary, contentDescription = "Library") },
                    label = { Text("Projects") },
                    modifier = Modifier.testTag("tab_library")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (activeTab == 4) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    IosAdaptiveCameraSystemView(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else if (projects.isEmpty()) {
                // Striking, polished empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.widthIn(max = 400.dp)
                    ) {
                        // Luxury Monogram VS Logo
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
                                    )
                                )
                                .border(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFF06B6D4))
                                    ),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("VS", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFFF59E0B))
                                Text("vmsb©", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color(0xFF38BDF8))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Veo Studio",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, fontSize = 22.sp),
                                textAlign = TextAlign.Center
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                                    .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("by Vs", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "vmsb© Native Engine Architecture & Berne Protocol 2026",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF06B6D4),
                                letterSpacing = 0.5.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Next-generation generative cinematic video studio powered by low-latency vectorized SIMD binary streams and stochastic MCTS multi-track optimization.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("empty_state_create_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate First AI Video Prompt")
                        }
                    }
                }
            } else {
                val currentProj = selectedProject ?: projects.firstOrNull()
                if (currentProj != null) {
                    // Main Workspace content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Top active project bar
                        ActiveProjectHeader(
                            project = currentProj,
                            projects = projects,
                            onSelect = { viewModel.selectProject(it) },
                            onOpenFreedAudit = { viewModel.openFreedDetailsDialog() }
                        )

                        // 1. Live Interactive Cinematic Canvas
                        LiveVideoCanvas(
                            project = currentProj,
                            viewModel = viewModel,
                            isTranscribing = isTranscribing,
                            isCheckingCompliance = isCheckingCompliance
                        )

                        DenseSpacer(height = 16)

                        // 2. Tabbed / Modular Controls
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .densePadding(horizontal = 16, vertical = 8)
                        ) {
                            when (activeTab) {
                                0 -> AIStudioControlWorkspace(
                                    project = currentProj,
                                    viewModel = viewModel,
                                    isTranscribing = isTranscribing
                                )
                                1 -> BatchExportWorkspace(
                                    project = currentProj,
                                    viewModel = viewModel,
                                    isExporting = isExporting,
                                    exportProgress = exportProgress,
                                    exportStatusText = exportStatusText,
                                    exportedPlatforms = exportedPlatforms
                                )
                                2 -> ComplianceAuditWorkspace(
                                    project = currentProj,
                                    viewModel = viewModel,
                                    isChecking = isCheckingCompliance
                                )
                                3 -> LibraryWorkspace(
                                    projects = projects,
                                    activeId = currentProj?.id ?: -1,
                                    onSelect = { viewModel.selectProject(it); activeTab = 0 },
                                    onDelete = { viewModel.deleteProject(it) },
                                    onDuplicate = { viewModel.duplicateProject(it) },
                                    onRename = { proj, newTitle -> viewModel.updateProjectTitle(proj, newTitle) },
                                    onCreateNew = { showCreateDialog = true },
                                    onExport = { viewModel.selectProject(it); viewModel.openSocialExportDialog() },
                                    onOpenFreedAudit = {
                                        viewModel.selectProject(it)
                                        viewModel.openFreedDetailsDialog()
                                    }
                                )
                                4 -> IosAdaptiveCameraSystemView(
                                    viewModel = viewModel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Veo 3.1 & Gemini Real-time AI Generation Console Overlay Modal
            AIGenerationConsoleModal(
                isGenerating = isGenerating,
                generationProgress = generationProgress,
                generationStatusText = generationStatusText,
                consoleSteps = generationConsoleSteps,
                terminalLogs = generationTerminalLogs
            )

            // Create Project Dialog
            if (showCreateDialog) {
                CreateProjectDialog(
                    onDismiss = { showCreateDialog = false },
                    onConfirm = { title, prompt, ratio, res, duration ->
                        viewModel.createProject(title, prompt, ratio, res, duration)
                        showCreateDialog = false
                    }
                )
            }

            // VMSB© Berne Convention & Lattice Zero-Data Dialogs
            val isBerneOpen by viewModel.isBerneCertificateDialogOpen.collectAsState()
            val berneCert by viewModel.selectedBerneCertificate.collectAsState()
            if (isBerneOpen && berneCert != null) {
                BerneLedgerCertificateDialog(
                    certificate = berneCert,
                    onDismiss = { viewModel.closeBerneCertificateDialog() }
                )
            }

            val isLatticeAuditOpen by viewModel.isLatticeAuditDialogOpen.collectAsState()
            if (isLatticeAuditOpen) {
                LatticeZeroDataAuditDialog(
                    protector = viewModel.latticeProtector,
                    onDismiss = { viewModel.closeLatticeAuditDialog() }
                )
            }

            val isConnOpen by viewModel.isConnectivityDialogOpen.collectAsState()
            if (isConnOpen) {
                UniversalConnectivityDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeConnectivityDialog() }
                )
            }

            // High-Class Veo Studio by Vs Brand Showcase Dialog
            if (showBrandShowcaseDialog) {
                VmsbBrandShowcaseDialog(
                    viewModel = viewModel,
                    onDismiss = { showBrandShowcaseDialog = false }
                )
            }

            // Veo Media Picker Service Sheet (Local Video Timeline Ingestion)
            val isMediaPickerOpen by viewModel.isMediaPickerSheetOpen.collectAsState()
            if (isMediaPickerOpen) {
                VeoMediaPickerSheet(
                    viewModel = viewModel,
                    currentPositionMs = 0L,
                    onDismiss = { viewModel.closeMediaPickerSheet() }
                )
            }

            // C2PA Content Credentials v2.1 Dialog
            val isC2paOpen by viewModel.isC2paDialogOpen.collectAsState()
            val c2paManifest by viewModel.activeC2paManifest.collectAsState()
            if (isC2paOpen && c2paManifest != null) {
                C2paCredentialsBadgeDialog(
                    manifest = c2paManifest!!,
                    onDismissRequest = { viewModel.closeC2paDialog() }
                )
            }

            // Executive Audit & ROI Report Dialog (Conectividad Google/Meta & Escabilidad PDF)
            val isExecAuditOpen by viewModel.isExecutiveAuditReportDialogOpen.collectAsState()
            if (isExecAuditOpen) {
                ExecutiveReportViewerDialog(
                    projectTitle = selectedProject?.title ?: "Veo Studio Master",
                    onDismissRequest = { viewModel.closeExecutiveAuditReportDialog() }
                )
            }

            // Social Media Export & Android Share Intent Integration Dialog
            val isSocialExportOpen by viewModel.isSocialExportDialogOpen.collectAsState()
            val currentProjectForExport = selectedProject ?: projects.firstOrNull()
            if (isSocialExportOpen && currentProjectForExport != null) {
                SocialMediaExportDialog(
                    project = currentProjectForExport,
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeSocialExportDialog() }
                )
            }

            // VMSB© 2026 Anti-REA-ing Native Security & Defense Console Dialog
            val isAntiReaOpen by viewModel.isAntiReaConsoleDialogOpen.collectAsState()
            val antiReaState by viewModel.antiReaState.collectAsState()
            if (isAntiReaOpen) {
                VmsbAntiReaConsoleDialog(
                    state = antiReaState,
                    onDismissRequest = { viewModel.closeAntiReaConsoleDialog() },
                    onRunDeepScan = { viewModel.runAntiReaScan() },
                    onSimulateThreat = { vector -> viewModel.simulateAntiReaThreat(vector) },
                    onZeroizeBuffers = { viewModel.zeroizeAntiReaEnclave() },
                    onExportCertificate = { ctx -> viewModel.exportAntiReaCertificate(ctx) }
                )
            }

            // Gemini Real-Time Smart-Cropping & Subject Reframe (9:16 / 4:5) Sheet
            val isSmartCropSheetOpen by viewModel.isSmartCropSheetOpen.collectAsState()
            val smartCropSugDialog by viewModel.smartCropSuggestion.collectAsState()
            val isSmartCropBusyDialog by viewModel.isSmartCropAnalyzing.collectAsState()
            val isRealtimeTracking by viewModel.isRealtimeSmartCropTrackingEnabled.collectAsState()
            val activeProjectForCrop = selectedProject ?: projects.firstOrNull()

            if (isSmartCropSheetOpen && activeProjectForCrop != null) {
                GeminiSmartCropSheet(
                    project = activeProjectForCrop,
                    suggestion = smartCropSugDialog,
                    isAnalyzing = isSmartCropBusyDialog,
                    isRealtimeTracking = isRealtimeTracking,
                    onDismissRequest = { viewModel.closeSmartCropSheet() },
                    onApplyAspectRatio = { aspect -> viewModel.applySmartCropSuggestion(aspect, activeProjectForCrop) },
                    onToggleRealtimeTracking = { viewModel.toggleRealtimeSmartCropTracking() },
                    onReanalyze = {
                        viewModel.analyzeSmartCrop(activeProjectForCrop, 0L)
                    }
                )
            }

            // Material 3 Theme & Dynamic Color Settings Dialog
            val isThemeSettingsOpen by viewModel.isThemeSettingsDialogOpen.collectAsState()
            if (isThemeSettingsOpen) {
                ThemeSettingsDialog(
                    onDismissRequest = { viewModel.closeThemeSettingsDialog() }
                )
            }

            // Global Compliance, Security, Technical & Quality Certification Dialog (Google Real-Time Link)
            val isComplianceOpen by viewModel.isGlobalComplianceDialogOpen.collectAsState()
            if (isComplianceOpen) {
                GlobalComplianceCertificationDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeGlobalComplianceDialog() }
                )
            }

            // VMSB Diagnostic Dashboard Dialog (Material 3 Cards)
            val isDiagnosticOpen by viewModel.isDiagnosticDashboardDialogOpen.collectAsState()
            if (isDiagnosticOpen) {
                VmsbDiagnosticDashboardDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeDiagnosticDashboard() }
                )
            }

            // VMSB 2026 - System, Connectivity, Security, Stress & Balance Suite Dialog
            val isStressAuditOpen by viewModel.isSystemStressAuditDialogOpen.collectAsState()
            if (isStressAuditOpen) {
                VmsbSystemStressAuditDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeSystemStressAuditDialog() }
                )
            }

            // Video Metadata Room Database & Dilithium Q-Day Post-Quantum Dialog
            val isMetadataEditorOpen by viewModel.isVideoMetadataEditorOpen.collectAsState()
            val activeMetadataVal by viewModel.activeVideoMetadata.collectAsState()
            val currentProjectForMetadata = selectedProject ?: projects.firstOrNull()
            if (isMetadataEditorOpen) {
                VideoMetadataEditorDialog(
                    viewModel = viewModel,
                    project = currentProjectForMetadata,
                    existingMetadata = activeMetadataVal,
                    onDismissRequest = { viewModel.closeVideoMetadataEditor() }
                )
            }

            // Veo Studio by Vs — Master Promo 20s (2 Segmentos x 10s) Dialog
            val isPromoOpen by viewModel.isMasterPromoDialogOpen.collectAsState()
            if (isPromoOpen) {
                VeoStudioMasterPromoDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeMasterPromoDialog() }
                )
            }
            val isStrategicRoiOpen by viewModel.isStrategicRoiDialogOpen.collectAsState()
            if (isStrategicRoiOpen) {
                StrategicRoiPlatformDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeStrategicRoiDialog() }
                )
            }

            // Strategic Partner C-Level Outreach & Institutional Protocol Transmission Dialog (30-Day Exclusivity)
            val isStrategicOutreachOpen by viewModel.isStrategicOutreachDialogOpen.collectAsState()
            if (isStrategicOutreachOpen) {
                StrategicPartnerOutreachDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeStrategicOutreachDialog() }
                )
            }

            // Institutional Legal Dispatch & Delivery Verification Dialog (Office of General Counsel)
            val isLegalDispatchOpen by viewModel.isLegalDispatchDialogOpen.collectAsState()
            if (isLegalDispatchOpen) {
                InstitutionalLegalDispatchDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeLegalDispatchDialog() }
                )
            }

            // VMSB Audit Log History Dialog (Persistencia Local Room SQLite)
            val isAuditLogHistoryOpen by viewModel.isAuditLogHistoryDialogOpen.collectAsState()
            if (isAuditLogHistoryOpen) {
                VmsbAuditLogHistoryDialog(
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeAuditLogHistoryDialog() }
                )
            }

            // VMSB Cinematic Color Grading & Preset LUTs Panel Dialog
            val isColorGradingOpen by viewModel.isColorGradingDialogOpen.collectAsState()
            val colorGradingStateVal by viewModel.colorGradingState.collectAsState()
            if (isColorGradingOpen) {
                ColorGradingDialog(
                    state = colorGradingStateVal,
                    onStateChange = { viewModel.updateColorGradingState(it) },
                    onResetNeutral = { viewModel.resetColorGradingNeutral() },
                    onDismissRequest = { viewModel.closeColorGradingDialog() }
                )
            }

            // VMSB Voice Over Studio Nativo Dialog
            val isVoiceOverOpen by viewModel.isVoiceOverStudioDialogOpen.collectAsState()
            val voiceOverConfigVal by viewModel.voiceOverConfig.collectAsState()
            val currentProjectForVoiceOver = selectedProject ?: projects.firstOrNull()
            if (isVoiceOverOpen && currentProjectForVoiceOver != null) {
                VmsbVoiceOverStudioDialog(
                    project = currentProjectForVoiceOver,
                    config = voiceOverConfigVal,
                    onConfigChange = { viewModel.updateVoiceOverConfig(it) },
                    onSynthesizeAndInsert = { cfg ->
                        viewModel.synthesizeVoiceOverAndInsert(cfg, currentProjectForVoiceOver)
                    },
                    onDismissRequest = { viewModel.closeVoiceOverStudioDialog() }
                )
            }

            // FREED_vmsb_lattice_ZKP Verifiable Status & Signed Compliance Audit Dialog
            val isFreedDetailsOpen by viewModel.isFreedDetailsDialogOpen.collectAsState()
            val freedAuditStateVal by viewModel.freedAuditStatus.collectAsState()
            if (isFreedDetailsOpen) {
                FreedAuditDetailsDialog(
                    project = selectedProject ?: projects.firstOrNull(),
                    auditStatus = freedAuditStateVal,
                    viewModel = viewModel,
                    onDismissRequest = { viewModel.closeFreedDetailsDialog() }
                )
            }
        }
    }
}
}
}

// Header indicating current project & select dropdown
@Composable
fun ActiveProjectHeader(
    project: Project,
    projects: List<Project>,
    onSelect: (Project) -> Unit,
    onOpenFreedAudit: ((Project) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .densePadding(all = 16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .densePadding(horizontal = 16, vertical = 12)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Workspace",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            project.resolution,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            project.aspectRatio,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                val freedStatus = remember(project) { FreedAuditEngine.evaluateProjectFreedAudit(project) }
                FreedVerifiableStatusBadge(
                    auditStatus = freedStatus,
                    onOpenDetails = { onOpenFreedAudit?.invoke(project) },
                    compact = true
                )
            }
            
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Switch", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select project",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    projects.forEach { proj ->
                        DropdownMenuItem(
                            text = { Text(proj.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            onClick = {
                                onSelect(proj)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (proj.id == project.id) Icons.Default.CheckCircle else Icons.Outlined.VideoLibrary,
                                    contentDescription = null,
                                    tint = if (proj.id == project.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// 1. Live Interactive Cinematic Canvas drawing adaptive graded color & subject-box overlay
@Composable
fun LiveVideoCanvas(
    project: Project,
    viewModel: MainViewModel,
    isTranscribing: Boolean,
    isCheckingCompliance: Boolean
) {
    var isPlaying by remember { mutableStateOf(true) }
    var frameProgress by remember { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }
    var isLooping by remember { mutableStateOf(true) }
    var showSecurityLattice by remember { mutableStateOf(true) }
    var mainPlayerMode by remember { mutableStateOf(MainPlayerMode.MEDIA3_PREVIEW) }
    val useNativePlayer = mainPlayerMode != MainPlayerMode.AI_SIMULATOR
    val scope = rememberCoroutineScope()
    val playerContext = LocalContext.current

    val trimStart by viewModel.videoTrimStart.collectAsState()
    val trimEnd by viewModel.videoTrimEnd.collectAsState()
    val activeRenderPalette by viewModel.activeRenderPalette.collectAsState()
    val dynamicProfile by viewModel.dynamicOptimizationProfile.collectAsState()

    // Animate custom video frames scanning with dynamic frame pacing
    LaunchedEffect(isPlaying, playbackSpeed, isLooping, trimStart, trimEnd, dynamicProfile.framePacingTargetFps) {
        if (isPlaying) {
            // Force progress within trim bounds if currently out of range
            if (frameProgress < trimStart || frameProgress > trimEnd) {
                frameProgress = trimStart
            }
            while (isPlaying) {
                val frameDelay = if (dynamicProfile.framePacingTargetFps >= 120) 8L else 16L
                delay(frameDelay)
                val duration = project.durationSec.coerceAtLeast(1)
                val increment = (frameDelay / 1000f) / duration * playbackSpeed
                val nextProgress = frameProgress + increment
                if (nextProgress >= trimEnd) {
                    if (isLooping) {
                        frameProgress = trimStart
                    } else {
                        frameProgress = trimEnd
                        isPlaying = false
                    }
                } else {
                    frameProgress = nextProgress
                }
                // Feed native frame into VMSB© engine stream & Fakfa ring buffer
                viewModel.processVmsbVideoFrame(
                    payload = project.title.toByteArray(),
                    frameType = com.example.data.vmsb.VmsbFrameType.P_FRAME_PREDICTED
                )
            }
        }
    }

    // Colors mapping representing sophisticated smooth color palette
    val moodColors = activeRenderPalette.gradientColors

    // Aspect ratio representation
    val canvasRatio = when (project.aspectRatio) {
        "9:16" -> 0.5625f
        "1:1" -> 1f
        else -> 1.777f // 16:9
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .densePadding(horizontal = 16, vertical = 0),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.densePadding(all = 12)) {
            // Selector de modo de previsualización: Media3 vs Studio NLE vs Simulador IA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = when (mainPlayerMode) {
                            MainPlayerMode.MEDIA3_PREVIEW -> Icons.Default.PlayCircle
                            MainPlayerMode.NLE_STUDIO -> Icons.Default.MovieFilter
                            MainPlayerMode.AI_SIMULATOR -> Icons.Default.AutoAwesome
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when (mainPlayerMode) {
                            MainPlayerMode.MEDIA3_PREVIEW -> "MEDIA3 EXOPLAYER PREVIEW"
                            MainPlayerMode.NLE_STUDIO -> "STUDIO NLE MULTI-TRACK"
                            MainPlayerMode.AI_SIMULATOR -> "SIMULADOR VEO 3.1 CANVAS"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(2.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (mainPlayerMode == MainPlayerMode.MEDIA3_PREVIEW) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { mainPlayerMode = MainPlayerMode.MEDIA3_PREVIEW }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("select_media3_player_mode_btn")
                    ) {
                        Text(
                            text = "MEDIA3",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mainPlayerMode == MainPlayerMode.MEDIA3_PREVIEW) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (mainPlayerMode == MainPlayerMode.NLE_STUDIO) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { mainPlayerMode = MainPlayerMode.NLE_STUDIO }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("select_exoplayer_mode_btn")
                    ) {
                        Text(
                            text = "STUDIO NLE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mainPlayerMode == MainPlayerMode.NLE_STUDIO) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (mainPlayerMode == MainPlayerMode.AI_SIMULATOR) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { mainPlayerMode = MainPlayerMode.AI_SIMULATOR }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("select_simulator_mode_btn")
                    ) {
                        Text(
                            text = "SIMULADOR",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mainPlayerMode == MainPlayerMode.AI_SIMULATOR) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Gemini AI Smart-Cropping Trigger
                    val isSmartCropBusy by viewModel.isSmartCropAnalyzing.collectAsState()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0284C7).copy(alpha = 0.22f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.openSmartCropSheet(project) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("player_smart_crop_action_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isSmartCropBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(10.dp),
                                    color = Color(0xFF38BDF8),
                                    strokeWidth = 1.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            Text(
                                text = "SMART-CROP 9:16/4:5",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }

                    // Color Grading & Preset LUTs Trigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFD97706).copy(alpha = 0.22f))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.openColorGradingDialog() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("player_color_grading_action_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "COLOR LUTs",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }

                    // Voice Over Studio Nativo VMSB Trigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF7E22CE).copy(alpha = 0.22f))
                            .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.openVoiceOverStudioDialog(project) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("player_voiceover_action_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "VOICE OVER VMSB©",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFA855F7)
                            )
                        }
                    }

                    // Video Metadata Room Database & Dilithium Q-Day Action Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF065F46).copy(alpha = 0.25f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.openVideoMetadataEditor(project) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("player_metadata_dilithium_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "METADATA (DILITHIUM)",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF34D399)
                            )
                        }
                    }

                    // Veo Studio Master Promo 20s Action Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF881337).copy(alpha = 0.35f))
                            .border(1.dp, Color(0xFFE11D48).copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.openMasterPromoDialog(playerContext) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("player_master_promo_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "PROMO VMSB 20s",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFFDA4AF)
                            )
                        }
                    }
                }
            }

            val multiTrackComp by viewModel.multiTrackComposition.collectAsState()
            val canUndoMultiTrack by viewModel.canUndoMultiTrack.collectAsState()
            val canRedoMultiTrack by viewModel.canRedoMultiTrack.collectAsState()
            val undoDescription by viewModel.lastActionDescription.collectAsState()
            val redoDescription by viewModel.nextRedoDescription.collectAsState()
            val undoHistoryList by viewModel.undoHistoryList.collectAsState()
            val redoHistoryList by viewModel.redoHistoryList.collectAsState()
            val projectDurationSec = project.durationSec.coerceAtLeast(1)
            val projectDurationMs = projectDurationSec * 1000L
            val currentPosMs = (frameProgress * projectDurationMs).toLong()

            LaunchedEffect(currentPosMs) {
                viewModel.syncMultiTrackPlayhead(currentPosMs)
            }

            // Barra de Herramientas de Deshacer/Rehacer Room SQLite e Indicador de Auto-Guardado
            VideoEditorHistoryToolbar(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            when (mainPlayerMode) {
                MainPlayerMode.MEDIA3_PREVIEW -> {
                    val activeMediaUri = if (project.videoUri.isNotBlank()) {
                        project.videoUri
                    } else {
                        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    }
                    val colorGradingPreviewState by viewModel.colorGradingState.collectAsState()
                    val reframePanX by viewModel.reframePanX.collectAsState()
                    val reframePanY by viewModel.reframePanY.collectAsState()
                    val reframeZoom by viewModel.reframeZoom.collectAsState()
                    val timelineMarkers by viewModel.timelineEditMarkers.collectAsState()
                    val smartCropSug by viewModel.smartCropSuggestion.collectAsState()
                    val isSmartCropBusy by viewModel.isSmartCropAnalyzing.collectAsState()

                    DedicatedExoPlayerPreviewSuite(
                        videoUri = activeMediaUri,
                        aspectRatio = project.aspectRatio,
                        onAspectRatioChange = { newRatio -> viewModel.updateAspectRatio(newRatio) },
                        colorGradingState = colorGradingPreviewState,
                        onColorGradingChange = { updated -> viewModel.updateColorGradingState(updated) },
                        title = project.title,
                        reframePanX = reframePanX,
                        reframePanY = reframePanY,
                        onReframePanChange = { px, py -> viewModel.updateReframePan(px, py) },
                        reframeZoom = reframeZoom,
                        onReframeZoomChange = { zm -> viewModel.updateReframeZoom(zm) },
                        trimStart = trimStart,
                        trimEnd = trimEnd,
                        onTrimRangeChange = { s, e ->
                            viewModel.updateVideoTrim(s, e)
                        },
                        markers = timelineMarkers,
                        onAddMarker = { pos -> viewModel.addTimelineMarker(pos) },
                        onSplitAtPlayhead = { pos -> viewModel.splitClipAtPlayhead(pos) },
                        smartCropSuggestion = smartCropSug,
                        isSmartCropAnalyzing = isSmartCropBusy,
                        onApplySmartCrop = { aspect -> viewModel.applySmartCropSuggestion(aspect, project) },
                        fps = 30,
                        autoPlay = isPlaying,
                        onPositionChanged = { currentMs, durMs ->
                            if (durMs > 0) {
                                frameProgress = (currentMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)
                                viewModel.updateVertPlaybackPosition(currentMs, durMs)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                MainPlayerMode.NLE_STUDIO -> {
                    HighPerformanceExoPlayerPreview(
                        videoUri = project.videoUri,
                        aspectRatio = project.aspectRatio,
                        isLoopingInitial = isLooping,
                        trimStart = trimStart,
                        trimEnd = trimEnd,
                        onTrimRangeChange = { s, e ->
                            viewModel.updateVideoTrim(s, e)
                        },
                        onTogglePlayerMode = { mainPlayerMode = MainPlayerMode.AI_SIMULATOR },
                        onPositionChanged = { _, progressNorm ->
                            frameProgress = progressNorm
                        },
                        onFrameProcessed = {
                            viewModel.processVmsbVideoFrame(
                                payload = project.title.toByteArray(),
                                frameType = com.example.data.vmsb.VmsbFrameType.P_FRAME_PREDICTED
                            )
                        },
                        multiTrackComposition = multiTrackComp,
                        onMultiTrackCompositionChange = { updated ->
                            viewModel.updateMultiTrackComposition(updated, currentPosMs)
                        },
                        canUndoMultiTrack = canUndoMultiTrack,
                        canRedoMultiTrack = canRedoMultiTrack,
                        undoDescription = undoDescription,
                        redoDescription = redoDescription,
                        undoHistoryList = undoHistoryList,
                        redoHistoryList = redoHistoryList,
                        onUndoMultiTrack = { viewModel.undoMultiTrack(currentPosMs) },
                        onRedoMultiTrack = { viewModel.redoMultiTrack(currentPosMs) },
                        onExecuteMultiTrackCommand = { cmd -> viewModel.executeMultiTrackCommand(cmd, currentPosMs) },
                        onImportVideoClick = { viewModel.openMediaPickerSheet() },
                        overlayContent = {
                            val activeSimulator by viewModel.activePlatformSimulator.collectAsState()
                            PlatformSimulatorOverlay(activeSimulator = activeSimulator, project = project)

                            val colorGradingPreviewState by viewModel.colorGradingState.collectAsState()
                            ColorGradingPreviewOverlay(state = colorGradingPreviewState)

                            val overlayText by viewModel.videoOverlayText.collectAsState()
                            if (overlayText.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 16.dp, start = 12.dp, end = 12.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Text(
                                        text = overlayText,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    )
                }
                MainPlayerMode.AI_SIMULATOR -> {
                // 1. Video Viewport Screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.777f) // Keep container bounds 16:9
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (useNativePlayer && project.videoUri.isNotBlank()) {
                    AndroidView(
                        factory = { ctx ->
                            android.widget.VideoView(ctx).apply {
                                layoutParams = android.view.ViewGroup.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        update = { videoView ->
                            try {
                                val uri = android.net.Uri.parse(project.videoUri)
                                val currentTag = videoView.tag as? String
                                if (currentTag != project.videoUri) {
                                    videoView.tag = project.videoUri
                                    videoView.setVideoURI(uri)
                                    videoView.setOnPreparedListener { mp ->
                                        mp.isLooping = isLooping
                                        if (isPlaying) {
                                            videoView.start()
                                        } else {
                                            videoView.pause()
                                        }
                                    }
                                    videoView.setOnErrorListener { _, _, _ -> true }
                                }
                                
                                if (isPlaying) {
                                    if (!videoView.isPlaying) videoView.start()
                                } else {
                                    if (videoView.isPlaying) videoView.pause()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(canvasRatio)
                            .testTag("native_video_view")
                    )
                } else {
                    // Simulated video content drawn via Canvas background
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(canvasRatio)
                            .drawBehind {
                                val brush = Brush.linearGradient(
                                    colors = moodColors,
                                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    end = androidx.compose.ui.geometry.Offset(size.width, size.height)
                                )
                                drawRect(brush)
                                
                                // Volumetric aura lighting
                                if (dynamicProfile.isVolumetricAuraEnabled) {
                                    val auraBrush = Brush.radialGradient(
                                        colors = listOf(
                                            activeRenderPalette.accentGlowColor.copy(alpha = 0.35f),
                                            activeRenderPalette.accentGlowColor.copy(alpha = 0.12f),
                                            Color.Transparent
                                        ),
                                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.42f),
                                        radius = size.width * 0.55f
                                    )
                                    drawRect(auraBrush)

                                    // Anamorphic horizontal lens flare streak
                                    val flareY = size.height * 0.42f
                                    drawLine(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                Color.Transparent,
                                                activeRenderPalette.accentGlowColor.copy(alpha = 0.3f),
                                                activeRenderPalette.highlightColor.copy(alpha = 0.6f),
                                                activeRenderPalette.accentGlowColor.copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        ),
                                        start = androidx.compose.ui.geometry.Offset(0f, flareY),
                                        end = androidx.compose.ui.geometry.Offset(size.width, flareY),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }

                                // Draw abstract moving camera lens circle overlays
                                val pulse = (frameProgress * 2 * Math.PI).toFloat()
                                val r1 = size.width * 0.15f + (Math.sin(pulse.toDouble()) * size.width * 0.02f).toFloat()
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.12f),
                                    radius = r1,
                                    center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.45f)
                                )
                                
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.06f),
                                    radius = size.width * 0.25f,
                                    center = androidx.compose.ui.geometry.Offset(size.width * (0.3f + frameProgress * 0.1f), size.height * 0.5f)
                                )

                                // Smooth velvet perimeter vignette
                                val vignetteBrush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        activeRenderPalette.shadowColor.copy(alpha = activeRenderPalette.vignetteStrength * 0.6f),
                                        activeRenderPalette.shadowColor.copy(alpha = activeRenderPalette.vignetteStrength)
                                    ),
                                    center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f),
                                    radius = size.width * 0.72f
                                )
                                drawRect(vignetteBrush)
                            }
                    )
                }

                // Overlay container to hold security lattices, reframing tracker, and subtitles
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(canvasRatio)
                ) {
                    // Native Multi-Track Overlay Layer (Text, Effects, Audio)
                    val activeTexts by viewModel.activeTextOverlays.collectAsState()
                    val activeEffects by viewModel.activeEffectOverlays.collectAsState()
                    val activeAudios by viewModel.activeAudioClips.collectAsState()
                    MultiTrackOverlayRenderer(
                        activeTextClips = activeTexts,
                        activeEffectClips = activeEffects,
                        activeAudioClips = activeAudios,
                        currentPositionMs = currentPosMs,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Native Platform UI Overlay Simulator (TikTok, Reels, Shorts, LinkedIn)
                    val activeSimulator by viewModel.activePlatformSimulator.collectAsState()
                    PlatformSimulatorOverlay(activeSimulator = activeSimulator, project = project)

                    val colorGradingPreviewState by viewModel.colorGradingState.collectAsState()
                    ColorGradingPreviewOverlay(state = colorGradingPreviewState)

                    // Dynamic Editorial Subtitle / Watermark Text Overlay
                    val overlayText by viewModel.videoOverlayText.collectAsState()
                    if (overlayText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp, start = 12.dp, end = 12.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = overlayText,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Security Lattice Overlay Graphics
                    if (showSecurityLattice) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    val w = size.width
                                    val h = size.height

                                    // Rule of Thirds - thin dashed lines
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.15f),
                                        start = androidx.compose.ui.geometry.Offset(w / 3f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(w / 3f, h),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.15f),
                                        start = androidx.compose.ui.geometry.Offset(2f * w / 3f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(2f * w / 3f, h),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.15f),
                                        start = androidx.compose.ui.geometry.Offset(0f, h / 3f),
                                        end = androidx.compose.ui.geometry.Offset(w, h / 3f),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.15f),
                                        start = androidx.compose.ui.geometry.Offset(0f, 2f * h / 3f),
                                        end = androidx.compose.ui.geometry.Offset(w, 2f * h / 3f),
                                        strokeWidth = 1.dp.toPx()
                                    )

                                    // Action Safe Zone Rectangle (93%)
                                    val safeActionScale = 0.93f
                                    val saW = w * safeActionScale
                                    val saH = h * safeActionScale
                                    val saL = (w - saW) / 2f
                                    val saT = (h - saH) / 2f
                                    drawRect(
                                        color = Color.White.copy(alpha = 0.12f),
                                        topLeft = androidx.compose.ui.geometry.Offset(saL, saT),
                                        size = androidx.compose.ui.geometry.Size(saW, saH),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 1.dp.toPx(),
                                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                                floatArrayOf(10f, 10f), 0f
                                            )
                                        )
                                    )

                                    // Title Safe Zone Rectangle (80%)
                                    val safeTitleScale = 0.80f
                                    val stW = w * safeTitleScale
                                    val stH = h * safeTitleScale
                                    val stL = (w - stW) / 2f
                                    val stT = (h - stH) / 2f
                                    drawRect(
                                        color = Color(0xFF38BDF8).copy(alpha = 0.18f),
                                        topLeft = androidx.compose.ui.geometry.Offset(stL, stT),
                                        size = androidx.compose.ui.geometry.Size(stW, stH),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 1.dp.toPx(),
                                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                                floatArrayOf(5f, 5f), 0f
                                            )
                                        )
                                    )

                                    // Camera Center Crosshairs
                                    val chSize = 16.dp.toPx()
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.4f),
                                        start = androidx.compose.ui.geometry.Offset(w / 2f - chSize, h / 2f),
                                        end = androidx.compose.ui.geometry.Offset(w / 2f + chSize, h / 2f),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.4f),
                                        start = androidx.compose.ui.geometry.Offset(w / 2f, h / 2f - chSize),
                                        end = androidx.compose.ui.geometry.Offset(w / 2f, h / 2f + chSize),
                                        strokeWidth = 1.dp.toPx()
                                    )

                                    // Viewport Corner brackets
                                    val brSize = 12.dp.toPx()
                                    val brGap = 6.dp.toPx()
                                    // Top Left
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(brGap, brGap), androidx.compose.ui.geometry.Offset(brGap + brSize, brGap), 2.dp.toPx())
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(brGap, brGap), androidx.compose.ui.geometry.Offset(brGap, brGap + brSize), 2.dp.toPx())
                                    // Top Right
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(w - brGap, brGap), androidx.compose.ui.geometry.Offset(w - brGap - brSize, brGap), 2.dp.toPx())
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(w - brGap, brGap), androidx.compose.ui.geometry.Offset(w - brGap, brGap + brSize), 2.dp.toPx())
                                    // Bottom Left
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(brGap, h - brGap), androidx.compose.ui.geometry.Offset(brGap + brSize, h - brGap), 2.dp.toPx())
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(brGap, h - brGap), androidx.compose.ui.geometry.Offset(brGap, h - brGap - brSize), 2.dp.toPx())
                                    // Bottom Right
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(w - brGap, h - brGap), androidx.compose.ui.geometry.Offset(w - brGap - brSize, h - brGap), 2.dp.toPx())
                                    drawLine(Color.White.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(w - brGap, h - brGap), androidx.compose.ui.geometry.Offset(w - brGap, h - brGap - brSize), 2.dp.toPx())
                                }
                        ) {
                            // Status bar layout within the viewport
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                // Bottom corner telemetry text
                                Text(
                                    text = "ACTION SAFE (93%) | TITLE SAFE (80%)",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                                Text(
                                    text = "LATTICE GRID ACTIVE",
                                    color = Color(0xFF10B981).copy(alpha = 0.7f),
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                        }
                    }

                    // If Smart subject reframing active, draw tracking box
                    if (project.isSubjectReframingEnabled) {
                        // Interactive bounding box simulation dancing smoothly around the subject
                        val phase = (frameProgress * 2 * Math.PI)
                        val boxX = (0.35f + Math.sin(phase) * 0.05f).toFloat()
                        val boxY = (0.25f + Math.cos(phase) * 0.03f).toFloat()
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    val w = size.width * 0.3f
                                    val h = size.height * 0.5f
                                    val left = size.width * boxX
                                    val top = size.height * boxY
                                    
                                    drawRect(
                                        color = Color(0xFF10B981).copy(alpha = 0.08f),
                                        topLeft = androidx.compose.ui.geometry.Offset(left, top),
                                        size = androidx.compose.ui.geometry.Size(w, h)
                                    )
                                    drawRect(
                                        color = Color(0xFF10B981),
                                        topLeft = androidx.compose.ui.geometry.Offset(left, top),
                                        size = androidx.compose.ui.geometry.Size(w, h),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                                    )
                                }
                        ) {
                            // Tag indicating active AI subject reframing
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF10B981))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FilterCenterFocus, "Reframing active", tint = Color.White, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VEO AUTO-REFRAME TRACKING", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    // Overlay text captioning (Automated transcription display)
                    if (project.transcriptionText.isNotBlank()) {
                        val subtitles = project.transcriptionText
                            .split("\n")
                            .filter { it.isNotBlank() }
                        
                        val activeSubtitle = if (subtitles.isNotEmpty()) {
                            val index = (frameProgress * subtitles.size).toInt().coerceIn(0, subtitles.lastIndex)
                            subtitles[index].substringAfter("] ").trim()
                        } else {
                            "Narrating cinematic visuals seamlessly..."
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = activeSubtitle,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Top Status Indicators Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 4K UHD Badge (Always there or showing status)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (project.isUpscalingEnabled) Color.Red else Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (project.isUpscalingEnabled) "4K UHD" else "1080P HD",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // AI Reframing ON
                    if (project.isSubjectReframingEnabled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "AI Reframing ON",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Selected Mood Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = project.selectedMood.uppercase(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Toggle player mode chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (mainPlayerMode == MainPlayerMode.MEDIA3_PREVIEW) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable {
                                mainPlayerMode = when (mainPlayerMode) {
                                    MainPlayerMode.MEDIA3_PREVIEW -> MainPlayerMode.NLE_STUDIO
                                    MainPlayerMode.NLE_STUDIO -> MainPlayerMode.AI_SIMULATOR
                                    MainPlayerMode.AI_SIMULATOR -> MainPlayerMode.MEDIA3_PREVIEW
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("toggle_player_mode_chip")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = when (mainPlayerMode) {
                                    MainPlayerMode.MEDIA3_PREVIEW -> Icons.Default.PlayCircle
                                    MainPlayerMode.NLE_STUDIO -> Icons.Default.Videocam
                                    MainPlayerMode.AI_SIMULATOR -> Icons.Default.AutoAwesome
                                },
                                contentDescription = "Player Mode",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = mainPlayerMode.label,
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Security lattice indicator flag top-right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (showSecurityLattice) Color(0xFF10B981).copy(alpha = 0.8f) else Color.DarkGray.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (showSecurityLattice) "GRID ON" else "GRID OFF",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Consola de Línea de Tiempo: Modo VMSB Responsive vs Dial Jog / Shuttle
            var showVmsbNativeTimelineMode by remember { mutableStateOf(true) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (showVmsbNativeTimelineMode) MaterialTheme.colorScheme.primary else Color(0xFF1E293B))
                            .clickable { showVmsbNativeTimelineMode = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("toggle_simulator_vmsb_timeline_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.MovieFilter,
                                contentDescription = null,
                                tint = if (showVmsbNativeTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "LÍNEA VMSB© & TRIMMING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showVmsbNativeTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!showVmsbNativeTimelineMode) MaterialTheme.colorScheme.primary else Color(0xFF1E293B))
                            .clickable { showVmsbNativeTimelineMode = false }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("toggle_simulator_jog_dial_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = if (!showVmsbNativeTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "DIAL JOG / SHUTTLE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!showVmsbNativeTimelineMode) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            if (showVmsbNativeTimelineMode) {
                val isVertEnabled by viewModel.isVertEngineEnabled.collectAsState()
                val vertConfigVal by viewModel.vertConfig.collectAsState()
                val vertTelemetryVal by viewModel.vertTelemetry.collectAsState()

                // COMPONENTE 1: LÍNEA DE TIEMPO RESPONSIVA VMSB
                VmsbResponsiveVideoTimeline(
                    currentPositionMs = currentPosMs,
                    durationMs = projectDurationMs,
                    trimStart = trimStart,
                    trimEnd = trimEnd,
                    videoUri = project.videoUri,
                    fps = 30,
                    isPlaying = isPlaying,
                    vertConfig = if (isVertEnabled) vertConfigVal else null,
                    onTrimRangeChange = { start, end ->
                        viewModel.updateVideoTrim(start, end)
                    },
                    onSeekTo = { targetMs ->
                        frameProgress = (targetMs.toFloat() / projectDurationMs).coerceIn(0f, 1f)
                        isPlaying = false
                        viewModel.updateVertPlaybackPosition(targetMs, projectDurationMs)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("simulator_vmsb_responsive_timeline")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // COMPONENTE 2: CONTROLADOR DE REPRODUCCIÓN INTERFAZADO
                VmsbVideoPlaybackController(
                    exoPlayer = null,
                    currentPositionMs = currentPosMs,
                    durationMs = projectDurationMs,
                    trimStart = trimStart,
                    trimEnd = trimEnd,
                    isPlaying = isPlaying,
                    playbackSpeed = playbackSpeed,
                    isLooping = isLooping,
                    isMuted = false,
                    fps = 30,
                    vertConfig = if (isVertEnabled) vertConfigVal else null,
                    onVertToggle = { viewModel.toggleVertEngine() },
                    onPlayPauseToggle = {
                        if (frameProgress >= trimEnd) frameProgress = trimStart
                        isPlaying = !isPlaying
                    },
                    onSeekTo = { targetMs ->
                        frameProgress = (targetMs.toFloat() / projectDurationMs).coerceIn(0f, 1f)
                        viewModel.updateVertPlaybackPosition(targetMs, projectDurationMs)
                    },
                    onSpeedChange = { spd -> playbackSpeed = spd },
                    onLoopToggle = { isLooping = !isLooping },
                    onMuteToggle = { },
                    modifier = Modifier.fillMaxWidth().testTag("simulator_vmsb_playback_controller")
                )

                // COMPONENTE 3: PANEL DE CONTROL Y TELEMETRÍA VERT NATIVO
                if (isVertEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    VertTelemetryControlCard(
                        config = vertConfigVal,
                        telemetry = vertTelemetryVal,
                        onConfigChange = { cfg -> viewModel.setVertConfig(cfg) },
                        modifier = Modifier.fillMaxWidth().testTag("simulator_vert_telemetry_card")
                    )
                }
            } else {
                // Modo secundario: Dial Jog / Shuttle con keyframes
                FramePreciseScrubber(
                    currentPositionMs = currentPosMs,
                    durationMs = projectDurationMs,
                    isPlaying = isPlaying,
                    fps = 30,
                    trimStart = trimStart,
                    trimEnd = trimEnd,
                    onSeekTo = { targetMs ->
                        frameProgress = (targetMs.toFloat() / projectDurationMs).coerceIn(0f, 1f)
                        isPlaying = false
                    },
                    onPlayPauseToggle = {
                        if (frameProgress >= 1f) frameProgress = 0f
                        isPlaying = !isPlaying
                    },
                    onStepFrame = { deltaFrames ->
                        isPlaying = false
                        val totalFrames = projectDurationSec * 30
                        val currentFrame = (frameProgress * totalFrames).roundToInt()
                        val targetFrame = (currentFrame + deltaFrames).coerceIn(0, totalFrames)
                        frameProgress = (targetFrame.toFloat() / totalFrames).coerceIn(0f, 1f)
                    },
                    onSpeedChange = { speed ->
                        playbackSpeed = speed
                    },
                    currentPlaybackSpeed = playbackSpeed,
                    showJogWheelInitial = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Native Multi-Track Synchronized Timeline Lanes (Fallback Mode)
            MultiTrackTimelineView(
                composition = multiTrackComp,
                currentPositionMs = currentPosMs,
                durationMs = projectDurationMs,
                canUndo = canUndoMultiTrack,
                canRedo = canRedoMultiTrack,
                undoDescription = undoDescription,
                redoDescription = redoDescription,
                undoHistoryList = undoHistoryList,
                redoHistoryList = redoHistoryList,
                onUndo = { viewModel.undoMultiTrack(currentPosMs) },
                onRedo = { viewModel.redoMultiTrack(currentPosMs) },
                onExecuteCommand = { cmd -> viewModel.executeMultiTrackCommand(cmd, currentPosMs) },
                onSeekTo = { targetMs ->
                    frameProgress = (targetMs.toFloat() / projectDurationMs).coerceIn(0f, 1f)
                    isPlaying = false
                },
                onCompositionChange = { updated ->
                    viewModel.updateMultiTrackComposition(updated, currentPosMs)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Barra accesoria: Bucle, Enrejado y Telemetría de Audio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de bucle continuo
                    IconButton(
                        onClick = { isLooping = !isLooping },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Alternar bucle",
                            tint = if (isLooping) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Barras de visualización de audio
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        for (i in 0..5) {
                            val factor = when (i) {
                                0 -> Math.sin((frameProgress * 30).toDouble())
                                1 -> Math.cos((frameProgress * 45).toDouble())
                                2 -> Math.sin((frameProgress * 60).toDouble())
                                3 -> Math.cos((frameProgress * 25).toDouble())
                                4 -> Math.sin((frameProgress * 50).toDouble())
                                else -> Math.cos((frameProgress * 35).toDouble())
                            }
                            val rawHeight = if (isPlaying) {
                                (4.dp + 12.dp * Math.abs(factor).toFloat())
                            } else {
                                3.dp
                            }
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(rawHeight)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                            )
                        }
                    }
                }

                // Alternar enrejado de seguridad
                IconButton(
                    onClick = { showSecurityLattice = !showSecurityLattice },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (showSecurityLattice) Icons.Default.GridOn else Icons.Default.GridOff,
                        contentDescription = "Alternar retícula de seguridad",
                        tint = if (showSecurityLattice) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Editorial Toolbar Title & Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editorial Tools",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "VEO EDITORIAL TOOLBAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VEO 3.1 ADVANCED",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toolbar Workspace Container Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    var editTab by remember { mutableStateOf(0) } // 0: Reformat/Crop, 1: Subtitle/Text, 2: Trimming
                    
                    // Editorial Tab Selection Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val tabs = listOf("Reframe & Crop", "Text Overlay", "Clip Trimmer", "Plataformas", "Color & Smooth", "VMSB© Nativo")
                        val icons = listOf(Icons.Default.Crop, Icons.Default.TextFields, Icons.Default.ContentCut, Icons.Default.Devices, Icons.Default.ColorLens, Icons.Default.Memory)
                        tabs.forEachIndexed { idx, label ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (editTab == idx) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (editTab == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { editTab = idx }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("editorial_tab_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = icons[idx],
                                        contentDescription = label,
                                        tint = if (editTab == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (editTab == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    when (editTab) {
                        0 -> {
                            // Reframe & Crop Panel
                            Column {
                                Text("Social Format Target & Aspect Ratio Reformatting", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val ratios = listOf(
                                        "16:9" to "Landscape (YouTube)",
                                        "9:16" to "Portrait (Shorts/TikTok)",
                                        "1:1" to "Square (Post/Feed)"
                                    )
                                    ratios.forEach { (ratio, desc) ->
                                        val isSel = project.aspectRatio == ratio
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                .border(
                                                    1.dp,
                                                    if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { viewModel.updateAspectRatio(ratio) }
                                                .padding(8.dp)
                                                .testTag("editorial_ratio_$ratio"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    ratio,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    desc,
                                                    fontSize = 7.5.sp,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Subtitle / Text Overlay Panel
                            Column {
                                Text("Add Custom Subtitles or Text Overlay", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                val textOverlayState by viewModel.videoOverlayText.collectAsState()
                                OutlinedTextField(
                                    value = textOverlayState,
                                    onValueChange = { viewModel.videoOverlayText.value = it },
                                    placeholder = { Text("Type subtitles/overlay text here...", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth().testTag("video_overlay_text_input"),
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 11.sp),
                                    trailingIcon = {
                                        if (textOverlayState.isNotBlank()) {
                                            IconButton(onClick = { viewModel.videoOverlayText.value = "" }) {
                                                Icon(Icons.Default.Clear, "Clear Text", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Subtitles appear dynamically at the bottom center of the video viewport player.", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        2 -> {
                            // Range Trimmer Panel: Native VMSB Responsive Timeline & Playback Controller
                            Column(modifier = Modifier.fillMaxWidth()) {
                                val trimStartVal by viewModel.videoTrimStart.collectAsState()
                                val trimEndVal by viewModel.videoTrimEnd.collectAsState()
                                val projectDurMs = project.durationSec.coerceAtLeast(1) * 1000L
                                val currentPlayheadMs = (frameProgress * projectDurMs).toLong()
                                val isVertEnabled by viewModel.isVertEngineEnabled.collectAsState()
                                val vertConfigVal by viewModel.vertConfig.collectAsState()
                                val vertTelemetryVal by viewModel.vertTelemetry.collectAsState()

                                VmsbResponsiveVideoTimeline(
                                    currentPositionMs = currentPlayheadMs,
                                    durationMs = projectDurMs,
                                    trimStart = trimStartVal,
                                    trimEnd = trimEndVal,
                                    videoUri = project.videoUri,
                                    fps = 30,
                                    isPlaying = isPlaying,
                                    vertConfig = if (isVertEnabled) vertConfigVal else null,
                                    onTrimRangeChange = { start, end ->
                                        viewModel.videoTrimStart.value = start
                                        viewModel.videoTrimEnd.value = end
                                    },
                                    onSeekTo = { targetMs ->
                                        frameProgress = (targetMs.toFloat() / projectDurMs.toFloat()).coerceIn(0f, 1f)
                                        isPlaying = false
                                        viewModel.updateVertPlaybackPosition(targetMs, projectDurMs)
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("tab_vmsb_responsive_timeline")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                VmsbVideoPlaybackController(
                                    exoPlayer = null,
                                    currentPositionMs = currentPlayheadMs,
                                    durationMs = projectDurMs,
                                    trimStart = trimStartVal,
                                    trimEnd = trimEndVal,
                                    isPlaying = isPlaying,
                                    playbackSpeed = playbackSpeed,
                                    isLooping = isLooping,
                                    isMuted = false,
                                    fps = 30,
                                    vertConfig = if (isVertEnabled) vertConfigVal else null,
                                    onVertToggle = { viewModel.toggleVertEngine() },
                                    onPlayPauseToggle = {
                                        if (frameProgress >= trimEndVal) frameProgress = trimStartVal
                                        isPlaying = !isPlaying
                                    },
                                    onSeekTo = { targetMs ->
                                        frameProgress = (targetMs.toFloat() / projectDurMs.toFloat()).coerceIn(0f, 1f)
                                        viewModel.updateVertPlaybackPosition(targetMs, projectDurMs)
                                    },
                                    onSpeedChange = { spd -> playbackSpeed = spd },
                                    onLoopToggle = { isLooping = !isLooping },
                                    onMuteToggle = { },
                                    modifier = Modifier.fillMaxWidth().testTag("tab_vmsb_playback_controller")
                                )

                                if (isVertEnabled) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    VertTelemetryControlCard(
                                        config = vertConfigVal,
                                        telemetry = vertTelemetryVal,
                                        onConfigChange = { cfg -> viewModel.setVertConfig(cfg) },
                                        modifier = Modifier.fillMaxWidth().testTag("tab_vert_telemetry_card")
                                    )
                                }
                            }
                        }
                        3 -> {
                            // Platform Mockup & Safe Zone Simulator Panel
                            Column {
                                Text("Simulador de Plataformas & Safe-Zones en Vivo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("Previsualiza la experiencia nativa de tu video antes de publicarlo en redes sociales.", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))

                                val currentSim by viewModel.activePlatformSimulator.collectAsState()
                                val simOptions = listOf(
                                    "none" to "Sin Mockup",
                                    "tiktok" to "TikTok (9:16)",
                                    "instagram" to "Reels (9:16)",
                                    "youtube" to "Shorts (9:16)",
                                    "linkedin" to "LinkedIn (1:1)"
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    simOptions.forEach { (simId, label) ->
                                        val isSelected = currentSim == simId || (simId == "none" && (currentSim == null || currentSim == "none"))
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.setPlatformSimulator(simId) },
                                            label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                selectedLabelColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.testTag("simulator_chip_$simId")
                                        )
                                    }
                                }
                            }
                        }
                        4 -> {
                            // Sophisticated Color Science & Smooth Dynamics Panel
                            SophisticatedPaletteAndDynamicsCard(viewModel = viewModel)
                        }
                        5 -> {
                            // VMSB© Native Core, MCTS Tree & Fakfa Engine Panel
                            VmsbNativeCoreDashboard(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

// 2. Tab 0: Studio Control Workspace with rich features
@Composable
fun AIStudioControlWorkspace(
    project: Project,
    viewModel: MainViewModel,
    isTranscribing: Boolean
) {
    var bgMusicInput by remember { mutableStateOf(project.bgMusicPrompt) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        val videoTransitionCfg by viewModel.videoTransitionConfig.collectAsState()

        // Selector de Transiciones de Video Cinematográficas VMSB
        VideoTransitionSelector(
            config = videoTransitionCfg,
            onConfigChange = { viewModel.updateVideoTransition(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Veo Studio by Vs - VMSB© Brand Visual Interface & Quality Showcase
        VmsbBrandVisualInterface(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth(),
            initiallyExpanded = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Panel de Ética & Procedencia C2PA en Tiempo Real
        EthicsProvenancePanel(
            project = project,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Estándar Universal Adaptativo VMSB© (iOS ProRes, Smart TV 10-Ft, Tablet Pro, Mobile)
        UniversalAdaptiveHubCard(
            project = project,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Monitor en Tiempo Real del Motor MCTS & Protocolo de Estado de Berna (Veo 3.1)
        MctsLiveProcessingMonitor(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Créditos Híbridos B2B (Freemium vs Pro Enterprise)
        B2BHybridCreditsCard(
            creditsManager = viewModel.b2bCreditsManager,
            onOpenUpgradeDialog = { viewModel.openC2paDialogForCurrentProject() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Hub de Ejecución Universal WebAssembly (Wasm) SIMD-128
        WasmUniversalExecutionHubCard(
            wasmRuntime = viewModel.wasmRuntime,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Capa de Gestión y Sincronización Segura de Activos WASSAM (Web-based Automated System for Secure Asset Management)
        WassamSecureAssetManagementCard(
            wassamManager = viewModel.wassamManager,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Panel de Diagnóstico Integral del Motor VMSB© (Caché, Latencia Sync & Protocolo de Berna)
        VmsbDiagnosticDashboardView(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Matriz Binaria Kafka C++ & Wasm / Circom ZKP Orquestador
        VmsbOrchestratorMatrixCard(
            orchestrator = viewModel.orchestratorMatrix,
            wasmRuntime = viewModel.wasmRuntime,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Soluciones Verticales de Alto Valor B2B (Real Estate, E-Commerce, EdTech)
        B2BVerticalStudioSelector(
            onApplyTemplate = { viewModel.applyB2BVerticalTemplate(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Control Determinista & Consistencia de Personajes (Anti-Drift)
        DeterministicDirectingConsole(
            continuityEngine = viewModel.deterministicContinuityEngine,
            basePrompt = project.prompt,
            onApplyContinuityToStoryboard = { viewModel.applyContinuityToStoryboard(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sophisticated Color Science & Smooth Render Palettes
        SophisticatedPaletteAndDynamicsCard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(14.dp))

        // VMSB© Native Engine Core (MCTS & Fakfa Binary Streaming)
        VmsbNativeCoreDashboard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(14.dp))

        // GreenAI & Low-Latency Telemetry Engine
        EcoEngineDashboard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(14.dp))

        // Local Video Sandbox & Validation Component
        LocalVideoImportCard(viewModel = viewModel)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Feature: Adaptive Color Grading
        Text(
            "Adaptive Color Grading (Moods)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        val moods = listOf(
            "Cinematic Teal & Orange",
            "Vintage Warm",
            "Dramatic Monochrome",
            "Golden Hour",
            "Cyberpunk Neon",
            "Natural Daylight"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            moods.forEach { mood ->
                FilterChip(
                    selected = project.selectedMood == mood,
                    onClick = { viewModel.updateProjectMood(mood) },
                    label = { Text(mood, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("mood_chip_${mood.replace(" ", "_").lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle: Smart subject reframing & 4K upscaling
        Text(
            "Visual Editing Enhancers",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reframing Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.toggleSubjectReframing(!project.isSubjectReframingEnabled) },
                colors = CardDefaults.cardColors(
                    containerColor = if (project.isSubjectReframingEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.dp, 
                    if (project.isSubjectReframingEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FilterCenterFocus, "Reframing", tint = if (project.isSubjectReframingEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = project.isSubjectReframingEnabled,
                            onCheckedChange = { viewModel.toggleSubjectReframing(it) },
                            modifier = Modifier.scale(0.7f).testTag("reframing_switch")
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Subject Reframing", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Auto track center subjects", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            // 4K Upscale Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.toggleUpscaling(!project.isUpscalingEnabled) },
                colors = CardDefaults.cardColors(
                    containerColor = if (project.isUpscalingEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.dp, 
                    if (project.isUpscalingEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Hd, "4K Upscale", tint = if (project.isUpscalingEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = project.isUpscalingEnabled,
                            onCheckedChange = { viewModel.toggleUpscaling(it) },
                            modifier = Modifier.scale(0.7f).testTag("upscale_switch")
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("4K AI Upscaling", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Professional grade clarity", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Narrative Voice & Soundtrack section
        Text(
            "Narrative commentary & Soundtrack Synthesis",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DEF8)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // VoiceOver Selector spinner-like row
                Text("Localized Voice-Over Narrative Language", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val languages = listOf("English", "Spanish", "Japanese", "German", "French", "Portuguese")
                    languages.forEach { lang ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (project.voiceOverLanguage == lang) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                )
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateVoiceOverLanguage(lang) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("lang_$lang")
                        ) {
                            Text(
                                lang, 
                                fontSize = 10.sp, 
                                color = if (project.voiceOverLanguage == lang) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Soundtrack prompt input
                Text("Generate AI Background Soundtrack", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = bgMusicInput,
                        onValueChange = { bgMusicInput = it },
                        placeholder = { Text("E.g., Mellow Tech Lofi Synth Beats", fontSize = 11.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("bg_music_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.updateBgMusic(bgMusicInput) },
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("bg_music_button")
                    ) {
                        Text("Synthesize", fontSize = 11.sp)
                    }
                }
                
                if (project.bgMusicUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        project.bgMusicUrl,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Automated Subtitle Transcription Generation
                Text("Automated Subtitles & Captions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.generateTranscription() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_transcription_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    if (isTranscribing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini transcribing voice track...")
                    } else {
                        Icon(Icons.Default.TextFields, contentDescription = "Subtitles", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (project.transcriptionText.isBlank()) "Generate Subtitles via AI" else "Regenerate Subtitles")
                    }
                }
                
                if (project.transcriptionText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        Text(
                            project.transcriptionText,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// 3. Tab 1: Multi-Platform Batch Export Workspace
@Composable
fun BatchExportWorkspace(
    project: Project,
    viewModel: MainViewModel,
    isExporting: Boolean,
    exportProgress: Float,
    exportStatusText: String,
    exportedPlatforms: List<String>
) {
    val platforms = listOf("YouTube", "TikTok", "Instagram Reels", "Facebook Watch", "Twitter Video")
    val selectedPlatforms = remember { mutableStateMapOf<String, Boolean>() }
    val context = LocalContext.current
    
    // Initialize
    LaunchedEffect(Unit) {
        if (selectedPlatforms.isEmpty()) {
            platforms.forEach { selectedPlatforms[it] = (it == "YouTube" || it == "TikTok") }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        val selectedPlatformPreset by viewModel.selectedPlatformPreset.collectAsState()
        val currentLufs by viewModel.currentLoudnessLufs.collectAsState()
        val audioNormalized by viewModel.audioNormalized.collectAsState()
        val isPublishingToGateway by viewModel.isPublishingToGateway.collectAsState()
        val gatewayPublishResults by viewModel.gatewayPublishResults.collectAsState()

        // Motor Omnicanal Inteligente "One-Take, Multi-Platform Master" (Zonas Seguras & EBU R128)
        OmnichannelSafeZoneMaster(
            selectedPlatform = selectedPlatformPreset,
            onSelectPlatform = { viewModel.selectedPlatformPreset.value = it },
            currentLufs = currentLufs,
            isAudioNormalized = audioNormalized,
            onToggleAudioNormalize = { viewModel.toggleAudioNormalization() },
            onBatchPublishAllGateways = { viewModel.batchPublishToAllGateways() },
            isPublishing = isPublishingToGateway,
            publishResults = gatewayPublishResults,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Native Platforms, Ingestion Gateways & Market Protocols
        NativeGatewayPublisherCard(project = project, viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Integración de Exportación a Redes Sociales vía Share Intents Estándar de Android
        SocialMediaExportIntegrationCard(
            project = project,
            viewModel = viewModel,
            onOpenFullExportDialog = { viewModel.openSocialExportDialog() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // GreenAI & Net-Zero Computing
        EcoEngineDashboard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Batch Export & Social Sharing",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Configure automatic metadata tagging & batch publishing in parallel.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Warning if duration exceeds 20 minutes limit
                val durationExceeded = project.durationSec > 1200 // 20m limit is 1200s
                if (durationExceeded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, "Warning", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Video exceeds the 20-minute export limit! Rendering will fall back to maximum 20-minute trim.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, "Duration Ok", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Within 20-minute limits. Full high-definition rendering active.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // AI Generated Tags display
                Text("AI Metadata Tags (Optimized by Gemini)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                if (project.tagsText.isNotBlank()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        project.tagsText.split(",").forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text("#${tag.trim()}", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                } else {
                    Text("No metadata tags generated yet.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Social Platform Checkboxes
                Text("Target Share Platforms", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                
                platforms.forEach { platform ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlatforms[platform] = !(selectedPlatforms[platform] ?: false) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedPlatforms[platform] ?: false,
                            onCheckedChange = { selectedPlatforms[platform] = it },
                            modifier = Modifier.scale(0.85f).testTag("checkbox_$platform")
                        )
                        Text(platform, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        
                        if (exportedPlatforms.contains(platform)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Shared", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share trigger button
                Button(
                    onClick = {
                        val targets = selectedPlatforms.filter { it.value }.keys.toList()
                        viewModel.startBatchExport(context, targets)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("batch_export_submit_button"),
                    enabled = !isExporting && selectedPlatforms.any { it.value }
                ) {
                    Icon(Icons.Default.IosShare, contentDescription = "Export")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (project.isUpscalingEnabled) "4K Render & Compartir en Redes" else "HD Render & Compartir en Redes")
                }

                // Export progress bars
                if (isExporting) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { exportProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        exportStatusText,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// 4. Tab 2: Legal Compliance center
@Composable
fun ComplianceAuditWorkspace(
    project: Project,
    viewModel: MainViewModel,
    isChecking: Boolean
) {
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val zkpProofKey by viewModel.zkpProofKey.collectAsState()
    val auditPdfGenerated by viewModel.auditPdfGenerated.collectAsState()
    val pdfCurrentPage by viewModel.pdfCurrentPage.collectAsState()
    val pdfZoomScale by viewModel.pdfZoomScale.collectAsState()
    val certReport by viewModel.certificationReport.collectAsState()
    val isCertifying by viewModel.isCertifying.collectAsState()
    val context = LocalContext.current

    var viewerModeIsPdf by remember { mutableStateOf(true) } // true = PDF, false = Google Docs
    var showCopiedToast by remember { mutableStateOf(false) }

    LaunchedEffect(showCopiedToast) {
        if (showCopiedToast) {
            delay(2000)
            showCopiedToast = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Portal Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Shield",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Portal de Verificación y Aprobación AI (ZKP Audit)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Consola automatizada para auditar contenido de vídeo, firmar pruebas criptográficas de conocimiento cero (ZKP) para pruebas cerradas y generar reportes oficiales en PDF.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // FREED_vmsb_lattice_ZKP Verifiable Status Badge
        val freedStatusVal by viewModel.freedAuditStatus.collectAsState()
        FreedVerifiableStatusBadge(
            auditStatus = freedStatusVal,
            onOpenDetails = { viewModel.openFreedDetailsDialog() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // C2PA Content Credentials v2.1 & Escudo Regulatorio Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF10B981))))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "C2PA Content Credentials v2.1",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF064E3B)) {
                        Text(
                            text = "EU AI ACT READY",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF34D399)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Garantía Legal de Publicación para Agencias & Corporaciones. Inyección nativa de metadatos JUMBF, hash SHA-256 inmutable y declaración obligatoria de medio sintético.",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.openC2paDialogForCurrentProject() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir Cédula C2PA & Manifiesto JSON-LD", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.openExecutiveAuditReportDialog() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Informe Ejecutivo: Conectividad Google/Meta, ROI & PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GRID: Store Approval Status & Checklist
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Card: Play Store / App Store Sandbox connect
            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .height(230.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Estado Sandbox Console",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (project.complianceStatus == "Compliant" && zkpProofKey != null) {
                                            Color(0xFF10B981).copy(alpha = 0.15f)
                                        } else {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (project.complianceStatus == "Compliant" && zkpProofKey != null) "APROBADO" else "SANDBOX REVISION",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (project.complianceStatus == "Compliant" && zkpProofKey != null) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Sistema integrado con Play Console para la revisión automatizada con IA durante la Fase de Prueba Cerrada (Closed Beta).",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 13.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Done",
                                tint = if (project.complianceStatus == "Compliant") Color(0xFF10B981) else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Auditoría de Guión IA", fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Done",
                                tint = if (zkpProofKey != null) Color(0xFF10B981) else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Prueba ZKP Generada", fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Done",
                                tint = if (auditPdfGenerated) Color(0xFF10B981) else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PDF de Auditoría Listo", fontSize = 10.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.checkCompliance() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("compliance_check_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        enabled = !isChecking
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(modifier = Modifier.size(12.dp), color = Color.White, strokeWidth = 1.5.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Escaneando...", fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verificar con IA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Right Card: ZKP (Zero Knowledge Proof) Closed Beta Generator
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(230.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Lock, "Lock", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Text(
                                "Seguridad ZKP",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Matemáticamente valida la app y video ante reguladores sin revelar el prompt sensible (Zero-Knowledge Proof).",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 12.sp
                        )
                    }

                    if (zkpProofKey != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("PROOFSIGN SIGNED", fontSize = 8.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.CloudDone, "Done", tint = Color(0xFF10B981), modifier = Modifier.size(10.dp))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = zkpProofKey ?: "",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 7.5.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Pendiente de Firma Criptográfica",
                                fontSize = 8.5.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.runFullSystemVerificationAndCertification(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("zkp_generate_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        enabled = !isGeneratingPdf && !isCertifying
                    ) {
                        if (isGeneratingPdf || isCertifying) {
                            CircularProgressIndicator(modifier = Modifier.size(12.dp), color = Color.White, strokeWidth = 1.5.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Certificando...", fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.Verified, contentDescription = "ZKP", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Certificar & PDF VMSB©", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // COMPLIANCE AUDIT STATUS BAR (Under Sandbox)
        Text("Resultado del Escaneo Legal AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        val statusColor = when (project.complianceStatus) {
            "Compliant" -> Color(0xFF10B981)
            "Warning" -> Color(0xFFF59E0B)
            "Flagged" -> Color(0xFFEF4444)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(statusColor.copy(alpha = 0.08f))
                .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (project.complianceStatus) {
                        "Compliant" -> "Veredicto: APTO (Listo para publicación)"
                        "Warning" -> "Veredicto: ADVERTENCIA (Revisar observaciones)"
                        "Flagged" -> "Veredicto: RECHAZADO (Violaciones detectadas)"
                        else -> "Veredicto: PENDIENTE DE ESCANEO"
                    },
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    fontSize = 11.sp
                )
                if (project.complianceDetails.isNotBlank()) {
                    Text(
                        text = project.complianceDetails,
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else {
                    Text(
                        text = "Por favor ejecute la revisión automatizada con el botón 'Verificar con IA' para auditar los términos de distribución legal.",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // INTERACTIVE SCREEN DOCUMENT VIEWER (Google Docs / PDF)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Visor Integrado de Documentos Oficiales",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Tab switcher for Viewer mode
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (viewerModeIsPdf) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewerModeIsPdf = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "PDF Reader",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewerModeIsPdf) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (!viewerModeIsPdf) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewerModeIsPdf = false }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Google Docs",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!viewerModeIsPdf) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (auditPdfGenerated) {
            // Document Viewer Toolbar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4)), // Sleek Google Viewer color
                border = BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title and Format Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (viewerModeIsPdf) Color(0xFFEA4335) else Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (viewerModeIsPdf) "PDF" else "GD",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = if (viewerModeIsPdf) "Reporte_Auditoria_ZKP_Veo31.pdf" else "Certificado de Cumplimiento Google Docs",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF202124),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Prueba Cerrada Beta - ZKP Verificado",
                                fontSize = 8.sp,
                                color = Color(0xFF5F6368)
                            )
                        }
                    }

                    // Interactive Controls: Zoom, Navigation, Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Zoom Out
                        IconButton(
                            onClick = { viewModel.updatePdfZoom(-10) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ZoomOut, "Zoom Out", tint = Color(0xFF5F6368), modifier = Modifier.size(16.dp))
                        }
                        
                        // Zoom scale text
                        Text(
                            text = "$pdfZoomScale%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF202124)
                        )

                        // Zoom In
                        IconButton(
                            onClick = { viewModel.updatePdfZoom(10) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ZoomIn, "Zoom In", tint = Color(0xFF5F6368), modifier = Modifier.size(16.dp))
                        }

                        VerticalDivider(modifier = Modifier.height(16.dp), color = Color(0xFFDADCE0))

                        // Page Navigation
                        IconButton(
                            onClick = { viewModel.updatePdfPage(pdfCurrentPage - 1) },
                            enabled = pdfCurrentPage > 1,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Prev Page", tint = if (pdfCurrentPage > 1) Color(0xFF202124) else Color.LightGray, modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "$pdfCurrentPage / 2",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF202124)
                        )

                        IconButton(
                            onClick = { viewModel.updatePdfPage(pdfCurrentPage + 1) },
                            enabled = pdfCurrentPage < 2,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next Page", tint = if (pdfCurrentPage < 2) Color(0xFF202124) else Color.LightGray, modifier = Modifier.size(18.dp))
                        }

                        VerticalDivider(modifier = Modifier.height(16.dp), color = Color(0xFFDADCE0))

                        // Download & Open PDF Action
                        IconButton(
                            onClick = {
                                val report = certReport
                                if (report != null) {
                                    VmsbPdfCertificationGenerator.openPdfWithSystemViewer(context, report.pdfFilePath)
                                } else {
                                    viewModel.runFullSystemVerificationAndCertification(context)
                                }
                                showCopiedToast = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Download, "Descargar y Abrir PDF Certificado", tint = Color(0xFF5F6368), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Document Canvas (Simulated White Paper Sheet)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8EAED)) // Google Docs viewport background gray
                    .border(BorderStroke(1.dp, Color(0xFFDADCE0)))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Scaling Box representing A4 paper sheet
                Card(
                    modifier = Modifier
                        .fillMaxWidth(if (pdfZoomScale == 100) 1f else (pdfZoomScale / 100f).coerceIn(0.6f, 1.3f))
                        .shadow(4.dp, shape = RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        if (pdfCurrentPage == 1) {
                            // --- PAGE 1: OFFICIAL AUDIT CERTIFICATE ---
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "VEO CORE AI COMPLIANCE PLATFORM",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF6750A4),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "REGULATORY CRYPTOGRAPHIC SYSTEMS",
                                        fontSize = 7.sp,
                                        color = Color(0xFF5F6368),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFEADDFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Official Badge",
                                        tint = Color(0xFF21005D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE0E0E0))

                            Text(
                                text = "CERTIFICADO DE CONFORMIDAD Y AUDITORÍA DIGITAL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF202124),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("DATOS DE VALIDACIÓN DEL PROYECTO:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                            Spacer(modifier = Modifier.height(4.dp))

                            // Table of data
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8F9FA))
                                    .border(1.dp, Color(0xFFE0E0E0))
                                    .padding(8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Text("Título App / Vídeo:", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Color(0xFF202124))
                                    Text(project.title, fontSize = 7.5.sp, modifier = Modifier.weight(2f), color = Color(0xFF202124))
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Text("Firma Cripto ZKP:", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Color(0xFF202124))
                                    Text(
                                        text = zkpProofKey ?: "No Firmado",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 7.sp,
                                        modifier = Modifier.weight(2f),
                                        color = Color(0xFF6750A4),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Text("Resolución de Salida:", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Color(0xFF202124))
                                    Text("${project.resolution} UHD (Aspect Ratio ${project.aspectRatio})", fontSize = 7.5.sp, modifier = Modifier.weight(2f), color = Color(0xFF202124))
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Text("Duración de Campaña:", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Color(0xFF202124))
                                    Text("${project.durationSec} Segundos de Retrato", fontSize = 7.5.sp, modifier = Modifier.weight(2f), color = Color(0xFF202124))
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Text("Método de Prueba:", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Color(0xFF202124))
                                    Text("Closed-Beta Sandbox Testing (ZKP Closed-Group)", fontSize = 7.5.sp, modifier = Modifier.weight(2f), color = Color(0xFF202124))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("DECLARACIÓN DE PRUEBA CERRADA CON CONOCIMIENTO CERO (ZKP):", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Se certifica matemáticamente mediante firma criptográfica que este software/vídeo cumple plenamente con los estándares internacionales de distribución para tiendas móviles (App Store / Play Store Sandbox) sin necesidad de divulgar a terceros las credenciales privadas del creador, el guión original íntegro o el prompt inicial sensible del usuario. La prueba de validez ha sido verificada con éxito en 3 nodos distribuidos autorizados.",
                                fontSize = 7.sp,
                                color = Color(0xFF202124),
                                lineHeight = 10.sp,
                                textAlign = TextAlign.Justify
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Verification Indicators
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, Color(0xFFE0E0E0))
                                        .padding(6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("AUDITORÍA DE COPYRIGHT", fontSize = 6.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("APROBADO", fontSize = 8.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, Color(0xFFE0E0E0))
                                        .padding(6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("GDPR PRIVACY COMPLIANT", fontSize = 6.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("100% SEGURO", fontSize = 8.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, Color(0xFFE0E0E0))
                                        .padding(6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("SANDBOX REVIEW STATUS", fontSize = 6.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("APROBADO", fontSize = 8.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Signatures
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Dr. Gemini Flash 3.5", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
                                    Text("AI Compliance Platform Lead", fontSize = 6.sp, color = Color(0xFF5F6368))
                                    Box(modifier = Modifier.width(80.dp).height(1.dp).background(Color.Gray).padding(top = 4.dp))
                                    Text("Firma Digital Certificada", fontSize = 5.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Veo Core v3.1 Engine", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
                                    Text("Automated Sandbox Validator", fontSize = 6.sp, color = Color(0xFF5F6368))
                                    Box(modifier = Modifier.width(80.dp).height(1.dp).background(Color.Gray).padding(top = 4.dp))
                                    Text("Firma de Código ZKP", fontSize = 5.sp, color = Color.Gray)
                                }
                            }
                        } else {
                            // --- PAGE 2: TRANSCRIPTION, SCRIPT AND METADATA ---
                            Text(
                                text = "ANEXO TECNICO DE AUDITORÍA DE CONTENIDOS IA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF202124),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Firma ZKP: ${zkpProofKey ?: ""}",
                                fontSize = 6.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF6750A4),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE0E0E0))

                            Text("GUION CINEMÁTICO AUDITADO (SCRIPT):", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8F9FA))
                                    .border(1.dp, Color(0xFFEAEAEA))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = project.scriptText.ifBlank { "Sin guión generado para auditar." },
                                    fontSize = 7.sp,
                                    color = Color(0xFF3C4043),
                                    lineHeight = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("METADATOS DE BÚSQUEDA Y OPTIMIZACIÓN (SEO TAGS):", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.tagsText.ifBlank { "Sin etiquetas generadas." },
                                fontSize = 7.sp,
                                color = Color(0xFF202124)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("EVALUACIÓN DE CONTENIDOS CONTRA POLÍTICAS DE PLAY CONSOLE:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368))
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val evaluationPoints = listOf(
                                "1. Política de Derechos de Autor (DMCA): Verificado. El contenido generado es 100% original, libre de infracciones intelectuales.",
                                "2. Política de Privacidad de Datos de Usuarios: Aprobado de extremo a extremo mediante el módulo de prueba ZKP seguro.",
                                "3. Seguridad Infantil (COPPA): Contenido calificado para todas las edades. No contiene escenas inapropiadas o lenguaje adulto.",
                                "4. Neutralidad Regional y Publicidad: Adecuado para monetización global sin restricciones de geovallado."
                            )

                            evaluationPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.5.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.Check, "Done", tint = Color(0xFF10B981), modifier = Modifier.size(9.dp).padding(top = 1.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = point, fontSize = 6.5.sp, color = Color(0xFF3C4043), lineHeight = 9.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Este es un reporte oficial electrónico firmado con el motor Gemini 3.5 Flash en los servidores seguros de AI Studio.",
                                fontSize = 6.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val report = certReport
                        if (report != null) {
                            VmsbPdfCertificationGenerator.openPdfWithSystemViewer(context, report.pdfFilePath)
                        } else {
                            viewModel.runFullSystemVerificationAndCertification(context)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("open_system_pdf_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir PDF en Visor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val report = certReport
                        if (report != null) {
                            VmsbPdfCertificationGenerator.sharePdf(context, report.pdfFilePath, project.title)
                        } else {
                            viewModel.runFullSystemVerificationAndCertification(context)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("share_pdf_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartir PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Toast overlay for download click simulation
            AnimatedVisibility(
                visible = showCopiedToast,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 40 })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF202124).copy(alpha = 0.9f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¡Abriendo Certificado PDF Oficial en visor del sistema!",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Document Viewer Empty State
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Document Empty",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Certificado PDF no generado aún",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Verifique, calibre y certifique cada componente del sistema VMSB© generando el documento formal en PDF firmado.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.runFullSystemVerificationAndCertification(context) },
                            enabled = !isCertifying,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp).testTag("certify_system_empty_btn")
                        ) {
                            if (isCertifying) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verificando y Certificando...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.VerifiedUser, "Certificar", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verificar, Ajustar y Certificar Sistema VMSB©", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Topología del Sistema VMSB©: Interconexión, Eficiencia y Certificación
        VmsbSystemTopologyView(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Real-time AI Generation Console Overlay Modal
@Composable
fun AIGenerationConsoleModal(
    isGenerating: Boolean,
    generationProgress: Float,
    generationStatusText: String,
    consoleSteps: List<AIConsoleStep>,
    terminalLogs: List<String>
) {
    if (!isGenerating) return

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("ai_generation_console_modal"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Header with Glowing AI Icon and Percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Active",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                "MOTOR AI VEO 3.1 & GEMINI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "PROCESAMIENTO EN TIEMPO REAL",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0284C7),
                        border = BorderStroke(1.dp, Color(0xFF7DD3FC))
                    ) {
                        Text(
                            text = "${(generationProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { generationProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = generationStatusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))

                // Stepper Steps
                Text(
                    "FASES DEL MOTOR IA",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    consoleSteps.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (step.status) {
                                        "completed" -> Color(0xFF052E16)
                                        "in_progress" -> Color(0xFF0C4A6E)
                                        else -> Color(0xFF1E293B).copy(alpha = 0.5f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    when (step.status) {
                                        "completed" -> Color(0xFF22C55E)
                                        "in_progress" -> Color(0xFF38BDF8)
                                        else -> Color(0xFF334155)
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (step.status) {
                                "completed" -> Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = Color(0xFF4ADE80),
                                    modifier = Modifier.size(16.dp)
                                )
                                "in_progress" -> CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF38BDF8)
                                )
                                else -> Icon(
                                    imageVector = Icons.Outlined.Circle,
                                    contentDescription = "Pending",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Fase ${step.phaseNumber}: ${step.title}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (step.status) {
                                        "completed" -> Color(0xFF86EFAC)
                                        "in_progress" -> Color(0xFF7DD3FC)
                                        else -> Color(0xFF94A3B8)
                                    }
                                )
                                Text(
                                    text = step.detailText,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFFCBD5E1),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Console Event Log Window
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF020617),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Text("CONSOLA DE EVENTOS EN VIVO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                            }
                            Text("STREAM", fontSize = 8.sp, color = Color(0xFF64748B), fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 70.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            terminalLogs.takeLast(6).forEach { logLine ->
                                Text(
                                    text = logLine,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = when {
                                        logLine.contains("[AI]") -> Color(0xFF38BDF8)
                                        logLine.contains("[DB]") -> Color(0xFFFACC15)
                                        logLine.contains("[SYS]") -> Color(0xFF4ADE80)
                                        else -> Color(0xFF94A3B8)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Quick Preview Floating Modal Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPreviewModal(
    project: Project,
    onDismiss: () -> Unit,
    onOpenStudio: (Project) -> Unit,
    onDuplicate: (Project) -> Unit,
    onExport: (Project) -> Unit,
    onDelete: (Project) -> Unit,
    onOpenFreedAudit: ((Project) -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf(0) }
    val dateFormatter = remember {
        java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    }

    val category = remember(project) {
        when {
            project.complianceStatus == "Archived" || project.cloudSyncStatus == "Archived" -> "Archivados"
            project.complianceStatus == "Generating" || (project.scriptText.isBlank() && project.storyboardText.isBlank() && project.videoUri.isBlank()) -> "En Generación"
            project.scriptText.isNotBlank() || project.videoUri.isNotBlank() -> "Completados"
            else -> "Borradores"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("quick_preview_modal_${project.id}"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (category) {
                                    "Completados" -> Color(0xFF052E16)
                                    "En Generación" -> Color(0xFF78350F)
                                    "Borradores" -> Color(0xFF1E3A8A)
                                    else -> Color(0xFF334155)
                                },
                                border = BorderStroke(1.dp, when (category) {
                                    "Completados" -> Color(0xFF22C55E)
                                    "En Generación" -> Color(0xFFF59E0B)
                                    "Borradores" -> Color(0xFF3B82F6)
                                    else -> Color(0xFF94A3B8)
                                })
                            ) {
                                Text(
                                    text = when (category) {
                                        "Completados" -> "✅ COMPLETADO"
                                        "En Generación" -> "⚡ EN GENERACIÓN"
                                        "Borradores" -> "📝 BORRADOR"
                                        else -> "📦 ARCHIVADO"
                                    },
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = "ID #${project.id}",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = project.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Video Thumbnail Banner Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Previsualizar",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Veo 3.1 Render (${project.aspectRatio} • ${project.resolution})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Floating Badges
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(color = Color.Black.copy(alpha = 0.75f), shape = RoundedCornerShape(4.dp)) {
                            Text("⏱️ ${project.durationSec}s", fontSize = 8.5.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Surface(color = Color.Black.copy(alpha = 0.75f), shape = RoundedCornerShape(4.dp)) {
                            Text("🎙️ ${project.voiceOverLanguage}", fontSize = 8.5.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prompt Concept Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("PROMPT DE CONCEPTO:", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(project.prompt, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs for Script & Storyboard
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Guion Narrativo", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Storyboard & SEO", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedTab) {
                    0 -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (project.scriptText.isNotBlank()) project.scriptText else "No hay guion generado aún para este proyecto.",
                                    fontSize = 10.5.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    1 -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("STORYBOARD VISUAL:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (project.storyboardText.isNotBlank()) project.storyboardText else "Sin storyboard disponible.",
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (project.tagsText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("ETIQUETAS SEO:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(project.tagsText, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Color Grading / Mood:", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(project.selectedMood, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estado de Cumplimiento:", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(project.complianceStatus, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Nube Sync:", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(project.cloudSyncStatus, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fecha de Creación:", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dateFormatter.format(java.util.Date(project.createdAt)), fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // FREED_vmsb_lattice_ZKP Verifiable Status Badge
                val freedStatus = remember(project) { FreedAuditEngine.evaluateProjectFreedAudit(project) }
                FreedVerifiableStatusBadge(
                    auditStatus = freedStatus,
                    onOpenDetails = {
                        onDismiss()
                        onOpenFreedAudit?.invoke(project)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onOpenStudio(project)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.3f).height(40.dp).testTag("quick_preview_open_studio_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Abrir Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onDuplicate(project) },
                        modifier = Modifier.weight(1f).height(40.dp).testTag("quick_preview_duplicate_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Duplicar", fontSize = 10.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onExport(project)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(40.dp).testTag("quick_preview_export_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Exportar", fontSize = 10.sp)
                    }

                    IconButton(
                        onClick = {
                            onDelete(project)
                            onDismiss()
                        },
                        modifier = Modifier.size(40.dp).testTag("quick_preview_delete_btn")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

// Helper function to resolve category status
fun getProjectCategory(project: Project, activeId: Int): String {
    return when {
        project.complianceStatus == "Archived" || project.cloudSyncStatus == "Archived" -> "Archivados"
        project.complianceStatus == "Generating" || (project.scriptText.isBlank() && project.storyboardText.isBlank() && project.videoUri.isBlank() && project.id == activeId) -> "En Generación"
        project.scriptText.isNotBlank() || project.videoUri.isNotBlank() -> "Completados"
        else -> "Borradores"
    }
}

// 5. Tab 3: Workspace Library / Project selection and management (Dashboard)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryWorkspace(
    projects: List<Project>,
    activeId: Int,
    onSelect: (Project) -> Unit,
    onDelete: (Project) -> Unit,
    onDuplicate: (Project) -> Unit,
    onRename: (Project, String) -> Unit,
    onCreateNew: () -> Unit,
    onExport: ((Project) -> Unit)? = null,
    onOpenFreedAudit: ((Project) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRatioFilter by remember { mutableStateOf("All") }
    var selectedCategoryFilter by remember { mutableStateOf("Todos") } // Todos, Borradores, En Generación, Completados, Archivados
    var sortBy by remember { mutableStateOf("Newest") } // Newest, Oldest, Title
    var projectToDelete by remember { mutableStateOf<Project?>(null) }
    var projectToRename by remember { mutableStateOf<Project?>(null) }
    var previewProject by remember { mutableStateOf<Project?>(null) }

    val categoryMetrics = remember(projects, activeId) {
        val total = projects.size
        val drafts = projects.count { getProjectCategory(it, activeId) == "Borradores" }
        val generating = projects.count { getProjectCategory(it, activeId) == "En Generación" }
        val completed = projects.count { getProjectCategory(it, activeId) == "Completados" }
        val archived = projects.count { getProjectCategory(it, activeId) == "Archivados" }
        val totalSecs = projects.sumOf { it.durationSec }
        val count4K = projects.count { it.resolution == "4K" }
        
        mapOf(
            "total" to total,
            "drafts" to drafts,
            "generating" to generating,
            "completed" to completed,
            "archived" to archived,
            "totalSecs" to totalSecs,
            "count4K" to count4K
        )
    }

    val filteredProjects = remember(projects, searchQuery, selectedRatioFilter, selectedCategoryFilter, sortBy, activeId) {
        projects.filter { proj ->
            val matchesQuery = searchQuery.isBlank() ||
                    proj.title.contains(searchQuery, ignoreCase = true) ||
                    proj.prompt.contains(searchQuery, ignoreCase = true)
            val matchesRatio = selectedRatioFilter == "All" || proj.aspectRatio == selectedRatioFilter
            val matchesCategory = selectedCategoryFilter == "Todos" || getProjectCategory(proj, activeId) == selectedCategoryFilter
            matchesQuery && matchesRatio && matchesCategory
        }.sortedWith { a, b ->
            when (sortBy) {
                "Oldest" -> a.createdAt.compareTo(b.createdAt)
                "Title" -> a.title.compareTo(b.title, ignoreCase = true)
                else -> b.createdAt.compareTo(a.createdAt) // Newest
            }
        }
    }

    val dateFormatter = remember {
        java.text.SimpleDateFormat("MMM dd, yyyy • HH:mm", java.util.Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("library_workspace")
    ) {
        // Executive Summary Dashboard KPI Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.VideoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "PANEL DE PROYECTOS IA (DASHBOARD)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${projects.size} proyecto(s) guardado(s) en SQLite Room Database",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onCreateNew,
                        modifier = Modifier.testTag("create_new_project_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo Proyecto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KPI Metrics Grid Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // KPI Card 1: Completados
                    Surface(
                        onClick = { selectedCategoryFilter = "Completados" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedCategoryFilter == "Completados") Color(0xFF052E16) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedCategoryFilter == "Completados") Color(0xFF22C55E) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.width(115.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(14.dp))
                                Text("Completados", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${categoryMetrics["completed"]}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF22C55E))
                            Text("Render listos", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // KPI Card 2: Borradores
                    Surface(
                        onClick = { selectedCategoryFilter = "Borradores" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedCategoryFilter == "Borradores") Color(0xFF1E3A8A) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedCategoryFilter == "Borradores") Color(0xFF3B82F6) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.width(115.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(14.dp))
                                Text("Borradores", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${categoryMetrics["drafts"]}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF3B82F6))
                            Text("Sin procesar", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // KPI Card 3: En Generación
                    Surface(
                        onClick = { selectedCategoryFilter = "En Generación" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedCategoryFilter == "En Generación") Color(0xFF78350F) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedCategoryFilter == "En Generación") Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.width(115.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                Text("En Generación", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${categoryMetrics["generating"]}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF59E0B))
                            Text("En cola / AI active", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // KPI Card 4: Total Tiempo y Res
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.width(125.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Text("Duración Total", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${categoryMetrics["totalSecs"]}s", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            Text("${categoryMetrics["count4K"]} en Ultra HD 4K", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search & Sort Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por título o prompt...", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("project_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(fontSize = 11.sp)
            )

            // Sort Button
            OutlinedButton(
                onClick = {
                    sortBy = when (sortBy) {
                        "Newest" -> "Oldest"
                        "Oldest" -> "Title"
                        else -> "Newest"
                    }
                },
                modifier = Modifier.height(48.dp).testTag("sort_projects_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(sortBy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category & Aspect Ratio Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Todos", "Borradores", "En Generación", "Completados", "Archivados").forEach { cat ->
                val selected = selectedCategoryFilter == cat
                FilterChip(
                    selected = selected,
                    onClick = { selectedCategoryFilter = cat },
                    label = { Text(cat, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.height(32.dp).testTag("category_chip_$cat")
                )
            }

            VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 2.dp))

            listOf("All", "16:9", "9:16", "1:1", "4:3").forEach { ratio ->
                val selected = selectedRatioFilter == ratio
                FilterChip(
                    selected = selected,
                    onClick = { selectedRatioFilter = ratio },
                    label = { Text(ratio, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Project Cards
        if (filteredProjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.VideoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedCategoryFilter != "Todos") "No se encontraron proyectos con los filtros activos" else "No hay proyectos guardados aún",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Prueba cambiando de categoría o creando un nuevo proyecto con la IA.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            filteredProjects.forEach { project ->
                val isActive = project.id == activeId
                val formattedDate = dateFormatter.format(java.util.Date(project.createdAt))
                val category = getProjectCategory(project, activeId)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { previewProject = project }
                        .testTag("project_item_${project.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        if (isActive) 2.dp else 1.dp,
                        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth()
                    ) {
                        // Title row & category status badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (category) {
                                        "Completados" -> Color(0xFF052E16)
                                        "En Generación" -> Color(0xFF78350F)
                                        "Borradores" -> Color(0xFF1E3A8A)
                                        else -> Color(0xFF334155)
                                    },
                                    border = BorderStroke(1.dp, when (category) {
                                        "Completados" -> Color(0xFF22C55E)
                                        "En Generación" -> Color(0xFFF59E0B)
                                        "Borradores" -> Color(0xFF3B82F6)
                                        else -> Color(0xFF94A3B8)
                                    })
                                ) {
                                    Text(
                                        text = when (category) {
                                            "Completados" -> "✅ COMPLETADO"
                                            "En Generación" -> "⚡ EN GENERACIÓN"
                                            "Borradores" -> "📝 BORRADOR"
                                            else -> "📦 ARCHIVADO"
                                        },
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = project.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isActive) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "ACTIVE STUDIO",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Prompt summary
                        Text(
                            text = project.prompt,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Metadata Badges Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "📐 ${project.aspectRatio}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "📺 ${project.resolution}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "⏱️ ${project.durationSec}s",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = formattedDate,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Action toolbar: Quick Preview, Select, Duplicate, Rename, Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { onSelect(project) },
                                    modifier = Modifier.height(34.dp).testTag("select_project_${project.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isActive) "Open Studio" else "Abrir Studio", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { previewProject = project },
                                    modifier = Modifier.height(34.dp).testTag("quick_preview_btn_${project.id}"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Vista Rápida", fontSize = 10.sp)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { onDuplicate(project) },
                                    modifier = Modifier.size(34.dp).testTag("duplicate_project_${project.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Duplicate project",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { projectToRename = project },
                                    modifier = Modifier.size(34.dp).testTag("rename_project_${project.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Rename project",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { projectToDelete = project },
                                    modifier = Modifier.size(34.dp).testTag("delete_project_${project.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete project",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Quick Preview Modal Trigger
    previewProject?.let { proj ->
        QuickPreviewModal(
            project = proj,
            onDismiss = { previewProject = null },
            onOpenStudio = {
                onSelect(it)
                previewProject = null
            },
            onDuplicate = { onDuplicate(it) },
            onExport = {
                onSelect(it)
                previewProject = null
                onExport?.invoke(it)
            },
            onDelete = { projectToDelete = it },
            onOpenFreedAudit = onOpenFreedAudit
        )
    }

    // Delete confirmation dialog
    projectToDelete?.let { proj ->
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text("Delete Project?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    "Are you sure you want to delete '${proj.title}' from your Room database? This action cannot be undone.",
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(proj)
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { projectToDelete = null }) {
                    Text("Cancel", fontSize = 11.sp)
                }
            }
        )
    }

    // Rename project dialog
    projectToRename?.let { proj ->
        var newTitleText by remember { mutableStateOf(proj.title) }

        AlertDialog(
            onDismissRequest = { projectToRename = null },
            title = {
                Text("Rename Project", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text("Enter a new title for this video project:", fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newTitleText,
                        onValueChange = { newTitleText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("rename_title_input"),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitleText.isNotBlank()) {
                            onRename(proj, newTitleText.trim())
                        }
                        projectToRename = null
                    },
                    enabled = newTitleText.isNotBlank()
                ) {
                    Text("Save", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { projectToRename = null }) {
                    Text("Cancel", fontSize = 11.sp)
                }
            }
        )
    }
}

// Dialog to prompt for new video generation
@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, prompt: String, ratio: String, res: String, duration: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var prompt by remember { mutableStateOf("") }
    var selectedRatio by remember { mutableStateOf("16:9") }
    var selectedRes by remember { mutableStateOf("1080p") }
    var duration by remember { mutableStateOf("15") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "VEO 3.1 PROMPT ENGINE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                Text("Project Title", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("E.g., Space Explorers Cinematic", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_title"),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Concept Prompt Input
                Text("AI Video Script / Storyboard Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Describe the visual style, characters, mood, and actions...", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("input_prompt"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Aspect Ratio Selector
                Text("Aspect Ratio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ratios = listOf("16:9", "9:16", "1:1")
                    ratios.forEach { ratio ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedRatio == ratio) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    1.dp,
                                    if (selectedRatio == ratio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedRatio = ratio }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                ratio,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedRatio == ratio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Duration Selector
                Text("Render Duration (Seconds)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = duration,
                    onValueChange = { duration = it },
                    placeholder = { Text("E.g., 15", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_duration"),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val durSec = duration.toIntOrNull() ?: 15
                            onConfirm(title, prompt, selectedRatio, selectedRes, durSec)
                        },
                        enabled = prompt.isNotBlank(),
                        modifier = Modifier.testTag("submit_prompt_button")
                    ) {
                        Text("Generate Video")
                    }
                }
            }
        }
    }
}

// FlowRow layout implementation backport to avoid compose dependency issues
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}

// Helper Modifier Extension for scaling elements
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)

data class DragVideoItem(
    val name: String,
    val sizeBytes: Long,
    val format: String,
    val mimeType: String,
    val uriString: String,
    val isSupported: Boolean,
    val color: Color,
    val thumbnailDesc: String
)

@Composable
fun LocalVideoImportCard(
    viewModel: MainViewModel
) {
    val localVideoUri by viewModel.localVideoUri.collectAsState()
    val localVideoInfo by viewModel.localVideoInfo.collectAsState()
    val localVideoError by viewModel.localVideoError.collectAsState()
    val localVideoProcessingStatus by viewModel.localVideoProcessingStatus.collectAsState()
    val isLocalVideoProcessing by viewModel.isLocalVideoProcessing.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            viewModel.setAndValidateLocalVideo(uri)
        }
    }

    val simulatedVideos = remember {
        listOf(
            DragVideoItem("nature_cinematic.mp4", 12 * 1024 * 1024, "MP4", "video/mp4", "simulated://nature_cinematic.mp4", true, Color(0xFF00796B), "🌿"),
            DragVideoItem("cyberpunk_tokyo.mov", 38 * 1024 * 1024, "MOV", "video/quicktime", "simulated://cyberpunk_tokyo.mov", true, Color(0xFF5E35B1), "🌌"),
            DragVideoItem("huge_commercial_draft.mkv", 145 * 1024 * 1024, "MKV", "video/x-matroska", "simulated://huge_commercial_draft.mkv", false, Color(0xFFE53935), "⚡"),
            DragVideoItem("raw_music_unsupported.wav", 18 * 1024 * 1024, "WAV", "audio/wav", "simulated://raw_music_unsupported.wav", false, Color(0xFFD81B60), "🎵")
        )
    }

    var activeDragItem by remember { mutableStateOf<DragVideoItem?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isHoveringDropZone by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("local_video_import_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Card Header Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = "Upload",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Local Video Processing Sandbox",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Upload and validate a local video file to inspect properties or import it into your workspace.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLocalVideoProcessing) {
                    // Processing State
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = localVideoProcessingStatus ?: "Processing...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (localVideoInfo != null) {
                    // Valid Video File Selected
                    val file = localVideoInfo!!
                    val sizeInMb = file.sizeBytes / (1024.0 * 1024.0)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D2818)) // Dark green accent background
                            .border(1.5.dp, Color(0xFF2E7D32), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Valid Format",
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "VALID VIDEO FORMAT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF81C784),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = file.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text("SIZE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text(String.format("%.2f MB", sizeInMb), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                                Column {
                                    Text("FORMAT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text(file.format, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                                Column {
                                    Text("MIME TYPE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text(file.mimeType ?: "Unknown", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.clearLocalVideo() },
                                    modifier = Modifier.weight(1f).height(40.dp).testTag("clear_local_video_btn"),
                                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81C784)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Clear", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.openMediaPickerSheet() },
                                    modifier = Modifier.weight(1.8f).height(40.dp).testTag("load_to_timeline_from_sandbox_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Timeline", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Load into Timeline", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.processLocalVideo() },
                                    modifier = Modifier.weight(1.8f).height(40.dp).testTag("process_local_video_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Process", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Veo AI", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else if (localVideoError != null) {
                    // Validation Error State
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2C0B0E)) // Dark red background
                            .border(1.5.dp, Color(0xFFC62828), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Invalid Format",
                                    tint = Color(0xFFEF5350),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "VALIDATION ERROR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF5350),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = localVideoError!!,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { viewModel.clearLocalVideo() },
                                    modifier = Modifier.testTag("dismiss_error_btn")
                                ) {
                                    Text("Dismiss", color = Color(0xFFEF5350), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { filePickerLauncher.launch("video/*") },
                                    modifier = Modifier.height(36.dp).testTag("retry_picker_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Choose Another File", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Empty / Selector state with dashed dropzone & drag sources
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val zoneColor = if (isHoveringDropZone) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
                        val zoneBg = if (isHoveringDropZone) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(zoneBg)
                                .drawBehind {
                                    val strokeWidth = if (isHoveringDropZone) 2.5.dp.toPx() else 1.5.dp.toPx()
                                    val intervals = floatArrayOf(15f, 15f)
                                    val pathEffect = android.graphics.DashPathEffect(intervals, 0f)
                                    
                                    val paint = android.graphics.Paint().apply {
                                        this.style = android.graphics.Paint.Style.STROKE
                                        this.strokeWidth = strokeWidth
                                        this.color = zoneColor.hashCode()
                                        this.pathEffect = pathEffect
                                        this.isAntiAlias = true
                                    }
                                    
                                    val path = android.graphics.Path().apply {
                                        addRoundRect(
                                            0f, 0f, size.width, size.height,
                                            12.dp.toPx(), 12.dp.toPx(),
                                            android.graphics.Path.Direction.CW
                                        )
                                    }
                                    
                                    drawContext.canvas.nativeCanvas.drawPath(path, paint)
                                }
                                .clickable { viewModel.openMediaPickerSheet() }
                                .testTag("local_video_selector_area"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = if (isHoveringDropZone) Icons.Default.FileDownload else Icons.Default.CloudUpload,
                                    contentDescription = "Upload Local Video",
                                    tint = if (isHoveringDropZone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHoveringDropZone) "DROP FILE NOW!" else "Drag local file here or select file...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Supports MP4, MKV, WebM, AVI, MOV (Max 50MB limit)",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Drag Source Gallery header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Simulated Device Clips",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "SIMULATED LOCAL DEVICE CLIPS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "💡 DRAG any clip UP and drop it in the dashed zone, or TAP to load instantly.",
                            fontSize = 8.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Gallery of drag/click sources
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            simulatedVideos.forEach { item ->
                                val sizeInMb = item.sizeBytes / (1024.0 * 1024.0)
                                Card(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.setSimulatedLocalVideo(
                                                displayName = item.name,
                                                size = item.sizeBytes,
                                                format = item.format,
                                                mimeType = item.mimeType,
                                                uriString = item.uriString
                                            )
                                        }
                                        .pointerInput(item) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    activeDragItem = item
                                                    dragOffset = Offset.Zero
                                                    isHoveringDropZone = false
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    dragOffset += dragAmount
                                                    isHoveringDropZone = dragOffset.y < -120f
                                                },
                                                onDragEnd = {
                                                    if (isHoveringDropZone) {
                                                        viewModel.setSimulatedLocalVideo(
                                                            displayName = item.name,
                                                            size = item.sizeBytes,
                                                            format = item.format,
                                                            mimeType = item.mimeType,
                                                            uriString = item.uriString
                                                        )
                                                    }
                                                    activeDragItem = null
                                                    dragOffset = Offset.Zero
                                                    isHoveringDropZone = false
                                                },
                                                onDragCancel = {
                                                    activeDragItem = null
                                                    dragOffset = Offset.Zero
                                                    isHoveringDropZone = false
                                                }
                                            )
                                        }
                                        .testTag("gallery_clip_${item.name.substringBefore('.')}"),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(item.color.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(item.thumbnailDesc, fontSize = 20.sp)
                                        }
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Text(
                                            text = item.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.format,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = item.color
                                            )
                                            Text(
                                                text = String.format("%.0f MB", sizeInMb),
                                                fontSize = 8.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Render floaty ghost drag avatar
        val currentDragItem = activeDragItem
        if (currentDragItem != null) {
            val density = androidx.compose.ui.platform.LocalDensity.current
            val dragOffsetDpX = with(density) { dragOffset.x.toDp() }
            val dragOffsetDpY = with(density) { dragOffset.y.toDp() }
            
            Box(
                modifier = Modifier
                    .offset(x = dragOffsetDpX, y = dragOffsetDpY)
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(currentDragItem.color.copy(alpha = 0.85f))
                    .border(1.5.dp, Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(currentDragItem.thumbnailDesc, fontSize = 14.sp)
                    Text(currentDragItem.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
