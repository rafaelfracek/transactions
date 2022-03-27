package com.mentisimo.transactions.domain.model

import java.math.BigDecimal

data class AccountSummaryReadModel(
        val customerId: Long,
        val firstName: String,
        val lastName: String,
        val numberOfTransactions: Int,
        val totalValueOfTransactions: BigDecimal,
        val transactionsFeeValue: BigDecimal,
        val lastTransactionDate: String
)