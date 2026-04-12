package br.edu.utfpr.appcontatos.ui.contact.form.visualTransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {
    //(00) 00000-0000
    //(00) 0000-0000
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedPhone = text.text.mapIndexed { index, char ->
            when {
                index == 0 -> "($char"
                index == 2 -> ") $char"
                (index == 6 && text.text.length < 11) ||
                        (index == 7 && text.text.length == 11) -> "-$char"
                else -> char
            }
        }.joinToString("")

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset > 6 -> offset + 4
                    offset > 2 -> offset + 3
                    offset > 0 -> offset + 1
                    else -> offset
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset > 6 -> offset - 4
                    offset > 2 -> offset - 3
                    offset > 0 -> offset - 1
                    else -> offset
                }
            }
        }

        return TransformedText(
            AnnotatedString(formattedPhone),
            offsetMapping
        )
    }
}