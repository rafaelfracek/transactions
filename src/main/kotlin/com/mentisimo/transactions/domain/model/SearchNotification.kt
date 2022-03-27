package com.mentisimo.transactions.domain.model

import java.time.LocalDateTime

data class SearchNotification(val username: String, val searchAllCustomers: Boolean,
                              val searchCustomerIds: Set<CustomerId>?, val searchTime: LocalDateTime)