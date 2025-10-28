package com.meu.stock.views.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class CurrencyVisualTransformation(
    private val locale: Locale = Locale("pt", "BR"),
    private val includeSymbol: Boolean = true
) : VisualTransformation {

    private val currencyFormatter = NumberFormat.getCurrencyInstance(locale)

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        if (digits.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        // Converte os dígitos para um valor numérico (em centavos)
        val amount = digits.toLong()

        // Formata o valor como moeda
        val formattedText = currencyFormatter.format(amount / 100.0)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // A lógica é complexa, então para simplificar, o cursor vai para o final
                return formattedText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return digits.length
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}

/**
 * Formata um valor Double para uma String de moeda no padrão brasileiro (R$).
 * Exemplo: 1234.5 se torna "R$ 1.234,50"
 */
fun Double.toCurrencyString(): String {
    val locale = Locale("pt", "BR")
    val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
    return currencyFormatter.format(this)
}
