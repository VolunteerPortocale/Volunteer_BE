package com.portocale.volunteer.users.controller

import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class UserGraphQlControllerTest {

    private val userService: UserService = mock(UserService::class.java)
    private val controller = UserGraphQlController(userService)

    @Test
    fun `getAllUsers returns list of UserGQL`() {
        val users = listOf(
            UserApi("1", "Alice", 25),
            UserApi("2", "Bob", 30)
        )
        `when`(userService.getAll()).thenReturn(users)

        val result = controller.getAllUsers()

        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[0].age)
        assertEquals("2", result[1].id)
        assertEquals("Bob", result[1].name)
        assertEquals(30, result[1].age)
    }

    @Test
    fun `getUserById returns UserGQL`() {
        val user = UserApi("1", "Alice", 25)
        `when`(userService.getById("1")).thenReturn(user)

        val result = controller.getUserById("1")

        assertNotNull(result)
        assertEquals("1", result.id)
        assertEquals("Alice", result.name)
        assertEquals(25, result.age)
    }
}
