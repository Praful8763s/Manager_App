// ignore_for_file: prefer_const_constructors

import 'package:flutter/material.dart';
import 'package:revanced_manager/ui/views/settings/settingsFragment/settings_manage_api_url.dart';
import 'package:revanced_manager/ui/views/settings/settingsFragment/settings_manage_sources.dart';
import 'package:revanced_manager/ui/views/settings/settings_viewmodel.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_enable_patches_selection.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_auto_update_patches.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_experimental_patches.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_experimental_universal_patches.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_section.dart';

final _settingsViewModel = SettingsViewModel();

class SAdvancedSection extends StatelessWidget {
  const SAdvancedSection({super.key});

  @override
  Widget build(BuildContext context) {
    return SettingsSection(
      title: 'settingsView.advancedSectionTitle',
      children: <Widget>[
        SAutoUpdatePatches(),
        SEnablePatchesSelection(),
        SExperimentalPatches(),
        SExperimentalUniversalPatches(),
        SManageSourcesUI(),
        SManageApiUrlUI(),
        // SManageKeystorePasswordUI(),
      ],
    );
  }
}
