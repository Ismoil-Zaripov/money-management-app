package uz.moneymanager.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.roundToInt
import uz.moneymanager.R
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.domain.model.AppLanguage
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.ThemeMode
import uz.moneymanager.ui.theme.barContainer
import uz.moneymanager.ui.theme.computeContentLineHeight
import uz.moneymanager.ui.theme.contentText
import uz.moneymanager.ui.theme.getPaletteSwatchColor
import uz.moneymanager.ui.theme.onBarContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateCurrencies: () -> Unit,
    onNavigateRates: () -> Unit,
    onNavigateCategories: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val palette by viewModel.palette.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val contentFontSize by viewModel.contentFontSize.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_settings),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.barContainer,
                        titleContentColor = MaterialTheme.colorScheme.onBarContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBarContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onBarContainer
                    )
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBarContainer.copy(alpha = 0.2f))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // General section
            Text(
                text = stringResource(R.string.settings_section_general),
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            val themeText = when (themeMode) {
                ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                ThemeMode.DARK -> stringResource(R.string.theme_dark)
            }

            SettingsItem(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.setting_theme),
                subtitle = themeText,
                onClick = { showThemeDialog = true }
            )

            // Palette selector
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.setting_palette),
                                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.desc_palette),
                                style = MaterialTheme.contentText,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
                        AppPalette.entries.forEach { p ->
                            val color = getPaletteSwatchColor(p, isDark)
                            val isSelected = palette == p
                            val name = when (p) {
                                AppPalette.OCEAN -> stringResource(R.string.palette_ocean)
                                AppPalette.EMERALD -> stringResource(R.string.palette_emerald)
                                AppPalette.VIOLET -> stringResource(R.string.palette_violet)
                                AppPalette.SUNSET -> stringResource(R.string.palette_sunset)
                                AppPalette.GRAPHITE -> stringResource(R.string.palette_graphite)
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { viewModel.setPalette(p) }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                                            shape = CircleShape
                                        )
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = name,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    style = MaterialTheme.contentText.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            // Font size selector
            FontSizeSettingItem(
                savedSize = contentFontSize,
                onSave = { viewModel.setContentFontSize(it) },
                onReset = { viewModel.setContentFontSize(SettingsRepository.DEFAULT_FONT_SIZE) }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            val langText = when (currentLanguage) {
                AppLanguage.EN -> stringResource(R.string.lang_en)
                AppLanguage.UZ -> stringResource(R.string.lang_uz)
                AppLanguage.RU -> stringResource(R.string.lang_ru)
            }

            SettingsItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.setting_language),
                subtitle = langText,
                onClick = { showLanguageDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Management section
            Text(
                text = stringResource(R.string.settings_section_management),
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            SettingsItem(
                icon = Icons.Default.MonetizationOn,
                title = stringResource(R.string.title_currencies),
                subtitle = stringResource(R.string.desc_currencies),
                onClick = onNavigateCurrencies,
                showArrow = true
            )

            SettingsItem(
                icon = Icons.Default.CurrencyExchange,
                title = stringResource(R.string.title_exchange_rates),
                subtitle = stringResource(R.string.desc_exchange_rates),
                onClick = onNavigateRates,
                showArrow = true
            )

            SettingsItem(
                icon = Icons.Default.Category,
                title = stringResource(R.string.title_categories),
                subtitle = stringResource(R.string.desc_categories),
                onClick = onNavigateCategories,
                showArrow = true
            )
        }
    }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.setting_theme)) },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        val text = when (mode) {
                            ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                            ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                            ThemeMode.DARK -> stringResource(R.string.theme_dark)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.contentText,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.setting_language)) },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        val text = when (lang) {
                            AppLanguage.EN -> stringResource(R.string.lang_en)
                            AppLanguage.UZ -> stringResource(R.string.lang_uz)
                            AppLanguage.RU -> stringResource(R.string.lang_ru)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == lang,
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.contentText,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.action_close))
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showArrow: Boolean = false,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.contentText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (showArrow) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
fun FontSizeSettingItem(
    savedSize: Int?,
    onSave: (Int) -> Unit,
    onReset: () -> Unit,
) {
    if (savedSize == null) return

    var sliderPosition by remember(savedSize) { mutableFloatStateOf(savedSize.toFloat()) }
    val currentSp = sliderPosition.roundToInt().coerceIn(SettingsRepository.MIN_FONT_SIZE, SettingsRepository.MAX_FONT_SIZE)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.setting_font_size),
                        style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.desc_font_size),
                        style = MaterialTheme.contentText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val stateDesc = stringResource(R.string.font_size_value_sp, currentSp)
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = {
                    onSave(currentSp)
                },
                valueRange = SettingsRepository.MIN_FONT_SIZE.toFloat()..SettingsRepository.MAX_FONT_SIZE.toFloat(),
                steps = 7,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        stateDescription = stateDesc
                    }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.font_size_value_sp, currentSp),
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                TextButton(
                    onClick = onReset,
                    enabled = currentSp != SettingsRepository.DEFAULT_FONT_SIZE
                ) {
                    Text(text = stringResource(R.string.font_size_reset))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Jonli namuna qatori
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    val sampleStyle = MaterialTheme.typography.bodySmall.copy(
                        fontSize = currentSp.sp,
                        lineHeight = computeContentLineHeight(currentSp.toFloat()).sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.font_size_sample),
                        style = sampleStyle,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
