package com.mentisimo.transactions.infrastructure.outbound.adapter

import com.google.common.collect.ListMultimap
import com.google.common.collect.Multimaps.newListMultimap
import org.springframework.stereotype.Component
import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.domain.model.TransactionEvent
import com.mentisimo.transactions.infrastructure.outbound.port.TransactionEventStore
import java.util.*
import java.util.Collections.synchronizedList
import java.util.Collections.unmodifiableList
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryTransactionEventStore : TransactionEventStore {
    private val allTransactionEvents: ListMultimap<CustomerId, TransactionEvent> =
            newListMultimap(ConcurrentHashMap()) { synchronizedList(ArrayList()) }

    override fun save(transactionEvent: TransactionEvent) {
        allTransactionEvents.put(transactionEvent.customer.id, transactionEvent)
    }

    override fun saveAll(transactionEvents: List<TransactionEvent>) {
        transactionEvents.forEach {
            allTransactionEvents.put(it.customer.id, it)
        }
    }

    override fun findAllByCustomerId(customerId: CustomerId): List<TransactionEvent> {
        return unmodifiableList(allTransactionEvents.get(customerId))
    }
}