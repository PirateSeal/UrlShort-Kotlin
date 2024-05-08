package com.tcousin.urlshort

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UrlShortApplication

fun main(args: Array<String>) {
    runApplication<UrlShortApplication>(*args)
}