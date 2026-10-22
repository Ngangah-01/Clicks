package com.clicks.app.ui.screens.login

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.clicks.app.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val brandGreen = Color(0xFF0B6638)
    val textDark = Color(0xFF111111)
    val textMuted = Color(0xFF6B7280)
    val labelColor = Color(0xFF9CA3AF)
    val borderColor = Color(0xFFE5E7EB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Floating Toast Banner for Error
        uiState.errorMessage?.let { error ->
            ToastBanner(
                message = error,
                isError = true,
                onDismiss = { viewModel.clearMessages() }
            )
        }

        // Floating Toast Banner for Success
        uiState.successMessage?.let { success ->
            ToastBanner(
                message = success,
                isError = false,
                onDismiss = { viewModel.clearMessages() }
            )
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                               // Logo Section (Clicks Logo + Clicks + Accent bar)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image2),
                            contentDescription = "Clicks Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Clicks",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(3.dp)
                            .background(Color(0xFFF2C299), RoundedCornerShape(1.5.dp))
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Header Title & Subtitle
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Welcome back! Enter your details to access your account",
                        fontSize = 13.sp,
                        color = textMuted,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Username / Email Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        fontSize = 13.sp,
                        color = labelColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = {
                            Text(text = "johndoe@gmail.com",
                                color = Color(0xFF9CA3AF),
                                fontSize = 16.sp,
                                ) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = emailError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textDark
                        )
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = if (emailError != null) Color(0xFFEF4444) else borderColor
                    )
                    if (emailError != null) {
                        Text(
                            text = emailError ?: "",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password",
                        fontSize = 13.sp,
                        color = labelColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = {
                            Text(text = "••••••••",
                                color = Color(0xFF9CA3AF),
                                fontSize = 16.sp,
                            ) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        isError = passwordError != null,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = brandGreen
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textDark
                        )
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = if (passwordError != null) Color(0xFFEF4444) else borderColor
                    )
                    if (passwordError != null) {
                        Text(
                            text = passwordError ?: "",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // LOGIN Button
                Button(
                    onClick = {
                        var valid = true
                        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                            emailError = "Valid email is required"
                            valid = false
                        }
                        if (password.isBlank()) {
                            passwordError = "Password is required"
                            valid = false
                        }
                        if (valid) {
                            viewModel.login(
                                email = email.trim(),
                                password = password,
                                onSuccess = onLoginSuccess
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandGreen,
                        contentColor = Color.White,
                        disabledContainerColor = brandGreen.copy(alpha = 0.6f),
                        disabledContentColor = Color.White
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "LOGIN",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Forgot Password Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Forgot Password? ",
                        color = textMuted,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Reset Password",
                        color = brandGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Don't have any account?
                Text(
                    text = "Don't have an account?",
                    color = textMuted,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Create an Account Button
                Button(
                    onClick = { onRegisterClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF7E2C6),
                        contentColor = textDark
                    )
                ) {
                    Text(
                        text = "CREATE AN ACCOUNT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = textDark
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ToastBanner(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        delay(4000L.milliseconds)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .zIndex(10f),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isError) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
            border = BorderStroke(1.dp, if (isError) Color(0xFFFCA5A5) else Color(0xFF86EFAC)),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isError) Icons.Outlined.ErrorOutline else Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = if (isError) Color(0xFFDC2626) else Color(0xFF16A34A),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isError) Color(0xFF991B1B) else Color(0xFF166534),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = if (isError) Color(0xFFDC2626) else Color(0xFF16A34A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
