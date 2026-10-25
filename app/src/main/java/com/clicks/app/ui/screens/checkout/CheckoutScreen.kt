package com.clicks.app.ui.screens.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.clicks.app.ui.screens.location.DeliveryLocationViewModel
import com.clicks.app.ui.theme.BorderColor
import com.clicks.app.ui.theme.ClicksGreen
import com.clicks.app.ui.theme.ClicksMuted
import com.clicks.app.ui.theme.ClicksText
import com.clicks.app.ui.theme.SurfaceLight
import com.clicks.app.ui.viewmodel.StoreViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

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

    var mpesaPhone by remember { mutableStateOf("+254 712 345 678") }
    var showEditPhoneDialog by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var isPaymentConfirmed by remember { mutableStateOf(false) }
    var transactionCode by remember { mutableStateOf("") }

    var promoCode by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isProcessingPayment) {
        if (isProcessingPayment) {
            delay(2500.milliseconds)
            transactionCode = "MP${(10000000..99999999).random()}"
            isPaymentConfirmed = true
            isProcessingPayment = false
        }
    }

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
                    val isLocationSet = deliveryLocationViewModel.location != null
                    val canPlaceOrder = isLocationSet && isPaymentConfirmed

                    Button(
                        onClick = { showSuccessDialog = true },
                        enabled = canPlaceOrder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClicksGreen,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE5E7EB),
                            disabledContentColor = ClicksMuted
                        )
                    ) {
                        Text(
                            text = if (!isLocationSet) "Select Delivery Location" else "Place Order",
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

            // Payment Method Section (M-Pesa Only)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )

                if (isPaymentConfirmed) {
                    Surface(
                        shape = CircleShape,
                        color = ClicksGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "✓ Paid",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // M-Pesa Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPaymentConfirmed) ClicksGreen.copy(alpha = 0.08f) else SurfaceLight
                ),
                border = if (isPaymentConfirmed) BorderStroke(1.5.dp, ClicksGreen) else BorderStroke(1.dp, BorderColor),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(ClicksGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhoneAndroid,
                                    contentDescription = "M-Pesa",
                                    tint = ClicksGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "M-Pesa",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClicksText
                                )
                                Text(
                                    text = mpesaPhone,
                                    fontSize = 13.sp,
                                    color = ClicksMuted
                                )
                            }
                        }

                        TextButton(onClick = { showEditPhoneDialog = true }) {
                            Text(
                                text = "Edit",
                                fontSize = 13.sp,
                                color = ClicksGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isPaymentConfirmed) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = ClicksGreen.copy(alpha = 0.2f), thickness = 1.dp)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Transaction Ref: $transactionCode",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ClicksGreen
                            )
                            Text(
                                text = "Confirmed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClicksGreen
                            )
                        }
                    } else {
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { isProcessingPayment = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ClicksGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Pay $ " + "%.2f".format(total) + " via M-Pesa",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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

    // Edit M-Pesa Phone Number Dialog
    if (showEditPhoneDialog) {
        var tempPhone by remember { mutableStateOf(mpesaPhone) }
        AlertDialog(
            onDismissRequest = { showEditPhoneDialog = false },
            title = {
                Text(
                    text = "Edit M-Pesa Number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ClicksText
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your M-Pesa registered phone number:",
                        fontSize = 14.sp,
                        color = ClicksMuted
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { newValue -> tempPhone = newValue },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ClicksText,
                            unfocusedTextColor = ClicksText,
                            focusedBorderColor = ClicksGreen,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tempPhone.isNotBlank()) {
                            mpesaPhone = tempPhone
                        }
                        showEditPhoneDialog = false
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold, color = ClicksGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPhoneDialog = false }) {
                    Text("Cancel", color = ClicksMuted)
                }
            },
            containerColor = Color.White
        )
    }

    // Processing M-Pesa Payment STK Push Dialog
    if (isProcessingPayment) {
        Dialog(onDismissRequest = {}) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = ClicksGreen, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "M-Pesa STK Push Sent",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Please enter your M-Pesa PIN on your phone ($mpesaPhone) to complete payment of $ " + "%.2f".format(total),
                        fontSize = 14.sp,
                        color = ClicksMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
                    modifier = Modifier.padding(24.dp),
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
                        text = "Your order has been placed successfully.",
                        fontSize = 15.sp,
                        color = ClicksMuted,
                        textAlign = TextAlign.Center
                    )

                    if (transactionCode.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "M-Pesa Ref: $transactionCode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClicksGreen
                        )
                    }

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
