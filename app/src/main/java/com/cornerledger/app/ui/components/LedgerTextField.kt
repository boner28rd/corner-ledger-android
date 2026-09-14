package com.cornerledger.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cornerledger.app.ui.theme.Accent
import com.cornerledger.app.ui.theme.Background
import com.cornerledger.app.ui.theme.SurfaceBorder
import com.cornerledger.app.ui.theme.TextMuted
import com.cornerledger.app.ui.theme.TextPrimary

/** Styled to match the prototype's dark inputs: `#0b0d12` fill, `#262d3a` border, 12dp corners. */
@Composable
fun LedgerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    numeric: Boolean = false,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder?.let { text -> { Text(text, color = TextMuted, style = textStyle) } },
        textStyle = textStyle.copy(color = TextPrimary),
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Decimal) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Background,
            unfocusedContainerColor = Background,
            disabledContainerColor = Background,
            focusedBorderColor = Accent,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = Accent,
        ),
    )
}
