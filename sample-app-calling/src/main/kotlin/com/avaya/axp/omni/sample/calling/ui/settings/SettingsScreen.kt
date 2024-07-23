package com.avaya.axp.omni.sample.calling.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avaya.axp.omni.sample.calling.R
import com.avaya.axp.omni.sample.calling.ui.settings.SettingsUiState.Loading
import com.avaya.axp.omni.sample.calling.ui.settings.SettingsUiState.Success
import com.avaya.axp.omni.sample.calling.ui.theme.SampleTheme

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel<SettingsViewModel>(),
    onNavUp: () -> Unit
) {
    val settingsUiState by settingsViewModel.settingsUiState.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        SettingsTopAppBar(
            topAppBarText = stringResource(R.string.settings),
            onNavUp = onNavUp
        )
    }) { padding ->
        SettingsContent(
            padding = padding,
            settingsUiState = settingsUiState,
            onSave = { tokenServerUrl, userId, userName, verifiedCustomer ->
                settingsViewModel.saveSettings(
                    tokenServerUrl = tokenServerUrl,
                    userId = userId,
                    userName = userName,
                    verified = verifiedCustomer
                )
            }
        )
    }
}

@Composable
private fun SettingsContent(
    padding: PaddingValues,
    settingsUiState: SettingsUiState,
    onSave: (
        tokenServerUrl: String,
        userId: String,
        userName: String,
        verifiedCustomer: Boolean
    ) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        when (settingsUiState) {
            Loading -> {
                Text(
                    text = stringResource(id = R.string.loading),
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }

            is Success -> {
                val settingsData = settingsUiState.settingsData

                val serverUrl = remember { mutableStateOf(settingsData.tokenServerUrl) }
                val userId = remember { mutableStateOf(settingsData.userId) }
                val userName = remember { mutableStateOf(settingsData.userName) }
                val verifiedState = remember { mutableStateOf(settingsData.verifiedCustomer) }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    value = serverUrl.value,
                    label = { Text(stringResource(R.string.server_url)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    onValueChange = { serverUrl.value = it }
                )

                Spacer(modifier = Modifier.padding(8.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    value = userId.value,
                    label = { Text(stringResource(R.string.user_id)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = { userId.value = it }
                )
                Spacer(modifier = Modifier.padding(4.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    value = userName.value,
                    label = { Text(stringResource(R.string.user_name)) },
                    onValueChange = { userName.value = it }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.verified_customer))
                    Switch(
                        checked = verifiedState.value,
                        onCheckedChange = { verifiedState.value = it }
                    )
                }

                Spacer(modifier = Modifier.padding(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        onSave(serverUrl.value, userId.value, userName.value, verifiedState.value)
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    topAppBarText: String,
    onNavUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = topAppBarText
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SampleTheme {
        SettingsContent(
            padding = PaddingValues(16.dp),
            settingsUiState = Success(
                SettingsData(
                    tokenServerUrl = "https://token-server.example.com",
                    userId = "caller-321@example.com",
                    userName = "Caller 321",
                    verifiedCustomer = true
                )
            )
        ) { _, _, _, _ -> }
    }
}
