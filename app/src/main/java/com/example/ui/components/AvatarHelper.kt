package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AvatarHelper {
    
    val avatarColors = listOf(
        Color(0xFFFF5722), // Vibrant Orange
        Color(0xFF00BCD4), // Cool Cyan
        Color(0xFFFFEB3B), // Sunny Yellow
        Color(0xFFE91E63), // Pink Electric
        Color(0xFF4CAF50), // Refreshing Green
        Color(0xFF9C27B0), // Regal Purple
        Color(0xFF3F51B5)  // Indigo
    )

    @Composable
    fun AvatarView(
        avatarIndex: Int, 
        modifier: Modifier = Modifier, 
        size: Dp = 48.dp, 
        label: String = "U"
    ) {
        val safeIndex = avatarIndex.coerceIn(0, avatarColors.lastIndex)
        val bgColor = avatarColors[safeIndex]
        val icon = when (safeIndex) {
            0 -> Icons.Filled.Person
            1 -> Icons.Filled.Face
            2 -> Icons.Filled.Shield
            3 -> Icons.Filled.Star
            4 -> Icons.Filled.RssFeed
            5 -> Icons.Filled.Fingerprint
            else -> Icons.Filled.Chat
        }
        
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Avatar $avatarIndex",
                tint = if (safeIndex == 2) Color.Black else Color.White,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}
