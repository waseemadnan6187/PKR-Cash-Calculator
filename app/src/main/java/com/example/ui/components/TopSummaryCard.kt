package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.CurrencyUtils
import com.example.viewmodel.CashCounterUiState

@Composable
fun TopSummaryCard(
    uiState: CashCounterUiState,
    onClearAll: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onTargetClick: () -> Unit,
    onToggleCoins: () -> Unit,
    onToggleFormat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showUrduWords by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
            .testTag("top_summary_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald900)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Emerald900,
                            Emerald800,
                            Color(0xFF003D24)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header row with App Branding & Quick format toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Gold500.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "₨",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Gold400
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "PKR CASH TOTAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = Emerald100.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "کل رقم",
                                fontSize = 10.sp,
                                color = Gold400.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Format Toggle Badge (South Asian 1,00,000 vs 100,000)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggleFormat() }
                            .testTag("format_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Toggle currency format",
                                tint = Emerald100,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (uiState.useSouthAsianCommas) "Lakh Format (1,00,000)" else "Std (100,000)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Emerald100
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Grand Total Display
                Text(
                    text = CurrencyUtils.formatPkr(uiState.grandTotalAmount, uiState.useSouthAsianCommas),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("grand_total_text")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Pieces Badges (Notes Count + Coins Count)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notes Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = Gold400,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${uiState.totalNotesCount} Notes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // Coins Badge (if active)
                    if (uiState.showCoins && uiState.totalCoinsCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = Gold400,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${uiState.totalCoinsCount} Coins",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Total Pieces
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Gold500.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "Total: ${uiState.totalPiecesCount} pcs",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold400,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Words Conversion Card (Tap to toggle English / Urdu)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showUrduWords = !showUrduWords }
                        .testTag("in_words_container")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (showUrduWords) "اردو میں (In Urdu Words)" else "IN ENGLISH WORDS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gold400,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Tap to switch language",
                                fontSize = 9.sp,
                                color = Emerald100.copy(alpha = 0.6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (showUrduWords) uiState.urduInWords else uiState.englishInWords,
                            fontSize = if (showUrduWords) 15.sp else 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Target Cash Reconciliation Banner (if set)
                AnimatedVisibility(
                    visible = uiState.targetAmount != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    uiState.targetAmount?.let { target ->
                        val diff = uiState.grandTotalAmount - target
                        val isBalanced = diff == 0L
                        val isSurplus = diff > 0

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isBalanced -> Color(0xFF0F5132).copy(alpha = 0.9f)
                                isSurplus -> Color(0xFF134E4A).copy(alpha = 0.9f)
                                else -> Color(0xFF842029).copy(alpha = 0.9f)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onTargetClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Target: ${CurrencyUtils.formatPkr(target, uiState.useSouthAsianCommas)}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = when {
                                            isBalanced -> "✅ Exact Balanced"
                                            isSurplus -> "🟢 Surplus: +${CurrencyUtils.formatPkr(diff, uiState.useSouthAsianCommas)}"
                                            else -> "🔴 Shortage: -${CurrencyUtils.formatPkr(-diff, uiState.useSouthAsianCommas)}"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit target",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fast Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Target Button
                    ActionButton(
                        icon = Icons.Outlined.TrackChanges,
                        label = if (uiState.targetAmount == null) "Target" else "Targeted",
                        onClick = onTargetClick,
                        testTag = "target_cash_button"
                    )

                    // Coins Toggle Button
                    ActionButton(
                        icon = if (uiState.showCoins) Icons.Filled.MonetizationOn else Icons.Outlined.MonetizationOn,
                        label = if (uiState.showCoins) "Hide Coins" else "+ Coins",
                        onClick = onToggleCoins,
                        testTag = "toggle_coins_button"
                    )

                    // Save Slip Button
                    ActionButton(
                        icon = Icons.Outlined.BookmarkAdd,
                        label = "Save Slip",
                        onClick = onSaveClick,
                        highlight = true,
                        testTag = "save_slip_button"
                    )

                    // Share Slip Button
                    ActionButton(
                        icon = Icons.Outlined.Share,
                        label = "Share",
                        onClick = onShareClick,
                        testTag = "share_slip_button"
                    )

                    // Reset / Clear All Button
                    ActionButton(
                        icon = Icons.Outlined.Refresh,
                        label = "Clear",
                        onClick = onClearAll,
                        testTag = "clear_all_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    highlight: Boolean = false,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (highlight) Gold500 else Color.White.copy(alpha = 0.15f),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .defaultMinSize(minWidth = 52.dp, minHeight = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (highlight) Emerald900 else Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
                color = if (highlight) Emerald900 else Color.White
            )
        }
    }
}
