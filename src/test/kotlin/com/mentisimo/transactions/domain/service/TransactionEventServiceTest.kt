package com.mentisimo.transactions.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import com.mentisimo.transactions.common.DateTimeConverter
import com.mentisimo.transactions.domain.model.*
import com.mentisimo.transactions.infrastructure.outbound.port.TransactionEventStore
import java.time.LocalDateTime

fun <T> eq(obj: T): T = Mockito.eq(obj)

@ExtendWith(MockitoExtension::class)
internal class TransactionEventServiceTest {

    companion object {
        val EXISTING_CUSTOMER_ID = CustomerId(1)
        val NON_EXISTING_CUSTOMER_ID = CustomerId(2)
        const val FIRST_NAME = "FIRST_NAME_1"
        const val LAST_NAME = "LAST_NAME_1"

        val TRANSACTION_ID_1 = TransactionId(1)
        val TRANSACTION_ID_2 = TransactionId(2)
        val TRANSACTION_AMOUNT_1 = "100.34".toBigDecimal()
        val TRANSACTION_AMOUNT_2 = "55.32".toBigDecimal()
        val TRANSACTION_DATE_1 = LocalDateTime.of(2021, 1, 1, 1, 1, 1)!!
        val TRANSACTION_DATE_2 = LocalDateTime.of(2021, 2, 2, 2, 2, 2)!!
        const val TRANSACTION_DATE_2_STR = "02.02.2021 02:02:02"

        const val NUMBER_OF_TRANSACTIONS = 2
        val TRANSACTIONS_FEE_VALUE = "2.22".toBigDecimal()
        val TOTAL_VALUE_OF_TRANSACTIONS = TRANSACTION_AMOUNT_1.plus(TRANSACTION_AMOUNT_2)
    }

    @InjectMocks
    private lateinit var transactionEventService: TransactionEventService

    @Mock
    private lateinit var transactionEventStore: TransactionEventStore

    @Mock
    private lateinit var transactionsFeeValueCalculationService: TransactionsFeeValueCalculationService

    @Mock
    private lateinit var dateTimeConverter: DateTimeConverter

    @Test
    fun shouldRetrieveCorrectReadModel_whenTransactionEventsExist() {
        //given
        val givenTransactionEvents = createTransactionEvents(EXISTING_CUSTOMER_ID)
        `when`(transactionEventStore.findAllByCustomerId(eq(EXISTING_CUSTOMER_ID)))
                .thenReturn(givenTransactionEvents)
        `when`(dateTimeConverter.toString(eq(TRANSACTION_DATE_2))).thenReturn(TRANSACTION_DATE_2_STR)
        `when`(transactionsFeeValueCalculationService.calculateTransactionsFeeValue(eq(TOTAL_VALUE_OF_TRANSACTIONS))).thenReturn(TRANSACTIONS_FEE_VALUE)
        //when
        val transactionEvents = transactionEventService.retrieveReadModel(EXISTING_CUSTOMER_ID);
        //then
        assertThat(transactionEvents).isEqualTo(createReadModel(EXISTING_CUSTOMER_ID))
    }

    @Test
    fun shouldRetrieveNullReadModel_whenTransactionEventsNotExist() {
        //given
        `when`(transactionEventStore.findAllByCustomerId(eq(NON_EXISTING_CUSTOMER_ID)))
                .thenReturn(emptyList());
        //when
        val accountSummaryReadModel = transactionEventService.retrieveReadModel(NON_EXISTING_CUSTOMER_ID);
        //then
        assertThat(accountSummaryReadModel).isNull()
    }

    private fun createTransactionEvents(customerId: CustomerId): List<TransactionEvent> {
        return listOf(
                TransactionEvent(
                        customer = Customer(id = customerId, firstName = FIRST_NAME, lastName = LAST_NAME),
                        transaction = Transaction(id = TRANSACTION_ID_1, amount = TRANSACTION_AMOUNT_1, date = TRANSACTION_DATE_1)
                ),
                TransactionEvent(
                        customer = Customer(id = customerId, firstName = FIRST_NAME, lastName = LAST_NAME),
                        transaction = Transaction(id = TRANSACTION_ID_2, amount = TRANSACTION_AMOUNT_2, date = TRANSACTION_DATE_2)
                )
        )
    }

    private fun createReadModel(customerId: CustomerId): AccountSummaryReadModel {
        return AccountSummaryReadModel(
                customerId = customerId.value,
                firstName = FIRST_NAME,
                lastName = LAST_NAME,
                lastTransactionDate = TRANSACTION_DATE_2_STR,
                numberOfTransactions = NUMBER_OF_TRANSACTIONS,
                transactionsFeeValue = TRANSACTIONS_FEE_VALUE,
                totalValueOfTransactions = TOTAL_VALUE_OF_TRANSACTIONS,
        )
    }
}