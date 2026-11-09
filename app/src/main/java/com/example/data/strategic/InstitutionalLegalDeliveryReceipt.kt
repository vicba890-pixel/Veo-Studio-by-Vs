package com.example.data.strategic

import java.io.Serializable

data class PlatformHandshakeDetail(
    val nodeName: String,
    val endpointUrl: String,
    val latencyMs: Long,
    val statusCode: Int,
    val tlsCipher: String,
    val isVerified: Boolean
) : Serializable

data class InstitutionalLegalDeliveryReceipt(
    val receiptFolio: String,
    val timestampUtc: String,
    val addresseeOffice: String = "Office of the General Counsel & Head of AI & IP Governance",
    val addresseeOrganization: String = "Alphabet Inc. / Google LLC",
    val addresseeAddress: String = "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA",
    val communicationSubject: String = "Notificación Formal de Protocolo Operativo VMSB©, Procedencia C2PA v2.1 y Ventana Exclusiva Preferente de 30 Días",
    val exclusivityWindowDays: Int = 30,
    val exclusivityExpirationUtc: String,
    val deliveryStatus: String = "TRANSMISIÓN CERTIFICADA Y RECIBIDA EN TIEMPO REAL (HTTP 200 OK • SLA 99.99%)",
    val googleGatewayLatencyMs: Long,
    val tlsCipherNegotiated: String,
    val platformNodes: List<PlatformHandshakeDetail>,
    val sha256CryptographicSeal: String,
    val berneConventionNotice: String,
    val euAiActComplianceNotice: String
) : Serializable
