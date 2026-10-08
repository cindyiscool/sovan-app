package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.UserProfile
import com.example.ui.theme.SovanBackground
import com.example.ui.theme.SovanBorder
import com.example.ui.theme.SovanBotBubble
import com.example.ui.theme.SovanSurface
import com.example.ui.theme.SovanText
import com.example.ui.theme.SovanTextMuted
import com.example.ui.theme.SovanUserBubble

@Composable
fun TenantDialog(
    activeUserId: String,
    allProfiles: List<UserProfile>,
    onSelectProfile: (String) -> Unit,
    onCreateProfile: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var isCreatingNew by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🌸") }

    val emojis = listOf("🌸", "🌿", "✨", "☁️", "🌊", "🌙")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isCreatingNew) "Create Private Space" else "Switch Private Space",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = SovanText
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Every soul gets a completely isolated sanctuary. Second Brain, chat history, and time capsules are private to each space.",
                    fontSize = 12.sp,
                    color = SovanTextMuted,
                    lineHeight = 17.sp
                )

                if (!isCreatingNew) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ) {
                        items(allProfiles, key = { it.id }) { profile ->
                            val isSelected = profile.id == activeUserId
                            Surface(
                                color = if (isSelected) SovanBotBubble else SovanSurface,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) SovanUserBubble else SovanBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectProfile(profile.id) }
                                    .testTag("tenant_item_${profile.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = profile.avatarEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = profile.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = SovanText
                                        )
                                        Text(
                                            text = profile.email,
                                            fontSize = 11.sp,
                                            color = SovanTextMuted
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = SovanUserBubble,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { isCreatingNew = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SovanSurface,
                            contentColor = SovanText
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_new_space_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add New Private Space", fontSize = 13.sp)
                    }
                } else {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = { Text("Your preferred name...", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SovanBackground,
                            unfocusedContainerColor = SovanBackground,
                            focusedBorderColor = SovanUserBubble,
                            unfocusedBorderColor = SovanBorder
                        )
                    )

                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        placeholder = { Text("Email (e.g. alex@sovan.app)", fontSize = 13.sp) },
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
                        text = "Pick your avatar emblem:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SovanText
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(emojis) { emoji ->
                            val isSel = selectedEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) SovanUserBubble else SovanBackground)
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isCreatingNew) {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onCreateProfile(newName, newEmail, selectedEmoji)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SovanUserBubble,
                        contentColor = SovanSurface
                    )
                ) {
                    Text("Create Space")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Done", color = SovanText)
                }
            }
        },
        dismissButton = {
            if (isCreatingNew) {
                TextButton(onClick = { isCreatingNew = false }) {
                    Text("Back", color = SovanTextMuted)
                }
            }
        },
        containerColor = SovanSurface,
        shape = RoundedCornerShape(20.dp)
    )
}
