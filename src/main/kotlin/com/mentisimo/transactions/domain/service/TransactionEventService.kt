package com.mentisimo.transactions.domain.service

import org.springframework.stereotype.Service
import com.mentisimo.transactions.common.DateTimeConverter
import com.mentisimo.transactions.domain.model.*
import com.mentisimo.transactions.domain.model.command.AddTransactionEventCommand
import com.mentisimo.transactions.infrastructure.outbound.port.TransactionEventStore
import java.math.BigDecimal

@Service
class TransactionEventService(private val dateTimeConverter: DateTimeConverter,
                              private val transactionEventStore: TransactionEventStore,
                              private val transactionsFeeValueCalculationService: TransactionsFeeValueCalculationService) {
    fun retrieveReadModel(customerId: CustomerId): AccountSummaryReadModel? {
        val transactionEvents = transactionEventStore.findAllByCustomerId(customerId)
        if (transactionEvents.isNotEmpty()) {
            val totalValueOfTransactions = transactionEvents.stream()
                    .map { transactionEvent: TransactionEvent -> transactionEvent.transaction.amount }
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            val lastTransactionEvent = transactionEvents[transactionEvents.size - 1]
            return AccountSummaryReadModel(
                    customerId = customerId.value,
                    firstName = lastTransactionEvent.customer.firstName,
                    lastName = lastTransactionEvent.customer.lastName,
                    lastTransactionDate = dateTimeConverter.toString(lastTransactionEvent.transaction.date),
                    numberOfTransactions = transactionEvents.size,
                    transactionsFeeValue = transactionsFeeValueCalculationService.calculateTransactionsFeeValue(totalValueOfTransactions),
                    totalValueOfTransactions = totalValueOfTransactions
            )
        } else {
            return null
        }
    }

    fun mapToTransactionEvent(command: AddTransactionEventCommand): TransactionEvent {
        return TransactionEvent(
                customer = Customer(
                        id = command.customerId,
                        firstName = command.customerFirstName,
                        lastName = command.customerLastName
                ),
                transaction = Transaction(
                        id = command.transactionId,
                        amount = command.transactionAmount,
                        date = command.transactionDate
                )
        )
    }
}