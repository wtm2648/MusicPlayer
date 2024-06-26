package com.androidcourse.mediaplayer

import android.app.Activity
import android.content.Intent
import android.media.session.MediaController
import android.util.Log
import com.androidcourse.mediaplayer.interfaces.IPlayerCallback
import com.androidcourse.mediaplayer.statics.IntentFields


class CorePlayer(private val m_vActivity: Activity, callback: MediaController.Callback?) {
    private var m_vCallback: IPlayerCallback?
    private val m_vMediaPlayerController: MediaPlayerController

    init {
        m_vCallback = this.callback
        m_vMediaPlayerController = MediaPlayerController(m_vActivity, callback!!)
    }

    val callback: IPlayerCallback?
        get() {
            m_vCallback = if (m_vCallback == null) object : IPlayerCallback {
                override fun onClickPlay(queueIndex: Int, queue: List<Int>) {
                    val new_queue = ArrayList<Int>()
                    for (i in queue) {
                        new_queue.add(i)
                    }
                    val intent = Intent(IntentFields.INTENT_PLAY)
                    intent.putIntegerArrayListExtra(IntentFields.EXTRA_TRACKS_QUEUE, new_queue)
                    intent.putExtra(IntentFields.EXTRA_TRACK_INDEX, queueIndex)
                    m_vActivity.sendBroadcast(intent)
                }

                override fun onClickPlayIndex(index: Int) {

                }

                override fun onClickPlayPause() {
                    Log.i("CorePlayer", "onClickPlayPause")
                    val intent : Intent = Intent(IntentFields.INTENT_PLAY_PAUSE)
                    m_vActivity.sendBroadcast(intent)
                }

                override fun onClickPlayNext() {
                    val intent = Intent(IntentFields.INTENT_PLAY_NEXT)
                    m_vActivity.sendBroadcast(intent)
                }
                override fun onClickPlayPrev() {
                    val intent = Intent(IntentFields.INTENT_PLAY_PREV)
                    m_vActivity.sendBroadcast(intent)
                }
                override fun onClickPause() {
                    val intent = Intent(IntentFields.INTENT_PLAY_PAUSE)
                    m_vActivity.sendBroadcast(intent)
                }

                override fun onSetRepeatType(@PlaybackManager.Companion.RepeatType repeatType: Int) {
                    val intent = Intent(IntentFields.INTENT_CHANGE_REPEAT)
                    intent.putExtra(IntentFields.EXTRA_REPEAT_STATE, repeatType)
                    m_vActivity.sendBroadcast(intent)
                }

                override fun onSetSeekbar(position: Int) {
                    val intent = Intent(IntentFields.INTENT_SET_SEEKBAR)
                    intent.putExtra(IntentFields.EXTRA_SEEK_BAR_POSITION, position)
                    m_vActivity.sendBroadcast(intent)
                }
                override fun onUpdateQueue(queue : List<Int>, queueIndex : Int) {}
                override fun onDestroy() {}


            } else m_vCallback
            return m_vCallback
        }

    fun onDestroy() {
        m_vMediaPlayerController.onDestroy()
    }

    fun onStart() {
        m_vMediaPlayerController.onStart()
    }
}