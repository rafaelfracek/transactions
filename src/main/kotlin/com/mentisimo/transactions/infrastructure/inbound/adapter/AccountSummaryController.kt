package com.mentisimo.transactions.infrastructure.inbound.adapter

import com.google.common.base.Splitter
import org.apache.commons.lang3.StringUtils
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.mentisimo.transactions.application.service.AccountSummaryAppService
import com.mentisimo.transactions.domain.model.AccountSummaryReadModel
import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.domain.model.User
import com.mentisimo.transactions.domain.model.query.FindAllAccountSummaries
import com.mentisimo.transactions.domain.model.query.FindAllAccountSummariesByCustomerIdsQuery
import com.mentisimo.transactions.infrastructure.inbound.adapter.CONST.ACCOUNT_SUMMARIES_MAPPING
import com.mentisimo.transactions.infrastructure.inbound.adapter.CONST.CUSTOMER_IDS_ALL_VALUE
import com.mentisimo.transactions.infrastructure.inbound.adapter.CONST.CUSTOMER_IDS_PARAM_NAME
import com.mentisimo.transactions.infrastructure.inbound.adapter.CONST.CUSTOMER_IDS_SEPARATOR
import java.util.stream.Collectors

object CONST {
    const val ACCOUNT_SUMMARIES_MAPPING = "/accountSummaries"
    const val CUSTOMER_IDS_PARAM_NAME = "customerIds"
    const val CUSTOMER_IDS_ALL_VALUE = "ALL"
    const val CUSTOMER_IDS_SEPARATOR = ","
}

@RestController
@RequestMapping(ACCOUNT_SUMMARIES_MAPPING)
class AccountSummaryController(private val accountSummaryAppService: AccountSummaryAppService) {

    @GetMapping
    fun findAllUsersWithTransactionDetails(@RequestParam(CUSTOMER_IDS_PARAM_NAME, required = false) customerIds: String?,
                                           authentication: Authentication): List<AccountSummaryReadModel> {
        return when (shouldFindAllCustomers(customerIds)) {
            true -> accountSummaryAppService.findAll(createFindAllQuery(authentication.name))
            false -> accountSummaryAppService.findAll(mapCustomerIdsToQuery(customerIds!!, authentication.name))
        }
    }

    private fun createFindAllQuery(username: String) =
            FindAllAccountSummaries(User(username))

    private fun mapCustomerIdsToQuery(customerIdsStr: String, username: String): FindAllAccountSummariesByCustomerIdsQuery {
        val customerIds = Splitter.on(CUSTOMER_IDS_SEPARATOR)
                .omitEmptyStrings()
                .trimResults()
                .splitToStream(customerIdsStr)
                .map { s: String -> CustomerId(s.toLong()) }
                .collect(Collectors.toSet())
        return FindAllAccountSummariesByCustomerIdsQuery(customerIds, User(username))
    }

    private fun shouldFindAllCustomers(customerIds: String?): Boolean {
        return StringUtils.isBlank(customerIds) || customerIds.equals(CUSTOMER_IDS_ALL_VALUE, ignoreCase = true);
    }
}