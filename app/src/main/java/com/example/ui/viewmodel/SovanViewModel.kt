package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SovanDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.MoodEntry
import com.example.data.model.TimeCapsule
import com.example.data.model.UserProfile
import com.example.data.model.VoidPost
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SovanScreen(val title: String, val icon: String) {
    CHAT("Tiểu Vân Chat", "💬"),
    SECOND_BRAIN("Second Brain", "🧠"),
    VOID("The Void", "🌌"),
    MOOD("Mood Tracker", "🌸"),
    TIME_CAPSULE("Time Capsule", "⏳"),
    WEB_CODE("Web Architecture", "🌐")
}

data class MoodOption(
    val emoji: String,
    val label: String,
    val toneInfluence: String,
    val description: String
)

class SovanViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SovanDatabase.getDatabase(application)
    private val profileDao = db.userProfileDao()
    private val chatDao = db.chatMessageDao()
    private val voidDao = db.voidPostDao()
    private val moodDao = db.moodDao()
    private val capsuleDao = db.timeCapsuleDao()

    // Current Screen
    private val _currentScreen = MutableStateFlow(SovanScreen.CHAT)
    val currentScreen: StateFlow<SovanScreen> = _currentScreen.asStateFlow()

    // Active User Space ID
    private val _activeUserId = MutableStateFlow("user_default")
    val activeUserId: StateFlow<String> = _activeUserId.asStateFlow()

    // All profiles for tenant / space switching
    val allProfiles: StateFlow<List<UserProfile>> = profileDao.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Active Profile
    private val _activeProfile = MutableStateFlow<UserProfile?>(null)
    val activeProfile: StateFlow<UserProfile?> = _activeProfile.asStateFlow()

    // Chat messages for active user
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Void anonymous posts
    val voidPosts: StateFlow<List<VoidPost>> = voidDao.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Mood entries for active user
    private val _moods = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moods: StateFlow<List<MoodEntry>> = _moods.asStateFlow()

    private val _latestMood = MutableStateFlow<MoodEntry?>(null)
    val latestMood: StateFlow<MoodEntry?> = _latestMood.asStateFlow()

    // Time Capsules for active user
    private val _timeCapsules = MutableStateFlow<List<TimeCapsule>>(emptyList())
    val timeCapsules: StateFlow<List<TimeCapsule>> = _timeCapsules.asStateFlow()

    // UI state for chat
    private val _isBotTyping = MutableStateFlow(false)
    val isBotTyping: StateFlow<Boolean> = _isBotTyping.asStateFlow()

    // Show mood check-in popup
    private val _showMoodPopup = MutableStateFlow(false)
    val showMoodPopup: StateFlow<Boolean> = _showMoodPopup.asStateFlow()

    // Show user switch / tenant dialog
    private val _showTenantDialog = MutableStateFlow(false)
    val showTenantDialog: StateFlow<Boolean> = _showTenantDialog.asStateFlow()

    // Notification / snackbar message
    private val _snackbarEvent = MutableStateFlow<String?>(null)
    val snackbarEvent: StateFlow<String?> = _snackbarEvent.asStateFlow()

    val moodOptions = listOf(
        MoodOption("🌸", "Joyful", "Celebratory, joyful validation and shared delight", "Feeling light, grateful, or excited"),
        MoodOption("🌿", "Peaceful", "Gentle, calm pacing and quiet reflection", "Content, centered, and tranquil"),
        MoodOption("✨", "Hopeful", "Warm encouragement and forward-looking wonder", "Sensing a new beginning or light"),
        MoodOption("☕", "Tired", "Soft sanctuary, minimal burden, restful presence", "Low energy, drained, or needing pause"),
        MoodOption("☁️", "Anxious", "Grounding questions, slow reassurance, zero pressure", "Nervous, racing thoughts, or tense"),
        MoodOption("🌧️", "Down / Sad", "Tender holding, deep compassion, permission to weep", "Heavy heart, grief, or melancholia"),
        MoodOption("⚡", "Overwhelmed", "Decompression, untangling knot by knot", "Too many demands, sensory overload")
    )

    init {
        viewModelScope.launch {
            _activeUserId.collect { uid ->
                // Fetch profile
                val profile = profileDao.getProfileById(uid)
                if (profile == null) {
                    val fallback = UserProfile(
                        id = uid,
                        name = "Vivian",
                        email = "vivian@sovan.app",
                        secondBrain = "I struggle with feeling like I am never doing enough, especially with deadlines. Conflict with family easily triggers my anxiety and makes me shut down. I value quiet spaces, gentle questions, and patience.",
                        avatarEmoji = "🌸"
                    )
                    profileDao.insertOrUpdateProfile(fallback)
                    _activeProfile.value = fallback
                } else {
                    _activeProfile.value = profile
                }

                // Collect chat messages for this user
                launch {
                    chatDao.getMessagesForUser(uid).collect { list ->
                        _messages.value = list
                    }
                }

                // Collect moods
                launch {
                    moodDao.getMoodsForUser(uid).collect { list ->
                        _moods.value = list
                    }
                }
                launch {
                    moodDao.getLatestMood(uid).collect { mood ->
                        _latestMood.value = mood
                    }
                }

                // Collect capsules
                launch {
                    capsuleDao.getCapsulesForUser(uid).collect { list ->
                        _timeCapsules.value = list
                    }
                }
            }
        }
    }

    fun navigateTo(screen: SovanScreen) {
        _currentScreen.value = screen
    }

    fun dismissSnackbar() {
        _snackbarEvent.value = null
    }

    fun openMoodPopup() {
        _showMoodPopup.value = true
    }

    fun closeMoodPopup() {
        _showMoodPopup.value = false
    }

    fun openTenantDialog() {
        _showTenantDialog.value = true
    }

    fun closeTenantDialog() {
        _showTenantDialog.value = false
    }

    fun switchUserSpace(newUserId: String) {
        _activeUserId.value = newUserId
        _showTenantDialog.value = false
        _snackbarEvent.value = "Switched to private space: $newUserId"
    }

    fun createNewUserSpace(name: String, email: String, emoji: String) {
        val newId = "user_" + System.currentTimeMillis().toString().takeLast(6)
        val profile = UserProfile(
            id = newId,
            name = name.ifBlank { "User" },
            email = email.ifBlank { "$newId@sovan.app" },
            secondBrain = "",
            avatarEmoji = emoji
        )
        viewModelScope.launch {
            profileDao.insertOrUpdateProfile(profile)
            chatDao.insertMessage(
                ChatMessage(
                    userId = newId,
                    sender = "bot",
                    text = "Chào bạn $name! Mình là Tiểu Vân. Không gian này hoàn toàn riêng tư cho bạn. Bạn có muốn tâm sự một chút không?"
                )
            )
            _activeUserId.value = newId
            _showTenantDialog.value = false
            _snackbarEvent.value = "Welcome, $name! Private space created."
        }
    }

    // Second Brain updates
    fun saveSecondBrain(updatedText: String) {
        val current = _activeProfile.value ?: return
        val updated = current.copy(secondBrain = updatedText)
        viewModelScope.launch {
            profileDao.insertOrUpdateProfile(updated)
            _activeProfile.value = updated
            _snackbarEvent.value = "Second Brain updated. Tiểu Vân will remember this context."
        }
    }

    // Chat functionality
    fun sendMessageToTieuVan(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val uid = _activeUserId.value
        val userMsg = ChatMessage(
            userId = uid,
            sender = "user",
            text = trimmed
        )

        viewModelScope.launch {
            chatDao.insertMessage(userMsg)
            _isBotTyping.value = true

            val secondBrain = _activeProfile.value?.secondBrain ?: ""
            val todayMood = _latestMood.value?.let { "${it.emoji} ${it.label}: ${it.toneInfluence}" } ?: ""
            val history = _messages.value.map { it.sender to it.text }

            val botReply = GeminiApiClient.generateTieuVanResponse(
                userMessage = trimmed,
                secondBrain = secondBrain,
                todayMood = todayMood,
                recentHistory = history
            )

            val botMsg = ChatMessage(
                userId = uid,
                sender = "bot",
                text = botReply
            )
            chatDao.insertMessage(botMsg)
            _isBotTyping.value = false
        }
    }

    fun clearChat() {
        val uid = _activeUserId.value
        viewModelScope.launch {
            chatDao.clearMessagesForUser(uid)
            chatDao.insertMessage(
                ChatMessage(
                    userId = uid,
                    sender = "bot",
                    text = "Trang giấy mới đã mở ra rồi. Bất cứ khi nào bạn cần một nơi lắng nghe, mình luôn ở đây cùng bạn."
                )
            )
            _snackbarEvent.value = "Chat reset for a fresh breath."
        }
    }

    // The Void: Anonymous Wall
    fun postToTheVoid(ventText: String, tag: String) {
        val trimmed = ventText.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val post = VoidPost(
                content = trimmed,
                hugsCount = 1,
                tag = tag.ifBlank { "Anonymous Vent" }
            )
            voidDao.insertPost(post)
            _snackbarEvent.value = "Your words have been released into The Void. You are heard."
        }
    }

    fun sendHugToPost(postId: Long) {
        viewModelScope.launch {
            voidDao.incrementHugs(postId)
            _snackbarEvent.value = "A warm hug was sent! ❤️"
        }
    }

    // Mood Tracker
    fun logMood(option: MoodOption, note: String) {
        val uid = _activeUserId.value
        viewModelScope.launch {
            val entry = MoodEntry(
                userId = uid,
                emoji = option.emoji,
                label = option.label,
                toneInfluence = option.toneInfluence,
                note = note
            )
            moodDao.insertMood(entry)
            _showMoodPopup.value = false
            _snackbarEvent.value = "Mood recorded. Tiểu Vân's tone is now adapted to your day."
        }
    }

    // Time Capsule
    fun createTimeCapsule(title: String, content: String, durationLabel: String, durationDays: Int) {
        val uid = _activeUserId.value
        val now = System.currentTimeMillis()
        val unlockTime = now + (durationDays.toLong() * 86400000L)

        viewModelScope.launch {
            val capsule = TimeCapsule(
                userId = uid,
                title = title.ifBlank { "Letter to Future Self" },
                content = content,
                durationLabel = durationLabel,
                createdDateMs = now,
                unlockDateMs = unlockTime,
                isOpened = false
            )
            capsuleDao.insertCapsule(capsule)
            _snackbarEvent.value = "Time capsule sealed until ${durationLabel} later. 💌"
        }
    }

    fun openTimeCapsule(capsule: TimeCapsule) {
        viewModelScope.launch {
            val updated = capsule.copy(isOpened = true)
            capsuleDao.updateCapsule(updated)
            _snackbarEvent.value = "Time capsule opened! ✨"
        }
    }
}
