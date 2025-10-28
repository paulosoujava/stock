package com.meu.stock.views.ui.utils


import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        // Limita a string a 11 dígitos (DDD + 9 dígitos do celular)
        val digitsOnly = text.text.filter { it.isDigit() }.take(11)

        val maskedText = buildString {
            when (digitsOnly.length) {
                0 -> return@buildString
                1 -> append("(${digitsOnly[0]}")
                2 -> append("(${digitsOnly[0]}${digitsOnly[1]})")
                in 3..6 -> {
                    append("(${digitsOnly.substring(0, 2)}) ${digitsOnly.substring(2)}")
                }
                in 7..10 -> { // 8 ou 9 dígitos no número principal
                    val ddd = digitsOnly.substring(0, 2)
                    val firstPart = digitsOnly.substring(2, 6)
                    val secondPart = digitsOnly.substring(6)
                    append("($ddd) $firstPart-$secondPart")
                }
                11 -> { // Celular com 9 dígitos
                    val ddd = digitsOnly.substring(0, 2)
                    val firstPart = digitsOnly.substring(2, 7)
                    val secondPart = digitsOnly.substring(7)
                    append("($ddd) $firstPart-$secondPart")
                }
            }
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Mapeia a posição do cursor do texto original para o texto formatado
                return when {
                    offset >= 7 -> offset + 5
                    offset >= 2 -> offset + 4
                    else -> offset + 1
                }.coerceAtMost(maskedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Mapeia a posição do cursor do texto formatado de volta para o original
                return when {
                    offset >= 10 -> offset - 5
                    offset >= 4 -> offset - 4
                    offset >= 1 -> offset - 1
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
