package com.tcousin.urlshort.controllers

import com.tcousin.urlshort.implementations.DataAccessImplementation
import com.tcousin.urlshort.models.ShortenedUrl
import com.tcousin.urlshort.models.UrlRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
class LinkController @Autowired constructor(private val dataAccess: DataAccessImplementation) {

    @GetMapping("/")
    fun index(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "20") size: Int): Any? {
        val response = dataAccess.getAll(PageRequest.of(page, size))
        return response ?: "No data found."
    }

    @GetMapping("/{uuid}")
    fun findByUuid(@PathVariable uuid: String): Any {
        val shortenedUrl = dataAccess.findByUuid(uuid)
        return if (shortenedUrl != null) {
            RedirectView(shortenedUrl.originalUrl)
        } else {
            ResponseEntity.notFound().build<Any>()
        }
    }

    @GetMapping("/url")
    fun findByUrl(@RequestBody urlRequest: UrlRequest): ResponseEntity<ShortenedUrl> {
        val response = dataAccess.findByUrl(urlRequest.url)
        return response?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/")
    fun create(@RequestBody urlRequest: UrlRequest): ResponseEntity<ShortenedUrl> {
        val response = dataAccess.create(urlRequest.url)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @DeleteMapping("/{uuid}")
    fun delete(@PathVariable uuid: String): ResponseEntity<Unit> {
        val response = dataAccess.delete(uuid)
        return if (response) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }

}