package com.betslip.analyzer.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.betslip.analyzer.data.model.FootballMatch
import com.betslip.analyzer.data.model.LiveMatch
import com.betslip.analyzer.data.model.TeamInfo
import com.betslip.analyzer.data.model.TeamStats

interface SportSrcApiService {
    @GET("api/football/latest-matches")
    suspend fun getLatestMatches(
        @Query("limit") limit: Int = 20
    ): Response<LatestMatchesResponse>

    @GET("api/football/live")
    suspend fun getLiveMatches(): Response<LiveMatchesResponse>

    @GET("api/football/league/{league_id}/matches")
    suspend fun getLeagueMatches(
        @Path("league_id") leagueId: String,
        @Query("limit") limit: Int = 20
    ): Response<LeagueMatchesResponse>

    @GET("api/football/team/{team_id}/stats")
    suspend fun getTeamStats(
        @Path("team_id") teamId: String
    ): Response<TeamStatsResponse>
}

data class LatestMatchesResponse(
    val matches: List<FootballMatch> = emptyList(),
    val timestamp: Long = 0
)

data class LiveMatchesResponse(
    val live: List<LiveMatch> = emptyList(),
    val count: Int = 0
)

data class LeagueMatchesResponse(
    val league: LeagueInfo = LeagueInfo("", ""),
    val matches: List<FootballMatch> = emptyList()
)

data class TeamStatsResponse(
    val team: TeamInfo = TeamInfo("", ""),
    val stats: TeamStats = TeamStats()
)

data class LeagueInfo(
    val id: String,
    val name: String
)
