@ECHO OFF
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION
REM == kill ppstream process ==
TASKKILL /F /IM "PPStream.exe" /T
REM == delete all pps ads directories ==
RMDIR /s /q "%APPDATA%\PPStream\banner"
ECHO banner >> "%APPDATA%\PPStream\banner"
RMDIR /s /q "%APPDATA%\PPStream\adsys"
ECHO adsys >> "%APPDATA%\PPStream\adsys"
REM == detect ppstream director ==
SET pps_dirs="%ProgramFiles%",C:\PPS.tv,D:\PPS.tv,E:\PPS.tv,C:\Programme,D:\Programme,E:\Programme,"C:\Program Files","D:\Program Files","E:\Program Files"
FOR %%A IN (%pps_dirs%) DO (
SET pps_cmd="%%~A\PPStream\PPStream.exe"
REM echo "Check PPStream in !pps_cmd!"
IF EXIST "!pps_cmd!" (
echo "PPStream found in !pps_cmd!"
!pps_cmd! >> "%APPDATA%\PPStream\adsys"
GOTO end
)
)
:end
ENDLOCAL