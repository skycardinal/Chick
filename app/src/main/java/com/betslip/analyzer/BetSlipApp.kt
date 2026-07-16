package com.betslip.analyzer

import android.app.Application
import androidx.room.Room
import com.betslip.analyzer.data.database.BetSlipDatabase

class BetSlipApp : Application() {
    companion object {
        lateinit var database: BetSlipDatabase
    }
    
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            BetSlipDatabase::class.java,
            "betslip_db"
        ).fallbackToDestructiveMigration().build()
    }
}
