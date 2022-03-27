package com.mentisimo.transactions.infrastructure.outbound.adapter.notification.internal

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import com.mentisimo.transactions.domain.model.SearchNotification
import java.util.*

@Repository
interface MongoSearchNotificationRepository : MongoRepository<SearchNotification, UUID> {
    fun findAllByUsername(username: String): List<SearchNotification>
}