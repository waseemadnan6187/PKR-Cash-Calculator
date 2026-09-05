package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CashDatabase
import com.example.data.CashSlipEntity
import com.example.data.CashSlipRepository
import com.example.model.PakistaniCurrency
import com.example.util.CurrencyUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

data class CashCounterUiState(
    val noteCounts: Map<Int, Int> = mapOf(
        5000 to 0,
        1000 to 0,
        500 to 0,
        100 to 0,
        75 to 0,
        50 to 0,
        20 to 0,
        10 to 0
    ),
    val coinCounts: Map<Int, Int> = mapOf(
        10 to 0,
        5 to 0,
        2 to 0,
        1 to 0
    ),
    val showCoins: Boolean = false,
    val useSouthAsianCommas: Boolean = true,
    val targetAmount: Long? = null,
    val isTargetDialogOpen: Boolean = false,
    val isSaveDialogOpen: Boolean = false,
    val isPacketMultiplierOpen: Boolean = false,
    val selectedDenominationForPacket: Int? = null,
    val activeTab: CashAppTab = CashAppTab.CALCULATOR,
    val lastClearedNoteCounts: Map<Int, Int>? = null,
    val lastClearedCoinCounts: Map<Int, Int>? = null,
    val showSnackbarMessage: String? = null
) {
    val totalNoteAmount: Long
        get() = noteCounts.entries.sumOf { (value, count) -> value.toLong() * count }

    val totalCoinAmount: Long
        get() = if (showCoins) coinCounts.entries.sumOf { (value, count) -> value.toLong() * count } else 0L

    val grandTotalAmount: Long
        get() = totalNoteAmount + totalCoinAmount

    val totalNotesCount: Int
        get() = noteCounts.values.sum()

    val totalCoinsCount: Int
        get() = if (showCoins) coinCounts.values.sum() else 0

    val totalPiecesCount: Int
        get() = totalNotesCount + totalCoinsCount

    val englishInWords: String
        get() = CurrencyUtils.numberToPakistaniWords(grandTotalAmount)

    val urduInWords: String
        get() = CurrencyUtils.numberToUrduWords(grandTotalAmount)

    val cashDifference: Long?
        get() = targetAmount?.let { grandTotalAmount - it }
}

enum class CashAppTab {
    CALCULATOR,
    MATH_CALCULATOR,
    BREAKDOWN,
    SAVED_SLIPS
}

class CashCounterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CashSlipRepository

    init {
        val db = CashDatabase.getDatabase(application)
        repository = CashSlipRepository(db.cashSlipDao())
    }

    private val _uiState = MutableStateFlow(CashCounterUiState())
    val uiState: StateFlow<CashCounterUiState> = _uiState.asStateFlow()

    val savedSlips: StateFlow<List<CashSlipEntity>> = repository.allSlips
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateCount(value: Int, isNote: Boolean, countStr: String) {
        val count = countStr.filter { it.isDigit() }.toIntOrNull() ?: 0
        setCount(value, isNote, count.coerceIn(0, 999999))
    }

    fun setCount(value: Int, isNote: Boolean, count: Int) {
        val safeCount = count.coerceAtLeast(0)
        _uiState.update { current ->
            if (isNote) {
                current.copy(noteCounts = current.noteCounts + (value to safeCount))
            } else {
                current.copy(coinCounts = current.coinCounts + (value to safeCount))
            }
        }
    }

    fun increment(value: Int, isNote: Boolean, delta: Int = 1) {
        _uiState.update { current ->
            if (isNote) {
                val existing = current.noteCounts[value] ?: 0
                current.copy(noteCounts = current.noteCounts + (value to (existing + delta).coerceAtLeast(0)))
            } else {
                val existing = current.coinCounts[value] ?: 0
                current.copy(coinCounts = current.coinCounts + (value to (existing + delta).coerceAtLeast(0)))
            }
        }
    }

    fun decrement(value: Int, isNote: Boolean, delta: Int = 1) {
        increment(value, isNote, -delta)
    }

    fun addPackets(value: Int, isNote: Boolean, packets: Int) {
        // 1 packet = 100 notes
        increment(value, isNote, packets * 100)
    }

    fun clearDenomination(value: Int, isNote: Boolean) {
        setCount(value, isNote, 0)
    }

    fun clearAll() {
        val current = _uiState.value
        if (current.grandTotalAmount == 0L && current.totalPiecesCount == 0) return

        _uiState.update {
            it.copy(
                lastClearedNoteCounts = it.noteCounts,
                lastClearedCoinCounts = it.coinCounts,
                noteCounts = PakistaniCurrency.NOTES.associate { n -> n.value to 0 },
                coinCounts = PakistaniCurrency.COINS.associate { c -> c.value to 0 },
                showSnackbarMessage = "Cash counter reset to zero"
            )
        }
    }

    fun undoClear() {
        val lastNotes = _uiState.value.lastClearedNoteCounts
        val lastCoins = _uiState.value.lastClearedCoinCounts
        if (lastNotes != null || lastCoins != null) {
            _uiState.update {
                it.copy(
                    noteCounts = lastNotes ?: it.noteCounts,
                    coinCounts = lastCoins ?: it.coinCounts,
                    lastClearedNoteCounts = null,
                    lastClearedCoinCounts = null,
                    showSnackbarMessage = "Restored previous counts"
                )
            }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(showSnackbarMessage = null) }
    }

    fun toggleCoins(show: Boolean? = null) {
        _uiState.update { it.copy(showCoins = show ?: !it.showCoins) }
    }

    fun toggleFormat(useSouthAsian: Boolean? = null) {
        _uiState.update { it.copy(useSouthAsianCommas = useSouthAsian ?: !it.useSouthAsianCommas) }
    }

    fun setTargetAmount(amount: Long?) {
        _uiState.update { it.copy(targetAmount = amount, isTargetDialogOpen = false) }
    }

    fun openTargetDialog(open: Boolean) {
        _uiState.update { it.copy(isTargetDialogOpen = open) }
    }

    fun openSaveDialog(open: Boolean) {
        _uiState.update { it.copy(isSaveDialogOpen = open) }
    }

    fun openPacketMultiplier(value: Int?) {
        _uiState.update {
            it.copy(
                isPacketMultiplierOpen = value != null,
                selectedDenominationForPacket = value
            )
        }
    }

    fun setTab(tab: CashAppTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun saveSlip(title: String, remarks: String) {
        val current = _uiState.value
        val stateJson = JSONObject()
        val notesJson = JSONObject()
        current.noteCounts.forEach { (k, v) -> notesJson.put(k.toString(), v) }
        val coinsJson = JSONObject()
        current.coinCounts.forEach { (k, v) -> coinsJson.put(k.toString(), v) }

        stateJson.put("notes", notesJson)
        stateJson.put("coins", coinsJson)

        val slip = CashSlipEntity(
            title = if (title.isBlank()) "Cash Count ${CurrencyUtils.formatDate(System.currentTimeMillis())}" else title.trim(),
            timestamp = System.currentTimeMillis(),
            totalAmount = current.grandTotalAmount,
            totalNotesCount = current.totalNotesCount,
            totalCoinsCount = current.totalCoinsCount,
            targetAmount = current.targetAmount,
            countsMapJson = stateJson.toString(),
            remarks = remarks.trim()
        )

        viewModelScope.launch {
            repository.insertSlip(slip)
            _uiState.update {
                it.copy(
                    isSaveDialogOpen = false,
                    showSnackbarMessage = "Cash slip saved to history"
                )
            }
        }
    }

    fun loadSlip(slip: CashSlipEntity) {
        try {
            val root = JSONObject(slip.countsMapJson)
            val notesObj = root.optJSONObject("notes")
            val coinsObj = root.optJSONObject("coins")

            val newNotes = PakistaniCurrency.NOTES.associate { note ->
                val count = notesObj?.optInt(note.value.toString(), 0) ?: 0
                note.value to count
            }

            val newCoins = PakistaniCurrency.COINS.associate { coin ->
                val count = coinsObj?.optInt(coin.value.toString(), 0) ?: 0
                coin.value to count
            }

            val hasCoins = newCoins.values.any { it > 0 }

            _uiState.update {
                it.copy(
                    noteCounts = newNotes,
                    coinCounts = newCoins,
                    showCoins = if (hasCoins) true else it.showCoins,
                    targetAmount = slip.targetAmount,
                    activeTab = CashAppTab.CALCULATOR,
                    showSnackbarMessage = "Loaded slip: ${slip.title}"
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(showSnackbarMessage = "Error loading slip data") }
        }
    }

    fun deleteSlip(slip: CashSlipEntity) {
        viewModelScope.launch {
            repository.deleteSlip(slip)
            _uiState.update { it.copy(showSnackbarMessage = "Slip deleted") }
        }
    }

    fun generateShareSlipText(): String {
        val current = _uiState.value
        val sb = StringBuilder()
        sb.append("🇵🇰 PAKISTAN CASH DENOMINATION SLIP\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📅 Date: ${CurrencyUtils.formatDate(System.currentTimeMillis())}\n\n")

        sb.append("💵 NOTES BREAKDOWN:\n")
        PakistaniCurrency.NOTES.forEach { note ->
            val count = current.noteCounts[note.value] ?: 0
            if (count > 0) {
                val subtotal = note.value.toLong() * count
                sb.append(String.format("• %-8s × %-4d = %s\n", "Rs ${note.value}", count, CurrencyUtils.formatPkr(subtotal, current.useSouthAsianCommas)))
            }
        }

        if (current.showCoins && current.totalCoinsCount > 0) {
            sb.append("\n🪙 COINS BREAKDOWN:\n")
            PakistaniCurrency.COINS.forEach { coin ->
                val count = current.coinCounts[coin.value] ?: 0
                if (count > 0) {
                    val subtotal = coin.value.toLong() * count
                    sb.append(String.format("• %-8s × %-4d = %s\n", "Rs ${coin.value}", count, CurrencyUtils.formatPkr(subtotal, current.useSouthAsianCommas)))
                }
            }
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🔢 Total Notes: ${current.totalNotesCount} pcs\n")
        if (current.showCoins && current.totalCoinsCount > 0) {
            sb.append("🪙 Total Coins: ${current.totalCoinsCount} pcs\n")
            sb.append("📦 Total Pieces: ${current.totalPiecesCount} pcs\n")
        }
        sb.append("💰 GRAND TOTAL: ${CurrencyUtils.formatPkr(current.grandTotalAmount, current.useSouthAsianCommas)}\n\n")
        sb.append("📝 IN WORDS:\n")
        sb.append("${current.englishInWords}\n")
        sb.append("${current.urduInWords}\n")

        current.targetAmount?.let { target ->
            val diff = current.grandTotalAmount - target
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            sb.append("🎯 Target / Expected: ${CurrencyUtils.formatPkr(target, current.useSouthAsianCommas)}\n")
            if (diff == 0L) {
                sb.append("✅ Status: EXACT BALANCED\n")
            } else if (diff > 0) {
                sb.append("🟢 Surplus (Over): +${CurrencyUtils.formatPkr(diff, current.useSouthAsianCommas)}\n")
            } else {
                sb.append("🔴 Shortage (Less): -${CurrencyUtils.formatPkr(-diff, current.useSouthAsianCommas)}\n")
            }
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("Generated via PKR Cash Counter")
        return sb.toString()
    }
}
