package com.clicks.app.feature.auth.ui.recovery

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.components.ClicksTextField
import com.clicks.app.lib.components.ToastBanner
import com.clicks.app.lib.theme.BrandGreen
import com.clicks.app.lib.theme.TextDark
import com.clicks.app.lib.theme.TextMuted
import com.clicks.app.lib.validation.AuthValidators

@Composable
fun PasswordRecoveryScreen(
    viewModel: PasswordRecoveryViewModel,
    onBackClick: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var email by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // System back: step back inside the flow first, leave the screen otherwise.
    BackHandler {
        if (!viewModel.onBack()) onBackClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        uiState.errorMessage?.let {
            ToastBanner(message = it, isError = true, onDismiss = viewModel::clearMessages)
        }
        uiState.infoMessage?.let {
            ToastBanner(message = it, isError = false, onDismiss = viewModel::clearMessages)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp)
        ) {
            if (uiState.step != RecoveryStep.Done) {
                IconButton(
                    onClick = { if (!viewModel.onBack()) onBackClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (uiState.step) {
                RecoveryStep.Email -> {
                    Header(
                        title = "Forgot Password?",
                        subtitle = "Enter the email linked to your account and we'll send you a code to reset your password."
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ClicksTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel.onInputChanged()
                        },
                        label = "Email",
                        placeholder = "johndoe@gmail.com",
                        errorMessage = uiState.fieldError,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                    ClicksButton(
                        text = "SEND CODE",
                        isLoading = uiState.isLoading,
                        onClick = {
                            code = ""
                            viewModel.submitEmail(email)
                        }
                    )
                }

                RecoveryStep.Code -> {
                    Header(
                        title = "Enter Code",
                        subtitle = "We sent a ${AuthValidators.OTP_LENGTH}-digit code to ${uiState.email}."
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ClicksTextField(
                        value = code,
                        onValueChange = {
                            code = it.filter(Char::isDigit).take(AuthValidators.OTP_LENGTH)
                            viewModel.onInputChanged()
                        },
                        label = "Verification code",
                        placeholder = "123456",
                        errorMessage = uiState.fieldError,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                    ClicksButton(
                        text = "VERIFY",
                        isLoading = uiState.isLoading,
                        onClick = { viewModel.submitCode(code) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ResendRow(
                        secondsLeft = uiState.resendSecondsLeft,
                        onResend = viewModel::resendCode
                    )
                }

                RecoveryStep.NewPassword -> {
                    Header(
                        title = "New Password",
                        subtitle = "Choose a new password of at least ${AuthValidators.MIN_PASSWORD_LENGTH} characters."
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    val visibility = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation()
                    ClicksTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            viewModel.onInputChanged()
                        },
                        label = "New password",
                        errorMessage = uiState.fieldError,
                        visualTransformation = visibility,
                        enabled = !uiState.isLoading,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.Visibility
                                    else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = BrandGreen
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ClicksTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            viewModel.onInputChanged()
                        },
                        label = "Confirm password",
                        errorMessage = uiState.confirmError,
                        visualTransformation = visibility,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                    ClicksButton(
                        text = "RESET PASSWORD",
                        isLoading = uiState.isLoading,
                        onClick = { viewModel.submitNewPassword(password, confirmPassword) }
                    )
                }

                RecoveryStep.Done -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = BrandGreen,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Password updated",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You can now sign in with your new password.",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(36.dp))
                        ClicksButton(text = "BACK TO LOGIN", onClick = onBackToLogin)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = TextMuted,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ResendRow(secondsLeft: Int, onResend: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (secondsLeft > 0) {
            Text(text = "Resend code in ${secondsLeft}s", color = TextMuted, fontSize = 14.sp)
        } else {
            Text(text = "Didn't get it? ", color = TextMuted, fontSize = 14.sp)
            Text(
                text = "Resend code",
                color = BrandGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onResend() }
            )
        }
    }
}