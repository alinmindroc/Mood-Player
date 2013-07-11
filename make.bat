@echo on

::insert bash path here
@set BASHPATH="C:\cygwin\bin\bash"
::insert project path here
@set PROJECTDIR="/cygdrive/C/Users/roberta/Desktop/OpenCV-2.4.5-android-sdk/samples/face-detection"
::insert NDK path here
@set NDKDIR="/cygdrive/C/Users/roberta/Downloads/android-ndk/android-ndk-r8e/ndk-build"

%BASHPATH% --login -c "cd %PROJECTDIR% && %NDKDIR%

@pause: