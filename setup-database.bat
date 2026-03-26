@echo off
echo ========================================
echo  BMI Calculator - Neon Database Setup
echo ========================================
echo.

REM Check if .env exists in backend
if not exist "backend\.env" (
    echo [STEP 1] Creating backend .env file...
    copy backend\.env.example backend\.env
    echo.
    echo IMPORTANT: Edit backend\.env dan paste connection string Anda!
    echo.
    echo Buka file: %CD%\backend\.env
    echo.
    pause
) else (
    echo [OK] backend\.env sudah ada
    echo.
)

echo ========================================
echo  Next Steps:
echo ========================================
echo.
echo 1. Buka Neon Console: https://console.neon.tech
echo 2. Copy Connection String dari project Anda
echo 3. Paste ke backend\.env (ganti DATABASE_URL=...)
echo 4. Jalankan schema SQL di Neon Console SQL Editor
echo    - File: database\schema.sql
echo    - Atau gunakan command: npm run db:setup
echo.
echo 5. Jalankan backend: npm run backend:dev
echo.
echo ========================================
echo.

REM Open Neon Console in browser
start https://console.neon.tech

REM Open .env file for editing
REM Uncomment below if you want to auto-open .env
REM start notepad backend\.env

echo Database schema file location:
echo %CD%\database\schema.sql
echo.
echo Copy isi file tersebut dan paste ke Neon SQL Editor!
echo.
pause
