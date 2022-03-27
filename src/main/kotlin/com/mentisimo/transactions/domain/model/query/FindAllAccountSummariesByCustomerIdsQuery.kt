package com.mentisimo.transactions.domain.model.query

import com.mentisimo.transactions.domain.model.CustomerId
import com.mentisimo.transactions.domain.model.User

data class FindAllAccountSummariesByCustomerIdsQuery(val customerIds: Set<CustomerId>, val user: User)