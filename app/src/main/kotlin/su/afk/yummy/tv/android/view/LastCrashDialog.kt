package su.afk.yummy.tv.android.view

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.R
import su.afk.yummy.tv.android.startup.LastCrashLogger

@Composable
fun LastCrashDialog() {
    val context = LocalContext.current
    var report by remember { mutableStateOf(LastCrashLogger.consume(context)) }
    val text = report ?: return
    AlertDialog(
        onDismissRequest = { report = null },
        title = { Text(stringResource(R.string.last_crash_title)) },
        text = {
            Text(
                text = text,
                modifier = Modifier
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton(onClick = { report = null }) {
                Text(stringResource(R.string.last_crash_ok))
            }
        },
    )
}
