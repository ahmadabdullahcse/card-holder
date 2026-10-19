package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.data.JsonListFile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class JsonListFileTest {
    @get:Rule val folder = TemporaryFolder()

    private fun card(id: String) = Card(id, "ucb", CardNetwork.VISA, "Gold", "Rahim", "Uddin", "4123", "4417", 200000, 0, 20, 6, 2029)

    private fun store(file: File) = JsonListFile(file, Card::fromJson, Card::toJson)

    @Test
    fun savedCardsReadBack() {
        val file = File(folder.root, "cards.json")
        store(file).write(listOf(card("a"), card("b")))
        assertEquals(listOf(card("a"), card("b")), store(file).read())
    }

    @Test
    fun aDamagedFileFallsBackToTheLastGoodList() {
        val file = File(folder.root, "cards.json")
        store(file).write(listOf(card("a")))
        store(file).write(listOf(card("a"), card("b")))
        // The phone dies halfway through the next save.
        file.writeText("""[{"id":"a","bankId":"ucb""")
        assertEquals(listOf(card("a")), store(file).read())
        assertTrue(File(folder.root, "cards.json.damaged").exists())
    }

    @Test
    fun noFileMeansNoCards() {
        assertEquals(emptyList<Card>(), store(File(folder.root, "cards.json")).read())
    }

    @Test
    fun aDamagedFileIsNeverBackedUp() {
        val file = File(folder.root, "cards.json")
        store(file).write(listOf(card("a")))
        store(file).write(listOf(card("a"), card("b")))
        file.writeText("not json")
        // Saving again keeps the good backup rather than replacing it with the damaged file.
        store(file).write(listOf(card("c")))
        file.writeText("not json")
        assertEquals(listOf(card("a")), store(file).read())
    }
}
