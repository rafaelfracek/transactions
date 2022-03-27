package com.mentisimo.transactions.infrastructure.outbound.port

import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.domain.model.TransactionEvent

interface TransactionEventStore {
    fun save(transactionEvent: TransactionEvent)
    fun saveAll(transactionEvents: List<TransactionEvent>)
    fun findAllByCustomerId(customerId: CustomerId): List<TransactionEvent>
}