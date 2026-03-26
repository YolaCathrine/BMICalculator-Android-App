@echo off
echo ========================================
echo  GRADLE CLEANUP SCRIPT
echo ========================================
echo.

echo [1/4] Stopping Gradle Daemons...
taskkill /F /IM java.exe 2>nul
if %errorlevel% equ 0 (
    echo Java processes stopped.
) else (
    echo No Java processes found.
)
echo.

echo [2/4] Deleting project .gradle folder...
if exist ".gradle" (
    rmdir /s /q .gradle
    echo Project .gradle folder deleted.
) else (
    echo Project .gradle folder not found.
)
echo.

echo [3/4] Cleaning Gradle caches...
if exist "%USERPROFILE%\.gradle\caches" (
    rmdir /s /q "%USERPROFILE%\.gradle\caches"
    echo Gradle caches deleted.
) else (
    echo Gradle caches not found.
)
echo.

if exist "%USERPROFILE%\.gradle\daemon" (
    rmdir /s /q "%USERPROFILE%\.gradle\daemon"
    echo Gradle daemon folder deleted.
) else (
    echo Gradle daemon folder not found.
)
echo.

echo [4/4] Cleanup complete!
echo.
echo ========================================
echo  NEXT STEPS:
echo ========================================
echo  1. Open Android Studio
echo  2. File > Invalidate Caches / Restart
echo  3. Click "Invalidate and Restart"
echo  4. After restart, Sync Project with Gradle Files
echo ========================================
echo.
pause
