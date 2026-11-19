package com.diasbrowser.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class BrowserEntry(
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)

class BrowserStore(context: Context) {
    private val preferences = context.getSharedPreferences("dias_browser_store", Context.MODE_PRIVATE)

    fun bookmarks(): List<BrowserEntry> = readEntries(KEY_BOOKMARKS)
    fun history(): List<BrowserEntry> = readEntries(KEY_HISTORY)

    fun addBookmark(title: String, url: String) {
        if (url.isBlank() || url == "about:blank") return
        val entries = bookmarks().filterNot { it.url == url }.toMutableList()
        entries.add(0, BrowserEntry(title.ifBlank { url }, url))
        writeEntries(KEY_BOOKMARKS, entries.take(MAX_BOOKMARKS))
    }

    fun removeBookmark(url: String) {
        writeEntries(KEY_BOOKMARKS, bookmarks().filterNot { it.url == url })
    }

    fun addHistory(title: String, url: String) {
        if (url.isBlank() || url == "about:blank" || url.startsWith("data:")) return
        val entries = history().filterNot { it.url == url }.toMutableList()
        entries.add(0, BrowserEntry(title.ifBlank { url }, url))
        writeEntries(KEY_HISTORY, entries.take(MAX_HISTORY))
    }

    fun clearHistory() {
        preferences.edit().remove(KEY_HISTORY).apply()
    }

    private fun readEntries(key: String): List<BrowserEntry> {
        val raw = preferences.getString(key, "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val url = item.optString("url")
                    if (url.isNotBlank()) {
                        add(
                            BrowserEntry(
                                title = item.optString("title", url),
                                url = url,
                                visitedAt = item.optLong("visitedAt", 0L)
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun writeEntries(key: String, entries: List<BrowserEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject()
                    .put("title", entry.title)
                    .put("url", entry.url)
                    .put("visitedAt", entry.visitedAt)
            )
        }
        preferences.edit().putString(key, array.toString()).apply()
    }

    private companion object {
        const val KEY_BOOKMARKS = "bookmarks"
        const val KEY_HISTORY = "history"
        const val MAX_BOOKMARKS = 200
        const val MAX_HISTORY = 300
    }
}
