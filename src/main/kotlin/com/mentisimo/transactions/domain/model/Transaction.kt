package com.mentisimo.transactions.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Transaction(val id: TransactionId, val amount: BigDecimal, val date: LocalDateTime)