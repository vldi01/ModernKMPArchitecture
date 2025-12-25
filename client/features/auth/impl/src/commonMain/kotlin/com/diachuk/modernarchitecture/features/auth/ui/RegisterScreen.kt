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
import com.diachuk.modernarchitecture.features.auth.logic.registration.RegisterEvent
import com.diachuk.modernarchitecture.features.auth.logic.registration.RegisterState
import com.diachuk.modernarchitecture.features.auth.logic.registration.RegisterViewModel
import com.diachuk.modernarchitecture.navigaion.LocalNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {
    val state by viewModel.state.collectAsState()

    RegisterScreenUi(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun RegisterScreenUi(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit
) {
    var name by remember { mutableStateOf("") }
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
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

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
            onClick = { onEvent(RegisterEvent.Register(name, email, password)) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isLoading) "Registering..." else "Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navigator.popBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account? Login")
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreenUi(
            state = RegisterState(),
            onEvent = {}
        )
    }
}