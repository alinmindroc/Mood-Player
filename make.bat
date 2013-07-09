@echo on

::insert bash path here
@set BASHPATH="C:\cygwin\bin\bash"
::insert project path here
@set PROJECTDIR="/cygdrive/C/Users/Alin/Desktop/face-detection"
::insert NDK path here
@set NDKDIR="/cygdrive/d/android-ndk-r8e/ndk-build"

%BASHPATH% --login -c "cd %PROJECTDIR% && %NDKDIR%

@pause: