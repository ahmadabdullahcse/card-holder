package com.ahmadabdullah.cardholder.data.bill

import java.time.DateTimeException
import java.time.LocalDate
import java.time.YearMonth

/**
 * Reads a credit card bill out of a bank's SMS or notification text.
 *
 * Banks word their bills differently, so rather than one pattern per bank this looks for the
 * parts they share: a masked card number, "Total Due", "Min Due" and a due date. For example:
 *
 * - UCB: `Bill for UCB Card#4417 … for SEP-26. Total Due is BDT 18450.50 Dr & USD 42.00 Dr
 *   Min Due is BDT 553.50 & USD 42.00 Due date 13-10-26`
 * - City: `City Islamic Amex Bill Sep'26 Total Due Tk 12345.60 Min Due Tk 370.37
 *   CARD: 371***905 … Pay by 08-10-26`
 * - EBL: `Monthly bill 455120****6630 SEP2026; Total Due: BDT 9876.54, Min Due: BDT 2100.00,
 *   Last Pmt: 10-OCT-26.`
 */
object BillParser {
    private val ignoreCase = setOf(RegexOption.IGNORE_CASE)

    private val totalDueLabel = Regex("""total\s*due""", ignoreCase)
    private val minDueLabel = Regex("""min(?:imum)?\.?\s*due""", ignoreCase)

    private const val AMOUNT = """([\d,]+(?:\.\d+)?)\s*(dr|cr)?\b"""
    private val bdtAmount = Regex("""(?:BDT|Tk\.?|৳)\s*:?\s*$AMOUNT""", ignoreCase)
    private val usdAmount = Regex("""(?:USD|US\$|\$)\s*:?\s*$AMOUNT""", ignoreCase)

    private val dueDate = Regex(
        """(?:due\s*date|pay\s*by|last\s*pmt|last\s*payment\s*date|payment\s*due|due\s*on)""" +
            """\s*(?:is|:)?\s*(\d{1,2})[-/. ]([a-z]{3}|\d{1,2})[-/. ](\d{4}|\d{2})\b""",
        ignoreCase,
    )

    /** "371***905", "455120****6630" */
    private val maskedNumber = Regex("""\b(\d{2,6})[*xX•]{2,}(\d{3,4})\b""")

    /** "Card#4417", "card ending 4417", "Card No: 4417" */
    private val lastDigits =
        Regex("""card\s*(?:no\.?|number)?\s*[#:]?\s*(?:ending\s*(?:in|with)?\s*)?(\d{4})\b""", ignoreCase)

    /** "SEP-26", "Sep'26", "SEP2026" */
    private val month = Regex("""\b(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*[-' ]?(\d{4}|\d{2})\b""", ignoreCase)

    private val monthNames = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")

    /** The bill in [text], or null when the text isn't a credit card bill this can read. */
    fun parse(text: String): BillMessage? {
        val message = text.replace(Regex("""\s+"""), " ").trim()
        val totalStart = totalDueLabel.find(message) ?: return null
        val minStart = minDueLabel.find(message, totalStart.range.last + 1)
        val date = dueDate.find(message) ?: return null

        // Each amount is read from the stretch of text after its label, up to the next label.
        fun textAfter(label: MatchResult, vararg next: MatchResult?): String {
            val start = label.range.last + 1
            val end = next.filterNotNull().map { it.range.first }.filter { it >= start }.minOrNull() ?: message.length
            return message.substring(start, end)
        }
        val totalText = textAfter(totalStart, minStart, date)
        val minText = minStart?.let { textAfter(it, date) }

        val total = due(totalText) ?: return null
        return BillMessage(
            card = cardDigits(message) ?: return null,
            statementMonth = month.find(message.substring(0, totalStart.range.first))?.let(::yearMonth),
            totalDue = total,
            minDue = minText?.let(::due) ?: Due(0, 0),
            dueDate = localDate(date) ?: return null,
        )
    }

    /** The BDT and USD amounts in one part of the message, or null if it has neither. */
    private fun due(text: String): Due? {
        val bdt = bdtAmount.find(text)
        val usd = usdAmount.find(text)
        if (bdt == null && usd == null) return null
        return Due(bdt?.let(::hundredths) ?: 0, usd?.let(::hundredths) ?: 0)
    }

    /** The amount owed in hundredths. A credit balance ("Cr") means nothing is owed. */
    private fun hundredths(match: MatchResult): Long {
        if (match.groupValues[2].equals("cr", ignoreCase = true)) return 0
        val amount = match.groupValues[1].replace(",", "").toBigDecimal()
        return amount.movePointRight(2).setScale(0, java.math.RoundingMode.HALF_UP).toLong()
    }

    private fun cardDigits(message: String): CardDigits? {
        maskedNumber.find(message)?.let { return CardDigits(it.groupValues[1], it.groupValues[2]) }
        lastDigits.find(message)?.let { return CardDigits("", it.groupValues[1]) }
        return null
    }

    private fun localDate(match: MatchResult): LocalDate? {
        val (day, monthText, yearText) = match.destructured
        val month = monthText.toIntOrNull() ?: (monthNames.indexOf(monthText.lowercase()) + 1)
        val year = yearText.toInt().let { if (it < 100) 2000 + it else it }
        return try {
            LocalDate.of(year, month, day.toInt())
        } catch (_: DateTimeException) {
            null
        }
    }

    private fun yearMonth(match: MatchResult): YearMonth {
        val (monthText, yearText) = match.destructured
        val year = yearText.toInt().let { if (it < 100) 2000 + it else it }
        return YearMonth.of(year, monthNames.indexOf(monthText.lowercase()) + 1)
    }
}
