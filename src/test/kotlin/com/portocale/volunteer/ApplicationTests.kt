package com.portocale.volunteer

import com.google.api.services.drive.Drive
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class ApplicationTests {
    @MockitoBean
    lateinit var drive: Drive

    @Test
    fun contextLoads() {
        // Verifies that the Spring application context loads successfully.
    }

}
