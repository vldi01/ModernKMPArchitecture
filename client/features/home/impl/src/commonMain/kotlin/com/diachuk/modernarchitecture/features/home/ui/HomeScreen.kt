package com.diachuk.modernarchitecture.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.diachuk.architecture.network.api.user.User
import com.diachuk.modernarchitecture.features.home.logic.HomeEvent
import com.diachuk.modernarchitecture.features.home.logic.HomeState
import com.diachuk.modernarchitecture.features.home.logic.HomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsState()
    HomeScreenUi(state = state, onEvent = viewModel::onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUi(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { onEvent(HomeEvent.OnLogoutClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading users",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    if (state.users.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No users found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.users) { user ->
                                UserCard(user = user)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreenUi(
            state = HomeState(
                users = listOf(
                    User(1, "John Doe", "john@example.com"),
                    User(2, "Jane Smith", "jane@example.com"),
                    User(3, "Alice Johnson", "alice@example.com")
                ),
                isLoading = false,
                error = null
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
fun HomeScreenLoadingPreview() {
    MaterialTheme {
        HomeScreenUi(
            state = HomeState(isLoading = true),
            onEvent = {}
        )
    }
}

@Preview
@Composable
fun HomeScreenErrorPreview() {
    MaterialTheme {
        HomeScreenUi(
            state = HomeState(error = "Network connection failed"),
            onEvent = {}
        )
    }
}
