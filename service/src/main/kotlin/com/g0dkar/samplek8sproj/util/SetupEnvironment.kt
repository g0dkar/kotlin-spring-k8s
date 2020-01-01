package com.g0dkar.samplek8sproj.util

import com.g0dkar.samplek8sproj.extensions.log
import com.g0dkar.samplek8sproj.util.Environment.Companion.DEFAULT
import com.g0dkar.samplek8sproj.util.Environment.Companion.ENVIRONMENT_ENV
import com.g0dkar.samplek8sproj.util.SetupEnvironment.disableLogos
import com.g0dkar.samplek8sproj.util.SetupEnvironment.setupSpringProfile

/**
 * Contains useful setup functions like [setupSpringProfile] and [disableLogos].
 */
internal object SetupEnvironment {
    /** Current [Region] extracted from the Environment Variables */
    val CURRENT_REGION = Region.fromEnv()

    /** Current [Environment] extracted from the Environment Variables */
    val CURRENT_ENV = Environment.fromEnv()

    /**
     * Sets the `spring.profiles.active` system property given a [Region] and [Environment].
     */
    fun setupSpringProfile(
        region: Region = CURRENT_REGION,
        environment: Environment = CURRENT_ENV
    ) {
        val activeProfiles = listOf(
            environment.suffix,
            region.suffix,
            "${environment.suffix}-${region.suffix}"
        )

        setProperty("spring.profiles.active", activeProfiles.joinToString())
    }

    /**
     * Disables printing of logos on startup (Spring Banner and jooq Logo - reduces log pollution).
     */
    fun disableLogos() {
        setProperty("org.jooq.no-logo", "true")
        setProperty("spring.main.banner-mode", "off")
    }

    /**
     * Disables the favicon.
     */
    fun disableFavIcon() {
        setProperty("spring.mvc.favicon.enabled", "false")
    }

    /**
     * Sets a property and prints a log line about it.
     */
    private fun setProperty(property: String, value: String) =
        log.info("Setting $property = \"$value\"")
            .also { System.setProperty(property, value) }
}

/**
 * Regions where this app might be deployed (loosely based on what AWS offers)
 */
internal enum class Region(val suffix: String) {
    /** European Union */
    EU("eu"),

    /** United States */
    US("us"),

    /** South America */
    SA("sa");

    companion object {
        val DEFAULT = EU
        const val REGION_ENV = "REGION"

        /**
         * Tries to extract which region this app is running based on the [REGION_ENV] environment variable.
         * Returns [DEFAULT] if it's not present or has an invalid value.
         */
        fun fromEnv(): Region =
            System.getenv(REGION_ENV)
                ?.let {
                    try {
                        valueOf(it.toUpperCase().trim())
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                ?: DEFAULT
    }
}

/**
 * Environments where this app might be deployed
 */
internal enum class Environment(val suffix: String) {
    /** Localhost development */
    TEST("test"),

    /** Localhost development */
    LOCAL("local"),

    /** Development */
    DEVELOPMENT("dev"),

    /** Staging or Pre-Prod */
    STAGING("staging"),

    /** Production */
    LIVE("live");

    companion object {
        val DEFAULT = LOCAL
        const val ENVIRONMENT_ENV = "ENV"

        /**
         * Tries to extract which environment this app is running based on the [ENVIRONMENT_ENV] environment variable.
         * Returns [DEFAULT] if it's not present or has an invalid value.
         */
        fun fromEnv(): Environment =
            System.getenv(ENVIRONMENT_ENV)
                ?.let {
                    try {
                        valueOf(it.toUpperCase().trim())
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                ?: DEFAULT
    }
}
