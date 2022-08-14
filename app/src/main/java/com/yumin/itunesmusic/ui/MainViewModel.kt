package com.yumin.itunesmusic.ui

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumin.itunesmusic.data.Result
import com.yumin.itunesmusic.data.SearchResult
import com.yumin.itunesmusic.repository.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: RemoteRepository
) : ViewModel() {

    var isLoading = MutableLiveData<Boolean>()
    var searchResult = MutableLiveData<SearchResult>()
    var previewResult = MutableLiveData<Result>()
    private var mediaPlayer: MediaPlayer? = null
    var canPreview = MutableLiveData<Boolean>()
    var previewDuration = MutableLiveData<Int>()
    var currentPosition = MutableLiveData<Int>()
    var startPreview = false
    var previewComplete = MutableLiveData<Boolean>()
    var previewIndex: Int = 0

    init {
        isLoading.value = false
        canPreview.value = false
    }

    fun searchResult(keyword: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.postValue(false)
                searchResult.postValue(repository.getSearchResult(keyword, "music", "tw"))
            } catch (exception: Exception) {
                isLoading.postValue(false)
            }
        }
    }

    fun setPreviewResult(position: Int) {
        previewResult.value = searchResult.value?.results?.get(position)
        previewIndex = position
    }

    fun prepareMediaPlayer(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(url)
                prepare()
            }

            mediaPlayer!!.setOnPreparedListener {
                canPreview.value = true
                previewDuration.value = mediaPlayer!!.duration
                currentPosition.value =
                    (mediaPlayer!!.currentPosition * mediaPlayer!!.duration) / 1000
            }

            mediaPlayer!!.setOnCompletionListener {
                it.seekTo(0)
                previewComplete.value = true
                startPreview = false
            }
        }
    }

    fun startPreview() {
        Log.d("[ViewModel]", "[startPreview] ")
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            startPreview = true
        }
        viewModelScope.launch(Dispatchers.IO) {
            while (startPreview && mediaPlayer?.isPlaying == true) {
                currentPosition.postValue(mediaPlayer!!.currentPosition / 1000)
                delay(1000)
            }
        }
    }

    fun pausePreview() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer!!.pause()
            startPreview = false
        }
    }

    fun previewSeekTo(position: Int) {
        mediaPlayer!!.seekTo(position * 1000)
    }

    fun releaseMediaPlayer() {
        startPreview = false
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer()
    }

    fun skipNext() {
        if (mediaPlayer != null)
            releaseMediaPlayer()

        if (previewIndex == searchResult.value?.results?.size!! - 1)
            previewIndex = 0
        else
            previewIndex += 1

        setPreviewResult(previewIndex)
    }

    fun skipPrevious() {
        if (mediaPlayer != null)
            releaseMediaPlayer()

        if (previewIndex == 0)
            previewIndex = searchResult.value?.results?.size!! - 1
        else
            previewIndex -= 1

        setPreviewResult(previewIndex)
    }
}