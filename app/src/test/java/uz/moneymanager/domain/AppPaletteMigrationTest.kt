package uz.moneymanager.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import uz.moneymanager.domain.model.AppPalette

class AppPaletteMigrationTest {

    @Test
    fun legacyStoredNames_mapToNewPalettes() {
        assertEquals(AppPalette.OCEAN_TEAL, AppPalette.fromStored("OCEAN"))
        assertEquals(AppPalette.ROYAL_INDIGO, AppPalette.fromStored("VIOLET"))
        assertEquals(AppPalette.WARM_CORAL, AppPalette.fromStored("SUNSET"))
        assertEquals(AppPalette.GRAPHITE_MINT, AppPalette.fromStored("GRAPHITE"))
        assertEquals(AppPalette.EMERALD, AppPalette.fromStored("EMERALD"))
    }

    @Test
    fun newStoredNames_roundTrip() {
        AppPalette.entries.forEach { assertEquals(it, AppPalette.fromStored(it.name)) }
    }

    @Test
    fun missingOrUnknown_fallsBackToEmerald() {
        assertEquals(AppPalette.EMERALD, AppPalette.fromStored(null))
        assertEquals(AppPalette.EMERALD, AppPalette.fromStored("???"))
    }
}
