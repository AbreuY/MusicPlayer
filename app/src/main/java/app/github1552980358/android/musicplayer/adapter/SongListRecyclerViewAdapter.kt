package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.MainActivity
import app.github1552980358.android.musicplayer.activity.SongListActivity
import app.github1552980358.android.musicplayer.activity.SongListEditingActivity
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ALBUM
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ARTIST
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_DURATION
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ID
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_PRESENT
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_TITLE
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_INFO
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_POS
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListCoverDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListFile
import app.github1552980358.android.musicplayer.base.SongListCover
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.SongListInfo.Companion.songListInfoList
import lib.github1552980358.ktExtension.android.kotlin.toBitmap
import lib.github1552980358.ktExtension.jvm.io.readObjectAs
import lib.github1552980358.ktExtension.jvm.io.writeObject
import java.io.File

/**
 * [SongListRecyclerViewAdapter]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/20
 * @time    : 11:01
 **/

class SongListRecyclerViewAdapter(
    arrayList: ArrayList<SongListInfo>,
    private val fragment: Fragment
): Adapter<SongListRecyclerViewAdapter.ViewHolder>() {
    
    /**
     * [data]
     **/
    private var data = arrayList
    
    /**
     * [updateList]
     * @author 1552980358
     * @since 0.1
     **/
    fun updateList(arrayList: ArrayList<SongListInfo>) {
        data = arrayList
        notifyDataSetChanged()
    }
    
    /**
     * [onCreateViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (parent.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_song_list, parent, false)
        )
    }
    
    /**
     * [getItemCount]
     * @author 1552980358
     * @since 0.1
     **/
    override fun getItemCount(): Int {
        return data.size
    }
    
    /**
     * [onBindViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text = data[position].listTitle
        holder.textViewSubtitle.text = data[position].listSize.toString()
        holder.relativeLayoutRoot.setOnClickListener {
            fragment.startActivity(
                Intent(fragment.requireContext(), SongListActivity::class.java)
                    .putExtra(INTENT_SONG_LIST_INFO, data[position])
                    .apply {
                        putExtra(INTENT_SONG_LIST_POS, position)
                        if ((fragment.requireActivity() as MainActivity).mediaControllerCompat.metadata == null) {
                            putExtra(INTENT_AUDIO_PRESENT, false)
                            return@apply
                        }
                        putExtra(INTENT_AUDIO_PRESENT, true)
                        putExtra(
                            INTENT_AUDIO_TITLE,
                            (fragment.requireActivity() as MainActivity)
                                .mediaControllerCompat
                                .metadata
                                .getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                        )
                        putExtra(
                            INTENT_AUDIO_ARTIST,
                            (fragment.requireActivity() as MainActivity)
                                .mediaControllerCompat
                                .metadata
                                .getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                        )
                        putExtra(
                            INTENT_AUDIO_ALBUM,
                            (fragment.requireActivity() as MainActivity)
                                .mediaControllerCompat
                                .metadata
                                .getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
                        )
                        putExtra(
                            INTENT_AUDIO_ID,
                            (fragment.requireActivity() as MainActivity)
                                .mediaControllerCompat
                                .metadata
                                .getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        )
                        putExtra(
                            INTENT_AUDIO_DURATION,
                            (fragment.requireActivity() as MainActivity)
                                .mediaControllerCompat
                                .metadata
                                .getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                        )
                    },
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    fragment.requireActivity(),
                    holder.imageViewCover, "imageView"
                ).toBundle()
            )
        }
        holder.imageButtonOpts.setOnClickListener { imageButtonOpts ->
            PopupMenu(imageButtonOpts.context, imageButtonOpts).apply {
                inflate(R.menu.menu_song_list)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
    
                        R.id.menu_edit_list -> {
                            fragment.startActivityForResult(
                                Intent(fragment.requireContext(), SongListEditingActivity::class.java)
                                    .putExtra(INTENT_SONG_LIST_INFO, data[position])
                                    .putExtra(INTENT_SONG_LIST_POS, position),
                                1
                            )
                        }
    
                        R.id.menu_delete_list -> {
                            File(
                                imageButtonOpts.context.getExternalFilesDir(SongListCoverDir),
                                songListInfoList[position].listTitle
                            ).apply {
                                if (exists()) {
                                    delete()
                                }
                            }
                            File(
                                imageButtonOpts.context.getExternalFilesDir(SongListDir),
                                songListInfoList.removeAt(position).listTitle
                            ).apply {
                                if (exists()) {
                                    delete()
                                }
                            }
                            File(imageButtonOpts.context.getExternalFilesDir(AudioDataDir), SongListFile).apply {
                                delete()
                                createNewFile()
                                writeObject(songListInfoList)
                                /**
                                 * outputStream().os { os ->
                                 *     ObjectOutputStream(os).os { oos ->
                                 *         oos.writeObject(songListInfoList)
                                 *     }
                                 * }
                                 **/
                            }
                            notifyDataSetChanged()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }
        if (!data[position].hasCoverImage) {
            return
        }
        
        File(fragment.requireContext().getExternalFilesDir(SongListCoverDir), data[position].listTitle).apply {
            if (!exists()) {
                return
            }
    
            readObjectAs<SongListCover>()?.apply {
                holder.imageViewCover.setImageBitmap(image.toBitmap())
            }
            /**
             * inputStream().use { `is` ->
             *     ObjectInputStream(`is`).use { ois ->
             *         (ois.readObject() as SongListCover).image.apply {
             *             holder.imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(this, 0, size))
             *         }
             *     }
             * }
             **/
        }
        
    }
    
    /**
     * [ViewHolder]
     * @param view [View]
     * @author 1552980358
     * @since 0.1
     **/
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        /**
         * [textViewTitle]
         * @author 1552980358
         * @since 0.1
         **/
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)!!
    
        /**
         * [textViewSubtitle]
         * @author 1552980358
         * @since 0.1
         **/
        val textViewSubtitle: TextView = view.findViewById(R.id.textViewSubtitle)!!
    
        /**
         * [relativeLayoutRoot]
         * @author 1552980358
         * @since 0.1
         **/
        val relativeLayoutRoot: RelativeLayout = view.findViewById(R.id.relativeLayoutRoot)!!
    
        /**
         * [imageViewCover]
         * @author 1552980358
         * @since 0.1
         **/
        val imageViewCover: ImageView = view.findViewById(R.id.imageViewCover)!!
    
        /**
         * [imageButtonOpts]
         * @author 1552980358
         * @since 0.1
         **/
        val imageButtonOpts: ImageButton = view.findViewById(R.id.imageButtonOpts)!!
    }
    
}