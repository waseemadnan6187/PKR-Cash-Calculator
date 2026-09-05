package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PakistaniCurrency
import com.example.ui.theme.*
import com.example.util.CurrencyUtils
import com.example.viewmodel.CashCounterUiState
import com.example.viewmodel.CashCounterViewModel
import java.text.DecimalFormat

data class CalculationHistoryItem(
    val expression: String,
    val result: String,
    val timestamp: String = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
)

@Composable
fun MathCalculatorScreen(
    viewModel: CashCounterViewModel,
    uiState: CashCounterUiState,
    modifier: Modifier = Modifier
) {
    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }
    var history by remember { mutableStateOf(listOf<CalculationHistoryItem>()) }
    var showHistory by remember { mutableStateOf(false) }

    // Customer Change helper state
    var showChangeHelper by remember { mutableStateOf(false) }
    var billAmountInput by remember { mutableStateOf("") }
    var cashReceivedInput by remember { mutableStateOf("") }

    val formatter = remember(uiState.useSouthAsianCommas) {
        { num: Double -> CurrencyUtils.formatPkr(num.toLong(), uiState.useSouthAsianCommas) }
    }

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
                    val finalRes = evaluateExpression(expression)
                    if (finalRes != "..." && finalRes != "Error") {
                        history = listOf(CalculationHistoryItem(expression, finalRes)) + history.take(19)
                        expression = finalRes
                        resultText = finalRes
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
                    val lastChar = expression.last()
                    if (lastChar == '+' || lastChar == '−' || lastChar == '×' || lastChar == '÷') {
                        expression = expression.dropLast(1) + key
                    } else {
                        expression += key
                    }
                } else if (key == "−") {
                    expression += key
                }
            }
            else -> {
                // Digit or decimal
                expression += key
                resultText = evaluateExpression(expression)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("math_calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Display Screen Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top control bar inside display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cashier Calculator",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Import cash total
                        AssistChip(
                            onClick = {
                                val total = uiState.grandTotalAmount
                                if (total > 0) {
                                    if (expression.isEmpty() || expression.endsWith("+") || expression.endsWith("−") || expression.endsWith("×") || expression.endsWith("÷")) {
                                        expression += total.toString()
                                    } else {
                                        expression = total.toString()
                                    }
                                    resultText = evaluateExpression(expression)
                                }
                            },
                            label = { Text("₨ Total (${uiState.grandTotalAmount})", fontSize = 10.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(12.dp))
                            }
                        )

                        // Set as target
                        if (resultText != "0" && resultText != "..." && resultText != "Error") {
                            AssistChip(
                                onClick = {
                                    val amount = resultText.toLongOrNull() ?: resultText.toDoubleOrNull()?.toLong()
                                    if (amount != null && amount > 0) {
                                        viewModel.setTargetAmount(amount)
                                        viewModel.setTab(com.example.viewmodel.CashAppTab.CALCULATOR)
                                    }
                                },
                                label = { Text("Set Target", fontSize = 10.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.TrackChanges, null, modifier = Modifier.size(12.dp))
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Formula / Expression Text
                val scrollState = rememberScrollState()
                LaunchedEffect(expression) {
                    scrollState.scrollTo(scrollState.maxValue)
                }
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                )

                // Large Main Result
                Text(
                    text = resultText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Pakistani Quick Currency Adder Bar
        Text(
            text = "Quick PKR Note Adders:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val quickNotes = listOf(5000, 1000, 500, 100, 75, 50, 20, 10)
            items(quickNotes) { noteVal ->
                val note = PakistaniCurrency.NOTES.find { it.value == noteVal }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = note?.containerColor ?: MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        note?.themeColor?.copy(alpha = 0.4f) ?: Color.Transparent
                    ),
                    modifier = Modifier
                        .clickable {
                            if (expression.isEmpty() || expression.endsWith("+") || expression.endsWith("−") || expression.endsWith("×") || expression.endsWith("÷")) {
                                expression += noteVal.toString()
                            } else {
                                expression += "+$noteVal"
                            }
                            resultText = evaluateExpression(expression)
                        }
                        .testTag("quick_add_$noteVal")
                ) {
                    Text(
                        text = "+$noteVal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = note?.themeColor ?: MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Packet multiplier shortcuts
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable {
                        expression += "×100"
                        resultText = evaluateExpression(expression)
                    }
                ) {
                    Text(
                        text = "×100 (Packet)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Keypad Grid Layout (7x4 style for quick cashier usage)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val rows = listOf(
                listOf("C", "%", "DEL", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "−"),
                listOf("1", "2", "3", "+"),
                listOf("00", "0", ".", "=")
            )

            for (row in rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (key in row) {
                        val isOperator = key in listOf("÷", "×", "−", "+", "=")
                        val isAction = key in listOf("C", "DEL", "%")
                        val isEquals = key == "="

                        val btnColor = when {
                            isEquals -> MaterialTheme.colorScheme.primary
                            isOperator -> MaterialTheme.colorScheme.primaryContainer
                            isAction -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val textColor = when {
                            isEquals -> MaterialTheme.colorScheme.onPrimary
                            isOperator -> MaterialTheme.colorScheme.onPrimaryContainer
                            isAction -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Button(
                            onClick = { onKeyPress(key) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                            contentPadding = PaddingValues(0.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(
                                    1.dp,
                                    if (isEquals) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .testTag("calc_key_$key")
                        ) {
                            Text(
                                text = key,
                                fontSize = if (key.length > 1) 16.sp else 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simple arithmetic expression parser for basic math operations: +, -, *, /, %
 */
object SimpleMathEvaluator {
    fun evaluate(expression: String): Double {
        if (expression.isBlank()) return 0.0

        // Tokenize
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()

        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            if (c.isDigit() || c == '.') {
                currentNumber.append(c)
            } else if (c in listOf('+', '-', '*', '/', '%')) {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString())
                    currentNumber = StringBuilder()
                } else if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/"))) {
                    // Unary minus
                    currentNumber.append(c)
                    i++
                    continue
                }
                tokens.add(c.toString())
            }
            i++
        }
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }

        if (tokens.isEmpty()) return 0.0

        // First pass: *, /, %
        val pass1 = mutableListOf<String>()
        var idx = 0
        while (idx < tokens.size) {
            val token = tokens[idx]
            if (token == "*" || token == "/" || token == "%") {
                if (pass1.isEmpty()) return 0.0
                val prevVal = pass1.removeAt(pass1.lastIndex).toDoubleOrNull() ?: 0.0
                val nextVal = if (idx + 1 < tokens.size) tokens[idx + 1].toDoubleOrNull() ?: 1.0 else 1.0
                val res = when (token) {
                    "*" -> prevVal * nextVal
                    "/" -> if (nextVal != 0.0) prevVal / nextVal else 0.0
                    "%" -> prevVal * (nextVal / 100.0)
                    else -> 0.0
                }
                pass1.add(res.toString())
                idx += 2
            } else {
                pass1.add(token)
                idx++
            }
        }

        // Second pass: +, -
        var total = pass1.firstOrNull()?.toDoubleOrNull() ?: 0.0
        var pIdx = 1
        while (pIdx < pass1.size) {
            val op = pass1[pIdx]
            val nextVal = if (pIdx + 1 < pass1.size) pass1[pIdx + 1].toDoubleOrNull() ?: 0.0 else 0.0
            if (op == "+") {
                total += nextVal
            } else if (op == "-") {
                total -= nextVal
            }
            pIdx += 2
        }

        return total
    }
}
