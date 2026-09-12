package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CurrencyData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GlobalCash", appName)
  }

  @Test
  fun `verify currency data mapping`() {
    val usd = CurrencyData.findByCode("USD")
    assertEquals("United States Dollar", usd.name)
    assertEquals("US", usd.countryCode)

    val jpyFromCountry = CurrencyData.getCurrencyForCountry("JP")
    assertEquals("JPY", jpyFromCountry.code)
    assertEquals("Japanese Yen", jpyFromCountry.name)
  }
}
