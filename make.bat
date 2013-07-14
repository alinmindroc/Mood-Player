@echo on

::insert bash path here
@set BASHPATH="C:\cygwin\bin\bash"
::insert project path here
@set PROJECTDIR="/cygdrive/C/Users/Alin/Desktop/face-detection-with-facebook"
::insert NDK path here
@set NDKDIR="/cygdrive/D/android-ndk-r8e/ndk-build"

%BASHPATH% --login -c "cd %PROJECTDIR% && %NDKDIR%

@pause: