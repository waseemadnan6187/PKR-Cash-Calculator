package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PakistaniCurrency
import com.example.util.CurrencyUtils
import com.example.viewmodel.CashCounterUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreakdownScreen(
    uiState: CashCounterUiState,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeNotes = PakistaniCurrency.NOTES.map { note ->
        val count = uiState.noteCounts[note.value] ?: 0
        val subtotal = note.value.toLong() * count
        val share = if (uiState.grandTotalAmount > 0) subtotal.toDouble() / uiState.grandTotalAmount else 0.0
        NoteStat(
            denomination = note,
            count = count,
            packets = count / 100,
            loose = count % 100,
            subtotal = subtotal,
            share = share
        )
    }.filter { it.count > 0 }

    val activeCoins = if (uiState.showCoins) {
        PakistaniCurrency.COINS.map { coin ->
            val count = uiState.coinCounts[coin.value] ?: 0
            val subtotal = coin.value.toLong() * count
            val share = if (uiState.grandTotalAmount > 0) subtotal.toDouble() / uiState.grandTotalAmount else 0.0
            NoteStat(
                denomination = coin,
                count = count,
                packets = 0,
                loose = count,
                subtotal = subtotal,
                share = share
            )
        }.filter { it.count > 0 }
    } else emptyList()

    val allActive = activeNotes + activeCoins

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("breakdown_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Overview Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CASH DENOMINATION SUMMARY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = CurrencyUtils.formatPkr(uiState.grandTotalAmount, uiState.useSouthAsianCommas),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.totalNotesCount} Notes (${activeNotes.sumOf { it.packets }} Packets, ${activeNotes.sumOf { it.loose }} Loose)" +
                                if (uiState.showCoins && uiState.totalCoinsCount > 0) " + ${uiState.totalCoinsCount} Coins" else "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Multi-Color Distribution Bar
        if (uiState.grandTotalAmount > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Value Distribution",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Segmented color bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            allActive.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .weight((item.share * 100).toFloat().coerceAtLeast(0.01f))
                                        .fillMaxHeight()
                                        .background(item.denomination.themeColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Legend Chips
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allActive.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(item.denomination.themeColor)
                                    )
                                    Text(
                                        text = "${item.denomination.label}: ${(item.share * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Detailed Table Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Denomination Breakdown Table",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (uiState.grandTotalAmount > 0) {
                    FilledTonalButton(
                        onClick = onShareClick,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Share Slip", fontSize = 12.sp)
                    }
                }
            }
        }

        if (allActive.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Cash Count Entered Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter note counts on the Calculator tab to see instant analytics and packet breakdowns.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(allActive) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Badge & count details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = item.denomination.themeColor,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${item.denomination.value}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = item.denomination.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (item.denomination.isNote) {
                                        if (item.packets > 0) {
                                            "${item.packets} Pkts (100x) + ${item.loose} Loose = ${item.count} pcs"
                                        } else {
                                            "${item.count} loose notes"
                                        }
                                    } else {
                                        "${item.count} coins"
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Right: Subtotal & Share %
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = CurrencyUtils.formatPkr(item.subtotal, uiState.useSouthAsianCommas),
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = item.denomination.themeColor
                            )
                            Text(
                                text = String.format("%.1f%% of total", item.share * 100),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class NoteStat(
    val denomination: com.example.model.Denomination,
    val count: Int,
    val packets: Int,
    val loose: Int,
    val subtotal: Long,
    val share: Double
)
