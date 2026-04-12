package br.edu.utfpr.appcontatos.ui.contact.form.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.appcontatos.data.format
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun FormDatePicker(
    modifier: Modifier = Modifier,
    label: String,
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    errorMessage: String = "",
    enabled: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = value
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )
    FormTextField(
        modifier = modifier,
        label = label,
        value = value.format(),
        onValueChange = {},
        readOnly = true,
        enabled = enabled,
        errorMessage = errorMessage,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Selecione a data"
                )
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant
                            .ofEpochMilli(it)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onValueChange(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormDatePickerPreview() {
    AppContatosTheme {
        var value by remember { mutableStateOf(LocalDate.now()) }
        Column(modifier = Modifier.fillMaxSize()) {
            FormDatePicker(
                modifier = Modifier.padding(20.dp),
                label = "Data",
                value = value,
                onValueChange = { newValue ->
                    value = newValue
                }
            )
        }
    }
}