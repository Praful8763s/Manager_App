import 'package:flutter/material.dart';
import 'package:flutter_i18n/flutter_i18n.dart';
import 'package:revanced_manager/app/app.locator.dart';
import 'package:revanced_manager/models/patch.dart';
import 'package:revanced_manager/services/manager_api.dart';
import 'package:revanced_manager/services/toast.dart';
import 'package:revanced_manager/ui/widgets/shared/custom_card.dart';
import 'package:revanced_manager/ui/widgets/shared/custom_material_button.dart';

// ignore: must_be_immutable
class PatchItem extends StatefulWidget {
  PatchItem({
    Key? key,
    required this.name,
    required this.simpleName,
    required this.description,
    required this.packageVersion,
    required this.supportedPackageVersions,
    required this.isUnsupported,
    required this.isNew,
    required this.hasUnsupportedPatchOption,
    required this.options,
    required this.isSelected,
    required this.onChanged,
    required this.navigateToOptions,
    required this.isChangeEnabled,
    this.child,
  }) : super(key: key);
  final String name;
  final String simpleName;
  final String description;
  final String packageVersion;
  final List<String> supportedPackageVersions;
  final bool isUnsupported;
  final bool isNew;
  final bool hasUnsupportedPatchOption;
  final List<Option> options;
  bool isSelected;
  final Function(bool) onChanged;
  final void Function(List<Option>) navigateToOptions;
  final bool isChangeEnabled;
  final Widget? child;
  final toast = locator<Toast>();
  final _managerAPI = locator<ManagerAPI>();

  @override
  State<PatchItem> createState() => _PatchItemState();
}

class _PatchItemState extends State<PatchItem> {
  @override
  Widget build(BuildContext context) {
    widget.isSelected = widget.isSelected &&
        (!widget.isUnsupported ||
            widget._managerAPI.areExperimentalPatchesEnabled()) &&
        !widget.hasUnsupportedPatchOption;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Opacity(
        opacity: widget.isUnsupported &&
                widget._managerAPI.areExperimentalPatchesEnabled() == false
            ? 0.5
            : 1,
        child: CustomCard(
          onTap: () {
            if (widget.isUnsupported &&
                !widget._managerAPI.areExperimentalPatchesEnabled()) {
              widget.isSelected = false;
              widget.toast.showBottom('patchItem.unsupportedPatchVersion');
            } else if (widget.isChangeEnabled) {
              if (!widget.isSelected) {
                if (widget.hasUnsupportedPatchOption) {
                  _showUnsupportedRequiredOptionDialog();
                  return;
                }
              }
              widget.isSelected = !widget.isSelected;
              setState(() {});
            }
            if (!widget.isUnsupported ||
                widget._managerAPI.areExperimentalPatchesEnabled()) {
              widget.onChanged(widget.isSelected);
            }
          },
          child: Column(
            children: <Widget>[
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  Flexible(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: <Widget>[
                            Expanded(
                              child: Text(
                                widget.simpleName,
                                maxLines: 2,
                                overflow: TextOverflow.visible,
                                style: const TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 4),
                        Text(
                          widget.description,
                          softWrap: true,
                          overflow: TextOverflow.visible,
                          style: TextStyle(
                            fontSize: 14,
                            color: Theme.of(context)
                                .colorScheme
                                .onSecondaryContainer,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Transform.scale(
                    scale: 1.2,
                    child: Checkbox(
                      value: widget.isSelected,
                      activeColor: Theme.of(context).colorScheme.primary,
                      checkColor:
                          Theme.of(context).colorScheme.secondaryContainer,
                      side: BorderSide(
                        width: 2.0,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                      onChanged: (newValue) {
                        if (widget.isUnsupported &&
                            !widget._managerAPI
                                .areExperimentalPatchesEnabled()) {
                          widget.isSelected = false;
                          widget.toast.showBottom(
                            'patchItem.unsupportedPatchVersion',
                          );
                        } else if (widget.isChangeEnabled) {
                          if (!widget.isSelected) {
                            if (widget.hasUnsupportedPatchOption) {
                              _showUnsupportedRequiredOptionDialog();
                              return;
                            }
                          }
                          widget.isSelected = newValue!;
                          setState(() {});
                        }
                        if (!widget.isUnsupported ||
                            widget._managerAPI
                                .areExperimentalPatchesEnabled()) {
                          widget.onChanged(widget.isSelected);
                        }
                      },
                    ),
                  ),
                ],
              ),
              Align(
                alignment: Alignment.topLeft,
                child: Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: [
                    if (widget.isUnsupported &&
                        widget._managerAPI.areExperimentalPatchesEnabled())
                      Padding(
                        padding: const EdgeInsets.only(top: 8),
                        child: IconButton(
                          icon: const Icon(Icons.warning, size: 20.0),
                          tooltip: FlutterI18n.translate(
                            context,
                            'warning',
                          ),
                          onPressed: () => _showUnsupportedWarningDialog(),
                          style: ButtonStyle(
                            shape: MaterialStateProperty.all(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(8),
                                side: BorderSide(
                                  color:
                                      Theme.of(context).colorScheme.secondary,
                                ),
                              ),
                            ),
                            backgroundColor: MaterialStateProperty.all(
                              Colors.transparent,
                            ),
                            foregroundColor: MaterialStateProperty.all(
                              Theme.of(context).colorScheme.secondary,
                            ),
                          ),
                        ),
                      ),
                    if (widget.isNew)
                      Padding(
                        padding: const EdgeInsets.only(top: 8),
                        child: IconButton(
                          icon: const Icon(Icons.star, size: 20.0),
                          tooltip: FlutterI18n.translate(
                            context,
                            'new',
                          ),
                          onPressed: () => _showNewPatchDialog(),
                          style: ButtonStyle(
                            shape: MaterialStateProperty.all(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(8),
                                side: BorderSide(
                                  color:
                                      Theme.of(context).colorScheme.secondary,
                                ),
                              ),
                            ),
                            backgroundColor: MaterialStateProperty.all(
                              Colors.transparent,
                            ),
                            foregroundColor: MaterialStateProperty.all(
                              Theme.of(context).colorScheme.secondary,
                            ),
                          ),
                        ),
                      ),
                    if (widget.options.isNotEmpty)
                      Padding(
                        padding: const EdgeInsets.only(top: 8),
                        child: IconButton(
                          icon: const Icon(Icons.settings, size: 20.0),
                          tooltip: FlutterI18n.translate(
                            context,
                            'options',
                          ),
                          onPressed: () =>
                              widget.navigateToOptions(widget.options),
                          style: ButtonStyle(
                            shape: MaterialStateProperty.all(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(8),
                                side: BorderSide(
                                  color:
                                      Theme.of(context).colorScheme.secondary,
                                ),
                              ),
                            ),
                            backgroundColor: MaterialStateProperty.all(
                              Colors.transparent,
                            ),
                            foregroundColor: MaterialStateProperty.all(
                              Theme.of(context).colorScheme.secondary,
                            ),
                          ),
                        ),
                      ),
                  ],
                ),
              ),
              widget.child ?? const SizedBox(),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _showUnsupportedWarningDialog() {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: I18nText('warning'),
        backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
        content: I18nText(
          'patchItem.unsupportedDialogText',
          translationParams: {
            'packageVersion': widget.packageVersion,
            'supportedVersions':
                '• ${widget.supportedPackageVersions.reversed.join('\n• ')}',
          },
        ),
        actions: <Widget>[
          CustomMaterialButton(
            label: I18nText('okButton'),
            onPressed: () => Navigator.of(context).pop(),
          ),
        ],
      ),
    );
  }

  Future<void> _showUnsupportedRequiredOptionDialog() {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: I18nText('notice'),
        backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
        content: I18nText(
          'patchItem.unsupportedRequiredOption',
        ),
        actions: <Widget>[
          CustomMaterialButton(
            label: I18nText('okButton'),
            onPressed: () => Navigator.of(context).pop(),
          ),
        ],
      ),
    );
  }

  Future<void> _showNewPatchDialog() {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: I18nText('patchItem.newPatch'),
        backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
        content: I18nText(
          'patchItem.newPatchDialogText',
        ),
        actions: <Widget>[
          CustomMaterialButton(
            label: I18nText('okButton'),
            onPressed: () => Navigator.of(context).pop(),
          ),
        ],
      ),
    );
  }
}
