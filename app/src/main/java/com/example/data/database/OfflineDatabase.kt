package com.example.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// ------------------------------------------------------------------------
// ENTITIES
// ------------------------------------------------------------------------

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val senderNickname: String,
    val content: String,
    val timestamp: Long,
    val isMe: Boolean,
    val peerId: String, // "GROUP" if group chat, or the peerId for private chat
    val chatType: String, // "PRIVATE" or "GROUP"
    val mediaUri: String? = null,
    val mediaType: String = "TEXT", // "TEXT", "IMAGE", "DOCUMENT", "VOICE"
    val isEncrypted: Boolean = true,
    val deliveryStatus: String = "DELIVERED" // "SENT", "DELIVERED"
)

@Entity(tableName = "peers")
data class Peer(
    @PrimaryKey val peerId: String,
    val name: String,
    val nickname: String,
    val bio: String,
    val avatarIndex: Int, // 1 to 6 mapped to nice vector profile illustrations
    val connectionStatus: String = "DISCONNECTED", // "DISCONNECTED", "CONNECTING", "CONNECTED"
    val isFavorite: Boolean = false,
    val isBlocked: Boolean = false,
    val connectionQuality: Int = 100, // percentage 0-100%
    val isOnline: Boolean = true,
    val lastActiveTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class MyProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val nickname: String = "",
    val bio: String = "",
    val avatarIndex: Int = 0,
    val preferredLanguage: String = "EN", // "EN", "SO"
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val isFirstLaunch: Boolean = true
)

// ------------------------------------------------------------------------
// DAOS
// ------------------------------------------------------------------------

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE peerId = :peerId AND chatType = 'PRIVATE' ORDER BY timestamp ASC")
    fun getPrivateChats(peerId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE chatType = 'GROUP' ORDER BY timestamp ASC")
    fun getGroupChats(): Flow<List<Message>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessagesFlow(): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("DELETE FROM messages WHERE peerId = :peerId AND chatType = 'PRIVATE'")
    suspend fun clearPrivateChatsWithPeer(peerId: String)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
    
    @Query("SELECT * FROM messages WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<Message>>
}

@Dao
interface PeerDao {
    @Query("SELECT * FROM peers ORDER BY name ASC")
    fun getAllPeersFlow(): Flow<List<Peer>>

    @Query("SELECT * FROM peers WHERE peerId = :peerId LIMIT 1")
    suspend fun getPeerById(peerId: String): Peer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePeer(peer: Peer)

    @Query("UPDATE peers SET connectionStatus = :status WHERE peerId = :peerId")
    suspend fun updateConnectionStatus(peerId: String, status: String)

    @Query("UPDATE peers SET isFavorite = :isFavorite WHERE peerId = :peerId")
    suspend fun updateFavorite(peerId: String, isFavorite: Boolean)

    @Query("UPDATE peers SET isBlocked = :isBlocked WHERE peerId = :peerId")
    suspend fun updateBlocked(peerId: String, isBlocked: Boolean)

    @Query("DELETE FROM peers WHERE peerId = :peerId")
    suspend fun deletePeer(peerId: String)

    @Query("DELETE FROM peers")
    suspend fun clearAllPeers()
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<MyProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileDirect(): MyProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: MyProfile)
}

// ------------------------------------------------------------------------
// DATABASE HOLDER
// ------------------------------------------------------------------------

@Database(entities = [Message::class, Peer::class, MyProfile::class], version = 1, exportSchema = false)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun peerDao(): PeerDao
    abstract fun profileDao(): ProfileDao
}
