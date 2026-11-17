
package com.sonora.music

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val Night = Color(0xFF071421)
private val Panel = Color(0xFF122A3D)
private val Cyan = Color(0xFF36E2E9)
private val Blue = Color(0xFF438BFF)
private val Muted = Color(0xFF91A9BA)

@Composable
fun SonoraApp(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    playerReady: Boolean,
    onPlaySong: (Int) -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRequestMusic: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Night,
            surface = Panel,
            primary = Cyan,
            secondary = Blue
        )
    ) {
        Scaffold(
            containerColor = Night,
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0B1B2A)
                ) {
                    val tabs = listOf(
                        Icons.Default.Home,
                        Icons.Default.LibraryMusic,
                        Icons.Default.GraphicEq,
                        Icons.Default.QueueMusic,
                        Icons.Default.Settings
                    )

                    val labels = listOf(
                        "Home",
                        "Library",
                        "Visual",
                        "Playlists",
                        "Settings"
                    )

                    tabs.forEachIndexed { index, icon ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(icon, contentDescription = labels[index])
                            },
                            label = {
                                Text(labels[index], fontSize = 10.sp)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Cyan,
                                selectedTextColor = Cyan,
                                indicatorColor = Color(0xFF173B50),
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF102A40),
                                Night,
                                Color(0xFF08111D)
                            )
                        )
                    )
                    .padding(padding)
            ) {
                when (selectedTab) {

                    0 -> HomeScreen(
                        songs,
                        currentSong,
                        isPlaying,
                        playerReady,
                        onPlaySong,
                        onTogglePlayback,
                        onNext,
                        onPrevious,
                        onOpenLibrary = { selectedTab = 1 }
                    )

                    1 -> LibraryScreen(
                        songs,
                        search,
                        { search = it },
                        onPlaySong,
                        onRequestMusic
                    )

                    2 -> VisualizerScreen(
                        currentSong,
                        isPlaying
                    )

                    3 -> EmptyScreen(
                        "Your playlists",
                        "Your music collections will appear here.",
                        Icons.Default.QueueMusic
                    )

                    else -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    subtitle: String
) {
    Column(
        Modifier.padding(
            horizontal = 22.dp,
            vertical = 18.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Cyan, Blue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.GraphicEq,
                    null,
                    tint = Night
                )
            }

            Spacer(Modifier.width(11.dp))

            Text(
                "SONORA",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.weight(1f))

            Icon(
                Icons.Default.MoreHoriz,
                null,
                tint = Muted
            )
        }

        Spacer(Modifier.height(23.dp))

        Text(
            title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            subtitle,
            color = Muted,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun HomeScreen(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    playerReady: Boolean,
    onPlaySong: (Int) -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onOpenLibrary: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {

        Header(
            "Feel the sound",
            "Your music. Your atmosphere."
        )

        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                GlassCard {

                    Column(
                        Modifier.padding(18.dp)
                    ) {

                        Text(
                            "NOW PLAYING",
                            color = Cyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(Modifier.height(15.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            AlbumArt(
                                currentSong,
                                Modifier.size(86.dp)
                            )

                            Spacer(Modifier.width(14.dp))

                            Column(
                                Modifier.weight(1f)
                            ) {

                                Text(
                                    currentSong?.title
                                        ?: "Choose your first track",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    currentSong?.artist
                                        ?: "Your library is waiting",
                                    color = Muted,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )

                                Spacer(Modifier.height(9.dp))

                                Text(
                                    if (playerReady)
                                        "PLAYER CONNECTED"
                                    else
                                        "CONNECTING PLAYER...",
                                    color = Cyan,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            IconButton(onClick = onPrevious) {
                                Icon(
                                    Icons.Default.SkipPrevious,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(
                                onClick = onTogglePlayback,
                                modifier = Modifier
                                    .size(56.dp)
                                    .shadow(12.dp, CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Cyan, Blue)
                                        ),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    if (isPlaying)
                                        Icons.Default.Pause
                                    else
                                        Icons.Default.PlayArrow,
                                    null,
                                    tint = Night,
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            IconButton(onClick = onNext) {
                                Icon(
                                    Icons.Default.SkipNext,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your library",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.weight(1f))

                    TextButton(onClick = onOpenLibrary) {
                        Text("See all", color = Cyan)
                    }
                }
            }

            if (songs.isEmpty()) {

                item {
                    GlassCard {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LibraryMusic,
                                null,
                                tint = Cyan,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "No music found yet",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Add music to your device.",
                                color = Muted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

            } else {

                itemsIndexed(songs.take(5)) { index, song ->
                    SongRow(song) {
                        onPlaySong(index)
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryScreen(
    songs: List<Song>,
    search: String,
    onSearch: (String) -> Unit,
    onPlaySong: (Int) -> Unit,
    onRequestMusic: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {

        Header(
            "Your library",
            "${songs.size} tracks on this device"
        )

        OutlinedTextField(
            value = search,
            onValueChange = onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = {
                Text(
                    "Search music...",
                    color = Muted
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    null,
                    tint = Cyan
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cyan,
                unfocusedBorderColor = Color(0xFF29465B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Cyan
            )
        )

        Spacer(Modifier.height(14.dp))

        val filtered = songs.withIndex().filter {
            it.value.title.contains(search, true) ||
                it.value.artist.contains(search, true)
        }

        if (filtered.isEmpty()) {

            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.MusicOff,
                        null,
                        tint = Muted,
                        modifier = Modifier.size(42.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        if (songs.isEmpty())
                            "No tracks available"
                        else
                            "Nothing found",
                        color = Color.White
                    )

                    if (songs.isEmpty()) {
                        TextButton(onClick = onRequestMusic) {
                            Text("Check music access", color = Cyan)
                        }
                    }
                }
            }

        } else {

            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 4.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filtered) { _, item ->
                    SongRow(item.value) {
                        onPlaySong(item.index)
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualizerScreen(
    currentSong: Song?,
    isPlaying: Boolean
) {
    val transition = rememberInfiniteTransition(
        label = "visualizer"
    )

    val heights = (0..25).map { index ->

        val height by transition.animateFloat(
            initialValue = 12f,
            targetValue = 35f + (index % 6) * 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 500 + index * 20
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$index"
        )

        height
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "SOUND VISUALIZER",
            color = Cyan,
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(28.dp))

        GlassCard {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    heights.forEach { height ->

                        Box(
                            Modifier
                                .padding(horizontal = 2.dp)
                                .width(5.dp)
                                .height(
                                    if (isPlaying) height.dp
                                    else 8.dp
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Cyan, Blue)
                                    )
                                )
                        )
                    }
                }

                Text(
                    currentSong?.title ?: "Waiting for music",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Text(
                    currentSong?.artist ?: "Play a track to begin",
                    color = Muted,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            "Animated visualizer preview",
            color = Muted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SettingsScreen() {
    Column(Modifier.fillMaxSize()) {

        Header(
            "Settings",
            "Make SONORA yours"
        )

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SettingsRow(
                Icons.Default.Palette,
                "Appearance",
                "Glass dark theme"
            )

            SettingsRow(
                Icons.Default.Equalizer,
                "Audio",
                "Equalizer — coming soon"
            )

            SettingsRow(
                Icons.Default.Notifications,
                "Notifications",
                "Playback controls"
            )

            SettingsRow(
                Icons.Default.Info,
                "About SONORA",
                "Version 1.0"
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    GlassCard {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(icon, null, tint = Cyan)

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    subtitle,
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.weight(1f))

            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = Muted
            )
        }
    }
}

@Composable
private fun EmptyScreen(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(Modifier.fillMaxSize()) {

        Header(title, description)

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icon,
                    null,
                    tint = Cyan,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Coming soon",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "This section is planned for the next update.",
                    color = Muted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SongRow(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x66162D40))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AlbumArt(
            song,
            Modifier.size(48.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {

            Text(
                song.title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )

            Text(
                song.artist,
                color = Muted,
                maxLines = 1,
                fontSize = 12.sp
            )
        }

        Text(
            formatDuration(song.duration),
            color = Muted,
            fontSize = 11.sp
        )

        Spacer(Modifier.width(5.dp))

        Icon(
            Icons.Default.MoreHoriz,
            null,
            tint = Muted
        )
    }
}

@Composable
private fun AlbumArt(
    song: Song?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1CCDD8),
                        Color(0xFF2456A8),
                        Color(0xFF5B4A9D)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        if (song?.albumArt != null) {

            AsyncImage(
                model = song.albumArt,
                contentDescription = song.album,
                modifier = Modifier.fillMaxSize()
            )

        } else {

            Icon(
                Icons.Default.GraphicEq,
                null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun GlassCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                14.dp,
                RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xCC19364B),
                        Color(0xAA102338),
                        Color(0x881A2940)
                    )
                )
            )
            .padding(1.dp)
            .background(
                Color(0x331C4560),
                RoundedCornerShape(23.dp)
            ),
        content = content
    )
}

private fun formatDuration(durationMs: Long): String {

    val seconds = durationMs / 1000

    return "%d:%02d".format(
        seconds / 60,
        seconds % 60
    )
}
