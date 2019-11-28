package com.g0dkar.samplek8sproj

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleK8sApplication

fun main(args: Array<String>) {
    runApplication<SampleK8sApplication>(*args)
}