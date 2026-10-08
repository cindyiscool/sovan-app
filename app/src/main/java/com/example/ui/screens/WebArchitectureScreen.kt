package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.SovanBackground
import com.example.ui.theme.SovanBorder
import com.example.ui.theme.SovanBotBubble
import com.example.ui.theme.SovanSurface
import com.example.ui.theme.SovanText
import com.example.ui.theme.SovanTextMuted
import com.example.ui.theme.SovanUserBubble

@Composable
fun WebArchitectureScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SovanBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SovanUserBubble),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌐", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Web Architecture & App",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SovanText
                )
                Text(
                    text = "HTML, CSS, JS, Firebase & Gemini companion code",
                    fontSize = 11.sp,
                    color = SovanTextMuted
                )
            }
        }

        // Tab switcher: 0 = Live Web Preview, 1 = Code & Schema Architecture
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SovanSurface,
            contentColor = SovanUserBubble,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Preview, contentDescription = "Preview", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Web App", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = "Code", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Architecture & Schema", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            // Live Web app loaded from asset bundle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.allowFileAccess = true
                            webViewClient = WebViewClient()
                            loadUrl("file:///android_asset/web/index.html")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Architecture & Schema summary
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SovanSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Firestore Collections & Documents Structure",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SovanText
                        )
                        Text(
                            text = """
/users/{userId}
   • name: string
   • email: string
   • secondBrain: string (traumas, triggers, sensitive boundaries)
   • createdAt: timestamp

/users/{userId}/chatHistory/{messageId}
   • sender: "user" | "bot"
   • text: string
   • timestamp: timestamp

/users/{userId}/moodLogs/{moodId}
   • emoji: string
   • label: string
   • toneInfluence: string
   • timestamp: timestamp

/users/{userId}/timeCapsules/{capsuleId}
   • title: string
   • content: string
   • unlockDate: timestamp
   • isOpened: boolean

/voidPosts/{postId} (Public & Anonymous)
   • text: string (no user details)
   • hugsCount: number
   • tag: string
   • timestamp: timestamp
                            """.trimIndent(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = SovanText,
                            lineHeight = 16.sp
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SovanBotBubble),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🌸 Strict Design System Verification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SovanText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Main Background: #FFF5F5\n• Sidebar/Cards: #FFFFFF\n• Primary User Bubble: #E2B4BD\n• Bot Bubble: #F7D6D0\n• Text Color: #4A4A4A",
                            fontSize = 12.sp,
                            color = SovanText,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
