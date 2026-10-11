package zed.rainxch.core.presentation.personality.clay

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.model.PersonalityColors
import kotlin.math.min
import kotlin.math.pow

/*
 * "Clay" look: soft, light 3D surfaces.
 *
 * Everything here is plain Compose drawing (no blur, no off-screen layers, no platform APIs), so it
 * stays cheap in long lists:
 *  - a soft cast shadow (bottom-right) and a soft glow (top-left), built from a few stacked,
 *    translucent rounded rectangles,
 *  - a gently inflated face (light top, true colour in the middle, a touch deeper at the bottom),
 *  - a thin bevel rim (light on the top-left edge, shade on the bottom-right edge).
 * Shadows/glow are drawn behind the node and are not clipped; the face and rim are clipped to the shape.
 */

/** Light/shade colours that make a surface look like soft clay, derived from the active palette. */
@Immutable
class ClayTones(
    val dark: Boolean,
    val glow: Color,
    val shade: Color,
    val rimLight: Color,
    val rimShade: Color,
)

fun clayTones(colors: PersonalityColors): ClayTones =
    if (colors.isDark) {
        ClayTones(
            dark = true,
            glow = Color(0xFFB8C8FF).copy(alpha = 0.075f),
            shade = Color(0xFF000000).copy(alpha = 0.58f),
            rimLight = Color(0xFFFFFFFF).copy(alpha = 0.15f),
            rimShade = Color(0xFF000000).copy(alpha = 0.38f),
        )
    } else {
        ClayTones(
            dark = false,
            glow = Color(0xFFFFFFFF).copy(alpha = 0.95f),
            shade = Color(0xFF5E73A3).copy(alpha = 0.42f),
            rimLight = Color(0xFFFFFFFF).copy(alpha = 0.95f),
            rimShade = Color(0xFF5E73A3).copy(alpha = 0.24f),
        )
    }

@Composable
fun rememberClayTones(): ClayTones {
    val colors = LocalPersonality.current.colors
    return remember(colors) { clayTones(colors) }
}

/** Reads a 0..1 press progress in the draw phase (so pressing never recomposes anything). */
internal val NoPress: () -> Float = { 0f }

private const val ShadowLayers = 6

private fun DrawScope.softRoundRect(
    color: Color,
    radius: Float,
    blur: Float,
    dx: Float,
    dy: Float,
    fade: Float,
) {
    val total = (color.alpha * fade).coerceIn(0f, 0.95f)
    if (total <= 0.004f || blur <= 0.5f) return
    val perLayer = 1f - (1f - total).pow(1f / ShadowLayers)
    val layerColor = color.copy(alpha = perLayer)
    for (i in 0 until ShadowLayers) {
        val grow = blur * (1f - i.toFloat() / ShadowLayers)
        drawRoundRect(
            color = layerColor,
            topLeft = Offset(dx - grow, dy - grow),
            size = Size(size.width + grow * 2f, size.height + grow * 2f),
            cornerRadius = CornerRadius(radius + grow),
        )
    }
}

private fun DrawScope.clayRadius(corner: Dp): Float = min(corner.toPx(), min(size.width, size.height) / 2f)

private fun DrawScope.drawClayShadows(
    tones: ClayTones,
    radius: Float,
    depthPx: Float,
    fade: Float,
) {
    if (depthPx <= 0.5f) return
    softRoundRect(tones.shade, radius, blur = depthPx * 1.7f, dx = depthPx * 0.45f, dy = depthPx * 0.8f, fade = fade)
    softRoundRect(tones.glow, radius, blur = depthPx * 1.2f, dx = -depthPx * 0.5f, dy = -depthPx * 0.6f, fade = fade)
}

private fun DrawScope.drawClayFace(
    face: Color,
    tones: ClayTones,
    radius: Float,
) {
    val top = lerp(face, Color.White, if (tones.dark) 0.06f else 0.55f)
    val bottom = lerp(face, Color(0xFF5E73A3), if (tones.dark) 0.0f else 0.07f)
    drawRoundRect(
        brush = Brush.verticalGradient(colors = listOf(top, face, bottom)),
        cornerRadius = CornerRadius(radius),
        size = size,
    )
    drawRoundRect(
        brush =
            Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = if (tones.dark) 0.07f else 0.5f), Color.White.copy(alpha = 0f)),
                center = Offset(size.width * 0.22f, size.height * 0.12f),
                radius = size.maxDimension * 0.75f,
            ),
        cornerRadius = CornerRadius(radius),
        size = size,
    )
}

private fun DrawScope.drawClayRim(
    tones: ClayTones,
    radius: Float,
    stroke: Float,
) {
    val inset = stroke / 2f
    drawRoundRect(
        brush =
            Brush.linearGradient(
                colors = listOf(tones.rimLight, tones.rimLight.copy(alpha = 0f), tones.rimShade),
                start = Offset.Zero,
                end = Offset(size.width, size.height),
            ),
        topLeft = Offset(inset, inset),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius((radius - inset).coerceAtLeast(0f)),
        style = Stroke(width = stroke),
    )
}

/**
 * A raised, inflated clay slab (cards, panels, tiles). [face] is the clay colour; [depth] is how far
 * it floats above the page (0 = flat). [press] (0..1) squishes it while pressed.
 */
fun Modifier.clayRaised(
    corner: Dp,
    face: Color,
    tones: ClayTones,
    depth: Dp = 7.dp,
    press: () -> Float = NoPress,
): Modifier =
    this
        .drawBehind {
            val p = press()
            drawClayShadows(
                tones = tones,
                radius = clayRadius(corner),
                depthPx = depth.toPx() * (1f - 0.4f * p),
                fade = 1f - 0.4f * p,
            )
        }.clip(RoundedCornerShape(corner))
        .drawWithContent {
            val radius = clayRadius(corner)
            drawClayFace(face = face, tones = tones, radius = radius)
            drawContent()
            drawClayRim(tones = tones, radius = radius, stroke = 1.6.dp.toPx())
        }

/**
 * Soft glossy overlay + shadows for controls that already paint their own container
 * (e.g. Material buttons): only the cast shadow behind and a light rim/sheen on top are added.
 */
fun Modifier.clayControl(
    corner: Dp,
    tones: ClayTones,
    depth: Dp = 5.dp,
    enabled: Boolean = true,
): Modifier =
    this
        .drawBehind {
            if (enabled) {
                drawClayShadows(tones = tones, radius = clayRadius(corner), depthPx = depth.toPx(), fade = 1f)
            }
        }.drawWithContent {
            drawContent()
            if (enabled) {
                val radius = clayRadius(corner)
                drawRoundRect(
                    brush =
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = if (tones.dark) 0.14f else 0.26f), Color.White.copy(alpha = 0f)),
                            endY = size.height * 0.55f,
                        ),
                    cornerRadius = CornerRadius(radius),
                    size = size,
                )
                drawClayRim(tones = tones, radius = radius, stroke = 1.4.dp.toPx())
            }
        }

/** A pressed-in clay well (text fields, wells behind controls). */
fun Modifier.clayInset(
    corner: Dp,
    face: Color,
    tones: ClayTones,
): Modifier =
    this
        .clip(RoundedCornerShape(corner))
        .drawWithContent {
            val radius = clayRadius(corner)
            drawRoundRect(color = face, cornerRadius = CornerRadius(radius), size = size)
            val stroke = 3.dp.toPx()
            val inset = stroke / 2f
            drawRoundRect(
                brush =
                    Brush.linearGradient(
                        colors = listOf(tones.shade, tones.shade.copy(alpha = 0f), tones.glow),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height),
                    ),
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke),
                cornerRadius = CornerRadius((radius - inset).coerceAtLeast(0f)),
                style = Stroke(width = stroke),
            )
            drawContent()
        }

/** Page backdrop: a very soft vertical wash so clay slabs have something to float above. */
fun clayBackdrop(colors: PersonalityColors): Brush {
    val top = lerp(colors.background, Color.White, if (colors.isDark) 0.035f else 0.45f)
    val bottom =
        if (colors.isDark) {
            lerp(colors.background, Color.Black, 0.25f)
        } else {
            lerp(colors.background, Color(0xFF5E73A3), 0.06f)
        }
    return Brush.verticalGradient(colors = listOf(top, colors.background, bottom))
}
