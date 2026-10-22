package com.clicks.app.ui.screens.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocalAtm
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.clicks.app.ui.screens.location.DeliveryLocationViewModel
import com.clicks.app.ui.theme.BorderColor
import com.clicks.app.ui.theme.ClicksGreen
import com.clicks.app.ui.theme.ClicksMuted
import com.clicks.app.ui.theme.ClicksText
import com.clicks.app.ui.theme.SurfaceLight
import com.clicks.app.ui.theme.SurfaceLightAlt
import com.clicks.app.ui.viewmodel.StoreViewModel

@Composable
fun CheckoutScreen(
    deliveryLocationViewModel: DeliveryLocationViewModel,
    onPickLocation: () -> Unit = {},
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onOrderPlaced: () -> Unit = {}
) {
    val subtotal = storeViewModel.cartSubtotal
    val shippingFee = if (subtotal > 0) 80.0 else 0.0
    val vat = 0.0
    val total = subtotal + shippingFee + vat

    var selectedPaymentMethod by remember { mutableStateOf("Card") }
    var promoCode by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { showSuccessDialog = true },
                        enabled = deliveryLocationViewModel.location != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClicksGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Place Order",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ClicksText
                    )
                }

                Text(
                    text = "Checkout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = ClicksText
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Delivery Address Section
            DeliveryLocationPreview(
                location = deliveryLocationViewModel.location,
                onClick = onPickLocation
            )

            Spacer(Modifier.height(24.dp))

            // Payment Method Section
            Text(
                text = "Payment Method",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Pair("Card", Icons.Outlined.CreditCard),
                    Pair("Cash", Icons.Outlined.LocalAtm),
                    Pair("Mpesa", Icons.Outlined.PhoneAndroid)
                ).forEach { (method, icon) ->
                    val selected = selectedPaymentMethod == method
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedPaymentMethod = method },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) ClicksGreen else SurfaceLightAlt,
                        border = if (selected) null else BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = method,
                                tint = if (selected) Color.White else ClicksText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = method,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.White else ClicksText
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Selected payment details card
            if (selectedPaymentMethod == "Card") {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VISA **** **** **** 2512",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ClicksText
                        )
                        Text(
                            text = "Edit",
                            fontSize = 13.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            } else if (selectedPaymentMethod == "Mpesa") {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "M-Pesa: +254 712 345 678",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ClicksText
                        )
                        Text(
                            text = "Edit",
                            fontSize = 13.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Order Summary Section
            Text(
                text = "Order Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceLight)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryRow("Sub-total", "$ " + "%.2f".format(subtotal))
                SummaryRow("VAT (%)", "$ " + "%.2f".format(vat))
                SummaryRow("Shipping fee", "$ " + "%.2f".format(shippingFee))
                HorizontalDivider(color = BorderColor, thickness = 1.dp)
                SummaryRow("Total", "$ " + "%.2f".format(total), isBold = true)
            }

            Spacer(Modifier.height(20.dp))

            // Promo Code Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promoCode,
                    onValueChange = { promoCode = it },
                    placeholder = { Text("Enter promo code", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Button(
                    onClick = { /* Apply promo */ },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ClicksGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Add", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // Success Notification Dialog
    if (showSuccessDialog) {
        Dialog(onDismissRequest = {}) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ClicksGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Success",
                            tint = ClicksGreen,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Congratulations!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Your order has been placed.",
                        fontSize = 15.sp,
                        color = ClicksMuted
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showSuccessDialog = false
                            storeViewModel.placeOrder()
                            onOrderPlaced()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClicksGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Track Your Order",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) ClicksText else ClicksMuted
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = ClicksText
        )
    }
}
