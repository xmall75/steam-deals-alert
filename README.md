# Steam Deals Alert

Side project. I kept claiming 100% off games (not the free-to-play games) by checking the store manually, so now the phone does it for me.

Android app that notifies you when Steam games go on sale, including the ones that hit 100% off (free to keep, not free-to-play), so you can grab them before the promo ends.

## Features

- Checks for deals in the background (every 60 minutes by default, 15 minutes minimum)
- One notification per new deal, with thumbnail, sale price, normal price and Steam rating
- Tap it to open the game in the Steam app, or in the browser if Steam isn't installed
- Remembers what it already notified, so no repeats

It doesn't claim anything for you. You still tap "Add to Account" yourself.

## Filters

Change them from Settings in the app.

| Setting       | Default        |
|---------------|----------------|
| Price range   | $0.00 to $0.50 |
| Rating range  | 70% to 100%    |
| Sync interval | 60 min         |

Set max price to `0` if you only want free games (100% off not the free-to-play games).

## Data source

[CheapShark API](https://apidocs.cheapshark.com/). Public, no API key, no auth.

## Build

Open in Android Studio, sync Gradle, run. Allow notifications or nothing will ping you. The "Test Notification" button shows what a notification looks like.

## Notes

- CheapShark lags behind Steam a bit, so notifications can come a few minutes late.
- Background checks aren't exact. Battery saver can delay them.
- No promo end time in the data, so claim early.

## License

MIT, see [LICENSE](LICENSE).
