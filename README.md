facedetection
=============
Android app which uses face detection to get your mood and generates a youtube playlist.

Build instructions:
project path should look like:

C:\Users\Alin\Desktop\OpenCV-2.4.5-android-sdk\OpenCV-2.4.5-android-sdk\samples
for the native code to build succesfully. If the path is different, you have to
change make.bat and android.mk from the jni dir.

Before running the app, make sure that you have:
-imported in eclipse the opencv main project from the opencv directory
-linked with the project the opencv library in properties/android
-succesfully run the make.bat executable
-added youtubeandroidplayerapi.jar external jar in properties/java build path
