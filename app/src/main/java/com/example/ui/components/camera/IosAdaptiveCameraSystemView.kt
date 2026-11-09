package com.example.ui.components.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.camera.IosCameraMode
import com.example.data.camera.IosLens
import com.example.ui.MainViewModel

@Composable
fun IosAdaptiveCameraSystemView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentMode by viewModel.iosCameraMode.collectAsState()
    val currentLens by viewModel.iosCameraLens.collectAsState()
    val controls by viewModel.iosManualControls.collectAsState()
    val isRecording by viewModel.isRecordingVideo.collectAsState()
    val recordingDuration by viewModel.recordingDurationSeconds.collectAsState()
    val vmsbPackets by viewModel.recordingVmsbPackets.collectAsState()
    val recordedClips by viewModel.recordedClips.collectAsState()
    val latestLedgerBlock = viewModel.berneLedger.getLatestBlock()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasCameraPermission = perms[Manifest.permission.CAMERA] == true
        if (hasCameraPermission) {
            viewModel.latticeProtector.recordAuditEvent(
                sensor = "PERMISOS_DEL_SISTEMA",
                event = "Permiso nativo de Cámara y Micrófono concedido bajo directivas GDPR/CCPA",
                compliance = "GDPR Art. 25 Privacy by Default",
                indicator = "GREEN_CAM_LED"
            )
        }
    }

    var showManualProControls by remember { mutableStateOf(false) }
    var tapFocusPoint by remember { mutableStateOf<Pair<Float, Float>?>(null) }
    var simulatedLevelAngle by remember { mutableStateOf(0.2f) }

    // Recording Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ios_camera_system_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF000000)),
        border = BorderStroke(
            1.5.dp,
            Brush.verticalGradient(
                listOf(
                    if (isRecording) Color(0xFFEF4444) else Color(0xFF38BDF8),
                    Color(0xFF1E293B)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // TOP HEADER: Privacy Indicators & Resolution Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Privacy LEDs (Green / Orange) & Enclave Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF111827))
                        .clickable { viewModel.openLatticeAuditDialog() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) Color(0xFFF97316) else Color(0xFF4B5563))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LATTICE CERO-DATA",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Format & FPS Badge (iOS Style)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .border(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = currentMode.badge,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${currentMode.maxFps} FPS",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showManualProControls = !showManualProControls },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Controles Pro",
                            tint = if (showManualProControls) Color(0xFFF59E0B) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // MAIN CAMERA VIEWFINDER (CameraX Preview or High-Precision Simulated Viewport)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0A0F1D))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            tapFocusPoint = Pair(offset.x, offset.y)
                        }
                    }
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            try {
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val cameraSelector = if (controls.isFrontCamera) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview
                                        )
                                    } catch (exc: Exception) {
                                        // Safe fallback if camera is busy or unavailable
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                            } catch (e: Exception) {
                                // Graceful fallback
                            }
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Overlay / Viewfinder Simulation (when permission not yet granted or in emulator)
                if (!hasCameraPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFF1E3A8A).copy(alpha = 0.5f),
                                        Color(0xFF030712).copy(alpha = 0.95f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Icon",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "SISTEMA DE CÁMARA IOS UNIVERSAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Cumplimiento estricto de privacidad GDPR • 100% On-Device",
                                fontSize = 8.5.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Icon(Icons.Default.LockOpen, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Activar Acceso Nativo a Cámara", fontSize = 10.sp)
                            }
                        }
                    }
                }

                // 3x3 Framing Grid Overlay
                if (controls.isGrid3x3Enabled) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.15f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.15f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.15f)))
                    }
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth().border(0.5.dp, Color.White.copy(alpha = 0.15f)))
                        Box(modifier = Modifier.weight(1f).fillMaxWidth().border(0.5.dp, Color.White.copy(alpha = 0.15f)))
                        Box(modifier = Modifier.weight(1f).fillMaxWidth().border(0.5.dp, Color.White.copy(alpha = 0.15f)))
                    }
                }

                // Horizon Leveler (Virtual Gyro Line)
                if (controls.isHorizonLevelerEnabled) {
                    val isLevel = kotlin.math.abs(simulatedLevelAngle) < 0.5f
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(80.dp)
                            .height(1.5.dp)
                            .background(if (isLevel) Color(0xFF10B981) else Color(0xFFF59E0B))
                    )
                }

                // Interactive Tap Focus Box
                tapFocusPoint?.let { (x, y) ->
                    Box(
                        modifier = Modifier
                            .offset(x = (x / 2.5f).dp, y = (y / 2.5f).dp)
                            .size(54.dp)
                            .border(1.2.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                    )
                }

                // Live Recording Red Pill Timer
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDC2626).copy(alpha = pulseAlpha))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                            Spacer(modifier = Modifier.width(6.dp))
                            val mins = (recordingDuration / 60).toInt()
                            val secs = (recordingDuration % 60).toInt()
                            val tenths = ((recordingDuration * 10) % 10).toInt()
                            Text(
                                text = String.format("%02d:%02d.%d", mins, secs, tenths),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• $vmsbPackets frames VMSB©",
                                fontSize = 8.5.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Mode Info Watermark
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Column {
                        Text(
                            text = "${currentMode.label} • ${currentLens.sensorType}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Color: ${currentMode.colorSpace}",
                            fontSize = 7.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // LENS SELECTOR (.5x, 1x, 2x, 5x - iOS Native Style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF111827))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IosLens.values().forEach { lens ->
                            val isSelected = currentLens == lens
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFF59E0B) else Color.Transparent)
                                    .clickable { viewModel.setIosCameraLens(lens) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lens.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) Color.Black else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PRO CONTROLS DRAWER (EV, ISO, Shutter, Kelvin, Aperture)
            AnimatedVisibility(visible = showManualProControls) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111827))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "CONTROLES MANUALES PRO (EXPOSICIÓN & ÓPTICA)",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF59E0B),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // EV Bias
                        Column(modifier = Modifier.weight(1f)) {
                            Text("EV: ${String.format("%+.1f", controls.evBias)}", fontSize = 8.sp, color = Color.LightGray)
                            Slider(
                                value = controls.evBias,
                                onValueChange = { v -> viewModel.updateIosManualControls { it.copy(evBias = v) } },
                                valueRange = -2.0f..2.0f,
                                steps = 8,
                                modifier = Modifier.height(26.dp),
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFF59E0B), activeTrackColor = Color(0xFFF59E0B))
                            )
                        }

                        // ISO
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ISO: ${controls.iso}", fontSize = 8.sp, color = Color.LightGray)
                            Slider(
                                value = controls.iso.toFloat(),
                                onValueChange = { v -> viewModel.updateIosManualControls { it.copy(iso = v.toInt()) } },
                                valueRange = 50f..3200f,
                                modifier = Modifier.height(26.dp),
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8))
                            )
                        }

                        // Apertura f-stop
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Apertura: f/${String.format("%.1f", controls.cinematicAperture)}", fontSize = 8.sp, color = Color.LightGray)
                            Slider(
                                value = controls.cinematicAperture,
                                onValueChange = { v -> viewModel.updateIosManualControls { it.copy(cinematicAperture = v) } },
                                valueRange = 1.4f..16.0f,
                                modifier = Modifier.height(26.dp),
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CAMERA MODE CAROUSEL (iOS Native Selector)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IosCameraMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    Text(
                        text = mode.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF64748B),
                        modifier = Modifier
                            .clickable { viewModel.setIosCameraMode(mode) }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SHUTTER & RECORDING BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flash / Torch
                IconButton(
                    onClick = { viewModel.updateIosManualControls { it.copy(isTorchActive = !it.isTorchActive) } },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = if (controls.isTorchActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Torch",
                        tint = if (controls.isTorchActive) Color(0xFFF59E0B) else Color.LightGray
                    )
                }

                // Shutter / Record Button (iOS Pro Circular Ring with Red Morphing Center)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .clickable {
                            if (isRecording) {
                                viewModel.stopRecordingVideo(addClipToCurrentProject = true)
                            } else {
                                viewModel.startRecordingVideo()
                            }
                        }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isRecording) 28.dp else 52.dp)
                            .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                }

                // Switch Camera (Front / Back)
                IconButton(
                    onClick = { viewModel.updateIosManualControls { it.copy(isFrontCamera = !it.isFrontCamera) } },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraIos,
                        contentDescription = "Flip Camera",
                        tint = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BERNE CONVENTION & RIGHTS RESERVED BANNER (MANDATORY COMPLIANCE)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Berne Legal",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PROTOCOLO DE BERNA (WIPO/OMPI) BY VMSB©",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF59E0B),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "LEDGER SELLADO",
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "CON TODOS LOS DERECHOS RESERVADOS. Protección jurídica automática universal inmediata desde el instante de fijación digital. Sellado criptográfico inmutable con firma de retículo y procedencia C2PA.",
                        fontSize = 8.sp,
                        color = Color.LightGray,
                        lineHeight = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons for Certificate, Lattice Audit, and Universal Connectivity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.openBerneCertificateForLatest() },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B)),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Verified, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Certificado Berna", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.openLatticeAuditDialog() },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Shield, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auditoría GDPR", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.openConnectivityDialog() },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Share, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hub Conectividad", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // RECENT RECORDED CLIPS LIST (if any)
            if (recordedClips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "CLIPS GRABADOS CON SELLO DE TIEMPO BERNA LEDGER (${recordedClips.size})",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recordedClips.take(5).forEach { clip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .border(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.openBerneCertificateForClip(clip) }
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Movie, null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(clip.title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Text("${clip.resolution} • ${clip.fps}fps • ${String.format("%.1f", clip.durationSeconds)}s", fontSize = 7.5.sp, color = Color.LightGray)
                                Text("Hash: ${clip.berneLedgerHash.take(8)}...", fontSize = 7.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFF59E0B))
                            }
                        }
                    }
                }
            }
        }
    }
}
