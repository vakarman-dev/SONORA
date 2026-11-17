
package com.sonora.music

import android.Manifest
import android.content.ComponentName
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : ComponentActivity() {

    private val songs = mutableListOf<Song>()

    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                loadSongs()
            }
        }

    private val playerListener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayerState()
        }

        override fun onMediaItemTransition(
            mediaItem: androidx.media3.common.MediaItem?,
            reason: Int
        ) {
            updatePlayerState()
        }
    }

    private fun updatePlayerState() {
        val player = controller ?: return

        val id = player.currentMediaItem
            ?.mediaId
            ?.toLongOrNull()

        val current = songs.firstOrNull { it.id == id }

        runOnUiThread {
            render()
            currentSong = current
            isPlaying = player.isPlaying
            render()
        }
    }

    private var currentSong: Song? = null
    private var isPlaying = false
    private var playerReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = SessionToken(
            this,
            ComponentName(this, PlaybackService::class.java)
        )

        controllerFuture =
            MediaController.Builder(this, token).buildAsync()

        controllerFuture?.addListener(
            {
                try {
                    controller = controllerFuture?.get()
                    controller?.addListener(playerListener)
                    playerReady = true
                    updatePlayerState()
                } catch (e: Exception) {
                    playerReady = false
                    render()
                }
            },
            ContextCompat.getMainExecutor(this)
        )

        render()
        requestMusicPermission()
    }

    private fun render() {
        runOnUiThread {
            setContent {
                SonoraApp(
                    songs = songs,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    playerReady = playerReady,

                    onPlaySong = { index ->
                        playSong(index)
                    },

                    onTogglePlayback = {
                        togglePlayback()
                    },

                    onNext = {
                        controller?.seekToNextMediaItem()
                    },

                    onPrevious = {
                        controller?.seekToPreviousMediaItem()
                    },

                    onRequestMusic = {
                        requestMusicPermission()
                    }
                )
            }
        }
    }

    private fun requestMusicPermission() {

        val permission =
            if (Build.VERSION.SDK_INT >= 33) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        if (
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadSongs()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun loadSongs() {

        val found = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        cursor?.use {

            val idColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

            val titleColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

            val artistColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            val albumColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            val albumIdColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            val durationColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {

                val id = it.getLong(idColumn)
                val albumId = it.getLong(albumIdColumn)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val art = if (albumId > 0) {
                    android.net.Uri.parse(
                        "content://media/external/audio/albumart/$albumId"
                    )
                } else null

                found.add(
                    Song(
                        id = id,
                        title = it.getString(titleColumn)
                            ?: "Unknown title",

                        artist = it.getString(artistColumn)
                            ?.takeUnless { value ->
                                value == "<unknown>"
                            } ?: "Unknown artist",

                        album = it.getString(albumColumn)
                            ?: "Unknown album",

                        duration = it.getLong(durationColumn),
                        uri = uri,
                        albumArt = art
                    )
                )
            }
        }

        songs.clear()
        songs.addAll(found)

        render()
    }

    private fun playSong(index: Int) {

        val player = controller ?: return

        if (index !in songs.indices) return

        player.setMediaItems(
            songs.map { it.toMediaItem() },
            index,
            0L
        )

        player.prepare()
        player.play()

        updatePlayerState()
    }

    private fun togglePlayback() {

        val player = controller ?: return

        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }

        updatePlayerState()
    }

    override fun onDestroy() {

        controller?.removeListener(playerListener)

        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }

        controller = null

        super.onDestroy()
    }
}
