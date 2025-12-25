package com.diachuk.modernarchitecture.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.diachuk.modernarchitecture.features.auth.api.RegisterDestination
import com.diachuk.modernarchitecture.features.auth.logic.login.LoginEvent
import com.diachuk.modernarchitecture.features.auth.logic.login.LoginState
import com.diachuk.modernarchitecture.features.auth.logic.login.LoginViewModel
import com.diachuk.modernarchitecture.navigaion.LocalNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val state by viewModel.state.collectAsState()

    LoginScreenUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LoginScreenUi(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.error != null) {
            Text(text = state.error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { onEvent(LoginEvent.Login(email, password)) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isLoading) "Logging in..." else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navigator.navigate(RegisterDestination) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? Register")
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenUi(
            state = LoginState(),
            onEvent = {}
        )
    }
}