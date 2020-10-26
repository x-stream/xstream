#!/bin/sh
#
# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the
# Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
# version 2 with the GNU Classpath Exception, which is available at
# https://www.gnu.org/software/classpath/license.html.
#
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
#

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

