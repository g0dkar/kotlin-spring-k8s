package com.g0dkar.samplek8sproj

import com.g0dkar.samplek8sproj.util.SetupEnvironment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Launcher

fun main(args: Array<String>) {
    SetupEnvironment.apply {
        setupSpringProfile()
        disableLogos()
        disableFavIcon()
    }

    runApplication<Launcher>(*args)
}
