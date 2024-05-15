package com.avaya.axp.client.sample.ui.call

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avaya.axp.client.sample.call.TelecomCallNotificationManager
import com.avaya.axp.client.sample.ui.theme.SampleTheme
import com.avaya.axp.client.sdk.webrtc.TelecomCallRepository
import com.avaya.axp.client.sdk.webrtc.callNotificationManager
import com.avaya.axp.client.sdk.webrtc.updateCall
import org.slf4j.LoggerFactory.getLogger

/**
 * This activity is used to launch the incoming or ongoing call.
 *
 * It uses special flags to be able to be launched in the lockscreen and as a
 * full-screen notification.
 */
class CallActivity : ComponentActivity() {

    private val log = getLogger("CallActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The repo contains all the call logic and communication with the Telecom SDK.
        val repository =
            TelecomCallRepository.instance ?: TelecomCallRepository.create(applicationContext)
        if (callNotificationManager == null) {
            callNotificationManager = TelecomCallNotificationManager(applicationContext)
        }

        // Set the flags for a call type activity.
        setupCallActivity()

        setContent {
            SampleTheme(darkTheme = true) {
                val callViewModel: CallViewModel = viewModel(
                    factory = CallViewModel.provideFactory(repository)
                )
                TelecomCallScreen(callViewModel) {
                    // If we receive that the called finished, finish the activity
                    finishAndRemoveTask()
                    log.debug("Call finished. Finishing activity")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Force the service to update in case something change like Mic permissions.
        updateCall()
    }

    /**
     * Enable the calling activity to be shown in the lockscreen and dismiss the keyguard to enable
     * users to answer without unblocking.
     */
    private fun setupCallActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                FLAG_KEEP_SCREEN_ON or FLAG_ALLOW_LOCK_WHILE_SCREEN_ON,
            )
        }

        getSystemService<KeyguardManager>()?.requestDismissKeyguard(this, null)
    }
}
