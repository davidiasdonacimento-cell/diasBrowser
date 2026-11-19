package com.diasbrowser.app

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

private val Midnight = Color(0xFF080D1A)
private val Panel = Color(0xFF10182A)
private val PanelRaised = Color(0xFF19243A)
private val Cyan = Color(0xFF62D9FF)
private val Purple = Color(0xFFB69CFF)
private val TextMain = Color(0xFFEAF3FF)
private val TextMuted = Color(0xFF9BAEC8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserApp(controller: BrowserController, store: BrowserStore) {
    var address by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showTabs by remember { mutableStateOf(false) }
    var library by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var refreshLibrary by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current
    val activeTab = controller.activeTab

    LaunchedEffect(controller.activeTabId, activeTab.url) {
        address = activeTab.url
    }

    fun openAddress() {
        if (controller.navigate(address)) focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Midnight)
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 12.dp, top = 12.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(38.dp), shape = RoundedCornerShape(13.dp), color = Color.Transparent) {
                Box(
                    modifier = Modifier.background(Brush.linearGradient(listOf(Cyan, Purple, Color(0xFFFF72C6)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("d", color = Midnight, fontSize = 25.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("diasBrowser", color = TextMain, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("Navegue do seu jeito", color = TextMuted, fontSize = 11.sp)
            }
            IconButton(onClick = { showTabs = true }) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(color = PanelRaised, shape = RoundedCornerShape(9.dp), modifier = Modifier.size(31.dp)) {}
                    Text(controller.tabs.size.toString(), color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextMain)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(19.dp)).background(PanelRaised)
                .padding(start = 13.dp, end = 8.dp, top = 3.dp, bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Cyan, modifier = Modifier.size(21.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Pesquisar ou digitar endereço", color = TextMuted, fontSize = 13.sp) },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextMain, fontSize = 14.sp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onGo = { openAddress() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Cyan,
                    focusedTextColor = TextMain,
                    unfocusedTextColor = TextMain
                )
            )
            IconButton(onClick = { openAddress() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Ir", tint = Cyan)
            }
        }

        if (controller.loading || controller.progress in 1..99) {
            LinearProgressIndicator(
                progress = { controller.progress.coerceIn(0, 100) / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(2.dp),
                color = Cyan,
                trackColor = Midnight
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AndroidView(
                factory = { context -> FrameLayout(context).also(controller::attachContainer) },
                update = controller::attachContainer,
                modifier = Modifier.fillMaxSize()
            )
            if (activeTab.url.isBlank() || activeTab.url == "about:blank") {
                StartPage(
                    onSearch = { address = it; controller.navigate(it) },
                    onOpen = { controller.navigate(it) }
                )
            }
        }

        Divider(color = Color(0xFF1B2941), thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF0C1424)).padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomAction("Voltar", enabled = controller.canGoBackNow(), onClick = { controller.goBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            BottomAction("Avançar", enabled = controller.canGoForwardNow(), onClick = { controller.goForward() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
            BottomAction("Início", onClick = { controller.goHome() }) {
                Icon(Icons.Default.Home, contentDescription = null)
            }
            BottomAction("Favorito", onClick = {
                if (activeTab.url.isNotBlank()) {
                    store.addBookmark(activeTab.title, activeTab.url)
                    refreshLibrary++
                }
            }) {
                Icon(Icons.Default.Star, contentDescription = null)
            }
            BottomAction("Nova aba", onClick = { controller.addTab() }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
            BottomAction("Atualizar", onClick = { controller.reload() }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
            }
        }
    }

    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            containerColor = Panel,
            contentColor = TextMain
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
                Text("Menu do navegador", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                MenuRow("Favoritos", "Seus sites salvos", Icons.Default.Bookmark) {
                    showMenu = false; library = "favorites"; refreshLibrary++
                }
                MenuRow("Histórico", "Páginas visitadas neste perfil", Icons.Default.History) {
                    showMenu = false; library = "history"; refreshLibrary++
                }
                MenuRow("Downloads", "Arquivos baixados pelo navegador", Icons.Default.Download) {
                    showMenu = false; library = "downloads"; refreshLibrary++
                }
                MenuRow("Nova aba temporária", "Não registra páginas no histórico local", Icons.Default.Shield) {
                    showMenu = false; controller.addTab(temporary = true)
                }
                MenuRow("Configurações e privacidade", "Informações e controles locais", Icons.Default.Settings) {
                    showMenu = false; showSettings = true
                }
            }
        }
    }

    if (showTabs) {
        AlertDialog(
            onDismissRequest = { showTabs = false },
            containerColor = Panel,
            title = { Text("Abas abertas (${controller.tabs.size})", color = TextMain) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 440.dp)) {
                    items(controller.tabs, key = { it.id }) { tab ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                controller.selectTab(tab.id); showTabs = false
                            }.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Cyan)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(tab.title, color = TextMain, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    if (tab.temporary) "Aba temporária" else tab.url.ifBlank { "Nova aba" },
                                    color = TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(onClick = { controller.closeTab(tab.id) }) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar aba", tint = TextMuted)
                            }
                        }
                        Divider(color = PanelRaised)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { controller.addTab(); showTabs = false }) {
                    Text("Nova aba", color = Cyan)
                }
            }
        )
    }

    if (library != null) {
        val mode = library!!
        val favorites = if (mode == "favorites") store.bookmarks() else emptyList()
        val history = if (mode == "history") store.history() else emptyList()
        val downloads = if (mode == "downloads") controller.downloadEntries() else emptyList()
        AlertDialog(
            onDismissRequest = { library = null },
            containerColor = Panel,
            title = {
                Text(when (mode) {
                    "favorites" -> "Favoritos"
                    "history" -> "Histórico"
                    else -> "Downloads"
                }, color = TextMain)
            },
            text = {
                when (mode) {
                    "favorites", "history" -> {
                        val entries = if (mode == "favorites") favorites else history
                        if (entries.isEmpty()) {
                            Text("Ainda não há itens aqui.", color = TextMuted)
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 440.dp)) {
                                items(entries, key = { it.url }) { entry ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            controller.navigate(entry.url); address = entry.url; library = null
                                        }.padding(vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (mode == "favorites") Icons.Default.Star else Icons.Default.History,
                                            contentDescription = null, tint = Cyan
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(entry.title, color = TextMain, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(entry.url, color = TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        if (mode == "favorites") {
                                            IconButton(onClick = {
                                                store.removeBookmark(entry.url); refreshLibrary++; library = "favorites"
                                            }) {
                                                Icon(Icons.Default.Close, contentDescription = "Remover", tint = TextMuted)
                                            }
                                        }
                                    }
                                    Divider(color = PanelRaised)
                                }
                            }
                        }
                    }
                    else -> {
                        if (downloads.isEmpty()) {
                            Text("Nenhum download encontrado. Os arquivos concluídos ficam na pasta Downloads do Android.", color = TextMuted)
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 440.dp)) {
                                items(downloads) { item ->
                                    Text(item, color = TextMain, modifier = Modifier.padding(vertical = 9.dp))
                                    Divider(color = PanelRaised)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (mode == "history" && history.isNotEmpty()) {
                        TextButton(onClick = { store.clearHistory(); refreshLibrary++; library = "history" }) {
                            Text("Limpar", color = Purple)
                        }
                    }
                    TextButton(onClick = { library = null }) { Text("Fechar", color = Cyan) }
                }
            }
        )
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = Panel,
            title = { Text("Configurações e privacidade", color = TextMain) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tema: Dark Premium", color = TextMain)
                    Text("Buscador padrão: Google", color = TextMain)
                    Text("O navegador usa o WebView do Android e o recurso Safe Browsing quando disponível.", color = TextMuted)
                    Text(
                        "Abas temporárias não são adicionadas ao histórico local. Nesta versão, elas ainda compartilham o perfil de cookies do WebView; não equivalem a um modo anônimo isolado.",
                        color = TextMuted, fontSize = 12.sp
                    )
                    Text("Downloads são gerenciados pelo serviço DownloadManager do Android.", color = TextMuted, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) { Text("Entendi", color = Cyan) }
            }
        )
    }
}

@Composable
private fun StartPage(onSearch: (String) -> Unit, onOpen: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Midnight, Color(0xFF111D35), Midnight))
        ).padding(horizontal = 22.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))
        Surface(shape = RoundedCornerShape(27.dp), color = Color.Transparent, modifier = Modifier.size(82.dp)) {
            Box(
                modifier = Modifier.background(Brush.linearGradient(listOf(Cyan, Purple, Color(0xFFFF72C6)))),
                contentAlignment = Alignment.Center
            ) {
                Text("d", color = Midnight, fontSize = 52.sp, fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("diasBrowser", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Descubra a internet.", color = TextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(32.dp))
        Text("Acesso rápido", color = TextMain, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            QuickLink("Google", "Pesquisar", Modifier.weight(1f)) { onOpen("https://www.google.com") }
            QuickLink("YouTube", "Vídeos", Modifier.weight(1f)) { onOpen("https://www.youtube.com") }
            QuickLink("Wikipedia", "Conhecimento", Modifier.weight(1f)) { onOpen("https://www.wikipedia.org") }
        }
        Spacer(Modifier.height(26.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().clickable { onSearch("notícias de tecnologia") }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Cyan)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Pesquise qualquer coisa", color = TextMain, fontWeight = FontWeight.SemiBold)
                    Text("Digite uma palavra ou endereço na barra acima", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Text("Navegação simples • Abas • Favoritos", color = TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun QuickLink(title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Panel)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp, horizontal = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Language, contentDescription = null, tint = Cyan, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(7.dp))
            Text(title, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
            Text(subtitle, color = TextMuted, fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
private fun BottomAction(label: String, enabled: Boolean = true, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(24.dp)) { icon() }
        Text(label, color = if (enabled) TextMuted else Color(0xFF53627A), fontSize = 9.sp)
    }
}

@Composable
private fun MenuRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick)
            .padding(vertical = 13.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = PanelRaised, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(42.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = Cyan) }
        }
        Spacer(Modifier.width(13.dp))
        Column {
            Text(title, color = TextMain, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }
    }
}
