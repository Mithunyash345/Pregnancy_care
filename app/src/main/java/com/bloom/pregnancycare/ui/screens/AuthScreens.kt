package com.bloom.pregnancycare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.bloom.pregnancycare.ui.components.PillButton
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    var email by remember { mutableStateOf("elena.rostova@gmail.com") }
    var password by remember { mutableStateOf("password123") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(HomeGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Text(
                text = "Sign in to track your pregnancy journey",
                fontSize = 14.sp,
                color = SoftGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot Password?",
                    color = DarkPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.navigateTo("forgot_password") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PillButton(
                text = "Sign In",
                onClick = {
                    viewModel.loginUser(email, password) { _, _ ->
                        viewModel.navigateTo("otp_verification")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", fontSize = 13.sp, color = SoftGray)
                Text(
                    text = "Register",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPink,
                    modifier = Modifier.clickable { viewModel.navigateTo("register") }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: MainViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Mother") } // Mother, Companion, Doctor

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(HomeGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Text(
                text = "Join Bloom Pregnancy Care",
                fontSize = 14.sp,
                color = SoftGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Role Selector Tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Mother", "Companion", "Doctor").forEach { role ->
                    val isSelected = selectedRole == role
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SoftPink else Color.Transparent)
                            .clickable { selectedRole = role }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = role,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF2C2C2C) else SoftGray
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PillButton(
                text = "Register",
                onClick = {
                    viewModel.registerUser(name, email, password, selectedRole) { _, _ ->
                        viewModel.switchRole(selectedRole.lowercase())
                        viewModel.navigateTo("otp_verification")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", fontSize = 13.sp, color = SoftGray)
                Text(
                    text = "Sign In",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPink,
                    modifier = Modifier.clickable { viewModel.navigateTo("login") }
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(viewModel: MainViewModel) {
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(HomeGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reset Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Text(
                text = "Enter your email to receive recovery instructions",
                fontSize = 14.sp,
                color = SoftGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PillButton(
                text = "Send Instructions",
                onClick = { 
                    // Simulate email trigger and go back to login
                    viewModel.navigateTo("login")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Back to Login",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPink,
                modifier = Modifier.clickable { viewModel.navigateTo("login") }
            )
        }
    }
}

@Composable
fun OtpVerificationScreen(viewModel: MainViewModel) {
    var otpCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(HomeGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Verify Code",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Text(
                text = "A 6-digit verification code was sent to your email",
                fontSize = 14.sp,
                color = SoftGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = otpCode,
                onValueChange = { 
                    if (it.length <= 6) {
                        otpCode = it
                        errorMessage = null
                    }
                },
                label = { Text("Verification OTP Code") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            PillButton(
                text = "Verify & Access Dashboard",
                onClick = { 
                    viewModel.verifyOtp(otpCode) { success, msg ->
                        if (success) {
                            viewModel.switchRole(viewModel.currentRole.value)
                        } else {
                            errorMessage = msg
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = otpCode.length >= 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Resend Code",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPink,
                modifier = Modifier.clickable { 
                    // Simulate resending SMS/Email
                }
            )
        }
    }
}
