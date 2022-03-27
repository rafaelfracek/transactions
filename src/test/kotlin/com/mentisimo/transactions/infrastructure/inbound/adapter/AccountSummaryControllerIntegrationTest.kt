package com.mentisimo.transactions.infrastructure.inbound.adapter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import com.mentisimo.transactions.IntegrationTest
import com.mentisimo.transactions.domain.model.AccountSummaryReadModel
import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.infrastructure.outbound.adapter.notification.internal.MongoSearchNotificationRepository

internal class AccountSummaryControllerIntegrationTest : IntegrationTest(TEST_USER, TEST_PASSWORD) {

    @Autowired
    private lateinit var mongoSearchNotificationRepository: MongoSearchNotificationRepository

    companion object {

        const val TEST_USER = "user1"
        const val TEST_PASSWORD = "user1"

        const val CUSTOMER_IDS_STR_1_AND_2 = "  ,,,, 1 ,,,   2,  "
        const val CUSTOMER_IDS_STR_2 = "2"
        const val CUSTOMER_IDS_STR_ALL = "ALL"
        const val CUSTOMER_IDS_STR_INCORRECT = "incorrect,request"

        val ACCOUNT_SUMMARY_1 = AccountSummaryReadModel(
                customerId = 1,
                firstName = "Jan",
                lastName = "Nowak",
                numberOfTransactions = 1,
                totalValueOfTransactions = "243.33".toBigDecimal(),
                transactionsFeeValue = "8.51655".toBigDecimal(),
                lastTransactionDate = "11.12.2020 14:57:21"
        )

        val ACCOUNT_SUMMARY_2 = AccountSummaryReadModel(
                customerId = 2,
                firstName = "Anna",
                lastName = "Nowak",
                numberOfTransactions = 2,
                totalValueOfTransactions = "15131.21".toBigDecimal(),
                transactionsFeeValue = "0".toBigDecimal(),
                lastTransactionDate = "29.12.2020 11:40:32"
        )
    }

    @Test
    @Order(1)
    fun shouldReturnExactlyTwoExpectedAccountSummaries_whenCorrectRequestForAllCustomers() {
        //when
        val result = restTemplate.getForEntity(withBaseUrl("/accountSummaries"), Array<AccountSummaryReadModel>::class.java)
        //then
        assertThat(result.body!!.toList()).containsExactly(ACCOUNT_SUMMARY_1, ACCOUNT_SUMMARY_2)
    }

    @Test
    @Order(2)
    fun shouldReturnExactlyTwoExpectedAccountSummaries_whenCorrectRequestForCustomerIds1And2() {
        //when
        val result = restTemplate.getForEntity(withBaseUrl("/accountSummaries?customerIds=$CUSTOMER_IDS_STR_1_AND_2"), Array<AccountSummaryReadModel>::class.java)
        //then
        assertThat(result.body!!.toList()).containsExactly(ACCOUNT_SUMMARY_1, ACCOUNT_SUMMARY_2)
    }

    @Test
    @Order(3)
    fun shouldReturnExactlyTwoExpectedAccountSummaries_whenCorrectRequestForCustomerIds2() {
        //when
        val result = restTemplate.getForEntity(withBaseUrl("/accountSummaries?customerIds=$CUSTOMER_IDS_STR_2"), Array<AccountSummaryReadModel>::class.java)
        //then
        assertThat(result.body!!.toList()).containsExactly(ACCOUNT_SUMMARY_2)
    }

    @Test
    @Order(4)
    fun shouldReturnExactlyTwoExpectedAccountSummaries_whenCorrectRequestForCustomerIdsAll() {
        //when
        val result = restTemplate.getForEntity(withBaseUrl("/accountSummaries?customerIds=$CUSTOMER_IDS_STR_ALL"), Array<AccountSummaryReadModel>::class.java)
        //then
        assertThat(result.body!!.toList()).containsExactly(ACCOUNT_SUMMARY_1, ACCOUNT_SUMMARY_2)
    }

    @Test
    @Order(5)
    fun shouldOccursInternalServerError_whenIncorrectRequest() {
        //when
        val result = restTemplate.getForEntity(withBaseUrl("/accountSummaries?customerIds=$CUSTOMER_IDS_STR_INCORRECT"), String::class.java)
        //then
        assertThat(result.statusCode).isEqualTo(INTERNAL_SERVER_ERROR)
    }

    @Test
    @Order(6)
    fun shouldAddSearchNotification_whenCorrectRequestCustomerIds1And2() {
        //given
        mongoSearchNotificationRepository.deleteAll()
        //when
        restTemplate.getForEntity(withBaseUrl("/accountSummaries?customerIds=$CUSTOMER_IDS_STR_1_AND_2"), Array<AccountSummaryReadModel>::class.java)
        //then
        val searchNotifications = mongoSearchNotificationRepository.findAllByUsername(TEST_USER)
        assertThat(searchNotifications).hasSize(1)
        assertThat(searchNotifications[0].searchAllCustomers).isFalse
        assertThat(searchNotifications[0].searchCustomerIds).containsExactlyInAnyOrder(CustomerId(1), CustomerId(2))
        assertThat(searchNotifications[0].username).isEqualTo(TEST_USER)
    }

    @Test
    @Order(7)
    fun shouldAddSearchNotification_whenCorrectRequestAll() {
        //given
        mongoSearchNotificationRepository.deleteAll()
        //when
        restTemplate.getForEntity(withBaseUrl("/accountSummaries?customerIds=$CUSTOMER_IDS_STR_ALL"), Array<AccountSummaryReadModel>::class.java)
        //then
        val searchNotifications = mongoSearchNotificationRepository.findAllByUsername(TEST_USER)
        assertThat(searchNotifications).hasSize(1)
        assertThat(searchNotifications[0].searchAllCustomers).isTrue
        assertThat(searchNotifications[0].searchCustomerIds).isNull()
        assertThat(searchNotifications[0].username).isEqualTo(TEST_USER)
    }
}