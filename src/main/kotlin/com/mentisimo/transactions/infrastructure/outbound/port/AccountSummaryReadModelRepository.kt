package com.mentisimo.transactions.infrastructure.outbound.port

import com.mentisimo.transactions.domain.model.AccountSummaryReadModel
import com.mentisimo.transactions.domain.model.CustomerId

interface AccountSummaryReadModelRepository {
    fun findAll(): List<AccountSummaryReadModel>
    fun findAll(customerIds: Set<CustomerId>): List<AccountSummaryReadModel>
    fun saveOrUpdate(accountSummary: AccountSummaryReadModel)
    fun deleteByCustomerId(customerId: CustomerId)
}