# 07 — Folder Structure

Literal package tree for the Clean Architecture layering defined in `02_ARCHITECTURE_SUMMARY.md`. Base package: `com.playit.app` (adjust to whatever the group ID is actually registered as — this is a placeholder, confirm before scaffolding).

```
app/src/main/java/com/playit/app/
│
├── PlayItApplication.kt                  # @HiltAndroidApp
├── MainActivity.kt                       # single-activity host, sets up NavGraph
│
├── di/
│   ├── DatabaseModule.kt                 # provides PlayItDatabase, DAOs
│   ├── RepositoryModule.kt               # binds Repository interfaces → Impls
│   ├── SpeechModule.kt                   # provides VoskRecognizer singleton, AudioCapture
│   └── AudioModule.kt                    # provides AudioPlayer
│
├── navigation/
│   ├── NavGraph.kt
│   ├── Routes.kt                         # route string constants + nav-arg keys
│   └── SessionManager.kt                 # in-memory active-profile singleton (cross-cutting; see 02 §2)
│
├── domain/                               # pure Kotlin — no android.* imports
│   ├── model/
│   │   ├── GameplayConstants.kt          # heart pools, star thresholds, timing budgets (05 §1.5)
│   │   ├── Phoneme.kt, Profile.kt, LetterGroup.kt, BlendItWord.kt, MapNode.kt (sealed: LetterNode | BlendItNode), ...
│   │   └── RiskStatus.kt, FeedbackState.kt, RecognitionResult.kt
│   ├── repository/                       # interfaces only
│   │   ├── ProfileRepository.kt, PhonemeRepository.kt, LessonProgressRepository.kt
│   │   ├── SayItAttemptRepository.kt, FindItAttemptRepository.kt
│   │   ├── BlendItWordRepository.kt, BlendItAttemptRepository.kt, BlendItProgressRepository.kt
│   │   ├── AchievementRepository.kt, ReportLogRepository.kt, PictureAssetRepository.kt
│   └── manager/
│       ├── HeartManager.kt, StarCalculator.kt, BlendItStarThresholds.kt
│       ├── StreakTracker.kt, UnlockManager.kt, GroupUnlockManager.kt
│       ├── SpeechValidator.kt, GridGenerator.kt, BlendItWordSelector.kt
│       ├── LetterStatusCalculator.kt, RetentionCalculator.kt, ReportGenerator.kt
│
├── data/
│   ├── local/
│   │   ├── PlayItDatabase.kt
│   │   ├── entity/                       # one file per table, see 08_DATABASE_SPEC.md
│   │   │   ├── ProfileEntity.kt, PhonemeEntity.kt, LetterGroupEntity.kt, LetterGroupMemberEntity.kt
│   │   │   ├── LessonProgressEntity.kt, SayItAttemptEntity.kt, FindItAttemptEntity.kt
│   │   │   ├── BlendItWordEntity.kt, BlendItAttemptEntity.kt, BlendItProgressEntity.kt
│   │   │   ├── AchievementEntity.kt, ReportLogEntity.kt
│   │   └── dao/                          # one DAO per entity above
│   ├── repository/                       # Impl classes, implement domain/repository interfaces
│   ├── speech/
│   │   ├── VoskRecognizer.kt, AudioCapture.kt, NoiseMonitor.kt
│   ├── audio/
│   │   └── AudioPlayer.kt
│   └── pdf/
│       └── PdfExporter.kt
│
└── presentation/
    ├── theme/                            # Color.kt, Type.kt, Shape.kt, Motion.kt — from 03_DESIGN_SYSTEM_SUMMARY.md
    ├── components/                       # cross-module reusable Composables: MascotBubble, HeartDisplay, StarAnimation, ...
    ├── splash/            SplashScreen.kt
    ├── profile/           ProfileSelectScreen.kt, NamePromptScreen.kt, ProfileViewModel.kt, components/ (ProfileCard, AvatarPicker, AddProfileButton)
    ├── map/                MapScreen.kt, MapViewModel.kt, components/ (LetterNode, BlendItChallengeNode, PathConnector, MascotBubble, TopStatsBar, StreakBadgeUnlock)
    ├── hearit/             HearItScreen.kt, HearItViewModel.kt, components/ (AnimatedLetterCard, PlayButton, ReplayCounter)
    ├── sayit/              SayItScreen.kt, SayItViewModel.kt, components/ (MicrophoneButton, ListeningAnimation, FeedbackCard, AttemptTracker, NoiseLevelIndicator, HeartRecoveryAnimation)
    ├── findit/             FindItScreen.kt, FindItViewModel.kt, components/ (ImageGrid, PictureCard, ScoreIndicator, CompletionAnimation)
    ├── lettercomplete/     LetterCompleteScreen.kt, components/ (StarAnimation)
    ├── blendit/            BlendItScreen.kt, BlendItCompleteScreen.kt, BlendItViewModel.kt, components/ (TargetWordImage, LetterSlotRow, LetterSlot, TileBank, LetterTile, SubmitButton, WordFeedbackCard, HintIndicator, BlendItProgressIndicator)
    └── dashboard/          ParentDashboardScreen.kt, ReportPreviewScreen.kt, ParentDashboardViewModel.kt, components/ (ProfileSwitcherDropdown, OverallStatsCard, LetterPerformanceTable, BlendItSummaryCard, AtRiskSection, ExportButton, ArithmeticGuardDialog)

app/src/main/assets/
├── audio/                                # see 22_FILE_NAMING_CONVENTION.md for exact naming
│   ├── phonemes/                         # 28 letter sound files
│   ├── words/                            # Blend It word audio (35)
│   ├── ui/                               # corrective/feedback/sfx clips
│   └── vosk-model-small-en-us/           # bundled Vosk model directory
├── images/
│   ├── letters/, pictures/, mascot/, backgrounds/, rewards/
└── fonts/                                # Lexend variable font + Andika fallback (03 §5.2)

app/src/test/java/...                     # unit tests, mirrors domain/ package structure
app/src/androidTest/java/...              # instrumented tests, mirrors presentation/ + data/local/
```

## Notes for the Agent

- `domain/` must remain importable by a plain JVM unit test module with zero Android dependencies — if you find yourself importing `android.*` there, the class belongs in `data/` or `presentation/` instead.
- One Composable file per component listed in the SDD's Front-end Component tables (already reflected above) — don't collapse multiple named components into one file; the SDD's naming is what QA and future contributors will search for.
- `assets/` (not `res/raw/`) is the deliberate choice for audio/images — matches the SDD's `AudioPlayer`/`Coil`-from-assets pattern and keeps the asset-naming convention in `22` filesystem-portable.
