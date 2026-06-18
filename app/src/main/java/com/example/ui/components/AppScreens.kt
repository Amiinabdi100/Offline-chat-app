package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.database.Message
import com.example.data.database.MyProfile
import com.example.data.database.Peer
import com.example.data.repository.ChatRepository
import com.example.localization.LanguageCode
import com.example.localization.Translations
import com.example.security.EncryptionHelper
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BeautifulButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    glow: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "beautiful_btn_glow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (glow) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .scale(scale)
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp, 
            color = if (enabled) containerColor.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun AppOnboardingScreen(
    viewModel: ChatViewModel,
    profile: MyProfile?,
    modifier: Modifier = Modifier
) {
    var rawLang by remember { mutableStateOf(LanguageCode.SO) }
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedAvatarIndex by remember { mutableIntStateOf(1) }

    fun t(key: String): String = Translations.translate(key, rawLang)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative Hero Banner
        Image(
            painter = painterResource(id = R.drawable.amiin_offline_hero_banner),
            contentDescription = "Onboarding banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.32f)
                .align(Alignment.TopCenter)
        )

        // Overlay Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
                .align(Alignment.TopCenter)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .testTag("onboarding_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = t("app_name"),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = t("tagline"),
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = t("developer"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }

                // Language Selection Buttons
                item {
                    Text(
                        text = "Preferred Language / Dooro Luuqadda",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val soSelected = rawLang == LanguageCode.SO
                        Button(
                            onClick = { rawLang = LanguageCode.SO },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("select_so_btn"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (soSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                contentColor = if (soSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp, 
                                if (soSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        ) {
                            if (soSelected) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Somali (SO)", fontWeight = FontWeight.Bold)
                        }
                        
                        val enSelected = rawLang == LanguageCode.EN
                        Button(
                            onClick = { rawLang = LanguageCode.EN },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("select_en_btn"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (enSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                contentColor = if (enSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp, 
                                if (enSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        ) {
                            if (enSelected) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("English (EN)", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Profile Configuration Title
                item {
                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = t("profile_title"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Avatar Grid Selector
                item {
                    Text(
                        text = t("profile_select_avatar"),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 0..5) {
                            val isSelected = selectedAvatarIndex == i
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else Color.Transparent
                                    )
                                    .clickable { selectedAvatarIndex = i }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AvatarHelper.AvatarView(
                                    avatarIndex = i,
                                    size = 38.dp
                                )
                            }
                        }
                    }
                }

                // Input fields
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(t("profile_name")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_name_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text(t("profile_nickname")) },
                        singleLine = true,
                        placeholder = { Text("@name") },
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_nick_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text(t("profile_bio")) },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_bio_input")
                    )
                }

                // Start button
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    BeautifulButton(
                        onClick = {
                            viewModel.completeOnboarding(
                                name = name,
                                nickname = if (nickname.startsWith("@")) nickname else "@$nickname",
                                bio = bio,
                                avatarIndex = selectedAvatarIndex,
                                language = rawLang
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_start_button"),
                        icon = Icons.Filled.Send,
                        text = if (rawLang == LanguageCode.SO) "Bilow Chat-ka Sugan" else "Start Safe Chat"
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveMeshBanner(
    viewModel: ChatViewModel,
    t: (String) -> String,
    modifier: Modifier = Modifier
) {
    val radarRunning by viewModel.isScanningRadar.collectAsStateWithLifecycle()
    val rawLang by viewModel.preferredLanguage.collectAsStateWithLifecycle()
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("mesh_status_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (radarRunning) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        shape = CircleShape
                    )
            )

            Text(
                text = if (radarRunning) {
                    if (rawLang == LanguageCode.SO) "Wi-Fi Direct & Bluetooth waa firfircoon yihiin" 
                    else "Wi-Fi Direct & Bluetooth Active"
                } else {
                    if (rawLang == LanguageCode.SO) "Habka Scan-ka waa u diyaarsan yahay" 
                    else "Mesh offline channel ready"
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "EN / SO",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun MainAppNavigation(
    viewModel: ChatViewModel,
    profile: MyProfile,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val rawLang by viewModel.preferredLanguage.collectAsStateWithLifecycle()
    val activeChatId by viewModel.activeChatPeerId.collectAsStateWithLifecycle()
    
    fun t(key: String): String = Translations.translate(key, rawLang)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (activeChatId == null) {
                Column {
                    MainAppTopBar(viewModel, { t(it) })
                    if (activeTab == Screen.Chats || activeTab == Screen.Nearby) {
                        ActiveMeshBanner(viewModel, { t(it) })
                    }
                }
            }
        },
        bottomBar = {
            if (activeChatId == null) {
                NavigationBar(
                    windowInsets = WindowInsets.navigationBars,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == Screen.Chats,
                        onClick = { viewModel.setTab(Screen.Chats) },
                        icon = { Icon(Icons.Filled.Chat, contentDescription = t("tab_chats")) },
                        label = { Text(t("tab_chats"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.testTag("tab_chats_item")
                    )
                    NavigationBarItem(
                        selected = activeTab == Screen.Nearby,
                        onClick = { viewModel.setTab(Screen.Nearby) },
                        icon = { Icon(Icons.Filled.Wifi, contentDescription = t("tab_nearby")) },
                        label = { Text(t("tab_nearby"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.testTag("tab_nearby_item")
                    )
                    NavigationBarItem(
                        selected = activeTab == Screen.Profile,
                        onClick = { viewModel.setTab(Screen.Profile) },
                        icon = { Icon(Icons.Filled.Person, contentDescription = t("tab_profile")) },
                        label = { Text(t("tab_profile"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.testTag("tab_profile_item")
                    )
                    NavigationBarItem(
                        selected = activeTab == Screen.Settings,
                        onClick = { viewModel.setTab(Screen.Settings) },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = t("tab_settings")) },
                        label = { Text(t("tab_settings"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.testTag("tab_settings_item")
                    )
                }
            }
        },
        floatingActionButton = {
            if (activeChatId == null && activeTab == Screen.Chats) {
                FloatingActionButton(
                    onClick = { viewModel.setTab(Screen.Nearby) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("new_chat_fab")
                ) {
                    Icon(Icons.Filled.Wifi, contentDescription = "Add peer")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeChatId != null) {
                // Open active chat overlay details
                ActiveChatThreadOverlay(viewModel, activeChatId!!, { t(it) })
            } else {
                when (activeTab) {
                    Screen.Chats -> ChatsListTab(viewModel, { t(it) })
                    Screen.Nearby -> NearbyRadarTab(viewModel, { t(it) })
                    Screen.Profile -> ProfileConfigTab(viewModel, profile, { t(it) })
                    Screen.Settings -> SettingsPanelTab(viewModel, profile, { t(it) })
                }
            }
            
            // In-app Alert Notifications overlay
            val activeAlert by viewModel.currentAlertMessage.collectAsStateWithLifecycle()
            AnimatedVisibility(
                visible = activeAlert != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                activeAlert?.let { alert ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.clickable { viewModel.dismissAlert() }.testTag("dismissable_toast")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notification icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text(
                                    text = if (rawLang == LanguageCode.SO) alert.somali else alert.english,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = if (rawLang == LanguageCode.SO) "Guji si aad u xirto" else "Tap to dismiss",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = { viewModel.dismissAlert() }) {
                                Icon(Icons.Filled.Close, contentDescription = "Close alert")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppTopBar(
    viewModel: ChatViewModel,
    t: (String) -> String
) {
    var searchOpen by remember { mutableStateOf(false) }
    val searchQuery by viewModel.messageSearchQuery.collectAsStateWithLifecycle()
    val radarRunning by viewModel.isScanningRadar.collectAsStateWithLifecycle()

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = {
            if (searchOpen) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchMessages(it) },
                    placeholder = { Text(t("search_placeholder"), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("menu_search_input")
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Wifi, contentDescription = "Amiin logo", tint = Color.White)
                    }
                    Column {
                        Text(
                            text = t("app_name"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = t("developer"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        },
        actions = {
            val currentTheme by viewModel.currentThemeMode.collectAsStateWithLifecycle()
            IconButton(onClick = { 
                searchOpen = !searchOpen 
                if (!searchOpen) { viewModel.searchMessages("") }
            }) {
                Icon(
                    imageVector = if (searchOpen) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription = "Search offline chat"
                )
            }
            IconButton(onClick = { 
                val nextTheme = if (currentTheme == "DARK") "LIGHT" else "DARK"
                viewModel.updateThemeMode(nextTheme)
            }) {
                Icon(
                    imageVector = if (currentTheme == "DARK") Icons.Filled.WbSunny else Icons.Filled.Star,
                    contentDescription = "Toggle Theme"
                )
            }
            IconButton(onClick = { viewModel.toggleRadarScan() }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Toggle Scan",
                    tint = if (radarRunning) Color.Green else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}

@Composable
fun ChatsListTab(
    viewModel: ChatViewModel,
    t: (String) -> String
) {
    val peers by viewModel.allPeers.collectAsStateWithLifecycle()
    val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val query by viewModel.messageSearchQuery.collectAsStateWithLifecycle()
    
    // Process active discussions
    val activePeersWithMessages = remember(peers, allMessages, query) {
        val peersMap = peers.associateBy { it.peerId }
        val uniqueTalkedPeerIds = allMessages.filter { it.chatType == "PRIVATE" }.map { it.peerId }.distinct()
        
        val chats = mutableListOf<ChatRowItem>()
        
        // Always add local Group Chat room at the top
        val groupMsgs = allMessages.filter { it.peerId == ChatRepository.GROUP_ID }
        val lastGroupMsg = groupMsgs.lastOrNull()
        if (query.isBlank() || lastGroupMsg?.content?.contains(query, ignoreCase = true) == true) {
            chats.add(
                ChatRowItem(
                    peerId = ChatRepository.GROUP_ID,
                    displayName = if (t("app_name").contains("Amiin")) "Local Group Chat / Sheekada Kooxda" else "Offline Local Room",
                    displayNickname = "@regional_mesh",
                    bio = "Decentralized mesh room loop.",
                    avatarIndex = 6, // Regal Purple for group
                    lastMessageText = lastGroupMsg?.content ?: "Send voice notes, files or texts to nearby devices.",
                    lastMessageTime = lastGroupMsg?.timestamp ?: System.currentTimeMillis() - 3600000,
                    isConnected = true,
                    unreadCount = 0
                )
            )
        }

        for (pId in uniqueTalkedPeerIds) {
            val peer = peersMap[pId] ?: continue
            val peerMsgs = allMessages.filter { it.peerId == pId && it.chatType == "PRIVATE" }
            val lastMsg = peerMsgs.lastOrNull() ?: continue
            
            chats.add(
                ChatRowItem(
                    peerId = pId,
                    displayName = peer.name,
                    displayNickname = peer.nickname,
                    bio = peer.bio,
                    avatarIndex = peer.avatarIndex,
                    lastMessageText = lastMsg.content,
                    lastMessageTime = lastMsg.timestamp,
                    isConnected = peer.connectionStatus == "CONNECTED",
                    unreadCount = if (peer.isFavorite) 1 else 0
                )
            )
        }

        // Also add peers that are currently CONNECTED even if we haven't typed yet
        for (peer in peers) {
            if (peer.connectionStatus == "CONNECTED" && chats.none { it.peerId == peer.peerId }) {
                chats.add(
                    ChatRowItem(
                        peerId = peer.peerId,
                        displayName = peer.name,
                        displayNickname = peer.nickname,
                        bio = peer.bio,
                        avatarIndex = peer.avatarIndex,
                        lastMessageText = "Tap to initialize offline AES negotiation.",
                        lastMessageTime = peer.lastActiveTime,
                        isConnected = true,
                        unreadCount = 0
                    )
                )
            }
        }

        chats.sortByDescending { it.lastMessageTime }
        chats
    }

    if (activePeersWithMessages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = "Radar empty",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    text = t("empty_chats_title"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = t("empty_chats_desc"),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
                BeautifulButton(
                    onClick = { viewModel.setTab(Screen.Nearby) },
                    modifier = Modifier.padding(top = 8.dp).testTag("go_nearby_empty"),
                    icon = Icons.Filled.Search,
                    text = t("action_scan")
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
        ) {
            // Header
            item {
                Text(
                    text = if (query.isNotBlank()) "Search Results / Natiijada Baaritaanka" else "Active Channels / Kanaalada Firfircoon",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            items(activePeersWithMessages, key = { it.peerId }) { chat ->
                val key = EncryptionHelper.getSessionKeyForPeer(chat.peerId)
                val isEncrypted = !chat.lastMessageText.startsWith("Tap to") && !chat.lastMessageText.startsWith("Send voice")
                val cleanPreview = if (isEncrypted) {
                    EncryptionHelper.decrypt(chat.lastMessageText, key)
                } else {
                    chat.lastMessageText
                }

                val isGroup = chat.peerId == ChatRepository.GROUP_ID
                val isConnected = chat.isConnected
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { viewModel.openChat(chat.peerId) }
                        .testTag("chat_card_${chat.peerId}"),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isGroup) 1.5.dp else 1.dp,
                        if (isGroup) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isGroup) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else if (isConnected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        AvatarHelper.AvatarView(
                            avatarIndex = chat.avatarIndex,
                            size = 52.dp
                        )

                        Column(modifier = Modifier.weight(1.5f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = chat.displayName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (chat.peerId == ChatRepository.GROUP_ID) {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text("P2P MESH", fontSize = 9.sp, color = Color.White)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (chat.isConnected) Color.Green else Color.Gray)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = chat.displayNickname,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isEncrypted) {
                                    Icon(Icons.Filled.Lock, contentDescription = "cipher", modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                                Text(
                                    text = cleanPreview,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            Text(
                                text = timeFormat.format(Date(chat.lastMessageTime)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            if (chat.unreadCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("★", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ChatRowItem(
    val peerId: String,
    val displayName: String,
    val displayNickname: String,
    val bio: String,
    val avatarIndex: Int,
    val lastMessageText: String,
    val lastMessageTime: Long,
    val isConnected: Boolean,
    val unreadCount: Int
)

@Composable
fun NearbyRadarTab(
    viewModel: ChatViewModel,
    t: (String) -> String
) {
    val peers by viewModel.allPeers.collectAsStateWithLifecycle()
    val radarRunning by viewModel.isScanningRadar.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // RADAR DECORATION SWEEP CANVAS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            RadarSonarAnimation(isScanning = radarRunning)
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = "Sweep pointer",
                    tint = if (radarRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = if (radarRunning) "RADAR ACTIVE / WAAR BAARAYAA" else "RADAR IDLE / RADARKU WUU JIRAA",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = if (radarRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }

        // Radar Scanning Toggle button
        BeautifulButton(
            onClick = { viewModel.toggleRadarScan() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("radar_toggle_btn"),
            icon = if (radarRunning) Icons.Filled.Refresh else Icons.Filled.Search,
            text = if (radarRunning) t("action_stop_scan") else t("action_scan"),
            containerColor = if (radarRunning) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
            glow = radarRunning
        )

        Divider()

        // Scan peers list
        val filteredPeers = peers.filter { !it.isBlocked }
        if (filteredPeers.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = "empty list", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                        Text(t("empty_peers_title"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(t("empty_peers_desc"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center)
                    }
                }
                
                // Keep the security banner visible even if list is empty
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("security_notice_card_empty"),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Shield Security icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "End-to-End Encryption: Secured peer-to-peer and never touch the internet.",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Xog-qarsoodi: Farriimuhu waa kuwo qarsoon oo dhex mara qalabka oo kaliya.",
                                style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text(
                        text = t("all_users"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(filteredPeers, key = { it.peerId }) { peer ->
                    val isConnected = peer.connectionStatus == "CONNECTED"
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("peer_row_${peer.peerId}"),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isConnected) 1.5.dp else 1.dp,
                            if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isConnected) MaterialTheme.colorScheme.secondaryContainer 
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AvatarHelper.AvatarView(avatarIndex = peer.avatarIndex, size = 44.dp)

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = peer.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (peer.isFavorite) {
                                        Icon(Icons.Filled.Star, contentDescription = "fav", tint = Color.Yellow, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text(peer.nickname, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(peer.bio, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                
                                // Quality percentage bar
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Signal: ${peer.connectionQuality}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    Box(
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color.Gray.copy(alpha = 0.3f))
                                    ) {
                                        val color = when {
                                            peer.connectionQuality > 75 -> Color.Green
                                            peer.connectionQuality > 40 -> Color.Yellow
                                            else -> Color.Red
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(peer.connectionQuality / 100f)
                                                .background(color)
                                        )
                                    }
                                }
                            }

                            // Interactive Connect/Disconnect Button
                            when (peer.connectionStatus) {
                                "DISCONNECTED" -> {
                                    Button(
                                        onClick = { viewModel.connectPeer(peer.peerId) },
                                        modifier = Modifier.testTag("connect_btn_${peer.peerId}").height(38.dp),
                                        shape = RoundedCornerShape(19.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Filled.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(t("action_connect"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                "CONNECTING" -> {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                                }
                                "CONNECTED" -> {
                                    OutlinedButton(
                                        onClick = { viewModel.disconnectPeer(peer.peerId) },
                                        modifier = Modifier.testTag("disconnect_btn_${peer.peerId}").height(38.dp),
                                        shape = RoundedCornerShape(19.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error,
                                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Filled.LinkOff, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(t("action_disconnect"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Security notice at the end of the scroll list
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("security_notice_card"),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = "Shield Security icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "End-to-End Encryption: Secured peer-to-peer and never touch the internet.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Xog-qarsoodi: Farriimuhu waa kuwo qarsoon oo dhex mara qalabka oo kaliya.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RadarSonarAnimation(isScanning: Boolean) {
    if (!isScanning) return
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    
    val pulseRadius1 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "p1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pa1"
    )

    val pulseRadius2 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1250)
        ), label = "p2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1250)
        ), label = "pa2"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rot"
    )

    val sonarColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        
        // Dynamic expanding wireless ripples
        drawCircle(
            color = sonarColor,
            radius = pulseRadius1,
            center = center,
            alpha = pulseAlpha1,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
        drawCircle(
            color = sonarColor,
            radius = pulseRadius2,
            center = center,
            alpha = pulseAlpha2,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )

        // Concentric stationary grid rings
        for (radius in listOf(60.dp.toPx(), 120.dp.toPx(), 180.dp.toPx())) {
            drawCircle(
                color = sonarColor.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
            )
        }

        // Concentric spinning radar sweep line
        val angleRad = Math.toRadians(rotationAngle.toDouble())
        val endX = center.x + 250.dp.toPx() * cos(angleRad).toFloat()
        val endY = center.y + 250.dp.toPx() * sin(angleRad).toFloat()
        
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(sonarColor, Color.Transparent),
                start = center,
                end = Offset(endX, endY)
            ),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 4.dp.toPx()
        )
    }
}

@Composable
fun ProfileConfigTab(
    viewModel: ChatViewModel,
    profile: MyProfile,
    t: (String) -> String
) {
    var name by remember(profile) { mutableStateOf(profile.name) }
    var nickname by remember(profile) { mutableStateOf(profile.nickname) }
    var bio by remember(profile) { mutableStateOf(profile.bio) }
    var selectedAvatarIndex by remember(profile) { mutableIntStateOf(profile.avatarIndex) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = t("profile_title"),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(4.dp))
        
        AvatarHelper.AvatarView(avatarIndex = selectedAvatarIndex, size = 90.dp)
        
        Text(
            text = t("profile_select_avatar"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )

        // Selector buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0..5) {
                val isSelected = selectedAvatarIndex == i
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .clickable { selectedAvatarIndex = i }
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarHelper.AvatarView(avatarIndex = i, size = 35.dp)
                }
            }
        }

        Divider()

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(t("profile_name")) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("profile_name_edit")
        )

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text(t("profile_nickname")) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("profile_nick_edit")
        )

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text(t("profile_bio")) },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("profile_bio_edit")
        )

        Spacer(modifier = Modifier.weight(1f))

        BeautifulButton(
            onClick = {
                viewModel.updateProfile(
                    name = name,
                    nickname = if (nickname.startsWith("@")) nickname else "@$nickname",
                    bio = bio,
                    avatarIndex = selectedAvatarIndex
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_profile_btn"),
            icon = Icons.Filled.Check,
            text = t("action_save"),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun SettingsPanelTab(
    viewModel: ChatViewModel,
    profile: MyProfile,
    t: (String) -> String
) {
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val rawLang by viewModel.preferredLanguage.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = t("settings_title"),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )

        // Row for language options
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = t("settings_language"),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val soSelected = rawLang == LanguageCode.SO
                    Button(
                        onClick = { viewModel.updateLanguage(LanguageCode.SO) },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("settings_so_btn"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (soSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (soSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp, 
                            if (soSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    ) {
                        if (soSelected) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text("Somali (SO)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    val enSelected = rawLang == LanguageCode.EN
                    Button(
                        onClick = { viewModel.updateLanguage(LanguageCode.EN) },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("settings_en_btn"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (enSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (enSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp, 
                            if (enSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    ) {
                        if (enSelected) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text("English (EN)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Row for Light/Dark themes
        val currentTheme by viewModel.currentThemeMode.collectAsStateWithLifecycle()
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = t("settings_theme"),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val lightSelected = currentTheme == "LIGHT"
                    Button(
                        onClick = { viewModel.updateThemeMode("LIGHT") },
                        modifier = Modifier.weight(1f).height(46.dp).testTag("settings_light_btn"),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lightSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (lightSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, 
                            if (lightSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Filled.WbSunny, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("settings_theme_light"), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    val darkSelected = currentTheme == "DARK"
                    Button(
                        onClick = { viewModel.updateThemeMode("DARK") },
                        modifier = Modifier.weight(1f).height(46.dp).testTag("settings_dark_btn"),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (darkSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (darkSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, 
                            if (darkSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("settings_theme_dark"), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    val sysSelected = currentTheme == "SYSTEM"
                    Button(
                        onClick = { viewModel.updateThemeMode("SYSTEM") },
                        modifier = Modifier.weight(1.1f).height(46.dp).testTag("settings_system_btn"),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (sysSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (sysSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, 
                            if (sysSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        val lbl = if (rawLang == LanguageCode.SO) "Nidaamka" else "System"
                        Text(lbl, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }

        // Settings actions trigger items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ListItem(
                headlineContent = { Text(t("settings_edit_profile")) },
                leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.setTab(Screen.Profile) }
            )

            ListItem(
                headlineContent = { Text(t("settings_about")) },
                supportingContent = { Text(t("developer"), style = MaterialTheme.typography.labelSmall) },
                leadingContent = { Icon(Icons.Filled.Info, contentDescription = null) },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showAboutDialog = true }
                    .testTag("about_dialog_trigger")
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text(t("settings_clear_history"), color = MaterialTheme.colorScheme.error) },
                supportingContent = { Text(t("settings_clear_history_desc"), style = MaterialTheme.typography.labelSmall) },
                leadingContent = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showClearHistoryDialog = true }
                    .testTag("clear_history_trigger")
            )
        }

        // Interactive Offline Guide Section in Somali & English
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "education",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = t("guide_title"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = t("guide_headline"),
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Guide Topic 1: How connection works
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhoneAndroid,
                            contentDescription = "Bluetooth",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = t("guide_how_works_title"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = t("guide_how_works_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Guide Topic 2: Security & Encryption setup
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Encryption",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = t("guide_security_title"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = t("guide_security_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Guide Topic 3: Distance range & coverage area
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Range",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = t("guide_range_title"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = t("guide_range_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Guide Topic 4: Connection Troubleshooting & Fixes
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Build,
                            contentDescription = "Troubleshoot",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = t("guide_trouble_title"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = t("guide_trouble_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (showClearHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = { Text(t("action_clear_history")) },
                text = { Text(t("settings_clear_history_desc")) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllHistoryAndCachedPeers()
                            showClearHistoryDialog = false
                        },
                        modifier = Modifier.testTag("confirm_clear_btn")
                    ) {
                        Text(t("action_confirm"), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text(t("action_cancel"))
                    }
                }
            )
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text(t("settings_about")) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Amiin Offline Chat", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = t("developer"), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        HorizontalDivider()
                        Text(text = t("about_description"))
                        Text(text = t("about_notice"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = t("about_copyright"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showAboutDialog = false }, 
                        modifier = Modifier.testTag("about_dismiss_btn").height(38.dp),
                        shape = RoundedCornerShape(19.dp)
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ActiveChatThreadOverlay(
    viewModel: ChatViewModel,
    peerId: String,
    t: (String) -> String
) {
    val peers by viewModel.allPeers.collectAsStateWithLifecycle()
    val rawMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val activeLang by viewModel.preferredLanguage.collectAsStateWithLifecycle()

    val peer = remember(peers, peerId) {
        peers.find { it.peerId == peerId } ?: Peer(peerId, "Local Room", "@regional", "Local room loop", 6, connectionStatus = "CONNECTED")
    }

    // Filter relevant messages
    val threadMessages = remember(rawMessages, peerId) {
        if (peerId == ChatRepository.GROUP_ID) {
            rawMessages.filter { it.chatType == "GROUP" }
        } else {
            rawMessages.filter { it.peerId == peerId && it.chatType == "PRIVATE" }
        }
    }.sortedBy { it.timestamp }

    var textInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Secure payload review inspection state
    var inspectingMessagePayload by remember { mutableStateOf<Message?>(null) }
    
    var showingAttachmentSheet by remember { mutableStateOf(false) }
    var showingEmojiDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { viewModel.openChat(null) }, modifier = Modifier.testTag("chat_back_btn")) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }

                    AvatarHelper.AvatarView(avatarIndex = peer.avatarIndex, size = 42.dp)

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = peer.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (peer.connectionStatus == "CONNECTED") Color.Green else Color.Gray)
                            )
                            val stat = when (peer.connectionStatus) {
                                "CONNECTED" -> t("status_connected")
                                "CONNECTING" -> t("status_connecting")
                                else -> t("status_disconnected")
                            }
                            Text(
                                text = "$stat • Signal ${peer.connectionQuality}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Header actions
                    if (peerId != ChatRepository.GROUP_ID) {
                        IconButton(onClick = { viewModel.toggleFavorite(peer.peerId, peer.isFavorite) }) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Favorite",
                                tint = if (peer.isFavorite) Color.Yellow else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(onClick = { viewModel.toggleBlock(peer.peerId, peer.isBlocked) }) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Block",
                                tint = if (peer.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    IconButton(onClick = { viewModel.clearChatWithPeer(peerId) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Clear Chat")
                    }
                }
                
                if (peer.isBlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (activeLang == LanguageCode.SO) "Waa xanniban tahay! Ma diri kartid fariimo." else "User is blocked! You cannot send or receive messages.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = "Locked", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = t("about_notice"),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(threadMessages, key = { it.id }) { msg ->
                    val key = EncryptionHelper.getSessionKeyForPeer(peerId)
                    val decryptedBody = remember(msg.content, key) {
                        if (msg.isEncrypted) EncryptionHelper.decrypt(msg.content, key) else msg.content
                    }

                    val rowArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
                    val bubbleColor = if (msg.isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    val textColor = if (msg.isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = rowArrangement,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (!msg.isMe) {
                            AvatarHelper.AvatarView(avatarIndex = peer.avatarIndex, size = 32.dp, modifier = Modifier.padding(end = 6.dp))
                        }

                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp, 
                                topEnd = 16.dp, 
                                bottomStart = if (msg.isMe) 16.dp else 4.dp, 
                                bottomEnd = if (msg.isMe) 4.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(containerColor = bubbleColor),
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clickable { inspectingMessagePayload = msg }
                                .testTag("message_bubble_${msg.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (!msg.isMe && peerId == ChatRepository.GROUP_ID) {
                                    Text(
                                        text = "${msg.senderName} (${msg.senderNickname})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }

                                if (msg.mediaType == "IMAGE") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.DarkGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = "image icon", tint = Color.LightGray, modifier = Modifier.size(42.dp))
                                        Text("OFFLINE IMAGE", style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp), color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                } else if (msg.mediaType == "DOCUMENT") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Gray.copy(alpha = 0.2f))
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = "document", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.error)
                                        Text("DOC_OFFLINE.pdf", maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                } else if (msg.mediaType == "VOICE") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Gray.copy(alpha = 0.2f))
                                            .padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "play voice", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(4.dp)
                                                .background(Color.LightGray)
                                        ) {
                                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.6f).background(MaterialTheme.colorScheme.primary))
                                        }
                                        Text("0:12", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Text(
                                    text = decryptedBody,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))
                                    Text(
                                        text = formattedTime,
                                        fontSize = 9.sp,
                                        color = textColor.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    if (msg.isMe) {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = msg.deliveryStatus,
                                            tint = textColor.copy(alpha = 0.7f),
                                            modifier = Modifier.size(11.dp)
                                        )
                                    } else {
                                        Icon(Icons.Filled.Lock, contentDescription = "secure", tint = textColor.copy(alpha = 0.6f), modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    if (showingAttachmentSheet) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AttachmentColumn(Icons.Filled.Share, "Attach Photo", "Sawir", Color(0xFF4CAF50)) {
                                viewModel.sendOfflineAttachment(peerId, "IMAGE", "Beautiful Horizon")
                                showingAttachmentSheet = false
                            }
                            AttachmentColumn(Icons.Filled.Share, "Doc File", "Fayl", Color(0xFF2196F3)) {
                                viewModel.sendOfflineAttachment(peerId, "DOCUMENT", "Amiin_Offline_Whitepaper.pdf")
                                showingAttachmentSheet = false
                            }
                            AttachmentColumn(Icons.Filled.Share, "Voice rec", "Cod", Color(0xFFFF9800)) {
                                viewModel.sendOfflineAttachment(peerId, "VOICE", "Microphone voice note")
                                showingAttachmentSheet = false
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    }

                    if (showingEmojiDialog) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            listOf("😀", "😂", "👍", "🇸🇴", "❤️", "📍", "📡", "🔑").forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .clickable { textInput += emoji }
                                        .padding(4.dp)
                                )
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { showingAttachmentSheet = !showingAttachmentSheet }) {
                            Icon(Icons.Filled.Add, contentDescription = "Attach details")
                        }

                        IconButton(onClick = { showingEmojiDialog = !showingEmojiDialog }) {
                            Icon(Icons.Filled.Face, contentDescription = "Emojis")
                        }

                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text(if (activeLang == LanguageCode.SO) "Qor fariinta..." else "Type message...") },
                            maxLines = 3,
                            enabled = !peer.isBlocked,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_input_box"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (textInput.isNotBlank()) {
                                        viewModel.sendTextMessage(peerId, textInput)
                                        textInput = ""
                                        keyboardController?.hide()
                                    }
                                }
                            )
                        )

                        Button(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    viewModel.sendTextMessage(peerId, textInput)
                                    textInput = ""
                                    keyboardController?.hide()
                                }
                            },
                            enabled = textInput.isNotBlank() && !peer.isBlocked,
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp).testTag("send_btn"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = t("action_send"), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (inspectingMessagePayload != null) {
        val msg = inspectingMessagePayload!!
        val sessionKey = EncryptionHelper.getSessionKeyForPeer(peerId)
        
        AlertDialog(
            onDismissRequest = { inspectingMessagePayload = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "secure lock", tint = Color.Green)
                    Text(t("sec_payload_title"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${t("sec_payload_algo")}: AES-128 Symmetric Cipher Block",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${t("sec_payload_key")}: $sessionKey",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall
                    )
                    
                    Divider()

                    Text(
                        text = t("sec_payload_original"),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    val decrypted = EncryptionHelper.decrypt(msg.content, sessionKey)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = decrypted,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = t("sec_payload_encrypted"),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = msg.content,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    Text(
                        text = if (activeLang == LanguageCode.SO) "Aalladan waxay isaga dirtaa fariimaha deegaanka iyadoo xogta la qariyay." else "Amiin Offline utilizes local radio packets encrypted as shown above to safeguard information from eavesdropping.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { inspectingMessagePayload = null },
                    modifier = Modifier.testTag("payload_dismiss_btn").height(38.dp),
                    shape = RoundedCornerShape(19.dp)
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun AttachmentColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    labelEn: String,
    labelSo: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Text(labelSo, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(labelEn, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 9.sp)
    }
}
