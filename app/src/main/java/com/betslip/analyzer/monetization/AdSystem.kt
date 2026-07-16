package com.betslip.analyzer.monetization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AdPlacement {
    BANNER_BOTTOM,
    BETWEEN_SELECTIONS,
    INTERSTITIAL_RESULT
}

data class NativeAd(
    val id: String,
    val title: String,
    val description: String,
    val ctaText: String,
    val imageUrl: String?,
    val link: String,
    val placement: AdPlacement,
    val priority: Int
)

@Composable
fun SmartBannerAd(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Bet with confidence",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                "Sponsored",
                fontSize = 10.sp,
                color = Color(0xFFBDBDBD)
            )
        }
    }
}

@Composable
fun ContextualBettingAd(
    bettingProvider: String = "BetKing",
    offer: String = "Get 100% Bonus",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE3F2FD))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    bettingProvider,
                    fontSize = 12.sp,
                    color = Color(0xFF1976D2)
                )
                Text(
                    offer,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Text(
                "Tap →",
                fontSize = 11.sp,
                color = Color(0xFF1976D2)
            )
        }
    }
}

object AdStrategy {
    fun shouldShowAd(
        sessionAdsShown: Int,
        maxAdsPerSession: Int = 2,
        timeSinceLastAd: Long = 0L,
        minTimeBetweenAds: Long = 60000
    ): Boolean {
        return sessionAdsShown < maxAdsPerSession && timeSinceLastAd > minTimeBetweenAds
    }
    
    fun selectBestAd(
        userBettingHistory: List<String>,
        availableAds: List<NativeAd>
    ): NativeAd? {
        return availableAds.maxByOrNull { ad ->
            val score = ad.priority.toDouble()
            if (userBettingHistory.any { ad.title.contains(it, ignoreCase = true) }) {
                score * 1.5
            } else {
                score
            }
        }
    }
}

data class RevenueMetrics(
    val totalAdsShown: Int,
    val totalClicks: Int,
    val totalImpressions: Int,
    val ctr: Double,
    val estimatedEarnings: Double
)

object RevenueCalculator {
    fun calculateRevenueMetrics(
        adsShown: Int,
        clicks: Int,
        cpmRate: Double = 5.0,
        cpcRate: Double = 0.25
    ): RevenueMetrics {
        val ctr = if (adsShown > 0) (clicks.toDouble() / adsShown) * 100 else 0.0
        val cpmEarnings = (adsShown / 1000.0) * cpmRate
        val cpcEarnings = clicks * cpcRate
        val totalEarnings = cpmEarnings + cpcEarnings
        
        return RevenueMetrics(
            totalAdsShown = adsShown,
            totalClicks = clicks,
            totalImpressions = adsShown,
            ctr = ctr,
            estimatedEarnings = totalEarnings
        )
    }
}
