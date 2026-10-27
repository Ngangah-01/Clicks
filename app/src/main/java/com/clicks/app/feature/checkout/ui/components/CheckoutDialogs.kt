package com.clicks.app.feature.checkout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText

@Composable
fun EditPhoneDialog(
    initialPhone: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var tempPhone by remember { mutableStateOf(initialPhone) }
    AlertDialog(
        onDismissRequest = onDismiss,
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
                        onSave(tempPhone)
                    }
                    onDismiss()
                }
            ) {
                Text("Save", fontWeight = FontWeight.Bold, color = ClicksGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ClicksMuted)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun ProcessingPaymentDialog(
    mpesaPhone: String,
    total: Double
) {
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

@Composable
fun SuccessOrderDialog(
    transactionCode: String,
    onTrackOrder: () -> Unit
) {
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

                ClicksButton(
                    text = "Track Your Order",
                    onClick = onTrackOrder,
                    height = 50.dp,
                    shape = RoundedCornerShape(25.dp)
                )
            }
        }
    }
}
