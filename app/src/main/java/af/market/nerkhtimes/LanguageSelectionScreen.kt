package af.market.nerkhtimes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class LangOption(
    val code: String,
    /** Native script name — always shown as-is regardless of current UI locale. */
    val nativeName: String,
    /** English subtitle shown under the native name. */
    val englishLabel: String
)

private val LANGUAGES = listOf(
    LangOption(LanguageManager.LANG_EN, "English",    "English"),
    LangOption(LanguageManager.LANG_PS, "پښتو",       "Pashto"),
    LangOption(LanguageManager.LANG_FA, "دری / فارسی","Dari / Farsi")
)

/**
 * Full-screen language picker.
 *
 * @param currentCode   The already-saved language code (null on first launch).
 * @param isFirstLaunch When true, back navigation is blocked — selection is mandatory.
 * @param onLanguageSelected Called with the chosen code; caller is responsible for
 *                           saving it and restarting the Activity.
 */
@Composable
fun LanguageSelectionScreen(
    currentCode: String?,
    isFirstLaunch: Boolean,
    onLanguageSelected: (String) -> Unit
) {
    // Block system-back on first launch — selection is mandatory
    BackHandler(enabled = isFirstLaunch) { /* intentionally empty */ }

    var picked by remember { mutableStateOf(currentCode) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Header ────────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.lang_screen_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.lang_screen_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // ── Language cards ─────────────────────────────────────────────────
            LANGUAGES.forEach { lang ->
                LanguageCard(
                    option = lang,
                    selected = picked == lang.code,
                    onClick = { picked = lang.code }
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))

            // ── Confirm button ─────────────────────────────────────────────────
            Button(
                onClick = { picked?.let { onLanguageSelected(it) } },
                enabled = picked != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = stringResource(R.string.lang_confirm),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun LanguageCard(option: LangOption, selected: Boolean, onClick: () -> Unit) {
    val accentColor  = MaterialTheme.colorScheme.primary
    val containerBg  = if (selected) MaterialTheme.colorScheme.primaryContainer
                       else MaterialTheme.colorScheme.surfaceVariant

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .then(
                if (selected) Modifier.border(
                    width = 2.dp,
                    color = accentColor,
                    shape = MaterialTheme.shapes.large
                ) else Modifier
            )
            .clickable(onClick = onClick),
        color = containerBg,
        shape = MaterialTheme.shapes.large,
        tonalElevation = if (selected) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.nativeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (option.code != LanguageManager.LANG_EN) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = option.englishLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            // Selection indicator
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = accentColor
                    ) {}
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
        }
    }
}
