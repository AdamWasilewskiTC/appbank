package com.example.bankapp

class PaymentModel (
    val uuid: String,
    val amount: Float,
    val destination: String
) {
    override fun toString(): String {
        return "ID transakcji: $uuid\n" +
               "Wysokość: $amount PLN\n" +
               "Do: $destination"
    }
}