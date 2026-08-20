package su.afk.yummy.tv.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import su.afk.yummy.tv.core.designsystem.R

@Composable
fun AppBrandIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.ic_yummy_brand),
        contentDescription = null,
        modifier = modifier,
    )
}
