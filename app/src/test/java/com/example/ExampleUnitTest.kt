package com.example

import com.example.model.PakistaniCurrency
import com.example.util.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCurrencyFormatting() {
        assertEquals("₨ 5,000", CurrencyUtils.formatPkr(5000, true))
        assertEquals("₨ 1,25,000", CurrencyUtils.formatPkr(125000, true))
        assertEquals("₨ 1,00,00,000", CurrencyUtils.formatPkr(10000000, true))
    }

    @Test
    fun testEnglishInWords() {
        assertEquals("Five Thousand Rupees Only", CurrencyUtils.numberToPakistaniWords(5000))
        assertEquals("One Lakh Twenty Five Thousand Rupees Only", CurrencyUtils.numberToPakistaniWords(125000))
        assertEquals("One Crore Rupees Only", CurrencyUtils.numberToPakistaniWords(10000000))
    }

    @Test
    fun testPakistaniDenominationsCount() {
        assertEquals(8, PakistaniCurrency.NOTES.size)
        assertEquals(5000, PakistaniCurrency.NOTES[0].value)
        assertEquals(10, PakistaniCurrency.NOTES.last().value)
        assertEquals(4, PakistaniCurrency.COINS.size)
    }
}
