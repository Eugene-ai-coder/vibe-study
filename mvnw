#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when type is jdk
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME - user name for downloading maven
#   MVNW_PASSWORD - password for downloading maven
# ----------------------------------------------------------------------------

set -euf

# OS specific support.
native_path() { printf '%s\n' "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVACMD and JAVACCMD
set_java_home() {
  # For Cygwin and MinGW, ensure paths are in Unix format before anything is touched
  if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ]; then
      die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
  else
    JAVACMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v java
    )" || die "ERROR: Cannot find 'java' command on your PATH.
Please provide the JAVA_HOME variable in your environment to match the
location of your Java installation."
  fi
}

# shellcheck disable=SC2120
die() {
  if [ -n "$*" ]; then
    echo "$*"
  fi
  echo "Please refer to https://maven.apache.org/wrapper/ for instructions."
  exit 1
}

# Parse the maven-wrapper.properties file
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(find_maven_basedir)}"
if [ -z "$MAVEN_PROJECTBASEDIR" ]; then
  MAVEN_PROJECTBASEDIR="$BASE_DIR"
fi

find_maven_basedir() {
  if [ -z "${1-}" ]; then
    basedir="$(pwd -P)"
  else
    basedir="$1"
  fi
  found=0
  while true; do
    if [ -f "$basedir/.mvn/wrapper/maven-wrapper.properties" ]; then
      found=1
      break
    fi
    new_basedir="$(dirname "$basedir")"
    if [ "$new_basedir" = "$basedir" ]; then
      break
    fi
    basedir="$new_basedir"
  done
  if [ "$found" = "1" ]; then
    printf '%s' "$basedir"
  fi
}

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-}"
if [ -z "${MAVEN_PROJECTBASEDIR}" ]; then
  MAVEN_PROJECTBASEDIR="$(find_maven_basedir)"
  if [ -z "${MAVEN_PROJECTBASEDIR}" ]; then
    MAVEN_PROJECTBASEDIR="$(pwd -P)"
  fi
fi
export MAVEN_PROJECTBASEDIR

MAVEN_WRAPPER_PROPERTIES_FILE="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

distributionUrl=
wrapperUrl=
if [ -f "$MAVEN_WRAPPER_PROPERTIES_FILE" ]; then
  # shellcheck disable=SC2094
  while IFS="=" read -r key value; do
    # Remove CR (Windows line endings)
    value="${value%$'\r'}"
    case "$key" in
    distributionUrl) distributionUrl="$value" ;;
    wrapperUrl) wrapperUrl="$value" ;;
    esac
  done <"$MAVEN_WRAPPER_PROPERTIES_FILE"
fi

if [ -z "${distributionUrl-}" ]; then
  die "distributionUrl property is missing from maven-wrapper.properties"
fi

# Determine MAVEN_HOME
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
MAVEN_WRAPPER_JAR="$MAVEN_USER_HOME/wrapper/dists/$(basename "$distributionUrl" .zip)/$(echo "$distributionUrl" | sha256sum | cut -c1-8)/maven-wrapper.jar"

MAVEN_HOME_PARENT="$MAVEN_USER_HOME/wrapper/dists/$(basename "$distributionUrl" .zip)"
MAVEN_HOME="$MAVEN_HOME_PARENT/$(ls "$MAVEN_HOME_PARENT" 2>/dev/null | head -1)"

if [ ! -d "$MAVEN_HOME" ]; then
  # Download Maven
  MAVEN_HOME="$MAVEN_HOME_PARENT/$(basename "$distributionUrl" -bin.zip)"
  mkdir -p "$(dirname "$MAVEN_HOME")"

  if command -v curl >/dev/null 2>&1; then
    DOWNLOAD_CMD="curl -fsSL"
    [ -n "${MVNW_USERNAME-}" ] && DOWNLOAD_CMD="$DOWNLOAD_CMD --user ${MVNW_USERNAME}:${MVNW_PASSWORD-}"
  elif command -v wget >/dev/null 2>&1; then
    DOWNLOAD_CMD="wget -q -O -"
    [ -n "${MVNW_USERNAME-}" ] && DOWNLOAD_CMD="$DOWNLOAD_CMD --user=${MVNW_USERNAME} --password=${MVNW_PASSWORD-}"
  else
    die "ERROR: No download tool (curl or wget) found. Please install one."
  fi

  TMP_DOWNLOAD_DIR="$(mktemp -d)"
  TMP_ZIP="$TMP_DOWNLOAD_DIR/apache-maven.zip"

  echo "Downloading $distributionUrl"
  $DOWNLOAD_CMD "$distributionUrl" >"$TMP_ZIP" || die "ERROR: Failed to download Maven."

  unzip -q "$TMP_ZIP" -d "$MAVEN_HOME_PARENT" || die "ERROR: Failed to unzip Maven."
  rm -rf "$TMP_DOWNLOAD_DIR"
fi

MAVEN_CMD="$MAVEN_HOME/bin/mvn"
if [ ! -x "$MAVEN_CMD" ]; then
  # Try to find it
  MAVEN_CMD="$(find "$MAVEN_HOME_PARENT" -name mvn -type f 2>/dev/null | head -1)"
  if [ -z "$MAVEN_CMD" ]; then
    die "ERROR: Cannot find Maven executable in $MAVEN_HOME_PARENT"
  fi
fi

set_java_home

exec "$MAVEN_CMD" "$@"
