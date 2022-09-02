import 'package:flutter/material.dart';
import 'package:flutter_i18n/flutter_i18n.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:revanced_manager/constants.dart';
import 'package:revanced_manager/theme.dart';
import 'package:revanced_manager/ui/views/contributors/contributors_view.dart';
import 'package:revanced_manager/ui/views/settings/settings_viewmodel.dart';
import 'package:revanced_manager/ui/widgets/settingsView/about_info_widget.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_tile_dialog.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_section.dart';
import 'package:revanced_manager/ui/widgets/settingsView/settings_switch_item.dart';
import 'package:revanced_manager/ui/widgets/settingsView/social_media_cards.dart';
import 'package:revanced_manager/ui/widgets/settingsView/sources_widget.dart';
import 'package:revanced_manager/ui/widgets/shared/open_container_wrapper.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_themes/stacked_themes.dart';

class SettingsView extends StatelessWidget {
  final TextEditingController organizationController = TextEditingController();
  final TextEditingController patchesSourceController = TextEditingController();
  final TextEditingController integrationsSourceController =
      TextEditingController();

  SettingsView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ViewModelBuilder<SettingsViewModel>.reactive(
      disposeViewModel: false,
      viewModelBuilder: () => SettingsViewModel(),
      onModelReady: (model) => model.initialize(),
      builder: (context, SettingsViewModel model, child) => Scaffold(
        body: CustomScrollView(
          slivers: <Widget>[
            SliverAppBar(
              pinned: true,
              snap: false,
              floating: false,
              expandedHeight: 100.0,
              automaticallyImplyLeading: false,
              backgroundColor: MaterialStateColor.resolveWith(
                (states) => states.contains(MaterialState.scrolledUnder)
                    ? isDark
                        ? Theme.of(context).colorScheme.primary
                        : Theme.of(context).navigationBarTheme.backgroundColor!
                    : Theme.of(context).scaffoldBackgroundColor,
              ),
              flexibleSpace: FlexibleSpaceBar(
                titlePadding: const EdgeInsets.symmetric(
                  vertical: 23.0,
                  horizontal: 20.0,
                ),
                title: I18nText(
                  'settingsView.widgetTitle',
                  child: Text(
                    '',
                    style: GoogleFonts.inter(
                      color: Theme.of(context).textTheme.headline5!.color,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ),
            ),
            SliverPadding(
              padding: const EdgeInsets.symmetric(
                vertical: 10.0,
                horizontal: 20.0,
              ),
              sliver: SliverList(
                delegate: SliverChildListDelegate.fixed(
                  <Widget>[
                    SettingsSection(
                      title: 'settingsView.appearanceSectionTitle',
                      children: <Widget>[
                        SettingsSwitchItem(
                          title: 'settingsView.themeLabel',
                          subtitle: 'settingsView.themeHint',
                          value: isDark,
                          onTap: (value) {
                            isDark = value;
                            getThemeManager(context).toggleDarkLightTheme();
                          },
                        ),
                      ],
                    ),
                    SettingsTileDialog(
                        title: 'settingsView.languageLabel',
                        subtitle: 'English',
                        children: <Widget>[
                          RadioListTile<String>(
                            title: I18nText('settingsView.englishOption'),
                            value: 'en',
                            groupValue: 'en',
                            onChanged: (value) {
                              model.updateLanguage(context, value);
                              Navigator.of(context).pop();
                            },
                          ),
                          RadioListTile<String>(
                            title: I18nText('settingsView.frenchOption'),
                            value: 'fr',
                            groupValue: 'en',
                            onChanged: (value) {
                              model.updateLanguage(context, value);
                              Navigator.of(context).pop();
                            },
                          ),
                        ]),
                    const Divider(thickness: 1.0),
                    SettingsSection(
                      title: 'settingsView.patcherSectionTitle',
                      children: <Widget>[
                        ListTile(
                          contentPadding: EdgeInsets.zero,
                          title: I18nText(
                            'settingsView.rootModeLabel',
                            child: Text(
                              '',
                              style: kSettingItemTextStyle,
                            ),
                          ),
                          subtitle: I18nText('settingsView.rootModeHint'),
                          trailing: GestureDetector(
                            onTap: () => model.navigateToRootChecker(),
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 16, vertical: 8),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(20),
                                border: Border.all(
                                  width: 1,
                                  color:
                                      Theme.of(context).colorScheme.secondary,
                                ),
                              ),
                              child: Text(
                                model.isRooted ? 'Rooted' : 'Not rooted',
                              ),
                            ),
                          ),
                        ),
                        SourcesWidget(
                          title: 'settingsView.sourcesLabel',
                          organizationController: organizationController,
                          patchesSourceController: patchesSourceController,
                          integrationsSourceController:
                              integrationsSourceController,
                        ),
                      ],
                    ),
                    const Divider(thickness: 1.0),
                    SettingsSection(
                      title: 'settingsView.teamSectionTitle',
                      children: <Widget>[
                        OpenContainerWrapper(
                          openBuilder: (_, __) => const ContributorsView(),
                          closedBuilder: (_, openContainer) => ListTile(
                            contentPadding: EdgeInsets.zero,
                            title: I18nText(
                              'settingsView.contributorsLabel',
                              child: Text('', style: kSettingItemTextStyle),
                            ),
                            subtitle: I18nText('settingsView.contributorsHint'),
                            onTap: openContainer,
                          ),
                        ),
                        const SocialMediaCard(),
                      ],
                    ),
                    const Divider(thickness: 1.0),
                    const SettingsSection(
                      title: 'settingsView.infoSectionTitle',
                      children: <Widget>[
                        AboutWidget(),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
