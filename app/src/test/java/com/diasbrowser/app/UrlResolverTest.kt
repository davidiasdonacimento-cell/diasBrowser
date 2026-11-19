package com.diasbrowser.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UrlResolverTest {
    @Test fun domainGetsHttpsScheme() {
        assertEquals("https://example.com", UrlResolver.resolve("example.com"))
    }

    @Test fun fullHttpAddressIsPreserved() {
        assertEquals("http://example.com/page", UrlResolver.resolve("http://example.com/page"))
    }

    @Test fun wordsBecomeGoogleSearch() {
        assertEquals("https://www.google.com/search?q=hello+world", UrlResolver.resolve("hello world"))
    }

    @Test fun blankInputIsRejected() {
        assertNull(UrlResolver.resolve("   "))
    }

    @Test fun javascriptUrlsAreRejected() {
        assertNull(UrlResolver.resolve("javascript:alert(1)"))
    }
}
