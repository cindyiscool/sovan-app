package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.MoodEntry
import com.example.data.model.TimeCapsule
import com.example.data.model.UserProfile
import com.example.data.model.VoidPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        ChatMessage::class,
        VoidPost::class,
        MoodEntry::class,
        TimeCapsule::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SovanDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun voidPostDao(): VoidPostDao
    abstract fun moodDao(): MoodDao
    abstract fun timeCapsuleDao(): TimeCapsuleDao

    companion object {
        @Volatile
        private var INSTANCE: SovanDatabase? = null

        fun getDatabase(context: Context): SovanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SovanDatabase::class.java,
                    "sovan_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial seed data for The Void and default User Space
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                seedInitialData(database)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(db: SovanDatabase) {
            // Seed default user profile
            val defaultProfile = UserProfile(
                id = "user_default",
                name = "Vivian",
                email = "vivian@sovan.app",
                secondBrain = "I struggle with feeling like I am never doing enough, especially with deadlines. Conflict with family easily triggers my anxiety and makes me shut down. I value quiet spaces, gentle questions, and patience.",
                avatarEmoji = "🌸"
            )
            db.userProfileDao().insertOrUpdateProfile(defaultProfile)

            // Seed initial welcome message from Tiểu Vân
            db.chatMessageDao().insertMessage(
                ChatMessage(
                    userId = "user_default",
                    sender = "bot",
                    text = "Chào bạn, mình là Tiểu Vân đây. Không gian này hoàn toàn an toàn và riêng tư dành riêng cho bạn. Hôm nay bạn đang cảm thấy thế nào trong lòng?",
                    timestamp = System.currentTimeMillis() - 60000
                )
            )

            // Seed initial mood
            db.moodDao().insertMood(
                MoodEntry(
                    userId = "user_default",
                    emoji = "🌿",
                    label = "Peaceful",
                    toneInfluence = "Gentle, grounding, and steady warmth.",
                    note = "Taking a deep breath this morning.",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )

            // Seed initial Void posts (Anonymous wall, no usernames, heart hugs only)
            val seedPosts = listOf(
                VoidPost(
                    content = "Today I felt like everyone was walking forward while I stood still. I had to sit in my car and just cry for ten minutes.",
                    hugsCount = 42,
                    tag = "Heavy Heart",
                    timestamp = System.currentTimeMillis() - 7200000
                ),
                VoidPost(
                    content = "To whoever needs to hear this: you don't have to have your whole life figured out by sunset today. Breathing is enough.",
                    hugsCount = 128,
                    tag = "Gentle Reminder",
                    timestamp = System.currentTimeMillis() - 14400000
                ),
                VoidPost(
                    content = "I pretended to be okay all through the office meeting. My chest felt tight the entire time, but I smiled anyway. I'm exhausted.",
                    hugsCount = 76,
                    tag = "Exhaustion",
                    timestamp = System.currentTimeMillis() - 28800000
                ),
                VoidPost(
                    content = "I made my bed today for the first time in two weeks. It is a tiny thing, but to me it felt like moving a mountain.",
                    hugsCount = 95,
                    tag = "Little Wins",
                    timestamp = System.currentTimeMillis() - 43200000
                ),
                VoidPost(
                    content = "Sometimes I miss who I was before all the worries took over. Just sending love to anyone else feeling nostalgic and tender tonight.",
                    hugsCount = 153,
                    tag = "Late Night Thoughts",
                    timestamp = System.currentTimeMillis() - 86400000
                )
            )
            for (post in seedPosts) {
                db.voidPostDao().insertPost(post)
            }

            // Seed an initial Time Capsule
            val now = System.currentTimeMillis()
            db.timeCapsuleDao().insertCapsule(
                TimeCapsule(
                    userId = "user_default",
                    title = "A gentle promise to future Vivian",
                    content = "Remember how overwhelmed you felt today, yet how you survived every single hard day before this? Please be soft with yourself. Don't rush into tomorrow without honoring who you are right now.",
                    durationLabel = "1 Month",
                    createdDateMs = now - 86400000 * 20,
                    unlockDateMs = now + 86400000 * 10,
                    isOpened = false
                )
            )
        }
    }
}
