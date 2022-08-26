package app.revanced.manager.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun HeadlineWithCard(
    @StringRes headline: Int,
    content: @Composable () -> Unit
) {
    Column {
        Text(stringResource(headline))
        ElevatedCard(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
        ) { content() }
    }
}