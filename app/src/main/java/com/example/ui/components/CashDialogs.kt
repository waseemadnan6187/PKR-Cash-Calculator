package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PakistaniCurrency
import com.example.ui.theme.Emerald800
import com.example.util.CurrencyUtils

@Composable
fun SaveSlipDialog(
    totalAmount: Long,
    totalNotes: Int,
    useSouthAsianCommas: Boolean,
    onDismiss: () -> Unit,
    onConfirmSave: (title: String, remarks: String) -> Unit
) {
    var title by remember {
        mutableStateOf("Cash Count ${CurrencyUtils.formatDate(System.currentTimeMillis())}")
    }
    var remarks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Save Cash Slip",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = CurrencyUtils.formatPkr(totalAmount, useSouthAsianCommas),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$totalNotes Notes / Pieces",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Slip Title / Voucher Name") },
                    placeholder = { Text("e.g., Shop Evening Close") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_title_input")
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Notes / Cashier / Remarks (Optional)") },
                    placeholder = { Text("e.g., Deposited to HBL, Checked by Ali") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_remarks_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmSave(title, remarks) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_save_button")
            ) {
                Text("Save to History")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TargetAmountDialog(
    currentTarget: Long?,
    useSouthAsianCommas: Boolean,
    onDismiss: () -> Unit,
    onSetTarget: (Long?) -> Unit
) {
    var targetText by remember {
        mutableStateOf(currentTarget?.toString() ?: "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrackChanges,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Cash Target / Opening Balance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Set an expected drawer balance to automatically calculate cash shortage or surplus differences.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { input ->
                        targetText = input.filter { it.isDigit() }
                    },
                    label = { Text("Target Cash Amount (PKR)") },
                    placeholder = { Text("e.g. 500000") },
                    prefix = { Text("₨ ") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("target_amount_input")
                )

                // Quick preset buttons (e.g. 50k, 1 Lakh, 5 Lakh, 10 Lakh)
                Text(
                    text = "Quick Presets:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(50000L to "50K", 100000L to "1 Lakh", 500000L to "5 Lakh", 1000000L to "10 Lakh").forEach { (amount, label) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { targetText = amount.toString() }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = targetText.toLongOrNull()
                    onSetTarget(amount)
                },
                modifier = Modifier.testTag("set_target_confirm_button")
            ) {
                Text("Set Target")
            }
        },
        dismissButton = {
            Row {
                if (currentTarget != null) {
                    TextButton(
                        onClick = { onSetTarget(null) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Remove")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun PacketMultiplierDialog(
    denominationValue: Int,
    currentCount: Int,
    useSouthAsianCommas: Boolean,
    onDismiss: () -> Unit,
    onApplyCount: (Int) -> Unit
) {
    val denomination = PakistaniCurrency.NOTES.firstOrNull { it.value == denominationValue }
        ?: PakistaniCurrency.COINS.firstOrNull { it.value == denominationValue }

    val initialPackets = currentCount / 100
    val initialLoose = currentCount % 100

    var packetsText by remember { mutableStateOf(if (initialPackets > 0) initialPackets.toString() else "") }
    var looseText by remember { mutableStateOf(if (initialLoose > 0) initialLoose.toString() else "") }

    val packets = packetsText.toIntOrNull() ?: 0
    val loose = looseText.toIntOrNull() ?: 0
    val computedCount = (packets * 100) + loose
    val computedSubtotal = denominationValue.toLong() * computedCount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = denomination?.themeColor ?: Emerald800,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "₨",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Bundles / Packets: ${denomination?.label ?: ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Standard Pakistani currency packet (Gaddi) = 100 notes.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Packets (100x)
                    OutlinedTextField(
                        value = packetsText,
                        onValueChange = { input -> packetsText = input.filter { it.isDigit() }.take(4) },
                        label = { Text("Packets (×100)") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    // Loose notes
                    OutlinedTextField(
                        value = looseText,
                        onValueChange = { input -> looseText = input.filter { it.isDigit() }.take(4) },
                        label = { Text("Loose Notes") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Quick preset bundle increments
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 2, 5, 10).forEach { pkt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    packetsText = ((packetsText.toIntOrNull() ?: 0) + pkt).toString()
                                }
                        ) {
                            Text(
                                text = "+$pkt Pkt",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Result Preview Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$computedCount Total Notes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyUtils.formatPkr(computedSubtotal, useSouthAsianCommas),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApplyCount(computedCount) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Apply Count")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
