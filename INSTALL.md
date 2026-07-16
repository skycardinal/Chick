# Bet-Slip Analyzer v2.0 - INSTALLATION GUIDE

## 🚀 QUICK START

### Prerequisites
- ✅ Android Studio installed
- ✅ Android SDK 34+
- ✅ Physical Android phone OR emulator
- ✅ USB cable (for physical device)
- ✅ ~500MB disk space

### Installation Steps (5 minutes)

```bash
# 1. Clone the repository
git clone https://github.com/skycardinal/Chick.git
cd Chick

# 2. Open in Android Studio
open -a "Android Studio" .
# OR manually: File → Open → Select Chick folder

# 3. Wait for Gradle to sync (2 mins)
# Android Studio will download dependencies

# 4. Connect your phone via USB
# - Enable Developer Mode (tap Build Number 7 times in Settings)
# - Enable USB Debugging
# - Trust the computer connection

# 5. Build the app
./gradlew build
# OR in Android Studio: Build → Build Bundle(s) / APK(s) → Build APK(s)

# 6. Install on device
./gradlew installDebug
# OR in Android Studio: Run → Run 'app' → Select your device

# 7. App installs in ~1 minute
# Tap app icon to launch
```

## ✨ What's Ready to Test

✅ **Scanner Screen**
- Tap camera button
- Capture bet-slip from any provider
- Auto-detects: SportyBet, BetKing, Nairabet, 1xBet, Bet365, etc.

✅ **Analysis Screen**
- Shows all selections
- Win probability (5-95%)
- Confidence scores
- Recommendations
- Model reliability %
- Betting provider detected

✅ **Learning Engine (Backend)**
- Records every prediction
- Stores to local database
- Calculates team accuracy
- Tracks model metrics

✅ **Smart Ads** (Non-intrusive)
- Max 2 ads per session
- Native format (blends with UI)
- Strategic placement

✅ **Multi-Provider Support**
- SportyBet ✅
- BetKing ✅
- Nairabet ✅
- 1xBet ✅
- Bet365 ✅
- Plus 2 more

## 🎯 What to Test First

### Test 1: Scanning (2 mins)
1. Open app
2. Tap camera button
3. Take photo of your SportyBet bet-slip
4. Should extract: Teams, odds, selections
5. **Expected result:** ✅ All data extracted correctly

### Test 2: Analysis (2 mins)
1. After scanning, view analysis
2. Should show each selection with:
   - Win probability
   - Confidence %
   - Recommendation
   - Form (W/D/L)
3. **Expected result:** ✅ Analysis generates without errors

### Test 3: Model Reliability (1 min)
1. Look at top of analysis screen
2. Should show "Model Reliability: X%"
3. **Expected result:** ✅ Shows percentage (starts at 50% - will improve with data)

### Test 4: Multi-Provider (2 mins)
1. If you have BetKing slip, scan that too
2. App should detect provider automatically
3. Should parse correctly
4. **Expected result:** ✅ Provider correctly identified

### Test 5: Database Recording (backend)
1. Check Android Studio Logcat
2. Look for: "Recorded prediction for [Team Name]"
3. **Expected result:** ✅ Logs show predictions being saved

## 🔍 How to View Logs

```bash
# Terminal method
adb logcat | grep "Prediction"

# OR in Android Studio
# View → Tool Windows → Logcat
# Filter: "Prediction" or "PredictionEngine"
```

## 🐛 Troubleshooting

### Issue: "Build failed: Cannot resolve symbol"
**Solution:** 
```bash
./gradlew clean
./gradlew build
```

### Issue: "Cannot resolve Room Database"
**Solution:** Gradle dependencies auto-download, wait 2-3 mins

### Issue: "Camera won't open"
**Check:**
- Device has camera
- Permissions granted (Android Studio should prompt)
- Emulator camera enabled (Settings → Virtual Device Manager)

### Issue: "No predictions recorded"
**Expected:** First predictions don't show metrics until resolved
**Timeline:** Need 8+ resolved predictions to see accuracy

## 📱 Testing on Emulator vs Device

### Physical Device (Recommended)
✅ Camera works
✅ Faster testing
✅ Real user experience
❌ Need USB cable

### Emulator
✅ No physical device needed
✅ Easy screenshots
❌ Camera requires setup
❌ Slower

## 📊 Data Stored Locally

All data saved to:
```
/data/data/com.betslip.analyzer/databases/betslip_db
```

**Nothing sent to cloud** (unless you enable it in v3.0)

## 🎬 After Installation

1. Test scanning 3-5 different bet-slips
2. Report what works/breaks
3. Screenshot any errors
4. Share Logcat output if issues

## 📞 Support

If something breaks:
1. Screenshot the error
2. Copy Logcat output
3. Tell me:
   - What you did
   - What happened
   - Expected result

## ⚡ Next Steps

After testing this build:
- We'll add "Update Outcome" screen (mark bets won/lost)
- Implement bet history dashboard
- Optimize predictions based on feedback
- Deploy to Play Store (v3.0)

---

**Ready to test?** Follow the 5 steps above and report back! 🚀
