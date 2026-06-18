package af.market.nerkhtimes.ui.theme.components

import af.market.nerkhtimes.data.model.MarketItem
import af.market.nerkhtimes.ui.theme.categoryAccentColor
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ItemCard(
    item: MarketItem,
    modifier: Modifier = Modifier,
    nf: NumberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
) {
    val name        = item.name_ps.trim().ifBlank { item.key.uppercase() }
    val unit        = item.unit_ps.trim().ifBlank { "—" }
    val accentColor = categoryAccentColor(item.group)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape    = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        // IntrinsicSize.Min lets the accent bar stretch to the full row height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Leading accent bar (appears on right in RTL — the natural reading edge)
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment  = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price — start side (visually right in RTL)
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text      = nf.format(item.price),
                        style     = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color     = accentColor,
                        maxLines  = 1
                    )
                    Text(
                        text  = "؋ AFN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Name + unit — end side (visually left in RTL)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text      = name,
                        style     = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.End,
                        maxLines  = 2,
                        overflow  = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text      = unit,
                        style     = MaterialTheme.typography.bodySmall,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        maxLines  = 1
                    )
                }
            }
        }
    }
}
