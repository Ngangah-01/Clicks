package com.clicks.app.lib.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorsTest {

    @Test
    fun validEmails_areAccepted() {
        listOf(
            "john@gmail.com",
            "john.doe+tag@sub.example.co.ke",
            "  padded@example.com  "
        ).forEach { assertTrue("expected valid: $it", AuthValidators.isValidEmail(it)) }
    }

    @Test
    fun invalidEmails_areRejected() {
        listOf(
            "", "   ", "plain", "@no-user.com", "no-domain@", "a@b", "a@b.", "a b@c.com", "a@@b.com"
        ).forEach { assertFalse("expected invalid: '$it'", AuthValidators.isValidEmail(it)) }
    }

    @Test
    fun password_lengthBoundary() {
        assertFalse(AuthValidators.isValidPassword("1234567"))
        assertTrue(AuthValidators.isValidPassword("12345678"))
        assertFalse(AuthValidators.isValidPassword(""))
    }

    @Test
    fun code_mustBeExactlySixDigits() {
        assertTrue(AuthValidators.isValidCode("123456"))
        assertFalse(AuthValidators.isValidCode("12345"))
        assertFalse(AuthValidators.isValidCode("1234567"))
        assertFalse(AuthValidators.isValidCode("12a456"))
        assertFalse(AuthValidators.isValidCode(""))
    }
}
