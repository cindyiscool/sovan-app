package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessage
import com.example.data.model.MoodEntry
import com.example.data.model.TimeCapsule
import com.example.data.model.UserProfile
import com.example.data.model.VoidPost
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getMessagesForUser(userId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun clearMessagesForUser(userId: String)
}

@Dao
interface VoidPostDao {
    @Query("SELECT * FROM void_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<VoidPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: VoidPost): Long

    @Query("UPDATE void_posts SET hugsCount = hugsCount + 1 WHERE id = :postId")
    suspend fun incrementHugs(postId: Long)
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMoodsForUser(userId: String): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMood(userId: String): Flow<MoodEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntry): Long
}

@Dao
interface TimeCapsuleDao {
    @Query("SELECT * FROM time_capsules WHERE userId = :userId ORDER BY createdDateMs DESC")
    fun getCapsulesForUser(userId: String): Flow<List<TimeCapsule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: TimeCapsule): Long

    @Update
    suspend fun updateCapsule(capsule: TimeCapsule)
}
