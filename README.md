# Card holder

A wallet for your credit cards, in the spirit of Google Wallet. It keeps each card's bank, network, tier, limits and billing date in one place. It never keeps the full card number.

## What it does

- **Wallet:** your cards, stacked. Tap one to see its BDT and USD limits and the next billing date.
- **Add card:** choose the bank, then the network, then the tier. Each dropdown lists only what the bank you chose actually issues, so you can't pick a combination that doesn't exist.
- **Only the first and last 4 digits** of the card number are stored. The first 4 are checked against the network (Visa starts with 4, Mastercard with 51–55 or 2221–2720, Amex with 34 or 37).
- Cards are stored in the app's private storage and left out of cloud backups.

The first release covers banks in Bangladesh. Their cards are listed in [`BankCatalog.kt`](app/src/main/java/com/ahmadabdullah/cardholder/data/BankCatalog.kt).

## Build

```bash
./gradlew assembleDebug
```

Run the unit tests with `./gradlew testDebugUnitTest`.
