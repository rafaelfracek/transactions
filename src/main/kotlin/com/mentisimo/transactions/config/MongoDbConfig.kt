package com.mentisimo.transactions.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import com.mentisimo.transactions.infrastructure.outbound.adapter.notification.internal.MongoSearchNotificationRepository

@Configuration
@EnableMongoRepositories(basePackageClasses = [MongoSearchNotificationRepository::class])
class MongoDbConfig(@Value("\${MONGO_DB_HOST}") private val mongoDbHost: String,
                    @Value("\${MONGO_DB_PORT}") private val mongoDbPort: Int,
                    @Value("\${MONGO_DB_NAME}") private val mongoDbName: String,
                    @Value("\${MONGO_DB_USER}") private val mongoDbUser: String,
                    @Value("\${MONGO_DB_PASSWORD}") private val mongoDbPassword: String) : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return mongoDbName
    }

    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString(createConnectionUrl())
        val mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build()
        return MongoClients.create(mongoClientSettings)
    }

    private fun createConnectionUrl() =
            "mongodb://${mongoDbUser}:${mongoDbPassword}@${mongoDbHost}:${mongoDbPort}/${databaseName}?authSource=admin"
}