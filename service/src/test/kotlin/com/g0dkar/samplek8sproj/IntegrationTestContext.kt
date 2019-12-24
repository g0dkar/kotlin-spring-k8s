package com.g0dkar.samplek8sproj

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

@Configuration
@Import(Launcher::class)
class IntegrationTestContext {
    companion object {
        private const val POSTGRESQL_IMAGE = "postgres:12-alpine"
        private const val POSTGRESQL_INIT_SCRIPT = "docker-postgres/init_db.sql"

        init {
            System.setProperty("org.jooq.no-logo", "true")

            val initDbFile = MountableFile.forClasspathResource(POSTGRESQL_INIT_SCRIPT)

            PostgreSQLContainer<Nothing>(POSTGRESQL_IMAGE)
                .apply {
                    withCopyFileToContainer(initDbFile, "/docker-entrypoint-initdb.d/")
                    start()
                    System.setProperty("DB_URL", jdbcUrl)
                    System.setProperty("DB_USER", username)
                    System.setProperty("DB_PASSWORD", password)
                }
        }
    }
}
