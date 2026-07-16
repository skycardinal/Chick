package com.betslip.analyzer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// ============= Football Match Models =============
@Entity(tableName = "matches")
data class FootballMatch(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    
    @SerializedName("homeTeam")
    val homeTeam: TeamInfo,
    
    @SerializedName("awayTeam")
    val awayTeam: TeamInfo,
    
    @SerializedName("homeScore")
    val homeScore: Int,
    
    @SerializedName("awayScore")
    val awayScore: Int,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("date")
    val date: String,
    
    @SerializedName("league")
    val league: String,
    
    @SerializedName("odds")
    val odds: OddsInfo?
)

data class TeamInfo(
    val id: String = "",
    val name: String,
    val logo: String? = null
)

data class OddsInfo(
    val homeWin: Double = 0.0,
    val draw: Double = 0.0,
    val awayWin: Double = 0.0
)

data class LiveMatch(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val minute: Int,
    val league: String
)

data class TeamStats(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val form: List<String> = emptyList()
)

// ============= Bet-Slip Models =============
data class Selection(
    val team: String,
    val matchType: String,
    val odds: Double,
    val league: String = "Unknown",
    val date: Long = System.currentTimeMillis()
)

data class BetSlipData(
    val selections: List<Selection>,
    val totalStake: Double,
    val betType: String,
    val date: Long,
    val bookingCode: String = "",
    val totalOdds: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

// ============= Analysis Models =============
data class SelectionAnalysis(
    val selection: Selection,
    val recentForm: String,
    val winProbability: Double,
    val recommendation: String,
    val confidence: Double,
    val currentlyLive: Boolean,
    val lastMatches: List<FootballMatch>,
    val insights: List<String>
)

data class BetSlipAnalysisResult(
    val bookingCode: String,
    val originalOdds: Double,
    val analysisOdds: Double,
    val selections: List<SelectionAnalysis>,
    val overallRecommendation: String,
    val riskLevel: String,
    val suggestedAlternatives: List<Selection> = emptyList()
)
