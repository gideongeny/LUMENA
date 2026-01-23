package com.dn0ne.player.core.util

import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Global crash handler that logs uncaught exceptions to logcat and saves them to a file.
 * This helps track crashes in production builds without requiring Firebase Crashlytics.
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    
    private val originalHandler: Thread.UncaughtExceptionHandler? = 
        Thread.getDefaultUncaughtExceptionHandler()
    
    companion object {
        private const val TAG = "CrashHandler"
        private const val CRASH_LOG_DIR = "crash_logs"
        
        fun install(applicationFilesDir: File) {
            val handler = CrashHandler()
            handler.applicationFilesDir = applicationFilesDir
            Thread.setDefaultUncaughtExceptionHandler(handler)
        }
    }
    
    private var applicationFilesDir: File? = null
    
    override fun uncaughtException(thread: Thread, exception: Throwable) {
        try {
            // Log to logcat
            Log.e(TAG, "Uncaught exception in thread: ${thread.name}", exception)
            
            // Save to file
            applicationFilesDir?.let { filesDir ->
                saveCrashLog(filesDir, thread, exception)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in crash handler", e)
        } finally {
            // Call original handler (which will terminate the app)
            originalHandler?.uncaughtException(thread, exception)
        }
    }
    
    private fun saveCrashLog(filesDir: File, thread: Thread, exception: Throwable) {
        try {
            val crashLogDir = File(filesDir, CRASH_LOG_DIR)
            if (!crashLogDir.exists()) {
                crashLogDir.mkdirs()
            }
            
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
            val crashFile = File(crashLogDir, "crash_$timestamp.txt")
            
            FileWriter(crashFile).use { writer ->
                writer.appendLine("Crash Report")
                writer.appendLine("============")
                writer.appendLine("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
                writer.appendLine("Thread: ${thread.name}")
                writer.appendLine("Thread ID: ${thread.id}")
                writer.appendLine()
                writer.appendLine("Exception:")
                writer.appendLine(exception.javaClass.name)
                writer.appendLine("Message: ${exception.message}")
                writer.appendLine()
                writer.appendLine("Stack Trace:")
                
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                exception.printStackTrace(pw)
                writer.appendLine(sw.toString())
                
                // Include cause if present
                exception.cause?.let { cause ->
                    writer.appendLine()
                    writer.appendLine("Caused by:")
                    writer.appendLine(cause.javaClass.name)
                    writer.appendLine("Message: ${cause.message}")
                    writer.appendLine()
                    val causeSw = StringWriter()
                    val causePw = PrintWriter(causeSw)
                    cause.printStackTrace(causePw)
                    writer.appendLine(causeSw.toString())
                }
            }
            
            Log.d(TAG, "Crash log saved to: ${crashFile.absolutePath}")
            
            // Keep only last 10 crash logs
            crashLogDir.listFiles()?.let { files ->
                if (files.size > 10) {
                    files.sortedBy { it.lastModified() }
                        .take(files.size - 10)
                        .forEach { it.delete() }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash log", e)
        }
    }
}
