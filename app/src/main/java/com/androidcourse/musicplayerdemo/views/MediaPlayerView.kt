package com.androidcourse.musicplayerdemo.views

import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.androidcourse.mediaplayer.PlaybackManager
import com.androidcourse.musicplayerdemo.R
import com.androidcourse.musicplayerdemo.ui.MediaPlayerThread
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout


class MediaPlayerView (rootView : View){

    companion object {
        const val STATE_NORMAL: Int = 0

        const val STATE_PARTIAL : Int = 1
    }


    private val m_RootView : View

    private var m_State : Int = 0

    private val m_BottomSheet : FrameLayout

    private val m_ControlsContainer : ConstraintLayout

    private val m_ExtFloatingActBtn_PlayPause_Big : ExtendedFloatingActionButton
    private val m_ExtFloatingActBtn_PlayNext : ExtendedFloatingActionButton
    private val m_ExtFloatingActBtn_PlayPrev : ExtendedFloatingActionButton
    private val m_ExtFloatingActBtn_Repeat : ExtendedFloatingActionButton

    private val m_TextView_SongArtist : TextView
    private val m_TextView_SongTitle : TextView
    private val m_TextView_Song_CurrentDuration : TextView
    private val m_TextView_Song_MaxDuration : TextView

    private val m_ImageView_Art : ImageView

    private val m_SeekBar : SeekBar
    private var m_CanUpdateSeekBar : Boolean = true

    private var m_PrevState : PlaybackState? = null

    @PlaybackManager.Companion.RepeatType
    private var m_RepeatType : Int = PlaybackManager.REPEAT_TYPE_ONE

    init {
        this.m_RootView = rootView

        this.m_BottomSheet = findViewById(R.id.media_player_bottom_sheet_behavior)
        this.m_ControlsContainer = findViewById(R.id.media_player_controls_container)

        this.m_ImageView_Art = findViewById(R.id.media_player_album_art)
        this.m_ExtFloatingActBtn_PlayPause_Big = findViewById(R.id.btn_play_pause_big)
        this.m_ExtFloatingActBtn_PlayNext = findViewById(R.id.btn_skip_next)
        this.m_ExtFloatingActBtn_PlayPrev = findViewById(R.id.btn_skip_previous)
        this.m_ExtFloatingActBtn_Repeat = findViewById(R.id.btn_repeat)

        this.m_TextView_SongArtist = findViewById(R.id.media_player_art_artist)
        this.m_TextView_SongTitle = findViewById(R.id.media_player_art_title)
        this.m_TextView_Song_MaxDuration = findViewById(R.id.media_player_song_max_duration)
        this.m_TextView_Song_CurrentDuration = findViewById(R.id.media_player_song_current_duration)

        this.m_SeekBar = findViewById(R.id.media_player_seekbar)
        this.m_SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var finalProgress : Int = 0
            var isUser : Boolean = false

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                this.finalProgress = progress
                this.isUser = fromUser
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                m_CanUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isUser) {
                    MediaPlayerThread.getInstance()?.getCallback()?.onSetSeekbar(finalProgress)
                }
                m_CanUpdateSeekBar = true
            }

        })

        this.m_RootView.alpha = 0.0F

        this.onInit()
    }

    private fun onInit() {
        this.m_ExtFloatingActBtn_PlayPause_Big.setOnClickListener{
            MediaPlayerThread.getInstance()?.getCallback()?.onClickPlayPause()
        }

        this.m_ExtFloatingActBtn_PlayNext.setOnClickListener{
            MediaPlayerThread.getInstance()?.getCallback()?.onClickPlayNext()
        }

        this.m_ExtFloatingActBtn_PlayPrev.setOnClickListener{
            MediaPlayerThread.getInstance()?.getCallback()?.onClickPlayPrev()
        }

        this.m_ExtFloatingActBtn_Repeat.setOnClickListener{
            if (m_RepeatType < 2)
                m_RepeatType++
            else
                m_RepeatType = PlaybackManager.REPEAT_TYPE_NONE

            when (m_RepeatType)
            {
                PlaybackManager.REPEAT_TYPE_NONE -> {
                    this.m_ExtFloatingActBtn_Repeat.setIconResource(com.androidcourse.icons_pack.R.drawable.repeat_none_24px)
                    this.m_ExtFloatingActBtn_Repeat.alpha = 0.5F
                }
                PlaybackManager.REPEAT_TYPE_ONE -> {
                    this.m_ExtFloatingActBtn_Repeat.setIconResource(com.androidcourse.icons_pack.R.drawable.repeat_one_24px)
                    this.m_ExtFloatingActBtn_Repeat.alpha = 1F
                }
                PlaybackManager.REPEAT_TYPE_ALL -> {
                    this.m_ExtFloatingActBtn_Repeat.setIconResource(com.androidcourse.icons_pack.R.drawable.repeat_all_24px)
                    this.m_ExtFloatingActBtn_Repeat.alpha = 1F
                }
                else -> {
                    m_RepeatType = PlaybackManager.REPEAT_TYPE_NONE
                    this.m_ExtFloatingActBtn_Repeat.setIconResource(com.androidcourse.icons_pack.R.drawable.repeat_none_24px)
                    this.m_ExtFloatingActBtn_Repeat.alpha = 0.5F
                }
            }

            MediaPlayerThread.getInstance()?.getCallback()?.onSetRepeatType(m_RepeatType)
        }
    }

    fun onMetadataChanged(metadata : MediaMetadata?) {
        if(metadata == null)
            return

//        val duration_minutes = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 60000
//        val duration_seconds = (metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) -
//                metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 60000 * 60000) / 1000

        this.m_TextView_SongTitle.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE))
        this.m_TextView_SongArtist.setText(metadata.getText(MediaMetadata.METADATA_KEY_ARTIST))
//        this.mTextView_SongPosition.setText( / 1000);
        //        this.mTextView_SongPosition.setText( / 1000);
        this.m_TextView_Song_MaxDuration.setText(
            GetTimeFormat(metadata.getLong(MediaMetadata.METADATA_KEY_DURATION))
        )

        val album_art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
        if (album_art != null) {
            this.m_ImageView_Art.setImageBitmap(album_art)
        } else {
            this.m_ImageView_Art.setImageDrawable(
                ResourcesCompat.getDrawable(
                    this.m_RootView.getResources(),
                    com.androidcourse.icons_pack.R.drawable.album_24px,
                    this.m_RootView.getContext().getTheme()
                )
            )
        }

        this.m_SeekBar.progress = 0
        this.m_SeekBar.max = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION).toInt()
    }

    // 获取对应时间的字符串
    fun GetTimeFormat(ms : Long) : String
    {
        val duration_minutes = ms / 60000
        val duration_seconds = (ms - ms / 60000 * 60000) / 1000

        return ("$duration_minutes:"
        + (if (duration_seconds >= 10) duration_seconds else ("0"
                + duration_seconds.toString())).toString())
    }

    // slideOffset 滑动偏移量 alpha 是透明度的设置 1F 表示完全不透明 0表示完全透明
    // 随着滑动
    fun onSliding(slideOffset : Float, state : Int) {
        val fadeStart = 0.25F
        val alpha : Float = (slideOffset - fadeStart) * (1F / (1F - fadeStart))

        //正常状态就显示
        if(state == STATE_NORMAL) {
            this.m_RootView.alpha = alpha
            this.m_ControlsContainer.alpha = 1F
        }
        else { //STATE_PARTIAL 部分显示
            this.m_ControlsContainer.alpha = 1F - alpha
        }

        this.m_State = state
    }

    //为何这里要做一个转换呢 大概因为AppCompatActivity本质也是个实现了findViewById的view
    fun <T : View> findViewById(@IdRes id : Int) : T {
        return this.m_RootView.findViewById(id)
    }

    fun onPanelStateChanged(panelState : Int) {
        if(panelState == MultiSlidingUpPanelLayout.COLLAPSED) {
            this.m_RootView.visibility = View.INVISIBLE
        } else {
            this.m_RootView.visibility = View.VISIBLE
        }
    }

    fun onPlaybackStateChanged(state : PlaybackState?) {
        if(state == null)
            return
        if (this.m_PrevState == null || this.m_PrevState!!.getState() != state.state) this.m_ExtFloatingActBtn_PlayPause_Big.setIconResource(
            if (state.state == PlaybackState.STATE_PLAYING) com.androidcourse.icons_pack.R.drawable.pause_24px else com.androidcourse.icons_pack.R.drawable.play_arrow_24px
        )
        if (this.m_CanUpdateSeekBar)
            this.m_SeekBar.progress = state.position.toInt()
        this.m_TextView_Song_CurrentDuration.text = GetTimeFormat(state.position)
    }

}