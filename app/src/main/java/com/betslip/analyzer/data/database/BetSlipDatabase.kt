package com.betslip.analyzer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.TypeConverter
import com.betslip.analyzer.data.model.FootballMatch
import com.betslip.analyzer.data.model.PredictionRecord
import com.betslip.analyzer.data.model.ModelMetrics
import com.betslip.analyzer.data.model.ActualOutcome
import com.google.gson.Gson
import com.betslip.analyzer.data.model.TeamInfo

@Database(
    entities = [
        FootballMatch::class,
        PredictionRecord::class,
        ModelMetrics::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class BetSlipDatabase : RoomDatabase() {
    abstract fun predictionDao(): PredictionDao
    abstract fun metricsDao(): ModelMetricsDao
}

@Dao
interface PredictionDao {
    @Insert
    suspend fun insertPrediction(prediction: PredictionRecord)
    
    @Update
    suspend fun updatePrediction(prediction: PredictionRecord)
    
    @Query("SELECT * FROM prediction_history WHERE actualOutcome = :outcome LIMIT :limit")
    suspend fun getPredictionsByOutcome(outcome: String, limit: Int): List<PredictionRecord>
    
    @Query("SELECT * FROM prediction_history ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentPredictions(limit: Int): List<PredictionRecord>
    
    @Query("SELECT * FROM prediction_history WHERE teamName = :teamName")
    suspend fun getTeamPredictions(teamName: String): List<PredictionRecord>
    
    @Query("SELECT COUNT(*) FROM prediction_history WHERE actualOutcome = 'WON'")
    suspend fun getCorrectPredictionCount(): Int
    
    @Query("SELECT COUNT(*) FROM prediction_history WHERE actualOutcome != 'PENDING'")
    suspend fun getTotalResolvedPredictions(): Int
}

@Dao
interface ModelMetricsDao {
    @Insert
    suspend fun insertMetrics(metrics: ModelMetrics)
    
    @Query("SELECT * FROM model_metrics ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMetrics(): ModelMetrics?
    
    @Query("SELECT * FROM model_metrics ORDER BY timestamp DESC LIMIT :days")
    suspend fun getMetricsForLastDays(days: Int): List<ModelMetrics>
}

class DatabaseConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromTeamInfo(teamInfo: TeamInfo): String {
        return gson.toJson(teamInfo)
    }
    
    @TypeConverter
    fun toTeamInfo(json: String): TeamInfo {
        return gson.fromJson(json, TeamInfo::class.java)
    }
    
    @TypeConverter
    fun fromActualOutcome(outcome: ActualOutcome): String {
        return outcome.name
    }
    
    @TypeConverter
    fun toActualOutcome(outcome: String): ActualOutcome {
        return ActualOutcome.valueOf(outcome)
    }
}
