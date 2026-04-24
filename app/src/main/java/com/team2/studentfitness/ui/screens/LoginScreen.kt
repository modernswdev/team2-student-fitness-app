package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.studentfitness.ui.theme.LoginInputBg
import com.team2.studentfitness.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }
    val isPinSet = viewModel.isPinSet()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val isDark = isSystemInDarkTheme()
    val bgColor = MaterialTheme.colorScheme.background
    val accentColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // HERO SECTION
            Text(
                text = "SCHOLAR STRONG",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = accentColor,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Push both your physical and mental limits.",
                color = if (isDark) Color.White else Color.White, // Keep white in light mode for contrast on teal
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // FORM SECTION
            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                Text(
                    text = if (isPinSet) "Enter Your PIN" else "Create a Security PIN",
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = pin,
                    onValueChange = { 
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            pin = it
                            errorMessage = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color.DarkGray else LoginInputBg,
                        unfocusedContainerColor = if (isDark) Color.DarkGray else LoginInputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button
                Button(
                    onClick = {
                        val pinInt = pin.toIntOrNull()
                        if (pinInt != null) {
                            if (isPinSet) {
                                if (viewModel.pinUnlock(pinInt)) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Incorrect PIN. Please try again."
                                }
                            } else {
                                if (pin.length >= 4) {
                                    viewModel.setPin(pinInt)
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "PIN must be at least 4 digits."
                                }
                            }
                        } else {
                            errorMessage = "Please enter a valid numeric PIN."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(8.dp),
                    enabled = pin.isNotEmpty()
                ) {
                    Text(
                        text = if (isPinSet) "Sign In" else "Set PIN & Continue",
                        color = if (isDark) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // DIVIDER
            Row(
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = if (isDark) Color.Gray else Color.DarkGray.copy(alpha = 0.5f))
                Text(" OR ", modifier = Modifier.padding(horizontal = 10.dp), color = if (isDark) Color.White else Color.Black, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = if (isDark) Color.Gray else Color.DarkGray.copy(alpha = 0.5f))
            }

            // GOOGLE BUTTON
            Button(
                onClick = { /* Google Login */ },
                modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color.DarkGray else Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("Continue with Google", color = if (isDark) Color.White else Color(0xFF333333))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // FOOTER
            if (isPinSet) {
                TextButton(onClick = { /* Handle Forgot PIN */ }) {
                    Text(
                        text = "Forgot PIN?",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = buildAnnotatedString {
                        append("Secure your account with a ")
                        withStyle(style = SpanStyle(color = accentColor, fontWeight = FontWeight.Bold)) {
                            append("Personal PIN")
                        }
                    },
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
