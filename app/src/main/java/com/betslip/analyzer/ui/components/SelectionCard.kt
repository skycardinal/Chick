package com.betslip.analyzer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.betslip.analyzer.data.model.SelectionAnalysis

@Composable
fun SelectionCard(
    analysis: SelectionAnalysis,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    analysis.selection.team,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "@${String.format("%.2f", analysis.selection.odds)}",
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    analysis.selection.matchType,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (analysis.currentlyLive) {
                    Text(
                        "🔴 LIVE",
                        fontSize = 11.sp,
                        color = Color(0xFFE53935)
                    )
                }
            }
            
            Surface(
                color = when {
                    analysis.recommendation.contains("STRONG") -> Color(0xFF4CAF50)
                    analysis.recommendation.contains("GOOD") -> Color(0xFF8BC34A)
                    analysis.recommendation.contains("RISKY") -> Color(0xFFFF9800)
                    analysis.recommendation.contains("POOR") -> Color(0xFFE53935)
                    else -> Color(0xFF9E9E9E)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    analysis.recommendation.take(50),
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 12.sp
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Win Prob.", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        "${String.format("%.0f", analysis.winProbability)}%",
                        fontSize = 13.sp,
                        color = Color(0xFF2196F3)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Confidence", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        "${String.format("%.0f", analysis.confidence)}%",
                        fontSize = 13.sp,
                        color = Color(0xFF2196F3)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Form", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        analysis.recentForm.take(3),
                        fontSize = 12.sp
                    )
                }
            }
            
            Text(
                analysis.recentForm,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                "Tap for details →",
                fontSize = 11.sp,
                color = Color(0xFF2196F3),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
