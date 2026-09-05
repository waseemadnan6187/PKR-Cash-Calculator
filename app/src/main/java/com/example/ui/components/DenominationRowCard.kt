package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Denomination
import com.example.ui.theme.*
import com.example.util.CurrencyUtils

@Composable
fun DenominationRowCard(
    denomination: Denomination,
    count: Int,
    useSouthAsianCommas: Boolean,
    grandTotalAmount: Long,
    onCountChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onAddPackets: (Int) -> Unit,
    onOpenPacketMultiplier: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Next,
    onNextAction: () -> Unit = {},
    onPreviewNote: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val subtotal = denomination.value.toLong() * count
    val percentage = if (grandTotalAmount > 0) (subtotal.toFloat() / grandTotalAmount * 100).toInt() else 0
    val hasCount = count > 0

    // Local text state with selection support for smooth keyboard editing
    var textFieldValue by remember(count) {
        val initialText = if (count == 0) "" else count.toString()
        mutableStateOf(TextFieldValue(text = initialText, selection = TextRange(initialText.length)))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("denomination_card_${denomination.value}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasCount) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (hasCount) 3.dp else 1.dp
        ),
        border = if (hasCount) {
            CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(denomination.themeColor.copy(alpha = 0.5f)),
                width = 1.5.dp
            )
        } else {
            CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                width = 0.8.dp
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Row: Denomination Badge, Subtitle & Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Denomination Visual Badge & Note Thumbnail
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (denomination.isNote) {
                        // Pakistani Note Miniature Image & Preview trigger
                        PakistaniNoteThumbnail(
                            denomination = denomination,
                            onClick = onPreviewNote
                        )
                    }

                    // Distinctive Note Color Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = denomination.themeColor,
                        modifier = Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = denomination.label,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Urdu Title & Landmark info
                    Column {
                        Text(
                            text = denomination.titleUrdu,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = denomination.subtitle,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right: Calculated Subtotal
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyUtils.formatPkr(subtotal, useSouthAsianCommas),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasCount) denomination.themeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("subtotal_${denomination.value}")
                    )

                    if (hasCount && percentage > 0) {
                        Text(
                            text = "$percentage% of total",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Input Controls, Stepper & Quick Multipliers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stepper Counter: [ – ] [ Count Input ] [ + ]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Decrement Button (48dp target)
                    IconButton(
                        onClick = {
                            onDecrement()
                            focusManager.clearFocus()
                        },
                        enabled = count > 0,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("decrement_${denomination.value}")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (count > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrement ${denomination.label}",
                                    tint = if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Direct Numeric Count TextField
                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .border(
                                width = 1.dp,
                                color = if (hasCount) denomination.themeColor.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = textFieldValue,
                            onValueChange = { newVal ->
                                val digitsOnly = newVal.text.filter { it.isDigit() }.take(6)
                                textFieldValue = newVal.copy(text = digitsOnly, selection = TextRange(digitsOnly.length))
                                onCountChange(digitsOnly)
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = imeAction
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { onNextAction() },
                                onDone = {
                                    if (imeAction == ImeAction.Next) {
                                        onNextAction()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                },
                                onGo = { onNextAction() },
                                onSend = { onNextAction() }
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(denomination.themeColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused && textFieldValue.text.isNotEmpty()) {
                                        // Select all on focus so user can immediately type new number
                                        textFieldValue = textFieldValue.copy(
                                            selection = TextRange(0, textFieldValue.text.length)
                                        )
                                    }
                                }
                                .onPreviewKeyEvent { event ->
                                    if ((event.key == Key.Enter || event.key == Key.NumPadEnter) && event.type == KeyEventType.KeyUp) {
                                        onNextAction()
                                        true
                                    } else {
                                        false
                                    }
                                }
                                .testTag("count_input_${denomination.value}"),
                            decorationBox = { innerTextField ->
                                if (textFieldValue.text.isEmpty()) {
                                    Text(
                                        text = "0",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    // Increment Button (48dp target)
                    IconButton(
                        onClick = {
                            onIncrement()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("increment_${denomination.value}")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increment ${denomination.label}",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Fast Action Buttons: [+100 / Pkt], [+Packet Dialog], [✕ Clear]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (denomination.isNote) {
                        // Quick +100 (1 Packet / Gaddi)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = denomination.themeColor.copy(alpha = 0.12f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onAddPackets(1)
                                    focusManager.clearFocus()
                                }
                                .testTag("add_100_${denomination.value}")
                        ) {
                            Text(
                                text = "+100 Pkt",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = denomination.themeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }

                        // Bundles / Multiple Packets button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onOpenPacketMultiplier()
                                    focusManager.clearFocus()
                                }
                                .testTag("packet_dialog_${denomination.value}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = "Bundle calculator",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Bundles",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // Quick Coin adders: +10, +50
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onAddPackets(1) // +10 for coins
                                    focusManager.clearFocus()
                                }
                        ) {
                            Text(
                                text = "+10",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Single Row Clear Button
                    AnimatedVisibility(
                        visible = hasCount,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = {
                                onClear()
                                focusManager.clearFocus()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("clear_${denomination.value}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear ${denomination.label}",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Progress / Share Line
            if (hasCount && grandTotalAmount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val fraction = (subtotal.toFloat() / grandTotalAmount).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape),
                    color = denomination.themeColor,
                    trackColor = denomination.themeColor.copy(alpha = 0.15f)
                )
            }
        }
    }
}
