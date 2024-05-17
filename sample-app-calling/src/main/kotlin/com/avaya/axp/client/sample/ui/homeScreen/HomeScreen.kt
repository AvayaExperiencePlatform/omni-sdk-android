package com.avaya.axp.client.sample.ui.homeScreen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avaya.axp.client.sample.R
import com.avaya.axp.client.sample.ui.call.CallActivity
import com.avaya.axp.client.sample.ui.theme.SampleTheme
import com.avaya.axp.client.sample.ui.utility.rememberMultiplePermissionsStateSafe
import com.avaya.axp.client.sdk.webrtc.TelecomCall
import com.avaya.axp.client.sdk.webrtc.TelecomCallRepository
import com.avaya.axp.client.sdk.webrtc.launchOutgoingCall
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

val callPermissions: List<String>
    get() {
        // To record the audio for the call
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)

        // We should be using make_own_call permissions but this requires
        // implementation of the telecom API to work correctly.
        // Please see telecom example for full implementation
        permissions.add(Manifest.permission.MANAGE_OWN_CALLS)

        // To show call notifications we need permissions since Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions.toList()
    }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel<HomeViewModel>(),
    onSettingsClicked: () -> Unit = {}
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val showLoadingDialog = remember { mutableStateOf(false) }
    val showRationaleDialog = remember { mutableStateOf(false) }

    val repository = remember {
        TelecomCallRepository.instance ?: TelecomCallRepository.create(context.applicationContext)
    }
    val call by repository.currentCallFlow.collectAsState()
    val hasOngoingCall = call is TelecomCall.Registered

    val displayName by homeViewModel.displayNameFlow.collectAsState("")
    val remoteAddress by homeViewModel.remoteAddressFlow.collectAsState("")
    val configError by homeViewModel.configErrorFlow.collectAsState()
    val configNeeded by homeViewModel.configurationNeededFlow.collectAsState(false)
    val sdkConfigured by homeViewModel.isSdkConfiguredFlow.collectAsState(false)

    if (hasOngoingCall) {
        LaunchedEffect(Unit) {
            context.startActivity(
                Intent(context, CallActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                },
            )
        }
        showLoadingDialog.value = false
    }

    LoadingDialog(showLoadingDialog.value)
    if (showRationaleDialog.value) {
        PermissionRationaleDialog(showRationaleDialog)
    }

    fun onLaunchCall() {
        showLoadingDialog.value = true
        coroutineScope.launch {
            val error = repository.failedCall.receive()
            Toast.makeText(
                context,
                context.getString(R.string.failed_to_start_call, error),
                Toast.LENGTH_LONG
            ).show()
            showLoadingDialog.value = false
        }

        context.launchOutgoingCall(
            remoteUri = Uri.parse(remoteAddress),
            localDisplayName = displayName
        )
    }

    val callPermissionsState =
        rememberMultiplePermissionsStateSafe(permissions = callPermissions) { grants ->
            if (grants.values.all { it }) {
                onLaunchCall()
            }
        }

    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopAppBar(onSettingsClicked = onSettingsClicked)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (sdkConfigured) {
                        if (!callPermissionsState.allPermissionsGranted) {
                            if (callPermissionsState.shouldShowRationale) {
                                showRationaleDialog.value = true
                            } else {
                                callPermissionsState.launchMultiplePermissionRequest()
                            }
                        } else {
                            onLaunchCall()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.sdk_not_configured),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
            ) {
                Icon(Icons.Filled.Call, stringResource(R.string.call))
            }
        },
        content = { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)
            HomeScreenContent(
                contentModifier, configNeeded, configError
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(onSettingsClicked: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(onClick = { onSettingsClicked() }) {
                Icon(Icons.Rounded.Settings, contentDescription = null)
            }
        })
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    configNeeded: Boolean = false,
    configError: String? = null
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.avaya_logo),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth(0.75f),
            contentScale = ContentScale.FillWidth,
        )
        if (configNeeded) {
            Text(text = "Configuration data is needed", color = Color.Red)
        }
        configError?.let {
            Text(text = it, color = Color.Red)
        }
    }
}

@Composable
fun LoadingDialog(isDisplayed: Boolean) {
    if (isDisplayed) {
        Dialog(
            onDismissRequest = {},
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun PermissionRationaleDialog(
    showRationalDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            showRationalDialog.value = false
        },
        title = {
            Text(
                text = stringResource(R.string.permissions_required)
            )
        },
        text = {
            Text(stringResource(R.string.permissions_needed))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showRationalDialog.value = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(context, intent, null)

                }) {
                Text(text = stringResource(R.string.continue_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showRationalDialog.value = false
                }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
    )
}

@Preview
@Composable
fun LoadingDialogPreview() {
    SampleTheme {
        LoadingDialog(true)
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    SampleTheme {
        HomeScreen()
    }
}

@Preview
@Composable
fun HomeScreenContentPreview() {
    SampleTheme {
        HomeScreenContent()
    }
}
