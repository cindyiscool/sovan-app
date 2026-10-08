package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.theme.SovanAccent
import com.example.ui.theme.SovanBackground
import com.example.ui.theme.SovanBorder
import com.example.ui.theme.SovanBotBubble
import com.example.ui.theme.SovanSurface
import com.example.ui.theme.SovanText
import com.example.ui.theme.SovanTextMuted
import com.example.ui.theme.SovanUserBubble

@Composable
fun SecondBrainScreen(
    userProfile: UserProfile?,
    onSaveSecondBrain: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var brainText by remember { mutableStateOf("") }
    var isDirty by remember { mutableStateOf(false) }

    LaunchedEffect(userProfile?.secondBrain) {
        brainText = userProfile?.secondBrain ?: ""
        isDirty = false
    }

    val quickPrompts = listOf(
        "⚡ Conflict: When voices are raised, I tend to freeze and shut down.",
        "🌧️ Burnout: I constantly feel guilty whenever I take time to rest.",
        "🤍 Needs: I respond best to patient silence and gentle, one-step questions.",
        "🌿 Sensitive: Please avoid giving toxic positivity or 'everything happens for a reason'."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SovanBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SovanUserBubble),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Second Brain",
                    tint = SovanSurface,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Second Brain",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = SovanText
                )
                Text(
                    text = "Private sanctuary context for Tiểu Vân",
                    fontSize = 12.sp,
                    color = SovanTextMuted
                )
            }
        }

        // Privacy Guarantee Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SovanSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Privacy Shield",
                    tint = SovanAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Why this exists",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = SovanText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You should never have to re-explain your painful history, triggers, or boundaries each time you need someone to talk to. Tiểu Vân silently weaves this context into every conversation.",
                        fontSize = 12.sp,
                        color = SovanTextMuted,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Quick Suggestion Chips to append to Second Brain
        Text(
            text = "Tap to add gentle prompts:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SovanText
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    color = SovanSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                    modifier = Modifier.clickable {
                        brainText = if (brainText.isBlank()) prompt else "$brainText\n\n$prompt"
                        isDirty = true
                    }
                ) {
                    Text(
                        text = prompt,
                        fontSize = 12.sp,
                        color = SovanText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Main Second Brain Editor
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SovanSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Personal Background & Triggers",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = SovanText
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = SovanAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Private space",
                            fontSize = 11.sp,
                            color = SovanAccent
                        )
                    }
                }

                OutlinedTextField(
                    value = brainText,
                    onValueChange = {
                        brainText = it
                        isDirty = true
                    },
                    placeholder = {
                        Text(
                            "Write about: what triggers your stress, childhood memories you hold, communication styles you dislike, or fears that keep you awake at night...",
                            fontSize = 13.sp,
                            color = SovanTextMuted,
                            lineHeight = 18.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("second_brain_input"),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${brainText.length} characters",
                        fontSize = 11.sp,
                        color = SovanTextMuted
                    )

                    Button(
                        onClick = {
                            onSaveSecondBrain(brainText)
                            isDirty = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SovanUserBubble,
                            contentColor = SovanSurface
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("save_second_brain_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isDirty) "Save Changes" else "Saved ✓", fontSize = 13.sp)
                    }
                }
            }
        }

        // Live AI Injection Preview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SovanBotBubble),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌸 How Tiểu Vân reads this:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SovanText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (brainText.isBlank()) {
                        "\"Tiểu Vân is waiting for your thoughts. When you add context here, she will automatically customize her questions to honor your sensitive boundaries.\""
                    } else {
                        "\"Tiểu Vân understands that you carry: ${brainText.take(120)}... She will never push you into panic and will tailor her presence to match your comfort.\""
                    },
                    fontSize = 12.sp,
                    color = SovanText,
                    lineHeight = 17.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
