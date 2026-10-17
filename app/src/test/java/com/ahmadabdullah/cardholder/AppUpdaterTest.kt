package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.sync.AppUpdater
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdaterTest {
    @Test
    fun versionsCompareNumberByNumber() {
        assertTrue(AppUpdater.isNewer("1.1", "1.0"))
        assertTrue(AppUpdater.isNewer("1.10", "1.9"))
        assertTrue(AppUpdater.isNewer("2.0", "1.12.3"))
        assertTrue(AppUpdater.isNewer("1.0.1", "1.0"))
        assertFalse(AppUpdater.isNewer("1.0", "1.0"))
        assertFalse(AppUpdater.isNewer("1.0", "1.0.0"))
        assertFalse(AppUpdater.isNewer("0.9", "1.0"))
    }
}
