package com.meu.stock.views.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meu.stock.ui.theme.StockTheme

/**
 * Um Composable que exibe uma contagem numérica dentro de um círculo (badge).
 * Se a contagem exceder 99, ele mostrará "+99".
 *
 * @param count O número a ser exibido.
 * @param modifier O modificador a ser aplicado ao componente.
 */
@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    // Determina o texto a ser exibido com base na contagem
    val displayText = if (count > 99) "+99" else count.toString()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary, // Cor de fundo do círculo
                shape = CircleShape
            )
            .padding(horizontal = 6.dp, vertical = 2.dp) // Padding para ajustar o tamanho
    ) {
        Text(
            text = displayText,
            color = MaterialTheme.colorScheme.onPrimary, // Cor do texto
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CountBadgePreview() {
    StockTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Exemplos:")
            CountBadge(count = 7)
            CountBadge(count = 99)
            CountBadge(count = 150)
        }
    }
}