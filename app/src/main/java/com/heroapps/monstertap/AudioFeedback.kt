package com.heroapps.monstertap

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator

/**
 * A utility class for playing different types of audio feedback in Android Compose applications
 */
class AudioFeedback(private val context: Context) {

    companion object {
        val SOUND_SUCCESS: Int = R.raw.sound_success
        val SOUND_FAILED: Int = R.raw.sound_failed
        val SOUND_CATCH: Int = R.raw.sound_catch
        val SOUND_CLICK: Int = R.raw.sound_click
    }

    // ToneGenerator for simple system tones
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 100)

    // SoundPool for custom sound effects
    private val soundPool: SoundPool by lazy {
        SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    fun loadSounds() {
        loadSound(SOUND_SUCCESS)
        loadSound(SOUND_FAILED)
        loadSound(SOUND_CATCH)
        loadSound(SOUND_CLICK)
    }

    // Map to store loaded sound IDs
    private val soundMap = mutableMapOf<Int, Int>()

    // Load a custom sound from a raw resource
    fun loadSound(soundResId: Int): Int {
        val soundId = soundPool.load(context, soundResId, 1)
        soundMap[soundResId] = soundId
        return soundId
    }

    // Play a predefined system tone
    fun playTone(toneType: Int, durationMs: Int = 200) {
        toneGenerator.startTone(toneType, durationMs)
    }

    // Play a custom sound loaded with loadSound()
    fun playSound(soundResId: Int, leftVolume: Float = 1f, rightVolume: Float = 1f) {
        val soundId = soundMap[soundResId] ?: return
        soundPool.play(soundId, leftVolume, rightVolume, 1, 0, 1f)
    }

    // Common tones for different actions
    fun playSuccess() {
        playSound(SOUND_SUCCESS)
    }

    fun playFailed() {
        playSound(SOUND_FAILED)
    }

    fun playCatch() {
        playSound(SOUND_CATCH)
    }

    fun playClick() {
        playSound(SOUND_CLICK)
    }

    // Release resources when done
    fun release() {
        toneGenerator.release()
        soundPool.release()
    }
}