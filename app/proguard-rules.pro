# Proguard rules for playIT
# ── Vosk & JNA Native Speech Recognition ──
-keep class org.vosk.** { *; }
-keep class com.alphacephei.vosk.** { *; }
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.** { *; }
-keepclassmembers class * {
    native <methods>;
}

# ── Room Database & SQLite ──
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.playit.app.data.local.entity.** { *; }
-keep interface com.playit.app.data.local.dao.** { *; }

# ── Jetpack Compose & Kotlin Coroutines ──
-keepclassmembers class androidx.compose.** { *; }
-dontwarn kotlinx.coroutines.**

# ── PlayIT Domain Models ──
-keep class com.playit.app.domain.model.** { *; }
