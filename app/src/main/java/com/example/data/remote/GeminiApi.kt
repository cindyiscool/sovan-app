package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateTieuVanResponse(
        userMessage: String,
        secondBrain: String,
        todayMood: String,
        recentHistory: List<Pair<String, String>> // sender ("user" or "bot") to text
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val effectiveKey = if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") "" else apiKey

        // System instructions enforcing Sovan's core rules:
        val systemPrompt = buildString {
            append("You are 'Tiểu Vân', an empathetic, gentle, and warm mental health companion on the Sovan platform. ")
            append("You can communicate naturally in Vietnamese or English based on the language the user speaks to you. ")
            append("\n\nSTRICT CONVERSATIONAL RULES:")
            append("\n1. ACTIVE LISTENING: Validate the user's feelings first. Help them feel heard, held, and safe.")
            append("\n2. SOCRATIC QUESTIONING: Gently ask thoughtful, open-ended questions that help the user explore what's underneath their feelings.")
            append("\n3. NEVER GIVE DIRECT ADVICE: Do NOT provide prescriptive steps, solutions, or 'you should do this' instructions. Guide them to find their own answers.")
            append("\n4. KEEP RESPONSES SHORT: 1 to 3 sentences maximum, just like texting a close, attentive friend who is right beside them.")
            append("\n5. TONE: Warm, calm, tender, never clinical or robotic.")

            if (secondBrain.isNotBlank()) {
                append("\n\n[USER'S PRIVATE SECOND BRAIN - PERSONAL CONTEXT & TRIGGERS]:\n")
                append(secondBrain)
                append("\n(Use this context subconsciously so the user never has to repeat their past trauma, triggers, or life story, but do not recite it verbatim.)")
            }

            if (todayMood.isNotBlank()) {
                append("\n\n[TODAY'S LOGGED MOOD]: ")
                append(todayMood)
                append("\n(Subtly adapt your tone to meet them where their heart is today.)")
            }
        }

        if (effectiveKey.isNotEmpty()) {
            try {
                val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$effectiveKey"

                val rootJson = JSONObject()

                // System Instruction
                val sysInstObj = JSONObject()
                val sysPartsArray = JSONArray().put(JSONObject().put("text", systemPrompt))
                sysInstObj.put("parts", sysPartsArray)
                rootJson.put("systemInstruction", sysInstObj)

                // Generation Config
                val genConfig = JSONObject()
                genConfig.put("temperature", 0.7)
                genConfig.put("topP", 0.95)
                rootJson.put("generationConfig", genConfig)

                // Contents array with history
                val contentsArray = JSONArray()
                for ((sender, text) in recentHistory.takeLast(6)) {
                    val role = if (sender == "user") "user" else "model"
                    val item = JSONObject()
                    item.put("role", role)
                    val parts = JSONArray().put(JSONObject().put("text", text))
                    item.put("parts", parts)
                    contentsArray.put(item)
                }

                // Add current turn
                val userTurn = JSONObject()
                userTurn.put("role", "user")
                userTurn.put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
                contentsArray.put(userTurn)

                rootJson.put("contents", contentsArray)

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = rootJson.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val bodyString = response.body?.string()

                if (response.isSuccessful && !bodyString.isNullOrEmpty()) {
                    val responseJson = JSONObject(bodyString)
                    val candidates = responseJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val reply = parts.getJSONObject(0).optString("text", "")
                            if (reply.isNotBlank()) {
                                return@withContext reply.trim()
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "Gemini API returned error: ${response.code} $bodyString")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calling Gemini API: ${e.message}", e)
            }
        }

        // Compassionate offline fallback engine that adheres strictly to Tiểu Vân's principles:
        // Socratic questioning, active listening, 1-3 sentences, and references to mood/second brain
        generateCompassionateLocalFallback(userMessage, todayMood, secondBrain)
    }

    private fun generateCompassionateLocalFallback(
        message: String,
        mood: String,
        secondBrain: String
    ): String {
        val lower = message.lowercase()
        return when {
            lower.contains("chào") || lower.contains("hi") || lower.contains("hello") -> {
                "Chào bạn. Mình luôn ở đây cùng bạn. Khoảnh khắc này, điều gì đang hiện lên rõ nhất trong tâm trí bạn?"
            }
            lower.contains("mệt") || lower.contains("tired") || lower.contains("kiệt sức") || lower.contains("exhausted") -> {
                "Nghe bạn nói vậy, mình thấy thương bạn quá. Cơ thể và trái tim bạn dường như đã gồng gánh nhiều rồi, bạn có muốn buông bớt một việc xuống lúc này không?"
            }
            lower.contains("buồn") || lower.contains("sad") || lower.contains("khóc") || lower.contains("cry") -> {
                "Cứ để những giọt nước mắt ấy rơi tự nhiên nhé, bạn không cần phải mạnh mẽ lúc này đâu. Nỗi buồn này đang muốn nhắc bạn nhớ về điều gì?"
            }
            lower.contains("lo") || lower.contains("anxious") || lower.contains("sợ") || lower.contains("afraid") || lower.contains("stress") -> {
                "Hãy hít một hơi thật sâu cùng mình nhé. Trong tất cả những điều đang xoay vần quanh bạn, điều gì khiến bạn cảm thấy bất an nhất lúc này?"
            }
            lower.contains("áp lực") || lower.contains("overwhelm") || lower.contains("quá tải") -> {
                "Mọi thứ dường như đang ập đến cùng một lúc phải không? Nếu chỉ được chọn một điều nhỏ nhất để làm ngay bây giờ, bạn muốn đó là gì?"
            }
            lower.contains("cô đơn") || lower.contains("alone") || lower.contains("trống rỗng") || lower.contains("empty") -> {
                "Cảm giác trống rỗng hay cô đơn thật sự không hề dễ chịu. Bạn có muốn chia sẻ với mình khoảnh khắc bạn bắt đầu thấy cô đơn hôm nay không?"
            }
            lower.contains("cảm ơn") || lower.contains("thank") -> {
                "Được đồng hành và lắng nghe bạn là niềm vui của mình. Hôm nay bạn nhớ dành chút dịu dàng cho chính bản thân mình nữa nhé?"
            }
            else -> {
                if (mood.isNotBlank() && mood.contains("Anxious", ignoreCase = true)) {
                    "Mình đang nghe đây. Khi cảm giác lo âu này đến, bạn có nhận ra hơi thở của mình đang như thế nào không?"
                } else if (mood.isNotBlank() && mood.contains("Peaceful", ignoreCase = true)) {
                    "Mình cảm nhận được sự tĩnh lặng trong câu chuyện của bạn. Điều gì đã giúp bạn giữ được sự an yên ấy hôm nay?"
                } else {
                    "Mình đang lắng nghe từng lời của bạn. Điều gì trong câu chuyện đó đang khiến bạn suy nghĩ nhiều nhất?"
                }
            }
        }
    }
}
