# Bet-Slip Analyzer

A comprehensive Android application for scanning and analyzing sports bet-slips with OCR, real-time match data, and AI-powered recommendations.

## Features

✅ **Bet-Slip OCR Scanning**
- Scan physical bet-slips with your camera
- Extract team names, odds, and selection types
- Parse booking codes and stake amounts

✅ **Real-Time Match Analysis**
- Fetch live match data from SportSRC API
- Get current team form and statistics
- Live score tracking

✅ **Intelligent Predictions**
- Calculate win probability for each selection
- Confidence scoring based on recent form
- Risk assessment and overall bet evaluation

✅ **Actionable Insights**
- Recent form analysis (W/D/L)
- Goal scoring trends
- Odds value assessment
- Alternative selection recommendations

## Architecture

```
Data Layer
├── API Service (SportSRC)
├── Repository (Data access)
└── Models (Data classes)

UI Layer (Jetpack Compose)
├── Scanner Screen (Camera + OCR)
├── Analysis Screen (Results)
└── Components (Reusable UI)

Business Logic
├── ViewModel (State management)
├── Analysis Engine (Algorithms)
└── OCR Service (Text extraction)
```

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **API**: Retrofit + Gson
- **OCR**: ML Kit Text Recognition
- **Architecture**: MVVM
- **Async**: Coroutines

## Installation

1. Clone the repository
```bash
git clone https://github.com/skycardinal/bet-slip-analyzer.git
cd bet-slip-analyzer
```

2. Open in Android Studio
```bash
# Navigate to project root and open with Android Studio
open -a "Android Studio" .
```

3. Build the app
```bash
./gradlew build
```

4. Run on emulator or device
```bash
./gradlew installDebug
```

## Usage

1. **Scan a Bet-Slip**
   - Tap the camera button
   - Capture a clear image of your bet-slip
   - App automatically extracts selections

2. **View Analysis**
   - See individual selection recommendations
   - Check overall bet assessment
   - Review risk level and odds value

3. **Get Insights**
   - Tap any selection for detailed analysis
   - View team form, scoring trends
   - Get value assessment vs market odds

## API Integration

The app uses **SportSRC API** for football data:
- No API key required
- Unlimited free requests
- Real-time match information
- Latest 2026 fixtures and scores

## Testing

Run unit tests:
```bash
./gradlew test
```

## Permissions

- `android.permission.INTERNET` - API calls
- `android.permission.CAMERA` - Bet-slip scanning

## Future Enhancements

- [ ] Live match tracking during bet resolution
- [ ] Push notifications for odds changes
- [ ] Multi-sport support (Basketball, Tennis, etc.)
- [ ] Bet history tracking
- [ ] Machine learning model for better predictions
- [ ] Social features (share analyses)

## Contributing

Pull requests are welcome. For major changes, please open an issue first.

## License

MIT License - see LICENSE file

## Author

**@skycardinal** - Initial development

## Support

For issues or questions, please open a GitHub issue.
