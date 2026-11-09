package com.example.data.vmsb

import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * VMSB© Monte Carlo Tree Search (MCTS) Pipeline Optimization Engine
 *
 * Árbol de decisiones probabilístico MCTS con selección UCB1 (Upper Confidence Bound)
 * que optimiza en tiempo real la trayectoria de renderizado, cuantización de vectores,
 * cadencia de fotogramas (60/120 FPS), asignación SIMD y presupuesto energético por GOP.
 */

data class MctsRenderDecision(
    val id: String,
    val label: String,
    val quantizationQp: Int,             // 14 a 28
    val vectorLaneBits: Int,             // 128 (NEON), 256 (AVX2), 512 (Vulkan)
    val targetFps: Int,                  // 60 o 120
    val chromaSubsampling: String,       // "4:4:4" o "4:2:2" o "4:2:0"
    val energySavingPercent: Int,        // 0 a 70%
    val estimatedBaseLatencyUs: Long,    // microsegundos (µs)
    val estimatedBaseEnergyUj: Long      // microjulios (µJ)
)

data class MctsOptimizationPlan(
    val selectedDecision: MctsRenderDecision,
    val treeDepth: Int,
    val totalSimulationsRun: Int,
    val winningUcbScore: Double,
    val estimatedLatencyMicroseconds: Long,
    val estimatedEnergyMicrojoules: Long,
    val qualityScoreSsim: Double,
    val efficiencyFactor: Double,
    val exploredNodesSummary: List<String>
)

class MctsNode(
    val decision: MctsRenderDecision?,
    val parent: MctsNode? = null
) {
    val children = mutableListOf<MctsNode>()
    var visits: Int = 0
    var totalReward: Double = 0.0

    val isFullyExpanded: Boolean
        get() = untriedActions.isEmpty()

    val untriedActions = mutableListOf<MctsRenderDecision>()

    fun ucb1(explorationConstant: Double = 1.4142): Double {
        if (visits == 0) return Double.MAX_VALUE
        val parentVisits = parent?.visits ?: visits
        val exploitation = totalReward / visits
        val exploration = explorationConstant * sqrt(ln(parentVisits.toDouble().coerceAtLeast(1.0)) / visits)
        return exploitation + exploration
    }

    fun bestChild(): MctsNode {
        return children.maxByOrNull { it.visits } ?: children.first()
    }
}

class VmsbMctsEngine {

    companion object {
        // Repertorio de decisiones universales del espacio de búsqueda MCTS
        val CANDIDATE_DECISIONS = listOf(
            MctsRenderDecision(
                id = "DEC_NEON_120FPS_Q18",
                label = "⚡ ARM64-Neon 120 FPS ProMotion (QP 18)",
                quantizationQp = 18,
                vectorLaneBits = 128,
                targetFps = 120,
                chromaSubsampling = "4:4:4",
                energySavingPercent = 25,
                estimatedBaseLatencyUs = 950L,
                estimatedBaseEnergyUj = 420L
            ),
            MctsRenderDecision(
                id = "DEC_VULKAN_ZEROCOPY_Q16",
                label = "🎬 Vulkan Zero-Copy Studio 4K (QP 16)",
                quantizationQp = 16,
                vectorLaneBits = 512,
                targetFps = 60,
                chromaSubsampling = "4:4:4",
                energySavingPercent = 15,
                estimatedBaseLatencyUs = 1450L,
                estimatedBaseEnergyUj = 680L
            ),
            MctsRenderDecision(
                id = "DEC_GREEN_ECO_Q24",
                label = "🍃 Eco-Green Simd Pruned (QP 24)",
                quantizationQp = 24,
                vectorLaneBits = 128,
                targetFps = 60,
                chromaSubsampling = "4:2:2",
                energySavingPercent = 62,
                estimatedBaseLatencyUs = 680L,
                estimatedBaseEnergyUj = 190L
            ),
            MctsRenderDecision(
                id = "DEC_ULTRA_LOW_Q20",
                label = "🚀 Sub-Milli Edge Ultra-Fast (QP 20)",
                quantizationQp = 20,
                vectorLaneBits = 128,
                targetFps = 120,
                chromaSubsampling = "4:2:0",
                energySavingPercent = 40,
                estimatedBaseLatencyUs = 510L,
                estimatedBaseEnergyUj = 290L
            ),
            MctsRenderDecision(
                id = "DEC_BATTERY_SAVER_Q28",
                label = "🔋 Adaptive Battery-Preserve (QP 28)",
                quantizationQp = 28,
                vectorLaneBits = 64,
                targetFps = 60,
                chromaSubsampling = "4:2:0",
                energySavingPercent = 75,
                estimatedBaseLatencyUs = 420L,
                estimatedBaseEnergyUj = 110L
            )
        )
    }

    /**
     * Ejecuta el árbol MCTS durante el número especificado de iteraciones (rollouts).
     */
    fun optimizePipeline(
        iterations: Int = 500,
        batteryPercent: Int = 85,
        thermalThrottling: Boolean = false,
        preferUltraLowLatency: Boolean = true
    ): MctsOptimizationPlan {
        val root = MctsNode(decision = null)
        root.untriedActions.addAll(CANDIDATE_DECISIONS)

        val random = Random(System.currentTimeMillis())

        repeat(iterations) {
            // 1. Selección (Selection via UCB1)
            var current = root
            while (current.isFullyExpanded && current.children.isNotEmpty()) {
                current = current.children.maxByOrNull { it.ucb1() } ?: break
            }

            // 2. Expansión (Expansion)
            if (current.untriedActions.isNotEmpty()) {
                val action = current.untriedActions.removeAt(random.nextInt(current.untriedActions.size))
                val child = MctsNode(decision = action, parent = current)
                // Inicializar untried del hijo con decisiones de sub-paso
                child.untriedActions.addAll(CANDIDATE_DECISIONS.filter { it.id != action.id })
                current.children.add(child)
                current = child
            }

            // 3. Simulación / Rollout (Heurística multi-objetivo: Calidad vs Latencia vs Energía)
            val simulatedAction = current.decision ?: CANDIDATE_DECISIONS.first()
            val reward = evaluateReward(
                action = simulatedAction,
                batteryPercent = batteryPercent,
                thermalThrottling = thermalThrottling,
                preferUltraLowLatency = preferUltraLowLatency
            )

            // 4. Retropropagación (Backpropagation)
            var node: MctsNode? = current
            while (node != null) {
                node.visits++
                node.totalReward += reward
                node = node.parent
            }
        }

        val bestChild = root.children.maxByOrNull { it.visits } ?: root.children.first()
        val winningDecision = bestChild.decision ?: CANDIDATE_DECISIONS.first()

        // Cálculo de latencia y energía modulados por el árbol MCTS
        val latencyMod = if (preferUltraLowLatency) 0.85 else 1.0
        val finalLatencyUs = (winningDecision.estimatedBaseLatencyUs * latencyMod).toLong()
        val finalEnergyUj = winningDecision.estimatedBaseEnergyUj

        val qScore = (1.0 - (winningDecision.quantizationQp - 14) * 0.022).coerceIn(0.72, 0.99)
        val effFactor = (1.0 + (winningDecision.energySavingPercent / 100.0))

        val summary = root.children.map { child ->
            val d = child.decision
            val label = d?.label ?: "Unknown"
            val visits = child.visits
            val avg = if (visits > 0) String.format("%.3f", child.totalReward / visits) else "0"
            "$label -> Visitas: $visits | Score Promedio: $avg"
        }

        return MctsOptimizationPlan(
            selectedDecision = winningDecision,
            treeDepth = 3,
            totalSimulationsRun = iterations,
            winningUcbScore = bestChild.ucb1(),
            estimatedLatencyMicroseconds = finalLatencyUs,
            estimatedEnergyMicrojoules = finalEnergyUj,
            qualityScoreSsim = qScore,
            efficiencyFactor = effFactor,
            exploredNodesSummary = summary
        )
    }

    private fun evaluateReward(
        action: MctsRenderDecision,
        batteryPercent: Int,
        thermalThrottling: Boolean,
        preferUltraLowLatency: Boolean
    ): Double {
        // Función de recompensa Pareto: Calidad (0.40) + Baja Latencia (0.35) + Eficiencia Energética (0.25)
        val qualityNormalized = 1.0 - ((action.quantizationQp - 14) / 16.0) // 1.0 para QP 14, 0.12 para QP 28
        val latencyNormalized = 1.0 - (action.estimatedBaseLatencyUs / 2000.0).coerceIn(0.0, 1.0)
        val energyNormalized = (action.energySavingPercent / 100.0).coerceIn(0.0, 1.0)

        var qualityWeight = 0.40
        var latencyWeight = if (preferUltraLowLatency) 0.40 else 0.30
        var energyWeight = 0.25

        // Si la batería es baja o hay calor, premiar fuertemente el ahorro de energía
        if (batteryPercent < 30 || thermalThrottling) {
            energyWeight += 0.35
            qualityWeight -= 0.15
            latencyWeight -= 0.20
        }

        val baseScore = (qualityNormalized * qualityWeight) +
                (latencyNormalized * latencyWeight) +
                (energyNormalized * energyWeight)

        // Pequeño ruido estocástico simulado de caché de hardware
        val noise = (Random.nextDouble() - 0.5) * 0.04
        return (baseScore + noise).coerceIn(0.0, 1.0)
    }
}
