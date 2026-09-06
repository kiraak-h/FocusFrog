package com.example.focusfrog.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusfrog.ui.components.StatCard

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Stats & Streaks 📊",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.totalSessions == 0) {
            // Empty State
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Complete your first session to start your journey! 🌱",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            title = "Current Streak",
                            value = formatDays(uiState.currentStreak),
                            icon = "🔥",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Best Streak",
                            value = formatDays(uiState.bestStreak),
                            icon = "⚡",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            title = "Today's Sessions",
                            value = uiState.todaySessions.toString(),
                            icon = "🐸",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Sessions",
                            value = uiState.totalSessions.toString(),
                            icon = "🏆",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    StatCard(
                        title = "Lifetime Focus Minutes",
                        value = "${uiState.totalMinutes} mins",
                        icon = "⏱️",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun formatDays(days: Int): String {
    return if (days == 1) "1 day" else "$days days"
}
