package af.market.nerkhtimes.ui.theme.components

import af.market.nerkhtimes.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class CityOption(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPicker(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cities = remember {
        listOf(
            CityOption("kabul", "کابل"),
            CityOption("kandahar", "کندهار"),
            CityOption("mazar", "مزار شریف"),
            CityOption("herat", "هرات"),
            CityOption("jalalabad", "جلال اباد"),
            CityOption("peshawar", "پېښور")
        )
    }

    var expanded by remember { mutableStateOf(false) }

    val selected = cities.firstOrNull { it.id == selectedId } ?: cities.first()
    LaunchedEffect(selectedId) {
        if (cities.none { it.id == selectedId }) onSelect(cities.first().id)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(stringResource(R.string.city_picker_label)) },
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(61.dp),
            maxLines = 1
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = city.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(city.id)
                    }
                )
            }
        }
    }
}
