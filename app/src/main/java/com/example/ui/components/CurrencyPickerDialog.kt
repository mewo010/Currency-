package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Currency
import com.example.data.model.CurrencyData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerDialog(
    title: String,
    selectedCurrency: Currency,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCurrencySelected: (Currency) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val allCurrencies = CurrencyData.currencies
    val filteredCurrencies = if (searchQuery.isBlank()) {
        allCurrencies
    } else {
        allCurrencies.filter {
            it.code.contains(searchQuery, ignoreCase = true) ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.countryCode.contains(searchQuery, ignoreCase = true)
        }
    }

    val popularCodes = listOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR")
    val popularCurrencies = allCurrencies.filter { popularCodes.contains(it.code) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("currency_picker_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close currency picker",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("currency_picker_search"),
                placeholder = { Text("Search currency name or code...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Popular Quick Chips
            if (searchQuery.isBlank()) {
                Text(
                    text = "Popular Currencies",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(popularCurrencies) { currency ->
                        val isSelected = currency.code == selectedCurrency.code
                        AssistChip(
                            onClick = {
                                onCurrencySelected(currency)
                                onDismiss()
                            },
                            label = {
                                Text("${currency.flagEmoji} ${currency.code}")
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "All Currencies (${filteredCurrencies.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Currency List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCurrencies, key = { it.code }) { currency ->
                    val isSelected = currency.code == selectedCurrency.code
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onCurrencySelected(currency)
                                onDismiss()
                            }
                            .testTag("currency_item_${currency.code}"),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Flag Box
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currency.flagEmoji,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currency.code,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currency.symbol,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = currency.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
