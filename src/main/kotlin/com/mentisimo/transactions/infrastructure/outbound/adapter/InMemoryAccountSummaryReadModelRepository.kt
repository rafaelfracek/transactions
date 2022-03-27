package com.mentisimo.transactions.infrastructure.outbound.adapter

import org.springframework.stereotype.Repository
import com.mentisimo.transactions.domain.model.AccountSummaryReadModel
import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.infrastructure.outbound.port.AccountSummaryReadModelRepository
import java.util.*
import java.util.Collections.unmodifiableList
import java.util.List.copyOf
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

@Repository
class InMemoryAccountSummaryReadModelRepository : AccountSummaryReadModelRepository {
    private val customerIdToAccountSummary: MutableMap<CustomerId, AccountSummaryReadModel> = ConcurrentHashMap()

    override fun findAll(): List<AccountSummaryReadModel> {
        return safeCastToUnmodifiableListOrCopy(customerIdToAccountSummary.values);
    }

    override fun findAll(customerIds: Set<CustomerId>): List<AccountSummaryReadModel> {
        return customerIds.stream()
                .map { customerId -> customerIdToAccountSummary[customerId] }
                .filter(Objects::nonNull)
                .collect(Collectors.toList<AccountSummaryReadModel>())
    }

    override fun saveOrUpdate(accountSummary: AccountSummaryReadModel) {
        customerIdToAccountSummary[CustomerId(accountSummary.customerId)] = accountSummary
    }

    override fun deleteByCustomerId(customerId: CustomerId) {
        customerIdToAccountSummary.remove(customerId);
    }

    private  fun <T> safeCastToUnmodifiableListOrCopy(collection: Collection<T>): List<T> {
        return if (collection is List<T>) unmodifiableList(collection) else copyOf(collection);
    }
}