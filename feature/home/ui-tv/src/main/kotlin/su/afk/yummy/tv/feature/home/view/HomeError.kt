package su.afk.yummy.tv.feature.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.presenter.tv.TvStateMessage
import su.afk.yummy.tv.feature.home.R
import su.afk.yummy.tv.feature.home.presentation.R as PresentationR

@Composable
internal fun HomeError(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TvStateMessage(
            title = stringResource(R.string.home_error_title),
            description = message,
            icon = Icons.Filled.Warning,
            retryLabel = stringResource(R.string.retry),
            onRetry = onRetry,
            fillMaxSize = false,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(
                PresentationR.string.home_error_status_hint,
                stringResource(PresentationR.string.home_error_status_url),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
