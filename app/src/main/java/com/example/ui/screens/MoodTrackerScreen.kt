package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodEntry
import com.example.ui.theme.SovanAccent
import com.example.ui.theme.SovanBackground
import com.example.ui.theme.SovanBorder
import com.example.ui.theme.SovanBotBubble
import com.example.ui.theme.SovanSurface
import com.example.ui.theme.SovanText
import com.example.ui.theme.SovanTextMuted
import com.example.ui.theme.SovanUserBubble
import com.example.ui.viewmodel.MoodOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MoodTrackerScreen(
    moods: List<MoodEntry>,
    latestMood: MoodEntry?,
    moodOptions: List<MoodOption>,
    onLogMood: (MoodOption, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOption by remember {
        mutableStateOf(moodOptions.firstOrNull { it.label == (latestMood?.label ?: "Peaceful") } ?: moodOptions[1])
    }
    var noteText by remember { mutableStateOf("") }

    val timeFormatter = remember { SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SovanBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SovanBotBubble),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌸", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Mood Tracker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = SovanText
                    )
                    Text(
                        text = "Daily check-in that shapes Tiểu Vân's conversational tone",
                        fontSize = 11.sp,
                        color = SovanTextMuted
                    )
                }
            }
        }

        // Active Mood Status Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SovanSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SovanBotBubble),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = latestMood?.emoji ?: selectedOption.emoji,
                            fontSize = 30.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Current State: ${latestMood?.label ?: selectedOption.label}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SovanText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = latestMood?.toneInfluence ?: selectedOption.toneInfluence,
                            fontSize = 12.sp,
                            color = SovanTextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Log Today's Mood Section
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SovanSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "How does your soul feel right now?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = SovanText
                    )

                    // Emoji Options
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(moodOptions) { option ->
                            val isSelected = selectedOption == option
                            Surface(
                                color = if (isSelected) SovanUserBubble else SovanBackground,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) SovanUserBubble else SovanBorder
                                ),
                                modifier = Modifier
                                    .clickable { selectedOption = option }
                                    .testTag("mood_option_${option.label}")
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(text = option.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = option.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) SovanSurface else SovanText
                                    )
                                }
                            }
                        }
                    }

                    // Explanation of Tone Influence
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SovanBotBubble)
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Tone influence",
                                tint = SovanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tiểu Vân's tone shift today:",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = SovanText
                                )
                                Text(
                                    text = selectedOption.toneInfluence,
                                    fontSize = 12.sp,
                                    color = SovanText,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Optional Note
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = {
                            Text("Add a sentence about your day (optional)...", fontSize = 13.sp, color = SovanTextMuted)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mood_note_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SovanBackground,
                            unfocusedContainerColor = SovanBackground,
                            focusedBorderColor = SovanUserBubble,
                            unfocusedBorderColor = SovanBorder,
                            focusedTextColor = SovanText,
                            unfocusedTextColor = SovanText
                        )
                    )

                    Button(
                        onClick = {
                            onLogMood(selectedOption, noteText)
                            noteText = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SovanUserBubble,
                            contentColor = SovanSurface
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_mood_button")
                    ) {
                        Text("Save Check-in", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Past Mood History
        item {
            Text(
                text = "Mood Journey",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = SovanText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(moods, key = { it.id }) { entry ->
            val dateStr = remember(entry.timestamp) { timeFormatter.format(Date(entry.timestamp)) }
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SovanSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = entry.emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = entry.label,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = SovanText
                            )
                            Text(
                                text = dateStr,
                                fontSize = 10.sp,
                                color = SovanTextMuted
                            )
                        }
                        if (entry.note.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${entry.note}\"",
                                fontSize = 12.sp,
                                color = SovanTextMuted,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}
