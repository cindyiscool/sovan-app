package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoidPost
import com.example.ui.theme.SovanAccent
import com.example.ui.theme.SovanBackground
import com.example.ui.theme.SovanBorder
import com.example.ui.theme.SovanHeart
import com.example.ui.theme.SovanSurface
import com.example.ui.theme.SovanText
import com.example.ui.theme.SovanTextMuted
import com.example.ui.theme.SovanUserBubble
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VoidScreen(
    posts: List<VoidPost>,
    onSendHug: (Long) -> Unit,
    onPostVent: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewVentDialog by remember { mutableStateOf(false) }
    var ventText by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Heavy Heart") }

    val tags = listOf("Heavy Heart", "Late Night Thoughts", "Exhaustion", "Little Wins", "Gentle Reminder")
    val huggedPosts = remember { mutableStateMapOf<Long, Boolean>() }

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
            // Header Banner
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                Text(text = "🌌", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "The Void",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = SovanText
                                )
                                Text(
                                    text = "Anonymous Wall • No names • No comments • Only hugs",
                                    fontSize = 11.sp,
                                    color = SovanTextMuted
                                )
                            }
                        }

                        Button(
                            onClick = { showNewVentDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SovanUserBubble,
                                contentColor = SovanSurface
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("open_vent_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Vent",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Release", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SovanSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Release what weighs heavily on your shoulders. Everyone who sees this can only press 'Send a hug'. You are held in gentle silence.",
                            fontSize = 12.sp,
                            color = SovanTextMuted,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            // Anonymous Post Cards
            items(posts, key = { it.id }) { post ->
                VoidPostCard(
                    post = post,
                    isHugged = huggedPosts[post.id] == true,
                    onHugClick = {
                        huggedPosts[post.id] = true
                        onSendHug(post.id)
                    }
                )
            }
        }

        // FAB to post
        FloatingActionButton(
            onClick = { showNewVentDialog = true },
            containerColor = SovanUserBubble,
            contentColor = SovanSurface,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("floating_vent_btn")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Release vent")
        }
    }

    // New Vent Dialog
    if (showNewVentDialog) {
        AlertDialog(
            onDismissRequest = { showNewVentDialog = false },
            title = {
                Text(
                    text = "Release into The Void",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = SovanText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Everything is 100% anonymous. Your name, email, and identity will never be visible.",
                        fontSize = 12.sp,
                        color = SovanTextMuted
                    )

                    Text(
                        text = "Choose a tone:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SovanText
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tags) { tag ->
                            Surface(
                                color = if (selectedTag == tag) SovanUserBubble else SovanSurface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                                modifier = Modifier.clickable { selectedTag = tag }
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    color = if (selectedTag == tag) SovanSurface else SovanText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ventText,
                        onValueChange = { ventText = it },
                        placeholder = {
                            Text(
                                "Let out whatever feels heavy in your chest right now...",
                                fontSize = 13.sp,
                                color = SovanTextMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("void_vent_input"),
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ventText.isNotBlank()) {
                            onPostVent(ventText, selectedTag)
                            ventText = ""
                            showNewVentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SovanUserBubble,
                        contentColor = SovanSurface
                    ),
                    modifier = Modifier.testTag("submit_vent_button")
                ) {
                    Text("Release")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewVentDialog = false }) {
                    Text("Cancel", color = SovanTextMuted)
                }
            },
            containerColor = SovanSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun VoidPostCard(
    post: VoidPost,
    isHugged: Boolean,
    onHugClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isHugged) 1.25f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "hug_scale"
    )

    val timeFormatter = remember { SimpleDateFormat("MMM d • HH:mm", Locale.getDefault()) }
    val timeFormatted = remember(post.timestamp) { timeFormatter.format(Date(post.timestamp)) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SovanSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
        modifier = Modifier.fillMaxWidth()
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
                    color = SovanBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder)
                ) {
                    Text(
                        text = post.tag,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SovanTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = timeFormatted,
                    fontSize = 11.sp,
                    color = SovanTextMuted
                )
            }

            Text(
                text = "\"${post.content}\"",
                fontSize = 14.sp,
                color = SovanText,
                lineHeight = 21.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Anonymous Soul",
                    fontSize = 11.sp,
                    color = SovanTextMuted,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Surface(
                    color = if (isHugged) SovanUserBubble else SovanBackground,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isHugged) SovanUserBubble else SovanBorder
                    ),
                    modifier = Modifier
                        .clickable { onHugClick() }
                        .testTag("hug_btn_${post.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isHugged) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Hug",
                            tint = if (isHugged) SovanSurface else SovanHeart,
                            modifier = Modifier
                                .size(16.dp)
                                .scale(scale)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHugged) "Hugged (${post.hugsCount})" else "Send a hug (${post.hugsCount})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHugged) SovanSurface else SovanText
                        )
                    }
                }
            }
        }
    }
}
