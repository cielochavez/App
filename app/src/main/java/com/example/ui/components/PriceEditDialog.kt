package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AthenaPink
import java.util.Locale

@Composable
fun PriceEditDialog(
    itemName: String,
    initialPrice: Double?,
    onDismiss: () -> Unit,
    onSavePrice: (Double) -> Unit
) {
    var priceText by remember {
        mutableStateOf(
            if (initialPrice != null && initialPrice > 0) {
                String.format(Locale.US, "%.2f", initialPrice)
            } else {
                ""
            }
        )
    }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Asignar / Cambiar Precio",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = "Introduce el precio para:\n\"$itemName\"",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        priceText = it
                        isError = false
                    },
                    label = { Text("Precio en $") },
                    leadingIcon = {
                        Text(
                            text = "$",
                            fontWeight = FontWeight.Bold,
                            color = AthenaPink,
                            fontSize = 16.sp
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Por favor ingresa un monto válido (ej: 45.00)") }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AthenaPink,
                        focusedLabelColor = AthenaPink
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("price_input_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanText = priceText.replace(",", ".").trim()
                    val parsed = cleanText.toDoubleOrNull()
                    if (parsed != null && parsed >= 0) {
                        onSavePrice(parsed)
                        onDismiss()
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthenaPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_price_btn")
            ) {
                Text("Guardar Precio", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_price_btn")
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
