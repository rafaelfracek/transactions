package com.mentisimo.transactions.infrastructure.outbound.adapter.notification

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import com.mentisimo.transactions.domain.model.SearchNotification
import com.mentisimo.transactions.infrastructure.outbound.adapter.notification.internal.MongoSearchNotificationRepository
import com.mentisimo.transactions.infrastructure.outbound.port.SearchNotificationSender

@Component
@Transactional
class MongoSearchNotificationSender(private val mongoSearchNotificationRepository: MongoSearchNotificationRepository)
    : SearchNotificationSender {

    override fun sendSearchNotification(searchNotification: SearchNotification) {
        mongoSearchNotificationRepository.save(searchNotification)
    }
}