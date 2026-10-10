package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.ui.formatBdt
import com.ahmadabdullah.cardholder.ui.formatShortDate
import com.ahmadabdullah.cardholder.ui.nextBillingDate
import com.ahmadabdullah.cardholder.ui.ordinal
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class FormatTest {
    private fun date(year: Int, month: Int, day: Int) = Calendar.getInstance().apply {
        clear()
        set(year, month - 1, day)
    }

    @Test
    fun bdtIsGroupedInLakhs() {
        assertEquals("৳500", formatBdt(500))
        assertEquals("৳5,000", formatBdt(5_000))
        assertEquals("৳5,00,000", formatBdt(500_000))
        assertEquals("৳1,25,00,000", formatBdt(12_500_000))
    }

    @Test
    fun ordinals() {
        assertEquals(listOf("1st", "2nd", "3rd", "4th", "11th", "12th", "13th", "21st", "22nd", "31st"),
            listOf(1, 2, 3, 4, 11, 12, 13, 21, 22, 31).map(::ordinal))
    }

    @Test
    fun nextBillingDateIsTodayOrLater() {
        assertEquals("15 Oct 2026", formatShortDate(nextBillingDate(15, date(2026, 10, 11))))
        assertEquals("11 Oct 2026", formatShortDate(nextBillingDate(11, date(2026, 10, 11))))
        assertEquals("5 Nov 2026", formatShortDate(nextBillingDate(5, date(2026, 10, 11))))
    }

    @Test
    fun billingDayPastMonthEndFallsOnTheLastDay() {
        assertEquals("28 Feb 2027", formatShortDate(nextBillingDate(31, date(2027, 2, 1))))
        assertEquals("31 Jan 2027", formatShortDate(nextBillingDate(31, date(2027, 1, 31))))
        assertEquals("28 Feb 2027", formatShortDate(nextBillingDate(30, date(2027, 1, 31))))
    }
}
