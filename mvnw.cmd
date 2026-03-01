@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Optional ENV vars
@REM -----------------
@REM   JAVA_HOME - location of a JDK home dir, required when type is jdk
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME - user name for downloading maven
@REM   MVNW_PASSWORD - password for downloading maven
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="mvnw" (SET "MVN_CMD=mvn.cmd") ELSE (SET "MVN_CMD=mvn")
@SET MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
@IF NOT "%MAVEN_PROJECTBASEDIR%"=="" GOTO endDetectBasedir

@SET EXEC_DIR=%CD%
@SET WDIR=%EXEC_DIR%
:findBaseDir
@IF EXIST "%WDIR%\.mvn\wrapper\maven-wrapper.properties" (
  SET "MAVEN_PROJECTBASEDIR=%WDIR%"
  GOTO endDetectBasedir
)
@SET "WDIR_PARENT=%WDIR%\..\"
@IF "%WDIR_PARENT%"=="%WDIR%\" GOTO basedirNotFound
@SET "WDIR=%WDIR_PARENT%"
@GOTO findBaseDir

:basedirNotFound
@SET "MAVEN_PROJECTBASEDIR=%EXEC_DIR%"

:endDetectBasedir
@SET MAVEN_WRAPPER_PROPERTIES_FILE=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties

@SET DOWNLOAD_URL=
@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_WRAPPER_PROPERTIES_FILE%") DO (
  @IF "%%A"=="distributionUrl" SET DOWNLOAD_URL=%%B
)

@IF "%DOWNLOAD_URL%"=="" (
  @ECHO [ERROR] distributionUrl property is missing from maven-wrapper.properties
  @EXIT /B 1
)

@SET MAVEN_USER_HOME=%USERPROFILE%\.m2
@IF NOT "%MAVEN_USER_HOME%"=="" GOTO haveMavenUserHome
@SET MAVEN_USER_HOME=%HOMEDRIVE%%HOMEPATH%\.m2
:haveMavenUserHome

@SET MAVEN_WRAPPER_DIST_SUBDIR=%MAVEN_USER_HOME%\wrapper\dists
@FOR %%F IN ("%DOWNLOAD_URL%") DO @SET DIST_ZIP_NAME=%%~nF
@SET MAVEN_HOME=%MAVEN_WRAPPER_DIST_SUBDIR%\%DIST_ZIP_NAME%

@IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" GOTO runMaven
@IF EXIST "%MAVEN_HOME%\bin\mvn" GOTO runMaven

@ECHO Downloading Maven...
@IF NOT EXIST "%MAVEN_WRAPPER_DIST_SUBDIR%" MKDIR "%MAVEN_WRAPPER_DIST_SUBDIR%"

@SET TMP_DOWNLOAD_DIR=%MAVEN_WRAPPER_DIST_SUBDIR%\tmp
@IF NOT EXIST "%TMP_DOWNLOAD_DIR%" MKDIR "%TMP_DOWNLOAD_DIR%"
@SET TMP_ZIP=%TMP_DOWNLOAD_DIR%\apache-maven.zip

@powershell -Command "(New-Object System.Net.WebClient).DownloadFile('%DOWNLOAD_URL%', '%TMP_ZIP%')" || (
  @ECHO [ERROR] Failed to download Maven distribution.
  @EXIT /B 1
)

@powershell -Command "Add-Type -AssemblyName System.IO.Compression.FileSystem; [System.IO.Compression.ZipFile]::ExtractToDirectory('%TMP_ZIP%', '%MAVEN_WRAPPER_DIST_SUBDIR%')" || (
  @ECHO [ERROR] Failed to extract Maven distribution.
  @EXIT /B 1
)

@IF EXIST "%TMP_DOWNLOAD_DIR%" RMDIR /S /Q "%TMP_DOWNLOAD_DIR%"

:runMaven
@SET MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd
@IF NOT EXIST "%MVN_CMD%" SET MVN_CMD=%MAVEN_HOME%\bin\mvn

@IF NOT DEFINED JAVA_HOME GOTO runMavenNoJavaHome
@SET "JAVA_HOME_NATIVE=%JAVA_HOME%"
:runMavenNoJavaHome
@"%MVN_CMD%" %*
@SET MVNW_EXIT_CODE=%ERRORLEVEL%
@EXIT /B %MVNW_EXIT_CODE%
