package com.example.data.strategic

import java.io.Serializable

data class PlatformPingItem(
    val platformName: String,
    val endpointUrl: String,
    val latencyMs: Long,
    val httpStatusCode: Int,
    val isOptimal: Boolean,
    val protocol: String,
    val cipherSuite: String,
    val verificationHash: String
) : Serializable

data class EconomicFeedData(
    val baseCurrency: String = "USD",
    val eurRate: Double = 0.92,
    val gbpRate: Double = 0.78,
    val feedDate: String,
    val feedSource: String,
    val alphabetMarketCap: String = "$2.28T USD",
    val metaMarketCap: String = "$1.46T USD",
    val appleMarketCap: String = "$3.42T USD",
    val cloudAiVideoTam: String = "$18.5B USD"
) : Serializable

data class DirectPlatformBenefit(
    val platformTarget: String,
    val capexRequirement: String, // "$0 Capex - Client-side NDK/WASM"
    val opexReductionPercent: String, // "42.6% Ahorro Ancho de Banda & CDN"
    val legalRiskMitigation: String, // "Blindaje C2PA v2.1 & Berna Art. 5.2"
    val strategicValueAdded: String // "Retención masiva creadores y monetización"
) : Serializable

data class BusinessOptionItem(
    val title: String,
    val structure: String,
    val estimatedValuation: String,
    val strategicAdvantage: String,
    val executionHorizon: String
) : Serializable

data class ScalabilityAndRoiMetrics(
    val year1StreamsCertified: String, // "120M Streams"
    val year1DataOptimizedPetabytes: String, // "18.4 PB"
    val year1NetFinancialSavingsUsd: String, // "$34.8M USD"
    val year1RoiPercentage: String, // "485%"
    val year3StreamsCertified: String, // "1.4B Streams"
    val year3DataOptimizedPetabytes: String, // "340 PB"
    val year3NetFinancialSavingsUsd: String, // "$215.6M USD"
    val year3RoiPercentage: String, // "1,140%"
    val paybackPeriodMonths: String, // "3.8 Meses"
    val benefitCostRatio: String // "5.85x"
) : Serializable

data class StrategicCrossedFeedEconomicReport(
    val reportId: String,
    val timestampIso: String,
    val overallPingStatus: String,
    val averageLatencyMs: Long,
    val pingMetrics: List<PlatformPingItem>,
    val feedData: EconomicFeedData,
    val platformBenefits: List<DirectPlatformBenefit>,
    val businessOptions: List<BusinessOptionItem>,
    val scalabilityAndRoi: ScalabilityAndRoiMetrics,
    val cryptographicSealSha256: String,
    val berneArt52ProtocolNotice: String
) : Serializable
