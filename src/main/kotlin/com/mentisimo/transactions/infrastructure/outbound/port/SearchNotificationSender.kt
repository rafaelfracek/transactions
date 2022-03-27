package com.mentisimo.transactions.infrastructure.outbound.port

import com.mentisimo.transactions.domain.model.SearchNotification

interface SearchNotificationSender {
    fun sendSearchNotification(searchNotification: SearchNotification)
}