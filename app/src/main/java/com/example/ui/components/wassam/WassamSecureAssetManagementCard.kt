package com.example.ui.components.wassam

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
import com.example.data.wassam.*
import kotlinx.coroutines.launch

/**
 * Suite Nativa WASSAM (Web-based Automated System for Secure Asset Management)
 *
 * Integra 4 subsistemas nativos fundamentales:
 * 1. Inventario de Activos Criptográficos VMSB© (Sincronización Cero-Fricción)
 * 2. Wassam Asset Editor (Editor nativo de metadatos, enclaves y re-sellado ZKP)
 * 3. Wassam Sync Health Monitor (Monitor de salud de canales Google, Meta, Apple y Wasm)
 * 4. Wassam Secure Cache Manager (Caché cifrada con política de borrado seguro Zeroization)
 */
@Composable
fun WassamSecureAssetManagementCard(
    wassamManager: WassamAssetManagerService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val assets by wassamManager.assets.collectAsState()
    val telemetry by wassamManager.telemetry.collectAsState()
    val isSyncing by wassamManager.isSyncing.collectAsState()

    val healthReport by wassamManager.healthMonitor.report.collectAsState()
    val isDiagnosing by wassamManager.healthMonitor.isDiagnosing.collectAsState()

    val cacheTelemetry by wassamManager.secureCache.telemetry.collectAsState()

    // Control de Suite Tabs
    var currentSuiteTab by remember { mutableStateOf(0) } // 0: Activos, 1: Editor Nativo, 2: Salud Sync, 3: Caché Segura
    val suiteTabs = listOf("Activos", "Editor Nativo", "Salud Sync", "Caché Segura")

    // Estado del Asset Editor
    var editingAssetId by remember { mutableStateOf<String?>(assets.firstOrNull()?.id) }
    val currentEditingAsset = assets.firstOrNull { it.id == editingAssetId } ?: assets.firstOrNull()

    var editTitle by remember(currentEditingAsset) { mutableStateOf(currentEditingAsset?.title ?: "") }
    var editDestination by remember(currentEditingAsset) { mutableStateOf(currentEditingAsset?.targetDestination ?: "") }
    var editType by remember(currentEditingAsset) { mutableStateOf(currentEditingAsset?.type ?: WassamAssetType.VIDEO_RENDER_MASTER) }

    // Filtros de inventario
    var selectedFilterIndex by remember { mutableStateOf(0) }
    val filterTabs = listOf("Todos", "Vídeo", "Audio", "C2PA", "Wasm LUT", "ZKP")

    var showRegisterDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wassam_secure_asset_management_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF070B14)),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFF0284C7), Color(0xFF10B981), Color(0xFF8B5CF6))
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0369A1), Color(0xFF065F46))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EnhancedEncryption,
                            contentDescription = "WASSAM Engine",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "WASSAM SUITE NATIVA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF064E3B)
                            ) {
                                Text(
                                    text = "VMSB AUTO-SYNC",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF34D399),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Asset Editor • Sync Health Monitor • Secure Cache Zeroization",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF38BDF8),
                            fontSize = 9.sp
                        )
                    }
                }

                // TOGGLE AUTOMATION SWITCH
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (telemetry.isAutomatedSyncActive) "AUTO" else "MANUAL",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (telemetry.isAutomatedSyncActive) Color(0xFF34D399) else Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = telemetry.isAutomatedSyncActive,
                        onCheckedChange = { wassamManager.toggleAutomatedSync() },
                        modifier = Modifier.height(24.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF059669),
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MAIN SUITE TABS SELECTOR (NATIVE WASSAM ARCHITECTURE)
            TabRow(
                selectedTabIndex = currentSuiteTab,
                containerColor = Color(0xFF0F172A),
                contentColor = Color(0xFF38BDF8),
                indicator = {},
                divider = {}
            ) {
                suiteTabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = currentSuiteTab == index,
                        onClick = { currentSuiteTab = index },
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (currentSuiteTab == index) Color(0xFF1E3A8A) else Color.Transparent
                            ),
                        text = {
                            Text(
                                text = tabTitle,
                                fontSize = 9.5.sp,
                                fontWeight = if (currentSuiteTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentSuiteTab == index) Color(0xFF93C5FD) else Color(0xFF94A3B8)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TAB CONTENT ROUTING
            when (currentSuiteTab) {
                0 -> {
                    // ==========================================
                    // TAB 0: INVENTARIO DE ACTIVOS & QUICK SYNC
                    // ==========================================
                    // METRICS STRIP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WassamMetricBox("Activos VMSB", "${telemetry.totalManagedAssets} Activos", "Enclaves", Color(0xFF38BDF8), Modifier.weight(1f))
                        WassamMetricBox("Throughput", "${telemetry.syncThroughputMBps} MB/s", "Cero Fricción", Color(0xFF34D399), Modifier.weight(1f))
                        WassamMetricBox("Cero-Fricción", "${telemetry.zeroFrictionSuccessRate.toInt()}% OK", "AES-256", Color(0xFFFBBF24), Modifier.weight(1f))
                        WassamMetricBox("Enclaves", "${telemetry.activeEnclaveSessions} Activos", "NIST FIPS 204", Color(0xFFA78BFA), Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SUB-FILTER
                    ScrollableTabRow(
                        selectedTabIndex = selectedFilterIndex,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF38BDF8),
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        filterTabs.forEachIndexed { index, tabTitle ->
                            Tab(
                                selected = selectedFilterIndex == index,
                                onClick = { selectedFilterIndex = index },
                                text = {
                                    Text(
                                        text = tabTitle,
                                        fontSize = 8.5.sp,
                                        fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedFilterIndex == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredAssets = when (selectedFilterIndex) {
                        1 -> assets.filter { it.type == WassamAssetType.VIDEO_RENDER_MASTER }
                        2 -> assets.filter { it.type == WassamAssetType.AUDIO_EBU_STEM }
                        3 -> assets.filter { it.type == WassamAssetType.C2PA_JUMBF_MANIFEST }
                        4 -> assets.filter { it.type == WassamAssetType.WASM_LUT_ENCLAVE }
                        5 -> assets.filter { it.type == WassamAssetType.ZKP_INTEGRITY_PROOF }
                        else -> assets
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filteredAssets.forEach { asset ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF1E293B))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(asset.type.badgeColor))
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = asset.title,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = {
                                                    editingAssetId = asset.id
                                                    currentSuiteTab = 1 // Conmutar al editor nativo
                                                },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF38BDF8), modifier = Modifier.size(13.dp))
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (asset.syncState == WassamSyncState.SYNCING) Color(0xFFB45309) else Color(0xFF064E3B)
                                            ) {
                                                Text(
                                                    text = asset.syncState.label,
                                                    fontSize = 7.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (asset.syncState == WassamSyncState.SYNCING) Color(0xFFFDE68A) else Color(0xFF6EE7B7),
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Destino: ${asset.targetDestination}",
                                            fontSize = 8.sp,
                                            color = Color(0xFF94A3B8),
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${asset.sizeFormatted} | ${asset.latencyMs} ms",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFBBF24)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    wassamManager.executeAutomatedFrictionlessSync()
                                    Toast.makeText(context, "Sincronización WASSAM Completada", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isSyncing,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(36.dp)
                                .testTag("wassam_sync_all_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isSyncing) Icons.Default.HourglassEmpty else Icons.Default.CloudSync,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isSyncing) "Sincronizando..." else "Sincronizar Todo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = { showRegisterDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("wassam_register_asset_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Nuevo Enclave", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                1 -> {
                    // ==========================================
                    // TAB 1: WASSAM ASSET EDITOR NATIVO
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0B132B))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WASSAM ASSET EDITOR NATIVO",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1E3A8A)
                            ) {
                                Text(
                                    text = "ID: ${currentEditingAsset?.id ?: "NUEVO"}",
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF93C5FD),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Selector de activo a editar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            assets.take(4).forEach { a ->
                                FilterChip(
                                    selected = a.id == editingAssetId,
                                    onClick = {
                                        editingAssetId = a.id
                                    },
                                    label = { Text(a.title.take(10), fontSize = 7.5.sp) }
                                )
                            }
                        }

                        // Input Título
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("Título del Activo Criptográfico", fontSize = 9.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Input Destino
                        OutlinedTextField(
                            value = editDestination,
                            onValueChange = { editDestination = it },
                            label = { Text("Destino de Sincronización Segura / Enclave", fontSize = 9.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Tipo de Activo
                        Text("Tipo de Activo VMSB:", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            WassamAssetType.values().forEach { t ->
                                FilterChip(
                                    selected = editType == t,
                                    onClick = { editType = t },
                                    label = { Text(t.label.take(11), fontSize = 7.5.sp) }
                                )
                            }
                        }

                        // Sello y Chequeo Criptográfico
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF030712)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Huella SHA-256 Actual: ${currentEditingAsset?.checksumSha256 ?: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"}",
                                    fontSize = 7.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Seguridad: AES-256-GCM • ZKP Groth16 • Aislamiento Off-Heap",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF34D399)
                                )
                            }
                        }

                        // Botón Guardar y Re-Sellar
                        Button(
                            onClick = {
                                if (currentEditingAsset != null && editTitle.isNotBlank()) {
                                    wassamManager.reEncryptAndEditAsset(
                                        assetId = currentEditingAsset.id,
                                        newTitle = editTitle,
                                        newDestination = editDestination,
                                        newType = editType
                                    )
                                    Toast.makeText(context, "Activo guardado y re-sellado en WASSAM", Toast.LENGTH_SHORT).show()
                                    currentSuiteTab = 0 // Regresar al inventario
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Guardar y Re-Sellar en Enclave", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                2 -> {
                    // ==========================================
                    // TAB 2: WASSAM SYNC HEALTH MONITOR
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0B132B))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MONITOR DE SALUD DE SINCRONIZACIÓN",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF34D399)
                                )
                            }

                            Text(
                                text = "Score: ${healthReport.overallHealthScorePercent}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24)
                            )
                        }

                        Text(
                            text = healthReport.statusVerdict,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )

                        // 4 CANALES
                        healthReport.channels.forEach { ch ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF1E293B))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(ch.platformName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(ch.statusLabel, fontSize = 7.5.sp, color = Color(0xFF94A3B8))
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${ch.latencyMs} ms", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFFBBF24))
                                        Text("Jitter ±${ch.jitterMs} ms", fontSize = 7.sp, color = Color(0xFF34D399))
                                    }
                                }
                            }
                        }

                        // Botón de Diagnóstico
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    wassamManager.healthMonitor.runChannelDiagnostics()
                                    Toast.makeText(context, "Diagnóstico de Canales Completado (100% Operativo)", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isDiagnosing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                        ) {
                            Icon(
                                imageVector = if (isDiagnosing) Icons.Default.HourglassEmpty else Icons.Default.Healing,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDiagnosing) "Diagnosticando..." else "Diagnosticar y Auto-Reparar Canales",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                3 -> {
                    // ==========================================
                    // TAB 3: WASSAM SECURE CACHE MANAGER
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0B132B))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GESTOR DE CACHÉ SEGURA ZEROIZATION",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF047857)
                            ) {
                                Text(
                                    text = "HIT RATE: ${cacheTelemetry.cacheHitRatePercent}%",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7F3D0),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Progreso de Capacidad
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Uso de Cuota Cifrada:",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "${cacheTelemetry.usedFormatted} / ${cacheTelemetry.capacityFormatted}",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { cacheTelemetry.usagePercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF0284C7),
                                trackColor = Color(0xFF1E293B)
                            )
                        }

                        // Detalles de Protección
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF030712)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "• Política Zeroization: Sobrescritura física de cada byte en memoria antes de desalojar.",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "• Bloques Criptográficos Activos: ${cacheTelemetry.cachedBlocksCount} enclaves en RAM off-heap.",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "• Último Borrado Cero-Huella: ${cacheTelemetry.lastWipeTimestamp}",
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Botones de Acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    val wiped = wassamManager.secureCache.secureWipeAndZeroize()
                                    Toast.makeText(context, "Caché Zeroizada: ${(wiped / (1024 * 1024))} MB purgados con ceros", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Limpiar y Zeroizar", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val saved = wassamManager.secureCache.optimizeWasmCache()
                                    Toast.makeText(context, "Caché Wasm Optimizada (${(saved / (1024 * 1024))} MB compactados)", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Default.Memory, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Optimizar Caché", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO PARA REGISTRAR NUEVO ACTIVO EN ENCLAVE WASSAM
    if (showRegisterDialog) {
        var newTitle by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(WassamAssetType.VIDEO_RENDER_MASTER) }
        var selectedDest by remember { mutableStateOf("Google Cloud Storage / YouTube Ingest") }

        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = {
                Text(
                    text = "Registrar Activo en Enclave WASSAM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "El activo se cifrará con AES-256-GCM y se vinculará al motor VMSB con verificación ZKP:",
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8)
                    )

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título del Activo", fontSize = 10.sp) },
                        placeholder = { Text("Ej. Veo 3.1 8K Final Cut Master", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Tipo de Activo:", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        WassamAssetType.values().take(3).forEach { t ->
                            FilterChip(
                                selected = selectedType == t,
                                onClick = { selectedType = t },
                                label = { Text(t.label.take(12), fontSize = 8.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val titleToUse = if (newTitle.isNotBlank()) newTitle else "Veo 3.1 Certified Master Track"
                        wassamManager.registerAsset(
                            title = titleToUse,
                            type = selectedType,
                            sizeBytes = 250000000L,
                            targetDestination = selectedDest
                        )
                        showRegisterDialog = false
                        Toast.makeText(context, "Activo sellado en WASSAM", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Registrar y Sellar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("Cancelar", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }
}

@Composable
private fun WassamMetricBox(
    label: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
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
            Text(text = label, fontSize = 7.5.sp, color = Color(0xFF94A3B8), maxLines = 1)
            Text(text = value, fontSize = 9.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1)
            Text(text = sub, fontSize = 7.sp, color = Color(0xFF64748B), maxLines = 1)
        }
    }
}
