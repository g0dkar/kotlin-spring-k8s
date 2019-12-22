package com.g0dkar.samplek8sproj

import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile

@Testcontainers
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(classes = [Launcher::class], webEnvironment = RANDOM_PORT)
abstract class IntegrationTest(
    val logger: Logger = LoggerFactory.getLogger(javaClass)
) {
    companion object {
        private val POSTGRESQL_IMAGE = "postgres:12-alpine"
        private val POSTGRESQL_INIT_SCRIPT = MountableFile.forClasspathResource("docker-postgres/init_db.sql")
        val POSTGRESQL = PostgreSQLContainer<Nothing>(POSTGRESQL_IMAGE)
            .apply {
                withCopyFileToContainer(POSTGRESQL_INIT_SCRIPT, "/docker-entrypoint-initdb.d/")
                start()
                System.setProperty("DB_URL", jdbcUrl)
                System.setProperty("DB_USER", username)
                System.setProperty("DB_PASSWORD", password)
            }
    }
}
