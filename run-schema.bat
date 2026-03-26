@echo off
REM Script untuk menjalankan schema database ke Neon
REM Ganti YOUR_CONNECTION_STRING dengan connection string dari Neon Console

set /p CONNECTION_STRING="Masukkan Connection String dari Neon Console: "

echo Running database schema...
echo %CONNECTION_STRING% | psql -f database\schema.sql

echo Done!
