package com.example.bankapp

class PaymentModel (
    val uuid: String,
    val amount: Float,
    val destination: String
) {
    override fun toString(): String {
        return "Payment ID: $uuid\n" +
               "Amount: $amount PLN\n" +
               "Destination: $destination"
    }
}