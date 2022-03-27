package com.mentisimo.transactions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integrationTest")
@TestMethodOrder(OrderAnnotation::class)
abstract class IntegrationTest {
    @LocalServerPort
    private val testAppPort = 0
    protected lateinit var restTemplate: TestRestTemplate
    private lateinit var baseUrl: String
    private var username: String?
    private var password: String?

    protected constructor() {
        this.username = null
        this.password = null
    }

    protected constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }

    @BeforeAll
    fun setUp() {
        baseUrl = "http://localhost:$testAppPort"
        restTemplate = if (username != null && password != null) {
            TestRestTemplate(username, password)
        } else {
            TestRestTemplate()
        }
    }

    protected fun withBaseUrl(url: String): String {
        return baseUrl + url;
    }

    companion object {

        @Container
        var mongoDBContainer = MongoDBContainer("mongo:4.4")

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {

            mongoDBContainer.start()
            val createUserResult = mongoDBContainer.execInContainer("/bin/bash", "-c",
                    "echo 'db.createUser({user: \"test\", pwd: \"test\", roles: [{ role: \"dbAdmin\", db: \"admin\" }]})' | mongo localhost:27017/admin")
            assertThat(createUserResult.stdout).contains("Successfully added user")

            registry.add("TRANSACTIONS_FILE") { "integration-test-resources/three-transactions.csv" }
            registry.add("USERS_FILE") { "integration-test-resources/users.csv" }
            registry.add("MONGO_DB_HOST") { mongoDBContainer.host }
            registry.add("MONGO_DB_PORT") { mongoDBContainer.firstMappedPort }
            registry.add("MONGO_DB_NAME") { "test" }
            registry.add("MONGO_DB_USER") { "test" }
            registry.add("MONGO_DB_PASSWORD") { "test" }
        }
    }
}