package app.fokkusu.music

import android.app.Application
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.os.Handler
import app.fokkusu.music.base.Constants.Companion.APPLICATION_MEDIA_SCAN_COMPLETE
import app.fokkusu.music.base.Constants.Companion.Dir_Cover
import app.fokkusu.music.base.Constants.Companion.Dir_Lyric
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_INIT
import app.fokkusu.music.service.PlayService
import java.io.File

/**
 * @File    : Application
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 5:23 PM
 **/

class Application : Application() {
    companion object {
        val handler by lazy { Handler() }
        
        var getApplication: app.fokkusu.music.Application? = null
            private set
            get() = field!!
        
        fun getContext() = getApplication!!
    
        val extDataDir_cover by lazy { File(getContext().externalCacheDir!!.absolutePath.plus(File.separator).plus(Dir_Cover)) }
        val extDataDir_lyric by lazy { File(getContext().externalCacheDir!!.absolutePath.plus(File.separator).plus(Dir_Lyric)) }
        var isScanComplete = false
        
        fun onScanMedia() = getApplication!!.onScanMedia()
    }
    
    init {
        getApplication = this
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Init Play Service
        startService(Intent(this, PlayService::class.java).putExtra(SERVICE_INTENT_CONTENT, SERVICE_INTENT_INIT))
        
        // Making Dirs
        if (!extDataDir_cover.exists())
            extDataDir_cover.mkdirs()
        if (!extDataDir_lyric.exists())
            extDataDir_lyric.mkdirs()
        
        handler
    
        onScanMedia()
    }
    
    private fun onScanMedia() {
        var mediaScannerConnection: MediaScannerConnection? = null
        val mediaScannerConnectionClient = object : MediaScannerConnection.MediaScannerConnectionClient {
            override fun onMediaScannerConnected() {
                @Suppress("DEPRECATION")
                mediaScannerConnection?.scanFile("file://".plus(Environment.getExternalStorageDirectory()!!.absolutePath), "audio/*")
            }
        
            override fun onScanCompleted(path: String?, uri: Uri?) {
                isScanComplete = true
                sendBroadcast(Intent(APPLICATION_MEDIA_SCAN_COMPLETE))
                mediaScannerConnection?.disconnect()
            }
        }
    
        mediaScannerConnection = MediaScannerConnection(this, mediaScannerConnectionClient).apply {
            connect()
        }
    }
    
}