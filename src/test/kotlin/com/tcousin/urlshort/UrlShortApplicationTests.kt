package com.tcousin.urlshort

import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.ConfigurableApplicationContext

@SpringBootTest
class UrlShortApplicationTests {

    @Test
    fun contextLoads() {
    }

    @Test
    fun testServletInitializer() {
        val servletInitializer = object : SpringBootServletInitializer() {
            public override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
                return application.sources(UrlShortApplication::class.java)
            }
        }

        val context: ConfigurableApplicationContext = SpringApplication.run(UrlShortApplication::class.java)
        assert(context.isActive)
    }
}
