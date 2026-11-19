package com.diasbrowser.app

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object UrlResolver {
    fun resolve(input: String): String? {
        val value = input.trim()
        if (value.isEmpty()) return null

        if (value.startsWith("javascript:", ignoreCase = true) ||
            value.startsWith("data:", ignoreCase = true) ||
            value.startsWith("file:", ignoreCase = true) ||
            value.startsWith("content:", ignoreCase = true) ||
            value.startsWith("intent:", ignoreCase = true) ||
            value.startsWith("about:", ignoreCase = true)
        ) return null

        if (value.startsWith("https://", ignoreCase = true) ||
            value.startsWith("http://", ignoreCase = true)
        ) {
            val hostPart = value.substringAfter("://").substringBefore('/').substringBefore('?')
            return value.takeIf { hostPart.isNotBlank() && !hostPart.contains(' ') }
        }

        if (value.contains("://")) return null

        if (value.any { it.isWhitespace() } || (!value.contains('.') && value != "localhost")) {
            return googleSearch(value)
        }

        val host = value.substringBefore('/').substringBefore('?')
        if (host.isBlank() || host.contains('@')) return null
        return "https://$value"
    }

    private fun googleSearch(query: String): String {
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
        return "https://www.google.com/search?q=$encoded"
    }
}
