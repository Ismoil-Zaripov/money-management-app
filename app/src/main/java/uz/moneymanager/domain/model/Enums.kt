package uz.moneymanager.domain.model

enum class TransactionType { INCOME, EXPENSE }

enum class ThemeMode { SYSTEM, LIGHT, DARK }

enum class AppPalette {
    EMERALD, OCEAN_TEAL, ROYAL_INDIGO, WARM_CORAL, GRAPHITE_MINT;

    companion object {
        val DEFAULT = EMERALD

        /** Maps a stored name (including legacy names from older versions) to a palette. */
        fun fromStored(value: String?): AppPalette = when (value) {
            "OCEAN" -> OCEAN_TEAL
            "VIOLET" -> ROYAL_INDIGO
            "SUNSET" -> WARM_CORAL
            "GRAPHITE" -> GRAPHITE_MINT
            else -> entries.firstOrNull { it.name == value } ?: DEFAULT
        }
    }
}

/** Look of the top app bar and bottom navigation bar. */
enum class BarStyle {
    /** Opaque bar tinted with the palette color. */
    TINTED,

    /** Translucent bar; content blurs behind it where the device supports it. */
    GLASS;

    companion object {
        val DEFAULT = TINTED
    }
}

enum class AppLanguage(val tag: String) { EN("en"), UZ("uz"), RU("ru") }
