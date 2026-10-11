package zed.rainxch.core.presentation.personality.classic

import androidx.compose.ui.graphics.Color
import zed.rainxch.core.domain.model.appearance.AccentId
import zed.rainxch.core.presentation.personality.model.PersonalityColors

private data class ClassicPrimary(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
)

private fun classicPrimary(accent: AccentId, dark: Boolean): ClassicPrimary =
    when (accent) {
        // Blue (default)
        AccentId.COBALT ->
            if (dark) ClassicPrimary(Color(0xFF8FB0FF), Color(0xFF0A1F5C), Color(0xFF2A4396), Color(0xFFDCE6FF))
            else ClassicPrimary(Color(0xFF3F6AE8), Color(0xFFFFFFFF), Color(0xFFDCE6FF), Color(0xFF0E2A78))

        // Red
        AccentId.CRIMSON ->
            if (dark) ClassicPrimary(Color(0xFFFF9A9E), Color(0xFF5C0A12), Color(0xFF8F2230), Color(0xFFFFDDDE))
            else ClassicPrimary(Color(0xFFD63A40), Color(0xFFFFFFFF), Color(0xFFFFDDDE), Color(0xFF5A0A10))

        // Gold
        AccentId.SUN ->
            if (dark) ClassicPrimary(Color(0xFFF7C45C), Color(0xFF3A2800), Color(0xFF7A5400), Color(0xFFFFE9C2))
            else ClassicPrimary(Color(0xFFE3A01C), Color(0xFF2B1A00), Color(0xFFFFE9C2), Color(0xFF3A2400))

        // Green
        AccentId.FROST ->
            if (dark) ClassicPrimary(Color(0xFF6BE3B0), Color(0xFF03281A), Color(0xFF146A49), Color(0xFFD3F5E6))
            else ClassicPrimary(Color(0xFF118553), Color(0xFFFFFFFF), Color(0xFFD3F5E6), Color(0xFF04301F))

        // Graphite
        AccentId.MONO ->
            if (dark) ClassicPrimary(Color(0xFFC9D0DF), Color(0xFF1F2736), Color(0xFF3B455B), Color(0xFFE3E7EF))
            else ClassicPrimary(Color(0xFF4B5568), Color(0xFFFFFFFF), Color(0xFFE3E7EF), Color(0xFF1B2333))
    }

fun classicAccentSwatch(accent: AccentId, dark: Boolean): Pair<Color, Color> =
    classicPrimary(accent, dark).let { it.primary to it.onPrimary }

private data class ClassicNeutral(
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val outline: Color,
    val outlineVariant: Color,
    val error: Color,
    val onError: Color,
)

private val LightNeutral =
    ClassicNeutral(
        background = Color(0xFFEAF0FA),
        onBackground = Color(0xFF1B2333),
        surface = Color(0xFFF9FBFF),
        onSurface = Color(0xFF1B2333),
        surfaceVariant = Color(0xFFDFE7F5),
        onSurfaceVariant = Color(0xFF55627C),
        surfaceContainer = Color(0xFFF1F5FC),
        surfaceContainerHigh = Color(0xFFE6EDF8),
        outline = Color(0xFFA9B6D1),
        outlineVariant = Color(0xFFD3DCEE),
        error = Color(0xFFD63A40),
        onError = Color(0xFFFFFFFF),
    )

private val DarkNeutral =
    ClassicNeutral(
        background = Color(0xFF111725),
        onBackground = Color(0xFFE7ECF8),
        surface = Color(0xFF1A2133),
        onSurface = Color(0xFFE7ECF8),
        surfaceVariant = Color(0xFF242D45),
        onSurfaceVariant = Color(0xFFA7B2CB),
        surfaceContainer = Color(0xFF161C2C),
        surfaceContainerHigh = Color(0xFF222A40),
        outline = Color(0xFF6A7696),
        outlineVariant = Color(0xFF2D3855),
        error = Color(0xFFFF8A8F),
        onError = Color(0xFF4A0A10),
    )

private val AmoledNeutral =
    DarkNeutral.copy(
        background = Color(0xFF000000),
        surface = Color(0xFF0A0E18),
        surfaceVariant = Color(0xFF151B2B),
        surfaceContainer = Color(0xFF05080F),
        surfaceContainerHigh = Color(0xFF101626),
        outlineVariant = Color(0xFF202A44),
    )

fun classicColors(
    dark: Boolean,
    amoled: Boolean = false,
    accent: AccentId = AccentId.COBALT,
): PersonalityColors {
    val primary = classicPrimary(accent, dark)
    val neutral =
        when {
            !dark -> LightNeutral
            amoled -> AmoledNeutral
            else -> DarkNeutral
        }
    return PersonalityColors(
        primary = primary.primary,
        onPrimary = primary.onPrimary,
        primaryContainer = primary.primaryContainer,
        onPrimaryContainer = primary.onPrimaryContainer,
        background = neutral.background,
        onBackground = neutral.onBackground,
        surface = neutral.surface,
        onSurface = neutral.onSurface,
        surfaceVariant = neutral.surfaceVariant,
        onSurfaceVariant = neutral.onSurfaceVariant,
        surfaceContainer = neutral.surfaceContainer,
        surfaceContainerHigh = neutral.surfaceContainerHigh,
        outline = neutral.outline,
        outlineVariant = neutral.outlineVariant,
        error = neutral.error,
        onError = neutral.onError,
        shadow = Color(0xFF000000),
        scrim = Color(0xFF000000),
        screentoneOpacity = 0f,
        gridOpacity = 0f,
    )
}
