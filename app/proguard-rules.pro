# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.imagenim.app.data.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
