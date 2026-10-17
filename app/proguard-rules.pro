# Keep kotlinx.serialization DTOs used by Retrofit
-keep,includedescriptorclasses class uz.moneymanager.data.remote.**$$serializer { *; }
-keepclassmembers class uz.moneymanager.data.remote.** { *** Companion; }
