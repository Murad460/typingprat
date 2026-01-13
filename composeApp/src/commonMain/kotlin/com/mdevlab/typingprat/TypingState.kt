package com.mdevlab.typingprat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.time.Clock

class TypingState(
    initialDifficulty: WordGenerator.Difficulty = WordGenerator.Difficulty.MEDIUM
) {
    var difficulty by mutableStateOf(initialDifficulty)
        private set

    var target by mutableStateOf(WordGenerator.randomContent(difficulty))
        private set

    var input by mutableStateOf("")
        private set

    private var startTimeMillis: Long? = null

    var mistakes by mutableIntStateOf(0)
        private set

    var completionCount by mutableIntStateOf(0)
        private set

    // Flag to track if current session was already counted
    private var sessionCounted = false

    val finished: Boolean
        get() = input == target && input.isNotEmpty()

    val progress: Float
        get() = if (target.isEmpty()) 0f else input.length.toFloat() / target.length

    fun inputChanged(newInput: String) {
        if (startTimeMillis == null && newInput.isNotEmpty()) {
            startTimeMillis = Clock.System.now().toEpochMilliseconds()
        }
        val trimmed = if (newInput.length > target.length) newInput.substring(0, target.length) else newInput
        mistakes = (0 until trimmed.length).count { trimmed[it] != target[it] }
        input = trimmed

        // Only increment once per session
        if (input == target && input.isNotEmpty() && !sessionCounted) {
            sessionCounted = true
            completionCount++
        }
    }

    fun wpm(): Int {
        val st = startTimeMillis ?: return 0
        val elapsedMinutes = (Clock.System.now().toEpochMilliseconds() - st) / 60000.0
        if (elapsedMinutes <= 0.0) return 0
        val words = input.length / 5.0
        return (words / elapsedMinutes).toInt()
    }

    fun accuracyPercent(): Int {
        if (input.isEmpty()) return 100
        val correct = input.length - mistakes
        return ((correct.toDouble() / input.length) * 100).toInt()
    }

    fun setDifficultyLevel(newDifficulty: WordGenerator.Difficulty) {
        difficulty = newDifficulty
        reset()
    }

    fun reset() {
        target = WordGenerator.randomContent(difficulty)
        input = ""
        startTimeMillis = null
        mistakes = 0
        sessionCounted = false  // Reset flag for new session
    }

    fun nextSentence() {
        reset()
    }
}