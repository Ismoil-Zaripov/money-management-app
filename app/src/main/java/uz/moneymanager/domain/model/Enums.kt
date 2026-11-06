package uz.moneymanager.domain.model

enum class TransactionType { INCOME, EXPENSE }

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Enterprise color themes. [darkOnly] themes ignore [ThemeMode] and always render dark. */
enum class AppPalette(val darkOnly: Boolean = false) {
    CORPORATE_BLUE,
    NAVY_PROFESSIONAL,
    SLATE_NEUTRAL,
    TEAL_FINANCE,
    FOREST_GREEN,
    AUBERGINE,
    DARK_SLATE(darkOnly = true),
    HIGH_CONTRAST,
    BURGUNDY,
    GRAPHITE_RED(darkOnly = true);

    companion object {
        val DEFAULT = CORPORATE_BLUE

        /** Maps a stored name to a palette; unknown or removed (older) names fall back to [DEFAULT]. */
        fun fromStored(value: String?): AppPalette = entries.firstOrNull { it.name == value } ?: DEFAULT
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
