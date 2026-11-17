# ===== ПР №11: правила R8 для release-сборки =====

# Вырезаем отладочные логи из release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Для читаемых стектрейсов в Crashlytics
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# Retrofit / Gson: не переименовывать DTO (имена полей = ключи JSON)
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod
-keep class com.example.authfeature.data.dto.** { *; }
-keep interface com.example.authfeature.data.api.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Room: сущности, DAO и база
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
