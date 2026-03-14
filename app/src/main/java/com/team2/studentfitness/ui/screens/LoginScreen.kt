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
import com.team2.studentfitness.ui.theme.LoginBackground
import com.team2.studentfitness.ui.theme.LoginOrange
import com.team2.studentfitness.ui.theme.LoginInputBg
import com.team2.studentfitness.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onOpenDevMenu: () -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }
    val isPinSet = viewModel.isPinSet()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LoginBackground)
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
                color = LoginOrange,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Push both your physical and mental limits.",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // FORM SECTION
            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                Text(
                    text = if (isPinSet) "Enter Your PIN" else "Create a Security PIN",
                    color = Color.Black,
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
                        focusedContainerColor = LoginInputBg,
                        unfocusedContainerColor = LoginInputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
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
                    colors = ButtonDefaults.buttonColors(containerColor = LoginOrange),
                    shape = RoundedCornerShape(8.dp),
                    enabled = pin.isNotEmpty()
                ) {
                    Text(
                        text = if (isPinSet) "Sign In" else "Set PIN & Continue",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Bypass Button (Dev Only)
                TextButton(
                    onClick = onOpenDevMenu,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                ) {
                    Text("Open Dev Menu", color = LoginOrange.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }

            // DIVIDER
            Row(
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray.copy(alpha = 0.5f))
                Text(" OR ", modifier = Modifier.padding(horizontal = 10.dp), color = Color.Black, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray.copy(alpha = 0.5f))
            }

            // GOOGLE BUTTON
            Button(
                onClick = { /* Google Login */ },
                modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("Continue with Google", color = Color(0xFF333333))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // FOOTER
            if (isPinSet) {
                TextButton(onClick = { /* Handle Forgot PIN */ }) {
                    Text(
                        text = "Forgot PIN?",
                        color = LoginOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = buildAnnotatedString {
                        append("Secure your account with a ")
                        withStyle(style = SpanStyle(color = LoginOrange, fontWeight = FontWeight.Bold)) {
                            append("Personal PIN")
                        }
                    },
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
