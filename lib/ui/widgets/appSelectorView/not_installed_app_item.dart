import 'package:flutter/material.dart';
import 'package:flutter_i18n/flutter_i18n.dart';
import 'package:revanced_manager/ui/widgets/shared/custom_card.dart';

class NotInstalledAppItem extends StatefulWidget {
  const NotInstalledAppItem({
    Key? key,
    required this.name,
    required this.patchesCount,
    required this.suggestedVersion,
    this.onTap,
  }) : super(key: key);
  final String name;
  final int patchesCount;
  final String suggestedVersion;
  final Function()? onTap;

  @override
  State<NotInstalledAppItem> createState() => _NotInstalledAppItem();
}

class _NotInstalledAppItem extends State<NotInstalledAppItem> {
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: CustomCard(
        onTap: widget.onTap,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: <Widget>[
            Container(
              height: 48,
              padding: const EdgeInsets.symmetric(vertical: 4.0),
              alignment: Alignment.center,
              child: const CircleAvatar(
                backgroundColor: Colors.transparent,
                child: Icon(
                  Icons.square_rounded,
                  color: Colors.grey,
                  size: 44,
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Text(
                    widget.name,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  const SizedBox(height: 4),
                  I18nText(
                    'appSelectorCard.notInstalled',
                    child: Text(
                      '',
                      style: TextStyle(
                        color:
                        Theme.of(context).textTheme.titleLarge!.color,
                      ),
                    ),
                  ),
                  Wrap(
                    children: [
                      I18nText(
                        'suggested',
                      ),
                      if (widget.suggestedVersion.isEmpty)
                        I18nText(
                          'appSelectorCard.allVersions',
                        )
                      else
                        Text('v${widget.suggestedVersion}'),
                      const SizedBox(width: 4),
                      Text(
                        widget.patchesCount == 1
                            ? '• ${widget.patchesCount} patch'
                            : '• ${widget.patchesCount} patches',
                        style: TextStyle(
                          color: Theme.of(context).colorScheme.secondary,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
