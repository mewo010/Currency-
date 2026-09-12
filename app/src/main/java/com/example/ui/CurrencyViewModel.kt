package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ConversionHistoryEntity
import com.example.data.local.FavoritePairEntity
import com.example.data.location.LocationCurrencyDetector
import com.example.data.model.Currency
import com.example.data.model.CurrencyData
import com.example.data.repository.CurrencyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CurrencyPickerType {
    FROM, TO
}

data class CurrencyUiState(
    val amountInput: String = "100",
    val fromCurrency: Currency = CurrencyData.findByCode("USD"),
    val toCurrency: Currency = CurrencyData.findByCode("EUR"),
    val convertedAmount: Double = 92.0,
    val currentRate: Double = 0.92,
    val inverseRate: Double = 1.087,
    val ratesMap: Map<String, Double> = emptyMap(),
    val isLoadingRates: Boolean = false,
    val isDetectingLocation: Boolean = false,
    val lastUpdatedText: String = "",
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    val isFavorite: Boolean = false,
    val favorites: List<FavoritePairEntity> = emptyList(),
    val history: List<ConversionHistoryEntity> = emptyList(),
    val activePickerType: CurrencyPickerType? = null,
    val pickerSearchQuery: String = ""
)

class CurrencyViewModel(
    private val repository: CurrencyRepository,
    private val locationDetector: LocationCurrencyDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    private var historySaveJob: Job? = null

    init {
        loadExchangeRates()
        observeFavoritesAndHistory()
    }

    private fun observeFavoritesAndHistory() {
        viewModelScope.launch {
            repository.getFavoritePairs().collect { favList ->
                _uiState.update { currentState ->
                    currentState.copy(
                        favorites = favList,
                        isFavorite = favList.any {
                            it.fromCurrency == currentState.fromCurrency.code && it.toCurrency == currentState.toCurrency.code
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getConversionHistory().collect { historyList ->
                _uiState.update { it.copy(history = historyList) }
            }
        }
    }

    fun loadExchangeRates(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRates = true, errorMessage = null) }
            val currentFrom = _uiState.value.fromCurrency.code
            val fetchResult = repository.getExchangeRates(currentFrom, forceRefresh)

            _uiState.update { state ->
                val rates = fetchResult.rates
                val toCode = state.toCurrency.code
                val amount = state.amountInput.toDoubleOrNull() ?: 0.0

                val rate = if (rates.containsKey(toCode)) rates[toCode] ?: 1.0 else 1.0
                val converted = amount * rate
                val invRate = if (rate != 0.0) 1.0 / rate else 0.0

                state.copy(
                    ratesMap = rates,
                    currentRate = rate,
                    convertedAmount = converted,
                    inverseRate = invRate,
                    isLoadingRates = false,
                    lastUpdatedText = fetchResult.lastUpdatedText,
                    errorMessage = fetchResult.errorMessage
                )
            }
            checkIsFavorite()
        }
    }

    fun onAmountInputChanged(input: String) {
        // Sanitize numeric input
        val cleanInput = input.filter { it.isDigit() || it == '.' }
        _uiState.update { state ->
            val amount = cleanInput.toDoubleOrNull() ?: 0.0
            val rate = state.currentRate
            val converted = amount * rate
            state.copy(
                amountInput = cleanInput,
                convertedAmount = converted
            )
        }
        scheduleHistorySave()
    }

    fun onQuickAmountSelected(amount: Int) {
        onAmountInputChanged(amount.toString())
    }

    fun onFromCurrencySelected(currency: Currency) {
        if (currency.code == _uiState.value.fromCurrency.code) return
        _uiState.update { it.copy(fromCurrency = currency) }
        loadExchangeRates(forceRefresh = false)
        scheduleHistorySave()
    }

    fun onToCurrencySelected(currency: Currency) {
        if (currency.code == _uiState.value.toCurrency.code) return
        _uiState.update { state ->
            val toCode = currency.code
            val rate = state.ratesMap[toCode] ?: 1.0
            val amount = state.amountInput.toDoubleOrNull() ?: 0.0
            val converted = amount * rate
            val invRate = if (rate != 0.0) 1.0 / rate else 0.0

            state.copy(
                toCurrency = currency,
                currentRate = rate,
                convertedAmount = converted,
                inverseRate = invRate
            )
        }
        checkIsFavorite()
        scheduleHistorySave()
    }

    fun onSwapCurrencies() {
        val currentFrom = _uiState.value.fromCurrency
        val currentTo = _uiState.value.toCurrency
        _uiState.update { state ->
            state.copy(
                fromCurrency = currentTo,
                toCurrency = currentFrom
            )
        }
        loadExchangeRates(forceRefresh = false)
        scheduleHistorySave()
    }

    fun onSelectCurrencyPair(from: Currency, to: Currency) {
        _uiState.update { state ->
            state.copy(
                fromCurrency = from,
                toCurrency = to
            )
        }
        loadExchangeRates(forceRefresh = false)
        scheduleHistorySave()
    }

    fun detectLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetectingLocation = true) }
            val result = locationDetector.detectLocalCurrency()

            val detectedCurrency = result.currency
            onToCurrencySelected(detectedCurrency)

            _uiState.update { state ->
                state.copy(
                    isDetectingLocation = false,
                    snackbarMessage = result.message
                )
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val from = _uiState.value.fromCurrency.code
            val to = _uiState.value.toCurrency.code
            repository.toggleFavorite(from, to)
            val isNowFav = repository.isFavorite(from, to)
            val message = if (isNowFav) "Added $from/$to to Favorites" else "Removed $from/$to from Favorites"
            _uiState.update { it.copy(isFavorite = isNowFav, snackbarMessage = message) }
        }
    }

    private fun checkIsFavorite() {
        viewModelScope.launch {
            val from = _uiState.value.fromCurrency.code
            val to = _uiState.value.toCurrency.code
            val isFav = repository.isFavorite(from, to)
            _uiState.update { it.copy(isFavorite = isFav) }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.update { it.copy(snackbarMessage = "Conversion history cleared") }
        }
    }

    fun showPicker(type: CurrencyPickerType) {
        _uiState.update { it.copy(activePickerType = type, pickerSearchQuery = "") }
    }

    fun hidePicker() {
        _uiState.update { it.copy(activePickerType = null, pickerSearchQuery = "") }
    }

    fun updatePickerSearchQuery(query: String) {
        _uiState.update { it.copy(pickerSearchQuery = query) }
    }

    fun consumeSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun scheduleHistorySave() {
        historySaveJob?.cancel()
        historySaveJob = viewModelScope.launch {
            delay(1200) // Debounce history saving
            val state = _uiState.value
            val amount = state.amountInput.toDoubleOrNull() ?: 0.0
            if (amount > 0) {
                repository.saveHistory(
                    fromCurrency = state.fromCurrency.code,
                    toCurrency = state.toCurrency.code,
                    fromAmount = amount,
                    toAmount = state.convertedAmount,
                    rate = state.currentRate
                )
            }
        }
    }

    class Factory(
        private val repository: CurrencyRepository,
        private val locationDetector: LocationCurrencyDetector
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CurrencyViewModel(repository, locationDetector) as T
        }
    }
}
