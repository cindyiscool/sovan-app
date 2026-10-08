package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.TenantDialog
import com.example.ui.theme.SovanAccent
import com.example.ui.theme.SovanBackground
import com.example.ui.theme.SovanBorder
import com.example.ui.theme.SovanBotBubble
import com.example.ui.theme.SovanSurface
import com.example.ui.theme.SovanText
import com.example.ui.theme.SovanTextMuted
import com.example.ui.theme.SovanUserBubble
import com.example.ui.viewmodel.SovanScreen
import com.example.ui.viewmodel.SovanViewModel
import kotlinx.coroutines.launch

@Composable
fun SovanMainScreen(
    viewModel: SovanViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()
    val activeUserId by viewModel.activeUserId.collectAsState()
    val allProfiles by viewModel.allProfiles.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isBotTyping by viewModel.isBotTyping.collectAsState()
    val voidPosts by viewModel.voidPosts.collectAsState()
    val moods by viewModel.moods.collectAsState()
    val latestMood by viewModel.latestMood.collectAsState()
    val timeCapsules by viewModel.timeCapsules.collectAsState()
    val showTenantDialog by viewModel.showTenantDialog.collectAsState()
    val snackbarMsg by viewModel.snackbarEvent.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 720.dp

        if (isWide) {
            // Tablet / Desktop Canonical Messenger Layout: Persistent Left Sidebar + Main View
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SovanBackground)
            ) {
                // Persistent Left Sidebar
                SovanSidebarContent(
                    activeProfile = activeProfile,
                    currentScreen = currentScreen,
                    latestMoodLabel = latestMood?.let { "${it.emoji} ${it.label}" } ?: "Check in",
                    onSelectScreen = { viewModel.navigateTo(it) },
                    onOpenTenantDialog = { viewModel.openTenantDialog() },
                    onOpenMood = { viewModel.navigateTo(SovanScreen.MOOD) },
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                )

                // Main Area
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    SovanActiveView(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        activeProfile = activeProfile,
                        messages = messages,
                        latestMood = latestMood,
                        isBotTyping = isBotTyping,
                        voidPosts = voidPosts,
                        moods = moods,
                        timeCapsules = timeCapsules
                    )
                }
            }
        } else {
            // Mobile Messenger Layout: Top bar with Drawer & Bottom Navigation Bar
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = SovanSurface,
                        drawerContentColor = SovanText,
                        modifier = Modifier.width(300.dp)
                    ) {
                        SovanSidebarContent(
                            activeProfile = activeProfile,
                            currentScreen = currentScreen,
                            latestMoodLabel = latestMood?.let { "${it.emoji} ${it.label}" } ?: "Check in",
                            onSelectScreen = {
                                viewModel.navigateTo(it)
                                scope.launch { drawerState.close() }
                            },
                            onOpenTenantDialog = {
                                viewModel.openTenantDialog()
                                scope.launch { drawerState.close() }
                            },
                            onOpenMood = {
                                viewModel.navigateTo(SovanScreen.MOOD)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        Surface(
                            color = SovanSurface,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("menu_drawer_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            tint = SovanText
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentScreen.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = SovanText
                                    )
                                }

                                Surface(
                                    color = SovanBackground,
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                                    modifier = Modifier
                                        .clickable { viewModel.openTenantDialog() }
                                        .testTag("mobile_tenant_pill")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = activeProfile?.avatarEmoji ?: "🌸", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = activeProfile?.name ?: "Vivian",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SovanText
                                        )
                                    }
                                }
                            }
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = SovanSurface,
                            contentColor = SovanText,
                            tonalElevation = 4.dp
                        ) {
                            val items = listOf(
                                SovanScreen.CHAT,
                                SovanScreen.SECOND_BRAIN,
                                SovanScreen.VOID,
                                SovanScreen.MOOD,
                                SovanScreen.TIME_CAPSULE
                            )
                            items.forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.navigateTo(screen) },
                                    icon = {
                                        Text(text = screen.icon, fontSize = 18.sp)
                                    },
                                    label = {
                                        Text(
                                            text = screen.title.split(" ").first(),
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SovanText,
                                        selectedTextColor = SovanText,
                                        indicatorColor = SovanUserBubble,
                                        unselectedIconColor = SovanTextMuted,
                                        unselectedTextColor = SovanTextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        SovanActiveView(
                            currentScreen = currentScreen,
                            viewModel = viewModel,
                            activeProfile = activeProfile,
                            messages = messages,
                            latestMood = latestMood,
                            isBotTyping = isBotTyping,
                            voidPosts = voidPosts,
                            moods = moods,
                            timeCapsules = timeCapsules
                        )
                    }
                }
            }
        }
    }

    // Multi-tenant Switcher Dialog
    if (showTenantDialog) {
        TenantDialog(
            activeUserId = activeUserId,
            allProfiles = allProfiles,
            onSelectProfile = { viewModel.switchUserSpace(it) },
            onCreateProfile = { name, email, emoji ->
                viewModel.createNewUserSpace(name, email, emoji)
            },
            onDismiss = { viewModel.closeTenantDialog() }
        )
    }
}

@Composable
fun SovanSidebarContent(
    activeProfile: UserProfile?,
    currentScreen: SovanScreen,
    latestMoodLabel: String,
    onSelectScreen: (SovanScreen) -> Unit,
    onOpenTenantDialog: () -> Unit,
    onOpenMood: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SovanSurface,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Sovan Brand
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SovanBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌸", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Sovan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = SovanText
                        )
                        Text(
                            text = "Mental Health Sanctuary",
                            fontSize = 10.sp,
                            color = SovanTextMuted
                        )
                    }
                }

                // User Profile & Space Card
                Surface(
                    color = SovanBackground,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTenantDialog() }
                        .testTag("sidebar_user_profile")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SovanUserBubble),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = activeProfile?.avatarEmoji ?: "🌸", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeProfile?.name ?: "Vivian",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SovanText
                            )
                            Text(
                                text = "Private space 🔒",
                                fontSize = 10.sp,
                                color = SovanTextMuted
                            )
                        }
                        Text(text = "Switch ⚙️", fontSize = 10.sp, color = SovanAccent)
                    }
                }

                // Nav Links
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SovanScreen.entries.forEach { screen ->
                        val isSelected = currentScreen == screen
                        Surface(
                            color = if (isSelected) SovanUserBubble else SovanSurface,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectScreen(screen) }
                                .testTag("sidebar_nav_${screen.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = screen.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = screen.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SovanSurface else SovanText
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Mood Quick Card in Sidebar
            Surface(
                color = SovanBackground,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SovanBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenMood() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TODAY'S MINDSET",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SovanTextMuted
                        )
                        Text(
                            text = latestMoodLabel,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = SovanText
                        )
                    }
                    Text(text = "Check in →", fontSize = 11.sp, color = SovanAccent)
                }
            }
        }
    }
}

@Composable
fun SovanActiveView(
    currentScreen: SovanScreen,
    viewModel: SovanViewModel,
    activeProfile: UserProfile?,
    messages: List<com.example.data.model.ChatMessage>,
    latestMood: com.example.data.model.MoodEntry?,
    isBotTyping: Boolean,
    voidPosts: List<com.example.data.model.VoidPost>,
    moods: List<com.example.data.model.MoodEntry>,
    timeCapsules: List<com.example.data.model.TimeCapsule>
) {
    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            SovanScreen.CHAT -> ChatScreen(
                userProfile = activeProfile,
                messages = messages,
                todayMood = latestMood,
                isBotTyping = isBotTyping,
                onSendMessage = { viewModel.sendMessageToTieuVan(it) },
                onClearChat = { viewModel.clearChat() },
                onNavigateToSecondBrain = { viewModel.navigateTo(SovanScreen.SECOND_BRAIN) },
                onOpenMoodPicker = { viewModel.navigateTo(SovanScreen.MOOD) }
            )
            SovanScreen.SECOND_BRAIN -> SecondBrainScreen(
                userProfile = activeProfile,
                onSaveSecondBrain = { viewModel.saveSecondBrain(it) }
            )
            SovanScreen.VOID -> VoidScreen(
                posts = voidPosts,
                onSendHug = { viewModel.sendHugToPost(it) },
                onPostVent = { text, tag -> viewModel.postToTheVoid(text, tag) }
            )
            SovanScreen.MOOD -> MoodTrackerScreen(
                moods = moods,
                latestMood = latestMood,
                moodOptions = viewModel.moodOptions,
                onLogMood = { opt, note -> viewModel.logMood(opt, note) }
            )
            SovanScreen.TIME_CAPSULE -> TimeCapsuleScreen(
                capsules = timeCapsules,
                onCreateCapsule = { title, content, label, days ->
                    viewModel.createTimeCapsule(title, content, label, days)
                },
                onOpenCapsule = { viewModel.openTimeCapsule(it) }
            )
            SovanScreen.WEB_CODE -> WebArchitectureScreen()
        }
    }
}
