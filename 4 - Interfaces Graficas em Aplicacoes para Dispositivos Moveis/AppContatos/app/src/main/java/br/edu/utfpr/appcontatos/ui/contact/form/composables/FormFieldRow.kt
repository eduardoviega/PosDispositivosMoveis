package br.edu.utfpr.appcontatos.ui.contact.form.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme

@Composable
fun FormFieldRow(
    modifier: Modifier = Modifier,
    label: String,
    imageVector: ImageVector? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = imageVector ?: Icons.Default.Brightness1,
            contentDescription = label,
            tint = if (imageVector == null) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
        content()
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
fun FormFieldRowPreview() {
    AppContatosTheme {
        FormFieldRow(
            label = "Nome",
            imageVector = Icons.Outlined.Person,
        ) {
            FormTextField(
                label = "Nome",
                value = "",
                onValueChange = {}
            )
        }
    }
}