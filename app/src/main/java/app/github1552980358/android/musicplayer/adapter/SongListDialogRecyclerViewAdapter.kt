package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.AudioActivity

/**
 * [SongListDialogRecyclerViewAdapter]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/18
 * @time    : 10:32
 **/

class SongListDialogRecyclerViewAdapter(
    private val parentActivity: AudioActivity
): Adapter<SongListDialogRecyclerViewAdapter.ViewHolder>() {
    
    /**
     * [onCreateViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (parent.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_song_list_dialog_content, parent, false)
        )
    }
    
    /**
     * [getItemCount]
     * @author 1552980358
     * @since 0.1
     **/
    override fun getItemCount(): Int {
        return 0
    }
    
    /**
     * [onBindViewHolder]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // No
        holder.textViewNo.text = position.plus(1).toString()
        
        // title
        //holder.textViewTitle.text = AudioData.audioDataMap[PlayService.playHistory[PlayService.playHistory.lastIndex - position]]?.run {
        //    "$title - $artist"
        //}
        
        holder.textViewTitle.text = parentActivity.songList[parentActivity.songList.lastIndex - position].run {
            description.title.toString() + " - " + description.subtitle.toString()
        }
        holder.textViewTitle.isSingleLine = true
        holder.textViewTitle.ellipsize = TextUtils.TruncateAt.END
        holder.relativeLayoutRoot.setOnClickListener {
        
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
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        /**
         * [textViewNo]
         * @author 1552980358
         * @since 0.1
         **/
        val textViewNo: TextView = view.findViewById(R.id.textViewNo)
        /**
         * [relativeLayoutRoot]
         * @author 1552980358
         * @since 0.1
         **/
        val relativeLayoutRoot: RelativeLayout = view.findViewById(R.id.relativeLayoutRoot)
    }
    
}