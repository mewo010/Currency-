package com.example.data.model

object CurrencyData {
    val currencies = listOf(
        Currency("USD", "United States Dollar", "$", "🇺🇸", "US"),
        Currency("EUR", "Euro", "€", "🇪🇺", "EU"),
        Currency("GBP", "British Pound Sterling", "£", "🇬🇧", "GB"),
        Currency("JPY", "Japanese Yen", "¥", "🇯🇵", "JP"),
        Currency("CAD", "Canadian Dollar", "CA$", "🇨🇦", "CA"),
        Currency("AUD", "Australian Dollar", "A$", "🇦🇺", "AU"),
        Currency("CHF", "Swiss Franc", "CHF", "🇨🇭", "CH"),
        Currency("CNY", "Chinese Yuan", "¥", "🇨🇳", "CN"),
        Currency("INR", "Indian Rupee", "₹", "🇮🇳", "IN"),
        Currency("BRL", "Brazilian Real", "R$", "🇧🇷", "BR"),
        Currency("MXN", "Mexican Peso", "Mex$", "🇲🇽", "MX"),
        Currency("SGD", "Singapore Dollar", "S$", "🇸🇬", "SG"),
        Currency("HKD", "Hong Kong Dollar", "HK$", "🇭🇰", "HK"),
        Currency("NZD", "New Zealand Dollar", "NZ$", "🇳🇿", "NZ"),
        Currency("KRW", "South Korean Won", "₩", "🇰🇷", "KR"),
        Currency("SEK", "Swedish Krona", "kr", "🇸🇪", "SE"),
        Currency("NOK", "Norwegian Krone", "kr", "🇳🇴", "NO"),
        Currency("DKK", "Danish Krone", "kr", "🇩🇰", "DK"),
        Currency("ZAR", "South African Rand", "R", "🇿🇦", "ZA"),
        Currency("TRY", "Turkish Lira", "₺", "🇹🇷", "TR"),
        Currency("AED", "United Arab Emirates Dirham", "🇦🇪", "🇦🇪", "AE"),
        Currency("SAR", "Saudi Riyal", "﷼", "🇸🇦", "SA"),
        Currency("THB", "Thai Baht", "฿", "🇹🇭", "TH"),
        Currency("MYR", "Malaysian Ringgit", "RM", "🇲🇾", "MY"),
        Currency("PHP", "Philippine Peso", "₱", "🇵🇭", "PH"),
        Currency("IDR", "Indonesian Rupiah", "Rp", "🇮🇩", "ID"),
        Currency("VND", "Vietnamese Dong", "₫", "🇻🇳", "VN"),
        Currency("ILS", "Israeli New Shekel", "₪", "🇮🇱", "IL"),
        Currency("PLN", "Polish Zloty", "zł", "🇵🇱", "PL"),
        Currency("CZK", "Czech Koruna", "Kč", "🇨🇿", "CZ"),
        Currency("HUF", "Hungarian Forint", "Ft", "🇭🇺", "HU"),
        Currency("RON", "Romanian Leu", "lei", "🇷🇴", "RO"),
        Currency("BGN", "Bulgarian Lev", "лв", "🇧🇬", "BG"),
        Currency("EGP", "Egyptian Pound", "E£", "🇪🇬", "EG"),
        Currency("CLP", "Chilean Peso", "CLP$", "🇨🇱", "CL"),
        Currency("COP", "Colombian Peso", "COL$", "🇨🇴", "CO"),
        Currency("PEN", "Peruvian Sol", "S/", "🇵🇪", "PE"),
        Currency("ARS", "Argentine Peso", "ARS$", "🇦🇷", "AR"),
        Currency("PKR", "Pakistani Rupee", "₨", "🇵🇰", "PK"),
        Currency("BDT", "Bangladeshi Taka", "৳", "🇧🇩", "BD"),
        Currency("NGN", "Nigerian Naira", "₦", "🇳🇬", "NG"),
        Currency("KES", "Kenyan Shilling", "KSh", "🇰🇪", "KE")
    )

    private val countryToCurrencyMap = mapOf(
        "US" to "USD", "PR" to "USD", "GU" to "USD", "VI" to "USD",
        "FR" to "EUR", "DE" to "EUR", "IT" to "EUR", "ES" to "EUR", "NL" to "EUR",
        "BE" to "EUR", "AT" to "EUR", "PT" to "EUR", "FI" to "EUR", "IE" to "EUR",
        "GR" to "EUR", "SK" to "EUR", "SI" to "EUR", "LT" to "EUR", "LV" to "EUR",
        "EE" to "EUR", "CY" to "EUR", "MT" to "EUR", "LU" to "EUR", "HR" to "EUR",
        "GB" to "GBP", "JP" to "JPY", "CA" to "CAD", "AU" to "AUD", "CH" to "CHF",
        "CN" to "CNY", "IN" to "INR", "BR" to "BRL", "MX" to "MXN", "SG" to "SGD",
        "HK" to "HKD", "NZ" to "NZD", "KR" to "KRW", "SE" to "SEK", "NO" to "NOK",
        "DK" to "DKK", "ZA" to "ZAR", "TR" to "TRY", "AE" to "AED", "SA" to "SAR",
        "TH" to "THB", "MY" to "MYR", "PH" to "PHP", "ID" to "IDR", "VN" to "VND",
        "IL" to "ILS", "PL" to "PLN", "CZ" to "CZK", "HU" to "HUF", "RO" to "RON",
        "BG" to "BGN", "EG" to "EGP", "CL" to "CLP", "CO" to "COP", "PE" to "PEN",
        "AR" to "ARS", "PK" to "PKR", "BD" to "BDT", "NG" to "NGN", "KE" to "KE"
    )

    fun findByCode(code: String): Currency {
        return currencies.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: Currency(code.uppercase(), code.uppercase(), "$", "🌐", "GLOBAL")
    }

    fun getCurrencyForCountry(countryCode: String?): Currency {
        if (countryCode.isNullOrEmpty()) return findByCode("USD")
        val upperCountry = countryCode.uppercase()
        val currencyCode = countryToCurrencyMap[upperCountry] ?: "USD"
        return findByCode(currencyCode)
    }

    // Default backup exchange rates relative to USD if offline and cache is empty
    val defaultBackupRatesToUsd = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.78,
        "JPY" to 155.20,
        "CAD" to 1.36,
        "AUD" to 1.51,
        "CHF" to 0.89,
        "CNY" to 7.24,
        "INR" to 83.45,
        "BRL" to 5.45,
        "MXN" to 18.10,
        "SGD" to 1.35,
        "HKD" to 7.81,
        "NZD" to 1.63,
        "KRW" to 1380.0,
        "SEK" to 10.55,
        "NOK" to 10.65,
        "DKK" to 6.87,
        "ZAR" to 18.25,
        "TRY" to 32.80,
        "AED" to 3.67,
        "SAR" to 3.75,
        "THB" to 36.50,
        "MYR" to 4.70,
        "PHP" to 58.50,
        "IDR" to 16400.0,
        "VND" to 25400.0,
        "ILS" to 3.70,
        "PLN" to 3.95,
        "CZK" to 23.20,
        "HUF" to 365.0,
        "RON" to 4.58,
        "BGN" to 1.80,
        "EGP" to 47.70,
        "CLP" to 920.0,
        "COP" to 4100.0,
        "PEN" to 3.75,
        "ARS" to 910.0,
        "PKR" to 278.5,
        "BDT" to 117.2,
        "NGN" to 1480.0,
        "KES" to 129.5
    )
}
