package app.fokkusu.music.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import app.fokkusu.music.R

/**
 * @File    : LyricView
 * @Author  : 1552980358
 * @Date    : 11 Oct 2019
 * @TIME    : 7:58 PM
 **/
class LyricView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {
    
    private val lyricTime = arrayListOf<Int>()
    private val lyricList = arrayListOf<String>()
    
    private val textMargin by lazy { resources.getDimensionPixelSize(R.dimen.lyricView_textMargin) }
    private val constWidth by lazy { resources.displayMetrics.widthPixels.toFloat() }
    
    private var current = -1
    private val rect = Rect()
    private var drawHeight = 0F
    
    private val empty = "···"
    private var emptyList = false
    
    /* Paints */
    private val paintCurrent by lazy {
        Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            textSize = resources.getDimension(R.dimen.lyricView_textSize)
        }
    }
    private val paintOther by lazy {
        Paint().apply {
            color = Color.parseColor("#80FFFFFF")
            isAntiAlias = true
            textSize = resources.getDimension(R.dimen.lyricView_textSize)
        }
    }
    
    init {
        paintCurrent
        paintOther
    }
    
    fun updateMusicLyricAndReturn(rawLyric: ArrayList<String>): ArrayList<String> {
        return arrayListOf<String>().apply {
            lyricList.clear()
            lyricTime.clear()
    
            for (j in rawLyric) {
                /* Prevent empty content */
                if (j.isEmpty() || !j.startsWith('[') || j[1] in 'a'..'z' || j[1] in 'A'..'Z' || !j.contains(
                        ']'
                    ) || j.substring(j.lastIndexOf(']') + 1).isEmpty()
                ) {
                    continue
                }
        
                /* Time */
                // 0123456789
                // [HH:mm:ss]
                lyricTime.add(
                    j.substring(1, 3).toInt() * 60000 +         // Hr
                        j.substring(4, 6).toInt() * 1000 +  // Min
                        j.substring(7, 9).toInt() * 10      // Sec
                )
        
                /* Lyric */
                lyricList.add(j.substring(j.lastIndexOf(']') + 1))
                
                add(j)
            }
            
            postInvalidate()
        }
    }
    
    @Synchronized
    fun updateMusicLyric(rawLyric: ArrayList<String>) {
        /* Remove all content */
        lyricList.clear()
        lyricTime.clear()
        
        if (rawLyric.isEmpty()) {
            emptyList = true
            postInvalidate()
        }
        
        emptyList = false
        for (j in rawLyric) {
            /* Time */
            // 0123456789
            // [HH:mm:ss]
            lyricTime.add(
                j.substring(1, 3).toInt() * 60000 +         // Hr
                        j.substring(4, 6).toInt() * 1000 +  // Min
                        j.substring(7, 9).toInt() * 10      // Sec
            )
            
            /* Lyric */
            lyricList.add(j.substring(j.lastIndexOf(']') + 1))
        }
        postInvalidate()
    }
    
    @Synchronized
    fun updateLyricLine(time: Int) {
        if (lyricTime.isEmpty() || lyricList.isEmpty()) {
            current = -1
            
            postInvalidate()
            return
        }
        
        for ((i, j) in lyricTime.withIndex()) {
            if (j > time) {
                current = (i - 1)
                break
            }
        }
        postInvalidate()
    }
    
    @Suppress("DuplicatedCode")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        canvas ?: return
        
        /* Draw background */
        canvas.drawColor(Color.TRANSPARENT)
        
        drawHeight = 0F
        
        if (emptyList) {
            paintOther.getTextBounds(empty, 0, empty.length, rect)
            canvas.drawText(
                empty,
                (constWidth - rect.width()) / 2,
                rect.height().toFloat(),
                paintCurrent
            )
            return
        }
        
        /* Pointer indicates empty list */
        if (current == -1) {
            // 1st line
            paintOther.getTextBounds(empty, 0, empty.length, rect)
            canvas.drawText(
                empty,
                (constWidth - rect.width()) / 2,
                rect.height().toFloat(),
                paintOther
            )
            
            drawHeight += rect.height().plus(textMargin)
            
            // 2nd line
            paintOther.getTextBounds(empty, 0, empty.length, rect)
            canvas.drawText(
                empty,
                (constWidth - rect.width()) / 2,
                rect.height().toFloat(),
                paintCurrent
            )
            
            drawHeight += rect.height().plus(textMargin)
            
            // 3rd line
            paintOther.getTextBounds(empty, 0, empty.length, rect)
            canvas.drawText(
                empty,
                (constWidth - rect.width()) / 2,
                rect.height().toFloat(),
                paintOther
            )
            
            return
        }
        
        /* First lyric line */
        if (current == 0) {
            // 1st line
            paintOther.getTextBounds(empty, 0, empty.length, rect)
            canvas.drawText(
                empty,
                (constWidth - rect.width()) / 2,
                rect.height().toFloat(),
                paintOther
            )
            
            drawHeight += rect.height().plus(textMargin)
            
            // 2nd line
            lyricList[current].apply {
                paintOther.getTextBounds(this, 0, length, rect)
                canvas.drawText(
                    this,
                    (constWidth - rect.width()) / 2,
                    rect.height() + drawHeight,
                    paintCurrent
                )
            }
            
            drawHeight += rect.height().plus(textMargin)
            
            // 3rd line
            lyricList[current + 1].apply {
                paintOther.getTextBounds(this, 0, length, rect)
                canvas.drawText(
                    this,
                    (constWidth - rect.width()) / 2,
                    rect.height() + drawHeight,
                    paintOther
                )
            }
            
            return
        }
        
        if (current == lyricList.lastIndex) {
            lyricList[current - 1].apply {
                paintOther.getTextBounds(this, 0, length, rect)
                canvas.drawText(
                    this,
                    (constWidth - rect.width()) / 2,
                    rect.height() + drawHeight,
                    paintOther
                )
            }
            
            drawHeight += rect.height().plus(textMargin)
            
            // 2nd line
            lyricList[current].apply {
                paintOther.getTextBounds(this, 0, length, rect)
                canvas.drawText(
                    this,
                    (constWidth - rect.width()) / 2,
                    rect.height() + drawHeight,
                    paintCurrent
                )
            }
            
            drawHeight += rect.height().plus(textMargin)
            
            // 3rd line
            paintOther.getTextBounds(empty, 0, empty.length, rect)
            canvas.drawText(
                empty,
                (constWidth - rect.width()) / 2,
                rect.height().toFloat(),
                paintOther
            )
            
            return
        }
        
        /* Draw other */
        // 1st line
        lyricList[current - 1].apply {
            paintOther.getTextBounds(this, 0, length, rect)
            canvas.drawText(
                this,
                (constWidth - rect.width()) / 2,
                rect.height() + drawHeight,
                paintOther
            )
        }
        
        drawHeight += rect.height().plus(textMargin)
        
        // 2nd line
        lyricList[current].apply {
            paintOther.getTextBounds(this, 0, length, rect)
            canvas.drawText(
                this,
                (constWidth - rect.width()) / 2,
                rect.height() + drawHeight,
                paintCurrent
            )
        }
        
        drawHeight += rect.height().plus(textMargin)
        
        // 3rd line
        lyricList[current + 1].apply {
            paintOther.getTextBounds(this, 0, length, rect)
            canvas.drawText(
                this,
                (constWidth - rect.width()) / 2,
                rect.height() + drawHeight,
                paintOther
            )
        }
        
    }
}