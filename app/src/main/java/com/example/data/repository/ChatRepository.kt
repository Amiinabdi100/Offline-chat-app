package com.example.data.repository

import com.example.data.database.Message
import com.example.data.database.MessageDao
import com.example.data.database.MyProfile
import com.example.data.database.Peer
import com.example.data.database.PeerDao
import com.example.data.database.ProfileDao
import com.example.security.EncryptionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatRepository(
    private val messageDao: MessageDao,
    private val peerDao: PeerDao,
    private val profileDao: ProfileDao,
    private val scope: CoroutineScope
) {
    // Expose all state variables reactively
    val allPeers: Flow<List<Peer>> = peerDao.getAllPeersFlow()
    val userProfile: Flow<MyProfile?> = profileDao.getProfileFlow()
    val allMessages: Flow<List<Message>> = messageDao.getAllMessagesFlow()
    
    // Scan simulation state
    private var isSimulatingRadar = false

    companion object {
        const val GROUP_ID = "GROUP"
    }

    suspend fun getProfileDirect(): MyProfile? = withContext(Dispatchers.IO) {
        profileDao.getProfileDirect()
    }

    suspend fun insertOrUpdateProfile(profile: MyProfile) = withContext(Dispatchers.IO) {
        profileDao.insertOrUpdateProfile(profile)
    }

    fun getPrivateChats(peerId: String): Flow<List<Message>> {
        return messageDao.getPrivateChats(peerId)
    }

    fun getGroupChats(): Flow<List<Message>> {
        return messageDao.getGroupChats()
    }
    
    fun searchMessages(query: String): Flow<List<Message>> {
        return messageDao.searchMessages(query)
    }

    /**
     * Sends a chat message. It encrypts the text content before writing to the database,
     * mimicking secure wireless broadcasting.
     */
    suspend fun sendMessage(
        peerId: String,
        content: String,
        chatType: String,
        mediaType: String = "TEXT",
        mediaUri: String? = null
    ) = withContext(Dispatchers.IO) {
        val myProfile = profileDao.getProfileDirect() ?: MyProfile(name = "Amiin User", nickname = "@user", bio = "Active", avatarIndex = 1)
        val myName = myProfile.name
        val myNickname = myProfile.nickname

        // Encrypt message content with the secure session key for transmission
        val sessionKey = EncryptionHelper.getSessionKeyForPeer(peerId)
        val encryptedContent = EncryptionHelper.encrypt(content, sessionKey)

        val message = Message(
            senderName = myName,
            senderNickname = myNickname,
            content = encryptedContent, // secure storage
            timestamp = System.currentTimeMillis(),
            isMe = true,
            peerId = peerId,
            chatType = chatType,
            mediaUri = mediaUri,
            mediaType = mediaType,
            isEncrypted = true,
            deliveryStatus = "SENT"
        )
        
        messageDao.insertMessage(message)

        // Simulate delivery delay over offline radio
        delay(300)
        messageDao.insertMessage(message.copy(deliveryStatus = "DELIVERED"))

        // Trigger simulation response if sending to a mock connected user or group
        if (peerId == GROUP_ID) {
            triggerGroupReplySimulation(content)
        } else {
            triggerPrivateReplySimulation(peerId, content)
        }
    }

    suspend fun blockPeer(peerId: String, isBlocked: Boolean) = withContext(Dispatchers.IO) {
        peerDao.updateBlocked(peerId, isBlocked)
        if (isBlocked) {
            peerDao.updateConnectionStatus(peerId, "DISCONNECTED")
        }
    }

    suspend fun favoritePeer(peerId: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        peerDao.updateFavorite(peerId, isFavorite)
    }

    suspend fun connectPeer(peerId: String) = withContext(Dispatchers.IO) {
        peerDao.updateConnectionStatus(peerId, "CONNECTING")
        delay(1200) // simulated radio handshake
        peerDao.updateConnectionStatus(peerId, "CONNECTED")
    }

    suspend fun disconnectPeer(peerId: String) = withContext(Dispatchers.IO) {
        peerDao.updateConnectionStatus(peerId, "DISCONNECTED")
    }

    suspend fun clearHistoryForPeer(peerId: String) = withContext(Dispatchers.IO) {
        messageDao.clearPrivateChatsWithPeer(peerId)
    }

    suspend fun clearAllHistoryAndPeers() = withContext(Dispatchers.IO) {
        messageDao.clearAllMessages()
        peerDao.clearAllPeers()
        // Re-inject static simulated peers (disconnected by default)
        injectSimulatedPeers()
    }

    suspend fun injectSimulatedPeers() = withContext(Dispatchers.IO) {
        val staticPeers = listOf(
            Peer("asha_cilmi", "Asha Cilmi", "@Asha_Calm", "Nurse & offline communication enthusiast. Let's chat!", 2, "DISCONNECTED", isFavorite = true),
            Peer("muse_gure", "Muse Gure", "@Muse_Tech", "Local wifi-direct coder. App developed by Amiin Cabdi!", 3, "DISCONNECTED"),
            Peer("deeq_warsame", "Deeq Warsame", "@Deeq_Builder", "Offline mesh networks researcher in Hargeisa.", 4, "DISCONNECTED"),
            Peer("farhiya_cabdi", "Farhiya Cabdi", "@Farhiya_Net", "Student in Garowe. Using Amiin's Offline App without internet.", 5, "DISCONNECTED")
        )
        for (peer in staticPeers) {
            if (peerDao.getPeerById(peer.peerId) == null) {
                peerDao.insertOrUpdatePeer(peer)
            }
        }
    }

    /**
     * Starts background radar scan simulation. Nearby peers start connecting.
     */
    fun startRadarScanSimulation() {
        if (isSimulatingRadar) return
        isSimulatingRadar = true
        scope.launch(Dispatchers.IO) {
            injectSimulatedPeers()

            // Periodically cycle discoverable status of mock peer targets
            val peerIds = listOf("asha_cilmi", "muse_gure", "deeq_warsame", "farhiya_cabdi")
            for (pId in peerIds) {
                if (!isSimulatingRadar) break
                val dbPeer = peerDao.getPeerById(pId)
                if (dbPeer != null && dbPeer.connectionStatus == "DISCONNECTED") {
                    // Update connection quality and show as online
                    peerDao.insertOrUpdatePeer(dbPeer.copy(
                        isOnline = true, 
                        connectionQuality = (70..100).random(),
                        lastActiveTime = System.currentTimeMillis()
                    ))
                }
                delay(1500)
            }
        }
    }

    fun stopRadarScanSimulation() {
        isSimulatingRadar = false
    }

    /**
     * Receives an encrypted message from a peer and saves it. Called by simulation or mock radio listener.
     */
    suspend fun receiveMessage(
        peerId: String,
        encryptedContent: String,
        chatType: String,
        senderName: String,
        senderNickname: String,
        mediaType: String = "TEXT",
        mediaUri: String? = null
    ) = withContext(Dispatchers.IO) {
        // Confirm peer is not blocked
        val peerInfo = peerDao.getPeerById(peerId)
        if (peerInfo?.isBlocked == true) return@withContext

        val message = Message(
            senderName = senderName,
            senderNickname = senderNickname,
            content = encryptedContent, // encrypted payload
            timestamp = System.currentTimeMillis(),
            isMe = false,
            peerId = peerId,
            chatType = chatType,
            mediaUri = mediaUri,
            mediaType = mediaType,
            isEncrypted = true,
            deliveryStatus = "DELIVERED"
        )
        messageDao.insertMessage(message)
    }

    private fun triggerPrivateReplySimulation(peerId: String, userMessage: String) {
        scope.launch(Dispatchers.IO) {
            delay(1500) // user typing delay
            val peer = peerDao.getPeerById(peerId) ?: return@launch
            if (peer.isBlocked) return@launch
            if (peer.connectionStatus != "CONNECTED") return@launch

            // Generate contextual responses based on who they are and whether they speak English or Somali!
            val userLower = userMessage.lowercase()
            val preferredLanguage = profileDao.getProfileDirect()?.preferredLanguage ?: "EN"
            
            val responseText = when (peerId) {
                "asha_cilmi" -> {
                    if (preferredLanguage == "SO") {
                        if (userLower.contains("hallo") || userLower.contains("khayr") || userLower.contains("islaam") || userLower.contains("ma nabad baa") || userLower.contains("set tahay") || userLower.contains("sheeg")) {
                            "Nabad iyo khayr saaxiib! App-kan Amiin Online-la'aanta ah waa mid aad ugu fiican wada-hadalka isbitaallada dhexdiisa si fariimo lagu qariyo loo diro."
                        } else {
                            "Aad ayaan kuugu kuurgalayaa! Ma u baahan tahay mucawino caafimaad ama macluumaad ku saabsan nidaamka xiriirka ee Amiin?"
                        }
                    } else {
                        if (userLower.contains("hello") || userLower.contains("hi") || userLower.contains("how")) {
                            "Hello friend! This offline chat app is perfect for communicating secure encrypted health briefs without local mobile data towers."
                        } else {
                            "I hear you clearly. Message encrypted & decrypted correctly via P2P Bluetooth loopback."
                        }
                    }
                }
                "muse_gure" -> {
                    if (preferredLanguage == "SO") {
                        if (userLower.contains("sida") || userLower.contains("code") || userLower.contains("fikrad") || userLower.contains("amiin")) {
                            "Walaal Amiin Cabdi wuxuu halkan ku sameeyey arrin aad u weyn! Koodhkan Kotlin/Compose waa mid nadiif ah oo adeegsada Room DB iyo AES Crypto."
                        } else {
                            "Haa walaal! Haddii signal-ka la waayo, aalladaha isku dhow waxay is weydaarsan karaan fariimo iyagoo abuuraya Local Peer socket."
                        }
                    } else {
                        if (userLower.contains("code") || userLower.contains("tech") || userLower.contains("how")) {
                            "Amiin Cabdi really nailed the Jetpack Compose Material 3 implementation here! Using fully offline reactive StateFlows."
                        } else {
                            "Awesome! We are connected using virtual Wi-Fi Direct interface simulation. Connection quality: ${peer.connectionQuality}%."
                        }
                    }
                }
                "deeq_warsame" -> {
                    if (preferredLanguage == "SO") {
                        "Xogta waa la helay! Waxaan hadda tijaabinayaa awoodda fidinta mawjadaha ee Hargeisa. Fariimaha is-qorista aad bay u ammaan badanyihiin."
                    } else {
                        "Mesh signal captured. Tested with simulated standard 2.4Ghz channel propagation. Encryption is verified offline."
                    }
                }
                "farhiya_cabdi" -> {
                    if (preferredLanguage == "SO") {
                        "Waryaa! Aad ayaan ugu faraxsanahay inaan ku helay online-la'aan. Halkan Garowe internet-ka aad buu u liitaa badanaa, app-kan waa badbaado!"
                    } else {
                        "Wow! So cool. Offline messaging works perfectly. No cellular towers needed, developed by developer Amiin Cabdi."
                    }
                }
                else -> {
                    "Simulated auto-reply from $peerId."
                }
            }

            // Encrypt response
            val sessionKey = EncryptionHelper.getSessionKeyForPeer(peerId)
            val encryptedResponse = EncryptionHelper.encrypt(responseText, sessionKey)
            
            receiveMessage(
                peerId = peerId,
                encryptedContent = encryptedResponse,
                chatType = "PRIVATE",
                senderName = peer.name,
                senderNickname = peer.nickname
            )
        }
    }

    private fun triggerGroupReplySimulation(userMessage: String) {
        scope.launch(Dispatchers.IO) {
            // After user messages the group, let multiple peers chime in consecutively to represent an active offline local room!
            delay(1200)
            val preferredLanguage = profileDao.getProfileDirect()?.preferredLanguage ?: "EN"
            
            val ashaActive = peerDao.getPeerById("asha_cilmi")
            if (ashaActive != null && !ashaActive.isBlocked && ashaActive.connectionStatus == "CONNECTED") {
                val responseText = if (preferredLanguage == "SO") {
                    "Ku soo dhowaada kooxda isgaarsiinta deegaanka ee Amiin Offline!"
                } else {
                    "Welcome to the local decentralized group chat run entirely on wireless peer signals!"
                }
                val key = EncryptionHelper.getSessionKeyForPeer(GROUP_ID)
                receiveMessage(
                    peerId = GROUP_ID,
                    encryptedContent = EncryptionHelper.encrypt(responseText, key),
                    chatType = "GROUP",
                    senderName = ashaActive.name,
                    senderNickname = ashaActive.nickname
                )
            }

            delay(1800)
            val museActive = peerDao.getPeerById("muse_gure")
            if (museActive != null && !museActive.isBlocked && museActive.connectionStatus == "CONNECTED") {
                val responseText = if (preferredLanguage == "SO") {
                    "Nidaamkayagu waa sugan yahay. Dhamaan fariimaha aan halkan u dirno waxaa lagu ilaalinayaa furaha AES-128."
                } else {
                    "Every packet shared here is protected by local AES-128 cryptographic payloads. Completely serverless."
                }
                val key = EncryptionHelper.getSessionKeyForPeer(GROUP_ID)
                receiveMessage(
                    peerId = GROUP_ID,
                    encryptedContent = EncryptionHelper.encrypt(responseText, key),
                    chatType = "GROUP",
                    senderName = museActive.name,
                    senderNickname = museActive.nickname
                )
            }
        }
    }
}
