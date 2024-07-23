package com.avaya.axp.omni.sample.calling.ui.call

import android.Manifest
import android.content.res.Resources
import android.telecom.DisconnectCause
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.SendToMobile
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.BluetoothAudio
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.SpeakerPhone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.telecom.CallEndpointCompat
import com.avaya.axp.omni.sample.calling.R
import com.avaya.axp.omni.sample.calling.ui.theme.SampleTheme
import com.avaya.axp.omni.sample.calling.ui.utility.rememberPermissionStateSafe
import com.avaya.axp.omni.sdk.core.AxpSdkError
import com.avaya.axp.omni.sdk.webrtc.TelecomCall
import com.avaya.axp.omni.sdk.webrtc.TelecomCallAction
import com.avaya.axp.omni.sdk.webrtc.TelecomError
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory.getLogger

@Composable
internal fun TelecomCallScreen(
    callViewModel: CallViewModel,
    onCallFinished: () -> Unit
) {
    // Collect the current call state and update UI
    val currentCall by callViewModel.currentCallFlow.collectAsState()
    when (val call = currentCall) {
        is TelecomCall.Unregistered, TelecomCall.None -> {
            // If there is no call invoke finish after a small delay
            LaunchedEffect(Unit) {
                delay(500)
                onCallFinished()
            }
            // Show call ended when there is no active call
            NoCallScreen(call.disconnectCause)
        }

        is TelecomCall.Registered -> {
            // Call screen only contains the logic to represent the values of the active call
            // and process user input by calling the processAction of the active call.
            CallScreen(
                name = call.callAttributes.displayName.toString(),
                isActive = call.isActive,
                isMuted = call.isMuted,
                error = call.error,
                currentEndpoint = call.currentCallEndpoint,
                endpoints = call.availableCallEndpoints,
                onCallAction = call::processAction
            ) { callViewModel.sendAndPlayDtmfTone(it.dtmfTone) }
        }
    }
}

@Composable
private fun NoCallScreen(disconnectCause: DisconnectCause?) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.call_ended),
                style = MaterialTheme.typography.titleLarge
            )
            disconnectCause?.let {
                Text(
                    text = disconnectCause.reason ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun CallScreen(
    name: String,
    isActive: Boolean,
    isMuted: Boolean,
    error: AxpSdkError?,
    currentEndpoint: CallEndpointCompat?,
    endpoints: List<CallEndpointCompat>,
    onCallAction: (TelecomCallAction) -> Unit,
    onCallKeyPressed: (Key) -> Unit
) {
    val context = LocalContext.current

    error?.let {
        Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        bottomBar = {
            CallControls(
                isActive = isActive,
                isMuted = isMuted,
                currentEndpoint = currentEndpoint,
                endpoints = endpoints,
                onCallAction = onCallAction,
                onKeyPressed = onCallKeyPressed
            )
        }
    ) {
        CallContent(it, name, isActive)
    }
}

private val AxpSdkError.errorMessage: String
    get() = when (this) {
        is TelecomError -> "Telecom error code $errorCode"
        else -> "Unknown error"
    }

@Composable
private fun CallContent(
    paddingValues: PaddingValues,
    name: String,
    isActive: Boolean
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .size(128.dp),
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        )
        Text(text = name, style = MaterialTheme.typography.titleLarge)

        if (!isActive) {
            Text(
                text = stringResource(R.string.connecting),
                style = MaterialTheme.typography.titleSmall
            )
        } else {
            Text(
                text = stringResource(R.string.connected),
                style = MaterialTheme.typography.titleSmall
            )
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallControls(
    isActive: Boolean,
    isMuted: Boolean,
    currentEndpoint: CallEndpointCompat?,
    endpoints: List<CallEndpointCompat>,
    onCallAction: (TelecomCallAction) -> Unit,
    onKeyPressed: (Key) -> Unit
) {
    val log = getLogger("CallControls")

    val micPermission = rememberPermissionStateSafe(permission = Manifest.permission.RECORD_AUDIO)
    var showRationale by remember(micPermission.status) {
        mutableStateOf(false)
    }

    var showEndPoints by remember {
        mutableStateOf(false)
    }
    var showKeypad by remember {
        mutableStateOf(false)
    }
    val endpointType = currentEndpoint?.type ?: CallEndpointCompat.TYPE_UNKNOWN
    Column {
        if (showKeypad) {
            KeypadDialog(onDismissRequest = { showKeypad = false },
                onKeyPressed = {
                    log.info("User pressed a DTMF key")
                    onKeyPressed(it)
                })
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FilledIconButton(
                modifier = Modifier
                    .size(48.dp),
                shape = CircleShape,
                onClick = {
                    showKeypad = !showKeypad
                },
                enabled = isActive
            ) {
                Icon(
                    imageVector = Icons.Rounded.Dialpad,
                    contentDescription = stringResource(R.string.show_keypad)
                )
            }
            if (micPermission.status.isGranted) {
                FilledIconToggleButton(
                    modifier = Modifier.size(48.dp),
                    checked = isMuted,
                    onCheckedChange = {
                        onCallAction(TelecomCallAction.ToggleMute(it))
                    }
                ) {
                    if (isMuted) {
                        Icon(
                            imageVector = Icons.Rounded.MicOff, contentDescription =
                            stringResource(R.string.mic_on)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Mic,
                            contentDescription = stringResource(R.string.mic_off)
                        )
                    }
                }
            } else {
                FilledIconButton(
                    onClick = {
                        if (micPermission.status.shouldShowRationale) {
                            showRationale = true
                        } else {
                            micPermission.launchPermissionRequest()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MicOff,
                        contentDescription = stringResource(R.string.missing_mic_permission),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Box {
                val resources = LocalContext.current.resources
                FilledIconButton(modifier = Modifier.size(48.dp),
                    onClick = { showEndPoints = !showEndPoints }) {
                    Icon(
                        imageVector = getEndpointIcon(type = endpointType),
                        contentDescription = "Toggle Endpoints",
                    )
                    Icon(
                        if (showEndPoints) {
                            Icons.Rounded.ArrowDropUp
                        } else {
                            Icons.Rounded.ArrowDropDown
                        },
                        contentDescription = resources.getEndpointLabel(
                            type = endpointType
                        ),
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }
                DropdownMenu(
                    expanded = showEndPoints,
                    onDismissRequest = { showEndPoints = false },
                ) {
                    endpoints.forEach {
                        CallEndPointItem(
                            endPoint = it,
                            onDeviceSelected = {
                                onCallAction(TelecomCallAction.SwitchAudioEndpoint(it.identifier))
                                showEndPoints = false
                            },
                        )
                    }
                }
            }
            FilledIconButton(
                modifier = Modifier
                    .size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Red
                ),
                shape = CircleShape,
                onClick = {
                    log.info("User pressed the hangup button")
                    onCallAction(
                        TelecomCallAction.Disconnect(DisconnectCause.LOCAL)
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.CallEnd,
                    contentDescription = stringResource(R.string.end_call)
                )
            }
        }
    }
    // Show a rationale dialog if user didn't accepted the permissions
    if (showRationale) {
        RationaleMicDialog(
            onResult = { request ->
                if (request) {
                    micPermission.launchPermissionRequest()
                }
                showRationale = false
            },
        )
    }
}

@Composable
private fun CallEndPointItem(
    endPoint: CallEndpointCompat,
    onDeviceSelected: (CallEndpointCompat) -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = endPoint.name.toString()) },
        onClick = { onDeviceSelected(endPoint) },
        leadingIcon = {
            Icon(
                getEndpointIcon(endPoint.type),
                contentDescription = endPoint.name.toString(),
            )
        },
    )
}

private fun getEndpointIcon(type: @CallEndpointCompat.Companion.EndpointType Int): ImageVector =
    when (type) {
        CallEndpointCompat.TYPE_BLUETOOTH -> Icons.Rounded.BluetoothAudio
        CallEndpointCompat.TYPE_SPEAKER -> Icons.Rounded.SpeakerPhone
        CallEndpointCompat.TYPE_STREAMING -> Icons.AutoMirrored.Rounded.SendToMobile
        CallEndpointCompat.TYPE_WIRED_HEADSET -> Icons.Rounded.Headphones
        else -> Icons.Rounded.Phone
    }

private fun Resources.getEndpointLabel(
    type: @CallEndpointCompat.Companion.EndpointType Int
): String =
    when (type) {
        CallEndpointCompat.TYPE_BLUETOOTH -> getString(R.string.bluetooth)
        CallEndpointCompat.TYPE_SPEAKER -> getString(R.string.speaker)
        CallEndpointCompat.TYPE_STREAMING -> getString(R.string.streaming)
        CallEndpointCompat.TYPE_WIRED_HEADSET -> getString(R.string.headset)
        else -> getString(R.string.phone)
    }

@Composable
private fun RationaleMicDialog(onResult: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onResult(false) },
        confirmButton = {
            TextButton(onClick = { onResult(true) }) {
                Text(text = stringResource(R.string.continue_label))
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult(false) }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.permissions_required))
        },
        text = {
            Text(text = stringResource(R.string.mic_permission_rationale))
        }
    )
}

@Preview
@Composable
fun CallScreenPreview() {
    SampleTheme(darkTheme = true) {
        CallScreen(
            name = "Unknown",
            isActive = true,
            isMuted = false,
            error = null,
            currentEndpoint = null,
            endpoints = emptyList(),
            onCallAction = { },
            onCallKeyPressed = {}
        )
    }
}

@Preview
@Composable
fun NoCallPreview() {
    SampleTheme(darkTheme = true) {
        NoCallScreen(
            disconnectCause = DisconnectCause(
                DisconnectCause.ERROR,
                "This is a call Preview"
            )
        )
    }
}

@Preview
@Composable
fun CallControlsPreview() {
    SampleTheme(darkTheme = true) {
        CallControls(
            isActive = true,
            isMuted = false,
            currentEndpoint = null,
            endpoints = emptyList(),
            onCallAction = { },
            onKeyPressed = {}
        )
    }
}

private val TelecomCall.disconnectCause: DisconnectCause?
    get() = if (this is TelecomCall.Unregistered) disconnectCause else null
