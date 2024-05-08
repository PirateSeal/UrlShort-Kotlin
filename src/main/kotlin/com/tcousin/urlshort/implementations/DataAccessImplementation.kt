package com.tcousin.urlshort.implementations

import com.tcousin.urlshort.interfaces.DataAccessInterface
import com.tcousin.urlshort.models.ShortenedUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant

@Repository
class DataAccessImplementation @Autowired constructor(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) :
    DataAccessInterface {
    override fun create(url: String): ShortenedUrl {
        val search = findByUrl(url)
        if (search != null) {
            incrementUsage(search.uuid)
            return search
        } else {
            val shortenedUrl = ShortenedUrl(url)
            val sql =
                "INSERT INTO shortened_url (original_url, uuid, number_of_uses, creation_date, last_access_date) VALUES (:original_url, :uuid, :number_of_uses, :creation_date, :last_access_date)"
            val namedParameters = MapSqlParameterSource()
                .addValue("original_url", shortenedUrl.originalUrl)
                .addValue("uuid", shortenedUrl.uuid)
                .addValue("number_of_uses", shortenedUrl.numberOfUses)
                .addValue("creation_date", shortenedUrl.creationDate)
                .addValue("last_access_date", shortenedUrl.lastAccessDate)
            namedParameterJdbcTemplate.update(sql, namedParameters)
            return shortenedUrl
        }
    }

    override fun findByUuid(uuid: String): ShortenedUrl? {
        val shortenedUrl = queryForShortenedUrl("SELECT * FROM shortened_url WHERE uuid = :uuid", "uuid", uuid)
        shortenedUrl?.let { incrementUsage(it.uuid) }
        return shortenedUrl
    }

    override fun findByUrl(url: String): ShortenedUrl? {
        val shortenedUrl = queryForShortenedUrl(
            "SELECT * FROM shortened_url WHERE original_url = :original_url",
            "original_url",
            url
        )
        shortenedUrl?.let { incrementUsage(it.uuid) }
        return shortenedUrl
    }

    private fun incrementUsage(uuid: String) {
        val sql =
            "UPDATE shortened_url SET number_of_uses = number_of_uses + 1, last_access_date = :last_access_date WHERE uuid = :uuid"
        val namedParameters = MapSqlParameterSource()
            .addValue("uuid", uuid)
            .addValue("last_access_date", Timestamp.from(Instant.now()))
        namedParameterJdbcTemplate.update(sql, namedParameters)
    }

    private fun queryForShortenedUrl(sql: String, paramName: String, paramValue: String): ShortenedUrl? {
        val namedParameters = MapSqlParameterSource().addValue(paramName, paramValue)
        val results = namedParameterJdbcTemplate.query(sql, namedParameters) { rs, _ ->
            ShortenedUrl(
                originalUrl = rs.getString("original_url"),
                uuid = rs.getString("uuid"),
                numberOfUses = rs.getInt("number_of_uses"),
                creationDate = rs.getTimestamp("creation_date")
            )
        }
        return results.firstOrNull()
    }

    override fun delete(uuid: String): Boolean {
        val sql = "DELETE FROM shortened_url WHERE uuid = :uuid"
        val namedParameters = MapSqlParameterSource().addValue("uuid", uuid)
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0
    }

    override fun getAll(pageable: Pageable): Page<ShortenedUrl>? {
        val sql = "SELECT * FROM shortened_url LIMIT :limit OFFSET :offset"
        val namedParameters = MapSqlParameterSource()
            .addValue("limit", pageable.pageSize)
            .addValue("offset", pageable.offset)
        val results = namedParameterJdbcTemplate.query(sql, namedParameters) { rs, _ ->
            ShortenedUrl(
                originalUrl = rs.getString("original_url"),
                uuid = rs.getString("uuid"),
                numberOfUses = rs.getInt("number_of_uses"),
                creationDate = rs.getTimestamp("creation_date"),
                lastAccessDate = rs.getTimestamp("last_access_date")
            )
        }
        return PageImpl(results, pageable, results.size.toLong())
    }
}