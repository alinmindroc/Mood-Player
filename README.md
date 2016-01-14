facedetection
=============
Android app which uses face detection to get your mood and generates a youtube playlist.

Build instructions:
project path should look like:

C:\Users\Alin\Desktop\OpenCV-2.4.5-android-sdk\OpenCV-2.4.5-android-sdk\samples
for the native code to build succesfully. If the path is different, you have to
change make.bat and android.mk from the jni dir.

Build instructions:  
-import the opencv main project from the opencv directory in eclipse  
-link the opencv library in properties/android with the project  
-run the make.bat executable  
-add youtubeandroidplayerapi.jar external jar in properties/java build path  

Playstore: https://play.google.com/store/apps/details?id=ncit.opencv.samples.facedetect  
