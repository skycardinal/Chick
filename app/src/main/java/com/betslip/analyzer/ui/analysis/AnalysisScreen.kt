package com.betslip.analyzer.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.betslip.analyzer.data.model.BetSlipData
import com.betslip.analyzer.data.model.SelectionAnalysis
import com.betslip.analyzer.ui.components.SelectionCard

@Composable
fun AnalysisScreen(
    betSlipData: BetSlipData,
    onBackToScanner: () -> Unit,
    viewModel: AnalysisViewModel = viewModel()
) {
    val analysisResults by viewModel.analysisResults.observeAsState(emptyList())
    val overallResult by viewModel.overallResult.observeAsState()
    val loadingState by viewModel.loadingState.observeAsState(false)
    val errorState by viewModel.errorState.observeAsState()
    val selectedDetails by viewModel.selectedSelectionDetails.observeAsState()
    
    LaunchedEffect(betSlipData) {
        viewModel.analyzeSelections(betSlipData)
    }
    
    if (selectedDetails != null) {
        SelectionDetailScreen(
            analysis = selectedDetails!!,
            onBack = { viewModel.clearSelection() }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Surface(
                color = Color(0xFF2196F3),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = onBackToScanner) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            "Analysis",
                            color = Color.White,
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
            
            if (loadingState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Fetching match data...")
                    }
                }
            } else if (errorState != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            errorState ?: "Unknown error",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        overallResult?.let { result ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        result.riskLevel == "LOW RISK" -> Color(0xFFE8F5E9)
                                        result.riskLevel == "MEDIUM RISK" -> Color(0xFFFFF3E0)
                                        else -> Color(0xFFFFEBEE)
                                    }
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Overall Assessment",
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        result.overallRecommendation,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Column {
                                            Text("Risk Level", fontSize = 12.sp, color = Color.Gray)
                                            Text(result.riskLevel, fontSize = 12.sp)
                                        }
                                        Column {
                                            Text("Odds", fontSize = 12.sp, color = Color.Gray)
                                            Text("${String.format("%.2f", result.originalOdds)}", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (betSlipData.bookingCode.isNotEmpty()) {
                                    Text(
                                        "Booking Code: ${betSlipData.bookingCode}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    "Stake: ${String.format("%.2f", betSlipData.totalStake)}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "Selections: ${analysisResults.size}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    items(analysisResults) { analysis ->
                        SelectionCard(
                            analysis = analysis,
                            onClick = { viewModel.selectForDetails(analysis) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionDetailScreen(
    analysis: SelectionAnalysis,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Surface(
            color = Color(0xFF2196F3),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    analysis.selection.team,
                    color = Color.White,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Match Type", fontSize = 12.sp, color = Color.Gray)
                                Text(analysis.selection.matchType, fontSize = 14.sp)
                            }
                            Column {
                                Text("Odds", fontSize = 12.sp, color = Color.Gray)
                                Text("@${String.format("%.2f", analysis.selection.odds)}", fontSize = 14.sp, color = Color(0xFF2196F3))
                            }
                        }
                        
                        Text(analysis.recommendation, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column {
                                Text("Win Probability", fontSize = 11.sp, color = Color.Gray)
                                Text("${String.format("%.1f", analysis.winProbability)}%", fontSize = 12.sp)
                            }
                            Column {
                                Text("Confidence", fontSize = 11.sp, color = Color.Gray)
                                Text("${String.format("%.1f", analysis.confidence)}%", fontSize = 12.sp)
                            }
                            Column {
                                Text("Status", fontSize = 11.sp, color = Color.Gray)
                                Text(if (analysis.currentlyLive) "🔴 LIVE" else "✅ Done", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            
            item {
                Text(
                    "Insights",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                )
            }
            
            items(analysis.insights) { insight ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        insight,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
