package com.mentisimo.transactions.infrastructure.inbound.adapter.csv.internal

import de.siegmar.fastcsv.reader.NamedCsvRow
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import com.mentisimo.transactions.application.service.TransactionEventAppService
import com.mentisimo.transactions.common.DateTimeConverter
import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.domain.model.TransactionId
import com.mentisimo.transactions.domain.model.command.AddTransactionEventCommand
import java.util.concurrent.CountDownLatch
import java.util.stream.Collectors

@Component
class AsyncCsvRowHandler(private val transactionEventAppService: TransactionEventAppService,
                         private val dateTimeConverter: DateTimeConverter) {

    companion object {
        const val CUSTOMER_ID_FIELD = "customer_id"
        const val CUSTOMER_FIRST_NAME_FIELD = "customer_first_name"
        const val CUSTOMER_LAST_NAME_FIELD = "customer_last_name"
        const val TRANSACTION_ID_FIELD = "transaction_id"
        const val TRANSACTION_AMOUNT_FIELD = "transaction_amount"
        const val TRANSACTION_DATE_FIELD = "transaction_date"
        const val COMMA = ","
        const val DOT = "."
    }

    @Async
    fun mapAndHandleEvents(customerRows: List<NamedCsvRow>, doneSignal: CountDownLatch) {
        try {
            transactionEventAppService.handleEvents(mapToCommands(customerRows))
        } finally {
            doneSignal.countDown();
        }
    }

    private fun mapRowToTransactionEvent(row: NamedCsvRow): AddTransactionEventCommand {
        return AddTransactionEventCommand(
                customerId = CustomerId(row.getField(CUSTOMER_ID_FIELD).toLong()),
                customerFirstName = row.getField(CUSTOMER_FIRST_NAME_FIELD),
                customerLastName = row.getField(CUSTOMER_LAST_NAME_FIELD),
                transactionId = TransactionId(row.getField(TRANSACTION_ID_FIELD).toLong()),
                transactionAmount = row.getField(TRANSACTION_AMOUNT_FIELD).replace(COMMA, DOT).toBigDecimal(),
                transactionDate = dateTimeConverter.fromString(row.getField(TRANSACTION_DATE_FIELD))
        )
    }

    private fun mapToCommands(customerRows: List<NamedCsvRow>): List<AddTransactionEventCommand> {
        return customerRows.stream()
                .map(this::mapRowToTransactionEvent)
                .sorted(Comparator.comparing(AddTransactionEventCommand::transactionDate))
                .collect(Collectors.toList())
    }
}