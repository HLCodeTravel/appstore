# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# See https://speakerdeck.com/chalup/proguard

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 基本指令区
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>基本指令区

#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<默认保留区
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}
# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}
#MTK 6.0+ ContentProvider
-renamesourcefileattribute transsion.java
-keepattributes SourceFile,LineNumberTable

#v7
-keep class android.support.v7.** { *; }

#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>默认保留区

#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<GameLobby

#热更新不被混淆
-keep class com.transhot.up.** {
    public *;
}

#不混淆资源类
-keep public class com.jph.android.R$*{
    public static final int *;
}

-keep class com.gemini.gamefolder.bean.** { *; }
-keep class com.gemini.h5gamelibrary.bean.** { *; }

#自定义组件不被混淆
-keep class com.gemini.gamefolder.draglayout.DragLayout {*;}
-keep class com.gemini.gamefolder.CellItemView {*;}
-keep class com.gemini.gamefolder.VerticalProgressBar {*;}
-keep class com.gemini.h5gamelibrary.view.CircularImage {*;}
-keep class com.gemini.h5gamelibrary.view.DragLayout {*;}

-keep class com.gemini.gamefolder.GameLobbyManager {*;}
-keep class com.gemini.gamefolder.GameLobbyManager$* {*;}
-keep class com.gemini.gamefolder.GameLobbyActivity {*;}
-keep class com.gemini.gamefolder.WebViewActivity {*;}
-keep class com.gemini.h5gamelibrary.WebViewAdInterface {*;}

-keep class com.gemini.gamefolder.RemoteActivity1 {*;}
-keep class com.gemini.gamefolder.RemoteActivity2 {*;}
-keep class com.gemini.gamefolder.RemoteActivity3 {*;}
-keep class com.gemini.gamefolder.RemoteActivity4 {*;}
-keep class com.gemini.gamefolder.RemoteActivity5 {*;}
-keep class com.gemini.gamefolder.RemoteActivity6 {*;}
-keep class com.gemini.gamefolder.RemoteActivity7 {*;}

-keep class com.gemini.h5gamelibrary.H5RemoteActivity1 {*;}
-keep class com.gemini.h5gamelibrary.H5RemoteActivity2 {*;}
-keep class com.gemini.h5gamelibrary.H5RemoteActivity3 {*;}
-keep class com.gemini.h5gamelibrary.H5RemoteActivity4 {*;}
-keep class com.gemini.h5gamelibrary.H5RemoteActivity5 {*;}

-keep class com.gemini.h5gamelibrary.GameActivity {*;}
-keep class com.gemini.h5gamelibrary.Remote {*;}
-keep class com.gemini.h5gamelibrary.GameManager {*;}
-keep class com.gemini.h5gamelibrary.GameRobbyActivity {*;}

-keep class com.gemini.h5gamelibrary.utils.PermissionUtils{*;}
-keep class com.gemini.h5gamelibrary.GRTransHotUpManagerGetter{*;}
-keep class com.gemini.h5gamelibrary.GRTransHotUpManagerGetter$*{*;}
-keep class com.gemini.gamefolder.util.DataTools {*;}
-keep class com.gemini.h5gamelibrary.utils.ContantUtil{*;}

#================================================ admedia库, admedia 还需依赖 Facebook、google
-dontwarn com.transsion.iad.core.**
-keep class com.transsion.iad.core.** { *;}

#--------------------Facebook-------------------------
-keep class com.facebook.** {*;}
-keep interface com.facebook.** {*;}
-keep enum com.facebook.** {*;}
-dontwarn com.facebook.ads.**

#-------------------Google------------------------------
-keep class com.google.** { *; }
-dontwarn com.google.**
-dontwarn android.support.v4.**
#============================================== admedia库

#=================================================webView
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#=================================================webView

#=================================================coreUtil
-keep class com.transsion.core.** { *; }
#=================================================coreUtil

#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>GameLobby

#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<aha
-keep class net.bat.store.AhaManager {*;}
-keep class net.bat.store.bean.** {*;}
-keep class net.bat.store.inter.** {*;}
-keep class net.bat.store.utils.** {*;}
-keep class net.bat.store.net.NetworkConstant {*;}
-keep class net.bat.store.constans.AppStoreConstant {*;}
-keep class net.bat.store.ux.home.selection.adapter.MainListHolder {*;}
-keep class net.bat.store.json.** {*;}
-keep class net.bat.store.net.** {*;}

#=====================================================glid依赖库
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
   **[] $VALUES;
    public *;
}
#=====================================================glid依赖库

#================================================ admedia库, admedia 还需依赖 Facebook、google
-dontwarn com.transsion.iad.core.**
-keep class com.transsion.iad.core.** { *;}

#--------------------Facebook-------------------------
-keep class com.facebook.** {*;}
-keep interface com.facebook.** {*;}
-keep enum com.facebook.** {*;}
-dontwarn com.facebook.ads.**

#-------------------Google------------------------------
-keep class com.google.** { *; }
-dontwarn com.google.**
-dontwarn android.support.v4.**
#============================================== admedia库


#=================================================coreUtil
-keep class com.transsion.core.** { *; }
#=================================================coreUtil

#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>aha

#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Video
-keep class com.transsion.video.videoplayer.AbstractVideoListener {*;}
-keep class com.transsion.video.videoplayer.IVideoCloseListener {*;}
-keep class com.transsion.video.videoplayer.VideoPlayerView {*;}
-keep class com.transsion.video.videoplayer.VideoPlayerView$* {*;}
-keep class com.transsion.video.videoplayer.VideoPlayer {*;}
-keep class com.transsion.video.videoplayer.VideoPlayer$* {*;}
-keep class com.transsion.video.videoplayer.VideoPlayerBridge {*;}

-keep class com.transsion.video.youtubeplayer.AbstractYouTubeListener {*;}
-keep class com.transsion.video.youtubeplayer.IVideoCloseListener {*;}
-keep class com.transsion.video.youtubeplayer.YouTubePlayerView {*;}
-keep class com.transsion.video.youtubeplayer.YouTubePlayerView$* {*;}
-keep class com.transsion.video.youtubeplayer.YouTubePlayer {*;}
-keep class com.transsion.video.youtubeplayer.YouTubePlayer$* {*;}
-keep class com.transsion.video.youtubeplayer.YouTubePlayerBridge {*;}

#=================================================webView
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#=================================================webView

#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Video

#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<news
-keep class transsion.sdk.bean.** {*;}
-keep class transsion.sdk.http.** {*;}
-keep class transsion.sdk.sdk.** {*;}
-keep class transsion.sdk.utils.** {*;}
-keep class transsion.sdk.viewholder.** {*;}
-keep class transsion.sdk.view.WrapContentLinearLayoutManager{*;}

#================================================ admedia库, admedia 还需依赖 Facebook、google
-dontwarn com.transsion.iad.core.bean**
-keep class com.transsion.iad.core.bean** { *;}
-keep class com.transsion.iad.core.internal.requests.Request {
    *;
}
-keep class com.transsion.iad.core.internal.requests.Response {
    *;
}

#--------------------Facebook-------------------------
-keep class com.facebook.** {*;}
-keep interface com.facebook.** {*;}
-keep enum com.facebook.** {*;}
-dontwarn com.facebook.ads.**
#============================================== admedia库

#=================================================webView
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#=================================================webView

#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>news

#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<ZeroScreen
-keep class com.transsion.zeroscreencore.view.** {*;}
-keep class com.transsion.zeroscreencore.manger.PushCardManger {*;}
-keep class com.transsion.zeroscreencore.bean.** {*;}
-keep class com.transsion.zeroscreencore.utils.** {*;}
-keep class com.transsion.zeroscreencore.card.view.DividerItemDecoration {*;}
-keep class com.transsion.zeroscreencore.card.view.ConvenientBanner {*;}
-keep class com.transsion.zeroscreencore.widget.** {*;}
#=================================================coreUtil
-keep class com.transsion.core.** { *; }
#=================================================coreUtil
#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ZeroScreen

#<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<CoreUtil
-keep class com.transsion.core.CoreUtil {*;}
#>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>CoreUtil
