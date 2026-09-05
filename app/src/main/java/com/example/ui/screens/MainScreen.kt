package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PacketMultiplierDialog
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.QuickCalculatorBottomSheet
import com.example.ui.components.SaveSlipDialog
import com.example.ui.components.TargetAmountDialog
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.viewmodel.CashAppTab
import com.example.viewmodel.CashCounterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CashCounterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedSlips by viewModel.savedSlips.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isQuickCalculatorOpen by remember { mutableStateOf(false) }
    var isPrivacyPolicyOpen by remember { mutableStateOf(false) }

    // Handle snackbars & undo actions
    LaunchedEffect(uiState.showSnackbarMessage) {
        uiState.showSnackbarMessage?.let { message ->
            coroutineScope.launch {
                val hasUndo = uiState.lastClearedNoteCounts != null || uiState.lastClearedCoinCounts != null
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = if (hasUndo) "UNDO" else null,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoClear()
                }
                viewModel.dismissSnackbar()
            }
        }
    }

    // Share Helper
    val onShareSlip: (String) -> Unit = { textToShare ->
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Pakistani Cash Slip")
        context.startActivity(shareIntent)
    }

    // Dialogs
    if (isQuickCalculatorOpen) {
        QuickCalculatorBottomSheet(
            currentCashTotal = uiState.grandTotalAmount,
            onSetAsTarget = { targetAmt -> viewModel.setTargetAmount(targetAmt) },
            onDismiss = { isQuickCalculatorOpen = false }
        )
    }

    if (uiState.isSaveDialogOpen) {
        SaveSlipDialog(
            totalAmount = uiState.grandTotalAmount,
            totalNotes = uiState.totalPiecesCount,
            useSouthAsianCommas = uiState.useSouthAsianCommas,
            onDismiss = { viewModel.openSaveDialog(false) },
            onConfirmSave = { title, remarks ->
                viewModel.saveSlip(title, remarks)
            }
        )
    }

    if (uiState.isTargetDialogOpen) {
        TargetAmountDialog(
            currentTarget = uiState.targetAmount,
            useSouthAsianCommas = uiState.useSouthAsianCommas,
            onDismiss = { viewModel.openTargetDialog(false) },
            onSetTarget = { viewModel.setTargetAmount(it) }
        )
    }

    if (uiState.isPacketMultiplierOpen && uiState.selectedDenominationForPacket != null) {
        val denomValue = uiState.selectedDenominationForPacket!!
        val isNote = denomValue > 10 || denomValue == 75 || denomValue == 50 || denomValue == 20 || denomValue == 10
        val currentCount = if (isNote) uiState.noteCounts[denomValue] ?: 0 else uiState.coinCounts[denomValue] ?: 0

        PacketMultiplierDialog(
            denominationValue = denomValue,
            currentCount = currentCount,
            useSouthAsianCommas = uiState.useSouthAsianCommas,
            onDismiss = { viewModel.openPacketMultiplier(null) },
            onApplyCount = { newCount ->
                viewModel.setCount(denomValue, isNote, newCount)
                viewModel.openPacketMultiplier(null)
            }
        )
    }

    if (isPrivacyPolicyOpen) {
        PrivacyPolicyDialog(
            onDismiss = { isPrivacyPolicyOpen = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Gold500,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "₨",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Emerald900
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "PKR Cash Counter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "پاکستان کرنسی کاؤنٹر و کیلکولیٹر",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Quick Calculator trigger
                    IconButton(
                        onClick = { isQuickCalculatorOpen = true },
                        modifier = Modifier.testTag("top_calculator_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Quick Calculator",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Privacy Policy & Link trigger
                    IconButton(
                        onClick = { isPrivacyPolicyOpen = true },
                        modifier = Modifier.testTag("top_privacy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = "Privacy Policy & Link",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (uiState.activeTab == CashAppTab.CALCULATOR && uiState.grandTotalAmount > 0) {
                        IconButton(
                            onClick = { onShareSlip(viewModel.generateShareSlipText()) },
                            modifier = Modifier.testTag("top_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Slip",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == CashAppTab.CALCULATOR,
                    onClick = { viewModel.setTab(CashAppTab.CALCULATOR) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == CashAppTab.CALCULATOR) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Cash Counter"
                        )
                    },
                    label = { Text("Counter", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_counter_tab")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == CashAppTab.MATH_CALCULATOR,
                    onClick = { viewModel.setTab(CashAppTab.MATH_CALCULATOR) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == CashAppTab.MATH_CALCULATOR) Icons.Filled.Calculate else Icons.Outlined.Calculate,
                            contentDescription = "Math Calculator"
                        )
                    },
                    label = { Text("Calculator", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_calculator_tab")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == CashAppTab.BREAKDOWN,
                    onClick = { viewModel.setTab(CashAppTab.BREAKDOWN) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == CashAppTab.BREAKDOWN) Icons.Filled.PieChart else Icons.Outlined.PieChart,
                            contentDescription = "Breakdown"
                        )
                    },
                    label = { Text("Breakdown", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_breakdown_tab")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == CashAppTab.SAVED_SLIPS,
                    onClick = { viewModel.setTab(CashAppTab.SAVED_SLIPS) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (savedSlips.isNotEmpty()) {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text("${savedSlips.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.activeTab == CashAppTab.SAVED_SLIPS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                contentDescription = "Saved Slips"
                            )
                        }
                    },
                    label = { Text("Saved Slips", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_history_tab")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                CashAppTab.CALCULATOR -> {
                    CalculatorScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onShareClick = { onShareSlip(viewModel.generateShareSlipText()) },
                        onOpenCalculatorSheet = { isQuickCalculatorOpen = true },
                        onOpenPrivacyPolicy = { isPrivacyPolicyOpen = true }
                    )
                }
                CashAppTab.MATH_CALCULATOR -> {
                    MathCalculatorScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
                CashAppTab.BREAKDOWN -> {
                    BreakdownScreen(
                        uiState = uiState,
                        onShareClick = { onShareSlip(viewModel.generateShareSlipText()) }
                    )
                }
                CashAppTab.SAVED_SLIPS -> {
                    HistoryScreen(
                        slips = savedSlips,
                        useSouthAsianCommas = uiState.useSouthAsianCommas,
                        onLoadSlip = { slip -> viewModel.loadSlip(slip) },
                        onDeleteSlip = { slip -> viewModel.deleteSlip(slip) },
                        onShareSlipText = { text -> onShareSlip(text) },
                        onOpenPrivacyPolicy = { isPrivacyPolicyOpen = true }
                    )
                }
            }
        }
    }
}
