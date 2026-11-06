package uz.moneymanager.feature.rates.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import uz.moneymanager.R
import uz.moneymanager.feature.rates.presentation.RateEvent

/** Shows rate events as snackbars; a failed CBU fetch offers "Retry" which calls [onRetryImport]. */
@Composable
fun RateEventsEffect(
    events: Flow<RateEvent>,
    snackbarHostState: SnackbarHostState,
    onRetryImport: () -> Unit,
) {
    val context = LocalContext.current
    val retry by rememberUpdatedState(onRetryImport)
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is RateEvent.Success -> {}
                is RateEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    if (event.messageRes == R.string.err_cbu_fetch_failed) {
                        val result = snackbarHostState.showSnackbar(
                            message = msg,
                            actionLabel = context.getString(R.string.action_retry)
                        )
                        if (result == SnackbarResult.ActionPerformed) retry()
                    } else {
                        snackbarHostState.showSnackbar(msg)
                    }
                }
                is RateEvent.CbuResult -> {
                    val msg = buildString {
                        append(context.getString(R.string.cbu_import_success, event.added, event.existing))
                        if (event.notFound.isNotEmpty()) {
                            append(" ")
                            append(context.getString(R.string.cbu_import_not_found, event.notFound.joinToString(", ")))
                        }
                    }
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }
}
