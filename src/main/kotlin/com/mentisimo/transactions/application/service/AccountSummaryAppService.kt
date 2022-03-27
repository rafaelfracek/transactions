package com.mentisimo.transactions.application.service

import org.springframework.stereotype.Service
import com.mentisimo.transactions.domain.model.AccountSummaryReadModel
import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.domain.model.SearchNotification
import com.mentisimo.transactions.domain.model.query.FindAllAccountSummaries
import com.mentisimo.transactions.domain.model.query.FindAllAccountSummariesByCustomerIdsQuery
import com.mentisimo.transactions.infrastructure.outbound.port.AccountSummaryReadModelRepository
import com.mentisimo.transactions.infrastructure.outbound.port.SearchNotificationOutboundSubject
import java.time.LocalDateTime

@Service
class AccountSummaryAppService(private val accountSummaryReadModelRepository: AccountSummaryReadModelRepository,
                               private val searchNotificationOutboundSubject: SearchNotificationOutboundSubject) {

    fun findAll(query: FindAllAccountSummaries): List<AccountSummaryReadModel> {
        addSearchNotificationToOutboundSubject(true, null, query.user.username)
        return accountSummaryReadModelRepository.findAll()
    }

    fun findAll(query: FindAllAccountSummariesByCustomerIdsQuery): List<AccountSummaryReadModel> {
        addSearchNotificationToOutboundSubject(false, query.customerIds, query.user.username)
        return accountSummaryReadModelRepository.findAll(query.customerIds)
    }

    private fun addSearchNotificationToOutboundSubject(searchAllCustomers: Boolean, searchCustomerIds: Set<CustomerId>?, username: String) {
        searchNotificationOutboundSubject.addToQueue(SearchNotification(
                searchAllCustomers = searchAllCustomers,
                searchCustomerIds = searchCustomerIds,
                searchTime = LocalDateTime.now(),
                username = username
        ))
    }
}