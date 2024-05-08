package com.tcousin.urlshort.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity
class ShortenedUrl() {
    constructor(originalUrl: String) : this() {
        this.originalUrl = originalUrl
    }

    constructor(originalUrl: String, uuid: String?, numberOfUses: Int, creationDate: Timestamp?) : this() {
        this.originalUrl = originalUrl
        this.uuid = uuid!!
        this.numberOfUses = numberOfUses
        this.creationDate = creationDate!!
    }

    constructor(
        originalUrl: String,
        uuid: String?,
        numberOfUses: Int,
        creationDate: Timestamp?,
        lastAccessDate: Timestamp?
    ) : this() {
        this.originalUrl = originalUrl
        this.uuid = uuid!!
        this.numberOfUses = numberOfUses
        this.creationDate = creationDate!!
        this.lastAccessDate = lastAccessDate!!
    }

    lateinit var originalUrl: String
    var uuid: String = generateShortUuid()
    var numberOfUses = 1

    @CreatedDate
    var creationDate: Timestamp = Timestamp.from(Instant.now())

    @LastModifiedDate
    var lastAccessDate: Timestamp = Timestamp.from(Instant.now())

    @Id
    @GeneratedValue
    private val id: Long? = null


    private fun generateShortUuid(): String {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return uuid.substring(0, 12)
    }
}