package com.clicks.app.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.ui.viewmodel.StoreViewModel

private val ClicksGreen = Color(0xFF07865B)
private val ClicksText = Color(0xFF163E32)
private val ClicksMint = Color(0xFFF0F7F3)
private val ClicksMuted = Color(0xFF789087)

@Composable
fun OrdersScreen(
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {}
) {
    val orders = storeViewModel.orders

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .background(Color.White)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ClicksText
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "My Orders (${orders.size})",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )
            }

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = "Empty",
                            tint = ClicksMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No orders placed yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Your completed orders will appear here",
                            fontSize = 14.sp,
                            color = ClicksMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(orders, key = { it.orderId }) { order ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.orderId,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClicksText
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = when (order.status) {
                                            "Delivered" -> ClicksMint
                                            "Processing" -> Color(0xFFFEF3C7)
                                            else -> Color(0xFFEFF6FF)
                                        }
                                    ) {
                                        Text(
                                            text = order.status,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (order.status) {
                                                "Delivered" -> ClicksGreen
                                                "Processing" -> Color(0xFFD97706)
                                                else -> Color(0xFF2563EB)
                                            },
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Date: ${order.date}",
                                    fontSize = 13.sp,
                                    color = ClicksMuted
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "Items: ${order.itemsSummary}",
                                    fontSize = 13.sp,
                                    color = ClicksText,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(Modifier.height(12.dp))

                                HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Amount",
                                        fontSize = 13.sp,
                                        color = ClicksMuted
                                    )
                                    Text(
                                        text = "$ " + "%.2f".format(order.total),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClicksGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
