package com.betslip.analyzer.ui.scanner

import android.graphics.Bitmap
import android.util.Log
import com.betslip.analyzer.data.model.BetSlipData
import com.betslip.analyzer.data.model.Selection
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrService {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    fun extractBetSlipData(bitmap: Bitmap): Result<BetSlipData> {
        return try {
            val image = InputImage.fromBitmap(bitmap)
            var extractedText = ""
            var recognitionComplete = false
            
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    extractedText = visionText.text
                    recognitionComplete = true
                }
                .addOnFailureListener { exception ->
                    Log.e("OcrService", "Text recognition failed", exception)
                }
            
            var attempts = 0
            while (!recognitionComplete && attempts < 20) {
                Thread.sleep(100)
                attempts++
            }
            
            val parsedData = parseOcrText(extractedText)
            Result.success(parsedData)
        } catch (e: Exception) {
            Log.e("OcrService", "Error processing bet-slip image", e)
            Result.failure(e)
        }
    }
    
    private fun parseOcrText(text: String): BetSlipData {
        val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        
        val selections = mutableListOf<Selection>()
        var totalStake = 0.0
        var totalOdds = 0.0
        var betType = "parlay"
        var bookingCode = ""
        
        Log.d("OcrService", "Parsing text with ${lines.size} lines")
        
        for (i in lines.indices) {
            val line = lines[i]
            
            if (line.matches(Regex("^[A-Z]\\d+[A-Z]+$"))) {
                bookingCode = line
                Log.d("OcrService", "Found booking code: $bookingCode")
            }
            
            if (line.contains("Stake", ignoreCase = true) && i + 1 < lines.size) {
                totalStake = extractAmount(lines[i + 1])
                Log.d("OcrService", "Found stake: $totalStake")
            }
            
            if (line.contains("Odds", ignoreCase = true) && i + 1 < lines.size) {
                totalOdds = extractAmount(lines[i + 1])
                Log.d("OcrService", "Found odds: $totalOdds")
            }
            
            if (line.contains("Home", ignoreCase = true) || 
                line.contains("Away", ignoreCase = true) ||
                line.contains("Draw", ignoreCase = true) ||
                line.contains("Over", ignoreCase = true) ||
                line.contains("Under", ignoreCase = true)) {
                
                val selection = parseSelection(line, lines, i)
                if (selection != null) {
                    selections.add(selection)
                    Log.d("OcrService", "Added selection: ${selection.team} @ ${selection.odds}")
                }
            }
        }
        
        return BetSlipData(
            selections = selections,
            totalStake = totalStake,
            betType = betType,
            date = System.currentTimeMillis(),
            bookingCode = bookingCode,
            totalOdds = totalOdds
        )
    }
    
    private fun parseSelection(line: String, allLines: List<String>, currentIndex: Int): Selection? {
        try {
            val matchType = when {
                line.contains("Home", ignoreCase = true) -> "Home"
                line.contains("Away", ignoreCase = true) -> "Away"
                line.contains("Draw", ignoreCase = true) -> "Draw"
                line.contains("Over", ignoreCase = true) -> "Over"
                line.contains("Under", ignoreCase = true) -> "Under"
                else -> return null
            }
            
            val oddsPattern = Regex("""(\d+\.\d{2})""")
            val oddsMatch = oddsPattern.find(line)
            val odds = oddsMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: return null
            
            val teamLine = if (currentIndex + 1 < allLines.size) allLines[currentIndex + 1] else line
            
            val teamPattern = Regex("""^([A-Za-z\s\.]+?)\s+(?:vs|v)\s+([A-Za-z\s\.]+?)$""")
            val teamMatch = teamPattern.find(teamLine)
            
            val team = if (teamMatch != null) {
                teamMatch.groupValues[1].trim()
            } else {
                teamLine.split(Regex("""\d\.\d""")).firstOrNull()?.trim() ?: "Unknown"
            }
            
            return Selection(
                team = team,
                matchType = matchType,
                odds = odds,
                league = "Unknown",
                date = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e("OcrService", "Error parsing selection from line: $line", e)
            return null
        }
    }
    
    private fun extractAmount(text: String): Double {
        val amountPattern = Regex("""(\d+(?:[.,]\d{2})?(?:\d+)?)""")
        val match = amountPattern.find(text)
        return match?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
    }
}
