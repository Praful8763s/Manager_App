import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:revanced_manager/gen/strings.g.dart';
import 'package:revanced_manager/ui/views/contributors/contributors_viewmodel.dart';
import 'package:revanced_manager/ui/widgets/contributorsView/contributors_card.dart';
import 'package:revanced_manager/ui/widgets/shared/custom_sliver_app_bar.dart';
import 'package:stacked/stacked.dart';

class ContributorsView extends StatelessWidget {
  const ContributorsView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ViewModelBuilder<ContributorsViewModel>.reactive(
      viewModelBuilder: () => ContributorsViewModel(),
      onViewModelReady: (model) => model.getContributors(),
      builder: (context, model, child) => Scaffold(
        body: CustomScrollView(
          slivers: <Widget>[
            CustomSliverAppBar(
              title: Text(
                t.contributorsView.widgetTitle,
                style: GoogleFonts.inter(
                  color: Theme.of(context).textTheme.titleLarge!.color,
                ),
              ),
            ),
            SliverPadding(
              padding: const EdgeInsets.all(20.0),
              sliver: SliverList(
                delegate: SliverChildListDelegate.fixed(
                  <Widget>[
                    ContributorsCard(
                      title: t.contributorsView.patcherContributors,
                      contributors: model.patcherContributors,
                    ),
                    const SizedBox(height: 20),
                    ContributorsCard(
                      title: t.contributorsView.patchesContributors,
                      contributors: model.patchesContributors,
                    ),
                    const SizedBox(height: 20),
                    ContributorsCard(
                      title: t.contributorsView.integrationsContributors,
                      contributors: model.integrationsContributors,
                    ),
                    const SizedBox(height: 20),
                    ContributorsCard(
                      title: t.contributorsView.cliContributors,
                      contributors: model.cliContributors,
                    ),
                    const SizedBox(height: 20),
                    ContributorsCard(
                      title: t.contributorsView.managerContributors,
                      contributors: model.managerContributors,
                    ),
                    SizedBox(height: MediaQuery.viewPaddingOf(context).bottom),
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
