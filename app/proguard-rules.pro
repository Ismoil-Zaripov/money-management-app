# Keep kotlinx.serialization DTOs used by Retrofit
-keep,includedescriptorclasses class uz.moneymanager.data.remote.**$$serializer { *; }
-keepclassmembers class uz.moneymanager.data.remote.** { *** Companion; }

# Keep Room entities and database
-keep class uz.moneymanager.data.local.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Keep Domain models and UI models
-keep class uz.moneymanager.domain.model.** { *; }
-keep class uz.moneymanager.data.repository.AppSettings { *; }
-keep class uz.moneymanager.ui.transactions.TransactionUiItem { *; }
-keep class uz.moneymanager.ui.transactions.form.FormUiState { *; }
