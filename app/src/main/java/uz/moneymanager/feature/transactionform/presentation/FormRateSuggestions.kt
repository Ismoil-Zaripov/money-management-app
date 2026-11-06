package uz.moneymanager.feature.transactionform.presentation

import kotlin.coroutines.cancellation.CancellationException
import uz.moneymanager.data.repository.ExchangeRateRepository
import uz.moneymanager.data.repository.SaveResult

/** CBU suggestions as plain strings, or `null` when they could not be fetched (offline / error). */
internal suspend fun ExchangeRateRepository.suggestOrNull(missing: SaveResult.MissingRates): Map<Long, String>? = try {
    suggestFromCbu(base = missing.base, quotes = missing.missing, date = missing.date)
        .mapValues { it.value.toPlainString() }
} catch (e: CancellationException) {
    throw e
} catch (_: Exception) {
    null
}
