package com.mentisimo.transactions.domain.model.command

import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.domain.model.TransactionId
import java.math.BigDecimal
import java.time.LocalDateTime

data class AddTransactionEventCommand(
        val customerId: CustomerId, val customerFirstName: String, val customerLastName: String,
        val transactionId: TransactionId, val transactionAmount: BigDecimal, val transactionDate: LocalDateTime
)