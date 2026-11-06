package uz.moneymanager.feature.paymentmethods.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PaymentMethodIconTile(isDefault: Boolean, modifier: Modifier = Modifier) {
    val container = if (isDefault) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceContainerHighest
    val tint = if (isDefault) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier.size(48.dp).background(container, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isDefault) Icons.Default.AccountBalanceWallet else Icons.Default.CreditCard,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}
