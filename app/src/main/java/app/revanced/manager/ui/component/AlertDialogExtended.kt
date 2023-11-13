package app.revanced.manager.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExtended(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    tertiaryButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    textHorizontalPadding: PaddingValues = PaddingValues(horizontal = 24.dp)
) {
    AlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column (modifier = Modifier.padding(vertical = 24.dp)) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    icon?.let {
                        CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                            Box(
                                Modifier
                                    .padding(bottom = 16.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                icon()
                            }
                        }
                    }
                    title?.let {
                        CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                            val textStyle = MaterialTheme.typography.headlineSmall
                            ProvideTextStyle(textStyle) {
                                Box(
                                    // Align the title to the center when an icon is present.
                                    Modifier
                                        .padding(bottom = 16.dp)
                                        .align(
                                            if (icon == null) {
                                                Alignment.Start
                                            } else {
                                                Alignment.CenterHorizontally
                                            }
                                        )
                                ) {
                                    title()
                                }
                            }
                        }
                    }
                }
                text?.let {
                    CompositionLocalProvider(LocalContentColor provides textContentColor) {
                        val textStyle =
                            MaterialTheme.typography.bodyMedium
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    .weight(weight = 1f, fill = false)
                                    .padding(bottom = 24.dp)
                                    .padding(textHorizontalPadding)
                                    .align(Alignment.Start)
                            ) {
                                text()
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                ) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                        val textStyle = MaterialTheme.typography.labelLarge
                        ProvideTextStyle(value = textStyle) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp, if (tertiaryButton != null) Alignment.Start else Alignment.End),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tertiaryButton?.let {
                                    it.invoke()
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                dismissButton?.invoke()
                                confirmButton()
                            }
                        }
                    }
                }
            }
        }
    }
}