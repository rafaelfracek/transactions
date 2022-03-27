package com.mentisimo.transactions.infrastructure.inbound.adapter.csv.internal

import org.springframework.stereotype.Component
import com.mentisimo.transactions.infrastructure.inbound.port.TransactionEventReader
import javax.annotation.PostConstruct

@Component
class CsvFileTransactionEventReaderInitializer(private val transactionEventReader: TransactionEventReader) {

    @PostConstruct
    fun initialize() {
        transactionEventReader.init();
    }
}