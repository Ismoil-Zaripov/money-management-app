package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

/** Maydon fokusda bo'lgan bo'lsa va fokusni yo'qotsa, [onLost] chaqiriladi ([hadFocus] holati caller'da saqlanadi). */
internal fun Modifier.onFocusLost(hadFocus: Boolean, onFocused: () -> Unit, onLost: () -> Unit): Modifier =
    onFocusChanged { state ->
        if (state.isFocused) onFocused()
        else if (hadFocus) onLost()
    }
