// ... (imports permanecem os mesmos)
import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration // ✅ Adicione este import
import androidx.compose.ui.unit.dp
import com.meu.stock.model.Product

/**
 * Um card de alerta que aparece quando o estoque de um ou mais itens está baixo.
 *
 * @param products A lista de produtos com estoque baixo.
 * @param onProductClick Ação para navegar até um produto específico.
 */
@Composable
fun LowStockWarningCard(
    products: List<Product>,
    onProductClick: (Long, Long) -> Unit
) {

    if (products.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF180303)
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabeçalho do Alerta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Alerta",
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Estoque Baixo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(Modifier.height(8.dp))

            Column {
                Text(
                    text = "Há ${products.size} produto(s) precisando de atenção:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(16.dp))

            // Corpo com os detalhes dos produtos (continua igual)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                products.take(products.size).forEach { product ->
                    Column {
                        ProductInfoRow(product) {
                            onProductClick(
                                product.id!!,
                                product.categoryId
                            )
                        }
                        if(products.indexOf(product) != products.size - 1){
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.White
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun ProductInfoRow(
    product: Product,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "ID: ${product.id}  •  ${product.categoryName}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Restam: ${product.stockQuantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                TextButton(
                    onClick = onClick,
                ) {
                    Text(
                        text = "ver mais",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

        }
    }
}