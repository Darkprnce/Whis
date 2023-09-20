package com.whis.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.whis.R

// Set of Material typography styles to start with
val typography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_bold)),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        //letterSpacing = 1.25.sp
    )
)