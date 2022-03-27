package com.mentisimo.transactions.infrastructure.outbound.port

import com.mentisimo.transactions.domain.model.SearchNotification

interface SearchNotificationOutboundSubject {
    fun addToQueue(searchNotification: SearchNotification)
}