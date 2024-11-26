package dev.jyotiraditya.echoir.data.media

import com.arthenica.ffmpegkit.FFmpegKit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class FFmpegProcessor @Inject constructor() {
    suspend fun processMergedFile(inputPath: String, outputPath: String): String {
        return suspendCoroutine { continuation ->
            val ffmpegCommand = "-i \"$inputPath\" -c copy \"$outputPath\""

            FFmpegKit.executeAsync(ffmpegCommand) { session ->
                if (session.returnCode.isValueSuccess) {
                    continuation.resume(outputPath)
                } else {
                    continuation.resumeWithException(
                        RuntimeException("FFmpeg process failed: ${session.failStackTrace}")
                    )
                }
            }
        }
    }
}