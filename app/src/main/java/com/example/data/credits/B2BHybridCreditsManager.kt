package com.example.data.credits

import java.io.Serializable

/**
 * Modelo de Créditos Híbridos B2B (Freemium vs Pro Enterprise)
 * - Capa Gratuita: Edición NLE local ilimitada, timeline multi-track, Media3 ExoPlayer a costo $0.
 * - Capa Pro Enterprise: Créditos de inferencia Veo 3.1 en la nube, firma C2PA empresarial, Gateways directos.
 */

enum class B2BSubscriptionTier(
    val title: String,
    val badge: String,
    val monthlyPriceUsd: String,
    val isUnlimitedLocalNle: Boolean,
    val cloudCreditsIncluded: Int,
    val hasEnterpriseC2paSigning: Boolean,
    val hasDirectGatewaysBatchPublish: Boolean,
    val watermarkRemoved: Boolean
) {
    FREEMIUM_COMMUNITY(
        title = "Community Studio Free",
        badge = "EDITOR LOCAL ILIMITADO",
        monthlyPriceUsd = "$0 / mes",
        isUnlimitedLocalNle = true,
        cloudCreditsIncluded = 150,
        hasEnterpriseC2paSigning = false,
        hasDirectGatewaysBatchPublish = false,
        watermarkRemoved = false
    ),
    PRO_ENTERPRISE(
        title = "Enterprise Studio Pro",
        badge = "AGENCIA & CORPORATIVO 2026",
        monthlyPriceUsd = "$149 / mes",
        isUnlimitedLocalNle = true,
        cloudCreditsIncluded = 5000,
        hasEnterpriseC2paSigning = true,
        hasDirectGatewaysBatchPublish = true,
        watermarkRemoved = true
    )
}

data class CreditWalletStatus(
    val tier: B2BSubscriptionTier = B2BSubscriptionTier.PRO_ENTERPRISE,
    val totalCreditsRemaining: Int = 4680,
    val maxCreditsAllocated: Int = 5000,
    val localRendersCompletedFree: Int = 142,
    val cloudGenerationsCompleted: Int = 32,
    val enterpriseSignedAssetsCount: Int = 28
) : Serializable {
    val usagePercent: Float
        get() = (1f - (totalCreditsRemaining.toFloat() / maxCreditsAllocated.coerceAtLeast(1))).coerceIn(0f, 1f)
}

class B2BHybridCreditsManager {

    private var currentStatus = CreditWalletStatus()

    fun getStatus(): CreditWalletStatus = currentStatus

    fun switchTier(tier: B2BSubscriptionTier): CreditWalletStatus {
        currentStatus = currentStatus.copy(
            tier = tier,
            maxCreditsAllocated = tier.cloudCreditsIncluded,
            totalCreditsRemaining = tier.cloudCreditsIncluded
        )
        return currentStatus
    }

    fun consumeCloudCredits(credits: Int = 25): Boolean {
        if (currentStatus.totalCreditsRemaining < credits) {
            return false
        }
        currentStatus = currentStatus.copy(
            totalCreditsRemaining = currentStatus.totalCreditsRemaining - credits,
            cloudGenerationsCompleted = currentStatus.cloudGenerationsCompleted + 1
        )
        return true
    }

    fun recordFreeLocalRender(): CreditWalletStatus {
        currentStatus = currentStatus.copy(
            localRendersCompletedFree = currentStatus.localRendersCompletedFree + 1
        )
        return currentStatus
    }

    fun recordC2paEnterpriseSignedAsset(): CreditWalletStatus {
        currentStatus = currentStatus.copy(
            enterpriseSignedAssetsCount = currentStatus.enterpriseSignedAssetsCount + 1
        )
        return currentStatus
    }
}
