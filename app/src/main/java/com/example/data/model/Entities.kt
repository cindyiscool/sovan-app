package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val secondBrain: String,
    val avatarEmoji: String = "🌸",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val sender: String, // "user" or "bot"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "void_posts")
data class VoidPost(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val hugsCount: Int = 0,
    val tag: String = "Late Night Thoughts",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val emoji: String,
    val label: String,
    val toneInfluence: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "time_capsules")
data class TimeCapsule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val title: String,
    val content: String,
    val durationLabel: String,
    val createdDateMs: Long = System.currentTimeMillis(),
    val unlockDateMs: Long,
    val isOpened: Boolean = false
)
