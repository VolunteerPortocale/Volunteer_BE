package com.portocale.volunteer.users

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ExtensionsTest {

    @Test
    fun `toUserGQL from UserApi maps all properties correctly`() {
        val userApi = UserApi(id = "user-123", name = "John Doe", age = 28)

        val gql = userApi.toUserGQL()

        assertEquals("user-123", gql.id)
        assertEquals("John Doe", gql.name)
        assertEquals(28, gql.age)
    }

    @Test
    fun `toUserGQL from User maps all properties correctly`() {
        val user = User(id = null, name = "Jane Doe", age = 22)

        val gql = user.toUserGQL()

        assertNull(gql.id)
        assertEquals("Jane Doe", gql.name)
        assertEquals(22, gql.age)
    }
}
