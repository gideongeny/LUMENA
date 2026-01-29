package com.dn0ne.player.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Choreographer

class FrameRateMonitor(private val tag: String = "FrameRateMonitor", private val intervalMs: Long = 5000L) : Choreographer.FrameCallback {
    private val choreographer: Choreographer = Choreographer.getInstance()
    private var frameCount: Int = 0
    private var lastTimeNanos: Long = 0L
    private var running: Boolean = false

    fun start() {
        if (running) return
        running = true
        frameCount = 0
        lastTimeNanos = System.nanoTime()
        choreographer.postFrameCallback(this)
        scheduleReport()
    }

    fun stop() {
        running = false
        try {
            choreographer.removeFrameCallback(this)
        } catch (_: Exception) {}
    }

    private fun scheduleReport() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!running) return@postDelayed
            val now = System.nanoTime()
            val elapsedSec = (now - lastTimeNanos) / 1_000_000_000.0
            val fps = if (elapsedSec > 0) frameCount / elapsedSec else 0.0
            Log.d(tag, "Avg FPS: %.2f (frames=%d, elapsed=%.2fs)".format(fps, frameCount, elapsedSec))
            frameCount = 0
            lastTimeNanos = now
            scheduleReport()
        }, intervalMs)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!running) return
        frameCount++
        choreographer.postFrameCallback(this)
    }
}
