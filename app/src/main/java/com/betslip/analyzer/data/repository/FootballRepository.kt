package com.betslip.analyzer.data.repository

import android.util.Log
import com.betslip.analyzer.data.api.RetrofitClient
import com.betslip.analyzer.data.model.FootballMatch
import com.betslip.analyzer.data.model.LiveMatch
import com.betslip.analyzer.data.model.TeamStats

class FootballRepository {
    private val apiService = RetrofitClient.apiService
    
    suspend fun getLatestMatches(): Result<List<FootballMatch>> {
        return try {
            val response = apiService.getLatestMatches(limit = 30)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.matches)
            } else {
                Log.e("FootballRepository", "API Error: ${response.code()}")
                Result.failure(Exception("Failed to fetch latest matches: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("FootballRepository", "Error fetching latest matches", e)
            Result.failure(e)
        }
    }
    
    suspend fun getLiveMatches(): Result<List<LiveMatch>> {
        return try {
            val response = apiService.getLiveMatches()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.live)
            } else {
                Result.failure(Exception("Failed to fetch live matches"))
            }
        } catch (e: Exception) {
            Log.e("FootballRepository", "Error fetching live matches", e)
            Result.failure(e)
        }
    }
    
    suspend fun getLeagueMatches(leagueId: String): Result<List<FootballMatch>> {
        return try {
            val response = apiService.getLeagueMatches(leagueId, limit = 20)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.matches)
            } else {
                Result.failure(Exception("Failed to fetch league matches"))
            }
        } catch (e: Exception) {
            Log.e("FootballRepository", "Error fetching league matches", e)
            Result.failure(e)
        }
    }
    
    suspend fun getTeamStats(teamId: String): Result<TeamStats> {
        return try {
            val response = apiService.getTeamStats(teamId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.stats)
            } else {
                Result.failure(Exception("Failed to fetch team stats"))
            }
        } catch (e: Exception) {
            Log.e("FootballRepository", "Error fetching team stats", e)
            Result.failure(e)
        }
    }
}
