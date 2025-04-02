package com.example.mydeal.model.data

import java.util.UUID

class Transaction {
    var id: String = UUID.randomUUID().toString()
    var amount: Double = 0.0
    var description: String = ""
    var category: String = ""
    var date: String = ""
    var time: String = ""
    var isExpense: Boolean = true
    var hasReceipt: Boolean = false
    var location: String = ""
    var isRecurring: Boolean = false
    var recurringPeriod: RecurringPeriod = RecurringPeriod.NONE
    var recurringEndDate: String? = null

    enum class RecurringPeriod {
        NONE,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }
}