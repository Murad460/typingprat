package com.mdevlab.typingprat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

data class SessionResult(
    val wpm: Int,
    val accuracy: Int,
    val mistakes: Int,
    val difficulty: WordGenerator.Difficulty,
    val charactersTyped: Int,
    val timestamp: Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
)

class AnalyticsState {
    var sessions by mutableStateOf(listOf<SessionResult>())
        private set

    val totalSessions: Int get() = sessions.size
    val averageWpm: Int get() = if (sessions.isEmpty()) 0 else sessions.map { it.wpm }.average().toInt()
    val averageAccuracy: Int get() = if (sessions.isEmpty()) 100 else sessions.map { it.accuracy }.average().toInt()
    val totalCharactersTyped: Int get() = sessions.sumOf { it.charactersTyped }
    val totalMistakes: Int get() = sessions.sumOf { it.mistakes }
    val bestWpm: Int get() = sessions.maxOfOrNull { it.wpm } ?: 0
    val bestAccuracy: Int get() = sessions.maxOfOrNull { it.accuracy } ?: 100

    fun addSession(result: SessionResult) {
        sessions = sessions + result
    }

    fun clearHistory() {
        sessions = emptyList()
    }
}


@Composable
@Preview

fun App() {
    MaterialTheme {
        var currentTab by remember { mutableStateOf(0) }
        val model = remember { TypingState() }
        val analytics = remember { AnalyticsState() }
        var lastCompletionCount by remember { mutableIntStateOf(0) }

        // Track completed sessions using completion counter
        LaunchedEffect(model.completionCount) {
            if (model.completionCount > lastCompletionCount && model.finished) {
                analytics.addSession(
                    SessionResult(
                        wpm = model.wpm(),
                        accuracy = model.accuracyPercent(),
                        mistakes = model.mistakes,
                        difficulty = model.difficulty,
                        charactersTyped = model.input.length
                    )
                )
                lastCompletionCount = model.completionCount
            }
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize()
        ) {
            GradientHeader(analytics)

            TabRow(
                selectedTabIndex = currentTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = { Text("⌨️ Practice") }
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = { Text("📊 Analytics") }
                )
                Tab(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    text = { Text("📜 History") }
                )
            }

            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                }
            ) { tab ->
                when (tab) {
                    0 -> TypingScreen(model = model)
                    1 -> AnalyticsScreen(analytics = analytics)
                    2 -> HistoryScreen(analytics = analytics)
                }
            }
        }
    }
}@Composable
private fun GradientHeader(analytics: AnalyticsState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "⌨️ Typing Master",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Improve your speed and accuracy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStat("Best WPM", "${analytics.bestWpm}")
                QuickStat("Sessions", "${analytics.totalSessions}")
                QuickStat("Avg Accuracy", "${analytics.averageAccuracy}%")
            }
        }
    }
}

@Composable
private fun QuickStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun AnalyticsScreen(analytics: AnalyticsState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Performance Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Main Stats Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnalyticsCard(
                    title = "Average WPM",
                    value = "${analytics.averageWpm}",
                    subtitle = "words per minute",
                    icon = "🚀",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsCard(
                    title = "Best WPM",
                    value = "${analytics.bestWpm}",
                    subtitle = "personal record",
                    icon = "🏆",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnalyticsCard(
                    title = "Accuracy",
                    value = "${analytics.averageAccuracy}%",
                    subtitle = "average accuracy",
                    icon = "🎯",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsCard(
                    title = "Best Accuracy",
                    value = "${analytics.bestAccuracy}%",
                    subtitle = "highest score",
                    icon = "⭐",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Detailed Stats
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📈 Detailed Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow("Total Sessions", "${analytics.totalSessions}")
                    DetailRow("Characters Typed", "${analytics.totalCharactersTyped}")
                    DetailRow("Total Mistakes", "${analytics.totalMistakes}")
                    DetailRow("Words Typed (est.)", "${analytics.totalCharactersTyped / 5}")

                   if (analytics.totalCharactersTyped > 0) {
                       val errorRate = (analytics.totalMistakes.toFloat() / analytics.totalCharactersTyped * 100)
                       val formattedRate = ((errorRate * 100).toInt() / 100.0)
                       DetailRow("Error Rate", "$formattedRate%")
                   }
                }
            }
        }

        // Difficulty Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎮 Difficulty Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WordGenerator.Difficulty.entries.forEach { difficulty ->
                        val count = analytics.sessions.count { it.difficulty == difficulty }
                        val avgWpm = analytics.sessions
                            .filter { it.difficulty == difficulty }
                            .takeIf { it.isNotEmpty() }
                            ?.map { it.wpm }
                            ?.average()
                            ?.toInt() ?: 0

                        DifficultyRow(difficulty, count, avgWpm)
                    }
                }
            }
        }

        // Progress Indicator
// Progress Indicator section
        if (analytics.sessions.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎯 Skill Level",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val skillLevel = when {
                            analytics.averageWpm >= 80 -> "Expert" to 1f
                            analytics.averageWpm >= 60 -> "Advanced" to 0.75f
                            analytics.averageWpm >= 40 -> "Intermediate" to 0.5f
                            analytics.averageWpm >= 20 -> "Beginner" to 0.25f
                            else -> "Novice" to 0.1f
                        }

                        Text(text = skillLevel.first, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { skillLevel.second },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))
                        )
                    }
                }
            }
        }    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    value: String,
    subtitle: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DifficultyRow(difficulty: WordGenerator.Difficulty, count: Int, avgWpm: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when (difficulty) {
                            WordGenerator.Difficulty.EASY -> MaterialTheme.colorScheme.tertiary
                            WordGenerator.Difficulty.MEDIUM -> MaterialTheme.colorScheme.secondary
                            WordGenerator.Difficulty.HARD -> MaterialTheme.colorScheme.error
                        }
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = difficulty.name, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = "$count sessions • $avgWpm WPM", style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun HistoryScreen(analytics: AnalyticsState) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Session History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (analytics.sessions.isNotEmpty()) {
                TextButton(onClick = { analytics.clearHistory() }) {
                    Text("Clear All")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (analytics.sessions.isEmpty()) {
            EmptyHistoryState()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(analytics.sessions.reversed()) { session ->
                    SessionCard(session)
                }
            }
        }
    }
}
@Composable
private fun SessionCard(session: SessionResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (session.difficulty) {
                            WordGenerator.Difficulty.EASY -> "🟢"
                            WordGenerator.Difficulty.MEDIUM -> "🟡"
                            WordGenerator.Difficulty.HARD -> "🔴"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${session.wpm} WPM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${session.accuracy}% accuracy • ${session.mistakes} mistakes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.charactersTyped} chars",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = session.difficulty.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
private fun EmptyHistoryState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "📝", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "No sessions yet", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Complete a typing session to see your history",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}