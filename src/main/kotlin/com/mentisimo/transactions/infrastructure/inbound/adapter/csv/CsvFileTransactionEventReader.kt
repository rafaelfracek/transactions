package com.mentisimo.transactions.infrastructure.inbound.adapter.csv

import de.siegmar.fastcsv.reader.NamedCsvReader
import de.siegmar.fastcsv.reader.NamedCsvRow
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import com.mentisimo.transactions.infrastructure.inbound.adapter.csv.internal.AsyncCsvRowHandler
import com.mentisimo.transactions.infrastructure.inbound.port.TransactionEventReader
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS


@Component
class CsvFileTransactionEventReader(private val asyncCsvRowHandler: AsyncCsvRowHandler,
                                    @Value("\${TRANSACTIONS_FILE}") private val transactionsFile: File,
                                    private val taskScheduler: TaskScheduler) : TransactionEventReader {

    private val logger = LoggerFactory.getLogger(CsvFileTransactionEventReader::class.java)

    companion object {
        const val CUSTOMER_ID_FIELD = "customer_id"
        const val ONE_SECOND = 1L
        const val ROWS_BATCH_SIZE = 1000
    }

    @Async
    override fun init() {
        logger.info("{} initialization has been started...", this::class.simpleName)
        val start = System.currentTimeMillis()
        val doneSignals = loadCustomerRowsFromFile();
        taskScheduler.schedule(createDoneSignalsCheckTask(doneSignals, start), getCurrentTimePlusOneSecond())
    }

    private fun createDoneSignalsCheckTask(doneSignals: List<CountDownLatch>, start: Long): Runnable {
        return Runnable {
            if (doneSignals.stream().map { it.count }.reduce(0L, Long::plus) == 0L) {
                logger.info("{} has been initialized in {} seconds", this::class.simpleName,
                        MILLISECONDS.toSeconds(System.currentTimeMillis().minus(start)))
            } else {
                taskScheduler.schedule(createDoneSignalsCheckTask(doneSignals, start), getCurrentTimePlusOneSecond())
            }
        }
    }

    private fun loadCustomerRowsFromFile(): List<CountDownLatch> {
        val customerToRows: MutableMap<String, MutableList<NamedCsvRow>> = HashMap()
        val doneSignals = ArrayList<CountDownLatch>()
        var numberOfRows = 0;
        NamedCsvReader.builder().build(transactionsFile.toPath(), StandardCharsets.UTF_8).forEach { row ->
            addRowToCustomer(customerToRows, row)
            numberOfRows++
            if (numberOfRows % ROWS_BATCH_SIZE == 0) {
                handleRowsBatch(customerToRows, doneSignals)
            }
        }
        handleRowsBatch(customerToRows, doneSignals)
        return doneSignals
    }

    private fun handleRowsBatch(customerToRows: MutableMap<String, MutableList<NamedCsvRow>>, doneSignals: ArrayList<CountDownLatch>) {
        val doneSignal = CountDownLatch(customerToRows.size)
        doneSignals.add(doneSignal)
        customerToRows.values.forEach { asyncCsvRowHandler.mapAndHandleEvents(it, doneSignal) }
        customerToRows.clear()
    }

    private fun addRowToCustomer(customerToRows: MutableMap<String, MutableList<NamedCsvRow>>, row: NamedCsvRow) {
        customerToRows.computeIfAbsent(row.getField(CUSTOMER_ID_FIELD)) { ArrayList() }
        customerToRows[row.getField(CUSTOMER_ID_FIELD)]!!.add(row)
    }

    private fun getCurrentTimePlusOneSecond() = Instant.now().plusSeconds(ONE_SECOND)
}