package uz.moneymanager.domain.model

enum class TransactionType { INCOME, EXPENSE }

enum class ThemeMode { SYSTEM, LIGHT, DARK }

enum class AppPalette { OCEAN, EMERALD, VIOLET, SUNSET, GRAPHITE }

enum class AppLanguage(val tag: String) { EN("en"), UZ("uz"), RU("ru") }
