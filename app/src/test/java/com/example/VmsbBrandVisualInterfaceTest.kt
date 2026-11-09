package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.data.camera.BerneConventionLedger
import com.example.data.vmsb.VmsbNativeCore
import com.example.ui.MainViewModel
import com.example.ui.components.vmsb.VmsbBrandVisualInterface
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VmsbBrandVisualInterfaceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test VMSB brand interface renders crest and core specs`() {
        val app = RuntimeEnvironment.getApplication()
        val viewModel = MainViewModel(app)

        composeTestRule.setContent {
            MyApplicationTheme {
                VmsbBrandVisualInterface(
                    viewModel = viewModel,
                    initiallyExpanded = true
                )
            }
        }

        // Verify the brand visual interface container is displayed
        composeTestRule.onNodeWithTag("vmsb_brand_visual_interface_card").assertIsDisplayed()

        // Verify brand texts are present
        composeTestRule.onNodeWithText("VEO STUDIO", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("by Vs", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("vmsb© NATIVE ENGINE • BERNE PROTOCOL 2026", substring = true).assertIsDisplayed()
    }

    @Test
    fun `test VMSB brand interface tabs switching and personal sail`() {
        val app = RuntimeEnvironment.getApplication()
        val viewModel = MainViewModel(app)

        composeTestRule.setContent {
            MyApplicationTheme {
                VmsbBrandVisualInterface(
                    viewModel = viewModel,
                    initiallyExpanded = true
                )
            }
        }

        // Click on "vmsb© Personal Sail" tab
        composeTestRule.onNodeWithText("vmsb© Personal Sail").performClick()
        composeTestRule.waitForIdle()

        // Verify personal sail and Berne Protocol details
        composeTestRule.onNodeWithText("vmsb© PERSONAL SAIL", substring = true).assertExists()
        composeTestRule.onNodeWithText("vicba890@gmail.com", substring = true).assertExists()
        composeTestRule.onNodeWithText("2026 INMUTABLE", substring = true).assertExists()
        composeTestRule.onNodeWithText("181 Estados Miembros de Berna", substring = true).assertExists()

        // Click on "Árbol MCTS" tab
        composeTestRule.onNodeWithText("Árbol MCTS").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("ÁRBOL ESTOCÁSTICO MCTS", substring = true).assertExists()

        // Click on "Manifiesto Vs" tab
        composeTestRule.onNodeWithText("Manifiesto Vs").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("MANIFIESTO VEO STUDIO BY VS", substring = true).assertExists()
    }

    @Test
    fun `test VMSB Native Core and Berne Protocol integration`() {
        val core = VmsbNativeCore()
        val ledger = BerneConventionLedger()

        // 1. Run MCTS Tree Search Optimization
        val plan = core.mctsEngine.optimizePipeline(iterations = 500)
        assertNotNull(plan)
        assertTrue(plan.totalSimulationsRun >= 500)
        assertTrue(plan.winningUcbScore > 0.0)

        // 2. Stream packets through Fakfa zero-copy ring buffer
        val samplePayload = "TEST_STREAM_FRAME".toByteArray()
        val packet = core.processFrame(samplePayload)
        assertNotNull(packet)
        assertEquals(samplePayload.size, packet.payloadSizeBytes)

        // 3. Seal in Berne Protocol 2026 ledger
        val block = ledger.sealMediaWork("Personal Sail Project 2026", samplePayload)
        assertNotNull(block)
        assertTrue(block.currentBlockHash.isNotEmpty())
        assertTrue(block.timestampIso.contains("2026") || block.timestampIso.contains("T"))

        // 4. Generate official copyright certificate
        val cert = ledger.generateCertificate(block)
        assertNotNull(cert)
        assertTrue(cert.universalJurisdiction.contains("Berna"))
    }
}
