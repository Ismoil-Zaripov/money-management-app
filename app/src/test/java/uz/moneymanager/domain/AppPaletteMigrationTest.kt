package uz.moneymanager.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import uz.moneymanager.domain.model.AppPalette

class AppPaletteMigrationTest {

    @Test
    fun removedAndLegacyNames_fallBackToDefault() {
        listOf("EMERALD", "OCEAN_TEAL", "ROYAL_INDIGO", "WARM_CORAL", "GRAPHITE_MINT", "OCEAN", "VIOLET", "SUNSET", "GRAPHITE")
            .forEach { assertEquals(it, AppPalette.DEFAULT, AppPalette.fromStored(it)) }
    }

    @Test
    fun currentNames_roundTrip() {
        AppPalette.entries.forEach { assertEquals(it, AppPalette.fromStored(it.name)) }
    }

    @Test
    fun nullOrUnknown_fallBackToDefault() {
        assertEquals(AppPalette.CORPORATE_BLUE, AppPalette.fromStored(null))
        assertEquals(AppPalette.CORPORATE_BLUE, AppPalette.fromStored("???"))
    }
}
