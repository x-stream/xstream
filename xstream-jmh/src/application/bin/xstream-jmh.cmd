@echo off
@REM Copyright (C) 2015, 2022 XStream Committers.
@REM All rights reserved.
@REM
@REM The software in this package is published under the terms of the BSD
@REM style license a copy of which has been included with this distribution in
@REM the LICENSE.txt file.
@REM
@REM Created on 28. October 2015 by Joerg Schaible

@REM Run XStream JMH
if "%XSTREAM_SCRIPT_ECHO%"=="on" echo on

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM * Set title
@REM ***********
title ScalarisDMS

@REM * Goto script root dir
@REM **********************
cd /d %~dp0\..

@REM * Initialize environment
@REM ************************
@REM JAVA_OPTS and APP_OPTS can be set from outside
set JAVA_BIN=
set APP_CP=

@REM * Set Java executable
@REM *********************
if not defined JAVA_EXE set JAVA_EXE=java.exe
if "%JAVA_BIN%" NEQ "" if exist %JAVA_BIN% goto SetClassPath
if defined JAVA_HOME if "%JAVA_HOME%" NEQ "" set JAVA_BIN=%JAVA_HOME%\bin\%JAVA_EXE% 
if exist %JAVA_BIN% goto SetClassPath
if defined JDK_HOME if "%JDK_HOME%" NEQ "" set JAVA_BIN=%JDK_HOME%\jre\bin\%JAVA_EXE% 
if exist %JAVA_BIN% goto SetClassPath
set JAVA_BIN=%JAVA_EXE%

:SetClassPath
@REM * Set class path
@REM ****************
for %%i in (lib\*.jar) do call :APP_CP_append %%i
call :APP_CP_append "config"

@REM * Open modules for parsers
@REM *************
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.xml/com.sun.org.apache.xerces.internal.parsers=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.xml/com.sun.org.apache.xerces.internal.util=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.xml/com.sun.xml.internal.stream=ALL-UNNAMED

@REM * Set options
@REM *************
set JAVA_OPTS=%JAVA_OPTS% -Xmx2048m -Xss4m

@REM * Main class
@REM ************
set MAIN_CLASS=org.openjdk.jmh.Main

@REM * Run application
@REM *****************
%JAVA_BIN% %JAVA_OPTS% %APP_DEFINES% -cp %APP_CP% %MAIN_CLASS% %APP_OPTS%  %*


if "%OS%"=="Windows_NT" @endlocal
goto :EOF


@REM ***************
@REM * Sub functions
@REM ***************

:APP_CP_append
	set APP_CP=%APP_CP%;%1
	goto :EOF
