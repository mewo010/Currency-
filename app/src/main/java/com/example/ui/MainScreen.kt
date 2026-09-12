package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.AppUpdateDialog
import com.example.ui.components.CurrencyPickerDialog
import com.example.ui.components.UpdateBanner
import com.example.ui.screens.AllRatesScreen
import com.example.ui.screens.ConverterScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HistoryScreen

sealed class NavTab(val title: String, val icon: ImageVector, val route: String) {
    data object Converter : NavTab("Converter", Icons.Default.CurrencyExchange, "converter")
    data object Favorites : NavTab("Favorites", Icons.Default.Star, "favorites")
    data object AllRates : NavTab("All Rates", Icons.Default.FormatListBulleted, "all_rates")
    data object History : NavTab("History", Icons.Default.History, "history")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CurrencyViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabItem by remember { mutableIntStateOf(0) }
    val tabs = listOf(NavTab.Converter, NavTab.Favorites, NavTab.AllRates, NavTab.History)

    // Handle Snackbar messages from ViewModel
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.consumeSnackbar()
        }
    }

    // Permission launcher for Location detection
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Proceed with location detection (detector falls back gracefully if permission wasn't granted)
        viewModel.detectLocation()
    }

    val triggerLocationDetection = {
        val finePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (finePerm == PackageManager.PERMISSION_GRANTED || coarsePerm == PackageManager.PERMISSION_GRANTED) {
            viewModel.detectLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon_1784885544152),
                            contentDescription = "GlobalCash Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GlobalCash",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showUpdateDialog() },
                        modifier = Modifier.testTag("top_bar_update_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.updateAvailableRelease != null) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "Check for Updates",
                                tint = if (uiState.updateAvailableRelease != null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                    IconButton(
                        onClick = { triggerLocationDetection() },
                        modifier = Modifier.testTag("top_bar_location_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Detect Local Currency",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.loadExchangeRates(forceRefresh = true) },
                        modifier = Modifier.testTag("top_bar_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh rates",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabItem == index,
                        onClick = { selectedTabItem = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selectedTabItem == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.route}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            UpdateBanner(
                visible = uiState.updateBannerVisible && uiState.updateAvailableRelease != null,
                newVersionTag = uiState.updateAvailableRelease?.tagName ?: "",
                onUpdateClick = { viewModel.showUpdateDialog() },
                onDismissClick = { viewModel.dismissUpdateBanner() }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTabItem) {
                    0 -> ConverterScreen(
                        uiState = uiState,
                        onAmountChange = { viewModel.onAmountInputChanged(it) },
                        onQuickAmountSelect = { viewModel.onQuickAmountSelected(it) },
                        onSwapCurrencies = { viewModel.onSwapCurrencies() },
                        onDetectLocation = { triggerLocationDetection() },
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onOpenPicker = { viewModel.showPicker(it) },
                        onSelectPair = { from, to -> viewModel.onSelectCurrencyPair(from, to) },
                        onRefreshRates = { viewModel.loadExchangeRates(forceRefresh = true) }
                    )
                    1 -> FavoritesScreen(
                        uiState = uiState,
                        onSelectPair = { from, to ->
                            viewModel.onSelectCurrencyPair(from, to)
                            selectedTabItem = 0
                        },
                        onRemoveFavorite = { from, to -> viewModel.toggleFavorite() }
                    )
                    2 -> AllRatesScreen(
                        uiState = uiState,
                        onOpenPicker = { viewModel.showPicker(it) },
                        onSelectTargetCurrency = { target ->
                            viewModel.onToCurrencySelected(target)
                            selectedTabItem = 0
                        }
                    )
                    3 -> HistoryScreen(
                        uiState = uiState,
                        onSelectPair = { from, to ->
                            viewModel.onSelectCurrencyPair(from, to)
                            selectedTabItem = 0
                        },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }

                // Currency Picker Bottom Sheet
                uiState.activePickerType?.let { pickerType ->
                    val title = if (pickerType == CurrencyPickerType.FROM) "Select Base Currency" else "Select Target Currency"
                    val selectedCurrency = if (pickerType == CurrencyPickerType.FROM) uiState.fromCurrency else uiState.toCurrency

                    CurrencyPickerDialog(
                        title = title,
                        selectedCurrency = selectedCurrency,
                        searchQuery = uiState.pickerSearchQuery,
                        onSearchQueryChange = { viewModel.updatePickerSearchQuery(it) },
                        onCurrencySelected = { currency ->
                            if (pickerType == CurrencyPickerType.FROM) {
                                viewModel.onFromCurrencySelected(currency)
                            } else {
                                viewModel.onToCurrencySelected(currency)
                            }
                        },
                        onDismiss = { viewModel.hidePicker() }
                    )
                }
            }
        }

        if (uiState.showUpdateDialog) {
            val context = LocalContext.current
            AppUpdateDialog(
                uiState = uiState,
                onDismiss = { viewModel.hideUpdateDialog() },
                onCheckForUpdates = { owner, repo ->
                    viewModel.checkForUpdates(silent = false, customOwner = owner, customRepo = repo)
                },
                onStartDownload = {
                    viewModel.startDownloadAndInstall()
                },
                onInstallApk = { file ->
                    viewModel.triggerInstall(file)
                },
                onOpenPermissionSettings = {
                    viewModel.openInstallPermissionSettings()
                    try {
                        val intent = android.content.Intent(
                            android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            android.net.Uri.parse("package:${context.packageName}")
                        ).apply {
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        try {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS).apply {
                                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                },
                onSaveRepository = { owner, repo ->
                    viewModel.saveUpdateRepository(owner, repo)
                }
            )
        }
    }
}
