package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Currency
import com.example.data.model.CurrencyData
import com.example.ui.CurrencyUiState
import java.util.Locale

@Composable
fun FavoritesScreen(
    uiState: CurrencyUiState,
    onSelectPair: (Currency, Currency) -> Unit,
    onRemoveFavorite: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites = uiState.favorites

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Favorites",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Favorite Currency Pairs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Live exchange values for ${uiState.amountInput.ifBlank { "100" }} base amount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Empty favorites",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Favorite Pairs Saved",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the star icon on any conversion result to save it here for quick access.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val amount = uiState.amountInput.toDoubleOrNull() ?: 100.0

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { "${it.fromCurrency}_${it.toCurrency}" }) { fav ->
                    val fromCurr = CurrencyData.findByCode(fav.fromCurrency)
                    val toCurr = CurrencyData.findByCode(fav.toCurrency)

                    // Calculate live converted value for this pair
                    val rate = if (fav.fromCurrency == uiState.fromCurrency.code) {
                        uiState.ratesMap[fav.toCurrency] ?: 1.0
                    } else {
                        // Estimate rate from USD rates map
                        val usdRates = CurrencyData.defaultBackupRatesToUsd
                        val fromUsd = usdRates[fav.fromCurrency] ?: 1.0
                        val toUsd = usdRates[fav.toCurrency] ?: 1.0
                        toUsd / fromUsd
                    }
                    val convertedVal = amount * rate

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectPair(fromCurr, toCurr) }
                            .testTag("favorite_pair_item_${fav.fromCurrency}_${fav.toCurrency}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${fromCurr.flagEmoji} ${fromCurr.code}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "to",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .size(18.dp)
                                    )
                                    Text(
                                        text = "${toCurr.flagEmoji} ${toCurr.code}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val formattedConverted = String.format(Locale.US, "%,.2f", convertedVal)
                                Text(
                                    text = "$amount ${fromCurr.code} = $formattedConverted ${toCurr.symbol}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                val formattedRate = String.format(Locale.US, "%.4f", rate)
                                Text(
                                    text = "Rate: 1 ${fromCurr.code} = $formattedRate ${toCurr.code}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { onRemoveFavorite(fav.fromCurrency, fav.toCurrency) },
                                modifier = Modifier.testTag("delete_favorite_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remove favorite",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
