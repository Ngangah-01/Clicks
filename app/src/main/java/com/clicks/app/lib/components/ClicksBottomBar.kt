package com.clicks.app.lib.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksGreenInactive

@Composable
fun ClicksBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
        Triple("Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        Triple("Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        Triple("Profile", Icons.Outlined.Person, Icons.Outlined.PersonOutline)
    )

    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (label, activeIcon, inactiveIcon) ->
                val selected = selectedTab == label

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onTabSelected(label) }
                        .padding(horizontal = 13.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) ClicksGreen else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) activeIcon else inactiveIcon,
                            contentDescription = label,
                            tint = if (selected) Color.White else ClicksGreenInactive,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = if (selected) ClicksGreen else ClicksGreenInactive,
                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )
                }
            }
        }
    }
}
