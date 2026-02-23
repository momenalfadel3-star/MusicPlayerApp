#!/bin/sh
# Gradle wrapper script
APP_HOME=$(cd "${0%/*}" && pwd)
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
if [ -n "$JAVA_HOME" ]; then
    JAVACMD=$JAVA_HOME/bin/java
else
    JAVACMD=java
fi
eval set -- $DEFAULT_JVM_OPTS '""' '""' '"-Dorg.gradle.appname=gradlew"' -classpath '""' org.gradle.wrapper.GradleWrapperMain '""'
exec "" ""
