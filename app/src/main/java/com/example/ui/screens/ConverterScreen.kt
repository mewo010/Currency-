package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Currency
import com.example.data.model.CurrencyData
import com.example.ui.CurrencyPickerType
import com.example.ui.CurrencyUiState
import java.util.Locale

@Composable
fun ConverterScreen(
    uiState: CurrencyUiState,
    onAmountChange: (String) -> Unit,
    onQuickAmountSelect: (Int) -> Unit,
    onSwapCurrencies: () -> Unit,
    onDetectLocation: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenPicker: (CurrencyPickerType) -> Unit,
    onSelectPair: (Currency, Currency) -> Unit,
    onRefreshRates: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 350),
        label = "swapRotation"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_currency_hero_1784885556615),
                    contentDescription = "Currency Hero Graphic",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Global Currency Exchange",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (uiState.lastUpdatedText.isNotBlank()) "Updated: ${uiState.lastUpdatedText}" else "Live global market rates",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        IconButton(
                            onClick = onRefreshRates,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            if (uiState.isLoadingRates) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh rates",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Location Detection Card / Action Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(18.dp)
                ),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Location icon",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Detect Local Currency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Set 'To Currency' based on GPS location",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = onDetectLocation,
                    enabled = !uiState.isDetectingLocation,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("detect_location_button")
                ) {
                    if (uiState.isDetectingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Detect",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Amount Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Amount to Convert",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.amountInput,
                    onValueChange = onAmountChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    leadingIcon = {
                        Text(
                            text = uiState.fromCurrency.symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.amountInput.isNotEmpty()) {
                            IconButton(onClick = { onAmountChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear input amount"
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Amount Presets
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(10, 50, 100, 500, 1000).forEach { amount ->
                        AssistChip(
                            onClick = { onQuickAmountSelect(amount) },
                            label = { Text("${uiState.fromCurrency.symbol}$amount") },
                            shape = RoundedCornerShape(10.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (uiState.amountInput == amount.toString())
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Currency Pickers & Swap Block
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // "From" Currency Picker
                CurrencySelectorCard(
                    label = "From Currency",
                    currency = uiState.fromCurrency,
                    onClick = { onOpenPicker(CurrencyPickerType.FROM) },
                    testTag = "from_currency_selector"
                )

                // "To" Currency Picker
                CurrencySelectorCard(
                    label = "To Currency",
                    currency = uiState.toCurrency,
                    onClick = { onOpenPicker(CurrencyPickerType.TO) },
                    testTag = "to_currency_selector"
                )
            }

            // Swap FAB Button floating in the center
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        rotationAngle += 180f
                        onSwapCurrencies()
                    }
                    .testTag("swap_currencies_button"),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap currencies",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(26.dp)
                            .rotate(animatedRotation)
                    )
                }
            }
        }

        // Conversion Result Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("conversion_result_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CurrencyExchange,
                            contentDescription = "Exchange rate result",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Conversion Result",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Favorite Star Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("favorite_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Toggle favorite",
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Formula Header
                Text(
                    text = "${uiState.amountInput.ifBlank { "0" }} ${uiState.fromCurrency.code} =",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                )

                // Large Main Result Text
                val formattedResult = String.format(Locale.US, "%,.2f", uiState.convertedAmount)
                Text(
                    text = "${uiState.toCurrency.symbol} $formattedResult ${uiState.toCurrency.code}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Exchange Rates Details
                val formattedRate = String.format(Locale.US, "%.4f", uiState.currentRate)
                val formattedInv = String.format(Locale.US, "%.4f", uiState.inverseRate)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "1 ${uiState.fromCurrency.code} = $formattedRate ${uiState.toCurrency.code}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "1 ${uiState.toCurrency.code} = $formattedInv ${uiState.fromCurrency.code}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Popular Currency Pair Shortcuts
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Quick Currency Pairs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            val quickPairs = listOf(
                Pair("USD", "EUR"),
                Pair("EUR", "GBP"),
                Pair("USD", "JPY"),
                Pair("USD", "CAD"),
                Pair("GBP", "USD")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPairs.forEach { (fromCode, toCode) ->
                    val fromCurr = CurrencyData.findByCode(fromCode)
                    val toCurr = CurrencyData.findByCode(toCode)
                    val isSelected = uiState.fromCurrency.code == fromCode && uiState.toCurrency.code == toCode

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectPair(fromCurr, toCurr) },
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${fromCurr.flagEmoji}${toCurr.flagEmoji}",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$fromCode/$toCode",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CurrencySelectorCard(
    label: String,
    currency: Currency,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currency.flagEmoji,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currency.code,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currency.symbol,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = currency.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select $label",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
