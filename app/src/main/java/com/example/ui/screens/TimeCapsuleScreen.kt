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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.TimeCapsule
import com.example.ui.theme.SovanAccent
import com.example.ui.theme.SovanBackground
import com.example.ui.theme.SovanBorder
import com.example.ui.theme.SovanBotBubble
import com.example.ui.theme.SovanSurface
import com.example.ui.theme.SovanText
import com.example.ui.theme.SovanTextMuted
import com.example.ui.theme.SovanUserBubble
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class DurationOption(val label: String, val days: Int)

@Composable
fun TimeCapsuleScreen(
    capsules: List<TimeCapsule>,
    onCreateCapsule: (String, String, String, Int) -> Unit,
    onOpenCapsule: (TimeCapsule) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var readingCapsule by remember { mutableStateOf<TimeCapsule?>(null) }

    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(DurationOption("1 Month", 30)) }

    val durationOptions = listOf(
        DurationOption("1 Month", 30),
        DurationOption("3 Months", 90),
        DurationOption("6 Months", 180),
        DurationOption("1 Year", 365)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SovanBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SovanUserBubble),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⏳", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Time Capsule",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = SovanText
                            )
                            Text(
                                text = "Letters to your future self • Sealed until unlocked",
                                fontSize = 11.sp,
                                color = SovanTextMuted
                            )
                        }
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SovanUserBubble,
                            contentColor = SovanSurface
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("create_capsule_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Write", fontSize = 12.sp)
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SovanSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Write to who you will become. Capture what you are holding today, what you hope to heal from, or a gentle reminder to breathe. Sealed letters stay locked until their release date.",
                        fontSize = 12.sp,
                        color = SovanTextMuted,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            if (capsules.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "💌", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No time capsules yet.",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = SovanText
                            )
                            Text(
                                text = "Write your first letter to future you.",
                                fontSize = 12.sp,
                                color = SovanTextMuted
                            )
                        }
                    }
                }
            } else {
                items(capsules, key = { it.id }) { capsule ->
                    TimeCapsuleCard(
                        capsule = capsule,
                        onOpen = {
                            if (System.currentTimeMillis() >= capsule.unlockDateMs) {
                                onOpenCapsule(capsule)
                            }
                            readingCapsule = capsule
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = SovanUserBubble,
            contentColor = SovanSurface,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("floating_create_capsule")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Write letter")
        }
    }

    // Create Capsule Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Write to Your Future Self",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = SovanText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        placeholder = { Text("Title: e.g., A promise to myself", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SovanBackground,
                            unfocusedContainerColor = SovanBackground,
                            focusedBorderColor = SovanUserBubble,
                            unfocusedBorderColor = SovanBorder
                        )
                    )

                    Text(
                        text = "Seal duration:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SovanText
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(durationOptions) { opt ->
                            Surface(
                                color = if (selectedDuration == opt) SovanUserBubble else SovanSurface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                                modifier = Modifier.clickable { selectedDuration = opt }
                            ) {
                                Text(
                                    text = opt.label,
                                    fontSize = 11.sp,
                                    color = if (selectedDuration == opt) SovanSurface else SovanText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        placeholder = {
                            Text(
                                "Dear future me,\nRight now I am carrying...\nI hope you remember that...",
                                fontSize = 13.sp,
                                color = SovanTextMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SovanBackground,
                            unfocusedContainerColor = SovanBackground,
                            focusedBorderColor = SovanUserBubble,
                            unfocusedBorderColor = SovanBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (contentText.isNotBlank()) {
                            onCreateCapsule(
                                titleText,
                                contentText,
                                selectedDuration.label,
                                selectedDuration.days
                            )
                            titleText = ""
                            contentText = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SovanUserBubble,
                        contentColor = SovanSurface
                    )
                ) {
                    Text("Seal Letter 💌")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = SovanTextMuted)
                }
            },
            containerColor = SovanSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Read Capsule Modal
    readingCapsule?.let { capsule ->
        val isUnlocked = System.currentTimeMillis() >= capsule.unlockDateMs || capsule.isOpened
        val timeFormatter = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
        val unlockDateStr = remember(capsule.unlockDateMs) { timeFormatter.format(Date(capsule.unlockDateMs)) }

        AlertDialog(
            onDismissRequest = { readingCapsule = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isUnlocked) "✨ " else "🔒 ")
                    Text(
                        text = capsule.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = SovanText
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (isUnlocked) {
                        Text(
                            text = "Written on ${timeFormatter.format(Date(capsule.createdDateMs))}:",
                            fontSize = 11.sp,
                            color = SovanTextMuted
                        )
                        Text(
                            text = capsule.content,
                            fontSize = 14.sp,
                            color = SovanText,
                            lineHeight = 22.sp
                        )
                    } else {
                        Text(
                            text = "This capsule is sealed until $unlockDateStr.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SovanText
                        )
                        Text(
                            text = "Preserving your feelings in time allows future you to witness how much strength you've gathered. Please wait until its unlock date to read.",
                            fontSize = 12.sp,
                            color = SovanTextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { readingCapsule = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SovanUserBubble,
                        contentColor = SovanSurface
                    )
                ) {
                    Text("Close")
                }
            },
            containerColor = SovanSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun TimeCapsuleCard(
    capsule: TimeCapsule,
    onOpen: () -> Unit
) {
    val now = System.currentTimeMillis()
    val isUnlocked = now >= capsule.unlockDateMs || capsule.isOpened
    val daysRemaining = max(1L, (capsule.unlockDateMs - now) / (1000 * 60 * 60 * 24))
    val timeFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SovanSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isUnlocked) SovanUserBubble else SovanBotBubble,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Status",
                            tint = SovanText,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isUnlocked) "Unlocked ✨" else "$daysRemaining days remaining",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SovanText
                        )
                    }
                }

                Text(
                    text = "Opens ${timeFormatter.format(Date(capsule.unlockDateMs))}",
                    fontSize = 11.sp,
                    color = SovanTextMuted
                )
            }

            Text(
                text = capsule.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = SovanText
            )

            Text(
                text = if (isUnlocked) capsule.content else "Locked digital letter • Written to your future self",
                fontSize = 12.sp,
                color = SovanTextMuted,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (isUnlocked) "Read Letter →" else "View Countdown →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SovanAccent
                )
            }
        }
    }
}
