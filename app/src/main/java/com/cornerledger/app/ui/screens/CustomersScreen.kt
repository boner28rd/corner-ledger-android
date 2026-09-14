package com.cornerledger.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.CustomerEntity
import com.cornerledger.app.ui.components.CustomerRow
import com.cornerledger.app.ui.theme.AccentSoftText
import com.cornerledger.app.ui.theme.BannerBorder
import com.cornerledger.app.ui.theme.BannerGradientEnd
import com.cornerledger.app.ui.theme.BannerGradientStart
import com.cornerledger.app.ui.theme.DividerColor
import com.cornerledger.app.ui.theme.ExportButtonBg
import com.cornerledger.app.ui.theme.TextPrimary
import com.cornerledger.app.util.formatAmount

@Composable
fun CustomersScreen(
    customers: List<CustomerEntity>,
    onCustomerClick: (CustomerEntity) -> Unit,
    onExportClick: () -> Unit,
) {
    val totalOutstanding = customers.sumOf { if (it.balance > 0) it.balance else 0.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 6.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(BannerGradientStart, BannerGradientEnd)))
                .border(1.dp, BannerBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        "TOTAL OUTSTANDING",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentSoftText,
                    )
                    Text(
                        formatAmount(totalOutstanding),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                Button(
                    onClick = onExportClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExportButtonBg, contentColor = AccentSoftText),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
                ) {
                    Text("Export", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Column {
            customers.forEachIndexed { index, customer ->
                CustomerRow(customer = customer, onClick = { onCustomerClick(customer) })
                if (index != customers.lastIndex) {
                    HorizontalDivider(color = DividerColor, thickness = 1.dp)
                }
            }
        }
    }
}
