package com.example.localization

enum class LanguageCode {
    EN, SO
}

object Translations {
    private val english = mapOf(
        "app_name" to "Amiin Offline Chat",
        "developer" to "Developer: Amiin Cabdi",
        "tagline" to "Peer-to-Peer Secure Messenger",
        
        // Navigation / Tabs
        "tab_chats" to "Chats",
        "tab_nearby" to "Nearby",
        "tab_profile" to "Profile",
        "tab_settings" to "Settings",
        
        // Actions
        "action_connect" to "Connect",
        "action_disconnect" to "Disconnect",
        "action_send" to "Send",
        "action_save" to "Save",
        "action_edit" to "Edit",
        "action_cancel" to "Cancel",
        "action_confirm" to "Confirm",
        "action_clear_chat" to "Clear Chat",
        "action_clear_history" to "Clear All History",
        "action_block" to "Block User",
        "action_unblock" to "Unblock",
        "action_favorite" to "Add to Favorites",
        "action_unfavorite" to "Remove Favorite",
        "action_share_image" to "Share Image",
        "action_share_file" to "Share File",
        "action_voice_msg" to "Voice Note",
        "action_scan" to "Scan for Nearby Users",
        "action_stop_scan" to "Stop Scanning",
        
        // Status & Metadata
        "status_connected" to "Connected",
        "status_connecting" to "Connecting...",
        "status_disconnected" to "Disconnected",
        "status_blocked" to "Blocked",
        "status_online" to "Online",
        "status_offline" to "Offline",
        "status_away" to "Away",
        "status_encrypted" to "Encrypted",
        "status_sent" to "Sent",
        "status_delivered" to "Delivered",
        "status_in_range" to "In Range",
        
        // Settings panel
        "settings_title" to "Settings",
        "settings_language" to "Change Language",
        "settings_theme" to "Theme Mode",
        "settings_theme_dark" to "Dark Theme",
        "settings_theme_light" to "Light Theme",
        "settings_edit_profile" to "Edit Profile",
        "settings_clear_history" to "Clear Chat History",
        "settings_clear_history_desc" to "This will erase all messages and cached peer entries from your device.",
        "settings_privacy" to "Privacy Settings",
        "settings_privacy_desc" to "Manage blocked users and transmission settings.",
        "settings_about" to "About Amiin Offline Chat",
        "settings_clean_up_success" to "Local chat history has been successfully cleared.",
        
        // Profile fields
        "profile_title" to "User Profile",
        "profile_name" to "Full Name",
        "profile_nickname" to "Nickname / Handle",
        "profile_bio" to "Bio / Status",
        "profile_select_avatar" to "Select Profile Picture",
        "profile_save_success" to "Profile updated successfully!",
        
        // Search & Filter
        "search_placeholder" to "Search messages or users...",
        "favorites_only" to "Show Favorites Only",
        "all_users" to "All Nearby Users",
        
        // Dialogs & Notifications
        "notify_new_message" to "New message received",
        "notify_connected" to "Connected to a nearby device!",
        "notify_disconnected" to "Connection lost with peer.",
        "perm_required_title" to "Permissions Required",
        "perm_required_desc" to "Amiin Offline Chat requires Bluetooth, Wi-Fi Direct, and Location permissions to discover and message nearby peers without internet access. Please enable them.",
        "perm_grant_btn" to "Grant Permissions",
        
        // Screen Empty States
        "empty_chats_title" to "No Chats Yet",
        "empty_chats_desc" to "Connect to scanning users nearby to start secure offline private or group communication.",
        "empty_peers_title" to "Scanning the Radar...",
        "empty_peers_desc" to "Turn on scan to find nearby peers. Make sure they have Amiin Offline Chat open and active.",
        
        // Payload Security Inspector
        "sec_payload_title" to "Security Payload Inspector",
        "sec_payload_original" to "Original Decrypted Text",
        "sec_payload_encrypted" to "Transmitted Ciphertext",
        "sec_payload_algo" to "Encryption Protocol",
        "sec_payload_key" to "Local Session Key",
        "sec_view_payload" to "Inspect Secure Payload",
        
        // About Section
        "about_heading" to "About App",
        "about_description" to "Amiin Offline Chat is a communication application that allows nearby users to exchange messages without internet access.",
        "about_notice" to "Built with state-of-the-art secure local peer-to-peer standards (Bluetooth, Wi-Fi Direct, and Nearby Connections simulation protocol). Zero cloud tracking. Offline-first.",
        "about_copyright" to "© 2026 Amiin Offline Chat. Developed by Amiin Cabdi. All rights reserved.",
        
        // Help and Guides
        "guide_title" to "Offline Interactive Guide & Help",
        "guide_headline" to "Learn how to communicate completely offline without cellular data or internet!",
        "guide_how_works_title" to "How Connection Works",
        "guide_how_works_desc" to "Amiin Chat uses your phone's built-in Bluetooth and local Wi-Fi Direct antennas to advertise presence and exchange key handshakes. Zero internet is loaded.",
        "guide_security_title" to "End-to-End Encryption Setup",
        "guide_security_desc" to "Every 1-on-1 peer channel establishes a unique key. Each sent character turns into strong ciphertext which is transmitted through local airwaves. Nobody else can sniff it.",
        "guide_range_title" to "Distance & Coverage Area",
        "guide_range_desc" to "Bluetooth covers up to 10-30 meters. Local high-speed Peer-to-Peer Wi-Fi direct extends up to 100 meters. Keep devices in the same room or close proximity for super-fast rates.",
        "guide_trouble_title" to "Disconnection Fixes",
        "guide_trouble_desc" to "If a connection drops, simply tap 'Disconnect' then 'Connect' again to re-authenticate. Ensure both devices have scanning enabled in the 'Nearby' tab."
    )

    private val somali = mapOf(
        "app_name" to "Amiin Offline Chat",
        "developer" to "Horumariye: Amiin Cabdi",
        "tagline" to "Farriin-side Sugan oo Iskuxir dhow ah",
        
        // Navigation / Tabs
        "tab_chats" to "Fariimaha",
        "tab_nearby" to "Xiriirada",
        "tab_profile" to "Astaanta",
        "tab_settings" to "Dejinta",
        
        // Actions
        "action_connect" to "Xiriiri",
        "action_disconnect" to "Kala saar",
        "action_send" to "Dir",
        "action_save" to "Keydi",
        "action_edit" to "Wax ka baddal",
        "action_cancel" to "Ibaabi",
        "action_confirm" to "Xaqiiji",
        "action_clear_chat" to "Kala sifee fariinta",
        "action_clear_history" to "Tirtir dhamaan kaydka",
        "action_block" to "Mamnuuc isticmaalaha",
        "action_unblock" to "Ka qaad mamnuuca",
        "action_favorite" to "Ku dar kuwa la jecelyahay",
        "action_unfavorite" to "Ka saar kuwa la jecelyahay",
        "action_share_image" to "La wadaag Sawir",
        "action_share_file" to "La wadaag Dokumeenti",
        "action_voice_msg" to "Fariin Cod ah",
        "action_scan" to "Raadi Isticmaalayaasha Dhow",
        "action_stop_scan" to "Jooji Baaritaanka",
        
        // Status & Metadata
        "status_connected" to "Waa ku xiranyahay",
        "status_connecting" to "La xiriirayaa...",
        "status_disconnected" to "Waa go'anyahay",
        "status_blocked" to "Waa mamnuuc",
        "status_online" to "Khadka ayuu ku jiraa",
        "status_offline" to "Aallada kama dhowa",
        "status_away" to "Mashquul",
        "status_encrypted" to "La qariyay (Encrypted)",
        "status_sent" to "La diray",
        "status_delivered" to "La gaarsiiyay",
        "status_in_range" to "Wuu dhowyahay",
        
        // Settings panel
        "settings_title" to "Dejinta",
        "settings_language" to "Baddal Luuqadda",
        "settings_theme" to "Habka Midabka",
        "settings_theme_dark" to "Habka Habeenka",
        "settings_theme_light" to "Habka Maalinta",
        "settings_edit_profile" to "Wax ka baddal profile-ka",
        "settings_clear_history" to "Tirtir taariikhda farriimaha",
        "settings_clear_history_desc" to "Tani waxay tirtiri doontaa dhamaan fariimaha iyo diiwaanka isticmaalayaasha ee aalladda ku keydsan.",
        "settings_privacy" to "Dejinta Gaarka ah",
        "settings_privacy_desc" to "Maamul dadka laga mamnuucay iyo qaabka gudbinta farriimaha.",
        "settings_about" to "Ku saabsan Amiin Offline Chat",
        "settings_clean_up_success" to "Taariikhda wada sheekaysiga deegaanka waa la tirtiray oo sifeeyey.",
        
        // Profile fields
        "profile_title" to "Astaanta Isticmaalaha",
        "profile_name" to "Magaca Buuxa",
        "profile_nickname" to "Naaneys / Handle",
        "profile_bio" to "Taariikh kooban / Bio",
        "profile_select_avatar" to "Dooro Sawirka Astaanta",
        "profile_save_success" to "Profile-ka si guul leh ayaa loo cusbooneysiiyay!",
        
        // Search & Filter
        "search_placeholder" to "Raadi fariimo ama saaxiibo...",
        "favorites_only" to "Kuwa la jecelyahay oo kaliya",
        "all_users" to "Dhammaan Dadka Dhow",
        
        // Dialogs & Notifications
        "notify_new_message" to "Fariin cusub ayaa kuu timid",
        "notify_connected" to "Waa lagu xiray aallad u dhow!",
        "notify_disconnected" to "Xiriirkii waa go'ay.",
        "perm_required_title" to "Idan ayaa Loo Baahanyahay",
        "perm_required_desc" to "Amiin Offline Chat wuxuu u baahanyahay idanka Bluetooth-ka, Wi-Fi Direct, iyo Location-ka si uu u baadho fariimona ula wadaago dadka ku dhow isaga oo aan adeegsan internet. Fadlan oggolow.",
        "perm_grant_btn" to "Bixi Idammada",
        
        // Screen Empty States
        "empty_chats_title" to "Ma jiraan Wada-hadallo",
        "empty_chats_desc" to "La xiriir isticmaalayaasha dhow si aad u bilowdo wada sheekeysi rasmi ah oo ammaan ah deegaanka.",
        "empty_peers_title" to "Raadinta Isticmaalayaasha Dhow...",
        "empty_peers_desc" to "Daar raadiyaha si aad u hesho saaxiibo. Hubi inay iyaguna qabaan Amiin Offline Chat oo uu u furan yahay.",
        
        // Payload Security Inspector
        "sec_payload_title" to "Kormeeraha Badbaadada Guud",
        "sec_payload_original" to "Farriintii caadiga ahayd",
        "sec_payload_encrypted" to "Cram-ka fariinta ee la diray",
        "sec_payload_algo" to "Nidaamka sir-qorista (Cipher)",
        "sec_payload_key" to "Furihii gaarka ahaa",
        "sec_view_payload" to "Kormeero Fariinta Sirta ah",
        
        // About Section
        "about_heading" to "Ku Saabsan App-ka",
        "about_description" to "Amiin Offline Chat waa app isgaarsiineed oo u oggolaanaya isticmaalayaasha isku dhow inay fariimo is dhaafsadaan iyaga oo aan adeegsan internet.",
        "about_notice" to "Waxaa lagu dhisay nidaamyada isgaarsiineed ee deegaanka sida Bluetooth, Wi-Fi Direct, iyo nidaamka simulation ee Nearby Connections. Ma jiro daba-gal daruuro ah. Offline-first.",
        "about_copyright" to "© 2026 Amiin Offline Chat. Horumariyay Amiin Cabdi. Xuquuqda oo dhan waa dhowran tahay.",
        
        // Help and Guides Somali
        "guide_title" to "Hanuuniyaha Wada-hadalka Offline-ka",
        "guide_headline" to "Baro sida aad ula xiriiri lahayd asxaabtaada gabi ahaanba khadka la'aan, adoon adeegsan internet ama xogta mobilka!",
        "guide_how_works_title" to "Sida uu u Shaqeeyo Iskuxirka",
        "guide_how_works_desc" to "Amiin Chat wuxuu isticmaalaa Bluetooth-ka iyo Wi-Fi Direct-ka aalladaada si uu u baahiyo dhowaanshaha una isdhaafsado fariimaha. Eyber internet ah lama isticmaalo.",
        "guide_security_title" to "Nidaamka Amniga & Qarsoodiga",
        "guide_security_desc" to "Xiriir kasta wuxuu leeyahay fure u gaar ah. Cilaamad kasta oo la diro waxay isu baddashaa sir sarsare (ciphertext) taas oo hawada ku dhex safreysa si sugan.",
        "guide_range_title" to "Masaafada & Baaxadda",
        "guide_range_desc" to "Bluetooth wuxuu gaaraa 10 ilaa 30 mitir. Wi-Fi Direct-fure wuxuu gaari karaa ilaa 100 mitir deegaan furan. Ku hay aalladaha meel isku dhow si fariimuhu u tagaan si dhakhso ah.",
        "guide_trouble_title" to "Xallinta Cilladaha & Khaladaadka",
        "guide_trouble_desc" to "Haddii xiriirku go'o, ku dhufo 'Kala saar' ka dib 'Xiriiri' si dib loogu xaqiijiyo. Hubi in raadiyadaha labada dhinacba ay furan yihiin fariimuhuna dhow yihiin."
    )

    fun translate(key: String, lang: LanguageCode): String {
        val map = when (lang) {
            LanguageCode.EN -> english
            LanguageCode.SO -> somali
        }
        return map[key] ?: key
    }
}
