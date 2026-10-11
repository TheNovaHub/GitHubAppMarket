package zed.rainxch.core.presentation.components.errors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zed.rainxch.core.presentation.components.buttons.KomiButton
import zed.rainxch.core.presentation.components.buttons.KomiButtonVariant
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.technical_details_hide
import zed.rainxch.githubstore.core.presentation.res.technical_details_show

/**
 * Collapsed-by-default section that keeps the raw error available for debugging
 * without showing it to normal users.
 */
@Composable
fun TechnicalDetails(
    details: String?,
    modifier: Modifier = Modifier,
) {
    if (details.isNullOrBlank()) return
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        KomiButton(
            onClick = { expanded = !expanded },
            label =
                stringResource(
                    if (expanded) Res.string.technical_details_hide else Res.string.technical_details_show,
                ),
            variant = KomiButtonVariant.Text,
        )
        if (expanded) {
            KomiText(
                text = details,
                role = KomiTextRole.Mono,
                color = LocalPersonality.current.colors.onSurfaceVariant,
                uppercase = false,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            )
        }
    }
}
