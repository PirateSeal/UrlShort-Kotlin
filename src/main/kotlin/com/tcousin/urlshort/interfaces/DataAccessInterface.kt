package com.tcousin.urlshort.interfaces

import com.tcousin.urlshort.models.ShortenedUrl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface DataAccessInterface {

    fun create(url: String): ShortenedUrl
    fun findByUuid(uuid: String): ShortenedUrl?
    fun findByUrl(url: String): ShortenedUrl?
    fun delete(uuid: String): Boolean
    fun getAll(pageable: Pageable): Page<ShortenedUrl>?

}