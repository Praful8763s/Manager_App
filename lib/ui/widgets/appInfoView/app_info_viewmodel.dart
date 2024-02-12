// ignore_for_file: use_build_context_synchronously
import 'package:device_apps/device_apps.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:revanced_manager/app/app.locator.dart';
import 'package:revanced_manager/gen/strings.g.dart';
import 'package:revanced_manager/models/patched_application.dart';
import 'package:revanced_manager/services/manager_api.dart';
import 'package:revanced_manager/services/patcher_api.dart';
import 'package:revanced_manager/services/root_api.dart';
import 'package:revanced_manager/services/toast.dart';
import 'package:revanced_manager/ui/views/home/home_viewmodel.dart';
import 'package:revanced_manager/ui/views/navigation/navigation_viewmodel.dart';
import 'package:revanced_manager/ui/views/patcher/patcher_viewmodel.dart';
import 'package:stacked/stacked.dart';

class AppInfoViewModel extends BaseViewModel {
  final ManagerAPI _managerAPI = locator<ManagerAPI>();
  final PatcherAPI _patcherAPI = locator<PatcherAPI>();
  final RootAPI _rootAPI = RootAPI();
  final Toast _toast = locator<Toast>();

  Future<void> uninstallApp(
    BuildContext context,
    PatchedApplication app,
    bool onlyUnpatch,
  ) async {
    var isUninstalled = onlyUnpatch;

    if (!onlyUnpatch) {
      // TODO(Someone): Wait for the app to uninstall successfully.
      isUninstalled = await DeviceApps.uninstallApp(app.packageName);
    }

    if (isUninstalled && app.isRooted && await _rootAPI.hasRootPermissions()) {
      await _rootAPI.uninstall(app.packageName);
    }

    if (isUninstalled) {
      await _managerAPI.deletePatchedApp(app);
      locator<HomeViewModel>().initialize(context);
    }
  }

  Future<void> navigateToPatcher(PatchedApplication app) async {
    locator<PatcherViewModel>().selectedApp = app;
    locator<PatcherViewModel>().selectedPatches =
        await _patcherAPI.getAppliedPatches(app.appliedPatches);
    locator<PatcherViewModel>().notifyListeners();
    locator<NavigationViewModel>().setIndex(1);
  }

  void updateNotImplemented(BuildContext context) {
    _toast.showBottom(t.appInfoView.updateNotImplemented);
  }

  Future<void> showUninstallDialog(
    BuildContext context,
    PatchedApplication app,
    bool onlyUnpatch,
  ) async {
    final bool hasRootPermissions = await _rootAPI.hasRootPermissions();
    if (app.isRooted && !hasRootPermissions) {
      return showDialog(
        context: context,
        builder: (context) => AlertDialog(
          title: Text(t.appInfoView.rootDialogTitle),
          content: Text(t.appInfoView.rootDialogText),
          actions: <Widget>[
            FilledButton(
              onPressed: () => Navigator.of(context).pop(),
              child: Text(t.okButton),
            ),
          ],
        ),
      );
    } else {
      if (onlyUnpatch) {
        return showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: Text(t.appInfoView.unmountButton),
            content: Text(t.appInfoView.unmountDialogText),
            actions: <Widget>[
              TextButton(
                onPressed: () => Navigator.of(context).pop(),
                child: Text(t.noButton),
              ),
              FilledButton(
                onPressed: () {
                  uninstallApp(context, app, true);
                  Navigator.of(context).pop();
                  Navigator.of(context).pop();
                },
                child: Text(t.yesButton),
              ),
            ],
          ),
        );
      } else {
        return showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: Text(t.appInfoView.uninstallButton),
            content: Text(t.appInfoView.uninstallDialogText),
            actions: <Widget>[
              TextButton(
                onPressed: () => Navigator.of(context).pop(),
                child: Text(t.noButton),
              ),
              FilledButton(
                onPressed: () {
                  uninstallApp(context, app, false);
                  Navigator.of(context).pop();
                  Navigator.of(context).pop();
                },
                child: Text(t.yesButton),
              ),
            ],
          ),
        );
      }
    }
  }

  String getPrettyDate(BuildContext context, DateTime dateTime) {
    return DateFormat.yMMMMd(Localizations.localeOf(context).languageCode)
        .format(dateTime);
  }

  String getPrettyTime(BuildContext context, DateTime dateTime) {
    return DateFormat.jm(Localizations.localeOf(context).languageCode)
        .format(dateTime);
  }

  Future<void> showAppliedPatchesDialog(
    BuildContext context,
    PatchedApplication app,
  ) async {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(t.appInfoView.appliedPatchesLabel),
        content: SingleChildScrollView(
          child: Text(getAppliedPatchesString(app.appliedPatches)),
        ),
        actions: <Widget>[
          FilledButton(
            onPressed: () => Navigator.of(context).pop(),
            child: Text(t.okButton),
          ),
        ],
      ),
    );
  }

  String getAppliedPatchesString(List<String> appliedPatches) {
    return '• ${appliedPatches.join('\n• ')}';
  }

  void openApp(PatchedApplication app) {
    DeviceApps.openApp(app.packageName);
  }
}
