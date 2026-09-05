package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Denomination
import com.example.model.PakistaniCurrency
import com.example.ui.components.DenominationRowCard
import com.example.ui.components.NoteDetailDialog
import com.example.ui.components.TopSummaryCard
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.viewmodel.CashCounterUiState
import com.example.viewmodel.CashCounterViewModel
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    uiState: CashCounterUiState,
    viewModel: CashCounterViewModel,
    onShareClick: () -> Unit,
    onOpenCalculatorSheet: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var selectedNoteForPreview by remember { mutableStateOf<Denomination?>(null) }

    // Focus requesters for seamless Enter-to-next-note navigation
    val noteFocusRequesters = remember {
        PakistaniCurrency.NOTES.associate { it.value to FocusRequester() }
    }
    val coinFocusRequesters = remember {
        PakistaniCurrency.COINS.associate { it.value to FocusRequester() }
    }

    // Note Details Dialog
    selectedNoteForPreview?.let { note ->
        NoteDetailDialog(
            denomination = note,
            onDismiss = { selectedNoteForPreview = null }
        )
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sticky/Hero Top Summary (Index 0)
        item {
            TopSummaryCard(
                uiState = uiState,
                onClearAll = { viewModel.clearAll() },
                onSaveClick = { viewModel.openSaveDialog(true) },
                onShareClick = onShareClick,
                onTargetClick = { viewModel.openTargetDialog(true) },
                onToggleCoins = { viewModel.toggleCoins() },
                onToggleFormat = { viewModel.toggleFormat() }
            )
        }

        // Pakistani Currency Hero Banner & Quick Math Launcher
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left image thumbnail and banner text
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_pkr_notes_stack_1787993959789),
                            contentDescription = "Pakistani Banknotes",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp, 36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Gold400.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        )

                        Column {
                            Text(
                                text = "Pakistani Rupee Notes (PKR)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap note image to preview watermark & security info",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Quick Calculator Button
                    FilledTonalButton(
                        onClick = onOpenCalculatorSheet,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Math Calc", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section Title: Currency Notes (Index 2)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAKISTANI CURRENCY NOTES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "کرنسی نوٹ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Notes List (5000 down to 10)
        itemsIndexed(PakistaniCurrency.NOTES, key = { _, it -> "note_${it.value}" }) { index, note ->
            val count = uiState.noteCounts[note.value] ?: 0
            val isLastNote = index == PakistaniCurrency.NOTES.lastIndex
            val imeAction = if (isLastNote && !uiState.showCoins) ImeAction.Done else ImeAction.Next

            DenominationRowCard(
                denomination = note,
                count = count,
                useSouthAsianCommas = uiState.useSouthAsianCommas,
                grandTotalAmount = uiState.grandTotalAmount,
                onCountChange = { viewModel.updateCount(note.value, true, it) },
                onIncrement = { viewModel.increment(note.value, true, 1) },
                onDecrement = { viewModel.decrement(note.value, true, 1) },
                onAddPackets = { packets -> viewModel.addPackets(note.value, true, packets) },
                onOpenPacketMultiplier = { viewModel.openPacketMultiplier(note.value) },
                onClear = { viewModel.clearDenomination(note.value, true) },
                focusRequester = noteFocusRequesters[note.value],
                imeAction = imeAction,
                onPreviewNote = { selectedNoteForPreview = note },
                onNextAction = {
                    if (!isLastNote) {
                        val nextNote = PakistaniCurrency.NOTES[index + 1]
                        coroutineScope.launch {
                            // Smooth scroll so the next row is visible
                            lazyListState.animateScrollToItem(3 + index + 1)
                            noteFocusRequesters[nextNote.value]?.requestFocus()
                        }
                    } else if (uiState.showCoins && PakistaniCurrency.COINS.isNotEmpty()) {
                        val firstCoin = PakistaniCurrency.COINS[0]
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(3 + PakistaniCurrency.NOTES.size + 1)
                            coinFocusRequesters[firstCoin.value]?.requestFocus()
                        }
                    } else {
                        focusManager.clearFocus()
                    }
                }
            )
        }

        // Optional Coins Section
        if (uiState.showCoins) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COINS (سکے)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    TextButton(
                        onClick = { viewModel.toggleCoins(false) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Hide Coins",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            itemsIndexed(PakistaniCurrency.COINS, key = { _, it -> "coin_${it.value}" }) { index, coin ->
                val count = uiState.coinCounts[coin.value] ?: 0
                val isLastCoin = index == PakistaniCurrency.COINS.lastIndex
                val imeAction = if (isLastCoin) ImeAction.Done else ImeAction.Next

                DenominationRowCard(
                    denomination = coin,
                    count = count,
                    useSouthAsianCommas = uiState.useSouthAsianCommas,
                    grandTotalAmount = uiState.grandTotalAmount,
                    onCountChange = { viewModel.updateCount(coin.value, false, it) },
                    onIncrement = { viewModel.increment(coin.value, false, 1) },
                    onDecrement = { viewModel.decrement(coin.value, false, 1) },
                    onAddPackets = { delta -> viewModel.increment(coin.value, false, delta * 10) },
                    onOpenPacketMultiplier = { viewModel.openPacketMultiplier(coin.value) },
                    onClear = { viewModel.clearDenomination(coin.value, false) },
                    focusRequester = coinFocusRequesters[coin.value],
                    imeAction = imeAction,
                    onPreviewNote = { selectedNoteForPreview = coin },
                    onNextAction = {
                        if (!isLastCoin) {
                            val nextCoin = PakistaniCurrency.COINS[index + 1]
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(3 + PakistaniCurrency.NOTES.size + 1 + index + 1)
                                coinFocusRequesters[nextCoin.value]?.requestFocus()
                            }
                        } else {
                            focusManager.clearFocus()
                        }
                    }
                )
            }
        }

        // Privacy Guarantee and Policy Link Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPrivacyPolicy() }
                    .testTag("calculator_privacy_footer_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "100% Offline & Private • رازداری کی پالیسی",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Zero data collected. View official policy & link.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Privacy Policy",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Bottom space so floating bars don't obstruct
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

