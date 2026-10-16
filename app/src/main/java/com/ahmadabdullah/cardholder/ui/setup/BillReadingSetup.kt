package com.ahmadabdullah.cardholder.ui.setup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.ahmadabdullah.cardholder.reminders.BillListener
import com.ahmadabdullah.cardholder.reminders.BillNotifications

/** Whether Card holder may show notifications and read them. Rechecked whenever the app resumes. */
data class BillReadingAccess(val canNotify: Boolean, val canReadNotifications: Boolean) {
    val isComplete: Boolean get() = canNotify && canReadNotifications

    companion object {
        fun of(context: Context) = BillReadingAccess(
            canNotify = BillNotifications.canPost(context),
            canReadNotifications = BillListener.isEnabled(context),
        )
    }
}

/** The current [BillReadingAccess], refreshed each time the user comes back to the app. */
@Composable
fun rememberBillReadingAccess(): BillReadingAccess {
    val context = LocalContext.current
    var access by remember { mutableStateOf(BillReadingAccess.of(context)) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { access = BillReadingAccess.of(context) }
    return access
}

/**
 * Explains what reading notifications means before asking for it, then walks the user through
 * the two switches: showing notifications, and notification access in Android's settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillReadingSetupScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var access by remember { mutableStateOf(BillReadingAccess.of(context)) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { access = BillReadingAccess.of(context) }
    val askToNotify = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        access = BillReadingAccess.of(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bill reminders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 520.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Card holder can read the bill your bank sends each month and remind you to pay it " +
                        "before the due date, counting around Fridays, Saturdays and bank holidays.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("What Card holder reads", style = MaterialTheme.typography.titleSmall)
                        Bullet("It sees each notification as it arrives, from any app, including your SMS app.")
                        Bullet("It keeps only credit card bills for cards you've saved here: the amounts due, the due date and the month.")
                        Bullet("Everything else is looked at and forgotten. Nothing is saved, and nothing leaves your phone.")
                        Bullet("You can turn this off at any time in Android's settings.")
                    }
                }

                Step(
                    number = 1,
                    title = "Allow notifications",
                    detail = "So Card holder can remind you.",
                    done = access.canNotify,
                    action = "Allow",
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            askToNotify.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            )
                        }
                    },
                )
                Step(
                    number = 2,
                    title = "Turn on notification access",
                    detail = "Android opens its settings. Switch on Card holder, then come back.",
                    done = access.canReadNotifications,
                    action = "Open settings",
                    onClick = { context.startActivity(notificationAccessSettings(context)) },
                )

                if (access.isComplete) {
                    Text(
                        "All set. The next bill your bank sends for a saved card will appear on that card.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

/** The settings page for Card holder's notification access, or the list of all apps on older Android. */
private fun notificationAccessSettings(context: Context): Intent =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
            .putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, BillListener.component(context).flattenToString())
    } else {
        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    }

@Composable
private fun Bullet(text: String) {
    Row {
        Text("•  ", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Step(number: Int, title: String, detail: String, done: Boolean, action: String, onClick: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("$number. $title", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (done) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
            } else {
                Button(onClick = onClick) { Text(action) }
            }
        }
    }
}
