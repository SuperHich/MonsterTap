package com.heroapps.monstertap

import com.heroapps.library.compose.AudioFeedback

/**
 * A utility class for playing different types of audio feedback in Android Compose applications
 */
object AudioFeedbackExtension {

    val SOUND_SUCCESS: Int = R.raw.sound_success
    val SOUND_FAILED: Int = R.raw.sound_failed
    val SOUND_CATCH: Int = R.raw.sound_catch
    val SOUND_CLICK: Int = R.raw.sound_click

    fun AudioFeedback.loadSounds() {
        loadSounds(listOf(
            SOUND_SUCCESS,
            SOUND_FAILED,
            SOUND_CATCH,
            SOUND_CLICK
        ))
    }

    // Common tones for different actions
    fun AudioFeedback.playSuccess() {
        playSound(SOUND_SUCCESS)
    }

    fun AudioFeedback.playFailed() {
        playSound(SOUND_FAILED)
    }

    fun AudioFeedback.playCatch() {
        playSound(SOUND_CATCH)
    }

    fun AudioFeedback.playClick() {
        playSound(SOUND_CLICK)
    }
}