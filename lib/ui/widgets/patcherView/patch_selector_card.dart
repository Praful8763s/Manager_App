import 'package:flutter/material.dart';
import 'package:flutter_i18n/flutter_i18n.dart';
import 'package:revanced_manager/app/app.locator.dart';
import 'package:revanced_manager/models/patch.dart';
import 'package:revanced_manager/ui/views/patcher/patcher_viewmodel.dart';
import 'package:revanced_manager/ui/widgets/shared/custom_card.dart';

class PatchSelectorCard extends StatelessWidget {
  const PatchSelectorCard({
    Key? key,
    required this.onPressed,
  }) : super(key: key);
  final Function() onPressed;

  @override
  Widget build(BuildContext context) {
    return CustomCard(
      onTap: onPressed,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Row(
            children: <Widget>[
              I18nText(
                locator<PatcherViewModel>().selectedPatches.isEmpty
                    ? 'patchSelectorCard.widgetTitle'
                    : 'patchSelectorCard.widgetTitleSelected',
                child: const Text(
                  '',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
              Text(
                locator<PatcherViewModel>().selectedPatches.isEmpty
                    ? ''
                    : ' (${locator<PatcherViewModel>().selectedPatches.length})',
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          if (locator<PatcherViewModel>().selectedApp == null)
            I18nText('patchSelectorCard.widgetSubtitle')
          else
            locator<PatcherViewModel>().selectedPatches.isEmpty
                ? I18nText('patchSelectorCard.widgetEmptySubtitle')
                : Text(_getPatchesSelection()),
        ],
      ),
    );
  }

  String _getPatchesSelection() {
    String text = '';
    // Nope, I'm not gonna question about sorting "lexicographically", work.
    final List<Patch> patches = locator<PatcherViewModel>().selectedPatches;
    patches.sort((a, b) => a.getSimpleName().compareTo(b.getSimpleName()));

    for (final Patch p in patches) {
      text += '\u2022  ${p.getSimpleName()}\n';
    }
    return text.substring(0, text.length - 1);
  }
}
