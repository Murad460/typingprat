package com.mdevlab.typingprat

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TypingScreen(model: TypingState = remember { TypingState() }) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Difficulty Selector
        DifficultySelector(model)

        // Stats Cards Row
        StatsRow(model)

        // Progress Bar
        ProgressIndicator(model.progress)

        // Target Text Card
        TargetTextCard(model)

        // Input Field
        InputField(model)

        // Action Buttons
        ActionButtons(model)

        // Completion Message
        if (model.finished) {
            CompletionCard(model)
        }
    }
}

@Composable
private fun DifficultySelector(model: TypingState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Difficulty Level",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WordGenerator.Difficulty.entries.forEach { difficulty ->
                    val isSelected = model.difficulty == difficulty
                    FilterChip(
                        selected = isSelected,
                        onClick = { model.setDifficultyLevel(difficulty) },
                        label = {
                            Text(
                                text = when (difficulty) {
                                    WordGenerator.Difficulty.EASY -> "🟢 Easy"
                                    WordGenerator.Difficulty.MEDIUM -> "🟡 Medium"
                                    WordGenerator.Difficulty.HARD -> "🔴 Hard"
                                }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Text(
                text = when (model.difficulty) {
                    WordGenerator.Difficulty.EASY -> "Short sentences for beginners"
                    WordGenerator.Difficulty.MEDIUM -> "Full paragraphs for practice"
                    WordGenerator.Difficulty.HARD -> "Long essays for experts"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun StatsRow(model: TypingState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "WPM",
            value = "${model.wpm()}",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary
        )
        StatCard(
            title = "Accuracy",
            value = "${model.accuracyPercent()}%",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondary
        )
        StatCard(
            title = "Mistakes",
            value = "${model.mistakes}",
            modifier = Modifier.weight(1f),
            color = if (model.mistakes > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    val animatedColor by animateColorAsState(targetValue = color)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = animatedColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = animatedColor
            )
        }
    }
}

@Composable
private fun ProgressIndicator(progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "${(progress * 100).toInt()}% complete",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TargetTextCard(model: TypingState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Type this:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${model.target.length} characters",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val annotated = buildAnnotatedString {
                for (i in model.target.indices) {
                    val ch = model.target[i]
                    val style = when {
                        i < model.input.length && model.input[i] == ch ->
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        i < model.input.length && model.input[i] != ch ->
                            SpanStyle(
                                color = MaterialTheme.colorScheme.error,
                                background = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            )
                        i == model.input.length ->
                            SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        else ->
                            SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    withStyle(style) { append(ch) }
                }
            }

            Text(
                text = annotated,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 26.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InputField(model: TypingState) {
    OutlinedTextField(
        value = model.input,
        onValueChange = { model.inputChanged(it) },
        label = { Text("Start typing...") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        enabled = !model.finished,
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
        shape = RoundedCornerShape(12.dp),
        maxLines = 10
    )
}

@Composable
private fun ActionButtons(model: TypingState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = { model.reset() },
            modifier = Modifier.weight(1f)
        ) {
            Text("🔄 Reset")
        }
        Button(
            onClick = { model.nextSentence() },
            modifier = Modifier.weight(1f)
        ) {
            Text("➡️ Next")
        }
    }
}

@Composable
private fun CompletionCard(model: TypingState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🎉 Great job!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Final WPM: ${model.wpm()} | Accuracy: ${model.accuracyPercent()}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Difficulty: ${model.difficulty.name}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}