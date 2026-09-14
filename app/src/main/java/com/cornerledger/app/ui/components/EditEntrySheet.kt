package com.cornerledger.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornerledger.app.data.entryLabel
import com.cornerledger.app.ui.EditSheetState
import com.cornerledger.app.ui.theme.Accent
import com.cornerledger.app.ui.theme.Background
import com.cornerledger.app.ui.theme.ChipBackground
import com.cornerledger.app.ui.theme.DeleteBackground
import com.cornerledger.app.ui.theme.DeleteBorder
import com.cornerledger.app.ui.theme.Owed
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextPrimary

@Composable
fun EditEntrySheet(
    sheet: EditSheetState,
    onAmountChange: (String) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    LedgerBottomSheetOverlay(onDismiss = onCancel) {
        Text(
            "Edit ${entryLabel(sheet.type, sheet.sign)}",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )

        Column(modifier = Modifier.padding(top = 14.dp)) {
            FieldLabel("Amount (£)")
            LedgerTextField(
                value = sheet.amount,
                onValueChange = onAmountChange,
                numeric = true,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = DeleteBackground, contentColor = Owed),
                border = BorderStroke(1.dp, DeleteBorder),
            ) {
                Text("Delete", style = MaterialTheme.typography.titleSmall)
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = ChipBackground, contentColor = TextPrimary),
                border = BorderStroke(1.dp, SurfaceBorder),
            ) {
                Text("Cancel", style = MaterialTheme.typography.titleSmall)
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background),
            ) {
                Text("Save", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
