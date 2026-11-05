package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import uz.moneymanager.core.designsystem.theme.contentText

/** Katta summa uchun uslub: foydalanuvchi tanlagan kontent shriftiga mutanosib. */
internal val summaryBigAmountStyle: TextStyle
    @Composable
    get() = MaterialTheme.contentText.let {
        it.copy(
            fontSize = it.fontSize * 1.7f,
            lineHeight = it.lineHeight * 1.7f,
            fontWeight = FontWeight.Bold,
            fontFeatureSettings = "tnum",
        )
    }
