package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PakistaniCurrency
import com.example.ui.screens.SimpleMathEvaluator
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCalculatorBottomSheet(
    currentCashTotal: Long,
    onSetAsTarget: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }

    fun evaluateExpression(expr: String): String {
        if (expr.isBlank()) return "0"
        return try {
            val sanitized = expr
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace(",", "")
            val eval = SimpleMathEvaluator.evaluate(sanitized)
            if (eval % 1.0 == 0.0) {
                eval.toLong().toString()
            } else {
                DecimalFormat("#.##").format(eval)
            }
        } catch (e: Exception) {
            "..."
        }
    }

    fun onKeyPress(key: String) {
        when (key) {
            "C" -> {
                expression = ""
                resultText = "0"
            }
            "DEL" -> {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    resultText = evaluateExpression(expression)
                }
            }
            "=" -> {
                if (expression.isNotEmpty()) {
                    val res = evaluateExpression(expression)
                    if (res != "..." && res != "Error") {
                        expression = res
                        resultText = res
                    }
                }
            }
            "%" -> {
                if (expression.isNotEmpty()) {
                    expression += "%"
                    resultText = evaluateExpression(expression)
                }
            }
            "+", "−", "×", "÷" -> {
                if (expression.isNotEmpty()) {
                    val last = expression.last()
                    if (last in listOf('+', '−', '×', '÷')) {
                        expression = expression.dropLast(1) + key
                    } else {
                        expression += key
                    }
                } else if (key == "−") {
                    expression += key
                }
            }
            else -> {
                expression += key
                resultText = evaluateExpression(expression)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("quick_calculator_sheet"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Quick Math Calculator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (currentCashTotal > 0) {
                        AssistChip(
                            onClick = {
                                if (expression.isEmpty() || expression.endsWith("+") || expression.endsWith("−") || expression.endsWith("×") || expression.endsWith("÷")) {
                                    expression += currentCashTotal.toString()
                                } else {
                                    expression = currentCashTotal.toString()
                                }
                                resultText = evaluateExpression(expression)
                            },
                            label = { Text("₨ Total", fontSize = 10.sp) }
                        )
                    }

                    if (resultText != "0" && resultText != "..." && resultText != "Error") {
                        AssistChip(
                            onClick = {
                                val amt = resultText.toLongOrNull() ?: resultText.toDoubleOrNull()?.toLong()
                                if (amt != null && amt > 0) {
                                    onSetAsTarget(amt)
                                    onDismiss()
                                }
                            },
                            label = { Text("Set Target", fontSize = 10.sp) }
                        )
                    }
                }
            }

            // Display Box
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    val scrollState = rememberScrollState()
                    LaunchedEffect(expression) {
                        scrollState.scrollTo(scrollState.maxValue)
                    }
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    )
                    Text(
                        text = resultText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Quick Note Adder row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val notes = listOf(5000, 1000, 500, 100, 50, 20, 10)
                items(notes) { valNote ->
                    val noteObj = PakistaniCurrency.NOTES.find { it.value == valNote }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = noteObj?.containerColor ?: MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            noteObj?.themeColor?.copy(alpha = 0.4f) ?: Color.Transparent
                        ),
                        modifier = Modifier.clickable {
                            if (expression.isEmpty() || expression.endsWith("+") || expression.endsWith("−") || expression.endsWith("×") || expression.endsWith("÷")) {
                                expression += valNote.toString()
                            } else {
                                expression += "+$valNote"
                            }
                            resultText = evaluateExpression(expression)
                        }
                    ) {
                        Text(
                            text = "+$valNote",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = noteObj?.themeColor ?: MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Keypad Grid (5 rows x 4 cols)
            val rows = listOf(
                listOf("C", "%", "DEL", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "−"),
                listOf("1", "2", "3", "+"),
                listOf("00", "0", ".", "=")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (row in rows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (key in row) {
                            val isEquals = key == "="
                            val isOp = key in listOf("÷", "×", "−", "+", "=")
                            val isAct = key in listOf("C", "DEL", "%")

                            val btnColor = when {
                                isEquals -> MaterialTheme.colorScheme.primary
                                isOp -> MaterialTheme.colorScheme.primaryContainer
                                isAct -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            }
                            val txtColor = when {
                                isEquals -> MaterialTheme.colorScheme.onPrimary
                                isOp -> MaterialTheme.colorScheme.onPrimaryContainer
                                isAct -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Button(
                                onClick = { onKeyPress(key) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    text = key,
                                    fontSize = if (key.length > 1) 14.sp else 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = txtColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
