# Card holder

A wallet for your credit cards, in the spirit of Google Wallet. It keeps each card's limits, billing date and expiry date in one place, and reminds you to pay each bill before it's due. It never holds the full card number.

Made for credit cards issued in **Bangladesh**.

<p align="center">
  <img src="docs/screenshots/wallet.png" width="260" alt="The wallet, with cards stacked">
  &nbsp;
  <img src="docs/screenshots/add-card.png" width="260" alt="Adding a card, with a live preview above the form">
  &nbsp;
  <img src="docs/screenshots/card-details.png" width="260" alt="A card with its latest bill and the BDT and USD pay-by dates">
</p>

## Features

- **Your cards in one place**, stacked like a wallet and drawn to look like each bank's own card.
- **21 Bangladeshi banks** on Visa, Mastercard, American Express, Diners Club, UnionPay and JCB. The form only offers the networks and tiers each bank actually issues.
- **Bill reminders.** Card holder reads the bill your bank sends by SMS, and reminds you 7, 5, 3, 2 and 1 days before the last safe day to pay, then once more on the day.
- **Pay-by dates that know the banking calendar.** They move before Fridays, Saturdays and bank holidays, and a USD due gets an earlier date so the bank has time to convert BDT.
- **Edit, remove and reorder** your cards at any time.

## Privacy

- Only the **first and last 4 digits** of a card are kept. Card holder never asks for the full number, the CVV or the PIN.
- **Everything stays on your phone.** Cards and bills are left out of cloud backups, and no card or bill data is ever sent anywhere.
- **Reading bills is your choice.** It only works after you turn on notification access, and Card holder keeps nothing but credit card bills from what it reads.
- The internet is used only to download the bank holiday list and to check for app updates.

## Installing

Card holder is in testing and isn't on Google Play yet. It runs on Android 7.0 and up.

## License

Free to use, study, change and share for any **noncommercial** purpose. Selling it or using it in a paid product isn't allowed without asking the author. The full terms are in [LICENSE.md](LICENSE.md) ([PolyForm Noncommercial 1.0.0](https://polyformproject.org/licenses/noncommercial/1.0.0)).
