package com.tcousin.urlshort.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.tcousin.urlshort.models.ShortenedUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LinkControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return 200 when accessing the root URL`() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 201 when creating a new shortened URL`() {
        mockMvc.perform(
            post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"http://example.com\"}")
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `should return 404 when trying to access a non-existing shortened URL`() {
        mockMvc.perform(get("/1234567890"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return 302 when trying to access an existing shortened URL`() {
        // First, create a new shortened URL
        val mvcResult = mockMvc.perform(
            post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"http://example.com\"}")
        )
            .andExpect(status().isCreated)
            .andReturn()

        // Extract the shortened URL from the response
        val objectMapper = ObjectMapper()
        val shortenedUrl = objectMapper.readValue(mvcResult.response.contentAsString, ShortenedUrl::class.java)

        // Then, try to access the shortened URL
        mockMvc.perform(get("/${shortenedUrl.uuid}"))
            .andExpect(status().isFound)
    }

    @Test
    fun `should find a shortened URL by its original URL`() {
        // First, create a new shortened URL
        val mvcResult = mockMvc.perform(
            post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"http://example.com\"}")
        )
            .andExpect(status().isCreated)
            .andReturn()

        // Extract the shortened URL from the response
        val objectMapper = ObjectMapper()
        val shortenedUrl = objectMapper.readValue(mvcResult.response.contentAsString, ShortenedUrl::class.java)

        // Then, try to find the shortened URL by its original URL
        mockMvc.perform(
            get("/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"http://example.com\"}")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 204 when deleting an existing shortened URL`() {
        // First, create a new shortened URL
        val mvcResult = mockMvc.perform(
            post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"http://example.com\"}")
        )
            .andExpect(status().isCreated)
            .andReturn()

        // Extract the shortened URL from the response
        val objectMapper = ObjectMapper()
        val shortenedUrl = objectMapper.readValue(mvcResult.response.contentAsString, ShortenedUrl::class.java)

        mockMvc.perform(get("/${shortenedUrl.uuid}"))
            .andExpect(status().isFound)

        mockMvc.perform(delete("/${shortenedUrl.uuid}"))
            .andExpect(status().isNoContent)

        mockMvc.perform(delete("/${shortenedUrl.uuid}"))
            .andExpect(status().isNotFound)
    }


}