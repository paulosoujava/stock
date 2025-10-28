// Adicione estes imports se eles não existirem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meu.stock.ui.theme.StockTheme
import java.text.NumberFormat
import java.util.Locale


/**
 * Um card informativo que resume as finanças do mês atual.
 *
 * @param month O nome do mês a ser exibido (ex: "Outubro").
 * @param revenue O valor total de entradas/receita do mês.
 * @param expenses O valor total de saídas/despesas do mês.
 * @param onHistoryClick Ação a ser executada quando o ícone de histórico for clicado.
 * @param modifier O modificador a ser aplicado ao card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySummaryCard(
    month: String,
    revenue: Double,
    expenses: Double,
    onHistoryClick: () -> Unit,
    onCloseMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = revenue - expenses
    // Formata os números para o padrão de moeda local (ex: R$ 1.234,56)
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Linha do Título com o ícone de histórico
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumo de $month",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onHistoryClick) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Ver histórico dos meses"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Linha com os valores de Entradas, Saídas e Total
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), // Garante que o Divider tenha a altura da Row
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Entradas",
                    value = currencyFormatter.format(revenue),
                    valueColor = MaterialTheme.colorScheme.primary
                )

                VerticalDivider()

                SummaryItem(
                    label = "Saídas",
                    value = currencyFormatter.format(expenses),
                    valueColor = MaterialTheme.colorScheme.error
                )

                VerticalDivider()

                SummaryItem(
                    label = "Total",
                    value = currencyFormatter.format(total),
                    valueColor = if (total >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            HorizontalDivider()
            TextButton(onClick = onCloseMonth,
                modifier = Modifier.align(Alignment.End)) {
                Text("FECHAR MÊS", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/**
 * Item de resumo individual para ser usado dentro do [MonthlySummaryCard].
 */
@Composable
private fun RowScope.SummaryItem(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Um Divider vertical simples.
 */
@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}


// Preview para visualizar o novo card
@Preview(showBackground = true)
@Composable
fun MonthlySummaryCardPreview() {
    StockTheme {
        MonthlySummaryCard(
            month = "Outubro",
            revenue = 12540.75,
            expenses = 8320.50,
            onHistoryClick = {},
            onCloseMonth = {}
        )
    }
}
