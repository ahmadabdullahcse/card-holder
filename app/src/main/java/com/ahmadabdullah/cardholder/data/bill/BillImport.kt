package com.ahmadabdullah.cardholder.data.bill

import android.content.Context
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardStore

/** What became of a bill message once it was read: saved to a card, or kept until its card is added. */
sealed interface BillImport {
    /** @property isNew false if this bill was already saved. */
    data class Saved(val card: Card, val bill: Bill, val isNew: Boolean) : BillImport

    /** @property isNew false if this bill was already being kept. */
    data class Unmatched(val bill: UnmatchedBill, val isNew: Boolean) : BillImport

    companion object {
        /**
         * Saves [message] to the card it's for, or keeps it for a card that isn't saved yet.
         * [sender] and [text] are where it came from, to tell which bank sent it.
         */
        suspend fun save(context: Context, sender: String, text: String, message: BillMessage): BillImport {
            val cardStore = CardStore.get(context)
            cardStore.load()
            val card = BillMatcher.cardFor(message, cardStore.cards.value.orEmpty(), "$sender $text")
            if (card == null) {
                val unmatched = UnmatchedBill(message, BillMatcher.bankNamedIn("$sender $text")?.id)
                return Unmatched(unmatched, BillStore.get(context).saveUnmatched(unmatched))
            }
            val bill = Bill.from(message, card.id)
            return Saved(card, bill, BillStore.get(context).save(bill))
        }
    }
}
