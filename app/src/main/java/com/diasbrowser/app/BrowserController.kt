package com.diasbrowser.app

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.concurrent.atomic.AtomicInteger

data class BrowserTab(
    val id: Int,
    val title: String = "Nova aba",
    val url: String = "",
    val temporary: Boolean = false
)

class BrowserController(
    private val context: Context,
    private val store: BrowserStore
) {
    val tabs = mutableStateListOf(BrowserTab(id = 1))
    var activeTabId by mutableIntStateOf(1)
        private set
    var progress by mutableIntStateOf(0)
        private set
    var loading by mutableStateOf(false)
        private set

    private val ids = AtomicInteger(1)
    private val webViews = mutableMapOf<Int, WebView>()
    private var container: FrameLayout? = null

    val activeTab: BrowserTab
        get() = tabs.firstOrNull { it.id == activeTabId } ?: BrowserTab(id = activeTabId)

    fun attachContainer(frame: FrameLayout) {
        container = frame
        syncViews()
    }

    fun syncViews() {
        val frame = container ?: return
        tabs.forEach { tab ->
            val webView = webViews.getOrPut(tab.id) { createWebView(tab) }
            if (webView.parent !== frame) {
                (webView.parent as? ViewGroup)?.removeView(webView)
                frame.addView(
                    webView,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
            webView.visibility = if (tab.id == activeTabId) android.view.View.VISIBLE else android.view.View.GONE
        }
        webViews.keys.toList().filterNot { id -> tabs.any { it.id == id } }.forEach { id ->
            webViews.remove(id)?.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
                view.destroy()
            }
        }
    }

    fun navigate(input: String): Boolean {
        val destination = UrlResolver.resolve(input) ?: return false
        if (webViews[activeTabId] == null) syncViews()
        val view = webViews[activeTabId] ?: return false
        view.loadUrl(destination)
        updateTab(activeTabId) { it.copy(url = destination, title = "Carregando…") }
        loading = true
        progress = 0
        return true
    }

    fun goBack(): Boolean {
        val view = webViews[activeTabId] ?: return false
        if (!view.canGoBack()) return false
        view.goBack()
        return true
    }

    fun goForward(): Boolean {
        val view = webViews[activeTabId] ?: return false
        if (!view.canGoForward()) return false
        view.goForward()
        return true
    }

    fun reload() { webViews[activeTabId]?.reload() }

    fun goHome() {
        webViews[activeTabId]?.stopLoading()
        updateTab(activeTabId) { it.copy(url = "", title = "Nova aba") }
        loading = false
        progress = 0
    }

    fun addTab(temporary: Boolean = false) {
        val id = ids.incrementAndGet()
        tabs.add(BrowserTab(id = id, temporary = temporary))
        activeTabId = id
        progress = 0
        loading = false
        syncViews()
    }

    fun selectTab(id: Int) {
        if (tabs.none { it.id == id }) return
        activeTabId = id
        syncViews()
        progress = 100
    }

    fun closeTab(id: Int) {
        val index = tabs.indexOfFirst { it.id == id }
        if (index < 0) return
        val wasActive = activeTabId == id
        webViews.remove(id)?.let { view ->
            view.stopLoading()
            (view.parent as? ViewGroup)?.removeView(view)
            view.destroy()
        }
        tabs.removeAt(index)
        if (tabs.isEmpty()) {
            addTab()
            return
        }
        if (wasActive) activeTabId = tabs[(index - 1).coerceAtLeast(0)].id
        syncViews()
    }

    fun canGoBackNow(): Boolean = webViews[activeTabId]?.canGoBack() == true
    fun canGoForwardNow(): Boolean = webViews[activeTabId]?.canGoForward() == true

    fun downloadEntries(): List<String> {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = manager.query(DownloadManager.Query())
        val entries = mutableListOf<String>()
        cursor?.use {
            val titleColumn = it.getColumnIndex(DownloadManager.COLUMN_TITLE)
            val statusColumn = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val reasonColumn = it.getColumnIndex(DownloadManager.COLUMN_REASON)
            while (it.moveToNext()) {
                val title = if (titleColumn >= 0) it.getString(titleColumn) else "Arquivo"
                val status = if (statusColumn >= 0) it.getInt(statusColumn) else -1
                val statusText = when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> "Concluído"
                    DownloadManager.STATUS_FAILED -> {
                        val reason = if (reasonColumn >= 0) it.getInt(reasonColumn) else 0
                        "Falhou (código $reason)"
                    }
                    DownloadManager.STATUS_RUNNING -> "Baixando"
                    DownloadManager.STATUS_PAUSED -> "Pausado"
                    else -> "Na fila"
                }
                entries.add("$title — $statusText")
            }
        }
        return entries
    }

    fun destroy() {
        webViews.values.forEach { view ->
            view.stopLoading()
            (view.parent as? ViewGroup)?.removeView(view)
            view.destroy()
        }
        webViews.clear()
        container = null
    }

    private fun createWebView(tab: BrowserTab): WebView {
        return WebView(context).apply {
            setBackgroundColor(android.graphics.Color.rgb(8, 13, 26))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            settings.allowFileAccess = false
            settings.allowContentAccess = true
            settings.mediaPlaybackRequiresUserGesture = true
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            if (android.os.Build.VERSION.SDK_INT >= 26) settings.safeBrowsingEnabled = true
            if (tab.temporary) settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val scheme = request?.url?.scheme?.lowercase()
                    return scheme != "http" && scheme != "https"
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    val pageUrl = url.orEmpty()
                    updateTab(tab.id) { it.copy(url = pageUrl) }
                    if (tab.id == activeTabId) {
                        loading = true
                        progress = 5
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    val pageUrl = url.orEmpty()
                    val pageTitle = view?.title?.takeIf { it.isNotBlank() } ?: pageUrl
                    updateTab(tab.id) { it.copy(url = pageUrl, title = pageTitle) }
                    if (!tab.temporary && pageUrl.isNotBlank()) store.addHistory(pageTitle, pageUrl)
                    if (tab.id == activeTabId) {
                        loading = false
                        progress = 100
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (tab.id == activeTabId) {
                        progress = newProgress
                        loading = newProgress < 100
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    if (!title.isNullOrBlank()) updateTab(tab.id) { it.copy(title = title) }
                }
            }

            setDownloadListener(
                DownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
                    startDownload(url, userAgent, contentDisposition, mimeType)
                }
            )
        }
    }

    private fun startDownload(url: String, userAgent: String, contentDisposition: String, mimeType: String) {
        try {
            val name = URLUtil.guessFileName(url, contentDisposition, mimeType)
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
                .ifBlank { "download" }
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(name)
                .setDescription("Download pelo diasBrowser")
                .setMimeType(mimeType)
                .addRequestHeader("User-Agent", userAgent)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
            Toast.makeText(context, "Download iniciado: $name", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "Não foi possível iniciar o download.", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateTab(id: Int, transform: (BrowserTab) -> BrowserTab) {
        val index = tabs.indexOfFirst { it.id == id }
        if (index >= 0) tabs[index] = transform(tabs[index])
    }
}
