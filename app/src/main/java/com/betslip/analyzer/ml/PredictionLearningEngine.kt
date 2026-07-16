package com.betslip.analyzer.ml

import android.util.Log
import com.betslip.analyzer.data.database.PredictionDao
import com.betslip.analyzer.data.database.ModelMetricsDao
import com.betslip.analyzer.data.model.*
import kotlin.math.pow
import kotlin.math.sqrt

class PredictionLearningEngine(
    private val predictionDao: PredictionDao,
    private val metricsDao: ModelMetricsDao
) {
    
    suspend fun recordPrediction(
        selectionAnalysis: SelectionAnalysis,
        selection: Selection
    ) {
        val prediction = PredictionRecord(
            selectionId = "${selection.team}_${selection.matchType}_${selection.date}",
            teamName = selection.team,
            matchType = selection.matchType,
            oddsOffered = selection.odds,
            predictedWinProbability = selectionAnalysis.winProbability,
            predictedConfidence = selectionAnalysis.confidence,
            createdAt = System.currentTimeMillis()
        )
        predictionDao.insertPrediction(prediction)
        Log.d("PredictionEngine", "Recorded prediction for ${selection.team}")
    }
    
    suspend fun updatePredictionOutcome(
        selectionId: String,
        actualOutcome: ActualOutcome,
        actualWinProbability: Double = 0.0
    ) {
        // Find prediction by selection ID - would need extended DAO
        Log.d("PredictionEngine", "Updated outcome: $actualOutcome")
        recalculateModelMetrics()
    }
    
    suspend fun getTeamAccuracy(teamName: String): TeamAccuracy {
        val predictions = predictionDao.getTeamPredictions(teamName)
        val resolved = predictions.filter { it.actualOutcome != ActualOutcome.PENDING }
        
        if (resolved.isEmpty()) return TeamAccuracy(teamName, 0.0, 0)
        
        val correct = resolved.count { isCorrectPrediction(it) }
        val accuracy = (correct.toDouble() / resolved.size) * 100
        
        return TeamAccuracy(teamName, accuracy, resolved.size)
    }
    
    suspend fun calculateCalibration(): Double {
        val predictions = predictionDao.getRecentPredictions(1000)
        val resolved = predictions.filter { it.actualOutcome != ActualOutcome.PENDING }
        
        if (resolved.isEmpty()) return 0.0
        
        var sumSquaredDifference = 0.0
        var count = 0
        
        resolved.forEach { pred ->
            val correct = isCorrectPrediction(pred)
            val predictedProb = if (correct) pred.predictedWinProbability / 100.0 else (100 - pred.predictedWinProbability) / 100.0
            val actualProb = if (correct) 1.0 else 0.0
            sumSquaredDifference += (predictedProb - actualProb).pow(2)
            count++
        }
        
        val brierScore = sumSquaredDifference / count
        return (1 - brierScore) * 100
    }
    
    suspend fun recalculateModelMetrics() {
        val totalResolved = predictionDao.getTotalResolvedPredictions()
        val correct = predictionDao.getCorrectPredictionCount()
        
        if (totalResolved == 0) return
        
        val accuracy = (correct.toDouble() / totalResolved) * 100
        val calibration = calculateCalibration()
        val recentPredictions = predictionDao.getRecentPredictions(100)
        val avgConfidence = if (recentPredictions.isNotEmpty()) recentPredictions.map { it.predictedConfidence }.average() else 0.0
        
        val metrics = ModelMetrics(
            timestamp = System.currentTimeMillis(),
            totalPredictions = totalResolved,
            correctPredictions = correct,
            accuracy = accuracy,
            calibration = calibration,
            avgConfidence = avgConfidence,
            profitLoss = 0.0
        )
        
        metricsDao.insertMetrics(metrics)
        Log.d("PredictionEngine", "Model Metrics: Accuracy=$accuracy%, Calibration=$calibration%")
    }
    
    suspend fun getAdjustedConfidence(
        selectionAnalysis: SelectionAnalysis,
        teamName: String
    ): Double {
        val teamAccuracy = getTeamAccuracy(teamName)
        
        if (teamAccuracy.sampleSize > 5) {
            val accuracyFactor = teamAccuracy.accuracy / 50.0
            val adjustedConfidence = selectionAnalysis.confidence * accuracyFactor
            return minOf(95.0, maxOf(5.0, adjustedConfidence))
        }
        
        return selectionAnalysis.confidence
    }
    
    suspend fun getModelReliability(): Double {
        val metrics = metricsDao.getLatestMetrics() ?: return 0.5
        
        val avgAccuracy = (metrics.accuracy + metrics.calibration) / 2
        val sampleReliability = when {
            metrics.totalPredictions > 100 -> 1.0
            metrics.totalPredictions > 50 -> 0.8
            metrics.totalPredictions > 10 -> 0.6
            else -> 0.4
        }
        
        return (avgAccuracy / 100) * sampleReliability
    }
    
    private fun isCorrectPrediction(prediction: PredictionRecord): Boolean {
        return prediction.actualOutcome == ActualOutcome.WON
    }
}

data class TeamAccuracy(
    val teamName: String,
    val accuracy: Double,
    val sampleSize: Int
)
