package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.database.MyProfile
import com.example.data.database.OfflineDatabase
import com.example.data.database.Peer
import com.example.data.database.Message
import com.example.data.repository.ChatRepository
import com.example.localization.LanguageCode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database: OfflineDatabase by lazy {
        Room.databaseBuilder(
            application,
            OfflineDatabase::class.java,
            "amiin_offline_chat_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    val repository: ChatRepository by lazy {
        ChatRepository(
            messageDao = database.messageDao(),
            peerDao = database.peerDao(),
            profileDao = database.profileDao(),
            scope = viewModelScope
        )
    }

    // Modern M3 Selected Tab & Nav Controller states
    val activeTab = MutableStateFlow(Screen.Chats)
    val activeChatPeerId = MutableStateFlow<String?>(null)
    
    // Scan radar state
    val isScanningRadar = MutableStateFlow(false)
    
    // Message search query state
    val messageSearchQuery = MutableStateFlow("")
    
    // In-app banner notifications state for alerts
    val currentAlertMessage = MutableStateFlow<AlertEvent?>(null)

    // Reactive streams
    val allPeers: StateFlow<List<Peer>> = repository.allPeers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<MyProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMessages: StateFlow<List<Message>> = messageSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allMessages
            } else {
                database.messageDao().searchMessages(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Pre-populate data on start
        viewModelScope.launch {
            repository.injectSimulatedPeers()
            
            // Check if profile exists, otherwise create a default profile with firstLaunch
            val current = repository.getProfileDirect()
            if (current == null) {
                repository.insertOrUpdateProfile(
                    MyProfile(
                        name = "Amiin Cabdi User",
                        nickname = "@amiincabdi_peer",
                        bio = "Sharing and chatting without internet safely.",
                        avatarIndex = 1,
                        preferredLanguage = "SO", // Default to Somali!
                        themeMode = "SYSTEM",
                        isFirstLaunch = true
                    )
                )
            }
        }
    }

    // Safe getters for configurations
    val preferredLanguage: StateFlow<LanguageCode> = userProfile
        .map { profile ->
            try {
                LanguageCode.valueOf(profile?.preferredLanguage ?: "SO")
            } catch (e: Exception) {
                LanguageCode.SO
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LanguageCode.SO)

    val currentThemeMode: StateFlow<String> = userProfile
        .map { it?.themeMode ?: "SYSTEM" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    fun setTab(screen: Screen) {
        activeTab.value = screen
        activeChatPeerId.value = null // reset chat channel
    }

    fun openChat(peerId: String?) {
        activeChatPeerId.value = peerId
    }

    fun toggleRadarScan() {
        val next = !isScanningRadar.value
        isScanningRadar.value = next
        if (next) {
            repository.startRadarScanSimulation()
            postAlert("Radar initialized. Discovering and advertising on local networks.", "Skaner deegaan wuu bilaabmay. Maqal baarista ayaa firfircoon.")
        } else {
            repository.stopRadarScanSimulation()
        }
    }

    fun updateLanguage(lang: LanguageCode) {
        viewModelScope.launch {
            val current = repository.getProfileDirect() ?: MyProfile(avatarIndex = 1)
            repository.insertOrUpdateProfile(current.copy(preferredLanguage = lang.name, isFirstLaunch = false))
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            val current = repository.getProfileDirect() ?: MyProfile(avatarIndex = 1)
            repository.insertOrUpdateProfile(current.copy(themeMode = mode))
        }
    }

    fun completeOnboarding(name: String, nickname: String, bio: String, avatarIndex: Int, language: LanguageCode) {
        viewModelScope.launch {
            repository.insertOrUpdateProfile(
                MyProfile(
                    name = name.ifBlank { "Amiin User" },
                    nickname = nickname.ifBlank { "@user_p2p" },
                    bio = bio.ifBlank { "Offline Communicator" },
                    avatarIndex = avatarIndex,
                    preferredLanguage = language.name,
                    themeMode = "SYSTEM",
                    isFirstLaunch = false
                )
            )
            postAlert("Welcome to Amiin Offline Chat! Your profile is ready.", "Ku soo dhowow Amiin Offline Chat! Astaantaada waa diyaar.")
        }
    }

    fun updateProfile(name: String, nickname: String, bio: String, avatarIndex: Int) {
        viewModelScope.launch {
            val current = repository.getProfileDirect() ?: MyProfile(avatarIndex = 1)
            repository.insertOrUpdateProfile(
                current.copy(
                    name = name,
                    nickname = nickname,
                    bio = bio,
                    avatarIndex = avatarIndex
                )
            )
            postAlert("Profile updated successfully!", "Guul: Profile-ka waa la cusbooneysiiyay!")
        }
    }

    fun sendTextMessage(peerId: String, content: String) {
        viewModelScope.launch {
            if (content.isBlank()) return@launch
            repository.sendMessage(peerId, content, if (peerId == ChatRepository.GROUP_ID) "GROUP" else "PRIVATE")
        }
    }

    fun sendOfflineAttachment(peerId: String, mediaType: String, contentDesc: String) {
        viewModelScope.launch {
            val isGroup = if (peerId == ChatRepository.GROUP_ID) "GROUP" else "PRIVATE"
            val placeholderUri = "content://amiin_offline/attachments/" + System.currentTimeMillis()
            
            // Format nice text for sharing logs
            val attachmentMsg = when (mediaType) {
                "IMAGE" -> "Sent local photo: $contentDesc"
                "DOCUMENT" -> "Sent local file: $contentDesc"
                "VOICE" -> "Voice recording shared"
                else -> "Shared rich asset"
            }
            repository.sendMessage(peerId, attachmentMsg, isGroup, mediaType = mediaType, mediaUri = placeholderUri)
            postAlert("Direct offline file transfer completed securely.", "Gudbinta faylka ee direct-ka ah waa la xaqiijiyay oo loo diray si sugan.")
        }
    }

    fun toggleFavorite(peerId: String, currentFav: Boolean) {
        viewModelScope.launch {
            repository.favoritePeer(peerId, !currentFav)
        }
    }

    fun toggleBlock(peerId: String, currentBlocked: Boolean) {
        viewModelScope.launch {
            repository.blockPeer(peerId, !currentBlocked)
            if (!currentBlocked) {
                postAlert("User has been blocked.", "Giriigga: Isticmaalaha waa la xannibay.")
            } else {
                postAlert("User has been unblocked.", "Xannibaaddii waa laga qaaday isticmaalaha.")
            }
        }
    }

    fun connectPeer(peerId: String) {
        viewModelScope.launch {
            repository.connectPeer(peerId)
            val p = database.peerDao().getPeerById(peerId)
            if (p != null) {
                postAlert("Connected securely to ${p.name}.", "Si sugan ayaad ugu xirmay ${p.name}.")
            }
        }
    }

    fun disconnectPeer(peerId: String) {
        viewModelScope.launch {
            repository.disconnectPeer(peerId)
            postAlert("Disconnected.", "Xiriirkii waa kala go'ay.")
        }
    }

    fun clearChatWithPeer(peerId: String) {
        viewModelScope.launch {
            repository.clearHistoryForPeer(peerId)
        }
    }

    fun clearAllHistoryAndCachedPeers() {
        viewModelScope.launch {
            repository.clearAllHistoryAndPeers()
            postAlert("All chat history and peer mappings cleared.", "Diiwaankii wada-sheekaysiga iyo aalladaha waa la tirtiray oo la sifeeyey.")
        }
    }

    fun searchMessages(query: String) {
        messageSearchQuery.value = query
    }

    private fun postAlert(enMsg: String, soMsg: String) {
        currentAlertMessage.value = AlertEvent(enMsg, soMsg)
    }

    fun dismissAlert() {
        currentAlertMessage.value = null
    }
}

enum class Screen {
    Chats, Nearby, Profile, Settings
}

data class AlertEvent(val english: String, val somali: String)
