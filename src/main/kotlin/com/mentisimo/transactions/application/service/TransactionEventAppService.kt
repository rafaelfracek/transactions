package com.mentisimo.transactions.application.service

import org.springframework.stereotype.Service
import com.mentisimo.transactions.domain.model.command.AddTransactionEventCommand
import com.mentisimo.transactions.domain.service.TransactionEventService
import com.mentisimo.transactions.infrastructure.outbound.port.AccountSummaryReadModelRepository
import com.mentisimo.transactions.infrastructure.outbound.port.TransactionEventStore
import java.util.stream.Collectors.toList

@Service
class TransactionEventAppService(private val transactionEventStore: TransactionEventStore,
                                 private val transactionEventService: TransactionEventService,
                                 private val accountSummaryReadModelRepository: AccountSummaryReadModelRepository) {

    fun handleEvents(commands: List<AddTransactionEventCommand>) {
        saveSortedTransactionsToEventStore(commands)
        updateReadModels(commands)
    }

    private fun saveSortedTransactionsToEventStore(commands: List<AddTransactionEventCommand>) {
        transactionEventStore.saveAll(commands.stream()
                .map(transactionEventService::mapToTransactionEvent)
                .sorted(Comparator.comparing { it.transaction.date })
                .collect(toList())
        )
    }

    private fun updateReadModels(commands: List<AddTransactionEventCommand>) {
        commands.stream()
                .map(AddTransactionEventCommand::customerId)
                .distinct()
                .forEach {
                    accountSummaryReadModelRepository.saveOrUpdate(transactionEventService.retrieveReadModel(it)!!)
                }
    }
}