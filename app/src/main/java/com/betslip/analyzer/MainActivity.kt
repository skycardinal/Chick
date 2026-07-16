package com.betslip.analyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.betslip.analyzer.data.model.BetSlipData
import com.betslip.analyzer.ui.analysis.AnalysisScreen
import com.betslip.analyzer.ui.scanner.OcrService
import com.betslip.analyzer.ui.scanner.ScannerScreen
import com.betslip.analyzer.ui.theme.BetSlipAnalyzerTheme

class MainActivity : ComponentActivity() {
    private val ocrService = OcrService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            BetSlipAnalyzerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(ocrService)
                }
            }
        }
    }
}

@Composable
fun MainApp(ocrService: OcrService) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Scanner) }
    var scannedBetSlip by remember { mutableStateOf<BetSlipData?>(null) }
    
    when (currentScreen) {
        Screen.Scanner -> {
            ScannerScreen(
                onBetSlipScanned = { betSlipData ->
                    scannedBetSlip = betSlipData
                    currentScreen = Screen.Analysis
                },
                onBackToScanner = {
                    currentScreen = Screen.Scanner
                    scannedBetSlip = null
                },
                ocrService = ocrService
            )
        }
        Screen.Analysis -> {
            if (scannedBetSlip != null) {
                AnalysisScreen(
                    betSlipData = scannedBetSlip!!,
                    onBackToScanner = {
                        currentScreen = Screen.Scanner
                        scannedBetSlip = null
                    }
                )
            }
        }
    }
}

sealed class Screen {
    object Scanner : Screen()
    object Analysis : Screen()
}
