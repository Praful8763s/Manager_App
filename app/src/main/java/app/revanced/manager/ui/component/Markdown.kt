package app.revanced.manager.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.markdownColor
import com.mikepenz.markdown.model.markdownTypography

@Composable
fun Markdown(
    text: String
) {
    val markdown = text.trimIndent()

    Markdown(
        content = markdown,
        colors = markdownColor(text = MaterialTheme.colorScheme.onSurfaceVariant),
        typography = markdownTypography(
            h1 = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            h2 = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            h3 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            text = MaterialTheme.typography.bodyMedium,
            list = MaterialTheme.typography.bodyMedium
        )
    )
}