# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# -dontobfuscate

-keep class org.jaudiotagger.** { *; }

-dontwarn java.awt.Graphics2D
-dontwarn java.awt.Image
-dontwarn java.awt.geom.AffineTransform
-dontwarn java.awt.image.BufferedImage
-dontwarn java.awt.image.ImageObserver
-dontwarn java.awt.image.RenderedImage
-dontwarn javax.imageio.ImageIO
-dontwarn javax.imageio.ImageWriter
-dontwarn javax.imageio.stream.ImageInputStream
-dontwarn javax.imageio.stream.ImageOutputStream
-dontwarn javax.swing.filechooser.FileFilter

# NewPipe Extractor / Rhino rules
-dontwarn javax.script.**
-dontwarn org.mozilla.javascript.**
-keep class org.mozilla.javascript.** { *; }
-keep class org.schabi.newpipe.extractor.** { *; }
-keep interface org.schabi.newpipe.extractor.** { *; }
-keep class org.mozilla.javascript.** { *; }
-keep interface org.mozilla.javascript.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod

# Coil
-dontwarn coil3.**

# Google API, gRPC, and re2j rules
-dontwarn com.google.api.**
-dontwarn com.google.cloud.**
-dontwarn com.google.re2j.**
-dontwarn com.google.common.**
-dontwarn io.grpc.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.apache.http.**
-dontwarn org.ietf.jgss.**
-dontwarn org.conscrypt.**
-dontwarn sun.misc.Unsafe
-dontwarn sun.security.**

-keep class com.google.api.** { *; }
-keep class com.google.re2j.** { *; }
-keep class com.google.common.** { *; }
-keep class io.grpc.** { *; }


# Media3
-dontwarn androidx.media3.**
-keep class androidx.media3.** { *; }

# kotlinx.serialization rules
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}
-keep class *$$Companion {
    *;
}
-keepclassmembers class ** {
    public static final kotlinx.serialization.KSerializer serializer(...);
}
-keep class *$$serializer {
    *;
}
-keepnames @kotlinx.serialization.Serializable class *

