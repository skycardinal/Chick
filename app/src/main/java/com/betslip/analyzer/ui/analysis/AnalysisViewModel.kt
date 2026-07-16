package com.betslip.analyzer.ui.analysis

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betslip.analyzer.data.model.*
import com.betslip.analyzer.data.repository.FootballRepository
import kotlinx.coroutines.launch

class AnalysisViewModel : ViewModel() {
    private val repository = FootballRepository()
    
    private val _analysisResults = MutableLiveData<List<SelectionAnalysis>>()
    val analysisResults: LiveData<List<SelectionAnalysis>> = _analysisResults
    
    private val _overallResult = MutableLiveData<BetSlipAnalysisResult>()
    val overallResult: LiveData<BetSlipAnalysisResult> = _overallResult
    
    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState
    
    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState
    
    private val _selectedSelectionDetails = MutableLiveData<SelectionAnalysis?>()
    val selectedSelectionDetails: LiveData<SelectionAnalysis?> = _selectedSelectionDetails
    
    fun analyzeSelections(betSlipData: BetSlipData) {
        viewModelScope.launch {
            _loadingState.value = true
            _errorState.value = null
            
            try {
                val matchesResult = repository.getLatestMatches()
                val liveMatchesResult = repository.getLiveMatches()
                
                if (!matchesResult.isSuccess) {
                    _errorState.value = "Failed to fetch match data. Please check your internet connection."
                    _loadingState.value = false
                    return@launch
                }
                
                val recentMatches = matchesResult.getOrNull() ?: emptyList()
                val liveMatches = liveMatchesResult.getOrNull() ?: emptyList()
                
                Log.d("AnalysisViewModel", "Fetched ${recentMatches.size} matches and ${liveMatches.size} live matches")
                
                val analysisResults = betSlipData.selections.map { selection ->
                    analyzeSelection(selection, recentMatches, liveMatches)
                }
                
                _analysisResults.value = analysisResults
                
                val overall = generateOverallAnalysis(betSlipData, analysisResults)
                _overallResult.value = overall
                
                _loadingState.value = false
            } catch (e: Exception) {
                Log.e("AnalysisViewModel", "Error analyzing selections", e)
                _errorState.value = e.message ?: "Unknown error occurred"
                _loadingState.value = false
            }
        }
    }
    
    private suspend fun analyzeSelection(
        selection: Selection,
        recentMatches: List<FootballMatch>,
        liveMatches: List<LiveMatch>
    ): SelectionAnalysis {
        val teamMatches = recentMatches.filter { match ->
            match.homeTeam.name.contains(selection.team, ignoreCase = true) ||
            match.awayTeam.name.contains(selection.team, ignoreCase = true)
        }
        
        val currentlyLive = liveMatches.any { match ->
            match.homeTeam.contains(selection.team, ignoreCase = true) ||
            match.awayTeam.contains(selection.team, ignoreCase = true)
        }
        
        val form = calculateForm(teamMatches)
        val winProbability = calculateWinProbability(teamMatches, form)
        val confidence = calculateConfidence(teamMatches)
        val recommendation = generateRecommendation(teamMatches, form, selection, winProbability)
        val insights = generateInsights(teamMatches, form, selection)
        
        return SelectionAnalysis(
            selection = selection,
            recentForm = form,
            winProbability = winProbability,
            recommendation = recommendation,
            confidence = confidence,
            currentlyLive = currentlyLive,
            lastMatches = teamMatches.take(5),
            insights = insights
        )
    }
    
    private fun calculateForm(matches: List<FootballMatch>): String {
        if (matches.isEmpty()) return "❓ No recent data"
        
        val results = matches.take(5).map { match ->
            when {
                match.status == "live" -> "🔴"
                match.homeScore > match.awayScore -> "✅"
                match.homeScore < match.awayScore -> "❌"
                else -> "🟰"
            }
        }
        
        val wins = results.count { it == "✅" }
        val losses = results.count { it == "❌" }
        val draws = results.count { it == "🟰" }
        
        return "${results.joinToString("")} (${wins}W-${draws}D-${losses}L)"
    }
    
    private fun calculateWinProbability(matches: List<FootballMatch>, form: String): Double {
        if (matches.isEmpty()) return 50.0
        
        val wins = matches.count { it.homeScore > it.awayScore }
        val winRate = (wins.toDouble() / matches.size) * 100
        
        val goalDifference = matches.sumOf { it.homeScore - it.awayScore }
        val goalBoost = when {
            goalDifference > 10 -> 10.0
            goalDifference > 0 -> (goalDifference / 2.0)
            else -> 0.0
        }
        
        return minOf(95.0, maxOf(5.0, winRate + goalBoost))
    }
    
    private fun calculateConfidence(matches: List<FootballMatch>): Double {
        return when {
            matches.size >= 10 -> 95.0
            matches.size >= 5 -> 80.0
            matches.size >= 3 -> 60.0
            matches.size >= 1 -> 40.0
            else -> 20.0
        }
    }
    
    private fun generateRecommendation(
        matches: List<FootballMatch>,
        form: String,
        selection: Selection,
        winProbability: Double
    ): String {
        if (matches.isEmpty()) return "⚠️ Insufficient data"
        
        val wins = matches.count { it.homeScore > it.awayScore }
        val losses = matches.count { it.homeScore < it.awayScore }
        
        return when {
            winProbability > 75 && wins > losses * 2 -> "✅ STRONG PICK - Team in excellent form"
            winProbability > 60 && wins > losses -> "👍 GOOD VALUE - Team performing well"
            winProbability > 50 -> "⚠️ MODERATE - Mixed recent form"
            losses > wins -> "❌ RISKY - Poor recent performance"
            else -> "🟰 MIXED - Inconsistent results"
        }
    }
    
    private fun generateInsights(
        matches: List<FootballMatch>,
        form: String,
        selection: Selection
    ): List<String> {
        val insights = mutableListOf<String>()
        
        if (matches.isEmpty()) {
            insights.add("❓ No recent match data available for detailed analysis")
            return insights
        }
        
        val wins = matches.count { it.homeScore > it.awayScore }
        val losses = matches.count { it.homeScore < it.awayScore }
        val draws = matches.count { it.homeScore == it.awayScore }
        
        when {
            wins >= 3 -> insights.add("🔥 Winning streak: ${wins} wins in last ${matches.size} matches")
            losses >= 3 -> insights.add("❄️ Struggling: ${losses} losses in last ${matches.size} matches")
            else -> insights.add("📊 Mixed form: $wins wins, $losses losses, $draws draws")
        }
        
        val totalGoalsFor = matches.sumOf { it.homeScore }
        val totalGoalsAgainst = matches.sumOf { it.awayScore }
        val avgGoalsFor = totalGoalsFor.toDouble() / matches.size
        val avgGoalsAgainst = totalGoalsAgainst.toDouble() / matches.size
        
        insights.add("⚽ Scoring avg: ${String.format("%.2f", avgGoalsFor)} GF / ${String.format("%.2f", avgGoalsAgainst)} GA")
        
        val lastThree = matches.take(3)
        val recentWins = lastThree.count { it.homeScore > it.awayScore }
        insights.add("📈 Recent trend: $recentWins wins in last 3")
        
        val impliedProbability = 1.0 / selection.odds
        val ourProbability = calculateWinProbability(matches, form) / 100.0
        val value = ourProbability - impliedProbability
        
        when {
            value > 0.15 -> insights.add("💰 EXCELLENT VALUE - Odds underestimate team by ~${(value*100).toInt()}%")
            value > 0.05 -> insights.add("✅ GOOD VALUE - Slight edge over market")
            value < -0.15 -> insights.add("⚠️ POOR VALUE - Odds overestimate by ~${(-value*100).toInt()}%")
            else -> insights.add("➖ FAIR VALUE - Odds match expected probability")
        }
        
        return insights
    }
    
    private fun generateOverallAnalysis(
        betSlipData: BetSlipData,
        analyses: List<SelectionAnalysis>
    ): BetSlipAnalysisResult {
        val strongPicks = analyses.count { it.recommendation.contains("STRONG") }
        val goodPicks = analyses.count { it.recommendation.contains("GOOD") }
        val riskPicks = analyses.count { it.recommendation.contains("RISKY") || it.recommendation.contains("POOR") }
        
        val avgConfidence = analyses.map { it.confidence }.average()
        val avgWinProbability = analyses.map { it.winProbability }.average()
        
        val impliedProb = 1.0 / betSlipData.totalOdds
        val recommendedOdds = analyses.fold(1.0) { acc, analysis ->
            acc * (analysis.winProbability / 100.0)
        }
        
        val recommendation = when {
            strongPicks == analyses.size && avgWinProbability > 70 -> "✅ EXCELLENT BET - All selections with strong form"
            strongPicks + goodPicks >= analyses.size * 0.7 -> "👍 GOOD BET - Majority in good form"
            riskPicks >= analyses.size * 0.5 -> "❌ HIGH RISK - Multiple weak selections"
            else -> "⚠️ MIXED - Consider reviewing selections"
        }
        
        val riskLevel = when {
            avgWinProbability > 75 -> "LOW RISK"
            avgWinProbability > 60 -> "MEDIUM RISK"
            else -> "HIGH RISK"
        }
        
        return BetSlipAnalysisResult(
            bookingCode = betSlipData.bookingCode,
            originalOdds = betSlipData.totalOdds,
            analysisOdds = recommendedOdds,
            selections = analyses,
            overallRecommendation = recommendation,
            riskLevel = riskLevel
        )
    }
    
    fun selectForDetails(analysis: SelectionAnalysis) {
        _selectedSelectionDetails.value = analysis
    }
    
    fun clearSelection() {
        _selectedSelectionDetails.value = null
    }
}
