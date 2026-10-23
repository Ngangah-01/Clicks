package com.clicks.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.ui.theme.ClicksGreen
import com.clicks.app.ui.theme.ClicksMint
import com.clicks.app.ui.theme.ClicksText

@Composable
fun QuantityPill(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    canDecrease: Boolean = true,
    canIncrease: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(ClicksMint)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PillButton(Icons.Filled.Remove, "Decrease quantity", canDecrease, onDecrease)
        Text(
            text = quantity.toString(),
            modifier = Modifier.widthIn(min = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ClicksText
        )
        PillButton(Icons.Filled.Add, "Increase quantity", canIncrease, onIncrease)
    }
}

@Composable
private fun PillButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(36.dp)
            .background(
                if (enabled) ClicksGreen else ClicksGreen.copy(alpha = 0.3f),
                CircleShape
            )
    ) {
        Icon(icon, contentDescription = description, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}