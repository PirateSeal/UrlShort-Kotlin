package com.tcousin.urlshort.model

import com.tcousin.urlshort.models.ShortenedUrl
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
class ShortenedUrlTests {
    @Test
    fun testShortenedUrlCtorFromUrl() {
        val url = "http://example.com"
        val shortenedUrl = ShortenedUrl(url)
        assert(shortenedUrl.originalUrl == url)
        assert(shortenedUrl.uuid.length == 12)
        assert(shortenedUrl.numberOfUses == 1)
    }

    @Test
    fun testShortenedUrlCtorFromUrlUuidNumberOfUsesCreationDate() {
        val url = "http://example.com"
        val uuid = "123456789012"
        val numberOfUses = 5
        val creationDate = Timestamp.from(Instant.now())
        val shortenedUrl = ShortenedUrl(url, uuid, numberOfUses, creationDate)
        assert(shortenedUrl.originalUrl == url)
        assert(shortenedUrl.uuid == uuid)
        assert(shortenedUrl.numberOfUses == numberOfUses)
        assert(shortenedUrl.creationDate == creationDate)
    }
}