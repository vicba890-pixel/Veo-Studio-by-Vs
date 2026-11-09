package com.example.data.compliance

import java.io.Serializable

data class GlobalNormativeItem(
    val category: String, // Normativa Legal, Seguridad, Técnico, Calidad
    val title: String,
    val standardCode: String,
    val status: String, // CUMPLIDO, CERTIFICADO, ACTIVO
    val issuingBody: String, // WIPO, UE, NIST, EBU, W3C, ISO
    val scopeDescription: String,
    val measuredMetric: String
) : Serializable

data class GoogleLiveConnectivityProof(
    val endpointName: String,
    val endpointUrl: String,
    val httpStatusCode: Int,
    val latencyMs: Long,
    val isReachable: Boolean,
    val tlsCipherSuite: String,
    val serverHeader: String,
    val timestampIso: String,
    val diagnosticSummary: String
) : Serializable

data class GlobalComplianceAuditReport(
    val folioId: String,
    val timestampIso: String,
    val overallVerdict: String,
    val googleConnectivityProof: GoogleLiveConnectivityProof,
    val secondaryEndpoints: List<GoogleLiveConnectivityProof>,
    val complianceItems: List<GlobalNormativeItem>,
    val sha256CryptographicSeal: String,
    val nistPostQuantumFips204Signature: String,
    val c2paManifestRootSha256: String,
    val complianceScorePercent: Int // 100%
) : Serializable
