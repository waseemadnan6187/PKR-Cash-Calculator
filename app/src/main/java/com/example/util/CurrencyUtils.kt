package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyUtils {

    /**
     * Formats amount in Pakistani Rupee standard representation.
     * e.g., 1250000 -> "₨ 1,250,000" or South Asian format "₨ 12,50,000"
     */
    fun formatPkr(amount: Long, useSouthAsianFormat: Boolean = true): String {
        return if (useSouthAsianFormat) {
            "₨ " + formatSouthAsianNumber(amount)
        } else {
            val formatter = DecimalFormat("#,###")
            "₨ " + formatter.format(amount)
        }
    }

    fun formatNumber(amount: Long, useSouthAsianFormat: Boolean = true): String {
        return if (useSouthAsianFormat) {
            formatSouthAsianNumber(amount)
        } else {
            val formatter = DecimalFormat("#,###")
            formatter.format(amount)
        }
    }

    private fun formatSouthAsianNumber(num: Long): String {
        if (num < 0) return "-" + formatSouthAsianNumber(-num)
        val s = num.toString()
        if (s.length <= 3) return s

        val lastThree = s.substring(s.length - 3)
        val rest = s.substring(0, s.length - 3)

        val result = StringBuilder()
        var count = 0
        for (i in rest.length - 1 downTo 0) {
            result.insert(0, rest[i])
            count++
            if (count % 2 == 0 && i > 0) {
                result.insert(0, ',')
            }
        }
        result.append(',').append(lastThree)
        return result.toString()
    }

    /**
     * Converts a number to English Words using Pakistani / South Asian numbering system
     * (Crore, Lakh, Thousand, Hundred, Rupees).
     */
    fun numberToPakistaniWords(number: Long): String {
        if (number == 0L) return "Zero Rupees Only"
        if (number < 0) return "Minus " + numberToPakistaniWords(-number)

        var n = number
        val sb = StringBuilder()

        val arab = n / 1000000000L
        if (arab > 0) {
            sb.append(convertThreeDigits(arab.toInt())).append(" Arab ")
            n %= 1000000000L
        }

        val crore = n / 10000000L
        if (crore > 0) {
            sb.append(convertThreeDigits(crore.toInt())).append(" Crore ")
            n %= 10000000L
        }

        val lakh = n / 100000L
        if (lakh > 0) {
            sb.append(convertThreeDigits(lakh.toInt())).append(" Lakh ")
            n %= 100000L
        }

        val thousand = n / 1000L
        if (thousand > 0) {
            sb.append(convertThreeDigits(thousand.toInt())).append(" Thousand ")
            n %= 1000L
        }

        if (n > 0) {
            sb.append(convertThreeDigits(n.toInt())).append(" ")
        }

        val text = sb.toString().trim()
        return "$text Rupees Only"
    }

    private val ONES = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    )

    private val TENS = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    private fun convertThreeDigits(number: Int): String {
        var num = number
        val sb = StringBuilder()

        if (num >= 100) {
            sb.append(ONES[num / 100]).append(" Hundred ")
            num %= 100
        }

        if (num in 1..19) {
            sb.append(ONES[num])
        } else if (num >= 20) {
            sb.append(TENS[num / 10])
            val rem = num % 10
            if (rem > 0) {
                sb.append(" ").append(ONES[rem])
            }
        }

        return sb.toString().trim()
    }

    /**
     * Converts a number to Urdu words
     */
    fun numberToUrduWords(number: Long): String {
        if (number == 0L) return "صفر روپے"
        if (number < 0) return "منفی " + numberToUrduWords(-number)

        var n = number
        val parts = mutableListOf<String>()

        val arab = n / 1000000000L
        if (arab > 0) {
            parts.add(urduTwoDigits(arab.toInt()) + " ارب")
            n %= 1000000000L
        }

        val crore = n / 10000000L
        if (crore > 0) {
            parts.add(urduTwoDigits(crore.toInt()) + " کروڑ")
            n %= 10000000L
        }

        val lakh = n / 100000L
        if (lakh > 0) {
            parts.add(urduTwoDigits(lakh.toInt()) + " لاکھ")
            n %= 100000L
        }

        val thousand = n / 1000L
        if (thousand > 0) {
            parts.add(urduTwoDigits(thousand.toInt()) + " ہزار")
            n %= 1000L
        }

        val hundred = n / 100L
        if (hundred > 0) {
            parts.add(urduTwoDigits(hundred.toInt()) + " سو")
            n %= 100L
        }

        if (n > 0) {
            parts.add(urduTwoDigits(n.toInt()))
        }

        return parts.joinToString(" ") + " روپے فقط"
    }

    private val URDU_NUMS = mapOf(
        1 to "ایک", 2 to "دو", 3 to "تین", 4 to "چار", 5 to "پانچ",
        6 to "چھ", 7 to "سات", 8 to "آٹھ", 9 to "نو", 10 to "دس",
        11 to "گیارہ", 12 to "بارہ", 13 to "تیرہ", 14 to "چودہ", 15 to "پندرہ",
        16 to "سولہ", 17 to "سترہ", 18 to "اٹھارہ", 19 to "انیس", 20 to "بیس",
        21 to "اکیس", 22 to "بائیس", 23 to "تیئیس", 24 to "چوبیس", 25 to "پچیس",
        26 to "چھبیس", 27 to "ستائیس", 28 to "اٹھائیس", 29 to "انتیس", 30 to "تیس",
        31 to "اکتیس", 32 to "بتیس", 33 to "تینتیس", 34 to "چونتیس", 35 to "پینتیس",
        36 to "چھتیس", 37 to "سینتیس", 38 to "اڑتیس", 39 to "انتالیس", 40 to "چالیس",
        41 to "اکتالیس", 42 to "بیالیس", 43 to "تینتالیس", 44 to "چوالیس", 45 to "پینتالیس",
        46 to "چھیاسٹھ", 47 to "سینتالیس", 48 to "اڑتالیس", 49 to "انچاس", 50 to "پچاس",
        51 to "اکاون", 52 to "باون", 53 to "ترپن", 54 to "چون", 55 to "پچپن",
        56 to "چھپن", 57 to "ستاون", 58 to "اٹھاون", 59 to "انسٹھ", 60 to "ساٹھ",
        61 to "اکسٹھ", 62 to "باسٹھ", 63 to "تریسٹھ", 64 to "چونسٹھ", 65 to "پینسٹھ",
        66 to "چھیاسٹھ", 67 to "سڑسٹھ", 68 to "اڑسٹھ", 69 to "انہتر", 70 to "ستر",
        71 to "اکہتر", 72 to "بہتر", 73 to "تہتر", 74 to "چوہتر", 75 to "پچھتر",
        76 to "چھہتر", 77 to "ستتر", 78 to "اٹھتر", 79 to "اناسی", 80 to "اسی",
        81 to "اکیاسی", 82 to "بیاسی", 83 to "تراسی", 84 to "چوراسی", 85 to "پچاسی",
        86 to "چھیاسی", 87 to "ستاسی", 88 to "اٹھاسی", 89 to "نواسی", 90 to "نوے",
        91 to "اکانوے", 92 to "بانوے", 93 to "ترانوے", 94 to "چورانوے", 95 to "پچانوے",
        96 to "چھیانوے", 97 to "ستانوے", 98 to "اٹھانوے", 99 to "نناوے"
    )

    private fun urduTwoDigits(n: Int): String {
        return URDU_NUMS[n] ?: n.toString()
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
