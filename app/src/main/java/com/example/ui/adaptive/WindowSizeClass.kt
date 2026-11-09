package com.example.ui.adaptive

import android.content.res.Configuration
import android.view.KeyEvent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Breakpoints oficiales de Window Size Classes (Material Design 3).
 */
enum class WindowWidthSizeClass {
    COMPACT, // < 600dp (Smartphones verticales)
    MEDIUM,  // 600dp ..< 840dp (Tablets compactas, plegables desplegados, móviles apaisados)
    EXPANDED // >= 840dp (Tablets Pro, Smart TVs, Desktops, ChromeOS)
}

enum class WindowHeightSizeClass {
    COMPACT, // < 480dp
    MEDIUM,  // 480dp ..< 900dp
    EXPANDED // >= 900dp
}

enum class DevicePosture {
    PHONE_PORTRAIT,
    PHONE_LANDSCAPE,
    TABLET_STUDIO,
    SMART_TV_10_FOOT
}

data class WindowAdaptiveInfo(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass,
    val widthDp: Dp,
    val heightDp: Dp,
    val posture: DevicePosture,
    val isTvMode: Boolean,
    val isTabletOrExpanded: Boolean,
    val useNavigationRail: Boolean
)

val LocalWindowAdaptiveInfo = staticCompositionLocalOf<WindowAdaptiveInfo> {
    error("LocalWindowAdaptiveInfo no inicializado")
}

/**
 * Proveedor de Breakpoints Adaptativos basado en BoxWithConstraints.
 * Permite que cualquier elemento de la app tome decisiones canónicas
 * según el tamaño real de ventana disponible.
 */
@Composable
fun ProvideWindowAdaptiveInfo(
    forceTvMode: Boolean = false,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth
        val height = maxHeight

        val widthClass = when {
            width < 600.dp -> WindowWidthSizeClass.COMPACT
            width < 840.dp -> WindowWidthSizeClass.MEDIUM
            else -> WindowWidthSizeClass.EXPANDED
        }

        val heightClass = when {
            height < 480.dp -> WindowHeightSizeClass.COMPACT
            height < 900.dp -> WindowHeightSizeClass.MEDIUM
            else -> WindowHeightSizeClass.EXPANDED
        }

        val isTv = forceTvMode || (width >= 840.dp && height >= 500.dp && width.value / height.value >= 1.6f)

        val posture = when {
            isTv -> DevicePosture.SMART_TV_10_FOOT
            widthClass == WindowWidthSizeClass.EXPANDED || (widthClass == WindowWidthSizeClass.MEDIUM && height >= 600.dp) -> DevicePosture.TABLET_STUDIO
            width > height -> DevicePosture.PHONE_LANDSCAPE
            else -> DevicePosture.PHONE_PORTRAIT
        }

        val adaptiveInfo = remember(widthClass, heightClass, width, height, posture, isTv) {
            WindowAdaptiveInfo(
                widthSizeClass = widthClass,
                heightSizeClass = heightClass,
                widthDp = width,
                heightDp = height,
                posture = posture,
                isTvMode = isTv,
                isTabletOrExpanded = widthClass == WindowWidthSizeClass.EXPANDED || posture == DevicePosture.TABLET_STUDIO,
                useNavigationRail = widthClass != WindowWidthSizeClass.COMPACT && !isTv
            )
        }

        CompositionLocalProvider(LocalWindowAdaptiveInfo provides adaptiveInfo) {
            content()
        }
    }
}

/**
 * Modificador para soporte de control remoto (D-Pad TV Remote) y anillos de foco de alta visibilidad.
 * Dibuja un borde de resplandor (Glowing Focus Ring) cuando el elemento tiene el foco del D-Pad o teclado.
 */
fun Modifier.tvFocusableRing(
    focusedBorderColor: Color = Color(0xFF38BDF8),
    focusedGlowColor: Color = Color(0xFF0284C7).copy(alpha = 0.5f),
    cornerRadius: Dp = 10.dp,
    onDpadEnter: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    this
        .focusable(interactionSource = interactionSource)
        .onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyUp) {
                when (keyEvent.key.nativeKeyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        onDpadEnter?.invoke()
                        true
                    }
                    else -> false
                }
            } else false
        }
        .drawWithContent {
            drawContent()
            if (isFocused) {
                // Borde de enfoque de alta visibilidad para TV / 10-Foot UI
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(focusedBorderColor, focusedGlowColor)
                    ),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                    style = Stroke(width = 3.5.dp.toPx())
                )
            }
        }
}
