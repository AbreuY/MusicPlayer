package app.github1552980358.android.musicplayer.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.adapter.SongListContentRecyclerViewAdapter
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumRoundDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_TITLE
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ARTIST
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ALBUM
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_DURATION
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ID
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_PRESENT
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_INFO
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListCoverDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.base.SongListCover
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.TimeExchange
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main_bottomsheet.cardView
import kotlinx.android.synthetic.main.activity_song_list.appBarLayout
import kotlinx.android.synthetic.main.activity_song_list.collapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_song_list.imageViewCover
import kotlinx.android.synthetic.main.activity_song_list.recyclerView
import kotlinx.android.synthetic.main.activity_song_list.textViewDescription
import kotlinx.android.synthetic.main.activity_song_list.textViewSubtitle
import kotlinx.android.synthetic.main.activity_song_list.textViewTitle
import kotlinx.android.synthetic.main.activity_song_list.toolbar
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.checkBoxPlay
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.imageView
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.linearLayoutBottom
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.textViewSubtitle_bottom_sheet
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.textViewTitle_bottom_sheet
import java.io.File
import java.io.ObjectInputStream

/**
 * [SongListActivity]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/24
 * @time    : 13:29
 **/

class SongListActivity: BaseAppCompatActivity(), TimeExchange {
    
    //private var currentTitle = ""
    //private var currentArtist = ""
    //private var currentId = ""
    //private var currentAlbum = ""
    //private var currentDuration = 0L
    
    /**
     * [bottomSheetBehavior]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    
    /**
     * [songListCover]
     * @author 1552980358
     * @since 0.1
     **/
    private var songListCover: SongListCover? = null
    
    /**
     * [songList]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var songList: SongList
    
    /**
     * [onCreate]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("SongListActivity", "onCreate")
    
        val songListInfo = intent?.getSerializableExtra(INTENT_SONG_LIST_INFO) as SongListInfo
        
        File(getExternalFilesDir(SongListCoverDir), songListInfo.listTitle).apply {
            @Suppress("LABEL_NAME_CLASH")
            if (!exists()) {
                window.statusBarColor = Color.WHITE
                window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                return@apply
            }
            
            // Load image and colours
            // 加载图片以及颜色
            inputStream().use { `is` ->
                ObjectInputStream(`is`).use { ois ->
                    songListCover = (ois.readObject() as SongListCover)
                }
            }
            
            window.statusBarColor = songListCover!!.backgroundColour
            
            @SuppressLint("InlinedApi")
            if (songListCover!!.isLight) {
                window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
            
        }
        
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_song_list)
        
        songListInfo.apply {
            toolbar.title = listTitle
            
            textViewTitle.text = listSize.toString()
            textViewSubtitle.text = getDateText(createDate)
            textViewDescription.text = description
            
            if (!hasCoverImage) {
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK)
                collapsingToolbarLayout.setExpandedTitleColor(Color.BLACK)
                return@apply
            }
            
            songListCover.apply {
                @Suppress("LABEL_NAME_CLASH")
                this ?: return@apply
                
                // Set colours
                // 设置颜色
                imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
                appBarLayout.setBackgroundColor(backgroundColour)
                textViewTitle.setTextColor(primaryTextColour)
                textViewSubtitle.setTextColor(secondaryTextColour)
                textViewDescription.setTextColor(secondaryTextColour)
                collapsingToolbarLayout.setCollapsedTitleTextColor(primaryTextColour)
                collapsingToolbarLayout.setExpandedTitleColor(primaryTextColour)
            }
            
        }
        
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        
        toolbar.navigationIcon?.setTint(if (songListCover != null) songListCover!!.primaryTextColour else Color.BLACK)
        
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        
        @Suppress("UNCHECKED_CAST")
        bottomSheetBehavior = BottomSheetBehavior.from(cardView) as BottomSheetBehavior<View>
        
        File(getExternalFilesDir(SongListDir), songListInfo.listTitle).apply {
            if (!exists()) {
                finish()
                return
            }
            
            inputStream().use { `is` ->
                ObjectInputStream(`is`).use { ois ->
                    songList = ois.readObject() as SongList
                }
                
            }
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SongListContentRecyclerViewAdapter(this, songList.listName, songList.audioList)
        
        checkBoxPlay.setOnClickListener {
            mediaControllerCompat.transportControls.apply {
                if (checkBoxPlay.isChecked) play() else pause()
            }
        }
    
        linearLayoutBottom.setOnClickListener {
            if (mediaControllerCompat.playbackState.state == PlaybackStateCompat.STATE_NONE) {
                return@setOnClickListener
            }
            startActivityForResult(
                Intent(this, AudioActivity::class.java)
                    .putExtra(INTENT_AUDIO_TITLE, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                    .putExtra(INTENT_AUDIO_ARTIST, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                    .putExtra(INTENT_AUDIO_ALBUM, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
                    .putExtra(INTENT_AUDIO_DURATION, mediaControllerCompat.metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
                    .putExtra(INTENT_AUDIO_ID, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)),
                0,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    imageView, "imageView"
                ).toBundle()
            )
        }
        
        if (!intent.getBooleanExtra(INTENT_AUDIO_PRESENT, false)) {
            textViewTitle_bottom_sheet.setText(R.string.songListActivity_bottom_sheet_title)
            textViewSubtitle_bottom_sheet.visibility = View.GONE
            return
        }
    
        textViewTitle_bottom_sheet.text = intent.getStringExtra(INTENT_AUDIO_TITLE)
        textViewSubtitle_bottom_sheet.text = intent.getStringExtra(INTENT_AUDIO_ARTIST)
        
    }
    
    override fun onResume() {
        super.onResume()
        Log.e("SongListActivity", "onResume")
    }
    
    /**
     * [onMetadataChanged]
     * @param metadata [MediaBrowserCompat]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        if (textViewSubtitle_bottom_sheet.visibility == View.GONE) {
            textViewSubtitle_bottom_sheet.visibility = View.VISIBLE
        }
        textViewTitle_bottom_sheet.text = metadata!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        textViewSubtitle_bottom_sheet.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        
        File(
            getExternalFilesDir(AlbumRoundDir),
            metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        ).apply {
            if (!exists()) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
                return
            }
            
            inputStream().use { `is` ->
                imageView.setImageBitmap(BitmapFactory.decodeStream(`is`))
            }
        }
        
    }
    
    /**
     * [onMetadataChanged]
     * @param state [PlaybackStateCompat]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        when (state?.state) {
            
            PlaybackStateCompat.STATE_BUFFERING, PlaybackStateCompat.STATE_PLAYING -> {
                checkBoxPlay.isChecked = true
            }
            
            PlaybackStateCompat.STATE_PAUSED -> {
                checkBoxPlay.isChecked = false
            }
            
        }
    }
    
    /**
     * [onMetadataChanged]
     * @param parentId [String]
     * @param children [MutableList]<[MediaBrowserCompat.MediaItem]>
     * @author 1552980358
     * @since 0.1
     **/
    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
        //
    }
    
    /**
     * [onConnected]
     * @param mediaControllerCompat [MediaControllerCompat]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onConnected(mediaControllerCompat: MediaControllerCompat) {
        when (mediaControllerCompat.playbackState.state) {
            
            PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                checkBoxPlay.isChecked = true
            }
            
            PlaybackStateCompat.STATE_PAUSED -> {
                checkBoxPlay.isChecked = false
            }
            
        }
    
    }
    
}