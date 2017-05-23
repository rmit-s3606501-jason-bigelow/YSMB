rem JDK auto-detection (first JDK listed by dir)
dir /b "C:\Program Files\Java" | findstr jdk > jdk
set /p jdkpath=<jdk
del jdk
set jdkpath=C:\Program Files\Java\%jdkpath%\bin
set path=%path%;%jdkpath%