# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\ProgramFiles\AndroidStudio\sdk2/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn net.soureceforge.pinyin4j.**
-dontwarn demo.**
#-libraryjars libs/pinyin4j-2.5.0.jar
-keep class net.sourceforge.pinyin4j.** { *;}
-keep class demo.** { *;}

