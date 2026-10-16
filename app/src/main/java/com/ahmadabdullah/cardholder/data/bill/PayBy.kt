package com.ahmadabdullah.cardholder.data.bill

import java.time.LocalDate

/**
 * The last safe day to pay each part of a bill without a late fee.
 *
 * - The BDT due is safe until the last day banks are open on or before the due date. A due
 *   date on a Friday, Saturday or holiday moves earlier, never later.
 * - The USD due is paid in taka, which the bank then converts to dollars. That takes it a few
 *   banking days, so the payment has to be made that many open days before the BDT deadline.
 */
data class PayBy(
    val bdt: LocalDate?,
    val usd: LocalDate?,
) {
    /** The earliest of the two deadlines: pay the whole bill by this day to be safe. */
    val earliest: LocalDate? get() = listOfNotNull(bdt, usd).minOrNull()

    companion object {
        /**
         * Working days a bank takes to turn a taka payment into dollars. UCB takes two; other
         * banks are assumed to take two as well until they're known, since guessing too few
         * would make a reminder arrive too late.
         */
        const val DEFAULT_USD_CONVERSION_DAYS = 2

        fun of(bill: BillMessage, usdConversionDays: Int = DEFAULT_USD_CONVERSION_DAYS): PayBy {
            val lastOpenDay = BankCalendar.lastOpenDayOnOrBefore(bill.dueDate)
            return PayBy(
                bdt = lastOpenDay.takeIf { bill.totalDue.bdt > 0 },
                usd = BankCalendar.openDaysBefore(lastOpenDay, usdConversionDays).takeIf { bill.totalDue.usd > 0 },
            )
        }
    }
}
