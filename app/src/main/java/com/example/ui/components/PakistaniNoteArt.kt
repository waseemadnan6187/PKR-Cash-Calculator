package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Denomination
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500

/**
 * Authentic visual representation of official Pakistani Banknotes.
 * Displays the authentic original banknote image with fallback to styled vector badge.
 */
@Composable
fun PakistaniNoteThumbnail(
    denomination: Denomination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = denomination.containerColor,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        modifier = modifier
            .width(62.dp)
            .height(38.dp)
            .border(
                width = 1.dp,
                color = denomination.themeColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp)
            )
            .testTag("note_image_${denomination.value}")
    ) {
        if (denomination.imageRes != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = denomination.imageRes),
                    contentDescription = "${denomination.label} Note",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle tint overlay to guarantee contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.35f)
                                )
                            )
                        )
                )

                // Value overlay and zoom indicator
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 3.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = "₨${denomination.value}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom Note",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                denomination.containerColor,
                                denomination.themeColor.copy(alpha = 0.15f),
                                denomination.containerColor
                            )
                        )
                    )
                    .padding(2.dp)
            ) {
                // Security thread line on left-center
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 13.dp)
                        .width(1.5.dp)
                        .fillMaxHeight()
                        .background(denomination.themeColor.copy(alpha = 0.6f))
                )

                // Banknote Content Layout
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Quaid silhouette emblem / State Bank watermark
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(denomination.themeColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "★",
                            fontSize = 8.sp,
                            color = denomination.themeColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Center: Value numeral and Urdu emblem
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${denomination.value}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = denomination.themeColor,
                            lineHeight = 11.sp
                        )
                        Text(
                            text = "PKR",
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Bold,
                            color = denomination.themeColor.copy(alpha = 0.8f),
                            lineHeight = 6.sp
                        )
                    }

                    // Right side: Mini zoom/preview indicator
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "View Note Details",
                        tint = denomination.themeColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Detailed Banknote Card representation for preview and inspection dialogs
 */
@Composable
fun PakistaniBanknoteDetailedCard(
    denomination: Denomination,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = denomination.containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = denomination.themeColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            denomination.containerColor,
                            denomination.themeColor.copy(alpha = 0.08f),
                            denomination.containerColor
                        )
                    )
                )
                .padding(14.dp)
        ) {
            // Top Banknote Header: State Bank of Pakistan in Urdu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "بینک دولت پاکستان",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = denomination.themeColor
                    )
                    Text(
                        text = "STATE BANK OF PAKISTAN",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = denomination.themeColor.copy(alpha = 0.85f)
                    )
                }

                // Top right official crest badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = denomination.themeColor,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = "₨ ${denomination.value}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Banknote Image Showcase
            if (denomination.imageRes != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .border(
                            1.dp,
                            denomination.themeColor.copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = denomination.imageRes),
                        contentDescription = "Original ${denomination.label} Note",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Note Details & Landmark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = denomination.themeColor.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Portrait / Watermark Box
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(denomination.themeColor.copy(alpha = 0.12f))
                        .border(1.dp, denomination.themeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = "Quaid Portrait Watermark",
                            tint = denomination.themeColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "قائداعظم",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = denomination.themeColor
                        )
                    }
                }

                // Note Details & Landmark
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = denomination.titleUrdu,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = denomination.themeColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = denomination.themeColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = denomination.subtitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "حامل ہذا کو مطالبہ پر ادا کریگا • حکومت پاکستان",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Security Features Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Security Features",
                        tint = denomination.themeColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Security Thread & UV Watermark",
                        fontSize = 10.sp,
                        color = denomination.themeColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "PKR • قانونی کرنسی",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
