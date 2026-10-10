package com.ahmadabdullah.cardholder.data

import org.json.JSONObject

/** A saved credit card. Only the first and last four digits of the number are ever kept. */
data class Card(
    val id: String,
    val bankId: String,
    val network: CardNetwork,
    val tier: String,
    val firstName: String,
    val lastName: String,
    val firstFour: String,
    val lastFour: String,
    val bdtLimit: Long,
    val usdLimit: Long,
    val billingDay: Int,
) {
    val bankName: String get() = BankCatalog.bank(bankId)?.name ?: bankId
    val holderName: String get() = "$firstName $lastName"

    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("bankId", bankId)
        .put("network", network.name)
        .put("tier", tier)
        .put("firstName", firstName)
        .put("lastName", lastName)
        .put("firstFour", firstFour)
        .put("lastFour", lastFour)
        .put("bdtLimit", bdtLimit)
        .put("usdLimit", usdLimit)
        .put("billingDay", billingDay)

    companion object {
        fun fromJson(json: JSONObject) = Card(
            id = json.getString("id"),
            bankId = json.getString("bankId"),
            network = CardNetwork.valueOf(json.getString("network")),
            tier = json.getString("tier"),
            firstName = json.getString("firstName"),
            lastName = json.getString("lastName"),
            firstFour = json.getString("firstFour"),
            lastFour = json.getString("lastFour"),
            bdtLimit = json.getLong("bdtLimit"),
            usdLimit = json.getLong("usdLimit"),
            billingDay = json.getInt("billingDay"),
        )
    }
}
