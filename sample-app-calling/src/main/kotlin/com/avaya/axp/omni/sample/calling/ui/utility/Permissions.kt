/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.avaya.axp.omni.sample.calling.ui.utility

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

/**
 * A variation of rememberMultiplePermissionsStateSafe that is safe for composable Previews
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberMultiplePermissionsStateSafe(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {},
): MultiplePermissionsState {
    return if (LocalInspectionMode.current) {
        object : MultiplePermissionsState {
            override val allPermissionsGranted: Boolean = false
            override val permissions: List<PermissionState> = emptyList()
            override val revokedPermissions: List<PermissionState> = emptyList()
            override val shouldShowRationale: Boolean = false
            override fun launchMultiplePermissionRequest() {}
        }
    } else {
        rememberMultiplePermissionsState(permissions, onPermissionsResult)
    }
}

/**
 * A variation of rememberPermissionState that is safe for composable Previews
 */
@ExperimentalPermissionsApi
@Composable
fun rememberPermissionStateSafe(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState {
    return if (LocalInspectionMode.current) {
        object : PermissionState {
            override val permission: String = ""
            override val status: PermissionStatus = PermissionStatus.Granted
            override fun launchPermissionRequest() {}
        }
    } else {
        rememberPermissionState(permission, onPermissionResult)
    }
}
