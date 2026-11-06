package uz.moneymanager.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.moneymanager.core.designsystem.theme.barSurface

/** Background of a top/bottom bar: the palette primary. */
@Composable
fun Modifier.barSurfaceBackground(): Modifier = background(MaterialTheme.colorScheme.barSurface)
