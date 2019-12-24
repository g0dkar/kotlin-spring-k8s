package com.g0dkar.samplek8sproj.util

import org.apache.commons.lang3.RandomStringUtils
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Random
import kotlin.math.abs

private object TestUtils {
    val RAND = Random()
    const val DEFAULT_MAX = 1000
}

private fun rand(max: Int = TestUtils.DEFAULT_MAX): Int =
    TestUtils.RAND.nextInt(max)

/**
 * Generates a Random Alphanumeric String.
 *
 * @param minSize Minimum size of the string (default = 5)
 * @param maxSize Minimum size of the string (default = 25)
 */
fun randomString(minSize: Int = 5, maxSize: Int = 25): String =
    RandomStringUtils.randomAlphanumeric(minSize, maxSize)

/**
 * Generates a random date from a reference point.
 * @param reference The reference time (default = [Instant.now])
 * @param chronoUnit The unit that will be added (default = [ChronoUnit.DAYS])
 * @param maxAmount The maximum number of units that will be added (default = 1000)
 */
fun randomDateFuture(
    reference: Instant = Instant.now(),
    chronoUnit: ChronoUnit = ChronoUnit.DAYS,
    maxAmount: Int = TestUtils.DEFAULT_MAX
):
    Instant =
    reference.plus(abs(rand(maxAmount).toLong()), chronoUnit)

/**
 * Generates a random date from a reference point. Subtracts _up to_ 1000 units of time.
 * @param reference The reference time (default = [Instant.now])
 * @param chronoUnit The unit that will be added (default = [ChronoUnit.DAYS])
 * @param maxAmount The maximum number of units that will be subtracted (default = 1000)
 */
fun randomDatePast(
    reference: Instant = Instant.now(),
    chronoUnit: ChronoUnit = ChronoUnit.DAYS,
    maxAmount: Int = TestUtils.DEFAULT_MAX
): Instant =
    reference.minus(abs(rand(maxAmount).toLong()), chronoUnit)

/**
 * Returns a random value from the [List].
 */
fun <T : Any> randomFrom(values: List<T>): T =
    values.get(rand(values.size))

/**
 * Returns a random value from the [Array].
 */
fun <T : Any> randomFrom(values: Array<T>): T =
    values.get(rand(values.size))
