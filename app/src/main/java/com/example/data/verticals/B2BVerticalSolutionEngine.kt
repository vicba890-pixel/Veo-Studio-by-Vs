package com.example.data.verticals

import java.io.Serializable

/**
 * Soluciones Verticales de Alto Valor (B2B sin intermediarios)
 * Generación especializada para Real Estate, E-Commerce y EdTech Corporativo.
 */

enum class B2BVerticalCategory(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val badge: String,
    val typicalArrRoi: String
) {
    REAL_ESTATE(
        title = "Bienes Raíces & Hospitality 4K",
        subtitle = "Recorridos cinemáticos 4K de propiedades, planos y voz multilingüe internacional.",
        iconEmoji = "🏛️",
        badge = "4K ARCHITECTURAL VIRTUAL TOUR",
        typicalArrRoi = "Ahorra $4,500 USD por producción audiovisual inmobiliaria"
    ),
    ECOMMERCE_RETAIL(
        title = "E-Commerce & Retail Dinámico",
        subtitle = "Videos de alta conversión sincronizados con inventario, precios y flash-promos.",
        iconEmoji = "🛍️",
        badge = "DYNAMIC SHOPPING CONVERSION",
        typicalArrRoi = "+340% ROAS en TikTok Shop, Instagram Reels y YouTube Shorts"
    ),
    EDTECH_CORPORATE(
        title = "Capacitación Corporativa & Micro-learning",
        subtitle = "Conversión de manuales operativos y PDFs en módulos de video con checkpoints interactivos.",
        iconEmoji = "🎓",
        badge = "INTERACTIVE COMPLIANCE TRAINING",
        typicalArrRoi = "Reduce tiempo de onboarding corporativo de 2 semanas a 35 minutos"
    )
}

data class VerticalTemplatePreset(
    val id: String,
    val category: B2BVerticalCategory,
    val name: String,
    val durationSec: Int,
    val targetAspectRatio: String,
    val defaultPrompt: String,
    val storyboardStructure: List<String>,
    val onScreenOverlays: List<String>,
    val audioVoiceLanguages: List<String>,
    val safeZonePreset: String
) : Serializable

object B2BVerticalRegistry {

    val presets: List<VerticalTemplatePreset> = listOf(
        VerticalTemplatePreset(
            id = "real_estate_villa",
            category = B2BVerticalCategory.REAL_ESTATE,
            name = "Luxury Villa & Penthouse Showcase 4K",
            durationSec = 30,
            targetAspectRatio = "16:9",
            defaultPrompt = "Cinematic 4K architectural drone flythrough entering a luxury modern minimalist villa at golden hour, marble floors, infinity pool reflecting dusk sky, high ceilings, warm interior lighting, photorealistic architectural digest quality.",
            storyboardStructure = listOf(
                "Toma 1 (0-6s): Vuelo panorámico de dron con revelación de fachada y piscina infinity",
                "Toma 2 (7-15s): Transición fluida al salón principal de doble altura con luz natural",
                "Toma 3 (16-23s): Suite principal con terraza privada y vista al océano",
                "Toma 4 (24-30s): Plano crepuscular con llamada a la acción y datos de contacto"
            ),
            onScreenOverlays = listOf(
                "Superficie: 450 m² construidos",
                "4 Suites de lujo • 5 Baños",
                "Certificación Energética: Clase A+",
                "Precio: Consultar Dossier Privado"
            ),
            audioVoiceLanguages = listOf("Español (Castellano/Neutro)", "English (US Cinema)", "Français (Parisien)", "Deutsch", "Mandarin (Putonghua)"),
            safeZonePreset = "youtube_16_9"
        ),
        VerticalTemplatePreset(
            id = "ecom_flash_drop",
            category = B2BVerticalCategory.ECOMMERCE_RETAIL,
            name = "Drop Viral de Producto (TikTok / Reels 9:16)",
            durationSec = 15,
            targetAspectRatio = "9:16",
            defaultPrompt = "Dynamic high-energy macro commercial of premium titanium chronometer watch, water splashes and neon reflections, rapid 360 degree turntable spin, studio lighting, hyper-sharp details, ultra-high engagement pacing.",
            storyboardStructure = listOf(
                "Gancho 0-3s: Macro explosivo del producto con sonido ASMR de alto impacto",
                "Beneficio 4-8s: Demostración de resistencia al agua y materiales aeroespaciales",
                "Prueba Social 9-12s: Texto dinámico de calificación 4.9 estrellas y stock limitado",
                "CTA 13-15s: Botón de compra y descuento flash del 30% activado"
            ),
            onScreenOverlays = listOf(
                "⚡ OFERTA RELÁMPAGO: -30% HOY",
                "ÚLTIMAS 14 UNIDADES EN STOCK",
                "ENVÍO GRATUITO EN 24 HORAS",
                "COMPRA PROTEGIDA 100% GARANTIZADA"
            ),
            audioVoiceLanguages = listOf("Español (Enérgico Comercial)", "English (Hype Viral)"),
            safeZonePreset = "tiktok_9_16"
        ),
        VerticalTemplatePreset(
            id = "edtech_compliance_module",
            category = B2BVerticalCategory.EDTECH_CORPORATE,
            name = "Micro-Módulo de Ciberseguridad & Compliance",
            durationSec = 45,
            targetAspectRatio = "16:9",
            defaultPrompt = "Polished modern corporate educational explainer video, 3D animated security shield, clean typography cards, cybersecurity enterprise incident prevention, minimalist tech aesthetic, crisp infographics.",
            storyboardStructure = listOf(
                "Introducción (0-10s): Planteamiento del riesgo de phishing y seguridad de datos",
                "Protocolo (11-25s): 3 pasos obligatorios de verificación de doble factor",
                "Simulación (26-38s): Caso práctico y cómo reportar un correo sospechoso",
                "Evaluación (39-45s): Checkpoint interactivo con pregunta de validación"
            ),
            onScreenOverlays = listOf(
                "Paso 1: Verificar dominio del remitente",
                "Paso 2: Nunca ingresar credenciales por enlace directo",
                "Paso 3: Notificar al equipo SOC en un clic",
                "✓ Checkpoint de Cumplimiento ISO 27001"
            ),
            audioVoiceLanguages = listOf("Español (Corporativo Claro)", "English (Professional Executive)"),
            safeZonePreset = "linkedin_1_1"
        )
    )

    fun getPresetById(id: String): VerticalTemplatePreset {
        return presets.firstOrNull { it.id == id } ?: presets.first()
    }
}
