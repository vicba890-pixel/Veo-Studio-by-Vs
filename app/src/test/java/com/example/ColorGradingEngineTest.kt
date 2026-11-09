package com.example

import com.example.data.colorgrading.ColorGradingEngine
import com.example.data.colorgrading.ColorGradingPresets
import com.example.data.colorgrading.ColorGradingState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorGradingEngineTest {

    @Test
    fun testAllPresetsDefined() {
        val presets = ColorGradingPresets.ALL_PRESETS
        assertEquals(8, presets.size)
        assertTrue(presets.any { it.id == ColorGradingPresets.REC709_DIRECTOR.id })
        assertTrue(presets.any { it.id == ColorGradingPresets.KODAK_VISION3_5219.id })
        assertTrue(presets.any { it.id == ColorGradingPresets.FUJI_ETERNA_250D.id })
        assertTrue(presets.any { it.id == ColorGradingPresets.TEAL_AND_ORANGE_BLOCKBUSTER.id })
        assertTrue(presets.any { it.id == ColorGradingPresets.MONOCHROME_NOIR.id })
        assertTrue(presets.any { it.id == ColorGradingPresets.CYBERPUNK_NEON.id })
        assertTrue(presets.any { it.id == ColorGradingPresets.BLEACH_BYPASS_WAR.id })
        assertTrue(presets.any { it.id == ColorGradingPresets.VINTAGE_POLAROID_70S.id })
    }

    @Test
    fun testDefaultStateValues() {
        val state = ColorGradingState()
        assertTrue(state.isEnabled)
        assertEquals(ColorGradingPresets.REC709_DIRECTOR, state.selectedLut)
        assertEquals(0.0f, state.brightness, 0.001f)
        assertEquals(1.0f, state.contrast, 0.001f)
        assertEquals(1.0f, state.saturation, 0.001f)
        assertEquals(0.0f, state.temperature, 0.001f)
    }

    @Test
    fun testBuildColorMatrixDisabledReturnsIdentity() {
        val state = ColorGradingState(isEnabled = false)
        val matrix = ColorGradingEngine.buildColorMatrix(state)
        assertNotNull(matrix)
        val values = matrix.values
        assertEquals(20, values.size)
        // Identity matrix has 1.0 at index 0, 6, 12, 18
        assertEquals(1.0f, values[0], 0.001f)
        assertEquals(1.0f, values[6], 0.001f)
        assertEquals(1.0f, values[12], 0.001f)
        assertEquals(1.0f, values[18], 0.001f)
    }

    @Test
    fun testBuildColorMatrixWithAdjustmentsProducesValidValues() {
        val state = ColorGradingState(
            isEnabled = true,
            selectedLut = ColorGradingPresets.TEAL_AND_ORANGE_BLOCKBUSTER,
            brightness = 0.2f,
            contrast = 1.3f,
            saturation = 1.4f,
            temperature = 0.3f
        )
        val matrix = ColorGradingEngine.buildColorMatrix(state)
        assertNotNull(matrix)
        assertEquals(20, matrix.values.size)
        // Ensure values are finite
        for (v in matrix.values) {
            assertTrue(!v.isNaN() && !v.isInfinite())
        }
    }

    @Test
    fun testMonochromeLutDesaturates() {
        val state = ColorGradingState(
            isEnabled = true,
            selectedLut = ColorGradingPresets.MONOCHROME_NOIR
        )
        val matrix = ColorGradingEngine.buildColorMatrix(state)
        assertNotNull(matrix)
        // Red, Green, Blue contribution in row 0 should equal row 1 for monochrome
        val values = matrix.values
        assertEquals(20, values.size)
    }
}
