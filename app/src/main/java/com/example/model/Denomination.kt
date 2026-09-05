package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.*

data class Denomination(
    val value: Int,
    val isNote: Boolean,
    val label: String,
    val titleUrdu: String,
    val subtitle: String,
    val themeColor: Color,
    val containerColor: Color,
    @DrawableRes val imageRes: Int? = null
)

object PakistaniCurrency {
    val NOTES = listOf(
        Denomination(
            value = 5000,
            isNote = true,
            label = "Rs. 5,000",
            titleUrdu = "پانچ ہزار روپے",
            subtitle = "Faisal Mosque, Islamabad",
            themeColor = Note5000Color,
            containerColor = Note5000Light,
            imageRes = R.drawable.note_5000_pkr_1787994423035
        ),
        Denomination(
            value = 1000,
            isNote = true,
            label = "Rs. 1,000",
            titleUrdu = "ایک ہزار روپے",
            subtitle = "Islamia College, Peshawar",
            themeColor = Note1000Color,
            containerColor = Note1000Light,
            imageRes = R.drawable.note_1000_pkr_1787994439435
        ),
        Denomination(
            value = 500,
            isNote = true,
            label = "Rs. 500",
            titleUrdu = "پانچ سو روپے",
            subtitle = "Badshahi Mosque, Lahore",
            themeColor = Note500Color,
            containerColor = Note500Light,
            imageRes = R.drawable.note_500_pkr_1787994454482
        ),
        Denomination(
            value = 100,
            isNote = true,
            label = "Rs. 100",
            titleUrdu = "ایک سو روپے",
            subtitle = "Quaid Residency, Ziarat",
            themeColor = Note100Color,
            containerColor = Note100Light,
            imageRes = R.drawable.note_100_pkr_1787994469956
        ),
        Denomination(
            value = 75,
            isNote = true,
            label = "Rs. 75",
            titleUrdu = "پچھتر روپے",
            subtitle = "75th Independence Note",
            themeColor = Note75Color,
            containerColor = Note75Light,
            imageRes = R.drawable.note_75_pkr_1787994491664
        ),
        Denomination(
            value = 50,
            isNote = true,
            label = "Rs. 50",
            titleUrdu = "پچاس روپے",
            subtitle = "K2 Peak, Karakoram",
            themeColor = Note50Color,
            containerColor = Note50Light,
            imageRes = R.drawable.note_50_pkr_1787994506566
        ),
        Denomination(
            value = 20,
            isNote = true,
            label = "Rs. 20",
            titleUrdu = "بیس روپے",
            subtitle = "Mohenjo-daro, Sindh",
            themeColor = Note20Color,
            containerColor = Note20Light,
            imageRes = R.drawable.note_20_pkr_1787994522744
        ),
        Denomination(
            value = 10,
            isNote = true,
            label = "Rs. 10",
            titleUrdu = "دس روپے",
            subtitle = "Bab-e-Khyber, Peshawar",
            themeColor = Note10Color,
            containerColor = Note10Light,
            imageRes = R.drawable.note_10_pkr_1787994536813
        )
    )

    val COINS = listOf(
        Denomination(
            value = 10,
            isNote = false,
            label = "Rs. 10 Coin",
            titleUrdu = "دس روپے کا سکہ",
            subtitle = "Bi-Metallic Coin",
            themeColor = CoinGoldColor,
            containerColor = Gold100
        ),
        Denomination(
            value = 5,
            isNote = false,
            label = "Rs. 5 Coin",
            titleUrdu = "پانچ روپے کا سکہ",
            subtitle = "Brass Golden Coin",
            themeColor = CoinGoldColor,
            containerColor = Gold100
        ),
        Denomination(
            value = 2,
            isNote = false,
            label = "Rs. 2 Coin",
            titleUrdu = "دو روپے کا سکہ",
            subtitle = "Badshahi Mosque Coin",
            themeColor = CoinSilverColor,
            containerColor = Emerald50
        ),
        Denomination(
            value = 1,
            isNote = false,
            label = "Rs. 1 Coin",
            titleUrdu = "ایک روپے کا سکہ",
            subtitle = "Quaid-e-Azam Coin",
            themeColor = CoinSilverColor,
            containerColor = Emerald50
        )
    )

    val ALL_ITEMS = NOTES + COINS
}
