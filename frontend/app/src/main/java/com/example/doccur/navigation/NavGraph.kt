package com.example.doccur.navigation

import androidx.compose.ui.graphics.vector.ImageVector

interface ScreenItem {
    val route: String
    val title: String
    val icon: ImageVector
}
