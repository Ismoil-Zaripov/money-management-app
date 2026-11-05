package uz.moneymanager.feature.settings.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.domain.model.AppLanguage

@Composable
internal fun LanguageSelector(selected: AppLanguage, onSelect: (AppLanguage) -> Unit) {
    val options = listOf(
        SegmentOption(AppLanguage.EN, stringResource(R.string.lang_en), label = stringResource(R.string.lang_short_en)),
        SegmentOption(AppLanguage.UZ, stringResource(R.string.lang_uz), label = stringResource(R.string.lang_short_uz)),
        SegmentOption(AppLanguage.RU, stringResource(R.string.lang_ru), label = stringResource(R.string.lang_short_ru)),
    )
    SettingsChoiceRow(
        icon = Icons.Default.Language,
        title = stringResource(R.string.setting_language),
        subtitle = options.first { it.value == selected }.description
    ) {
        SegmentedChoice(options, selected, onSelect)
    }
}
