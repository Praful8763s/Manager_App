package app.revanced.manager.ui.screen.settings.update

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.revanced.manager.R
import app.revanced.manager.domain.manager.PreferencesManager
import app.revanced.manager.ui.component.AppTopBar
import app.revanced.manager.ui.component.settings.BooleanItem
import app.revanced.manager.ui.component.settings.SettingsListItem
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesSettingsScreen(
    onBackClick: () -> Unit,
    onChangelogClick: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    val prefs: PreferencesManager = rememberKoinInject()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.updates),
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // TODO: check if a new version is available before bringing them to the page.
            SettingsListItem(
                modifier = Modifier.clickable(onClick = onUpdateClick),
                headlineContent = stringResource(R.string.manual_update_check),
                supportingContent = stringResource(R.string.manual_update_check_description)
            )

            SettingsListItem(
                modifier = Modifier.clickable(onClick = onChangelogClick),
                headlineContent = stringResource(R.string.changelog),
                supportingContent = stringResource(
                    R.string.changelog_description
                )
            )

            BooleanItem(
                preference = prefs.managerAutoUpdates,
                headline = R.string.update_checking_manager,
                description = R.string.update_checking_manager_description
            )
        }
    }
}