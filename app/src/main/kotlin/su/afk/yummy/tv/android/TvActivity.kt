package su.afk.yummy.tv.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import su.afk.yummy.tv.core.deeplink.DeepLinkHandler
import su.afk.yummy.tv.core.tv.api.ITvIntegration
import su.afk.yummy.tv.feature.main.TvMainGraph
import su.afk.yummy.tv.feature.search.android.SystemSearchIntentHandler
import javax.inject.Inject

@AndroidEntryPoint
class TvActivity : ComponentActivity() {

    @Inject
    lateinit var mainGraph: TvMainGraph
    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    @Inject
    lateinit var searchIntentHandler: SystemSearchIntentHandler
    @Inject
    lateinit var tvIntegration: ITvIntegration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        tvIntegration.bindBrowsableChannelRequests(this, lifecycleScope)

        setContent {
            mainGraph.MainGraph()
        }

        tvIntegration.start()
        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (!searchIntentHandler.handle(intent)) {
            deepLinkHandler.handle(intent)
        }
    }

}
