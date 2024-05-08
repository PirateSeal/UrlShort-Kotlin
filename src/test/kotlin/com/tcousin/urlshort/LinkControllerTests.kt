package com.tcousin.urlshort

import com.fasterxml.jackson.databind.ObjectMapper
import com.tcousin.urlshort.models.ShortenedUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LinkControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return paginated results when accessing the root URL with page and size parameters`() {
        // Create some shortened URLs for testing
        for (i in 1..30) {
            mockMvc.perform(
                post("/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"http://example.com/$i\"}")
            )
                .andExpect(status().isCreated)
        }

        // Test the first page
        var mvcResult = mockMvc.perform(get("/?page=0&size=10"))
            .andExpect(status().isOk)
            .andReturn()

        var content = mvcResult.response.contentAsString
        // Assert that the first page contains the first 10 URLs
        for (i in 1..10) {
            assert(content.contains("http://example.com/$i"))
        }

        // Test the second page
        mvcResult = mockMvc.perform(get("/?page=1&size=10"))
            .andExpect(status().isOk)
            .andReturn()

        content = mvcResult.response.contentAsString
        // Assert that the second page contains the next 10 URLs
        for (i in 11..20) {
            assert(content.contains("http://example.com/$i"))
        }

        // Test the third page
        mvcResult = mockMvc.perform(get("/?page=2&size=10"))
            .andExpect(status().isOk)
            .andReturn()

        content = mvcResult.response.contentAsString
        // Assert that the third page contains the last 10 URLs
        for (i in 21..30) {
            assert(content.contains("http://example.com/$i"))
        }
    }

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


}