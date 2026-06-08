# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/vikasdulgunde/.gemini/antigravity/scratch/screen_mirror/android_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools-proguard.html

# Add any custom rules here to keep specific classes/members from being removed
-keep class com.screenmirror.** { *; }

# ----------------------------------------------------------------------
# Room + WorkManager keep rules.
#
# The Mobile Ads SDK pulls in WorkManager, which uses a Room database
# (WorkDatabase). Room generates *_Impl classes at compile time and
# instantiates them reflectively at runtime via Room.databaseBuilder().
# R8 sees the _Impl classes as "unused" (no static call sites) and strips
# their constructors, causing:
#   NoSuchMethodException: androidx.work.impl.WorkDatabase_Impl.<init>
# at first launch. These rules tell R8 to preserve the reflective entry
# points.
# ----------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase { <init>(...); }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers @androidx.room.Database class * { *; }
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }
-dontwarn androidx.room.**
-dontwarn androidx.sqlite.**

-keep class androidx.work.impl.** { *; }
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class androidx.work.impl.WorkDatabase { *; }
-keep class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.work.InputMerger
-keep class * extends androidx.work.RxWorker
-dontwarn androidx.work.**

# Mobile Ads SDK — keep all public API.
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn com.google.android.gms.**

# Content providers (AppStartup's InitializationProvider). The merged
# manifest references androidx.startup.InitializationProvider by name
# and R8 occasionally over-aggresses on stripping its dependencies.
-keep class * extends android.content.ContentProvider
-keep class androidx.startup.** { *; }
