package com.example.ui.components.credits

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.credits.*

@Composable
fun B2BHybridCreditsCard(
    creditsManager: B2BHybridCreditsManager,
    onOpenUpgradeDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var walletStatus by remember { mutableStateOf(creditsManager.getStatus()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("b2b_hybrid_credits_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A29)),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6))
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .background(Color(0xFF78350F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Credits",
                            tint = Color(0xFFFCD34D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Créditos Híbridos B2B",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Nivel Actual: ${walletStatus.tier.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFBBF24)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF064E3B)
                ) {
                    Text(
                        text = walletStatus.tier.badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Wallet Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricMiniCard(
                    title = "Créditos Cloud",
                    value = "${walletStatus.totalCreditsRemaining}",
                    subtitle = "de ${walletStatus.maxCreditsAllocated} mensuales",
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "NLE Local (Media3)",
                    value = "ILIMITADO",
                    subtitle = "${walletStatus.localRendersCompletedFree} renders a $0",
                    color = Color(0xFF34D399),
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "Firma C2PA",
                    value = "EMPRESARIAL",
                    subtitle = "${walletStatus.enterpriseSignedAssetsCount} certificados",
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar of remaining
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Consumo mensual de cómputo Veo/Gemini", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(
                        text = "${((1f - walletStatus.usagePercent) * 100).toInt()}% disponible",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    )
                }
                LinearProgressIndicator(
                    progress = { 1f - walletStatus.usagePercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFF59E0B),
                    trackColor = Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tier Switch / Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val newTier = if (walletStatus.tier == B2BSubscriptionTier.PRO_ENTERPRISE) {
                            B2BSubscriptionTier.FREEMIUM_COMMUNITY
                        } else {
                            B2BSubscriptionTier.PRO_ENTERPRISE
                        }
                        walletStatus = creditsManager.switchTier(newTier)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                ) {
                    Text(
                        text = if (walletStatus.tier == B2BSubscriptionTier.PRO_ENTERPRISE) "Ver Modo Free" else "Activar Pro Enterprise",
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onOpenUpgradeDialog,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    Text("Recargar Créditos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(8.dp)
    ) {
        Text(text = title, fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle, fontSize = 8.5.sp, color = Color(0xFF64748B))
    }
}
