package com.meu.stock.views.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CpfVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        // Limita a string a 11 dígitos
        val digitsOnly = text.text.filter { it.isDigit() }.take(11)

        val maskedText = buildString {
            digitsOnly.forEachIndexed { index, char ->
                append(char)
                when (index) {
                    2 -> append(".") // Adiciona o primeiro ponto
                    5 -> append(".") // Adiciona o segundo ponto
                    8 -> append("-") // Adiciona o hífen
                }
            }
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset >= 9 -> offset + 3
                    offset >= 6 -> offset + 2
                    offset >= 3 -> offset + 1
                    else -> offset
                }.coerceAtMost(maskedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset >= 12 -> offset - 3
                    offset >= 8 -> offset - 2
                    offset >= 4 -> offset - 1
                    else -> offset
                }.coerceAtLeast(0)
            }
        }

        return TransformedText(
            AnnotatedString(maskedText),
            offsetTranslator
        )
    }
}
