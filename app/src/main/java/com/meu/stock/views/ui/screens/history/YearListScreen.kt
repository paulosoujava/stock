package com.meu.stock.views.ui.screens.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.meu.stock.model.MonthSummary
import com.meu.stock.model.SalesMonth
import com.meu.stock.model.SalesYear
import com.meu.stock.views.ui.components.AlertCloseMonth
import com.meu.stock.views.ui.components.DefaultTabBar
import com.meu.stock.views.ui.utils.generateRandomPastelColor
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.text.format

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearListScreen(
    navController: NavController,
    viewModel: YearListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentDate = remember { LocalDate.now() }
    val currentYear = currentDate.year
    val currentMonth = currentDate.monthValue


    Scaffold(
        topBar = {
            DefaultTabBar(
                title = "Histórico de Vendas",
                navController = navController
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.years.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma venda registrada ainda.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.years, key = { it.year }) { salesYear ->
                    YearItem(
                        salesYear = salesYear,
                        currentYear = currentYear,
                        currentMonth = currentMonth
                    )
                }
            }
        }
    }
}


@Composable
fun YearItem(
    salesYear: SalesYear,
    currentYear: Int,
    currentMonth: Int
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val isCurrentYear = salesYear.year == currentYear

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = generateRandomPastelColor())
    ) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .background(color = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${salesYear.year}",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .width(210.dp),
                        color = Color.Black
                    )
                    Text(
                        text = "Total: " + currencyFormatter.format(salesYear.total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    salesYear.months.forEach { salesMonth ->
                        MonthItem(
                            salesMonth = salesMonth,
                            isCurrentMonth = isCurrentYear && salesMonth.monthNumber == currentMonth
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthItem(
    salesMonth: SalesMonth,
    isCurrentMonth: Boolean
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Color.Black
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = salesMonth.monthName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal
            )

            // ✅ 4. Adiciona o ícone se for o mês atual
            if (isCurrentMonth) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "Mês Atual",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = currencyFormatter.format(salesMonth.total),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}