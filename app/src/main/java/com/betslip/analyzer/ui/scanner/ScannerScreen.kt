package com.betslip.analyzer.ui.scanner

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.betslip.analyzer.data.model.BetSlipData

@Composable
fun ScannerScreen(
    onBetSlipScanned: (BetSlipData) -> Unit,
    onBackToScanner: () -> Unit = {},
    ocrService: OcrService
) {
    var scannedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            scannedBitmap = bitmap
            isProcessing = true
            errorMessage = null
            
            val result = ocrService.extractBetSlipData(bitmap)
            result.onSuccess { betSlipData ->
                isProcessing = false
                if (betSlipData.selections.isNotEmpty()) {
                    onBetSlipScanned(betSlipData)
                } else {
                    errorMessage = "No selections found in bet-slip. Please try again."
                }
            }
            result.onFailure { exception ->
                isProcessing = false
                errorMessage = exception.message ?: "Failed to process image"
            }
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isProcessing = true
            errorMessage = null
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            errorMessage = "Camera permission denied. Enable it in settings."
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "📊 Bet-Slip Analyzer",
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.headlineLarge
        )
        
        Text(
            "Scan your bet-slip to get analysis",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Processing bet-slip...", fontSize = 16.sp)
        } else {
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier
                    .size(120.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Scan",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "Gallery",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Or choose from gallery")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Tap to scan a bet-slip", fontSize = 16.sp, color = Color.Gray)
        }
        
        errorMessage?.let {
            Spacer(modifier = Modifier.height(32.dp))
            Surface(
                color = Color(0xFFFF6B6B),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    it,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}
