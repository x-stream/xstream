#!/bin/sh
# Copyright (C) 2015, 2022 XStream Committers.
# All rights reserved.
#
# The software in this package is published under the terms of the BSD
# style license a copy of which has been included with this distribution in
# the LICENSE.txt file.
#
# Created on 28. October 2015 by Joerg Schaible

# Run XStream JMH

# * Goto script root dir
# **********************
cd `dirname $0`/..

# * Initialize environment
# ************************
# JAVA_OPTS and APP_OPTS can be set from outside
JAVA_BIN=
APP_CP=

# * Set Java executable
# *********************
if [ -z "$JAVA_EXE" ]; then
	JAVA_EXE=java
fi
if [ -z "$JAVA_BIN" ] || [ ! -r $JAVA_BIN ]; then
	JAVA_BIN=$JAVA_HOME/bin/$JAVA_EXE
	if [ -z "$JAVA_HOME" ] || [ ! -r $JAVA_BIN ]; then
		JAVA_BIN=$JDK_HOME/jre/bin/$JAVA_EXE
		if [ -z "$JDK_HOME" ] || [ ! -r $JAVA_BIN ]; then
			JAVA_BIN=$JAVA_EXE
		fi
	fi
fi

# * Set class path
# ****************
for i in lib/*.jar; do
	APP_CP=$APP_CP:$i
done

# * Open modules for parsers
# *************
JAVA_OPTS="$JAVA_OPTS --add-opens java.xml/com.sun.org.apache.xerces.internal.parsers=ALL-UNNAMED"
JAVA_OPTS="$JAVA_OPTS --add-opens java.xml/com.sun.org.apache.xerces.internal.util=ALL-UNNAMED"
JAVA_OPTS="$JAVA_OPTS --add-opens java.xml/com.sun.xml.internal.stream=ALL-UNNAMED"

# * Set options
# *************
JAVA_OPTS="$JAVA_OPTS -Xmx2048m -Xss4m"

# * Main class
# ************
MAIN_CLASS=org.openjdk.jmh.Main

# * Debug
# *******
if [ "$XSTREAM_SCRIPT_ECHO" = "on" ]; then
	echo JAVA_BIN=$JAVA_BIN
	echo JAVA_OPTS=$JAVA_OPTS
	echo APP_OPTS=$APP_OPTS
	echo APP_CP=$APP_CP
	echo MAIN_CLASS=$MAIN_CLASS
fi

# * Run application
# *****************
$JAVA_BIN $JAVA_OPTS -cp $APP_CP $MAIN_CLASS $APP_OPTS "$@"

