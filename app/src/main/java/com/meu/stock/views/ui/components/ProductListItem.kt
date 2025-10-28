package com.meu.stock.views.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meu.stock.model.Product
import com.meu.stock.ui.theme.StockTheme
import com.meu.stock.views.ui.utils.generateRandomPastelColor
import com.meu.stock.views.ui.utils.toCurrencyString


@Preview
@Composable
private fun Show() {
    StockTheme {
        ProductListItem(
            product = Product(
                id = 0L,
                name = "Produto de Teste",
                description = "Descrição do produto de teste",
                priceUnit = 10.0,
                priceSale = 15.0,
                stockQuantity = 10,
                categoryId = 2L,
                categoryName = "Categoria de Teste",
                lowStockQuantity = 1
            ),
            onEditClick = { },
            onPrintClick = { }

        )
    }
}

@Composable
fun ProductListItem(
    product: Product,
    onEditClick: () -> Unit,
    onPrintClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            generateRandomPastelColor()
        )
    ) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QrCodeImage(
                    content = product.id.toString(),
                    modifier = Modifier.size(40.dp).clickable(onClick = onPrintClick),
                    size = 250
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar Produto",
                        tint = Color(0xFF467548)
                    )
                }
            }

            if (product.description.isNotBlank()) {
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoColumn(
                    icon = Icons.Default.ArrowDownward,
                    label = "Custo",
                    color = Color(0xFF5E2639),
                    value = product.priceUnit.toCurrencyString()
                )
                InfoColumn(
                    icon = Icons.Default.ArrowUpward,
                    color = Color(0xFF467548),
                    label = "Venda",
                    value = product.priceSale.toCurrencyString()
                )
                InfoColumn(
                    icon = Icons.Default.Inventory2,
                    color = if (product.stockQuantity <= product.lowStockQuantity) Color.Red else Color.Black,
                    label = "Estoque",
                    value = product.stockQuantity.toString()
                )
            }
        }
    }
}

@Composable
private fun InfoColumn(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
