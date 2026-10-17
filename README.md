# Card holder

A wallet for your credit cards, in the spirit of Google Wallet. It keeps each card's bank, network, tier, limits, billing date and expiry date in one place, without ever holding the full card number.

The first release covers credit cards issued in **Bangladesh**.

<p align="center">
  <img src="docs/screenshots/wallet.png" width="260" alt="The wallet, with three cards stacked">
  &nbsp;
  <img src="docs/screenshots/add-card.png" width="260" alt="Adding a card, with a live preview above the form">
  &nbsp;
  <img src="docs/screenshots/card-details.png" width="260" alt="A card's limits, expiry and next billing date">
</p>

## What it does

- **Wallet.** Your cards are stacked like real cards in a wallet. Each card is drawn to look like that bank's real card for its network and tier (see [Card designs](#card-designs)). Tap a card to see its details.
- **Card details.** Shows the BDT and USD limits, the expiry date, and the billing date along with the next date the bill is due. BDT amounts are grouped the Bangladeshi way (৳12,00,000).
- **Adding a card.** Choose the bank, then the network, then the tier. Each list offers only what the chosen bank actually issues, so you can't pick a card that doesn't exist. A preview of the card updates as you type.
- **Editing a card.** Use the edit button on the card's page, for example when an endorsed passport raises the USD limit, the card is upgraded or it's renewed with a new expiry date. Its bills stay with it.
- **Removing a card.** Use the delete button on the card's page. It asks before removing.
- **Bill reminders.** Card holder reads the bill your bank sends each month (by SMS or app notification), saves it on the matching card, and reminds you 7, 5, 3, 2 and 1 days before the last safe day to pay. On that day itself an urgent "Pay today" reminder arrives with a red warning. Mark the bill as paid to stop the reminders.
- **Bills for cards you haven't added.** Card holder offers to add the card, with the bank, the network and whatever digits the bill showed already filled in. Once the card is saved, the bill appears on it.

### How the pay-by date is worked out

- **Banks are closed on Fridays, Saturdays and bank holidays.** When a due date falls on one of these, the pay-by date moves to the last open day before it, never after.
- **USD dues come first.** A USD due is paid in BDT, and the bank takes working days to convert it, so that part has an earlier pay-by date with its own countdown. UCB takes 2 working days; other banks are assumed to take 2 until known.
- **Holidays and working Saturdays** come from Bangladesh Bank's official list and circulars. The app downloads [`data/bank-calendar.json`](data/bank-calendar.json) from this repository once a week, so a holiday or working Saturday announced at short notice reaches every phone without a new release. See [Updating holidays](#updating-holidays).
- **Bills are read from UCB, City and EBL** so far. Other banks' bills are read if their messages use the same wording ("Total Due", "Min Due", a due date and a masked card number); more formats will be added as samples come in.

### What you enter

| Field | Notes |
|---|---|
| Bank | Picked from the banks below |
| Card network | Only the networks that bank issues |
| Card tier | Only the tiers that bank issues on that network |
| First and last name | As printed on the card |
| First 4 and last 4 digits | The middle digits are never asked for |
| Expiry month and year | Months that have already passed can't be chosen |
| BDT limit | Required |
| USD limit | Enter 0 if the card has none |
| Billing date | The day of the month the statement is generated |

### How the details are checked

- **The first 4 digits must match the network:** Visa starts with 4, Mastercard with 51–55 or 2221–2720, Amex with 34 or 37, Diners Club with 300–305, 36, 38 or 39, UnionPay with 62, and JCB with 3528–3589.
- **Names** can contain only letters, spaces, dots, hyphens and apostrophes.
- **Changing the bank** clears the network and tier you had chosen if the new bank doesn't offer them.
- **A billing date of 29–31** falls on the last day of shorter months.
- **Errors for empty fields** stay hidden until you tap *Save card*. A wrong value is flagged as soon as you type it.

## Supported banks

21 Bangladeshi banks and their credit cards on Visa, Mastercard, American Express, Diners Club, UnionPay and JCB. The list was taken from each bank's own credit card pages in October 2026 and lives in [`BankCatalog.kt`](app/src/main/java/com/ahmadabdullah/cardholder/data/BankCatalog.kt). If your card is missing, see [Updating the bank list](#updating-the-bank-list).

## Privacy

- **Only the first and last 4 digits** of the card number are kept. The app never asks for the full number, the CVV or the PIN.
- **Cards and bills stay on the phone.** They are saved in the app's private storage and left out of Google cloud backups and device-to-device transfers.
- **Bill reading is opt-in.** It works only after you switch on notification access for Card holder in Android's settings. Card holder keeps nothing from a notification except a credit card bill, and from that only the amounts, the due date, the month and the few card digits the bank shows. You can switch it off at any time.
- **Nothing is sent anywhere.** The app uses the internet only to download the holiday list and to check this repository for a new version. No card, bill or personal data ever leaves the phone, and there are no accounts and no analytics.

## Roadmap

- **Keeping the holiday list current automatically:** a scheduled GitHub job that reads Bangladesh Bank's holiday page and updates `data/bank-calendar.json`.
- **How each card is paid:** pay-by dates that depend on the payment method (NPSB posts instantly even on weekends; bKash and Nagad can take 3 working days).
- **Google Play release:** with its own signing key, and without the GitHub updater, which Play doesn't allow.
- **Bill formats from more banks,** as bill message samples are collected.
- **Expiry reminders:** two months and one month before a card expires, for anyone who wants to cancel it rather than let it renew.
- **Annual fee reminders:** shortly before each card's yearly anniversary, so you can close the card or make sure the annual fee is waived.
- **App lock:** sign-in with a PIN or fingerprint.
- **Security:** encrypting the saved cards, and hiding the app's contents from screenshots and the recent apps screen.
- **Banks outside Bangladesh.**

## Building

You need Android Studio (or JDK 17+ and the Android SDK) with API level 37 installed.

```bash
./gradlew assembleDebug
```

To install the debug build on a connected phone or a running emulator:

```bash
./gradlew installDebug
```

To run the unit tests:

```bash
./gradlew testDebugUnitTest
```

The app runs on Android 7.0 (API 24) and up.

## Project layout

Built with Kotlin and Jetpack Compose (Material 3), in a single `app` module.

```
data/bank-calendar.json       Holidays and working Saturdays, downloaded by the app each week
app/src/main/java/com/ahmadabdullah/cardholder/
├── CardHolderApplication.kt  Loads the downloaded holiday list when the app starts
├── MainActivity.kt           Switches between the wallet, card, add-card and reminder setup screens
├── data/
│   ├── BankCatalog.kt        Banks, the networks each issues, and the tiers on each network
│   ├── Card.kt               A saved card, and its JSON form
│   ├── CardStore.kt          Reads and writes cards.json in private storage
│   └── bill/                 Reading bills from messages, bank holidays, pay-by dates, reminder days
├── reminders/                The notification reader, the daily reminder check, and the notifications
├── sync/                     The weekly holiday download, and updating the app from GitHub Releases
└── ui/
    ├── WalletViewModel.kt    Loads cards and bills, saves changes, and offers app updates
    ├── CreditCardView.kt     Draws a card: its design, chip, number and network mark
    ├── cardart/              Each bank's card designs, and the finishes they're drawn with
    ├── Format.kt             BDT grouping, masked numbers, billing and expiry dates
    ├── wallet/               The stacked wallet
    ├── detail/               A card's details and latest bill, and removing it
    ├── setup/                Explaining bill reading and turning it on
    └── addcard/              The add and edit form, and CardDraft, which checks what was entered
app/src/debug/                A gallery of every card design, in debug builds only
```

## Card designs

Every bank, network and tier in the catalog has its own design in [`CardDesigns.kt`](app/src/main/java/com/ahmadabdullah/cardholder/ui/cardart/CardDesigns.kt): its colors, a finish such as brushed metal, engraved lines, silk or a planet's glowing edge, and dark writing for light cards. Each was matched by eye and by measured color to the card pictures on the bank's own website in October 2026, then drawn from scratch. **No bank artwork or logo is copied**, which keeps the app safe to publish.

To see every design side by side, install a debug build and open the gallery (add `--es bank city` to show one bank):

```bash
adb shell am start -n com.ahmadabdullah.cardholder/.DesignGalleryActivity
```

The gallery fills every card with the same sample name and digits; in the app each card shows what its owner entered.

## Updating holidays

Edit [`data/bank-calendar.json`](data/bank-calendar.json) on GitHub: add the date to `holidays` when banks will be closed, or to `workingDays` when a Friday or Saturday is made a working day. Every installed app picks it up within a week; nothing needs to be released. The built-in list in `BankCalendar.kt` is only a fallback for phones that haven't downloaded the file yet, so it needs updating only when you release a new version anyway.

## Releasing an update to testers

Testers who installed the APK get an "Update available" banner in the app as soon as a newer GitHub release exists.

1. Raise `versionName` (and `versionCode`) in `app/build.gradle.kts`, for example to 1.1 and 2.
2. Build the APK with `./gradlew assembleDebug`.
3. Publish a GitHub release whose tag is the version with a `v` in front, with the APK attached:

   ```bash
   gh release create v1.1 app/build/outputs/apk/debug/app-debug.apk --title "Card holder 1.1" --notes "What changed"
   ```

The notes are shown in the banner. Android asks testers to confirm the install, and only accepts an APK signed with the same key as the version they have. Until the app has a release signing key, every APK must be built on the same computer.

## Updating the bank list

Every bank, network and tier comes from [`BankCatalog.kt`](app/src/main/java/com/ahmadabdullah/cardholder/data/BankCatalog.kt). Each bank has a comment naming the page its cards were taken from.

To add or change a bank:

1. Find its current credit cards on the bank's own website.
2. Add or edit its entry. Keep the list in alphabetical order.
3. Give each new card a design in `CardDesigns.kt`, based on the bank's own card pictures. Check it in the design gallery.
4. Run `./gradlew testDebugUnitTest`. The tests check that every bank lists at least one tier for each network it offers, and that every card has a design of its own.

## License

Card holder's source code is public, and you're free to use, study, change and share it for any **noncommercial** purpose: personal use, learning, research, hobby projects, or use by charities, schools and government bodies.

**Commercial use is not allowed.** That includes selling the app, publishing it in an app store for money or ads, or building it into a paid product. For any of that, ask the author first.

The full terms are in [LICENSE.md](LICENSE.md) ([PolyForm Noncommercial 1.0.0](https://polyformproject.org/licenses/noncommercial/1.0.0)).
