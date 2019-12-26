package com.g0dkar.samplek8sproj.extensions

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

internal inline val <reified T> T.log: Logger
    get() = LogManager.getLogger(T::class.java)
