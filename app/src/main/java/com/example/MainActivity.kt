package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.location.LocationCurrencyDetector
import com.example.data.network.ExchangeRateApiService
import com.example.data.repository.CurrencyRepository
import com.example.data.update.AppUpdateManager
import com.example.ui.CurrencyViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.GlobalCashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val apiService = ExchangeRateApiService.create()
        val repository = CurrencyRepository(apiService, database)
        val locationDetector = LocationCurrencyDetector(applicationContext)
        val updateManager = AppUpdateManager(applicationContext)

        val viewModelFactory = CurrencyViewModel.Factory(repository, locationDetector, updateManager)
        val viewModel = ViewModelProvider(this, viewModelFactory)[CurrencyViewModel::class.java]

        setContent {
            GlobalCashTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
