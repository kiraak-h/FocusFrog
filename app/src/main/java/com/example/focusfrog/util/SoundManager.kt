package com.example.focusfrog.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

enum class SoundEffect {
    SESSION_COMPLETE,
    HAPPY_JUMP,
    COIN_PURCHASE,
    THUD_FAILED,
    SUBTLE_POP
}

object SoundManager {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundEffect, Int>()
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val pool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool = pool

        val cacheDir = context.cacheDir
        val sampleRate = 22050

        // 1. Session Complete Chime
        val sessionWav = generateWav(sampleRate, generateSessionCompletePcm(sampleRate))
        val sessionFile = saveWavFile(cacheDir, "session_complete.wav", sessionWav)
        soundMap[SoundEffect.SESSION_COMPLETE] = pool.load(sessionFile.absolutePath, 1)

        // 2. Happy Jump Boing/Pop
        val jumpWav = generateWav(sampleRate, generateHappyJumpPcm(sampleRate))
        val jumpFile = saveWavFile(cacheDir, "happy_jump.wav", jumpWav)
        soundMap[SoundEffect.HAPPY_JUMP] = pool.load(jumpFile.absolutePath, 1)

        // 3. Coin Purchase
        val coinWav = generateWav(sampleRate, generateCoinPcm(sampleRate))
        val coinFile = saveWavFile(cacheDir, "coin_purchase.wav", coinWav)
        soundMap[SoundEffect.COIN_PURCHASE] = pool.load(coinFile.absolutePath, 1)

        // 4. Thud Failed
        val thudWav = generateWav(sampleRate, generateThudPcm(sampleRate))
        val thudFile = saveWavFile(cacheDir, "thud_failed.wav", thudWav)
        soundMap[SoundEffect.THUD_FAILED] = pool.load(thudFile.absolutePath, 1)

        // 5. Subtle Pop
        val popWav = generateWav(sampleRate, generateSubtlePopPcm(sampleRate))
        val popFile = saveWavFile(cacheDir, "subtle_pop.wav", popWav)
        soundMap[SoundEffect.SUBTLE_POP] = pool.load(popFile.absolutePath, 1)

        isInitialized = true
    }

    fun playSound(effect: SoundEffect, isSoundEnabled: Boolean) {
        if (!isSoundEnabled || !isInitialized) return
        val soundId = soundMap[effect] ?: return
        soundPool?.play(soundId, 0.8f, 0.8f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
        isInitialized = false
    }

    private fun saveWavFile(dir: File, fileName: String, bytes: ByteArray): File {
        val file = File(dir, fileName)
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    private fun generateSessionCompletePcm(sampleRate: Int): ShortArray {
        val durationMs = 300
        val numSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(numSamples)
        val half = numSamples / 2

        for (i in 0 until numSamples) {
            val freq = if (i < half) 523.25 else 783.99 // C5 -> G5
            val t = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / numSamples)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope * 0.5
            pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }

    private fun generateHappyJumpPcm(sampleRate: Int): ShortArray {
        val durationMs = 120
        val numSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 400.0 + (progress * 400.0) // 400Hz -> 800Hz sweep
            val t = i.toDouble() / sampleRate
            val envelope = 1.0 - progress
            val sample = sin(2.0 * Math.PI * freq * t) * envelope * 0.5
            pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }

    private fun generateCoinPcm(sampleRate: Int): ShortArray {
        val durationMs = 150
        val numSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(numSamples)
        val half = numSamples / 2

        for (i in 0 until numSamples) {
            val freq = if (i < half) 987.77 else 1318.51 // B5 -> E6
            val t = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / numSamples)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope * 0.5
            pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }

    private fun generateThudPcm(sampleRate: Int): ShortArray {
        val durationMs = 100
        val numSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 120.0
            val t = i.toDouble() / sampleRate
            val envelope = Math.exp(-progress * 5.0)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope * 0.4
            pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }

    private fun generateSubtlePopPcm(sampleRate: Int): ShortArray {
        val durationMs = 60
        val numSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 600.0
            val t = i.toDouble() / sampleRate
            val envelope = Math.exp(-progress * 8.0)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope * 0.4
            pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }

    private fun generateWav(sampleRate: Int, pcm: ShortArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(44 + pcm.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        val totalDataLen = 36 + pcm.size * 2
        val byteRate = sampleRate * 2

        // RIFF Header
        byteBuffer.put('R'.code.toByte())
        byteBuffer.put('I'.code.toByte())
        byteBuffer.put('F'.code.toByte())
        byteBuffer.put('F'.code.toByte())
        byteBuffer.putInt(totalDataLen)
        byteBuffer.put('W'.code.toByte())
        byteBuffer.put('A'.code.toByte())
        byteBuffer.put('V'.code.toByte())
        byteBuffer.put('E'.code.toByte())

        // fmt chunk
        byteBuffer.put('f'.code.toByte())
        byteBuffer.put('m'.code.toByte())
        byteBuffer.put('t'.code.toByte())
        byteBuffer.put(' '.code.toByte())
        byteBuffer.putInt(16) // Subchunk1Size (16 for PCM)
        byteBuffer.putShort(1.toShort()) // AudioFormat (1 for PCM)
        byteBuffer.putShort(1.toShort()) // NumChannels (1 mono)
        byteBuffer.putInt(sampleRate)
        byteBuffer.putInt(byteRate)
        byteBuffer.putShort(2.toShort()) // BlockAlign
        byteBuffer.putShort(16.toShort()) // BitsPerSample

        // data chunk
        byteBuffer.put('d'.code.toByte())
        byteBuffer.put('a'.code.toByte())
        byteBuffer.put('t'.code.toByte())
        byteBuffer.put('a'.code.toByte())
        byteBuffer.putInt(pcm.size * 2)

        for (sample in pcm) {
            byteBuffer.putShort(sample)
        }

        return byteBuffer.array()
    }
}
