package com.ahmadabdullah.cardholder.data.bill

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * The days banks in Bangladesh are closed: every Friday and Saturday, plus the holidays
 * Bangladesh Bank announces each year, except for the odd Friday or Saturday the government
 * makes a working day in exchange for a holiday.
 *
 * The lists are built in; Card holder never goes online. Holidays announced later arrive with an
 * app update.
 */
object BankCalendar {
    /**
     * From Bangladesh Bank's list for 2026 (bb.org.bd/en/index.php/mediaroom/holiday), with
     * Eid-ul-Azha starting on 25 May as its circular of 18 May 2026 changed it. Holidays only
     * for the hill districts are left out, and holidays falling on a Friday or Saturday are
     * covered by the weekend anyway.
     *
     * Bangladesh Bank hasn't published 2027 yet; it usually does in November. Until then 2027
     * has only the holidays fixed by date. Shab-e-Barat, the Eids, Buddha Purnima, Ashura,
     * Eid-e-Milad-un-Nabi, Janmashtami and Durga Puja follow the moon or the Bengali and Hindu
     * calendars, and must be added once the list is out.
     */
    private val holidays: Set<LocalDate> = listOf(
        "2026-02-04", // Shab-e-Barat
        "2026-02-21", // Shahid Day and International Mother Language Day
        "2026-03-17", // Shab-e-Qadr
        "2026-03-19", "2026-03-20", "2026-03-21", "2026-03-22", "2026-03-23", // Eid-ul-Fitr
        "2026-03-26", // Independence and National Day
        "2026-04-14", // Bengali New Year
        "2026-05-01", // May Day and Buddha Purnima
        "2026-05-25", "2026-05-26", "2026-05-27", "2026-05-28", "2026-05-29", "2026-05-30", "2026-05-31", // Eid-ul-Azha
        "2026-06-26", // Ashura
        "2026-07-01", // Bank Holiday
        "2026-08-05", // July Mass Uprising Day
        "2026-08-26", // Eid-e-Milad-un-Nabi
        "2026-09-04", // Janmashtami
        "2026-10-20", "2026-10-21", // Durga Puja
        "2026-10-22", // Durga Puja, in exchange for Saturday 17 October being a working day
        "2026-12-16", // Victory Day
        "2026-12-25", // Christmas Day
        "2026-12-31", // Bank Holiday

        "2027-02-21", // Shahid Day and International Mother Language Day
        "2027-03-26", // Independence and National Day
        "2027-04-14", // Bengali New Year
        "2027-05-01", // May Day
        "2027-07-01", // Bank Holiday
        "2027-08-05", // July Mass Uprising Day
        "2027-12-16", // Victory Day
        "2027-12-25", // Christmas Day
        "2027-12-31", // Bank Holiday
    ).map(LocalDate::parse).toSet()

    /** Fridays and Saturdays declared working days, usually in exchange for a holiday. */
    private val workingDays: Set<LocalDate> = listOf(
        "2026-10-17", // In exchange for Thursday 22 October, for Durga Puja
    ).map(LocalDate::parse).toSet()

    fun isOpen(date: LocalDate): Boolean = when {
        date in workingDays -> true
        date.dayOfWeek == DayOfWeek.FRIDAY || date.dayOfWeek == DayOfWeek.SATURDAY -> false
        else -> date !in holidays
    }

    /** [date] if banks are open that day, otherwise the last day before it that they are. */
    fun lastOpenDayOnOrBefore(date: LocalDate): LocalDate {
        var day = date
        while (!isOpen(day)) day = day.minusDays(1)
        return day
    }

    /** The day that is [count] open banking days before [date]. */
    fun openDaysBefore(date: LocalDate, count: Int): LocalDate {
        var day = date
        repeat(count) { day = lastOpenDayOnOrBefore(day.minusDays(1)) }
        return day
    }
}
