# Add project specific ProGuard rules here. By default, the flags in this
# file are appended to flags specified in
#
# ~/Android/Sdk/tools/proguard/proguard-android.txt
#
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle. For more details, see:
#
# http://developer.android.com/guide/developing/tools/proguard.html

# add any project specific keep options here.
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**
