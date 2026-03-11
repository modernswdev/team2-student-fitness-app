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

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onBypassLogin: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                // Email Input
                Text("Email Address", color = Color.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("name@example.com", color = Color.Gray) },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                Text("Password", color = Color.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                Button(
                    onClick = { /* Handle Login */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LoginOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sign In", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                // Bypass Button (Dev Only)
                TextButton(
                    onClick = onBypassLogin,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                ) {
                    Text("Bypass Login (Dev Mode)", color = LoginOrange.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }

            // DIVIDER
            Row(
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.DarkGray.copy(alpha = 0.5f))
                Text(" OR ", modifier = Modifier.padding(horizontal = 10.dp), color = Color.Black, fontSize = 12.sp)
                Divider(modifier = Modifier.weight(1f), color = Color.DarkGray.copy(alpha = 0.5f))
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
            Text(
                text = buildAnnotatedString {
                    append("New here? ")
                    withStyle(style = SpanStyle(color = LoginOrange, fontWeight = FontWeight.Bold)) {
                        append("Create an account")
                    }
                },
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
}
